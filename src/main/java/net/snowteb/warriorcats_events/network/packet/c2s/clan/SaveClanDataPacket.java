package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.*;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.S2CSyncClanDataPacket;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import tocraft.walkers.api.PlayerShape;

public class SaveClanDataPacket implements CustomPacketPayload {

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

    public static void handle(SaveClanDataPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            String oldMorphName = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

            CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_WCE_DATA, cap -> {
                cap.copyFrom(packet.data);
            });

            WCatEntity shape = WCEPlayerDataUtils.createShape(player, player.level());

            PlayerShape.updateShapes(player, shape);

            CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_SKILL, cap -> {
                PlayerSkill.reviveAttributes(player, cap);
            });

            CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_WCE_DATA, cap -> {
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

            CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_WCE_DATA, cap -> {
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

    }

    public static final Type<SaveClanDataPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "save_clan_data"));

    public static final StreamCodec<FriendlyByteBuf, SaveClanDataPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
