package net.snowteb.warriorcats_events.network.packet.c2s.cats;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.sound.ModSounds;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CommandCatsPacket {

    public static List<Integer> specificEntityIds = List.of();
    public static List<Integer> allEntityIds = List.of();
    private final String command1;
    private final String command2;

    public CommandCatsPacket(List<Integer> allEntityIds, List<Integer> specificEntityIds, String allOrSome, String modeKey) {
        this.allEntityIds = allEntityIds;
        this.specificEntityIds = specificEntityIds;
        this.command1 = allOrSome;
        this.command2 = modeKey;
    }

    public static void encode(CommandCatsPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.allEntityIds.size());
        for (int id : msg.allEntityIds) buf.writeVarInt(id);

        buf.writeVarInt(msg.specificEntityIds.size());
        for (int id : msg.specificEntityIds) buf.writeVarInt(id);

        buf.writeUtf(msg.command1);
        buf.writeUtf(msg.command2);
    }

    public static CommandCatsPacket decode(FriendlyByteBuf buf) {
        int size1 = buf.readVarInt();
        List<Integer> idsAll = new ArrayList<>();
        for (int i = 0; i < size1; i++) idsAll.add(buf.readVarInt());

        int size2 = buf.readVarInt();
        List<Integer> idsSpec = new ArrayList<>();
        for (int i = 0; i < size2; i++) idsSpec.add(buf.readVarInt());

        String command1 = buf.readUtf();
        String command2 = buf.readUtf();
        return new CommandCatsPacket(idsAll, idsSpec, command1, command2);
    }

    public static void handle(CommandCatsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = player.serverLevel();

            setCatsMode(msg.command1, msg.command2, level, player);

        });

        ctx.get().setPacketHandled(true);
    }

    public static void setCatsMode(String keyAmmount, String keyMode, ServerLevel level, ServerPlayer player) {
        int catsAffected = 0;
        WCatEntity.CatMode mode = switch (keyMode) {
            case "stay" -> WCatEntity.CatMode.SIT;
            case "follow" -> WCatEntity.CatMode.FOLLOW;
            case "wander" -> WCatEntity.CatMode.WANDER;
            default -> WCatEntity.CatMode.WANDER;
        };

        float pitch = 0.9f;
        WCEPlayerData.Age morphAge = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                .map(WCEPlayerData::getMorphAge).orElse(WCEPlayerData.Age.ADULT);
        switch (morphAge) {
            case KIT -> pitch = 1.3f;
            case APPRENTICE -> pitch = 1.1f;
            case ADULT -> pitch = 0.9f;
        }

        if (keyAmmount.equals("all")) {
            List<WCatEntity> cats = allEntityIds.stream()
                    .map(level::getEntity)
                    .filter(e -> e instanceof WCatEntity)
                    .map(e -> (WCatEntity) e)
                    .toList();

            if (cats.isEmpty() && mode != WCatEntity.CatMode.WANDER) {
                player.displayClientMessage(Component.translatable("generic.no_clanmates_in_range").withStyle(ChatFormatting.GRAY), true);
                return;
            }

            for (WCatEntity cat : cats) {
                if (mode == WCatEntity.CatMode.WANDER) {
                    cat.mode = mode;
                    cat.leaderCallingToSitFlag = false;
                    cat.leaderCallingToFollowFlag = false;
                    cat.wanderCenter = cat.blockPosition();
                }
                if (mode == WCatEntity.CatMode.SIT) {
                    cat.leaderCallingToSitFlag = true;
                }
                if (mode == WCatEntity.CatMode.FOLLOW) {
                    cat.leaderCallingToFollowFlag = true;
                }

                catsAffected++;

                if (cats.size() == 1) {
                    cat.sendModeMessage(player);
                    return;
                }
            }

        } else if (keyAmmount.equals("specific")) {
            List<WCatEntity> cats = specificEntityIds.stream()
                    .map(level::getEntity)
                    .filter(e -> e instanceof WCatEntity)
                    .map(e -> (WCatEntity) e)
                    .toList();
            if (cats.isEmpty() && mode != WCatEntity.CatMode.WANDER) {
                player.displayClientMessage(Component.translatable("generic.no_clanmates_in_range").withStyle(ChatFormatting.GRAY), true);
                return;
            }



            for (WCatEntity cat : cats) {
                if (mode == WCatEntity.CatMode.WANDER) {
                    cat.mode = mode;
                    cat.leaderCallingToSitFlag = false;
                    cat.leaderCallingToFollowFlag = false;
                    cat.wanderCenter = cat.blockPosition();
                }
                if (mode == WCatEntity.CatMode.SIT) {
                    cat.leaderCallingToSitFlag = true;
                }
                if (mode == WCatEntity.CatMode.FOLLOW) {
                    cat.leaderCallingToFollowFlag = true;
                }

                catsAffected++;

                if (cats.size() == 1) {
                    cat.sendModeMessage(player, mode);
                    if (mode == WCatEntity.CatMode.WANDER) {
                        level.playSound(null, player.blockPosition(), SoundEvents.CAT_STRAY_AMBIENT, SoundSource.PLAYERS, 1F, pitch);
                    } else {
                        level.playSound(null, player.blockPosition(), ModSounds.LEADER_CALL.get(), SoundSource.PLAYERS, 0.7F, pitch);
                    }
                    return;
                }
            }

        }

        if (mode == WCatEntity.CatMode.WANDER) {
            level.playSound(null, player.blockPosition(), SoundEvents.CAT_STRAY_AMBIENT, SoundSource.PLAYERS, 1F, pitch);
        } else {
            level.playSound(null, player.blockPosition(), ModSounds.LEADER_CALL.get(), SoundSource.PLAYERS, 0.8F, pitch);
        }

        if (!player.isCreative() && mode != WCatEntity.CatMode.WANDER) {
            ItemStack iteminHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (iteminHand.is(ModItems.ANCIENT_STICK.get())) {
                iteminHand.hurt(catsAffected, player.getRandom(), player);
                player.getCooldowns().addCooldown(iteminHand.getItem(), 20 * 8);
            }
        }

        player.displayClientMessage(
                Component.translatable("generic.cats_set_to",
                        Component.literal(String.valueOf(catsAffected)).withStyle(ChatFormatting.GOLD),
                        Component.literal(keyMode).withStyle(ChatFormatting.GREEN))
                ,true
        );

    }
}

