package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.CapabilityManager;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.attachments.WCEPlayerData;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.event.ModEventsForge1;
import net.snowteb.warriorcats_events.integration.WCatTypeProvider;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.S2CSyncClanDataPacket;
import tocraft.walkers.api.PlayerShape;

public class UpdateClanDataPacket implements CustomPacketPayload {

    private final int data;

    public UpdateClanDataPacket(int data) {
        this.data = data;
    }

    public static UpdateClanDataPacket decode(FriendlyByteBuf buf) {
        int data = buf.readInt();
        return new UpdateClanDataPacket(data);
    }

    public static void encode(UpdateClanDataPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.data);
    }

    public static void handle(UpdateClanDataPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            if (packet.data == 0) {
                CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_WCE_DATA, cap -> {
                    cap.setMorphAge(WCEPlayerData.Age.APPRENTICE);
                    cap.setMorphName(cap.getPrefix() + "paw");
                    ModPackets.sendToPlayer(new S2CSyncClanDataPacket(cap), player);
                });
            }
            if (packet.data == 1) {
                CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_WCE_DATA, cap -> {
                    cap.setMorphAge(WCEPlayerData.Age.ADULT);
                    cap.setMorphName(cap.getPrefix() + cap.getSufix());
                    ModPackets.sendToPlayer(new S2CSyncClanDataPacket(cap), player);
                });
            }


            int variantData = player.getData(ModAttachments.PLAYER_WCE_DATA).getVariantData();


            ModEventsForge1.schedule(10, () -> {
                WCatEntity shape = new WCatTypeProvider().create(ModEntities.WCAT.get(), player.level(), variantData, player);
                PlayerShape.updateShapes(player, shape);
                ((ServerLevel) player.level()).sendParticles(ParticleTypes.ENCHANT,
                        player.getX(), player.getY() + 1.5, player.getZ(),
                        400, 0f, 0f, 0f, 4.0f);
                ((ServerLevel) player.level()).sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        player.getX(), player.getY() + 0.5, player.getZ(),
                        10, 0.4f, 0.4f, 0.4f, 0.3f);

                        player.level().playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.7f, 1.2f);

                String playerMorphName = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphName();

                String playerMorphPrefix = player.getData(ModAttachments.PLAYER_WCE_DATA).getPrefix();

                String playerMorphClan = player.getData(ModAttachments.PLAYER_WCE_DATA).getClanName();

                Boolean usesSufix = player.getData(ModAttachments.PLAYER_WCE_DATA).isUseSufixes();

                WCEPlayerData.Age playerMorphAge = player.getData(ModAttachments.PLAYER_WCE_DATA).getMorphAge();

                String lastSufix = "";
                switch (playerMorphAge) {
                    case APPRENTICE -> lastSufix = "kit";
                    case ADULT ->  lastSufix = "paw";
                }

                if (usesSufix) {
                    player.getServer().getPlayerList().broadcastSystemMessage(
                            Component.translatable("clan.player_has_grown_sufix",
                                    Component.literal(playerMorphPrefix + lastSufix).withStyle(ChatFormatting.GOLD),
                                    Component.literal(playerMorphClan).withStyle(ChatFormatting.AQUA),
                                    Component.literal(playerMorphName).withStyle(ChatFormatting.GOLD))
                            , false
                    );
                } else {
                    player.getServer().getPlayerList().broadcastSystemMessage(
                            Component.translatable("clan.player_has_grown_no_sufix",
                                    Component.literal(playerMorphName).withStyle(ChatFormatting.GOLD),
                                    Component.literal(playerMorphClan).withStyle(ChatFormatting.AQUA))
                            , false
                    );
                }

                CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_WCE_DATA, cap -> {
                    if (cap.getMateUUID() != null) {
                        if (!cap.getMateUUID().equals(WCatEntity.emptyUUID)) {
                            Entity entity = ((ServerLevel) player.level()).getEntity(cap.getMateUUID());
                            if (entity instanceof WCatEntity cat) {
                                cat.setMate(Component.literal(cap.getMorphName()));
                            }
                        }
                    }
                });

            });

            ClanData data = ClanData.get(player.serverLevel());

            CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_WCE_DATA, cap -> {
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

        });

    }

    public static final Type<UpdateClanDataPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "update_clan_data"));

    public static final StreamCodec<FriendlyByteBuf, UpdateClanDataPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
