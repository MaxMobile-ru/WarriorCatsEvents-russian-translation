package net.snowteb.warriorcats_events.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.snowteb.warriorcats_events.attachments.CapabilityManager;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.attachments.PlayerSkill;
import net.snowteb.warriorcats_events.attachments.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.client.ClanInfo;
import net.snowteb.warriorcats_events.diseases.DiseaseRegistry;
import net.snowteb.warriorcats_events.diseases.DiseaseType;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.managers.CarryPlayerRequestManager;
import net.snowteb.warriorcats_events.managers.ClanInviteManager;
import net.snowteb.warriorcats_events.managers.PlayerKittingRequestManager;
import net.snowteb.warriorcats_events.managers.PlayerMateRequestManager;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.*;
import net.snowteb.warriorcats_events.network.packet.s2c.others.OpenPlayerCatDataScreenPacket;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;
import tocraft.walkers.api.PlayerShape;

import java.util.*;
import java.util.stream.Collectors;

public class WCECommandHandles {

    public static int carryRequestAccept(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        CarryPlayerRequestManager.CarryRequest request = CarryPlayerRequestManager.getRequest(player);
        if (request == null) {
            player.sendSystemMessage(
                    Component.translatable("managers.no_request_pending")
                            .withStyle(ChatFormatting.GRAY)
            );
            return 0;
        }

        Entity ent = player.serverLevel().getEntity(request.requester);
        if (ent == null) {
            source.sendFailure(Component.translatable("argument.entity.notfound.player"));
            return 0;
        } else {
            if (ent instanceof ServerPlayer requester && PlayerShape.getCurrentShape(requester) instanceof WCatEntity) {
                player.startRiding(requester);
                CarryPlayerRequestManager.clear(player);
            }
        }

        return 1;

    }

    public static int carryRequestDeny(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        CarryPlayerRequestManager.CarryRequest request = CarryPlayerRequestManager.getRequest(player);
        if (request == null) {
            player.sendSystemMessage(
                    Component.translatable("managers.no_request_pending").withStyle(ChatFormatting.GRAY)
            );
            return 0;
        }

        CarryPlayerRequestManager.clear(player);


        String deniedPlayerName = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();


        player.sendSystemMessage(
                Component.empty()
                        .append(Component.translatable("managers.carry_request_declined").withStyle(ChatFormatting.GRAY))
        );


        ServerPlayer requester = player.server.getPlayerList().getPlayer(request.requester);
        if (requester != null) {
            Component message = Component.translatable("managers.carry_request_client_declined",
                    Component.empty().append(Component.literal(deniedPlayerName)
                            .withStyle(ChatFormatting.GOLD)));

            requester.sendSystemMessage(message);
        }

        return 1;
    }

    public static int openChangelogScreen(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        ModPackets.sendToPlayer(new OpenChangelogScreenPacket(), player);

        return 1;
    }

    public static int changeMemberPermissions(CommandSourceStack source, ServerPlayer targetPlayer, String perms) throws CommandSyntaxException {
        ServerPlayer sPlayer = source.getPlayerOrException();

        ClanData.ClanPermissions newPerms;

        try {
            newPerms = ClanData.ClanPermissions.valueOf(perms.toUpperCase());
        } catch (IllegalArgumentException e) {
            source.sendFailure(Component.translatable("commands.clan.invalid_perms", perms).withStyle(ChatFormatting.RED));
            return 0;
        }

        if (newPerms == ClanData.ClanPermissions.OWNER) {
            source.sendFailure(Component.translatable("commands.clan.invalid_perms", perms).withStyle(ChatFormatting.RED));
            return 0;
        }

        String hostMorphName = sPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();
        String targetMorphName = targetPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

        UUID targetClanId = sPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();
        UUID currentMemberClanId = targetPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();

        ClanData data = ClanData.get(targetPlayer.serverLevel().getServer().overworld());
        ClanData.Clan targetClan = data.getClan(targetClanId);
        ClanData.Clan currentMemberClan = data.getClan(currentMemberClanId);

        if (targetClanId.equals(ClanData.EMPTY_UUID)) {
            sPlayer.sendSystemMessage(Component.translatable("clan.player_not_clan").withStyle(ChatFormatting.GRAY));
            return 0;
        }

        if (targetClan != null) {
            boolean canKick = data.canManage(targetClan, sPlayer.getUUID());
            boolean canKickThis = data.canManagePlayer(targetClanId, sPlayer.getUUID(), targetPlayer.getUUID());
            if (!canKick || !canKickThis) {
                sPlayer.sendSystemMessage(Component.translatable("clan.no_permissions").withStyle(ChatFormatting.RED));
                return 0;
            }
        }

        if (targetPlayer == sPlayer) {
            sPlayer.sendSystemMessage(Component.translatable("clan.cant_change_own_perms").withStyle(ChatFormatting.YELLOW));
            return 0;
        }

        if (!currentMemberClanId.equals(ClanData.EMPTY_UUID)) {
            if (data.getClan(currentMemberClanId) == null) {
                sPlayer.sendSystemMessage(Component.translatable("command.failed").withStyle(ChatFormatting.GRAY));
                CapabilityManager.attachmentProvider(targetPlayer , ModAttachments.PLAYER_WCE_DATA, cap -> {
                    cap.setCurrentClanUUID(ClanData.EMPTY_UUID);
                });
                return 0;
            }
        }

        if (targetClan == currentMemberClan) {
            data.changeMemberPermissions(targetPlayer, targetClanId, newPerms);
            data.setDirty();

            sPlayer.sendSystemMessage(
                    Component.translatable("clan.member_perms",
                            Component.literal(targetPlayer.getName().getString()).withStyle(ChatFormatting.GOLD),
                            Component.literal(perms)).withStyle(ChatFormatting.RED)
            );

            for (UUID memberUUID : targetClan.members.keySet()) {
                ServerPlayer member = sPlayer.server.getPlayerList().getPlayer(memberUUID);
                if (member == null) continue;

                Component message = Component.translatable("clan.broadcast_player_change",
                        Component.literal(targetPlayer.getName().getString()).withStyle(ChatFormatting.GOLD),
                        Component.literal(perms).withStyle(ChatFormatting.RED));

                member.sendSystemMessage(message);
            }

        }

        return 1;
    }

    public static int changeMemberRank(CommandSourceStack source, ServerPlayer targetPlayer, String role) throws CommandSyntaxException {
        ServerPlayer sPlayer = source.getPlayerOrException();

        ClanData.ClanPlayerRank newRank;

        try {
            newRank = ClanData.ClanPlayerRank.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            source.sendFailure(Component.translatable("commands.clan.invalid_role", role).withStyle(ChatFormatting.RED));
            return 0;
        }

        if (newRank == ClanData.ClanPlayerRank.LEADER) {
            source.sendFailure(Component.translatable("commands.clan.invalid_role", role).withStyle(ChatFormatting.RED));
            return 0;
        }

        String hostMorphName = sPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();
        String targetMorphName = targetPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

        UUID targetClanId = sPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();
        UUID currentMemberClanId = targetPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();

        ClanData data = ClanData.get(targetPlayer.serverLevel().getServer().overworld());
        ClanData.Clan targetClan = data.getClan(targetClanId);
        ClanData.Clan currentMemberClan = data.getClan(currentMemberClanId);

        if (targetClanId.equals(ClanData.EMPTY_UUID)) {
            sPlayer.sendSystemMessage(Component.translatable("clan.player_not_clan").withStyle(ChatFormatting.GRAY));
            return 0;
        }

        if (targetClan != null) {
            boolean canKick = data.canManage(targetClan, sPlayer.getUUID());
            boolean canKickThis = data.canManagePlayer(targetClanId, sPlayer.getUUID(), targetPlayer.getUUID());
            if (!canKick || !canKickThis) {
                sPlayer.sendSystemMessage(Component.translatable("clan.no_permissions").withStyle(ChatFormatting.YELLOW));
                return 0;
            }
        }

        if (targetPlayer == sPlayer) {
            sPlayer.sendSystemMessage(Component.translatable("clan.cant_change_own_role").withStyle(ChatFormatting.YELLOW));
            return 0;
        }

        if (!currentMemberClanId.equals(ClanData.EMPTY_UUID)) {
            if (data.getClan(currentMemberClanId) == null) {
                sPlayer.sendSystemMessage(Component.translatable("command.failed").withStyle(ChatFormatting.GRAY));
                CapabilityManager.attachmentProvider(targetPlayer , ModAttachments.PLAYER_WCE_DATA, cap -> {
                    cap.setCurrentClanUUID(ClanData.EMPTY_UUID);
                });
                return 0;
            }
        }

        int deputies = 0;
        int medicine = 0;
        int medicineApp = 0;
        for (Map.Entry<UUID, ClanData.ClanPlayerRank> entry : targetClan.members.entrySet()) {
            if (entry.getValue() == ClanData.ClanPlayerRank.DEPUTY) deputies++;
            if (entry.getValue() == ClanData.ClanPlayerRank.MEDICINE) medicine++;
            if (entry.getValue() == ClanData.ClanPlayerRank.MEDICINEAPP) medicineApp++;
        }

        if (newRank == ClanData.ClanPlayerRank.DEPUTY && deputies >= 1) {
            sPlayer.sendSystemMessage(
                    Component.translatable("clan.role_limit_reached",
                            Component.literal("(" + deputies + ")").withStyle(ChatFormatting.GOLD))
            );
            return 0;
        }
        if (newRank == ClanData.ClanPlayerRank.MEDICINE && medicine >= 2) {
            sPlayer.sendSystemMessage(
                    Component.translatable("clan.role_limit_reached",
                            Component.literal("(" + medicine + ")").withStyle(ChatFormatting.GOLD))
            );
            return 0;
        }
        if (newRank == ClanData.ClanPlayerRank.MEDICINEAPP && medicineApp >= 1) {
            sPlayer.sendSystemMessage(
                    Component.translatable("clan.role_limit_reached",
                            Component.literal("(" + medicineApp + ")").withStyle(ChatFormatting.GOLD))
            );
            return 0;
        }

        if (targetClan == currentMemberClan) {
            data.changeMemberRank(targetPlayer, targetClanId, newRank);
            data.setDirty();

            String morphName = data.playerMorphNames.getOrDefault(targetPlayer.getUUID(), Component.translatable("generic.wcat.unnamedcat").getString());

            Component message = Component.translatable("clan.player_role_promoted",
                    Component.literal(morphName).withStyle(ChatFormatting.GOLD),
                    Component.literal(role).withStyle(ChatFormatting.AQUA));

            sPlayer.sendSystemMessage(message);

            for (UUID memberUUID : targetClan.members.keySet()) {
                ServerPlayer member = sPlayer.server.getPlayerList().getPlayer(memberUUID);
                if (member == null) continue;

                Component message2 = Component.translatable("clan.broadcast_player_change",
                        Component.literal(targetMorphName).withStyle(ChatFormatting.GOLD),
                        Component.literal(role).withStyle(ChatFormatting.AQUA));

                member.sendSystemMessage(message2);
            }

        }

        return 1;
    }

    public static int clanInviteAccept(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        ClanInviteManager.ClanInvite invite = ClanInviteManager.getInvite(player);
        if (invite == null) {
            player.sendSystemMessage(
                    Component.translatable("managers.no_invites_pending")
                            .withStyle(ChatFormatting.GRAY)
            );
            return 0;
        }

        UUID currentClanUUID = player.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();

        ClanData data = ClanData.get(player.serverLevel().getServer().overworld());
        ClanData.Clan clan = data.getClan(currentClanUUID);
        if (clan != null) {
            player.sendSystemMessage(
                    Component.translatable("clan.player_already_in_clan")
                            .withStyle(ChatFormatting.RED)
            );
            return 0;
        }

        UUID clanUUID = invite.clanUUID;
        ClanInviteManager.clear(player);


        boolean joined = data.addMember(
                player,
                clanUUID,
                ClanData.ClanPlayerRank.WARRIOR
        );

        if (!joined) {
            player.sendSystemMessage(
                    Component.translatable("commands.clan.cant_join_clan")
                            .withStyle(ChatFormatting.RED)
            );
            return 0;
        }

        ClanData.Clan targetClan = data.getClan(clanUUID);
        if (targetClan != null) {

            String invitedPlayerMorphName = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

            for (UUID memberUUID : targetClan.members.keySet()) {
                ServerPlayer member = player.server.getPlayerList().getPlayer(memberUUID);
                if (member == null) continue;

                Component msg =  Component.translatable("clan.player_joined_log",
                        Component.literal(invitedPlayerMorphName).withStyle(ChatFormatting.GOLD),
                        Component.literal(targetClan.name).withStyle(Style.EMPTY.withColor(targetClan.color))
                );

                member.sendSystemMessage(msg);
            }
        }
        return 1;

    }

    public static int clanInviteDeny(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        ClanInviteManager.ClanInvite invite = ClanInviteManager.getInvite(player);
        if (invite == null) {
            player.sendSystemMessage(
                    Component.translatable("managers.no_invites_pending")
                            .withStyle(ChatFormatting.GRAY)
            );
            return 0;
        }

        UUID clanUUID = invite.clanUUID;
        UUID inviterUUID = invite.inviter;

        ClanInviteManager.clear(player);

        ClanData data = ClanData.get(player.serverLevel().getServer().overworld());
        ClanData.Clan clan = data.getClan(clanUUID);

        String deniedPlayerName = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

        if (clan != null) {
            player.sendSystemMessage(
                    Component.translatable("managers.invite_declined",
                            Component.literal(clan.name)
                                    .withStyle(Style.EMPTY.withColor(clan.color))
                    )
            );
        }

        ServerPlayer inviter = player.server.getPlayerList().getPlayer(inviterUUID);
        if (inviter != null && clan != null) {
            inviter.sendSystemMessage(
                    Component.translatable("managers.player_invite_declined",
                            Component.literal(deniedPlayerName).withStyle(ChatFormatting.GOLD),
                            Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color)))
            );
        }

        return 1;
    }

    public static int clanList(ServerPlayer player, boolean seeingMyClan, boolean territoryMap) {

        ServerLevel level = player.serverLevel().getServer().overworld();
        ClanData data = ClanData.get(level);

        List<ClanInfo> list = new ArrayList<>();

        for (ClanData.Clan clan : data.clans.values()) {

            List<String> morphNames = clan.members.keySet().stream()
                    .map(uuid -> data.playerMorphNames.getOrDefault(uuid, "Unknown"))
                    .collect(Collectors.toList());


            List<ClanInfo.ClientClanCat> clientCats = new ArrayList<>();

            for (ClanData.ClanCat cat : clan.clanCats.values()) {
                clientCats.add(new ClanInfo.ClientClanCat(
                        cat.catUUID,
                        cat.catName.getString(),
                        cat.catGender,
                        cat.catRank,
                        cat.catAge.getString(),
                        cat.catVariant,
                        cat.catParents.getString(),

                        cat.onGeneticalSkin,
                        cat.genetics,
                        cat.chimeraGenetics,
                        cat.variants,
                        cat.chimeraVariants
                ));
            }

            List<ClanInfo.ClientLogEntry> clientLogList = new ArrayList<>();

            for (ClanData.ClanLogEntry log : clan.logs) {
                clientLogList.add(new ClanInfo.ClientLogEntry(log.gameTimeID, log.message));
            }

            boolean canManage = data.canManage(clan, player.getUUID());

            list.add(new ClanInfo(
                    clan.clanUUID,
                    clan.name,
                    clan.color,
                    clan.leaderName,
                    clan.clanBioSentence,
                    canManage,
                    clan.members.size(),
                    morphNames,
                    clientCats,
                    clientLogList,
                    clan.clanSymbolIndex
            ));

        }


        ModPackets.sendToPlayer(new S2CClanListPacket(list, seeingMyClan, territoryMap), player);
        return 1;
    }

    public static int diseaseGive(CommandSourceStack source, Collection<? extends Entity> entities, String disease) throws CommandSyntaxException {

        ServerPlayer player = source.getPlayerOrException();

        if (!WCEServerConfig.SERVER.DISEASES.get()) {
            player.sendSystemMessage(Component.translatable("commands.diseases_disabled")
                    .withStyle(ChatFormatting.RED));
            return 0;
        }

        DiseaseType<?> type = DiseaseRegistry.getByID(disease);
        if (type == null) return 0;
        int entityCount = 0;

        for (Entity entity : entities) {
            if (!(entity instanceof Diseaseable<?> diseaseable)) {continue;}

            if (!diseaseable.addDisease(type, false)) {
                if (diseaseable.hasDisease(type)) {
                    player.sendSystemMessage(Component.translatable("commands.already_has_disease")
                            .withStyle(ChatFormatting.RED));
                } else {
                    player.sendSystemMessage(Component.translatable("commands.disease_couldnt_apply")
                            .withStyle(ChatFormatting.RED));
                }
            } else {
                entityCount++;
            }

        }

        player.sendSystemMessage(Component.translatable("commands.diseases_applied_count", disease, entityCount));

        return 1;
    }

    public static int diseaseRemove(CommandSourceStack source, Collection<? extends Entity> entities, String disease) throws CommandSyntaxException {

        ServerLevel level = source.getLevel();
        ServerPlayer player = source.getPlayerOrException();

        if (!WCEServerConfig.SERVER.DISEASES.get()) {
            player.sendSystemMessage(Component.translatable("commands.diseases_disabled")
                    .withStyle(ChatFormatting.RED));
            return 0;
        }

        DiseaseType<?> type = DiseaseRegistry.getByID(disease);
        if (type == null) return 0;
        int entityCount = 0;

        for (Entity entity : entities) {
            if (!(entity instanceof Diseaseable<?> diseaseable)) continue;

            if (!diseaseable.removeDisease(type)) {
                player.sendSystemMessage(Component.translatable("commands.dont_have_disease")
                        .withStyle(ChatFormatting.RED));
            } else {
                entityCount++;
            }

            diseaseable.onChange();

        }

        player.sendSystemMessage(Component.translatable("commands.diseases_removed_count", disease, entityCount));

        return 1;
    }

    public static int diseaseClear(CommandSourceStack source, Collection<? extends Entity> entities) throws CommandSyntaxException {

        ServerPlayer player = source.getPlayerOrException();

        if (!WCEServerConfig.SERVER.DISEASES.get()) {
            player.sendSystemMessage(Component.translatable("commands.diseases_disabled")
                    .withStyle(ChatFormatting.RED));
            return 0;
        }

        int entityCount = 0;

        for (Entity entity : entities) {
            if (!(entity instanceof Diseaseable<?> diseaseable)) continue;

            diseaseable.getList().clear();
            diseaseable.onChange();

            entityCount++;
        }

        player.sendSystemMessage(Component.translatable("commands.all_diseases_removed", entityCount));

        return 1;
    }

    public static int clanDisband(CommandSourceStack source, ServerPlayer player) {

        ServerLevel level = player.serverLevel().getServer().overworld();
        ClanData data = ClanData.get(level);

        UUID clanUUID = player.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();

        if (clanUUID.equals(ClanData.EMPTY_UUID)) {
            player.sendSystemMessage(
                    Component.translatable("clan.player_not_clan").withStyle(ChatFormatting.GRAY)
            );
        }

        ClanData.Clan clan = data.getClan(clanUUID);
        if (clan != null) {
            boolean can = clan.memberPerms.get(player.getUUID()) == ClanData.ClanPermissions.OWNER;
            if (can) {
                data.deleteClan(level, clan.clanUUID);
                player.sendSystemMessage(
                        Component.translatable("commands.clan_disbanded",
                                Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color)))
                );
                data.setDirty();
            } else {
                player.sendSystemMessage(Component.translatable("clan.no_permissions").withStyle(ChatFormatting.YELLOW));

            }
        }

        return 1;
    }

    public static int getProfileData(CommandSourceStack source, ServerPlayer targetToShow) {



        CapabilityManager.attachmentProvider(targetToShow , ModAttachments.PLAYER_WCE_DATA, cap -> {

            String genderText = switch (cap.getGenderData()) {
                case 0 -> "Male";
                case 1 -> "Female";
                default -> cap.getGenderText();
            };

            source.sendSuccess(
                    () -> Component.translatable("commands.display_player_info", targetToShow.getName().copy()).append(Component.literal(
                            "\n==================" +
                                    "\nClan: " + cap.getClanName(targetToShow.serverLevel()) +
                                    "\nClanUUID: " + cap.getCurrentClanUUID() +
                                    "\nCharacter Name: " + cap.getMorphName() +
                                    "\nMate: " + cap.getMateName().copy() +
                                    "\nMateUUID: " + cap.getMateUUID() +
                                    "\nPrefix: " + cap.getPrefix() +
                                    "\nSuffix: " + cap.getSufix() +
                                    "\nPreferred Variant: " + cap.getVariantData() +
                                    "\nGender: " + genderText +
                                    "\nAge: " + cap.getMorphAge() +
                                    "\nUses suffixes: " + cap.isUseSufixes() +
                                    "\nRegistered: " + cap.isFirstLoginHandled() +
                                    "\n=================="

                    ).withStyle(ChatFormatting.GRAY)),
                    false
            );

        });
        return 1;

    }

    public static int infoProfile(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_WCE_DATA, clanData -> {
            ModPackets.sendToPlayer(new S2CSyncClanDataPacket(clanData), player);
        });

        String name = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();
        String clanName = player.getData(ModAttachments.PLAYER_WCE_DATA).getClanName(player.serverLevel());
        int gender = player.getData(ModAttachments.PLAYER_WCE_DATA).getGenderData();
        String genderText = switch (gender) {
            case 0 -> Component.translatable("generic.wcat.tomcat").getString();
            case 1 -> Component.translatable("generic.wcat.shecat").getString();
            default -> player.getData(ModAttachments.PLAYER_WCE_DATA).getGenderText();
        };

        String mateName = (player.getData(ModAttachments.PLAYER_WCE_DATA).getMateName()).getString();

        WCEPlayerData.Age age = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphAge();

        int targetKitCooldown = player.getData(ModAttachments.PLAYER_WCE_DATA).getPlayerKitsCooldown();

        int myKitCooldown = player.getData(ModAttachments.PLAYER_WCE_DATA).getPlayerKitsCooldown();

        String bio = (player.getData(ModAttachments.PLAYER_WCE_DATA).getCharacterBio());


        WCEPlayerData.PackedData targetData =
                new WCEPlayerData.PackedData(name, clanName, genderText, mateName, age, targetKitCooldown, bio);


        UUID targetUUID = player.getUUID();

        if (player instanceof Diseaseable<?> diseaseable) {
            diseaseable.onChange();
        }

        ModPackets.sendToPlayer(new OpenPlayerCatDataScreenPacket(targetData, targetUUID, myKitCooldown, true), player);

        return 1;
    }

    public static int infoSetup(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        ModPackets.sendToPlayer(new OpenClanSetupScreenPacket(), player);

        CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_WCE_DATA,clanData -> {
            ModPackets.sendToPlayer(new S2CSyncClanDataPacket(clanData), player);
        });

        return 1;
    }

    public static int invitePlayerToClan(CommandSourceStack source, ServerPlayer invitedPlayer) throws CommandSyntaxException {
        ServerPlayer sPlayer = source.getPlayerOrException();


        String hostMorphName = sPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();
        String invitedMorphName = invitedPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

        UUID invitingClanId = sPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();
        UUID currentClanId = invitedPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();

        ClanData data = ClanData.get(invitedPlayer.serverLevel().getServer().overworld());
        ClanData.Clan clan = data.getClan(invitingClanId);

        if (invitedPlayer == sPlayer) {
            sPlayer.sendSystemMessage(Component.translatable("commands.cannot_invite_self").withStyle(ChatFormatting.GRAY));
            return 0;
        }

        if (invitingClanId.equals(ClanData.EMPTY_UUID)) {
            sPlayer.sendSystemMessage(Component.translatable("clan.player_not_clan").withStyle(ChatFormatting.GRAY));
            return 0;
        }

        if (clan != null) {
            boolean canInvite = data.canInvite(clan, sPlayer.getUUID());
            if (!canInvite) {
                sPlayer.sendSystemMessage(Component.translatable("clan.no_permissions").withStyle(ChatFormatting.YELLOW));
                return 0;
            }
        }

        if (ClanInviteManager.getInvite(invitedPlayer) != null) {
            sPlayer.sendSystemMessage(Component.translatable("commands.invite_pending").withStyle(ChatFormatting.YELLOW));
            return 0;
        }

        if (!currentClanId.equals(ClanData.EMPTY_UUID)) {
            if (data.getClan(currentClanId) ==  null) {
                CapabilityManager.attachmentProvider(invitedPlayer, ModAttachments.PLAYER_WCE_DATA, cap -> {
                    cap.setCurrentClanUUID(ClanData.EMPTY_UUID);
                });
            } else {
                sPlayer.sendSystemMessage(Component.translatable("clan.target_already_in_clan").withStyle(ChatFormatting.YELLOW));
            }
        }

        if (clan != null) {

            sPlayer.sendSystemMessage(
                    Component.translatable("commands.invite_to_clan",
                            Component.literal(invitedMorphName).withStyle(ChatFormatting.GOLD),
                            Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color))
                    ));

            invitedPlayer.sendSystemMessage(
                    Component.translatable("commands.invite_received",
                            Component.literal(hostMorphName).withStyle(ChatFormatting.AQUA),
                            Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color))
                    )
            );

            ClanInviteManager.invite(invitedPlayer, invitingClanId, sPlayer);

            invitedPlayer.sendSystemMessage(
                    ClanInviteManager.message()
            );
        }

        return 1;
    }

    public static int kickClanMember(CommandSourceStack source, ServerPlayer targetPlayer) throws CommandSyntaxException {
        ServerPlayer sPlayer = source.getPlayerOrException();


        String hostMorphName = sPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();
        String kickedMorphName = targetPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

        UUID targetClanId = sPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();
        UUID currentMemberClanId = targetPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();

        ClanData data = ClanData.get(targetPlayer.serverLevel().getServer().overworld());
        ClanData.Clan targetClan = data.getClan(targetClanId);
        ClanData.Clan currentMemberClan = data.getClan(currentMemberClanId);

        if (targetClanId.equals(ClanData.EMPTY_UUID)) {
            sPlayer.sendSystemMessage(Component.translatable("clan.player_not_clan").withStyle(ChatFormatting.GRAY));
            return 0;
        }

        if (targetClan != null) {
            boolean canKick = data.canManage(targetClan, sPlayer.getUUID());
            boolean canKickThisOne = data.canManagePlayer(targetClanId, sPlayer.getUUID(), targetPlayer.getUUID());

            if (!canKick || !canKickThisOne) {
                sPlayer.sendSystemMessage(Component.translatable("clan.no_permissions").withStyle(ChatFormatting.YELLOW));
                return 0;
            }
        }

        if (targetPlayer == sPlayer) {
            sPlayer.sendSystemMessage(Component.translatable("commands.clan.cant_kick_self").withStyle(ChatFormatting.YELLOW));
            return 0;
        }

        if (!currentMemberClanId.equals(ClanData.EMPTY_UUID)) {
            if (data.getClan(currentMemberClanId) == null) {
                CapabilityManager.attachmentProvider(targetPlayer , ModAttachments.PLAYER_WCE_DATA, cap -> {
                    cap.setCurrentClanUUID(ClanData.EMPTY_UUID);
                });
                return 0;
            }
        }

        if (targetClan == currentMemberClan) {
            data.removeMember(targetPlayer,  targetClanId);
            data.setDirty();

            sPlayer.sendSystemMessage(
                    Component.translatable("clan.player_removed",
                            Component.literal(kickedMorphName).withStyle(ChatFormatting.GOLD),
                            Component.literal(targetClan.name).withStyle(Style.EMPTY.withColor(targetClan.color)))
            );
        }

        return 1;
    }

    public static int kitRequestAccept(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        PlayerKittingRequestManager.KitRequest request = PlayerKittingRequestManager.getRequest(player);
        if (request == null) {
            player.sendSystemMessage(
                    Component.translatable("managers.no_request_pending")
                            .withStyle(ChatFormatting.GRAY)
            );
            return 0;
        }

        ServerLevel level = player.serverLevel();

        Entity ent = level.getEntity(request.requester);
        if (ent == null) {
            source.sendFailure(Component.translatable("argument.entity.notfound.player"));
            return 0;
        } else {
            if (ent instanceof ServerPlayer requester && PlayerShape.getCurrentShape(requester) instanceof WCatEntity) {

                int kittingCD = (int) ((WCEServerConfig.SERVER.KIT_GROWTH_MINUTES.get() * 20 * 60) * 0.75f);

                List<Player> toPerform = new ArrayList<>();
                toPerform.add(requester);
                toPerform.add(player);

                for (Player p : toPerform) {
                    ItemStack kitStack = new ItemStack(ModItems.KIT_ITEM.get(), 1 + p.getRandom().nextInt(2));

                    level.playSound(null, p.blockPosition(), SoundEvents.CAT_PURREOW,
                            SoundSource.AMBIENT, 0.7F, 1.0F);
                    level.sendParticles(ParticleTypes.HEART, p.getX(), p.getY(), p.getZ(),
                            3, 0.2f,0.2f,0.2f,0.2f);

                    String myMorphName = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();
                    String myMate = (player.getData(ModAttachments.PLAYER_WCE_DATA).getMateName()).getString();

                    if (!p.addItem(kitStack)) {
                        p.drop(kitStack, false);
                    }

                    CapabilityManager.attachmentProvider(p, ModAttachments.PLAYER_WCE_DATA, cap -> {
                        cap.setPlayerKitsCooldown(kittingCD);
                    });

                    p.sendSystemMessage(
                            Component.translatable("commands.kit_request_accepted",
                                    Component.literal(myMorphName).withStyle(ChatFormatting.AQUA),
                                    Component.literal(myMate).withStyle(ChatFormatting.AQUA))
                    );

                }

                PlayerKittingRequestManager.clear(player);
            }
        }

        return 1;
    }

    public static int kitRequestDeny(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        PlayerKittingRequestManager.KitRequest request = PlayerKittingRequestManager.getRequest(player);
        if (request == null) {
            player.sendSystemMessage(
                    Component.translatable("managers.no_request_pending").withStyle(ChatFormatting.GRAY)
            );
            return 0;
        }

        PlayerKittingRequestManager.clear(player);


        String deniedPlayerName = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();


        player.sendSystemMessage(
                Component.empty()
                        .append(Component.translatable("managers.request_declined").withStyle(ChatFormatting.GRAY))
        );


        ServerPlayer requester = player.server.getPlayerList().getPlayer(request.requester);
        if (requester != null) {
            requester.sendSystemMessage(
                    Component.translatable("commands.target_request_declined",
                            Component.literal(deniedPlayerName).withStyle(ChatFormatting.GOLD))
            );
        }

        return 1;
    }

    public static int leaveClan(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer sPlayer = source.getPlayerOrException();


        String morphName = sPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

        UUID targetClanId = sPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();

        ClanData data = ClanData.get(sPlayer.serverLevel().getServer().overworld());
        ClanData.Clan targetClan = data.getClan(targetClanId);

        if (targetClanId.equals(ClanData.EMPTY_UUID)) {
            sPlayer.sendSystemMessage(Component.translatable("clan.player_not_clan").withStyle(ChatFormatting.GRAY));
            return 0;
        } else {
            if (targetClan == null) {
                CapabilityManager.attachmentProvider(sPlayer , ModAttachments.PLAYER_WCE_DATA, cap -> {
                    cap.setCurrentClanUUID(ClanData.EMPTY_UUID);
                });
                return 0;
            }
        }

        boolean isOwner = (targetClan.members.get(sPlayer.getUUID()) == ClanData.ClanPlayerRank.LEADER)
                && (targetClan.memberPerms.get(sPlayer.getUUID()) == ClanData.ClanPermissions.OWNER);
        if (isOwner) {
            sPlayer.sendSystemMessage(Component.translatable("clan.cant_leave_when_leader").withStyle(ChatFormatting.YELLOW));
            return 0;
        }


        data.removeMember(sPlayer, targetClanId);
        data.setDirty();

        sPlayer.sendSystemMessage(
                Component.translatable("clan.self_left_clan",
                        Component.literal(targetClan.name).withStyle(Style.EMPTY.withColor(targetClan.color)))
        );

        for (UUID memberUUID : targetClan.members.keySet()) {
            ServerPlayer member = sPlayer.server.getPlayerList().getPlayer(memberUUID);
            if (member == null) continue;


            member.sendSystemMessage(
                    Component.translatable("clan.cat_left_clan",
                            Component.literal(morphName).withStyle(ChatFormatting.GOLD),
                            Component.literal(targetClan.name).withStyle(Style.EMPTY.withColor(targetClan.color)))
            );
        }

        return 1;
    }

    public static int manageClan(CommandSourceStack source, ServerPlayer player) {

        ClanData data = ClanData.get(player.serverLevel().getServer().overworld());

        UUID clanUUID = player.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();

        if (clanUUID.equals(ClanData.EMPTY_UUID)) {
            player.sendSystemMessage(
                    Component.translatable("clan.player_not_clan").withStyle(ChatFormatting.GRAY)
            );
        }

        ClanData.Clan clan = data.getClan(clanUUID);
        if (clan != null) {
            boolean can = data.canManage(clan, player.getUUID());
            if (can) {

                Map<UUID, ClanInfo.Member> playersInClan = new HashMap<>();
                for (UUID uuid : clan.members.keySet()) {
                    ServerPlayer clanMember = player.serverLevel().getServer()
                            .getPlayerList().getPlayer(uuid);

                    String morphName = data.playerMorphNames.getOrDefault(uuid, "Unknown");
                    WCGenetics.PackedGeneticData morphData = data.playerMorphData.getOrDefault(uuid, WCGenetics.PackedGeneticData.empty());
                    String rank = String.valueOf(clan.members.get(uuid));
                    String perms = String.valueOf(clan.memberPerms.get(uuid));
                    String age = "Undefined";
                    boolean isOnline = false;

                    if (clanMember != null) {
                        age = String.valueOf(clanMember.getData(ModAttachments.PLAYER_WCE_DATA).getMorphAge());
                        isOnline = true;
                    }

                    ClanInfo.Member member = new ClanInfo.Member(uuid, morphName, rank, perms, age, isOnline,
                            morphData.genetics, morphData.variants, morphData.chimerasGenetics, morphData.chimeraVariants,
                            morphData.onGeneticalSkin, morphData.morphSkin);

                    playersInClan.put(uuid, member);
                }


                ClanInfo clanInfo = new ClanInfo(clan.clanUUID, clan.name, clan.color, clan.leaderName, clan.clanBioSentence,
                        clan.members.size(), playersInClan, clan.clanSymbolIndex);

                ModPackets.sendToPlayer(new S2CManageClanPacket(clanInfo), player);

                return 1;

            } else {
                player.sendSystemMessage(Component.translatable("clan.no_permissions").withStyle(ChatFormatting.RED));

            }
        }
        return 0;
    }

    public static int mateAccept(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        PlayerMateRequestManager.MateRequest request = PlayerMateRequestManager.getRequest(player);
        if (request == null) {
            player.sendSystemMessage(
                    Component.translatable("managers.no_request_pending")
                            .withStyle(ChatFormatting.GRAY)
            );
            return 0;
        }

        Entity ent = player.serverLevel().getEntity(request.requester);
        if (ent == null) {
            source.sendFailure(Component.translatable("argument.entity.notfound.player"));
            return 0;
        } else {
            if (ent instanceof ServerPlayer requester && PlayerShape.getCurrentShape(requester) instanceof WCatEntity) {

                String myMorphName = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();
                String targetMorphName = requester.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

                boolean isPlayerChimera = WCGenetics.Chimerism.isChimera(player.getData(ModAttachments.PLAYER_WCE_DATA).getPlayerGenetics().chimeraGene);

                boolean isRequesterChimera = WCGenetics.Chimerism.isChimera(requester.getData(ModAttachments.PLAYER_WCE_DATA).getPlayerGenetics().chimeraGene);

                WCGenetics playerGenetics;
                if (player.getRandom().nextBoolean() && isPlayerChimera) {
                    playerGenetics = player.getData(ModAttachments.PLAYER_WCE_DATA).getPlayerChimeraGenetics();
                } else {
                    playerGenetics = player.getData(ModAttachments.PLAYER_WCE_DATA).getPlayerGenetics();
                }

                WCGenetics requesterGenetics;
                if (requester.getRandom().nextBoolean() && isRequesterChimera) {
                    requesterGenetics = player.getData(ModAttachments.PLAYER_WCE_DATA).getPlayerChimeraGenetics();
                } else {
                    requesterGenetics = player.getData(ModAttachments.PLAYER_WCE_DATA).getPlayerGenetics();
                }

                CapabilityManager.attachmentProvider(requester, ModAttachments.PLAYER_WCE_DATA, data -> {
                    data.setMateUUID(player.getUUID());
                    data.setMateName(Component.literal(myMorphName));
                    data.setMateGenetics(playerGenetics);
                });
                CapabilityManager.attachmentProvider(player , ModAttachments.PLAYER_WCE_DATA, data -> {
                    data.setMateUUID(requester.getUUID());
                    data.setMateName(Component.literal(targetMorphName));
                    data.setMateGenetics(requesterGenetics);
                });


                player.sendSystemMessage(Component.translatable("managers.new_couple", myMorphName, targetMorphName)
                        .withStyle(ChatFormatting.GREEN));
                requester.sendSystemMessage(Component.translatable("managers.new_couple", targetMorphName, myMorphName)
                        .withStyle(ChatFormatting.GREEN));


                List<Player> toPerform = new ArrayList<>();
                toPerform.add(requester);
                toPerform.add(player);
                ServerLevel level = player.serverLevel();

                for (Player p : toPerform) {
                    level.playSound(null, p.blockPosition(), SoundEvents.CAT_PURREOW,
                            SoundSource.AMBIENT, 0.7F, 1.0F);
                    level.sendParticles(ParticleTypes.HEART, p.getX(), p.getY(), p.getZ(),
                            3, 0.2f,0.2f,0.2f,0.2f);
                }


                PlayerMateRequestManager.clear(player);
            }
        }

        return 1;
    }

    public static int mateDecline(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        PlayerMateRequestManager.MateRequest request = PlayerMateRequestManager.getRequest(player);
        if (request == null) {
            player.sendSystemMessage(
                    Component.translatable("managers.no_request_pending")
            );
            return 0;
        }

        PlayerMateRequestManager.clear(player);


        String deniedPlayerName = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();


        player.sendSystemMessage(
                Component.empty()
                        .append(Component.translatable("managers.proposal_declined").withStyle(ChatFormatting.GRAY))
        );


        ServerPlayer requester = player.server.getPlayerList().getPlayer(request.requester);
        if (requester != null) {
            requester.sendSystemMessage(
                    Component.translatable("managers.player_declined_proposal",
                            Component.literal(deniedPlayerName).withStyle(ChatFormatting.GOLD))
            );

            ItemStack stack = new ItemStack(ModItems.MYSTIC_FLOWERS_BOUQUET.get());
            if (!requester.addItem(stack)) {
                requester.drop(stack, false);
            }
        }

        ServerLevel level = player.serverLevel();

        level.sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY(), player.getZ(),
                10, 0.2f,0.2f,0.2f,0.2f);

        return 1;
    }

    public static int mateDivorce(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        UUID currentMateUUID = player.getData(ModAttachments.PLAYER_WCE_DATA).getMateUUID();

        if (currentMateUUID.equals(ClanData.EMPTY_UUID)) {
            player.sendSystemMessage(
                    Component.empty()
                            .append(Component.translatable("managers.player_dont_have_mate").withStyle(ChatFormatting.GRAY))
            );
            return 0;
        } else {

            CapabilityManager.attachmentProvider(player , ModAttachments.PLAYER_WCE_DATA, data -> {
                data.setMateUUID(ClanData.EMPTY_UUID);
                data.setMateName(Component.literal("None"));

                player.sendSystemMessage(Component.translatable("managers.no_longer_has_mate"));

            });

            Entity entity = player.serverLevel().getEntity(currentMateUUID);

            if (entity instanceof Player exMate) {
                String myMorphName = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

                CapabilityManager.attachmentProvider(exMate , ModAttachments.PLAYER_WCE_DATA, data -> {
                    data.setMateUUID(ClanData.EMPTY_UUID);
                    data.setMateName(Component.literal("None"));
                });

                exMate.sendSystemMessage(
                        Component.translatable("managers.player_has_divorced",
                                Component.literal(myMorphName).withStyle(ChatFormatting.AQUA))
                );

            } else if (entity instanceof WCatEntity cat) {
                cat.setMateUUID(ClanData.EMPTY_UUID);
                cat.setMate(Component.literal("None"));
            }

            return 1;

        }
    }

    public static int adminForceDelete(CommandSourceStack source, String clanName) {

        ServerLevel level = source.getLevel();
        ClanData data = ClanData.get(level);

        ClanData.Clan targetClan = data.getClanByName(clanName);

        if (targetClan == null) {
            source.sendFailure(Component.translatable("commands.clan_dont_exist").withStyle(ChatFormatting.RED));
            return 0;
        }

        int color = targetClan.color;
        String clanRealName = targetClan.name;

        data.deleteClan(level, targetClan.clanUUID);

        source.sendSystemMessage(
                Component.translatable("commands.clan_deleted",
                        Component.literal(clanRealName).withStyle(Style.EMPTY.withColor(color)))
        );


        return 1;
    }

    public static int adminInvite(CommandSourceStack source, String clanName, ServerPlayer invitedPlayer) throws CommandSyntaxException {

        ServerLevel level = source.getLevel();
        ClanData data = ClanData.get(level);

        ClanData.Clan clan = data.getClanByName(clanName);

        if (clan == null) {
            source.sendFailure(Component.translatable("commands.clan_dont_exist").withStyle(ChatFormatting.RED));
            return 0;
        }

        ServerPlayer sPlayer = source.getPlayerOrException();
        if (invitedPlayer == null) return 0;

        String hostMorphName = sPlayer.getName().getString();

        String invitedMorphName = invitedPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

        UUID currentClanId = invitedPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();

        if (invitedPlayer == sPlayer) {
            sPlayer.sendSystemMessage(Component.translatable("commands.cannot_invite_self").withStyle(ChatFormatting.GRAY));
            return 0;
        }

        if (ClanInviteManager.getInvite(invitedPlayer) != null) {
            sPlayer.sendSystemMessage(Component.translatable("commands.invite_pending").withStyle(ChatFormatting.YELLOW));
            return 0;
        }

        if (!currentClanId.equals(ClanData.EMPTY_UUID)) {
            if (data.getClan(currentClanId) ==  null) {
                CapabilityManager.attachmentProvider(invitedPlayer, ModAttachments.PLAYER_WCE_DATA, cap -> {
                    cap.setCurrentClanUUID(ClanData.EMPTY_UUID);
                });
            } else {
                sPlayer.sendSystemMessage(Component.translatable("clan.target_already_in_clan").withStyle(ChatFormatting.YELLOW));
            }
        }

        sPlayer.sendSystemMessage(
                Component.translatable("commands.invite_to_clan",
                        Component.literal(invitedMorphName).withStyle(ChatFormatting.GOLD),
                        Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color))
                ));

        invitedPlayer.sendSystemMessage(
                Component.translatable("commands.invite_received",
                        Component.literal(hostMorphName).withStyle(ChatFormatting.AQUA),
                        Component.literal(clan.name).withStyle(Style.EMPTY.withColor(clan.color))
                )
        );

        ClanInviteManager.invite(invitedPlayer, clan.clanUUID, sPlayer);

        invitedPlayer.sendSystemMessage(
                ClanInviteManager.message()
        );

        return 1;
    }

    public static int createMorphMenu(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        ModPackets.sendToPlayer(new OpenCreateMorphPacket(false), player);

        return 1;
    }

    public static int summonMenu(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        ModPackets.sendToPlayer(new OpenCreateMorphPacket(true), player);

        return 1;
    }

    public static int clanRegisterMenu(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        String morphName = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();
        UUID currentClanId = player.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();

        if (!currentClanId.equals(ClanData.EMPTY_UUID)) {
            ClanData data = ClanData.get(player.serverLevel().getServer().overworld());
            if (data.getClan(currentClanId) ==  null) {
                CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_WCE_DATA, cap -> {
                    cap.setCurrentClanUUID(ClanData.EMPTY_UUID);
                });
            } else {
                player.sendSystemMessage(Component.translatable("commands.leave_clan_to_create_clan").withStyle(ChatFormatting.YELLOW));
            }
            return 0;
        }

        ModPackets.sendToPlayer(new S2COpenRegisterClanScreenPacket(morphName), player);

        return 1;
    }

    public static int dataReset(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        ClanData data = ClanData.get(player.serverLevel().getServer().overworld());

        CapabilityManager.attachmentProvider(player , ModAttachments.PLAYER_WCE_DATA, cap -> {
            cap.reset();
            ModPackets.sendToPlayer(new S2CSyncClanDataPacket(cap), player);
            data.playerMorphNames.put(player.getUUID(), cap.getMorphName());

            WCGenetics.PackedGeneticData morphData =
                    new WCGenetics.PackedGeneticData(cap.getPlayerGenetics(),
                            cap.getPlayerGeneticalVariants(),
                            cap.getPlayerChimeraGenetics(),
                            cap.getPlayerChimeraVariants(),
                            cap.isOnGeneticalSkin(), cap.getVariantData());

            data.playerMorphData.put(player.getUUID(), morphData);
            data.setDirty();
        });

        source.sendSuccess(
                () -> Component.translatable("commands.reset_player_data"),
                false
        );

        return 1;
    }

    public static int resyncShapes(CommandSourceStack source) throws CommandSyntaxException {

        ServerPlayer player = source.getPlayerOrException();

        if (player.getServer() == null) return 0;

        List<ServerPlayer> players = player.getServer().getPlayerList().getPlayers();

        for (ServerPlayer p : players) {
            if (p != null) {
                if (PlayerShape.getCurrentShape(p) instanceof WCatEntity) {
                    PlayerShape.sync(p, player);
                }
            }
        }

        player.sendSystemMessage(Component.translatable("commands.resync_shapes").withStyle(ChatFormatting.ITALIC));
        return 1;
    }

    public static int setNewClanLeader(CommandSourceStack source, ServerPlayer targetPlayer) throws CommandSyntaxException {
        ServerPlayer sPlayer = source.getPlayerOrException();



        String hostMorphName = sPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();
        String targetMorphName = targetPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

        UUID targetClanId = sPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();
        UUID currentMemberClanId = targetPlayer.getData(ModAttachments.PLAYER_WCE_DATA).getCurrentClanUUID();

        ClanData data = ClanData.get(targetPlayer.serverLevel().getServer().overworld());
        ClanData.Clan targetClan = data.getClan(targetClanId);
        ClanData.Clan currentMemberClan = data.getClan(currentMemberClanId);


        if (targetClanId.equals(ClanData.EMPTY_UUID)) {
            sPlayer.sendSystemMessage(Component.translatable("clan.player_not_clan").withStyle(ChatFormatting.GRAY));
            return 0;
        }

        if (targetClan != null) {
            boolean canManage = targetClan.members.get(sPlayer.getUUID()) == ClanData.ClanPlayerRank.LEADER
                    && targetClan.memberPerms.get(sPlayer.getUUID()) == ClanData.ClanPermissions.OWNER;
            if (!canManage) {
                sPlayer.sendSystemMessage(Component.translatable("clan.no_permissions").withStyle(ChatFormatting.YELLOW));
                return 0;
            }
        }

        if (targetPlayer == sPlayer) {
            sPlayer.sendSystemMessage(Component.translatable("clan.already_a_leader").withStyle(ChatFormatting.YELLOW));
            return 0;
        }

        if (!currentMemberClanId.equals(ClanData.EMPTY_UUID)) {
            if (data.getClan(currentMemberClanId) == null) {
                CapabilityManager.attachmentProvider(targetPlayer , ModAttachments.PLAYER_WCE_DATA, cap -> {
                    cap.setCurrentClanUUID(ClanData.EMPTY_UUID);
                });
                return 0;
            }
        }

        if (targetClan == currentMemberClan && targetClan != null) {

            for (Map.Entry<UUID, ClanData.ClanPlayerRank> entry : targetClan.members.entrySet()) {
                if (entry.getValue() == ClanData.ClanPlayerRank.LEADER) {
                    entry.setValue(ClanData.ClanPlayerRank.WARRIOR);
                }
            }

            for (Map.Entry<UUID, ClanData.ClanPermissions> entry : targetClan.memberPerms.entrySet()) {
                if (entry.getValue() == ClanData.ClanPermissions.OWNER) {
                    entry.setValue(ClanData.ClanPermissions.MEMBER);
                }
            }


            data.changeMemberRank(targetPlayer, targetClanId, ClanData.ClanPlayerRank.LEADER);
            data.changeMemberPermissions(targetPlayer, targetClanId, ClanData.ClanPermissions.OWNER);

            targetClan.leaderName = hostMorphName;
            data.setDirty();

            String morphName = data.playerMorphNames.getOrDefault(targetPlayer.getUUID(), "Unknown");

            sPlayer.sendSystemMessage(
                    Component.translatable("clan.given_leadership",
                            Component.literal(morphName).withStyle(ChatFormatting.GOLD))
            );

            for (UUID memberUUID : targetClan.members.keySet()) {
                ServerPlayer member = sPlayer.server.getPlayerList().getPlayer(memberUUID);
                if (member == null) continue;

                member.sendSystemMessage(
                        Component.translatable("clan.new_leader_announce",
                                Component.literal(targetMorphName).withStyle(ChatFormatting.GOLD),
                                Component.literal(targetClan.name).withStyle(Style.EMPTY.withColor(targetClan.color))
                        ));
            }

        }

        return 1;
    }

    public static int setPoseMenu(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        if (!(PlayerShape.getCurrentShape(player) instanceof WCatEntity)) return 0;

        ModPackets.sendToPlayer(new OpenPoseMenuPacket(), player);

        return 1;
    }

    public static int toggleChatMorph(ServerPlayer player) {

        CapabilityManager.attachmentProvider(player , ModAttachments.PLAYER_WCE_DATA, cap -> {
            boolean morphNameInChat = cap.isMorphNameInChat();

            Component message = morphNameInChat ?
                    Component.translatable("commands.show_morph_name_toggle",
                            Component.literal("OFF").withStyle(ChatFormatting.RED))
                    :
                    Component.translatable("commands.show_morph_name_toggle",
                            Component.literal("ON").withStyle(ChatFormatting.GREEN));

            cap.setMorphNameInChat(!morphNameInChat);
            player.sendSystemMessage(
                    Component.empty()
                            .append(Component.literal("[WCE] ").withStyle(ChatFormatting.GOLD))
                            .append(message.copy())
            );

        });

        return 1;
    }

    public static int toggleFancyFont(ServerPlayer player) {

        CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_WCE_DATA, cap -> {
            boolean usingFancyFont = cap.isUsingFancyFont();

            Component message = usingFancyFont ?
                    Component.translatable("commands.fancy_morphname_font",
                            Component.literal("OFF").withStyle(ChatFormatting.RED))
                    :
                    Component.translatable("commands.fancy_morphname_font",
                            Component.literal("ON").withStyle(ChatFormatting.GREEN));

            cap.setUsingFancyFont(!usingFancyFont);
            player.sendSystemMessage(
                    Component.empty()
                            .append(Component.literal("[WCE] ").withStyle(ChatFormatting.GOLD))
                            .append(message.copy())
            );

        });

        return 1;
    }

    public static int wceGamerule(ServerPlayer player, boolean argument, WCECommands.ConfigType commandType) {

        ServerLevel level = player.serverLevel();

        switch (commandType) {
            case THIRST:
                WCEServerConfig.SERVER.THIRST.set(argument);
                break;

            case LEAP:
                WCEServerConfig.SERVER.LEAP_SERVER.set(argument);
                break;

            case SKILL:
                WCEServerConfig.SERVER.SKILL_TREE_SERVER.set(argument);
                for (ServerPlayer toAffect : level.getServer().getPlayerList().getPlayers()) {
                    CapabilityManager.attachmentProvider(toAffect, ModAttachments.PLAYER_SKILL, skillData -> {
                        if (!argument) PlayerSkill.removeAttributes(toAffect);
                        else PlayerSkill.reviveAttributes(toAffect, skillData);
                    });
                }
                break;

            case AGGRESSIVE_ANIMALS:
                WCEServerConfig.SERVER.ENHANCED_ANIMALS.set(argument);
                break;

            case CHOOSE_SPAWN_LOC:
                WCEServerConfig.SERVER.TELEPORT_WHEN_JOIN.set(argument);
                break;

            case PROTECT_TERRITORY_CONTAINERS:
                WCEServerConfig.SERVER.PROTECT_CONTAINERS.set(argument);
                break;

            case PROTECT_TERRITORY_BLOCKS:
                WCEServerConfig.SERVER.PROTECT_PLACE_AND_BREAK_BLOCKS.set(argument);
                break;

            case DISEASES:
                WCEServerConfig.SERVER.DISEASES.set(argument);
                break;
        }

        WCEServerConfig.SPEC.save();

        if (commandType == WCECommands.ConfigType.DISEASES) {
            for (ServerPlayer toAffect : level.getServer().getPlayerList().getPlayers()) {
                if (toAffect instanceof Diseaseable<?> diseaseable) {
                    diseaseable.onChange();
                }
            }
        }


        player.sendSystemMessage(Component.translatable("commands.gamerule.set",
                commandType.name(), argument));


        return 1;
    }

}
