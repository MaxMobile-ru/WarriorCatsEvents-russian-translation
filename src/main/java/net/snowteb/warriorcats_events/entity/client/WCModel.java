package net.snowteb.warriorcats_events.entity.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.compat.CompatibilitiesClient;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class WCModel extends GeoModel<WCatEntity> {

    static final String[] RESOURCE_IGNORE_LIST = {
            "genetics",
            "wcskintemplate",
            "empty",
    };

    public static ResourceLocation[] getTextures() {
        ArrayList<ResourceLocation> textures = new ArrayList<>(Arrays.asList(BUILT_IN_TEXTURES));
        ArrayList<ResourceLocation> packTextures = new ArrayList<>();

        final Collection<Pack> RESOURCE_PACKS = Minecraft.getInstance().getResourcePackRepository().getSelectedPacks();

        String[] builtInTexturePaths = new String[BUILT_IN_TEXTURES.length];
        for (int i = 0; i < BUILT_IN_TEXTURES.length; i++) {
            builtInTexturePaths[i] = BUILT_IN_TEXTURES[i].getPath();
        }

        for (Pack resourcePack : RESOURCE_PACKS) {
            resourcePack.open().listResources(PackType.CLIENT_RESOURCES, WarriorCatsEvents.MODID, "textures/entity/wcat", (a, b) -> {
                for (String s : builtInTexturePaths) {
                    if (a.getPath().equals(s)) return;
                }
                for (String s : RESOURCE_IGNORE_LIST) {
                    if (a.getPath().contains(s)) return;
                }
                textures.add(a);
                packTextures.add(a);
            });
        }

        PACK_TEXTURES = packTextures.toArray(new ResourceLocation[packTextures.size()]);

        return textures.toArray(new ResourceLocation[textures.size()]);
    }

    public static final ResourceLocation[] BUILT_IN_TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin1.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin2.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin3.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin4.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin5.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin6.png"), // 5
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin7.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin8.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin9.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin10.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin11.png"), //10
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin12.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/chestnutpatch.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/ratstar.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/twitchstream.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/blazepit.png"), // 15
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/bengalpelt.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/sparrowstar.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/foxeater.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/willowsong.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin13.png"), //20
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin14.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin15.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin16.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin17.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin18.png"), //25
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin19.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin20.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin21.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/wcskin22.png"),

            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/albino.png"), //30
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/bengal.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/brindle_tortie.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/brown_cream_calico.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/brown_cream_calico2.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/brown_cream_calico3.png"), //35
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/caramel.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/frostdawn.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/gray_white_tabby.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/hailflake.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/karpati.png"), //40
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/leafstar.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/longtail.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/mothpaw.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/redtail.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/salem.png"), // 45
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/short_hair.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/stoneflare.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/tortie_point.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/turtleheart.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/violetdew.png"), //50
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/patch.png"),
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/entity/wcat/parlee.png"), //52
    };

    public static ResourceLocation[] TEXTURES = BUILT_IN_TEXTURES;


    public static ResourceLocation[] PACK_TEXTURES = new ResourceLocation[]{};

    @Override
    public ResourceLocation getModelResource(WCatEntity object) {
        return ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "geo/wcat.geo.json");
    }

    /**
     * Depending on the variant, grab a texture from the list.
     */
    @Override
    public ResourceLocation getTextureResource(WCatEntity object) {
        return TEXTURES[object.getVariant()];
    }

    @Override
    public ResourceLocation getAnimationResource(WCatEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "animations/wcat.animation.json");
    }

    private static float tail2InitialRot = -213;

    @Override
    public void setCustomAnimations(WCatEntity animatable, long instanceId, AnimationState<WCatEntity> animationState) {
        GeoBone head = animatable.isAnImage() ? null : getAnimationProcessor().getBone("mainHead");

        boolean hasChestFur = (WCGenetics.FurGene.isLongFur(animatable.getGeneticsModule().getGenetics().chestFur));
        boolean hasBellyFur = (WCGenetics.FurGene.isLongFur(animatable.getGeneticsModule().getGenetics().bellyFur));
        boolean hasLegsFur = (WCGenetics.FurGene.isLongFur(animatable.getGeneticsModule().getGenetics().legsFur));
        boolean hasHeadFur = (WCGenetics.FurGene.isLongFur(animatable.getGeneticsModule().getGenetics().headFur));
        boolean hasCheekFur = (WCGenetics.FurGene.isLongFur(animatable.getGeneticsModule().getGenetics().cheekFur));
        boolean hasBackFur = (WCGenetics.FurGene.isLongFur(animatable.getGeneticsModule().getGenetics().backFur));
        boolean hasTailFur = (WCGenetics.FurGene.isLongFur(animatable.getGeneticsModule().getGenetics().tailFur));
        boolean isBobtail = (WCGenetics.Bobtail.isBobtail(animatable.getGeneticsModule().getGenetics().bobtail));

        {
            getBone("chest_fur").ifPresent(bone -> bone.setHidden(!hasChestFur));
            getBone("belly_fur").ifPresent(bone -> bone.setHidden(!hasBellyFur));

            getBone("front_right_fur2").ifPresent(bone -> bone.setHidden(!hasLegsFur));
            getBone("front_right_fur").ifPresent(bone -> bone.setHidden(!hasLegsFur));

            getBone("head_tuft").ifPresent(bone -> bone.setHidden(!hasHeadFur));
            getBone("face_fur").ifPresent(bone -> bone.setHidden(!hasCheekFur));
            getBone("back_fur").ifPresent(bone -> bone.setHidden(!hasBackFur));

            getBone("tailsub").ifPresent(bone -> bone.setHidden(isBobtail));
            getBone("tail2").ifPresent(bone -> bone.setHidden(isBobtail));

            getBone("tail_fur").ifPresent(bone -> bone.setHidden(!hasTailFur));
            getBone("tail_fur2").ifPresent(bone -> bone.setHidden(!hasTailFur));
            getBone("tail_fur3").ifPresent(bone -> bone.setHidden(!hasTailFur));

        }


        {

            if (head != null) {
                float scale = 1.0f + (0.3f*((12 - animatable.getAgeInMoons())/12));
                float yPos = 0.0f + (2f*((12 - animatable.getAgeInMoons())/12));
                float zPos = 0.0f + (2f*((12 - animatable.getAgeInMoons())/12));
                head.setScaleX(scale);
                head.setScaleY(scale);
                head.setScaleZ(scale);
                head.setPosY(yPos);
                head.setPosZ(zPos);

                if (getAnimationProcessor().getBone("tail2") != null) {
                    GeoBone bone = getAnimationProcessor().getBone("tail2");
                    if (tail2InitialRot == -213) {
                        tail2InitialRot = bone.getRotX();
                    }
                    float rot = tail2InitialRot - (0.4f*((12 - animatable.getAgeInMoons())/12));
                    bone.setRotX(rot);
                }

                EntityModelData entityModelData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
                if (!animatable.isAnImage()) {
                    head.setRotX(entityModelData.headPitch() * Mth.DEG_TO_RAD);
                    head.setRotY(entityModelData.netHeadYaw() * Mth.DEG_TO_RAD);
                }
            }

            if (head != null && animatable.isAnImage()) {
                head.setRotX(0);
                head.setRotY(0);
                head.setRotZ(0);
            }
        }


    }
}
