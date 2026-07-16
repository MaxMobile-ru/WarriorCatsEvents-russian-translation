package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataUtils;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.S2CSyncClanDataPacket;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.skills.PlayerSkill;
import net.snowteb.warriorcats_events.skills.PlayerSkillProvider;
import tocraft.walkers.api.PlayerShape;

import java.util.function.Supplier;

public class SaveClanDataPacket {

    private final WCEPlayerData data;

    public SaveClanDataPacket(WCEPlayerData data) {
        this.data = data;
    }

    public static SaveClanDataPacket decode(FriendlyByteBuf buf) {
        WCEPlayerData data = new WCEPlayerData();
        CompoundTag tag = buf.readNbt();
        if (tag != null) {
            data.loadNBT(tag);
        }
        return new SaveClanDataPacket(data);
    }

    public static void encode(SaveClanDataPacket packet, FriendlyByteBuf buf) {
        CompoundTag tag = new CompoundTag();
        packet.data.saveNBT(tag);
        buf.writeNbt(tag);
    }

    public static void handle(SaveClanDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            String oldMorphName = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                            .map(WCEPlayerData::getMorphName).orElse(player.getName().getString());

            player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                cap.copyFrom(packet.data);
            });

            WCatEntity shape = WCEPlayerDataUtils.createShape(player, player.level());

            PlayerShape.updateShapes(player, shape);

            player.getCapability(PlayerSkillProvider.SKILL_DATA).ifPresent(skillProvider -> {
                PlayerSkill.reviveAttributes(player, skillProvider);
            });

            player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                ModPackets.sendToPlayer(new S2CSyncClanDataPacket(cap), player);

                if (cap.getMateUUID() != null) {
                    if (!cap.getMateUUID().equals(WCatEntity.emptyUUID)) {
                        Entity entity = ((ServerLevel) player.level()).getEntity(cap.getMateUUID());
                        if (entity instanceof WCatEntity cat) {
                            cat.setMate(Component.literal(cap.getMorphName()));
                        }
                    }
                }
            });

            ClanData data = ClanData.get(player.serverLevel().getServer().overworld());

            player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                data.playerMorphNames.put(player.getUUID(), cap.getMorphName());

                WCGenetics.PackedGeneticData morphData =
                        new WCGenetics.PackedGeneticData(cap.getPlayerGenetics(),
                                cap.getPlayerGeneticalVariants(),
                                cap.getPlayerChimeraGenetics(),
                                cap.getPlayerChimeraVariants(),
                                cap.isOnGeneticalSkin(), cap.getVariantData());

                data.playerMorphData.put(player.getUUID(), morphData);

                ClanData.Clan clan = data.getClan(cap.getCurrentClanUUID());
                if (clan != null) {

                    Component message = Component.translatable("clan.profile_updated_log",
                            Component.literal(oldMorphName).withStyle(ChatFormatting.AQUA),
                            Component.literal(cap.getMorphName()).withStyle(ChatFormatting.AQUA));

                    data.registerLog(player.serverLevel().getServer().overworld(), clan.clanUUID, message);

                    if (clan.members.get(player.getUUID()) == ClanData.ClanPlayerRank.LEADER) {
                        clan.leaderName = cap.getMorphName();
                    }

                    cap.setClanName(clan.name);
                }

                data.setDirty();
            });


        });

        ctx.get().setPacketHandled(true);
    }

}
