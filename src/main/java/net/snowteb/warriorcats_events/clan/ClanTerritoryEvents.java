package net.snowteb.warriorcats_events.clan;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;

import static net.snowteb.warriorcats_events.clan.ClanData.getOwnerOfTerritory;

@Mod.EventBusSubscriber(modid = WarriorCatsEvents.MODID)
public class ClanTerritoryEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        if (!WCEServerConfig.SERVER.ENFORCE_TERRITORIES.get()) return;

        if (!WCEServerConfig.SERVER.PROTECT_PLACE_AND_BREAK_BLOCKS.get()) return;

        if (player.isCreative()) return;

        if (player.hasPermissions(2)) return;

        ChunkPos pos = new ChunkPos(event.getPos());
        ClanData.Clan ownerClan = getOwnerOfTerritory(player, pos);
        if (ownerClan != null && ownerClan.protectTerritory) {
            player.displayClientMessage(Component.translatable("clan.cannot_break_blocks").withStyle(ChatFormatting.RED), true);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!WCEServerConfig.SERVER.ENFORCE_TERRITORIES.get()) return;

        if (!WCEServerConfig.SERVER.PROTECT_PLACE_AND_BREAK_BLOCKS.get()) return;

        if (player.isCreative()) return;

        if (player.hasPermissions(2)) return;

        ChunkPos pos = new ChunkPos(event.getPos());
        ClanData.Clan ownerClan = getOwnerOfTerritory(player, pos);
        if (ownerClan != null && ownerClan.protectTerritory) {
            player.displayClientMessage(Component.translatable("clan.cannot_place_blocks").withStyle(ChatFormatting.RED), true);

            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!WCEServerConfig.SERVER.ENFORCE_TERRITORIES.get()) return;

        if (!WCEServerConfig.SERVER.PROTECT_CONTAINERS.get()) return;

        if (player.hasPermissions(2)) return;

        ChunkPos pos = new ChunkPos(event.getPos());
        ClanData.Clan ownerClan = ClanData.getOwnerOfTerritory(player, pos);
        if (ownerClan != null) {
            if (player.serverLevel().getBlockEntity(event.getPos()) != null && ownerClan.protectContainers) {
                player.displayClientMessage(Component.translatable("clan.cannot_open_containers").withStyle(ChatFormatting.RED), true);
                event.setCanceled(true);
            } else if (!event.getItemStack().isEmpty() && ownerClan.protectTerritory) {
                player.displayClientMessage(Component.translatable("clan.cannot_place_blocks").withStyle(ChatFormatting.RED), true);
                event.setCanceled(true);
            }

        }
    }

}
