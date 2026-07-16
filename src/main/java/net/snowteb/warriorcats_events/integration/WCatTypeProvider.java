package net.snowteb.warriorcats_events.integration;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataUtils;
import net.snowteb.warriorcats_events.diseases.DiseaseManager;
import net.snowteb.warriorcats_events.entity.client.WCModel;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
import tocraft.walkers.api.variant.TypeProvider;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;

/**
 * Provides all the available variants of the wild cat.
 */
public class WCatTypeProvider extends TypeProvider<WCatEntity> {

    @Override
    public int getVariantData(WCatEntity wCatEntity) {
        return wCatEntity.getVariant();
    }

    @Override
    public WCatEntity create(EntityType<WCatEntity> type, Level world, int data) {
        WCatEntity cat = new WCatEntity(type, world);
        cat.setVariant(data);
        return cat;
    }

    @Override
    public WCatEntity create(EntityType<WCatEntity> type, Level level, int data, Player player) {

        if (player instanceof ServerPlayer serverPlayer) {
            ClanData clanData = ClanData.get(serverPlayer.serverLevel().getServer().overworld());

            player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
                cap.setVariantData(data);

                WCGenetics.PackedGeneticData morphData =
                        new WCGenetics.PackedGeneticData(cap.getPlayerGenetics(),
                                cap.getPlayerGeneticalVariants(),
                                cap.getPlayerChimeraGenetics(),
                                cap.getPlayerChimeraVariants(),
                                cap.isOnGeneticalSkin(), cap.getVariantData());

                clanData.playerMorphData.put(player.getUUID(), morphData);
            });

            clanData.setDirty();

            DiseaseManager.refreshData(serverPlayer);
        }

        return WCEPlayerDataUtils.createShape(player, player.level());
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return WCModel.TEXTURES.length - 1;
    }

    @Override
    public Component modifyText(WCatEntity wCatEntity, MutableComponent mutableComponent) {
        return mutableComponent.append("Variant " + wCatEntity.getVariant());
    }

    
}
