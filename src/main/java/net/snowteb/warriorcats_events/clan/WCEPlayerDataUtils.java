package net.snowteb.warriorcats_events.clan;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;

public class WCEPlayerDataUtils {

    public static WCatEntity createShape(Player player, Level level) {
        WCatEntity cat = new WCatEntity(ModEntities.WCAT.get(), level);

        String shapeNameString = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                .map(WCEPlayerData::getMorphName)
                .orElse("undefined");

        WCEPlayerData.Age shapeAge = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                .map(WCEPlayerData::getMorphAge)
                .orElse(WCEPlayerData.Age.ADULT);

        int genderValue = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                .map(WCEPlayerData::getGenderData)
                .orElse(0);

        String genderS;
        if (genderValue == 0) {
            genderS = " ♂";
        } else if (genderValue == 1){
            genderS = " ♀";
        } else {
            genderS = "";
        }

        int age = 0;
        boolean isAppScale = false;
        boolean isBaby = false;
        float ageMoons = 12;

        if (shapeAge == WCEPlayerData.Age.KIT) {
            age = -1000;
            ageMoons = 0;
            isBaby = true;
            isAppScale = false;
        } else if (shapeAge == WCEPlayerData.Age.APPRENTICE) {
            age = -500;
            ageMoons = 6;
            isAppScale = true;
            isBaby = true;
        } else if (shapeAge == WCEPlayerData.Age.ADULT) {
            age = 0;
            ageMoons = 12;
            isAppScale = false;
            isBaby = false;
        }
        cat.getEntityData().set(WCatEntity.AGE_SYNC, ageMoons);

        Component name = Component.literal(shapeNameString + genderS);

        cat.setCustomName(name);
        cat.setCustomNameVisible(true);
        cat.setShowMorphName(true);

        cat.setAge(age);
        cat.setBaby(isBaby);
        cat.setAppScale(isAppScale);

        player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA).ifPresent(cap -> {
            cat.setOnGeneticalSkin(cap.isOnGeneticalSkin());

            if (cap.isOnGeneticalSkin()) {
                cat.setGender(1);

                cat.getGeneticsModule().setGenetics(cap.getPlayerGenetics());
                cat.getGeneticsModule().setGeneticalVariants(cap.getPlayerGeneticalVariants());

                cat.getGeneticsModule().setChimeraGenetics(cap.getPlayerChimeraGenetics());
                cat.getGeneticsModule().setGeneticalVariantsChimera(cap.getPlayerChimeraVariants());

            } else {
                cat.getGeneticsModule().setNonGeneticalValues(cap.getPlayerGenetics(), cap.getPlayerGeneticalVariants().size);
                cat.setVariant(cap.getVariantData());
            }

            ClanData data = ClanData.get(((ServerLevel) level).getServer().overworld());
            data.playerMorphData.put(player.getUUID(), new WCGenetics.PackedGeneticData(cap.getPlayerGenetics(),
                    cap.getPlayerGeneticalVariants(), cap.getPlayerChimeraGenetics(),
                    cap.getPlayerChimeraVariants(), cap.isOnGeneticalSkin(), cap.getVariantData()));
            data.setDirty();

            cat.setIdlePose(cap.getIdlePose());

        });

        cat.setPlayerBoundUuid(player.getUUID());


        return cat;
    }

}
