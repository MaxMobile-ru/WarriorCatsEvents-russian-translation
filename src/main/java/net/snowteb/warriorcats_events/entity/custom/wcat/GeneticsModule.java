package net.snowteb.warriorcats_events.entity.custom.wcat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.snowteb.warriorcats_events.entity.client.WCRenderer;
import net.snowteb.warriorcats_events.util.GeneticsForVariant;

public class GeneticsModule {
    private final WCatEntity cat;
    
    public GeneticsModule(WCatEntity cat) {
        this.cat = cat;
    }

    public WCGenetics getGenetics() {
        WCGenetics genetics = new WCGenetics();

        genetics.chestFur = (cat.getEntityData().get(WCatEntity.CHEST_FUR));
        genetics.bellyFur = (cat.getEntityData().get(WCatEntity.BELLY_FUR));
        genetics.legsFur = (cat.getEntityData().get(WCatEntity.LEGS_FUR));
        genetics.headFur = (cat.getEntityData().get(WCatEntity.HEAD_FUR));
        genetics.cheekFur = (cat.getEntityData().get(WCatEntity.CHEEK_FUR));
        genetics.backFur = (cat.getEntityData().get(WCatEntity.BACK_FUR));
        genetics.bobtail = (cat.getEntityData().get(WCatEntity.BOBTAIL));
        genetics.tailFur = (cat.getEntityData().get(WCatEntity.TAIL_FUR));

        genetics.base = (cat.getEntityData().get(WCatEntity.BASE));
        genetics.orangeBase = (cat.getEntityData().get(WCatEntity.ORANGE_BASE));
        genetics.whiteRatio = (cat.getEntityData().get(WCatEntity.WHITE_RATIO));
        genetics.albino = (cat.getEntityData().get(WCatEntity.ALBINO));
        genetics.dilute = (cat.getEntityData().get(WCatEntity.DILUTE));
        genetics.agouti = (cat.getEntityData().get(WCatEntity.AGOUTI));
        genetics.tabbyStripes = (cat.getEntityData().get(WCatEntity.TABBY_STRIPES));
        genetics.eyesAnomaly = (cat.getEntityData().get(WCatEntity.EYES_ANOMALY));
        genetics.silver = cat.getEntityData().get(WCatEntity.SILVER);

        return genetics;
    }

    public WCGenetics.GeneticalVariants getGenVariants() {
        WCGenetics.GeneticalVariants variants = new WCGenetics.GeneticalVariants(
                cat.getEntityData().get(WCatEntity.EYE_COLOR_LEFT),
                cat.getEntityData().get(WCatEntity.EYE_COLOR_RIGHT),
                cat.getEntityData().get(WCatEntity.RUFOUSING_VARIANT),
                cat.getEntityData().get(WCatEntity.BLUE_RUFOUSING_VARIANT),
                cat.getEntityData().get(WCatEntity.ORANGE_BASE_VARIANT),
                cat.getEntityData().get(WCatEntity.WHITE_RATIO_VARIANT),
                cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT),
                cat.getEntityData().get(WCatEntity.ALBINO_VARIANT),
                cat.getEntityData().get(WCatEntity.EYE_COLOR_VARIANT_LEFT),
                cat.getEntityData().get(WCatEntity.EYE_COLOR_VARIANT_RIGHT),
                cat.getEntityData().get(WCatEntity.NOISE),
                cat.getEntityData().get(WCatEntity.SIZE),
                cat.getEntityData().get(WCatEntity.SILVER_VARIANT),
                cat.getEntityData().get(WCatEntity.SCARS),
                cat.getEntityData().get(WCatEntity.SKIN_COLOR)
        );
        return variants;
    }

    public WCGenetics.GeneticalChimeraVariants getChimeraGenVariants() {
        WCGenetics.GeneticalChimeraVariants variants = new WCGenetics.GeneticalChimeraVariants(
                cat.getEntityData().get(WCatEntity.CHIMERA_VARIANT),
                cat.getEntityData().get(WCatEntity.RUFOUSING_VARIANT_CHIMERA),
                cat.getEntityData().get(WCatEntity.BLUE_RUFOUSING_VARIANT_CHIMERA),
                cat.getEntityData().get(WCatEntity.ORANGE_BASE_VARIANT_CHIMERA),
                cat.getEntityData().get(WCatEntity.WHITE_RATIO_VARIANT_CHIMERA),
                cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA),
                cat.getEntityData().get(WCatEntity.ALBINO_VARIANT_CHIMERA),
                cat.getEntityData().get(WCatEntity.NOISE_CHIMERA),
                cat.getEntityData().get(WCatEntity.SILVER_VARIANT_CHIMERA)
        );
        return variants;
    }


    public WCGenetics getChimeraGenetics() {
        WCGenetics genetics = new WCGenetics();

        genetics.chestFur = (cat.getEntityData().get(WCatEntity.CHEST_FUR));
        genetics.bellyFur = (cat.getEntityData().get(WCatEntity.BELLY_FUR));
        genetics.legsFur = (cat.getEntityData().get(WCatEntity.LEGS_FUR));
        genetics.headFur = (cat.getEntityData().get(WCatEntity.HEAD_FUR));
        genetics.cheekFur = (cat.getEntityData().get(WCatEntity.CHEEK_FUR));
        genetics.backFur = (cat.getEntityData().get(WCatEntity.BACK_FUR));
        genetics.bobtail = (cat.getEntityData().get(WCatEntity.BOBTAIL));
        genetics.tailFur = (cat.getEntityData().get(WCatEntity.TAIL_FUR));

        genetics.base = (cat.getEntityData().get(WCatEntity.BASE_CHIMERA));
        genetics.orangeBase = (cat.getEntityData().get(WCatEntity.ORANGE_BASE_CHIMERA));
        genetics.whiteRatio = (cat.getEntityData().get(WCatEntity.WHITE_RATIO_CHIMERA));
        genetics.albino = (cat.getEntityData().get(WCatEntity.ALBINO_CHIMERA));
        genetics.dilute = (cat.getEntityData().get(WCatEntity.DILUTE_CHIMERA));
        genetics.agouti = (cat.getEntityData().get(WCatEntity.AGOUTI_CHIMERA));
        genetics.tabbyStripes = (cat.getEntityData().get(WCatEntity.TABBY_STRIPES_CHIMERA));
        genetics.eyesAnomaly = (cat.getEntityData().get(WCatEntity.EYES_ANOMALY));
        genetics.chimeraGene = cat.getEntityData().get(WCatEntity.CHIMERA_GENE);
        genetics.silver = cat.getEntityData().get(WCatEntity.SILVER_CHIMERA);

        return genetics;
    }

    public void setGenetics(WCGenetics genetics) {
        cat.getEntityData().set(WCatEntity.CHEST_FUR, genetics.chestFur);
        cat.getEntityData().set(WCatEntity.BELLY_FUR, genetics.bellyFur);
        cat.getEntityData().set(WCatEntity.LEGS_FUR, genetics.legsFur);
        cat.getEntityData().set(WCatEntity.HEAD_FUR, genetics.headFur);
        cat.getEntityData().set(WCatEntity.CHEEK_FUR, genetics.cheekFur);
        cat.getEntityData().set(WCatEntity.BACK_FUR, genetics.backFur);
        cat.getEntityData().set(WCatEntity.BOBTAIL, genetics.bobtail);
        cat.getEntityData().set(WCatEntity.TAIL_FUR, genetics.tailFur);

        cat.getEntityData().set(WCatEntity.BASE, genetics.base);
        cat.getEntityData().set(WCatEntity.ORANGE_BASE, genetics.orangeBase);
        cat.getEntityData().set(WCatEntity.WHITE_RATIO, genetics.whiteRatio);
        cat.getEntityData().set(WCatEntity.ALBINO, genetics.albino);
        cat.getEntityData().set(WCatEntity.DILUTE, genetics.dilute);
        cat.getEntityData().set(WCatEntity.AGOUTI, genetics.agouti);
        cat.getEntityData().set(WCatEntity.TABBY_STRIPES, genetics.tabbyStripes);
        cat.getEntityData().set(WCatEntity.EYES_ANOMALY, genetics.eyesAnomaly);

        cat.getEntityData().set(WCatEntity.SILVER, genetics.silver);
    }

    public void initializeGenetics() {
        cat.getEntityData().set(WCatEntity.CHEST_FUR, WCGenetics.FurGene.generateAlelo(cat.getRandom()) + "-" + WCGenetics.FurGene.generateAlelo(cat.getRandom()));
        cat.getEntityData().set(WCatEntity.BELLY_FUR, WCGenetics.FurGene.generateAlelo(cat.getRandom()) + "-" + WCGenetics.FurGene.generateAlelo(cat.getRandom()));
        cat.getEntityData().set(WCatEntity.LEGS_FUR, WCGenetics.FurGene.generateAlelo(cat.getRandom()) + "-" + WCGenetics.FurGene.generateAlelo(cat.getRandom()));
        cat.getEntityData().set(WCatEntity.HEAD_FUR, WCGenetics.FurGene.generateAlelo(cat.getRandom()) + "-" + WCGenetics.FurGene.generateAlelo(cat.getRandom()));
        cat.getEntityData().set(WCatEntity.CHEEK_FUR, WCGenetics.FurGene.generateAlelo(cat.getRandom()) + "-" + WCGenetics.FurGene.generateAlelo(cat.getRandom()));
        cat.getEntityData().set(WCatEntity.BACK_FUR, WCGenetics.FurGene.generateAlelo(cat.getRandom()) + "-" + WCGenetics.FurGene.generateAlelo(cat.getRandom()));
        cat.getEntityData().set(WCatEntity.BOBTAIL, WCGenetics.Bobtail.init(cat.getRandom()));
        cat.getEntityData().set(WCatEntity.TAIL_FUR, WCGenetics.FurGene.generateAlelo(cat.getRandom()) + "-" + WCGenetics.FurGene.generateAlelo(cat.getRandom()));

        cat.setOnGeneticalSkin(true);

        cat.getEntityData().set(WCatEntity.BASE, WCGenetics.Base.generateAlelo(cat.getRandom()) + "-" + WCGenetics.Base.generateAlelo(cat.getRandom()));

        cat.getEntityData().set(WCatEntity.ORANGE_BASE, WCGenetics.OrangeBase.generateAlelo(cat.getRandom()) + "-" + WCGenetics.OrangeBase.generateAlelo(cat.getRandom()));
        cat.getEntityData().set(WCatEntity.ORANGE_BASE_VARIANT, cat.getRandom().nextInt(WCGenetics.Constants.MAX_TORTIE_VARIANTS));

        String whiteRatioGene = WCGenetics.WhiteRatio.generateAlelo(cat.getRandom()) + "-" + WCGenetics.WhiteRatio.generateAlelo(cat.getRandom());
        cat.getEntityData().set(WCatEntity.WHITE_RATIO, whiteRatioGene);
        cat.getEntityData().set(WCatEntity.WHITE_RATIO_VARIANT, cat.getRandom().nextInt(WCGenetics.Constants.getWhiteVariants(whiteRatioGene)));

        cat.getEntityData().set(WCatEntity.ALBINO, WCGenetics.Albino.generateAlelo(cat.getRandom()) + "-" + WCGenetics.Albino.generateAlelo(cat.getRandom()));
        cat.getEntityData().set(WCatEntity.ALBINO_VARIANT, cat.getRandom().nextInt(WCGenetics.Constants.MAX_ALBINO_VARIANTS));

        cat.getEntityData().set(WCatEntity.DILUTE, WCGenetics.Dilute.generateAlelo(cat.getRandom()) + "-" + WCGenetics.Dilute.generateAlelo(cat.getRandom()));

        cat.getEntityData().set(WCatEntity.AGOUTI, WCGenetics.Agouti.generateAlelo(cat.getRandom()) + "-" + WCGenetics.Agouti.generateAlelo(cat.getRandom()));

        String tabbyStripesGene = WCGenetics.TabbyStripeTypes.generateAlelo(cat.getRandom()) + "-" + WCGenetics.TabbyStripeTypes.generateAlelo(cat.getRandom());
        cat.getEntityData().set(WCatEntity.TABBY_STRIPES, tabbyStripesGene);
        cat.getEntityData().set(WCatEntity.TABBY_STRIPES_VARIANT, cat.getRandom().nextInt(WCGenetics.Constants.getTabbyVariants(tabbyStripesGene)));

        cat.getEntityData().set(WCatEntity.EYES_ANOMALY, WCGenetics.EyesAnomaly.generateAlelo(cat.getRandom()) + "-" + WCGenetics.EyesAnomaly.generateAlelo(cat.getRandom()));

        cat.getEntityData().set(WCatEntity.SILVER, WCGenetics.Silver.generateAlelo(cat.getRandom()) + "-" + WCGenetics.Silver.generateAlelo(cat.getRandom()));
        cat.getEntityData().set(WCatEntity.SILVER_VARIANT, cat.getRandom().nextInt(WCGenetics.Constants.MAX_SILVER_VARIANTS));

        String leftEyeColor = WCGenetics.EyeColor.generateAlelo(cat.getRandom(), cat.getEntityData().get(WCatEntity.WHITE_RATIO), cat.getEntityData().get(WCatEntity.ALBINO));
        int eyeLeftVariant = cat.getRandom().nextInt(WCGenetics.Constants.MAX_EYE_VARIANTS);

        cat.getEntityData().set(WCatEntity.EYE_COLOR_LEFT, leftEyeColor);
        cat.getEntityData().set(WCatEntity.EYE_COLOR_VARIANT_LEFT, eyeLeftVariant);

        if (WCGenetics.EyesAnomaly.isHeteroChromic(cat.getEntityData().get(WCatEntity.EYES_ANOMALY))) {
            cat.getEntityData().set(WCatEntity.EYE_COLOR_RIGHT, WCGenetics.EyeColor.generateAlelo(cat.getRandom(), cat.getEntityData().get(WCatEntity.WHITE_RATIO), cat.getEntityData().get(WCatEntity.ALBINO)));
            cat.getEntityData().set(WCatEntity.EYE_COLOR_VARIANT_RIGHT, cat.getRandom().nextInt(WCGenetics.Constants.MAX_EYE_VARIANTS));
        } else {
            cat.getEntityData().set(WCatEntity.EYE_COLOR_RIGHT, leftEyeColor);
            cat.getEntityData().set(WCatEntity.EYE_COLOR_VARIANT_RIGHT, eyeLeftVariant);
        }

        cat.getEntityData().set(WCatEntity.NOISE, cat.getRandom().nextInt(WCGenetics.Constants.MAX_NOISE_VARIANTS));

        cat.getEntityData().set(WCatEntity.SKIN_COLOR,
                WCGenetics.Constants.getSkinShade(cat.getEntityData().get(WCatEntity.WHITE_RATIO),
                        cat.getEntityData().get(WCatEntity.BASE),
                        cat.getEntityData().get(WCatEntity.AGOUTI),
                        cat.getEntityData().get(WCatEntity.ORANGE_BASE),
                        cat.getGender(), cat.getRandom()));

        if (WCGenetics.Base.isBlack(cat.getEntityData().get(WCatEntity.BASE))) {
            cat.getEntityData().set(WCatEntity.RUFOUSING_VARIANT, cat.getRandom().nextInt(3));
        } else {
            cat.getEntityData().set(WCatEntity.RUFOUSING_VARIANT, cat.getRandom().nextInt(WCGenetics.Constants.MAX_RUFOUSING_VARIANTS));
        }

        if (WCGenetics.Dilute.isDilute(cat.getEntityData().get(WCatEntity.DILUTE))) {
            cat.getEntityData().set(WCatEntity.BLUE_RUFOUSING_VARIANT, cat.getRandom().nextInt(3));
        } else {
            cat.getEntityData().set(WCatEntity.BLUE_RUFOUSING_VARIANT, cat.getRandom().nextInt(WCGenetics.Constants.MAX_BLUE_RUFOUSING_VARIANTS));
        }

        cat.getEntityData().set(WCatEntity.CHIMERA_GENE, WCGenetics.Chimerism.generateAlelo(cat.getRandom()) + "-" + WCGenetics.Chimerism.generateAlelo(cat.getRandom()));
        if (WCGenetics.Chimerism.isChimera(cat.getEntityData().get(WCatEntity.CHIMERA_GENE))) {
            initializeChimeraGenetics();
        }

    }

    public void initializeChimeraGenetics() {
        cat.getEntityData().set(WCatEntity.BASE_CHIMERA, WCGenetics.Base.generateAlelo(cat.getRandom()) + "-" + WCGenetics.Base.generateAlelo(cat.getRandom()));

        cat.getEntityData().set(WCatEntity.CHIMERA_VARIANT, cat.getRandom().nextInt(WCGenetics.Constants.MAX_CHIMERISM_VARIANTS));

        cat.getEntityData().set(WCatEntity.ORANGE_BASE_CHIMERA, WCGenetics.OrangeBase.generateAlelo(cat.getRandom()) + "-" + WCGenetics.OrangeBase.generateAlelo(cat.getRandom()));
        cat.getEntityData().set(WCatEntity.ORANGE_BASE_VARIANT_CHIMERA, cat.getRandom().nextInt(WCGenetics.Constants.MAX_TORTIE_VARIANTS));

        String chimWhiteRatioGene = WCGenetics.WhiteRatio.generateAlelo(cat.getRandom()) + "-" + WCGenetics.WhiteRatio.generateAlelo(cat.getRandom());
        cat.getEntityData().set(WCatEntity.WHITE_RATIO_CHIMERA, chimWhiteRatioGene);
        cat.getEntityData().set(WCatEntity.WHITE_RATIO_VARIANT_CHIMERA, cat.getRandom().nextInt(WCGenetics.Constants.getWhiteVariants(chimWhiteRatioGene)));

        cat.getEntityData().set(WCatEntity.ALBINO_CHIMERA, WCGenetics.Albino.generateAlelo(cat.getRandom()) + "-" + WCGenetics.Albino.generateAlelo(cat.getRandom()));
        cat.getEntityData().set(WCatEntity.ALBINO_VARIANT_CHIMERA, cat.getRandom().nextInt(WCGenetics.Constants.MAX_ALBINO_VARIANTS));

        cat.getEntityData().set(WCatEntity.DILUTE_CHIMERA, WCGenetics.Dilute.generateAlelo(cat.getRandom()) + "-" + WCGenetics.Dilute.generateAlelo(cat.getRandom()));

        cat.getEntityData().set(WCatEntity.AGOUTI_CHIMERA, WCGenetics.Agouti.generateAlelo(cat.getRandom()) + "-" + WCGenetics.Agouti.generateAlelo(cat.getRandom()));

        String chimTabbyGene = WCGenetics.TabbyStripeTypes.generateAlelo(cat.getRandom()) + "-" + WCGenetics.TabbyStripeTypes.generateAlelo(cat.getRandom());
        cat.getEntityData().set(WCatEntity.TABBY_STRIPES_CHIMERA, chimTabbyGene);
        cat.getEntityData().set(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA, cat.getRandom().nextInt(WCGenetics.Constants.getTabbyVariants(chimTabbyGene)));

        cat.getEntityData().set(WCatEntity.SILVER_CHIMERA, WCGenetics.Silver.generateAlelo(cat.getRandom()) + "-" + WCGenetics.Silver.generateAlelo(cat.getRandom()));
        cat.getEntityData().set(WCatEntity.SILVER_VARIANT_CHIMERA, cat.getRandom().nextInt(WCGenetics.Constants.MAX_SILVER_VARIANTS));

        cat.getEntityData().set(WCatEntity.EYES_ANOMALY, "h-h");
        String leftEyeColor = WCGenetics.EyeColor.generateAlelo(cat.getRandom(), cat.getEntityData().get(WCatEntity.WHITE_RATIO), cat.getEntityData().get(WCatEntity.ALBINO));
        int eyeLeftVariant = cat.getRandom().nextInt(WCGenetics.Constants.MAX_EYE_VARIANTS);
        cat.getEntityData().set(WCatEntity.EYE_COLOR_LEFT, leftEyeColor);
        cat.getEntityData().set(WCatEntity.EYE_COLOR_VARIANT_LEFT, eyeLeftVariant);

        if (WCGenetics.EyesAnomaly.isHeteroChromic(cat.getEntityData().get(WCatEntity.EYES_ANOMALY))) {
            cat.getEntityData().set(WCatEntity.EYE_COLOR_RIGHT, WCGenetics.EyeColor.generateAlelo(cat.getRandom(), cat.getEntityData().get(WCatEntity.WHITE_RATIO_CHIMERA), cat.getEntityData().get(WCatEntity.ALBINO_CHIMERA)));
            cat.getEntityData().set(WCatEntity.EYE_COLOR_VARIANT_RIGHT, cat.getRandom().nextInt(WCGenetics.Constants.MAX_EYE_VARIANTS));
        } else {
            cat.getEntityData().set(WCatEntity.EYE_COLOR_RIGHT, leftEyeColor);
            cat.getEntityData().set(WCatEntity.EYE_COLOR_VARIANT_RIGHT, eyeLeftVariant);
        }

        cat.getEntityData().set(WCatEntity.NOISE_CHIMERA, cat.getRandom().nextInt(WCGenetics.Constants.MAX_NOISE_VARIANTS));

        if (WCGenetics.Base.isBlack(cat.getEntityData().get(WCatEntity.BASE_CHIMERA))) {
            cat.getEntityData().set(WCatEntity.RUFOUSING_VARIANT_CHIMERA, cat.getRandom().nextInt(3));
        } else {
            cat.getEntityData().set(WCatEntity.RUFOUSING_VARIANT_CHIMERA, cat.getRandom().nextInt(WCGenetics.Constants.MAX_RUFOUSING_VARIANTS));
        }

        if (WCGenetics.Dilute.isDilute(cat.getEntityData().get(WCatEntity.DILUTE_CHIMERA))) {
            cat.getEntityData().set(WCatEntity.BLUE_RUFOUSING_VARIANT_CHIMERA, cat.getRandom().nextInt(3));
        } else {
            cat.getEntityData().set(WCatEntity.BLUE_RUFOUSING_VARIANT_CHIMERA, cat.getRandom().nextInt(WCGenetics.Constants.MAX_BLUE_RUFOUSING_VARIANTS));
        }

    }

    public void saveGeneticsNBT(CompoundTag tag) {

        {
            WCGenetics.PackedGeneticData geneticData = new WCGenetics.PackedGeneticData(
                    this.getGenetics(), this.getGenVariants(), this.getChimeraGenetics(), this.getChimeraGenVariants(),
                    cat.isOnGeneticalSkin(), cat.getVariant()
            );
            WCGenetics.saveModuleNBT(tag, geneticData);
            tag.putInt("IdlePose", cat.getIdlePose());

        }
    }

    public void loadGeneticsNBT(CompoundTag tag) {

        if (tag.contains("Genetics")) {
            CompoundTag geneticsTag = tag.getCompound("Genetics");

            cat.setOnGeneticalSkin(geneticsTag.getBoolean("Genetical"));

            WCGenetics genetics = new WCGenetics(
                    geneticsTag.getString("Bobtail"),
                    geneticsTag.getString("ChestFur"),
                    geneticsTag.getString("BellyFur"),
                    geneticsTag.getString("LegsFur"),
                    geneticsTag.getString("HeadFur"),
                    geneticsTag.getString("CheekFur"),
                    geneticsTag.getString("TailFur"),
                    geneticsTag.getString("BackFur"),

                    geneticsTag.getString("Base"),
                    geneticsTag.getString("OrangeBase"),
                    geneticsTag.getString("WhiteRatio"),
                    geneticsTag.getString("Albino"),
                    geneticsTag.getString("Dilute"),
                    geneticsTag.getString("Agouti"),
                    geneticsTag.getString("TabbyStripes"),
                    geneticsTag.getString("EyesAnomaly"),

                    geneticsTag.getString("Chimera"),
                    geneticsTag.getString("Silver")
            );

            WCGenetics geneticsChimera = new WCGenetics(
                    geneticsTag.getString("Bobtail"),
                    geneticsTag.getString("ChestFur"),
                    geneticsTag.getString("BellyFur"),
                    geneticsTag.getString("LegsFur"),
                    geneticsTag.getString("HeadFur"),
                    geneticsTag.getString("CheekFur"),
                    geneticsTag.getString("TailFur"),
                    geneticsTag.getString("BackFur"),

                    geneticsTag.getString("BaseChimera"),
                    geneticsTag.getString("OrangeBaseChimera"),
                    geneticsTag.getString("WhiteRatioChimera"),
                    geneticsTag.getString("AlbinoChimera"),
                    geneticsTag.getString("DiluteChimera"),
                    geneticsTag.getString("AgoutiChimera"),
                    geneticsTag.getString("TabbyStripesChimera"),
                    geneticsTag.getString("EyesAnomalyChimera"),

                    geneticsTag.getString("Chimera"),
                    geneticsTag.getString("SilverChimera")
            );

            this.setGenetics(genetics);

            this.setChimeraGenetics(geneticsChimera);


            cat.getEntityData().set(WCatEntity.RUFOUSING_VARIANT, geneticsTag.getInt("Rufousing"));
            cat.getEntityData().set(WCatEntity.BLUE_RUFOUSING_VARIANT, geneticsTag.getInt("BlueRufousing"));
            cat.getEntityData().set(WCatEntity.NOISE, geneticsTag.getInt("Noise"));

            cat.getEntityData().set(WCatEntity.CHIMERA_VARIANT, geneticsTag.getInt("ChimeraVariant"));

            cat.getEntityData().set(WCatEntity.EYE_COLOR_LEFT, geneticsTag.getString("EyeColorLeft"));
            cat.getEntityData().set(WCatEntity.EYE_COLOR_RIGHT, geneticsTag.getString("EyeColorRight"));

            cat.getEntityData().set(WCatEntity.ORANGE_BASE_VARIANT, geneticsTag.getInt("OrangeBaseVariant"));
            cat.getEntityData().set(WCatEntity.WHITE_RATIO_VARIANT, geneticsTag.getInt("WhiteRatioVariant"));
            cat.getEntityData().set(WCatEntity.ALBINO_VARIANT, geneticsTag.getInt("AlbinoVariant"));
            cat.getEntityData().set(WCatEntity.TABBY_STRIPES_VARIANT, geneticsTag.getInt("TabbyStripesVariant"));
            cat.getEntityData().set(WCatEntity.EYE_COLOR_VARIANT_LEFT, geneticsTag.getInt("EyeColorVariantLeft"));
            cat.getEntityData().set(WCatEntity.EYE_COLOR_VARIANT_RIGHT, geneticsTag.getInt("EyeColorVariantRight"));
            cat.getEntityData().set(WCatEntity.SIZE, geneticsTag.getFloat("Size"));
            cat.getEntityData().set(WCatEntity.SILVER_VARIANT, geneticsTag.getInt("SilverVariant"));
            cat.getEntityData().set(WCatEntity.SCARS, geneticsTag.getInt("Scars"));

            cat.getEntityData().set(WCatEntity.IDLE_POSE, geneticsTag.getInt("IdlePose"));



            cat.getEntityData().set(WCatEntity.ORANGE_BASE_VARIANT_CHIMERA, geneticsTag.getInt("OrangeBaseVariantChimera"));
            cat.getEntityData().set(WCatEntity.WHITE_RATIO_VARIANT_CHIMERA, geneticsTag.getInt("WhiteRatioVariantChimera"));
            cat.getEntityData().set(WCatEntity.ALBINO_VARIANT_CHIMERA, geneticsTag.getInt("AlbinoVariantChimera"));
            cat.getEntityData().set(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA, geneticsTag.getInt("TabbyStripesVariantChimera"));
            cat.getEntityData().set(WCatEntity.SILVER_VARIANT_CHIMERA, geneticsTag.getInt("SilverVariantChimera"));
            cat.getEntityData().set(WCatEntity.NOISE_CHIMERA, geneticsTag.getInt("NoiseChimera"));
            cat.getEntityData().set(WCatEntity.RUFOUSING_VARIANT_CHIMERA, geneticsTag.getInt("RufousingChimera"));
            cat.getEntityData().set(WCatEntity.BLUE_RUFOUSING_VARIANT_CHIMERA, geneticsTag.getInt("BlueRufousingChimera"));
        }

        WCGenetics.PackedGeneticData data = WCGenetics.loadModuleNBT(tag);
        if (data != null) {
            this.setGenetics(data.genetics);
            this.setGeneticalVariants(data.variants);
            this.setChimeraGenetics(data.chimerasGenetics);
            this.setGeneticalVariantsChimera(data.chimeraVariants);
            cat.setOnGeneticalSkin(data.onGeneticalSkin);
            cat.setIdlePose(tag.getInt("IdlePose"));
        }
    }

    public void setStoredFatherGenetics(WCatEntity father) {
        if (father.isOnGeneticalSkin()) {
            if (WCGenetics.Chimerism.isChimera(father.getEntityData().get(WCatEntity.CHIMERA_GENE))) {
                if (father.getRandom().nextBoolean()) {
                    cat.storedFatherGenetics = father.getGeneticsModule().getChimeraGenetics();
                } else {
                    cat.storedFatherGenetics = father.getGeneticsModule().getGenetics();
                }
            } else {
                cat.storedFatherGenetics = father.getGeneticsModule().getGenetics();
            }
        } else {
            cat.storedFatherGenetics = GeneticsForVariant.get(father.getVariant());
        }
    }

    void saveFatherGeneticsToNBT(CompoundTag tag, WCGenetics genetics) {

        tag.putString("ChestFur", genetics.chestFur);
        tag.putString("BellyFur", genetics.bellyFur);
        tag.putString("LegsFur", genetics.legsFur);
        tag.putString("HeadFur", genetics.headFur);
        tag.putString("CheekFur", genetics.cheekFur);
        tag.putString("BackFur", genetics.backFur);
        tag.putString("TailFur", genetics.tailFur);
        tag.putString("Bobtail", genetics.bobtail);

        tag.putString("Base", genetics.base);
        tag.putString("Orange", genetics.orangeBase);
        tag.putString("WhiteRatio", genetics.whiteRatio);
        tag.putString("Albino", genetics.albino);
        tag.putString("Dilute", genetics.dilute);
        tag.putString("Agouti", genetics.agouti);
        tag.putString("TabbyStripes", genetics.tabbyStripes);
        tag.putString("EyesAnomaly", genetics.eyesAnomaly);
        tag.putString("Chimera", genetics.chimeraGene);
        tag.putString("Silver", genetics.silver);

    }

    public static String inheritGenetics(String motherGene, String fatherGene, RandomSource randomSource) {
        try {
            String[] motherAlleles = motherGene.split("-");
            String[] fatherAlleles = fatherGene.split("-");

            if ((motherAlleles[0].isEmpty() || motherAlleles[1].isEmpty()) || (fatherAlleles[0].isEmpty() || fatherAlleles[1].isEmpty()))
                return normalize("x-x");

            String motherAllele = motherAlleles[randomSource.nextInt(2)];
            String fatherAllele = fatherAlleles[randomSource.nextInt(2)];

            return normalize(motherAllele + "-" + fatherAllele);
        } catch (Exception e) {
            return normalize("x-x");
        }
    }

    private static String normalize(String gene) {

        String[] alleles = gene.split("-");

        String a = alleles[0];
        String b = alleles[1];

        if (b.equals(b.toUpperCase()) && a.equals(a.toLowerCase())) {
            return b + "-" + a;
        }

        return gene;
    }

    @OnlyIn(Dist.CLIENT)
    public String getCatTextureKey() {
        if (cat.textureKey == null) defineTextureLayers();

        return cat.textureKey;
    }

    @OnlyIn(Dist.CLIENT)
    private void defineTextureLayers() {
        String folderPath = "warriorcats_events:textures/entity/wcat/genetics/";

        if (!cat.isOnGeneticalSkin()) return;

        String basePath = folderPath + "base/";
        if (WCGenetics.Base.isBlack(cat.getEntityData().get(WCatEntity.BASE))) {
            basePath += "black";
        } else if (WCGenetics.Base.isChocolate(cat.getEntityData().get(WCatEntity.BASE))) {
            basePath += "chocolate";
        } else if (WCGenetics.Base.isCinnamon(cat.getEntityData().get(WCatEntity.BASE))) {
            basePath += "cinnamon";
        } else {
            basePath = folderPath + "empty";
        }

        String orangebasePath = folderPath + "orange_base/";
        String stripesForOrangePath = folderPath + "agouti_marks/orange/";
        if (WCGenetics.OrangeBase.isOrange(cat.getEntityData().get(WCatEntity.ORANGE_BASE), cat.getGender())) {

            orangebasePath += "orange";

            if (WCGenetics.TabbyStripeTypes.isMackerel(cat.getEntityData().get(WCatEntity.TABBY_STRIPES))) {
                stripesForOrangePath += "mackerel_orange_" + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT);// WCatEntity.ORANGE_BASE -> 0-4
            } else {
                stripesForOrangePath += "classic_orange_" + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT);// WCatEntity.ORANGE_BASE -> 0-4
            }

        } else if (WCGenetics.OrangeBase.isTortoiseshell(cat.getEntityData().get(WCatEntity.ORANGE_BASE))) {

            orangebasePath += "tortie_" + cat.getEntityData().get(WCatEntity.ORANGE_BASE_VARIANT);// WCatEntity.ORANGE_BASE -> 0-4

            if (WCGenetics.TabbyStripeTypes.isMackerel(cat.getEntityData().get(WCatEntity.TABBY_STRIPES))) {
                stripesForOrangePath += "mackerel_orange_" + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT);// WCatEntity.ORANGE_BASE -> 0-4
            } else {
                stripesForOrangePath += "classic_orange_" + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT);// WCatEntity.ORANGE_BASE -> 0-4
            }

        } else {
            orangebasePath = folderPath + "empty";
            stripesForOrangePath = folderPath + "empty";
        }

        if (WCGenetics.Dilute.isDilute(cat.getEntityData().get(WCatEntity.DILUTE))) {
            if (WCGenetics.Base.isBlack(cat.getEntityData().get(WCatEntity.BASE))) {
                basePath = folderPath + "base/black_to_gray";
            } else if (WCGenetics.Base.isChocolate(cat.getEntityData().get(WCatEntity.BASE))) {
                basePath = folderPath + "base/chocolate_to_lilac";
            } else if (WCGenetics.Base.isCinnamon(cat.getEntityData().get(WCatEntity.BASE))) {
                basePath = folderPath + "base/cinnamon_to_fawn";
            }

            if (WCGenetics.OrangeBase.isOrange(cat.getEntityData().get(WCatEntity.ORANGE_BASE), cat.getGender())) {
                orangebasePath = folderPath + "orange_base/orange_to_cream";

                if (WCGenetics.TabbyStripeTypes.isMackerel(cat.getEntityData().get(WCatEntity.TABBY_STRIPES))) {
                    stripesForOrangePath = folderPath + "agouti_marks/orange/mackerel_otc_" + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT);
                } else {
                    stripesForOrangePath = folderPath + "agouti_marks/orange/classic_otc_" + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT);
                }

            } else if (WCGenetics.OrangeBase.isTortoiseshell(cat.getEntityData().get(WCatEntity.ORANGE_BASE))) {
                orangebasePath = folderPath + "orange_base/tortie_to_cream_" + cat.getEntityData().get(WCatEntity.ORANGE_BASE_VARIANT);

                if (WCGenetics.TabbyStripeTypes.isMackerel(cat.getEntityData().get(WCatEntity.TABBY_STRIPES))) {
                    stripesForOrangePath = folderPath + "agouti_marks/orange/mackerel_otc_" + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT);
                } else {
                    stripesForOrangePath = folderPath + "agouti_marks/orange/classic_otc_" + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT);
                }

            } else {
                orangebasePath = folderPath + "empty";
                stripesForOrangePath = folderPath + "empty";
            }

        }

        String albinoPath = folderPath + "albino/";
        if (!WCGenetics.Albino.isNotAlbino(cat.getEntityData().get(WCatEntity.ALBINO))) {
            if (WCGenetics.Albino.isTrueAlbino(cat.getEntityData().get(WCatEntity.ALBINO))) {
                albinoPath += "full_albino_" + cat.getEntityData().get(WCatEntity.ALBINO_VARIANT);
            } else if (WCGenetics.Albino.isMink(cat.getEntityData().get(WCatEntity.ALBINO))) {
                albinoPath += "mink_" + cat.getEntityData().get(WCatEntity.ALBINO_VARIANT);// WCatEntity.ALBINO -> 0-2
            } else if (WCGenetics.Albino.isSepia(cat.getEntityData().get(WCatEntity.ALBINO))) {
                albinoPath += "sepia_" + cat.getEntityData().get(WCatEntity.ALBINO_VARIANT);// WCatEntity.ALBINO -> 0-2
            } else if (WCGenetics.Albino.isSiamese(cat.getEntityData().get(WCatEntity.ALBINO))) {
                albinoPath += "siamese_" + cat.getEntityData().get(WCatEntity.ALBINO_VARIANT);// WCatEntity.ALBINO -> 0-2
            } else {
                albinoPath = folderPath + "empty";
            }
        } else {
            albinoPath = folderPath + "empty";
        }

        String whiteMarks = folderPath + "white_marks/";
        if (WCGenetics.WhiteRatio.isWhite(cat.getEntityData().get(WCatEntity.WHITE_RATIO))) {
            whiteMarks += "full_white";
        } else if (WCGenetics.WhiteRatio.isHighSpotted(cat.getEntityData().get(WCatEntity.WHITE_RATIO))) {
            whiteMarks += "high_spots_" + cat.getEntityData().get(WCatEntity.WHITE_RATIO_VARIANT);// WHITE_MARKS -> 0-3
        } else if (WCGenetics.WhiteRatio.isLowSpotted(cat.getEntityData().get(WCatEntity.WHITE_RATIO))) {
            whiteMarks += "low_spots_" + cat.getEntityData().get(WCatEntity.WHITE_RATIO_VARIANT);// WHITE_MARKS -> 0-3
        } else {
            whiteMarks = folderPath + "empty";
        }


        String agoutiMarks = folderPath + "agouti_marks/";
        if (WCGenetics.Agouti.isTabby(cat.getEntityData().get(WCatEntity.AGOUTI))) {
            String secondStripesKey = "";
            if (WCGenetics.Base.isBlack(cat.getEntityData().get(WCatEntity.BASE))) {
                if (!WCGenetics.Dilute.isDilute(cat.getEntityData().get(WCatEntity.DILUTE))) {
                    basePath = folderPath + "base/black_to_darkbrown";
                }
                secondStripesKey = "black_";
            } else if (WCGenetics.Base.isChocolate(cat.getEntityData().get(WCatEntity.BASE))) {
                secondStripesKey = "darkbrown_";
            } else if (WCGenetics.Base.isCinnamon(cat.getEntityData().get(WCatEntity.BASE))) {
                secondStripesKey = "mediumbrown_";
            }


            if (WCGenetics.TabbyStripeTypes.isMackerel(cat.getEntityData().get(WCatEntity.TABBY_STRIPES))) {
                if (WCGenetics.Dilute.isDilute(cat.getEntityData().get(WCatEntity.DILUTE))) {
                    agoutiMarks += "mackerel_dilute_" + secondStripesKey + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT); // TABBY_MARKS -> 0-4
                } else {
                    agoutiMarks += "mackerel_" + secondStripesKey + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT); // TABBY_MARKS -> 0-4
                }
            } else if (WCGenetics.TabbyStripeTypes.isClassic(cat.getEntityData().get(WCatEntity.TABBY_STRIPES))) {
                if (WCGenetics.Dilute.isDilute(cat.getEntityData().get(WCatEntity.DILUTE))) {
                    agoutiMarks += "classic_dilute_" + secondStripesKey + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT); // TABBY_MARKS -> 0-4
                } else {
                    agoutiMarks += "classic_" + secondStripesKey + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT); // TABBY_MARKS -> 0-4
                }
            } else {
                agoutiMarks = folderPath + "empty";
            }
        } else {
            agoutiMarks = folderPath + "empty";
        }

        String eyeColorLeft = folderPath + "eyes_color/left/";
        switch (WCGenetics.EyeColor.getEyeColor(cat.getEntityData().get(WCatEntity.EYE_COLOR_LEFT))) {
            case BLUE -> eyeColorLeft += "blue_" + cat.getEntityData().get(WCatEntity.EYE_COLOR_VARIANT_LEFT);// EYES -> 0.4
            case GREEN -> eyeColorLeft += "green_" + cat.getEntityData().get(WCatEntity.EYE_COLOR_VARIANT_LEFT);// EYES -> 0.4
            case YELLOW -> eyeColorLeft += "yellow_" + cat.getEntityData().get(WCatEntity.EYE_COLOR_VARIANT_LEFT);// EYES -> 0.4
            case RED -> eyeColorLeft += "red_" + cat.getEntityData().get(WCatEntity.EYE_COLOR_VARIANT_LEFT);// EYES -> 0.4
            case BLIND -> eyeColorLeft += "blind";
        }

        String eyeColorRight = folderPath + "eyes_color/right/";
        switch (WCGenetics.EyeColor.getEyeColor(cat.getEntityData().get(WCatEntity.EYE_COLOR_RIGHT))) {
            case BLUE -> eyeColorRight += "blue_" + cat.getEntityData().get(WCatEntity.EYE_COLOR_VARIANT_RIGHT);// EYES -> 0.4
            case GREEN -> eyeColorRight += "green_" + cat.getEntityData().get(WCatEntity.EYE_COLOR_VARIANT_RIGHT);// EYES -> 0.4
            case YELLOW -> eyeColorRight += "yellow_" + cat.getEntityData().get(WCatEntity.EYE_COLOR_VARIANT_RIGHT);// EYES -> 0.4
            case RED -> eyeColorRight += "red_" + cat.getEntityData().get(WCatEntity.EYE_COLOR_VARIANT_RIGHT);// EYES -> 0.4
            case BLIND -> eyeColorRight += "blind";
        }

        String scarsPath = folderPath + "scars/scars_" + cat.getEntityData().get(WCatEntity.SCARS);

        String noisePath = folderPath + "details/noise_" + cat.getEntityData().get(WCatEntity.NOISE);
        if (WCGenetics.Base.isBlack(cat.getEntityData().get(WCatEntity.BASE)) && WCGenetics.Dilute.isDilute(cat.getEntityData().get(WCatEntity.DILUTE))){
            noisePath = folderPath + "details/noise_black_" + cat.getEntityData().get(WCatEntity.NOISE);
        }
        String skinDetails = folderPath + "details/skin/skin_" + cat.getEntityData().get(WCatEntity.SKIN_COLOR);

        int rufousingRatio = cat.getEntityData().get(WCatEntity.RUFOUSING_VARIANT);
        int rufousingIntKey = rufousingRatio * 5;
        if (WCGenetics.Albino.isTrueAlbino(cat.getEntityData().get(WCatEntity.ALBINO))) rufousingIntKey = 0;
        String rufousing = folderPath + "details/rufousing_" + rufousingIntKey;

        int bluerufousingRatio = cat.getEntityData().get(WCatEntity.BLUE_RUFOUSING_VARIANT);
        int bluerufousingIntKey = bluerufousingRatio * 5;
        if (WCGenetics.Albino.isTrueAlbino(cat.getEntityData().get(WCatEntity.ALBINO))) bluerufousingIntKey = 0;
        String bluerufousing = folderPath + "details/blue_rufousing_" + bluerufousingIntKey;


        String silver = folderPath + "silver/";
        String silver2 = folderPath + "silver/";
        if (WCGenetics.Silver.isSilver(cat.getEntityData().get(WCatEntity.SILVER), cat.getEntityData().get(WCatEntity.AGOUTI), cat.getEntityData().get(WCatEntity.ORANGE_BASE), cat.getGender())) {
            silver2 = folderPath + "empty";
            if (WCGenetics.TabbyStripeTypes.isClassic(cat.getEntityData().get(WCatEntity.TABBY_STRIPES))) {
                silver += "classic_";
            } else {
                silver += "mackerel_";
            }
            silver += cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT) + "_silver_" + cat.getEntityData().get(WCatEntity.SILVER_VARIANT);
        } else if (WCGenetics.Silver.isSmokeTortie(cat.getEntityData().get(WCatEntity.SILVER), cat.getEntityData().get(WCatEntity.AGOUTI), cat.getEntityData().get(WCatEntity.ORANGE_BASE))) {
            if (cat.getEntityData().get(WCatEntity.SILVER_VARIANT) == 2) {
                if (WCGenetics.TabbyStripeTypes.isClassic(cat.getEntityData().get(WCatEntity.TABBY_STRIPES))) {
                    silver += "classic_";
                } else {
                    silver += "mackerel_";
                }
                silver += cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT) + "_smoke_" + cat.getEntityData().get(WCatEntity.SILVER_VARIANT);
            } else {
                silver += "smoke_" +  cat.getEntityData().get(WCatEntity.SILVER_VARIANT);
            }

            if (WCGenetics.TabbyStripeTypes.isClassic(cat.getEntityData().get(WCatEntity.TABBY_STRIPES))) {
                silver2 += "classic_";
            } else {
                silver2 += "mackerel_";
            }
            silver2 += cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT) + "_silver_" + cat.getEntityData().get(WCatEntity.SILVER_VARIANT);

        } else if (WCGenetics.Silver.isSmoke(cat.getEntityData().get(WCatEntity.SILVER), cat.getEntityData().get(WCatEntity.AGOUTI))) {
            if (cat.getEntityData().get(WCatEntity.SILVER_VARIANT) == 2) {
                if (WCGenetics.TabbyStripeTypes.isClassic(cat.getEntityData().get(WCatEntity.TABBY_STRIPES))) {
                    silver += "classic_";
                } else {
                    silver += "mackerel_";
                }
                silver += cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT) + "_smoke_" + cat.getEntityData().get(WCatEntity.SILVER_VARIANT);
            } else {
                silver += "smoke_" +  cat.getEntityData().get(WCatEntity.SILVER_VARIANT);
            }

            silver2 = folderPath + "empty";

        } else {
            silver = folderPath + "empty";
            silver2 = folderPath + "empty";
        }


        String[] chimeraArray = defineTextureLayersChimera();



        cat.textureLayersPaths[0] = basePath + ".png";
        cat.textureLayersPaths[1] = agoutiMarks + ".png";
        cat.textureLayersPaths[2] = orangebasePath + ".png";
        cat.textureLayersPaths[3] = stripesForOrangePath + ".png";
        cat.textureLayersPaths[4] = rufousing + ".png";
        cat.textureLayersPaths[5] = bluerufousing + ".png";
        cat.textureLayersPaths[6] = silver + ".png";
        cat.textureLayersPaths[7] = silver2 + ".png";
        cat.textureLayersPaths[8] = whiteMarks + ".png";
        cat.textureLayersPaths[9] = albinoPath + ".png";
        cat.textureLayersPaths[10] = noisePath + ".png";

        cat.textureLayersPaths[11] = chimeraArray[0];
        cat.textureLayersPaths[12] = chimeraArray[1];
        cat.textureLayersPaths[13] = chimeraArray[2];
        cat.textureLayersPaths[14] = chimeraArray[3];
        cat.textureLayersPaths[15] = chimeraArray[4];
        cat.textureLayersPaths[16] = chimeraArray[5];
        cat.textureLayersPaths[17] = chimeraArray[6];
        cat.textureLayersPaths[18] = chimeraArray[7];
        cat.textureLayersPaths[19] = chimeraArray[8];
        cat.textureLayersPaths[20] = chimeraArray[9];
        cat.textureLayersPaths[21] = chimeraArray[10];

        cat.textureLayersPaths[22] = skinDetails + ".png";
        cat.textureLayersPaths[23] = eyeColorLeft + ".png";
        cat.textureLayersPaths[24] = eyeColorRight + ".png";
        cat.textureLayersPaths[25] = scarsPath + ".png";

        StringBuilder k = new StringBuilder("wcat_");

        for (String path : cat.textureLayersPaths) {
            k.append(path.replace(folderPath, "")).append('/');
        }

        cat.textureKey = k.toString() + "_wcat";
    }


    @OnlyIn(Dist.CLIENT)
    private String[] defineTextureLayersChimera() {
        String folderPath = "warriorcats_events:textures/entity/wcat/genetics/genetics_chimera/";

        String[] chimeraArray = new String[11];

        chimeraArray[0] = folderPath + "empty.png";
        chimeraArray[1] = folderPath + "empty.png";
        chimeraArray[2] = folderPath + "empty.png";
        chimeraArray[3] = folderPath + "empty.png";
        chimeraArray[4] = folderPath + "empty.png";
        chimeraArray[5] = folderPath + "empty.png";
        chimeraArray[6] = folderPath + "empty.png";
        chimeraArray[7] = folderPath + "empty.png";
        chimeraArray[8] = folderPath + "empty.png";
        chimeraArray[9] = folderPath + "empty.png";
        chimeraArray[10] = folderPath + "empty.png";

        if (!cat.isOnGeneticalSkin()) return chimeraArray;
        if (!WCGenetics.Chimerism.isChimera(cat.getEntityData().get(WCatEntity.CHIMERA_GENE))) return chimeraArray;

        String basePath = folderPath + "base/";
        if (WCGenetics.Base.isBlack(cat.getEntityData().get(WCatEntity.BASE_CHIMERA))) {
            basePath += "black_" + cat.getEntityData().get(WCatEntity.CHIMERA_VARIANT);
        } else if (WCGenetics.Base.isChocolate(cat.getEntityData().get(WCatEntity.BASE_CHIMERA))) {
            basePath += "chocolate_" + cat.getEntityData().get(WCatEntity.CHIMERA_VARIANT);
        } else if (WCGenetics.Base.isCinnamon(cat.getEntityData().get(WCatEntity.BASE_CHIMERA))) {
            basePath += "cinnamon_" + cat.getEntityData().get(WCatEntity.CHIMERA_VARIANT);
        } else {
            basePath = folderPath + "empty";
        }

        String orangebasePath = folderPath + "orange_base/";
        String stripesForOrangePath = folderPath + "agouti_marks/orange/";
        if (WCGenetics.OrangeBase.isOrange(cat.getEntityData().get(WCatEntity.ORANGE_BASE_CHIMERA), cat.getGender())) {

            orangebasePath += "orange";

            if (WCGenetics.TabbyStripeTypes.isMackerel(cat.getEntityData().get(WCatEntity.TABBY_STRIPES_CHIMERA))) {
                stripesForOrangePath += "mackerel_orange_" + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA);// WCatEntity.ORANGE_BASE -> 0-4
            } else {
                stripesForOrangePath += "classic_orange_" + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA);// WCatEntity.ORANGE_BASE -> 0-4
            }

        } else if (WCGenetics.OrangeBase.isTortoiseshell(cat.getEntityData().get(WCatEntity.ORANGE_BASE_CHIMERA))) {

            orangebasePath += "tortie_" + cat.getEntityData().get(WCatEntity.ORANGE_BASE_VARIANT_CHIMERA);// WCatEntity.ORANGE_BASE -> 0-4

            if (WCGenetics.TabbyStripeTypes.isMackerel(cat.getEntityData().get(WCatEntity.TABBY_STRIPES_CHIMERA))) {
                stripesForOrangePath += "mackerel_orange_" + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA);// WCatEntity.ORANGE_BASE -> 0-4
            } else {
                stripesForOrangePath += "classic_orange_" + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA);// WCatEntity.ORANGE_BASE -> 0-4
            }

        } else {
            orangebasePath = folderPath + "empty";
            stripesForOrangePath = folderPath + "empty";
        }


        if (WCGenetics.Dilute.isDilute(cat.getEntityData().get(WCatEntity.DILUTE_CHIMERA))) {
            if (WCGenetics.Base.isBlack(cat.getEntityData().get(WCatEntity.BASE_CHIMERA))) {
                basePath = folderPath + "base/black_to_gray_" + cat.getEntityData().get(WCatEntity.CHIMERA_VARIANT);
            } else if (WCGenetics.Base.isChocolate(cat.getEntityData().get(WCatEntity.BASE_CHIMERA))) {
                basePath = folderPath + "base/chocolate_to_lilac_" + cat.getEntityData().get(WCatEntity.CHIMERA_VARIANT);
            } else if (WCGenetics.Base.isCinnamon(cat.getEntityData().get(WCatEntity.BASE_CHIMERA))) {
                basePath = folderPath + "base/cinnamon_to_fawn_" + cat.getEntityData().get(WCatEntity.CHIMERA_VARIANT);
            }

            if (WCGenetics.OrangeBase.isOrange(cat.getEntityData().get(WCatEntity.ORANGE_BASE_CHIMERA), cat.getGender())) {
                orangebasePath = folderPath + "orange_base/orange_to_cream";

                if (WCGenetics.TabbyStripeTypes.isMackerel(cat.getEntityData().get(WCatEntity.TABBY_STRIPES_CHIMERA))) {
                    stripesForOrangePath = folderPath + "agouti_marks/orange/mackerel_otc_" + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA);
                } else {
                    stripesForOrangePath = folderPath + "agouti_marks/orange/classic_otc_" + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA);
                }

            } else if (WCGenetics.OrangeBase.isTortoiseshell(cat.getEntityData().get(WCatEntity.ORANGE_BASE_CHIMERA))) {
                orangebasePath = folderPath + "orange_base/tortie_to_cream_" + cat.getEntityData().get(WCatEntity.ORANGE_BASE_VARIANT_CHIMERA);

                if (WCGenetics.TabbyStripeTypes.isMackerel(cat.getEntityData().get(WCatEntity.TABBY_STRIPES_CHIMERA))) {
                    stripesForOrangePath = folderPath + "agouti_marks/orange/mackerel_otc_" + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA);
                } else {
                    stripesForOrangePath = folderPath + "agouti_marks/orange/classic_otc_" + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA);
                }

            } else {
                orangebasePath = folderPath + "empty";
                stripesForOrangePath = folderPath + "empty";
            }

        }

        String albinoPath = folderPath + "albino/";
        if (!WCGenetics.Albino.isNotAlbino(cat.getEntityData().get(WCatEntity.ALBINO_CHIMERA))) {
            if (WCGenetics.Albino.isTrueAlbino(cat.getEntityData().get(WCatEntity.ALBINO_CHIMERA))) {
                albinoPath += "full_albino_" + cat.getEntityData().get(WCatEntity.ALBINO_VARIANT_CHIMERA);
            } else if (WCGenetics.Albino.isMink(cat.getEntityData().get(WCatEntity.ALBINO_CHIMERA))) {
                albinoPath += "mink_" + cat.getEntityData().get(WCatEntity.ALBINO_VARIANT_CHIMERA);// WCatEntity.ALBINO -> 0-2
            } else if (WCGenetics.Albino.isSepia(cat.getEntityData().get(WCatEntity.ALBINO_CHIMERA))) {
                albinoPath += "sepia_" + cat.getEntityData().get(WCatEntity.ALBINO_VARIANT_CHIMERA);// WCatEntity.ALBINO -> 0-2
            } else if (WCGenetics.Albino.isSiamese(cat.getEntityData().get(WCatEntity.ALBINO_CHIMERA))) {
                albinoPath += "siamese_" + cat.getEntityData().get(WCatEntity.ALBINO_VARIANT_CHIMERA);// WCatEntity.ALBINO -> 0-2
            } else {
                albinoPath = folderPath + "empty";
            }
        } else {
            albinoPath = folderPath + "empty";
        }

        String whiteMarks = folderPath + "white_marks/";
        if (WCGenetics.WhiteRatio.isWhite(cat.getEntityData().get(WCatEntity.WHITE_RATIO_CHIMERA))) {
            whiteMarks += "full_white";
        } else if (WCGenetics.WhiteRatio.isHighSpotted(cat.getEntityData().get(WCatEntity.WHITE_RATIO_CHIMERA))) {
            whiteMarks += "high_spots_" + cat.getEntityData().get(WCatEntity.WHITE_RATIO_VARIANT_CHIMERA);// WHITE_MARKS -> 0-3
        } else if (WCGenetics.WhiteRatio.isLowSpotted(cat.getEntityData().get(WCatEntity.WHITE_RATIO_CHIMERA))) {
            whiteMarks += "low_spots_" + cat.getEntityData().get(WCatEntity.WHITE_RATIO_VARIANT_CHIMERA);// WHITE_MARKS -> 0-3
        } else {
            whiteMarks = folderPath + "empty";
        }


        String agoutiMarks = folderPath + "agouti_marks/";
        if (WCGenetics.Agouti.isTabby(cat.getEntityData().get(WCatEntity.AGOUTI_CHIMERA))) {
            String secondStripesKey = "";
            if (WCGenetics.Base.isBlack(cat.getEntityData().get(WCatEntity.BASE_CHIMERA))) {
                if (!WCGenetics.Dilute.isDilute(cat.getEntityData().get(WCatEntity.DILUTE_CHIMERA))) {
                    basePath = folderPath + "base/black_to_darkbrown_" + cat.getEntityData().get(WCatEntity.CHIMERA_VARIANT);
                }
                secondStripesKey = "black_";
            } else if (WCGenetics.Base.isChocolate(cat.getEntityData().get(WCatEntity.BASE_CHIMERA))) {
                secondStripesKey = "darkbrown_";
            } else if (WCGenetics.Base.isCinnamon(cat.getEntityData().get(WCatEntity.BASE_CHIMERA))) {
                secondStripesKey = "mediumbrown_";
            }


            if (WCGenetics.TabbyStripeTypes.isMackerel(cat.getEntityData().get(WCatEntity.TABBY_STRIPES_CHIMERA))) {
                if (WCGenetics.Dilute.isDilute(cat.getEntityData().get(WCatEntity.DILUTE_CHIMERA))) {
                    agoutiMarks += "mackerel_dilute_" + secondStripesKey + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA); // TABBY_MARKS -> 0-4
                } else {
                    agoutiMarks += "mackerel_" + secondStripesKey + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA); // TABBY_MARKS -> 0-4
                }
            } else if (WCGenetics.TabbyStripeTypes.isClassic(cat.getEntityData().get(WCatEntity.TABBY_STRIPES_CHIMERA))) {
                if (WCGenetics.Dilute.isDilute(cat.getEntityData().get(WCatEntity.DILUTE_CHIMERA))) {
                    agoutiMarks += "classic_dilute_" + secondStripesKey + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA); // TABBY_MARKS -> 0-4
                } else {
                    agoutiMarks += "classic_" + secondStripesKey + cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA); // TABBY_MARKS -> 0-4
                }
            } else {
                agoutiMarks = folderPath + "empty";
            }
        } else {
            agoutiMarks = folderPath + "empty";
        }


        String noisePath = folderPath + "details/noise_" + cat.getEntityData().get(WCatEntity.NOISE_CHIMERA);
        if (WCGenetics.Base.isBlack(cat.getEntityData().get(WCatEntity.BASE_CHIMERA)) && WCGenetics.Dilute.isDilute(cat.getEntityData().get(WCatEntity.DILUTE_CHIMERA))){
            noisePath = folderPath + "details/noise_black_" + cat.getEntityData().get(WCatEntity.NOISE_CHIMERA);
        }

        int rufousingRatio = cat.getEntityData().get(WCatEntity.RUFOUSING_VARIANT_CHIMERA);
        int rufousingIntKey = rufousingRatio * 5;
        if (WCGenetics.Albino.isTrueAlbino(cat.getEntityData().get(WCatEntity.ALBINO_CHIMERA))) rufousingIntKey = 0;
        String rufousing = folderPath + "details/rufousing_" + rufousingIntKey;

        int bluerufousingRatio = cat.getEntityData().get(WCatEntity.BLUE_RUFOUSING_VARIANT_CHIMERA);
        int bluerufousingIntKey = bluerufousingRatio * 5;
        if (WCGenetics.Albino.isTrueAlbino(cat.getEntityData().get(WCatEntity.ALBINO_CHIMERA))) bluerufousingIntKey = 0;
        String bluerufousing = folderPath + "details/blue_rufousing_" + bluerufousingIntKey;

        String silver = folderPath + "silver/";
        String silver2 = folderPath + "silver/";
        if (WCGenetics.Silver.isSilver(cat.getEntityData().get(WCatEntity.SILVER_CHIMERA), cat.getEntityData().get(WCatEntity.AGOUTI_CHIMERA), cat.getEntityData().get(WCatEntity.ORANGE_BASE_CHIMERA), cat.getGender())) {
            silver2 = folderPath + "empty";
            if (WCGenetics.TabbyStripeTypes.isClassic(cat.getEntityData().get(WCatEntity.TABBY_STRIPES_CHIMERA))) {
                silver += "classic_";
            } else {
                silver += "mackerel_";
            }
            silver += cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA) + "_silver_" + cat.getEntityData().get(WCatEntity.SILVER_VARIANT_CHIMERA);
        } else if (WCGenetics.Silver.isSmokeTortie(cat.getEntityData().get(WCatEntity.SILVER_CHIMERA), cat.getEntityData().get(WCatEntity.AGOUTI_CHIMERA), cat.getEntityData().get(WCatEntity.ORANGE_BASE_CHIMERA))) {
            if (cat.getEntityData().get(WCatEntity.SILVER_VARIANT_CHIMERA) == 2) {
                if (WCGenetics.TabbyStripeTypes.isClassic(cat.getEntityData().get(WCatEntity.TABBY_STRIPES_CHIMERA))) {
                    silver += "classic_";
                } else {
                    silver += "mackerel_";
                }
                silver += cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA) + "_smoke_" + cat.getEntityData().get(WCatEntity.SILVER_VARIANT_CHIMERA);
            } else {
                silver += "smoke_" +  cat.getEntityData().get(WCatEntity.SILVER_VARIANT_CHIMERA);
            }

            if (WCGenetics.TabbyStripeTypes.isClassic(cat.getEntityData().get(WCatEntity.TABBY_STRIPES_CHIMERA))) {
                silver2 += "classic_";
            } else {
                silver2 += "mackerel_";
            }
            silver2 += cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA) + "_silver_" + cat.getEntityData().get(WCatEntity.SILVER_VARIANT_CHIMERA);

        } else if (WCGenetics.Silver.isSmoke(cat.getEntityData().get(WCatEntity.SILVER_CHIMERA), cat.getEntityData().get(WCatEntity.AGOUTI_CHIMERA))) {
            if (cat.getEntityData().get(WCatEntity.SILVER_VARIANT_CHIMERA) == 2) {
                if (WCGenetics.TabbyStripeTypes.isClassic(cat.getEntityData().get(WCatEntity.TABBY_STRIPES_CHIMERA))) {
                    silver += "classic_";
                } else {
                    silver += "mackerel_";
                }
                silver += cat.getEntityData().get(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA) + "_smoke_" + cat.getEntityData().get(WCatEntity.SILVER_VARIANT_CHIMERA);
            } else {
                silver += "smoke_" +  cat.getEntityData().get(WCatEntity.SILVER_VARIANT_CHIMERA);
            }

            silver2 = folderPath + "empty";

        } else {
            silver = folderPath + "empty";
            silver2 = folderPath + "empty";
        }
        /**
         *  // WCatEntity.BASE -> 0-4
         *  // WCatEntity.ORANGE_BASE -> 0-4
         *  // WHITE_MARKS -> 0-3
         *  // WCatEntity.ALBINO -> 0-2
         *  // WCatEntity.DILUTE -> 0-4
         *  // TABBY_MARKS -> 0-4
         *  // EYES -> 0.4
         *  // WCatEntity.NOISE -> 0-2
         */

        chimeraArray[0] = basePath + ".png";
        chimeraArray[1] = agoutiMarks + ".png";
        chimeraArray[2] = orangebasePath + ".png";
        chimeraArray[3] = stripesForOrangePath + ".png";
        chimeraArray[4] = rufousing + ".png";
        chimeraArray[5] = bluerufousing + ".png";
        chimeraArray[6] = silver + ".png";
        chimeraArray[7] = silver2 + ".png";
        chimeraArray[8] = whiteMarks + ".png";
        chimeraArray[9] = albinoPath + ".png";
        chimeraArray[10] = noisePath + ".png";


        return chimeraArray;
    }

    @OnlyIn(Dist.CLIENT)
    public void invalidate() {
        Object obj = WCRenderer.TEXTURE_CACHE.get(cat.textureKey);
        if (obj != null) {
            WCRenderer.TEXTURE_CACHE.remove(cat.textureKey);
        }
        cat.textureKey = null;
    }

    @OnlyIn(Dist.CLIENT)
    public String[] getTextureLayersPaths() {
        if (cat.textureKey == null) defineTextureLayers();

        return cat.textureLayersPaths;
    }

    public void inheritGeneticsFromParents(WCGenetics mother, WCGenetics father) {
        WCGenetics childGenes = new WCGenetics();

        childGenes.chestFur = inheritGenetics(mother.chestFur, father.chestFur, cat.getRandom());
        childGenes.bellyFur = inheritGenetics(mother.bellyFur, father.bellyFur, cat.getRandom());
        childGenes.legsFur = inheritGenetics(mother.legsFur, father.legsFur, cat.getRandom());
        childGenes.headFur = inheritGenetics(mother.headFur, father.headFur, cat.getRandom());
        childGenes.cheekFur = inheritGenetics(mother.cheekFur, father.cheekFur, cat.getRandom());
        childGenes.backFur = inheritGenetics(mother.backFur, father.backFur, cat.getRandom());
        childGenes.tailFur = inheritGenetics(mother.tailFur, father.tailFur, cat.getRandom());
        childGenes.bobtail = inheritGenetics(mother.bobtail, father.bobtail, cat.getRandom());

        childGenes.base = inheritGenetics(mother.base, father.base, cat.getRandom());
        childGenes.orangeBase = inheritGenetics(mother.orangeBase, father.orangeBase, cat.getRandom());
        childGenes.whiteRatio = inheritGenetics(mother.whiteRatio, father.whiteRatio, cat.getRandom());
        childGenes.albino = inheritGenetics(mother.albino, father.albino, cat.getRandom());
        childGenes.dilute = inheritGenetics(mother.dilute, father.dilute, cat.getRandom());
        childGenes.agouti = inheritGenetics(mother.agouti, father.agouti, cat.getRandom());
        childGenes.tabbyStripes = inheritGenetics(mother.tabbyStripes, father.tabbyStripes, cat.getRandom());
        childGenes.eyesAnomaly = inheritGenetics(mother.eyesAnomaly, father.eyesAnomaly, cat.getRandom());
        childGenes.chimeraGene = inheritGenetics(mother.chimeraGene, father.chimeraGene, cat.getRandom());
        childGenes.silver = inheritGenetics(mother.silver, father.silver, cat.getRandom());


        this.setGenetics(childGenes);


        if (WCGenetics.Chimerism.isChimera(childGenes.chimeraGene)) {
            WCGenetics chimeraChildGenes = new WCGenetics();
            WCGenetics.GeneticalChimeraVariants chimeraChildVariants = new WCGenetics.GeneticalChimeraVariants();


            WCGenetics toInheritFrom;
            if (cat.getRandom().nextBoolean()) {
                toInheritFrom = father;
            } else {
                toInheritFrom = mother;
            }

            chimeraChildGenes.chimeraGene = inheritGenetics(father.chimeraGene, mother.chimeraGene, cat.getRandom());

            chimeraChildGenes.base = inheritGenetics(toInheritFrom.base, toInheritFrom.base, cat.getRandom());
            chimeraChildGenes.orangeBase = inheritGenetics(toInheritFrom.orangeBase, toInheritFrom.orangeBase, cat.getRandom());
            chimeraChildGenes.whiteRatio = inheritGenetics(toInheritFrom.whiteRatio, toInheritFrom.whiteRatio, cat.getRandom());
            chimeraChildGenes.albino = inheritGenetics(toInheritFrom.albino, toInheritFrom.albino, cat.getRandom());
            chimeraChildGenes.dilute = inheritGenetics(toInheritFrom.dilute, toInheritFrom.dilute, cat.getRandom());
            chimeraChildGenes.silver = inheritGenetics(toInheritFrom.silver, toInheritFrom.silver, cat.getRandom());
            chimeraChildGenes.agouti = inheritGenetics(toInheritFrom.agouti, toInheritFrom.agouti, cat.getRandom());
            chimeraChildGenes.tabbyStripes = inheritGenetics(toInheritFrom.tabbyStripes, toInheritFrom.tabbyStripes, cat.getRandom());

            this.setChimeraGenetics(chimeraChildGenes);

            chimeraChildVariants.chimeraVariant = cat.getRandom().nextInt(WCGenetics.Constants.MAX_CHIMERISM_VARIANTS);
            chimeraChildVariants.rufousingVariant = cat.getRandom().nextInt(WCGenetics.Constants.MAX_RUFOUSING_VARIANTS);
            chimeraChildVariants.blueRufousingVariant = cat.getRandom().nextInt(WCGenetics.Constants.MAX_BLUE_RUFOUSING_VARIANTS);
            chimeraChildVariants.orangeVar = cat.getRandom().nextInt(WCGenetics.Constants.MAX_TORTIE_VARIANTS);
            chimeraChildVariants.whiteVar = cat.getRandom().nextInt(WCGenetics.Constants.getWhiteVariants(chimeraChildGenes.whiteRatio));
            chimeraChildVariants.tabbyVar = cat.getRandom().nextInt(WCGenetics.Constants.getTabbyVariants(chimeraChildGenes.tabbyStripes));
            chimeraChildVariants.albinoVar = cat.getRandom().nextInt(WCGenetics.Constants.MAX_ALBINO_VARIANTS);
            chimeraChildVariants.noise = cat.getRandom().nextInt(WCGenetics.Constants.MAX_NOISE_VARIANTS);
            chimeraChildVariants.silverVar = cat.getRandom().nextInt(WCGenetics.Constants.MAX_SILVER_VARIANTS);

            this.setGeneticalVariantsChimera(chimeraChildVariants);

        }

        String leftEyeColor = WCGenetics.EyeColor.generateAlelo(cat.getRandom(), cat.getEntityData().get(WCatEntity.WHITE_RATIO), cat.getEntityData().get(WCatEntity.ALBINO));
        int eyeLeftVariant = cat.getRandom().nextInt(WCGenetics.Constants.MAX_EYE_VARIANTS);

        cat.getEntityData().set(WCatEntity.EYE_COLOR_LEFT, leftEyeColor);
        cat.getEntityData().set(WCatEntity.EYE_COLOR_VARIANT_LEFT, eyeLeftVariant);

        if (WCGenetics.EyesAnomaly.isHeteroChromic(cat.getEntityData().get(WCatEntity.EYES_ANOMALY))) {
            cat.getEntityData().set(WCatEntity.EYE_COLOR_RIGHT, WCGenetics.EyeColor.generateAlelo(cat.getRandom(), cat.getEntityData().get(WCatEntity.WHITE_RATIO), cat.getEntityData().get(WCatEntity.ALBINO)));
            cat.getEntityData().set(WCatEntity.EYE_COLOR_VARIANT_RIGHT, cat.getRandom().nextInt(WCGenetics.Constants.MAX_EYE_VARIANTS));
        } else {
            cat.getEntityData().set(WCatEntity.EYE_COLOR_RIGHT, leftEyeColor);
            cat.getEntityData().set(WCatEntity.EYE_COLOR_VARIANT_RIGHT, eyeLeftVariant);
        }

        cat.getEntityData().set(WCatEntity.ORANGE_BASE_VARIANT, cat.getRandom().nextInt(WCGenetics.Constants.MAX_TORTIE_VARIANTS));
        cat.getEntityData().set(WCatEntity.WHITE_RATIO_VARIANT, cat.getRandom().nextInt(WCGenetics.Constants.getWhiteVariants(childGenes.whiteRatio)));
        cat.getEntityData().set(WCatEntity.ALBINO_VARIANT, cat.getRandom().nextInt(WCGenetics.Constants.MAX_ALBINO_VARIANTS));
        cat.getEntityData().set(WCatEntity.TABBY_STRIPES_VARIANT, cat.getRandom().nextInt(WCGenetics.Constants.getTabbyVariants(childGenes.tabbyStripes)));
        cat.getEntityData().set(WCatEntity.SILVER_VARIANT, cat.getRandom().nextInt(WCGenetics.Constants.MAX_SILVER_VARIANTS));

        cat.getEntityData().set(WCatEntity.SKIN_COLOR,
                WCGenetics.Constants.getSkinShade(cat.getEntityData().get(WCatEntity.WHITE_RATIO),
                        cat.getEntityData().get(WCatEntity.BASE),
                        cat.getEntityData().get(WCatEntity.AGOUTI),
                        cat.getEntityData().get(WCatEntity.ORANGE_BASE),
                        cat.getGender(), cat.getRandom()));

        if (WCGenetics.Base.isBlack(cat.getEntityData().get(WCatEntity.BASE))) {
            cat.getEntityData().set(WCatEntity.RUFOUSING_VARIANT, cat.getRandom().nextInt(3));
        } else {
            cat.getEntityData().set(WCatEntity.RUFOUSING_VARIANT, cat.getRandom().nextInt(WCGenetics.Constants.MAX_RUFOUSING_VARIANTS));
        }

        if (WCGenetics.Dilute.isDilute(cat.getEntityData().get(WCatEntity.DILUTE))) {
            cat.getEntityData().set(WCatEntity.BLUE_RUFOUSING_VARIANT, cat.getRandom().nextInt(3));
        } else {
            cat.getEntityData().set(WCatEntity.BLUE_RUFOUSING_VARIANT, cat.getRandom().nextInt(WCGenetics.Constants.MAX_BLUE_RUFOUSING_VARIANTS));
        }

    }


    public void setGeneticalVariants(WCGenetics.GeneticalVariants variants) {

        cat.getEntityData().set(WCatEntity.EYE_COLOR_LEFT, variants.eyeColorLeft);
        cat.getEntityData().set(WCatEntity.EYE_COLOR_RIGHT, variants.eyeColorRight);
        cat.getEntityData().set(WCatEntity.RUFOUSING_VARIANT, Math.min(variants.rufousingVariant, WCGenetics.Constants.MAX_RUFOUSING_VARIANTS-1));
        cat.getEntityData().set(WCatEntity.BLUE_RUFOUSING_VARIANT, Math.min(variants.blueRufousingVariant, WCGenetics.Constants.MAX_BLUE_RUFOUSING_VARIANTS-1));
        cat.getEntityData().set(WCatEntity.ORANGE_BASE_VARIANT, Math.min(variants.orangeVar, WCGenetics.Constants.MAX_TORTIE_VARIANTS-1));
        cat.getEntityData().set(WCatEntity.WHITE_RATIO_VARIANT, Math.min(variants.whiteVar, WCGenetics.Constants.getWhiteVariants(cat.getEntityData().get(WCatEntity.WHITE_RATIO))-1));
        cat.getEntityData().set(WCatEntity.TABBY_STRIPES_VARIANT, Math.min(variants.tabbyVar, WCGenetics.Constants.getTabbyVariants(cat.getEntityData().get(WCatEntity.TABBY_STRIPES)) -1));
        cat.getEntityData().set(WCatEntity.ALBINO_VARIANT, Math.min(variants.albinoVar, WCGenetics.Constants.MAX_ALBINO_VARIANTS-1));
        cat.getEntityData().set(WCatEntity.EYE_COLOR_VARIANT_LEFT, Math.min(variants.leftEyeVar, WCGenetics.Constants.MAX_EYE_VARIANTS-1));
        cat.getEntityData().set(WCatEntity.EYE_COLOR_VARIANT_RIGHT, Math.min(variants.rightEyeVar, WCGenetics.Constants.MAX_EYE_VARIANTS-1));
        cat.getEntityData().set(WCatEntity.NOISE, Math.min(variants.noise, WCGenetics.Constants.MAX_NOISE_VARIANTS-1));
        cat.getEntityData().set(WCatEntity.SILVER_VARIANT, Math.min(variants.silverVar, WCGenetics.Constants.MAX_SILVER_VARIANTS-1));
        cat.getEntityData().set(WCatEntity.SIZE, variants.size);
        cat.getEntityData().set(WCatEntity.SCARS, Math.min(variants.scars, WCGenetics.Constants.MAX_SCAR_VARIANTS-1));
        cat.getEntityData().set(WCatEntity.SKIN_COLOR, Math.min(variants.skin_color, WCGenetics.Constants.MAX_SKIN_VARIANTS-1));

    }

    public void setGeneticalVariantsChimera(WCGenetics.GeneticalChimeraVariants variants) {

        cat.getEntityData().set(WCatEntity.CHIMERA_VARIANT, Math.min(variants.chimeraVariant, WCGenetics.Constants.MAX_CHIMERISM_VARIANTS-1));

        cat.getEntityData().set(WCatEntity.RUFOUSING_VARIANT_CHIMERA, Math.min(variants.rufousingVariant, WCGenetics.Constants.MAX_RUFOUSING_VARIANTS-1));
        cat.getEntityData().set(WCatEntity.BLUE_RUFOUSING_VARIANT_CHIMERA, Math.min(variants.blueRufousingVariant, WCGenetics.Constants.MAX_BLUE_RUFOUSING_VARIANTS-1));
        cat.getEntityData().set(WCatEntity.ORANGE_BASE_VARIANT_CHIMERA, Math.min(variants.orangeVar, WCGenetics.Constants.MAX_TORTIE_VARIANTS-1));
        cat.getEntityData().set(WCatEntity.WHITE_RATIO_VARIANT_CHIMERA, Math.min(variants.whiteVar, WCGenetics.Constants.getWhiteVariants(cat.getEntityData().get(WCatEntity.WHITE_RATIO_CHIMERA))-1));
        cat.getEntityData().set(WCatEntity.TABBY_STRIPES_VARIANT_CHIMERA, Math.min(variants.tabbyVar, WCGenetics.Constants.getTabbyVariants(cat.getEntityData().get(WCatEntity.TABBY_STRIPES_CHIMERA))-1));
        cat.getEntityData().set(WCatEntity.ALBINO_VARIANT_CHIMERA, Math.min(variants.albinoVar, WCGenetics.Constants.MAX_ALBINO_VARIANTS-1));
        cat.getEntityData().set(WCatEntity.NOISE_CHIMERA, Math.min(variants.noise, WCGenetics.Constants.MAX_NOISE_VARIANTS-1));
        cat.getEntityData().set(WCatEntity.SILVER_VARIANT_CHIMERA, Math.min(variants.silverVar, WCGenetics.Constants.MAX_SILVER_VARIANTS-1));

    }

    public void setChimeraGenetics(WCGenetics genetics) {

        cat.getEntityData().set(WCatEntity.CHIMERA_GENE, genetics.chimeraGene);

        cat.getEntityData().set(WCatEntity.BASE_CHIMERA, genetics.base);
        cat.getEntityData().set(WCatEntity.ORANGE_BASE_CHIMERA, genetics.orangeBase);
        cat.getEntityData().set(WCatEntity.WHITE_RATIO_CHIMERA, genetics.whiteRatio);
        cat.getEntityData().set(WCatEntity.ALBINO_CHIMERA, genetics.albino);
        cat.getEntityData().set(WCatEntity.DILUTE_CHIMERA, genetics.dilute);
        cat.getEntityData().set(WCatEntity.AGOUTI_CHIMERA, genetics.agouti);
        cat.getEntityData().set(WCatEntity.TABBY_STRIPES_CHIMERA, genetics.tabbyStripes);

        cat.getEntityData().set(WCatEntity.SILVER_CHIMERA, genetics.silver);
    }

    public void setNonGeneticalValues(WCGenetics genetics, float size) {
        cat.getEntityData().set(WCatEntity.CHEST_FUR, genetics.chestFur);
        cat.getEntityData().set(WCatEntity.BELLY_FUR, genetics.bellyFur);
        cat.getEntityData().set(WCatEntity.LEGS_FUR, genetics.legsFur);
        cat.getEntityData().set(WCatEntity.HEAD_FUR, genetics.headFur);
        cat.getEntityData().set(WCatEntity.CHEEK_FUR, genetics.cheekFur);
        cat.getEntityData().set(WCatEntity.BACK_FUR, genetics.backFur);
        cat.getEntityData().set(WCatEntity.BOBTAIL, genetics.bobtail);
        cat.getEntityData().set(WCatEntity.TAIL_FUR, genetics.tailFur);

        cat.getEntityData().set(WCatEntity.SIZE, size);
    }

}
