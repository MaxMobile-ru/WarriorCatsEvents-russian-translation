package net.snowteb.warriorcats_events.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.diseases.DiseaseRegistry;
import net.snowteb.warriorcats_events.diseases.DiseaseType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class WCECommands {

    public enum ConfigType {
        THIRST,
        SKILL,
        AGGRESSIVE_ANIMALS,
        LEAP,
        CHOOSE_SPAWN_LOC,
        PROTECT_TERRITORY_CONTAINERS,
        PROTECT_TERRITORY_BLOCKS,
        DISEASES,

    }

    private static final SuggestionProvider<CommandSourceStack> CLAN_SUGGESTIONS =
            (ctx, builder) -> {
                ServerLevel level = ctx.getSource().getLevel();
                ClanData data = ClanData.get(level);
                return SharedSuggestionProvider.suggest(data.clans.values().stream()
                        .map(clan -> clan.normalizedName).toList(), builder);
            };

    private static final SuggestionProvider<CommandSourceStack> DISEASES =
            (ctx, builder) -> {
                List<String> diseaseIDS = DiseaseRegistry.getList().values().stream().map(DiseaseType::getID).toList();
                return SharedSuggestionProvider.suggest(diseaseIDS, builder);
            };

    private static final SuggestionProvider<CommandSourceStack> RANK_SUGGESTIONS =
            (ctx, builder) -> SharedSuggestionProvider.suggest(
                    Arrays.stream(ClanData.ClanPlayerRank.values())
                            .filter(rank -> rank != ClanData.ClanPlayerRank.LEADER)
                            .map(Enum::name)
                            .toList(),
                    builder
            );

    private static final SuggestionProvider<CommandSourceStack> PERMS_SUGGESTIONS =
            (ctx, builder) -> SharedSuggestionProvider.suggest(
                    Arrays.stream(ClanData.ClanPermissions.values())
                            .filter(rank -> rank != ClanData.ClanPermissions.OWNER)
                            .map(Enum::name)
                            .toList(),
                    builder
            );


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("gamerule")
                        .requires(source -> source.hasPermission(3))

                        .then(Commands.literal("wceThirstSystem")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> WCECommandHandles.wceGamerule(ctx.getSource().getPlayerOrException(), BoolArgumentType.getBool(ctx, "value"), ConfigType.THIRST))
                                )
                        )
                        .then(Commands.literal("wceSkillSystem")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> WCECommandHandles.wceGamerule(ctx.getSource().getPlayerOrException(), BoolArgumentType.getBool(ctx, "value"), ConfigType.SKILL))
                                )
                        )
                        .then(Commands.literal("wceAggressiveAnimals")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> WCECommandHandles.wceGamerule(ctx.getSource().getPlayerOrException(), BoolArgumentType.getBool(ctx, "value"), ConfigType.AGGRESSIVE_ANIMALS))
                                )
                        )
                        .then(Commands.literal("wceLeapSystem")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> WCECommandHandles.wceGamerule(ctx.getSource().getPlayerOrException(), BoolArgumentType.getBool(ctx, "value"), ConfigType.LEAP))
                                )
                        )
                        .then(Commands.literal("wceChooseSpawnLocation")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> WCECommandHandles.wceGamerule(ctx.getSource().getPlayerOrException(), BoolArgumentType.getBool(ctx, "value"), ConfigType.CHOOSE_SPAWN_LOC))
                                )
                        )
                        .then(Commands.literal("wceProtectContainersInTerritory")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> WCECommandHandles.wceGamerule(ctx.getSource().getPlayerOrException(), BoolArgumentType.getBool(ctx, "value"), ConfigType.PROTECT_TERRITORY_CONTAINERS))
                                )
                        )
                        .then(Commands.literal("wceProtectBlocksInTerritory")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> WCECommandHandles.wceGamerule(ctx.getSource().getPlayerOrException(), BoolArgumentType.getBool(ctx, "value"), ConfigType.PROTECT_TERRITORY_BLOCKS))
                                )
                        )
                        .then(Commands.literal("wceDiseases")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> WCECommandHandles.wceGamerule(ctx.getSource().getPlayerOrException(), BoolArgumentType.getBool(ctx, "value"), ConfigType.DISEASES))
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("chatMorphName")
                                .then(Commands.literal("toggleChatMorphName")
                                        .executes(ctx -> WCECommandHandles.toggleChatMorph(ctx.getSource().getPlayerOrException()))
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("chatMorphName")
                                .then(Commands.literal("toggleFancyFont")
                                        .executes(ctx -> WCECommandHandles.toggleFancyFont(ctx.getSource().getPlayerOrException()))
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("info")
                                .then(Commands.literal("morphPose")
                                        .executes((command)
                                                -> WCECommandHandles.setPoseMenu(command.getSource()))
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("clan")
                                .then(Commands.literal("setNewLeader")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx ->
                                                        WCECommandHandles.setNewClanLeader(
                                                                ctx.getSource(),
                                                                EntityArgument.getPlayer(ctx, "player")
                                                        )
                                                )
                                        )
                                )
                        )

        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("resync-shapes")
                                .executes((command)
                                                -> WCECommandHandles.resyncShapes(command.getSource()
                                        )
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("info")
                                .then(Commands.literal("reset")
                                        .executes((command)
                                                -> WCECommandHandles.dataReset(command.getSource()))
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("clan")
                                .then(Commands.literal("register")
                                        .executes((command)
                                                -> WCECommandHandles.clanRegisterMenu(command.getSource()))
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("summon").requires(
                                        source -> source.hasPermission(3) || (source.getEntity() instanceof ServerPlayer player && player.isCreative()))
                                .executes((command)
                                        -> WCECommandHandles.summonMenu(command.getSource()))
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("info")
                                .then(Commands.literal("createMorph")
                                        .executes((command)
                                                -> WCECommandHandles.createMorphMenu(command.getSource()))
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("clan")
                                .then(Commands.literal("adminForceDelete").requires(source -> source.hasPermission(3))
                                        .then(Commands.argument("clan", StringArgumentType.word())
                                                .suggests(CLAN_SUGGESTIONS)
                                                .executes(ctx ->
                                                        WCECommandHandles.adminForceDelete(
                                                                ctx.getSource(),
                                                                StringArgumentType.getString(ctx, "clan")
                                                        )
                                                )
                                        )
                                )
                        )

        );


        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("clan")
                                .then(Commands.literal("adminInvite")
                                        .requires(source -> source.hasPermission(3))
                                        .then(Commands.argument("clan", StringArgumentType.word())
                                                .suggests(CLAN_SUGGESTIONS)
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(ctx ->
                                                                WCECommandHandles.adminInvite(
                                                                        ctx.getSource(),
                                                                        StringArgumentType.getString(ctx, "clan"),
                                                                        EntityArgument.getPlayer(ctx, "player")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("mate")
                                .then(Commands.literal("accept")
                                        .executes(ctx -> WCECommandHandles.mateAccept(ctx.getSource()))
                                )
                                .then(Commands.literal("decline")
                                        .executes(ctx -> WCECommandHandles.mateDecline(ctx.getSource()))
                                )
                                .then(Commands.literal("divorce")
                                        .executes(ctx -> WCECommandHandles.mateDivorce(ctx.getSource()))
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("clan")
                                .then(Commands.literal("manage")
                                        .executes(ctx -> WCECommandHandles.manageClan(ctx.getSource(), ctx.getSource().getPlayerOrException()))
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("clan")
                                .then(Commands.literal("leave")
                                        .executes(ctx -> WCECommandHandles.leaveClan(ctx.getSource()))
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("mate")
                                .then(Commands.literal("kits")
                                        .then(Commands.literal("accept")
                                                .executes(ctx -> WCECommandHandles.kitRequestAccept(ctx.getSource()))
                                        )
                                        .then(Commands.literal("decline")
                                                .executes(ctx -> WCECommandHandles.kitRequestDeny(ctx.getSource()))
                                        )
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("clan")
                                .then(Commands.literal("kick")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx ->
                                                        WCECommandHandles.kickClanMember(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"))
                                                )
                                        )
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("clan")
                                .then(Commands.literal("invite")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx ->
                                                        WCECommandHandles.invitePlayerToClan(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"))
                                                )
                                        )
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("info")
                                .then(Commands.literal("setup")
                                        .executes((command)
                                                -> WCECommandHandles.infoSetup(command.getSource()))
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("info")
                                .then(Commands.literal("profile")
                                        .executes((command)
                                                -> WCECommandHandles.infoProfile(command.getSource()))
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("info")
                                .then(Commands.literal("get")
                                        .executes(ctx -> WCECommandHandles.getProfileData(ctx.getSource(), ctx.getSource().getPlayerOrException()))
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx ->
                                                        WCECommandHandles.getProfileData(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"))))
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("clan")
                                .then(Commands.literal("disband")
                                        .executes(ctx -> WCECommandHandles.clanDisband(ctx.getSource(), ctx.getSource().getPlayerOrException()))
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("disease")
                                .requires(source -> source.hasPermission(3))
                                .then(Commands.literal("give")
                                        .then(Commands.argument("entity", EntityArgument.entities())
                                                .then(Commands.argument("disease", StringArgumentType.greedyString())
                                                        .suggests(DISEASES)

                                                        .executes(ctx -> {

                                                            Collection<? extends Entity> entities =
                                                                    EntityArgument.getEntities(ctx, "entity");

                                                            String diseaseID =
                                                                    StringArgumentType.getString(ctx, "disease");

                                                            return WCECommandHandles.diseaseGive(ctx.getSource(), entities, diseaseID);
                                                        })
                                                )
                                        )
                                )

                                .then(Commands.literal("remove")
                                        .then(Commands.argument("entity", EntityArgument.entities())
                                                .then(Commands.argument("disease", StringArgumentType.greedyString())
                                                        .suggests(DISEASES)
                                                        .executes(ctx -> {
                                                            Collection<? extends Entity> entities =
                                                                    EntityArgument.getEntities(ctx, "entity");

                                                            String diseaseID =
                                                                    StringArgumentType.getString(ctx, "disease");

                                                            return WCECommandHandles.diseaseRemove(ctx.getSource(), entities, diseaseID);
                                                        })
                                                )
                                        )
                                )

                                .then(Commands.literal("clear")
                                        .then(Commands.argument("entity", EntityArgument.entities())
                                                .executes(ctx -> {
                                                    Collection<? extends Entity> entities =
                                                            EntityArgument.getEntities(ctx, "entity");

                                                    return WCECommandHandles.diseaseClear(ctx.getSource(), entities);
                                                })

                                        )

                                        .executes(ctx -> {
                                            Collection<Entity> entities = new ArrayList<>();
                                            entities.add(ctx.getSource().getEntity());

                                            return WCECommandHandles.diseaseClear(ctx.getSource(), entities);
                                        })
                                )
                        )
        );


        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("clan")
                                .then(Commands.literal("list")
                                        .executes(ctx -> WCECommandHandles.clanList(ctx.getSource().getPlayerOrException(), false, false))
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("clan")
                                .executes(ctx -> WCECommandHandles.clanList(ctx.getSource().getPlayerOrException(), true, false))
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("clan")
                                .then(Commands.literal("map")
                                        .executes(ctx -> WCECommandHandles.clanList(ctx.getSource().getPlayerOrException(), false, true))
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("clan")
                                .then(Commands.literal("invite")
                                        .then(Commands.literal("deny")
                                                .executes(ctx ->
                                                        WCECommandHandles.clanInviteDeny(ctx.getSource())
                                                )
                                        )
                                        .then(Commands.literal("accept")
                                                .executes(ctx ->
                                                        WCECommandHandles.clanInviteAccept(ctx.getSource())
                                                )
                                        )
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("clan")
                                .then(Commands.literal("changerole")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .then(Commands.argument("role", StringArgumentType.word())
                                                        .suggests(RANK_SUGGESTIONS)
                                                        .executes(ctx ->
                                                                WCECommandHandles.changeMemberRank(
                                                                        ctx.getSource(),
                                                                        EntityArgument.getPlayer(ctx, "player"),
                                                                        StringArgumentType.getString(ctx, "role")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("clan")
                                .then(Commands.literal("changeperms")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .then(Commands.argument("perms", StringArgumentType.word())
                                                        .suggests(PERMS_SUGGESTIONS)
                                                        .executes(ctx ->
                                                                WCECommandHandles.changeMemberPermissions(
                                                                        ctx.getSource(),
                                                                        EntityArgument.getPlayer(ctx, "player"),
                                                                        StringArgumentType.getString(ctx, "perms")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("changelog")
                                .executes((command)
                                        -> WCECommandHandles.openChangelogScreen(command.getSource()))
                        )
        );

        dispatcher.register(
                Commands.literal("wce")
                        .then(Commands.literal("carryRequest")
                                .then(Commands.literal("deny")
                                        .executes(ctx ->
                                                WCECommandHandles.carryRequestDeny(ctx.getSource())
                                        )
                                )
                                .then(Commands.literal("accept")
                                        .executes(ctx ->
                                                WCECommandHandles.carryRequestAccept(ctx.getSource())
                                        )
                                )
                        )
        );

    }
}
