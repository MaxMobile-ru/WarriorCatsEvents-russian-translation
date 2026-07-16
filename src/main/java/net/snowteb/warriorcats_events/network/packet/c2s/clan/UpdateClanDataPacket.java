package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.event.ModEventsForge1;
import net.snowteb.warriorcats_events.integration.WCatTypeProvider;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.S2CSyncClanDataPacket;
import tocraft.walkers.api.PlayerShape;

import java.util.function.Supplier;

public class UpdateClanDataPacket {

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

    public static void handle(UpdateClanDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            if (packet.data == 0) {
                player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                    cap.setMorphAge(WCEPlayerData.Age.APPRENTICE);
                    cap.setMorphName(cap.getPrefix() + "paw");
                    ModPackets.sendToPlayer(new S2CSyncClanDataPacket(cap), player);
                });
            }
            if (packet.data == 1) {
                player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                    cap.setMorphAge(WCEPlayerData.Age.ADULT);
                    cap.setMorphName(cap.getPrefix() + cap.getSufix());
                    ModPackets.sendToPlayer(new S2CSyncClanDataPacket(cap), player);
                });
            }


            int variantData = player
                    .getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                    .map(cap -> cap.getVariantData())
                    .orElse(0);



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
                String playerMorphName = player
                        .getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(cap -> cap.getMorphName())
                        .orElse("<player>");
                String playerMorphPrefix = player
                        .getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(cap -> cap.getPrefix())
                        .orElse("<player>");
                String playerMorphClan = player
                        .getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(cap -> cap.getClanName(player.serverLevel()))
                        .orElse("<clan>");
                Boolean usesSufix = player
                        .getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(cap -> cap.isUseSufixes())
                        .orElse(false);

                WCEPlayerData.Age playerMorphAge = player
                        .getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                        .map(cap -> cap.getMorphAge())
                        .orElse(WCEPlayerData.Age.ADULT);

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

                player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
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

            player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
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

        ctx.get().setPacketHandled(true);
    }
}
