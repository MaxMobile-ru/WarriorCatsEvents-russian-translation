package net.snowteb.warriorcats_events.entity.custom.wcat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.concurrent.Computable;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class WCGenetics {

    public class Constants {
        public static final int MAX_TORTIE_VARIANTS = 18;

        public static final int MAX_WHITE_VARIANTS_HIGH = 21;
        public static final int MAX_WHITE_VARIANTS_LOW = 21;

        public static final int MAX_ALBINO_VARIANTS = 3;

        public static final int MAX_TABBY_VARIANTS_CLASSIC = 5;
        public static final int MAX_TABBY_VARIANTS_MACKEREL = 5;

        public static final int MAX_EYE_VARIANTS = 12;

        public static final int MAX_NOISE_VARIANTS = 6;

        public static final int MAX_RUFOUSING_VARIANTS = 7;
        public static final int MAX_BLUE_RUFOUSING_VARIANTS = 7;

        public static final int MAX_SILVER_VARIANTS = 3;

        public static final int MAX_SCAR_VARIANTS = 26;

        public static final int MAX_IDLE_POSES = 4;

        public static final int MAX_CHIMERISM_VARIANTS = 8;

        public static final int MAX_SKIN_VARIANTS = 25;

        public static int getSkinShade(String whiteGenotype, String base, String agouti, String orange, int gender, RandomSource random) {
            int slices = MAX_SKIN_VARIANTS/5;

            if (WhiteRatio.isWhite(whiteGenotype)) {
                return random.nextInt(slices) + slices*2;
            }

            if (Base.isBlack(base) && !Agouti.isTabby(agouti)
                    && !OrangeBase.isTortoiseshell(orange) && !OrangeBase.isOrange(orange, gender)) {
                return random.nextInt(slices);
            }

            return random.nextInt(MAX_SKIN_VARIANTS);
        }

        public static int getWhiteVariants(String whiteGene) {
            if (WhiteRatio.isHighSpotted(whiteGene)) {
                return MAX_WHITE_VARIANTS_HIGH;
            } else if (WhiteRatio.isLowSpotted(whiteGene)) {
                return MAX_WHITE_VARIANTS_LOW;
            }
            return 1;
        }

        public static int getTabbyVariants(String tabbyGene) {
            if (TabbyStripeTypes.isMackerel(tabbyGene)) {
                return  MAX_TABBY_VARIANTS_MACKEREL;
            } else if (TabbyStripeTypes.isClassic(tabbyGene)) {
                return  MAX_TABBY_VARIANTS_CLASSIC;
            }
            return 1;
        }
    }

    public String chestFur = "s-s";
    public String bellyFur = "s-s";
    public String legsFur = "s-s";
    public String headFur = "s-s";
    public String cheekFur = "s-s";
    public String backFur = "s-s";
    public String tailFur = "s-s";
    public String bobtail = "B-B";

    public String base = "B-b";
    public String orangeBase = "o-o";
    public String whiteRatio = "w-w";
    public String albino = "C-cs";
    public String dilute = "d-d";
    public String agouti = "a-a";
    public String tabbyStripes = "mc-mc";
    public String eyesAnomaly = "H-h";
    public String silver = "i-i";

    public String chimeraGene = "C-C";


    public WCGenetics(WCGenetics copy) {
        this.chestFur = copy.chestFur;
        this.bellyFur = copy.bellyFur;
        this.legsFur = copy.legsFur;
        this.headFur = copy.headFur;
        this.cheekFur = copy.cheekFur;
        this.backFur =  copy.backFur;
        this.tailFur = copy.tailFur;
        this.bobtail = copy.bobtail;

        this.base = copy.base;
        this.orangeBase = copy.orangeBase;
        this.whiteRatio = copy.whiteRatio;
        this.albino = copy.albino;
        this.dilute = copy.dilute;
        this.agouti = copy.agouti;
        this.tabbyStripes = copy.tabbyStripes;
        this.silver = copy.silver;
        this.eyesAnomaly = copy.eyesAnomaly;

        this.chimeraGene = copy.chimeraGene;
    }

    public WCGenetics(String bobtail, String chestFur, String bellyFur,
                      String legsFur, String headFur, String cheekFur,
                      String tailFur, String backFur, String base,
                      String orangeBase, String whiteRatio, String albino, String dilute,
                      String agouti, String tabbyStripes, String eyesAnomaly,
                      String chimeraGene, String silver) {
        this.chestFur = chestFur;
        this.bellyFur = bellyFur;
        this.legsFur = legsFur;
        this.headFur = headFur;
        this.cheekFur = cheekFur;
        this.backFur =  backFur;
        this.tailFur = tailFur;
        this.bobtail = bobtail;

        this.base = base;
        this.orangeBase = orangeBase;
        this.whiteRatio = whiteRatio;
        this.albino = albino;
        this.dilute = dilute;
        this.agouti = agouti;
        this.tabbyStripes = tabbyStripes;
        this.eyesAnomaly = eyesAnomaly;
        this.silver = silver;

        this.chimeraGene = chimeraGene;
    }

    public WCGenetics() {
    }


    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.bobtail);
        buf.writeUtf(this.chestFur);
        buf.writeUtf(this.bellyFur);
        buf.writeUtf(this.legsFur);
        buf.writeUtf(this.headFur);
        buf.writeUtf(this.cheekFur);
        buf.writeUtf(this.tailFur);
        buf.writeUtf(this.backFur);

        buf.writeUtf(this.base);
        buf.writeUtf(this.orangeBase);
        buf.writeUtf(this.whiteRatio);
        buf.writeUtf(this.albino);
        buf.writeUtf(this.dilute);
        buf.writeUtf(this.agouti);
        buf.writeUtf(this.tabbyStripes);
        buf.writeUtf(this.eyesAnomaly);

        buf.writeUtf(this.chimeraGene);
        buf.writeUtf(this.silver);
    }

    public static WCGenetics decode(FriendlyByteBuf buf) {
        return new WCGenetics(
                buf.readUtf(),
                buf.readUtf(),
                buf.readUtf(),
                buf.readUtf(),
                buf.readUtf(),
                buf.readUtf(),
                buf.readUtf(),
                buf.readUtf(),

                buf.readUtf(),
                buf.readUtf(),
                buf.readUtf(),
                buf.readUtf(),
                buf.readUtf(),
                buf.readUtf(),
                buf.readUtf(),
                buf.readUtf(),

                buf.readUtf(),
                buf.readUtf()
        );
    }

    public void storeNBTSubModule(CompoundTag tag) {
        tag.putString("ChestFur", this.chestFur);
        tag.putString("BellyFur", this.bellyFur);
        tag.putString("LegsFur", this.legsFur);
        tag.putString("HeadFur", this.headFur);
        tag.putString("CheekFur", this.cheekFur);
        tag.putString("BackFur", this.backFur);
        tag.putString("TailFur", this.tailFur);
        tag.putString("Bobtail", this.bobtail);

        tag.putString("Base", this.base);
        tag.putString("OrangeBase", this.orangeBase);
        tag.putString("WhiteRatio", this.whiteRatio);
        tag.putString("Albino", this.albino);
        tag.putString("Dilute", this.dilute);
        tag.putString("Agouti", this.agouti);
        tag.putString("TabbyStripes", this.tabbyStripes);
        tag.putString("EyesAnomaly", this.eyesAnomaly);
        tag.putString("Silver", this.silver);
        tag.putString("ChimeraGene", this.chimeraGene);
    }

    public void loadNBTSubModule(CompoundTag tag) {
        this.chestFur = tag.getString("ChestFur");
        this.bellyFur = tag.getString("BellyFur");
        this.legsFur = tag.getString("LegsFur");
        this.headFur = tag.getString("HeadFur");
        this.cheekFur = tag.getString("CheekFur");
        this.backFur = tag.getString("BackFur");
        this.tailFur = tag.getString("TailFur");
        this.bobtail = tag.getString("Bobtail");

        this.base = tag.getString("Base");
        this.orangeBase = tag.getString("OrangeBase");
        this.whiteRatio = tag.getString("WhiteRatio");
        this.albino = tag.getString("Albino");
        this.dilute = tag.getString("Dilute");
        this.agouti = tag.getString("Agouti");
        this.tabbyStripes = tag.getString("TabbyStripes");
        this.eyesAnomaly = tag.getString("EyesAnomaly");
        this.silver = tag.getString("Silver");
        this.chimeraGene = tag.getString("ChimeraGene");
    }


    public static void saveModuleNBT(CompoundTag tag, PackedGeneticData data) {
        CompoundTag moduleTag = new CompoundTag();

        {
            moduleTag.putBoolean("OnGeneticalSkin", data.onGeneticalSkin);
            moduleTag.putInt("Preset", data.morphSkin);

            CompoundTag geneticsSubmodule = new CompoundTag();
            data.genetics.storeNBTSubModule(geneticsSubmodule);
            moduleTag.put("Genetics", geneticsSubmodule);

            CompoundTag chimeraGeneticsSubmodule = new CompoundTag();
            data.chimerasGenetics.storeNBTSubModule(chimeraGeneticsSubmodule);
            moduleTag.put("ChimeraGenetics", chimeraGeneticsSubmodule);

            CompoundTag variantsSubmodule = new CompoundTag();
            data.variants.storeNBTSubModule(variantsSubmodule);
            moduleTag.put("Variants", variantsSubmodule);

            CompoundTag chimeraVariantsSubmodule = new CompoundTag();
            data.chimeraVariants.storeNBTSubModule(chimeraVariantsSubmodule);
            moduleTag.put("ChimeraVariants", chimeraVariantsSubmodule);

        }

        tag.put("GeneticsModule", moduleTag);

    }

    @Nullable
    public static PackedGeneticData loadModuleNBT(CompoundTag tag) {
        if (tag.contains("GeneticsModule")) {
            CompoundTag moduleTag = tag.getCompound("GeneticsModule");

            WCGenetics genetics = new WCGenetics();
            WCGenetics chimeraGenetics = new WCGenetics();
            WCGenetics.GeneticalVariants variants = new WCGenetics.GeneticalVariants();
            WCGenetics.GeneticalChimeraVariants chimeraVariants = new WCGenetics.GeneticalChimeraVariants();

            boolean onGeneticalSkin = moduleTag.getBoolean("OnGeneticalSkin");
            int preset = moduleTag.getInt("Preset");

            if (moduleTag.contains("Genetics")) {
                CompoundTag geneticsSubmodule = moduleTag.getCompound("Genetics");
                genetics.loadNBTSubModule(geneticsSubmodule);
            }

            if (moduleTag.contains("ChimeraGenetics")) {
                CompoundTag chimeraGeneticsSubmodule = moduleTag.getCompound("ChimeraGenetics");
                chimeraGenetics.loadNBTSubModule(chimeraGeneticsSubmodule);
            }

            if (moduleTag.contains("Variants")) {
                CompoundTag variantsSubmodule = moduleTag.getCompound("Variants");
                variants.loadNBTSubModule(variantsSubmodule);
            }

            if (moduleTag.contains("ChimeraVariants")) {
                CompoundTag chimeraVariantsSubmodule = moduleTag.getCompound("ChimeraVariants");
                chimeraVariants.loadNBTSubModule(chimeraVariantsSubmodule);
            }

            return new PackedGeneticData(genetics, variants, chimeraGenetics, chimeraVariants, onGeneticalSkin, preset);
        }

        return null;
    }




    public static class GeneticalVariants {
        public String eyeColorLeft = "yellow";
        public String eyeColorRight = "yellow";
        public int orangeVar = 0;
        public int whiteVar = 0;
        public int tabbyVar = 0;
        public int silverVar = 0;
        public int albinoVar = 0;
        public int leftEyeVar = 0;
        public int rightEyeVar = 0;
        public float size = 0.8f;
        public int scars = 0;

        public int rufousingVariant = 0;
        public int blueRufousingVariant = 0;
        public int noise = 0;
        public int skin_color = 9;

        public GeneticalVariants() {

        }

        public GeneticalVariants(String eyeColorLeft, String eyeColorRight, int rufousingVariant,
                                 int blueRufousingVariant, int orangeVar, int whiteVar,
                                 int tabbyVar, int albinoVar, int leftEyeVar, int rightEyeVar,
                                 int noise, float size, int silverVar, int scars, int skin_color) {
            this.eyeColorLeft = eyeColorLeft;
            this.eyeColorRight = eyeColorRight;
            this.rufousingVariant = rufousingVariant;
            this.blueRufousingVariant = blueRufousingVariant;
            this.orangeVar = orangeVar;
            this.whiteVar = whiteVar;
            this.tabbyVar = tabbyVar;
            this.albinoVar = albinoVar;
            this.leftEyeVar = leftEyeVar;
            this.rightEyeVar = rightEyeVar;
            this.noise = noise;
            this.size = size;
            this.silverVar = silverVar;
            this.scars = scars;
            this.skin_color = skin_color;
        }

        public GeneticalVariants(GeneticalVariants copy) {
            this.eyeColorLeft = copy.eyeColorLeft;
            this.eyeColorRight = copy.eyeColorRight;
            this.rufousingVariant = copy.rufousingVariant;
            this.blueRufousingVariant = copy.blueRufousingVariant;
            this.orangeVar = copy.orangeVar;
            this.whiteVar = copy.whiteVar;
            this.tabbyVar = copy.tabbyVar;
            this.albinoVar = copy.albinoVar;
            this.leftEyeVar = copy.leftEyeVar;
            this.rightEyeVar = copy.rightEyeVar;
            this.noise = copy.noise;
            this.size = copy.size;
            this.silverVar = copy.silverVar;
            this.scars = copy.scars;
            this.skin_color = copy.skin_color;
        }

        public void storeNBTSubModule(CompoundTag tag) {
            tag.putString("EyeColorLeft", this.eyeColorLeft);
            tag.putString("EyeColorRight", this.eyeColorRight);
            tag.putInt("OrangeVar", this.orangeVar);
            tag.putInt("WhiteVar", this.whiteVar);
            tag.putInt("TabbyVar", this.tabbyVar);
            tag.putInt("SilverVar", this.silverVar);
            tag.putInt("AlbinoVar", this.albinoVar);
            tag.putInt("LeftEyeVar", this.leftEyeVar);
            tag.putInt("RightEyeVar", this.rightEyeVar);

            tag.putFloat("Size", this.size);

            tag.putInt("Scars", this.scars);

            tag.putInt("Rufousing", this.rufousingVariant);
            tag.putInt("BlueTint", this.blueRufousingVariant);
            tag.putInt("Noise", this.noise);
            tag.putInt("SkinColor", this.skin_color);
        }

        public void loadNBTSubModule(CompoundTag tag) {
            this.eyeColorLeft = tag.getString("EyeColorLeft");
            this.eyeColorRight = tag.getString("EyeColorRight");
            this.orangeVar = tag.getInt("OrangeVar");
            this.whiteVar = tag.getInt("WhiteVar");
            this.tabbyVar = tag.getInt("TabbyVar");
            this.silverVar = tag.getInt("SilverVar");
            this.albinoVar = tag.getInt("AlbinoVar");
            this.leftEyeVar = tag.getInt("LeftEyeVar");
            this.rightEyeVar = tag.getInt("RightEyeVar");

            this.size = tag.getFloat("Size");

            this.scars = tag.getInt("Scars");

            this.rufousingVariant = tag.getInt("Rufousing");
            this.blueRufousingVariant = tag.getInt("BlueTint");
            this.noise = tag.getInt("Noise");
            this.skin_color = tag.getInt("SkinColor");
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeUtf(this.eyeColorLeft);
            buf.writeUtf(this.eyeColorRight);

            buf.writeInt(this.rufousingVariant);
            buf.writeInt(this.blueRufousingVariant);
            buf.writeInt(this.orangeVar);
            buf.writeInt(this.whiteVar);
            buf.writeInt(this.tabbyVar);
            buf.writeInt(this.albinoVar);
            buf.writeInt(this.leftEyeVar);
            buf.writeInt(this.rightEyeVar);
            buf.writeInt(this.noise);
            buf.writeFloat(this.size);
            buf.writeInt(this.silverVar);
            buf.writeInt(this.scars);
            buf.writeInt(this.skin_color);
        }
        public static GeneticalVariants decode(FriendlyByteBuf buf) {
            return new GeneticalVariants(
                    buf.readUtf(),
                    buf.readUtf(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readFloat(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt()
            );
        }
    }

    public static class GeneticalChimeraVariants {
        public int rufousingVariant = 0;
        public int blueRufousingVariant = 0;
        public int orangeVar = 0;
        public int whiteVar = 0;
        public int tabbyVar = 0;
        public int albinoVar = 0;
        public int noise = 0;
        public int silverVar = 0;

        public int chimeraVariant = 0;

        public GeneticalChimeraVariants() {

        }

        public GeneticalChimeraVariants(int chimeraVariant , int rufousingVariant,
                                        int blueRufousingVariant, int orangeVar, int whiteVar,
                                        int tabbyVar, int albinoVar, int noise, int silverVar) {
            this.rufousingVariant = rufousingVariant;
            this.blueRufousingVariant = blueRufousingVariant;
            this.orangeVar = orangeVar;
            this.whiteVar = whiteVar;
            this.chimeraVariant = chimeraVariant;
            this.tabbyVar = tabbyVar;
            this.albinoVar = albinoVar;
            this.noise = noise;
            this.silverVar = silverVar;
        }

        public GeneticalChimeraVariants(GeneticalChimeraVariants copy) {
            this.rufousingVariant = copy.rufousingVariant;
            this.blueRufousingVariant = copy.blueRufousingVariant;
            this.orangeVar = copy.orangeVar;
            this.whiteVar = copy.whiteVar;
            this.chimeraVariant = copy.chimeraVariant;
            this.tabbyVar = copy.tabbyVar;
            this.albinoVar = copy.albinoVar;
            this.noise = copy.noise;
            this.silverVar = copy.silverVar;
        }

        public void storeNBTSubModule(CompoundTag tag) {
            tag.putInt("ChimeraVariant", this.chimeraVariant);

            tag.putInt("OrangeVar", this.orangeVar);
            tag.putInt("WhiteVar", this.whiteVar);
            tag.putInt("TabbyVar", this.tabbyVar);
            tag.putInt("SilverVar", this.silverVar);
            tag.putInt("AlbinoVar", this.albinoVar);

            tag.putInt("Rufousing", this.rufousingVariant);
            tag.putInt("BlueTint", this.blueRufousingVariant);
            tag.putInt("Noise", this.noise);
        }

        public void loadNBTSubModule(CompoundTag tag) {
            this.chimeraVariant = tag.getInt("ChimeraVariant");

            this.orangeVar = tag.getInt("OrangeVar");
            this.whiteVar = tag.getInt("WhiteVar");
            this.tabbyVar = tag.getInt("TabbyVar");
            this.silverVar = tag.getInt("SilverVar");
            this.albinoVar = tag.getInt("AlbinoVar");

            this.rufousingVariant = tag.getInt("Rufousing");
            this.blueRufousingVariant = tag.getInt("BlueTint");
            this.noise = tag.getInt("Noise");
        }


        public void encode(FriendlyByteBuf buf) {
            buf.writeInt(this.chimeraVariant);
            buf.writeInt(this.rufousingVariant);
            buf.writeInt(this.blueRufousingVariant);
            buf.writeInt(this.orangeVar);
            buf.writeInt(this.whiteVar);
            buf.writeInt(this.tabbyVar);
            buf.writeInt(this.albinoVar);
            buf.writeInt(this.noise);
            buf.writeInt(this.silverVar);
        }
        public static GeneticalChimeraVariants decode(FriendlyByteBuf buf) {
            return new GeneticalChimeraVariants(
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt()
            );
        }
    }


    public static class RandomizedGenetics {
        public final WCGenetics genetics;
        public final WCGenetics chimeraGenetics;
        public final WCGenetics.GeneticalVariants variants;
        public final WCGenetics.GeneticalChimeraVariants chimeraVariants;

        public RandomizedGenetics(WCGenetics genetics, WCGenetics chimeraGenetics, GeneticalVariants variants, GeneticalChimeraVariants chimeraVariants) {
            this.genetics = genetics;
            this.chimeraGenetics = chimeraGenetics;
            this.variants = variants;
            this.chimeraVariants = chimeraVariants;
        }

        public static RandomizedGenetics randomize(RandomSource random) {
            WCGenetics genetics = new WCGenetics();
            WCGenetics geneticsChimera = new WCGenetics();
            WCGenetics.GeneticalVariants variants = new GeneticalVariants();
            WCGenetics.GeneticalChimeraVariants variantsChimera = new GeneticalChimeraVariants();
            
            genetics.chestFur = WCGenetics.FurGene.generateAlelo(random) + "-" + WCGenetics.FurGene.generateAlelo(random);
            genetics.bellyFur = WCGenetics.FurGene.generateAlelo(random) + "-" + WCGenetics.FurGene.generateAlelo(random);
            genetics.legsFur = WCGenetics.FurGene.generateAlelo(random) + "-" + WCGenetics.FurGene.generateAlelo(random);
            genetics.headFur = WCGenetics.FurGene.generateAlelo(random) + "-" + WCGenetics.FurGene.generateAlelo(random);
            genetics.cheekFur = WCGenetics.FurGene.generateAlelo(random) + "-" + WCGenetics.FurGene.generateAlelo(random);
            genetics.backFur = WCGenetics.FurGene.generateAlelo(random) + "-" + WCGenetics.FurGene.generateAlelo(random);
            genetics.bobtail = WCGenetics.FurGene.generateAlelo(random) + "-" + WCGenetics.FurGene.generateAlelo(random);
            genetics.tailFur = WCGenetics.FurGene.generateAlelo(random) + "-" + WCGenetics.FurGene.generateAlelo(random);

            genetics.base = WCGenetics.Base.generateAlelo(random) + "-" + WCGenetics.Base.generateAlelo(random);


            genetics.orangeBase = WCGenetics.OrangeBase.generateAlelo(random) + "-" + WCGenetics.OrangeBase.generateAlelo(random);
            variants.orangeVar = random.nextInt(Constants.MAX_TORTIE_VARIANTS);

            genetics.whiteRatio = WCGenetics.WhiteRatio.generateAlelo(random) + "-" + WCGenetics.WhiteRatio.generateAlelo(random);
            variants.whiteVar = random.nextInt(Constants.getWhiteVariants(genetics.whiteRatio));

            genetics.albino = WCGenetics.Albino.generateAlelo(random) + "-" + WCGenetics.Albino.generateAlelo(random);
            variants.albinoVar = random.nextInt(Constants.MAX_ALBINO_VARIANTS);

            genetics.dilute = WCGenetics.Dilute.generateAlelo(random) + "-" + WCGenetics.Dilute.generateAlelo(random);

            genetics.agouti = WCGenetics.Agouti.generateAlelo(random) + "-" + WCGenetics.Agouti.generateAlelo(random);

            genetics.tabbyStripes = WCGenetics.TabbyStripeTypes.generateAlelo(random) + "-" + WCGenetics.TabbyStripeTypes.generateAlelo(random);
            variants.tabbyVar = random.nextInt(Constants.getTabbyVariants(genetics.tabbyStripes));

            genetics.eyesAnomaly = WCGenetics.EyesAnomaly.generateAlelo(random) + "-" + WCGenetics.EyesAnomaly.generateAlelo(random);

            genetics.silver = WCGenetics.Silver.generateAlelo(random) + "-" + WCGenetics.Silver.generateAlelo(random);
            variants.silverVar = random.nextInt(Constants.MAX_SILVER_VARIANTS);

            variants.eyeColorLeft = WCGenetics.EyeColor.generateAlelo(random, genetics.whiteRatio, genetics.albino);
            variants.leftEyeVar = random.nextInt(Constants.MAX_EYE_VARIANTS);

            if (WCGenetics.EyesAnomaly.isHeteroChromic(genetics.eyesAnomaly)) {
                variants.eyeColorRight = WCGenetics.EyeColor.generateAlelo(random, genetics.whiteRatio, genetics.albino);
                variants.rightEyeVar = random.nextInt(Constants.MAX_EYE_VARIANTS);
            } else {
                variants.eyeColorRight = variants.eyeColorLeft;
                variants.rightEyeVar = variants.leftEyeVar;
            }

            variants.noise = random.nextInt(Constants.MAX_NOISE_VARIANTS);

            variants.skin_color = Constants.getSkinShade(genetics.whiteRatio, genetics.base,
                    genetics.agouti, genetics.orangeBase, 1, random);

            if (WCGenetics.Base.isBlack(genetics.base)) {
                variants.rufousingVariant = random.nextInt(3);
            } else {
                variants.rufousingVariant = random.nextInt(Constants.MAX_RUFOUSING_VARIANTS);
            }

            if (WCGenetics.Dilute.isDilute(genetics.dilute)) {
                variants.blueRufousingVariant = random.nextInt(3);
            } else {
                variants.blueRufousingVariant = random.nextInt(Constants.MAX_BLUE_RUFOUSING_VARIANTS);
            }

            genetics.chimeraGene = WCGenetics.Chimerism.generateAlelo(random) + "-" + WCGenetics.Chimerism.generateAlelo(random);

            if (WCGenetics.Chimerism.isChimera(genetics.chimeraGene)) {
                geneticsChimera.base = WCGenetics.Base.generateAlelo(random) + "-" + WCGenetics.Base.generateAlelo(random);

                variantsChimera.chimeraVariant = random.nextInt(Constants.MAX_CHIMERISM_VARIANTS);

                geneticsChimera.orangeBase = WCGenetics.OrangeBase.generateAlelo(random) + "-" + WCGenetics.OrangeBase.generateAlelo(random);
                variantsChimera.orangeVar = random.nextInt(Constants.MAX_TORTIE_VARIANTS);

                geneticsChimera.whiteRatio = WCGenetics.WhiteRatio.generateAlelo(random) + "-" + WCGenetics.WhiteRatio.generateAlelo(random);
                variantsChimera.whiteVar = random.nextInt(Constants.getWhiteVariants(geneticsChimera.whiteRatio));

                geneticsChimera.albino = WCGenetics.Albino.generateAlelo(random) + "-" + WCGenetics.Albino.generateAlelo(random);
                variantsChimera.albinoVar = random.nextInt(Constants.MAX_ALBINO_VARIANTS);

                geneticsChimera.dilute = WCGenetics.Dilute.generateAlelo(random) + "-" + WCGenetics.Dilute.generateAlelo(random);

                geneticsChimera.agouti = WCGenetics.Agouti.generateAlelo(random) + "-" + WCGenetics.Agouti.generateAlelo(random);

                geneticsChimera.tabbyStripes = WCGenetics.TabbyStripeTypes.generateAlelo(random) + "-" + WCGenetics.TabbyStripeTypes.generateAlelo(random);
                variantsChimera.tabbyVar = random.nextInt(Constants.getTabbyVariants(geneticsChimera.tabbyStripes));

                geneticsChimera.silver = WCGenetics.Silver.generateAlelo(random) + "-" + WCGenetics.Silver.generateAlelo(random);
                variantsChimera.silverVar = random.nextInt(Constants.MAX_SILVER_VARIANTS);

                genetics.eyesAnomaly = "h-h";
                variants.eyeColorLeft = WCGenetics.EyeColor.generateAlelo(random, geneticsChimera.whiteRatio, geneticsChimera.albino);
                variants.leftEyeVar = random.nextInt(Constants.MAX_EYE_VARIANTS);

                if (WCGenetics.EyesAnomaly.isHeteroChromic(genetics.eyesAnomaly)) {
                    variants.eyeColorRight = WCGenetics.EyeColor.generateAlelo(random, geneticsChimera.whiteRatio, geneticsChimera.albino);
                    variants.rightEyeVar = random.nextInt(Constants.MAX_EYE_VARIANTS);
                } else {
                    variants.eyeColorRight = variants.eyeColorLeft;
                    variants.rightEyeVar = variants.leftEyeVar;
                }

                variantsChimera.noise = random.nextInt(Constants.MAX_NOISE_VARIANTS);

                if (WCGenetics.Base.isBlack(geneticsChimera.base)) {
                    variantsChimera.rufousingVariant = random.nextInt(3);
                } else {
                    variantsChimera.rufousingVariant = random.nextInt(Constants.MAX_BLUE_RUFOUSING_VARIANTS);
                }

                if (WCGenetics.Dilute.isDilute(geneticsChimera.dilute)) {
                    variantsChimera.blueRufousingVariant = random.nextInt(3);
                } else {
                    variantsChimera.blueRufousingVariant = random.nextInt(Constants.MAX_BLUE_RUFOUSING_VARIANTS);
                }
            }

            return new RandomizedGenetics(genetics, geneticsChimera, variants, variantsChimera);
        }
    }


    public enum Bobtail {
        FULL("B"),
        BOBTAIL("b");

        private final String allele;

        Bobtail(String allele) {
            this.allele = allele;
        }

        public String getAllele() {
            return allele;
        }

        public static String init(RandomSource random) {
            String a1 = random.nextFloat() < 0.70F ? FULL.allele : BOBTAIL.allele;
            String a2 = random.nextFloat() < 0.70F ? FULL.allele : BOBTAIL.allele;
            return a1 + "-" + a2;
        }

        public static boolean isBobtail(String alleles) {
            return alleles.equals("b-b");
        }
    }

    public enum FurGene {

        DOMINANT("L"),
        RECESSIVE("s");

        private final String allele;

        FurGene(String allele) {
            this.allele = allele;
        }

        public String getAllele() {
            return allele;
        }

        public static String generateAlelo(RandomSource random) {
            float chance = random.nextFloat();
            if (chance < 0.2) {
                return DOMINANT.getAllele();
            } else {
                return RECESSIVE.getAllele();
            }
        }

        public static boolean isLongFur(String genotype) {
            return genotype.contains("L");
        }
    }

    public enum Base {
        BLACK("B"),
        CHOCOLATE("b"),
        CINNAMON("b1"),

        ;
        private String alelo;

        Base(String alelo) {
            this.alelo = alelo;
        }

        public String getAlelo() {
            return alelo;
        }

        public static String generateAlelo(RandomSource random) {
            float chance = random.nextFloat();
            if (chance < 0.33f) {
                return BLACK.getAlelo();
            } else if (chance < 0.67f){
                return CHOCOLATE.getAlelo();
            } else {
                return CINNAMON.getAlelo();
            }
        }

        public static boolean isBlack(String genotype) {
            return genotype.contains("B");
        }
        public static boolean isChocolate(String genotype) {
            if (genotype == null || genotype.isEmpty()) return false;
            for (String allele : genotype.split("-")) {
                if (allele.equals("b")) return true;
            }
            return false;
        }
        public static boolean isCinnamon(String genotype) {
            return genotype.equals("b1-b1");
        }

    }

    public enum OrangeBase {
        ORANGE("O"),
        NOT_ORANGE("o"),

        ;
        private String alelo;

        OrangeBase(String alelo) {
            this.alelo = alelo;
        }

        public String getAlelo() {
            return alelo;
        }

        public static String generateAlelo(RandomSource random) {
            float chance = random.nextFloat();
            if (chance < 0.32f) {
                return ORANGE.getAlelo();
            } else {
                return NOT_ORANGE.getAlelo();
            }
        }

        public static boolean isOrange(String genotype, int gender) {
            return (gender == 0 && genotype.contains("O"))
                    || (gender == 1 && genotype.equals("O-O"));
        }

        public static boolean isTortoiseshell(String genotype) {
            return genotype.contains("O") && genotype.contains("o");
        }

    }

    public enum WhiteRatio {
        WHITE("Wd"),
        SPOTTING("S"),
        NO_WHITE("w"),

        ;
        private String alelo;

        WhiteRatio(String alelo) {
            this.alelo = alelo;
        }

        public String getAlelo() {
            return alelo;
        }

        public static String generateAlelo(RandomSource random) {
            float chance = random.nextFloat();
            if (chance < 0.10f) {
                return WHITE.getAlelo();
            } else if (chance < 0.30f) {
                return SPOTTING.getAlelo();
            } else {
                return  NO_WHITE.getAlelo();
            }
        }

        public static boolean isWhite(String genotype) {
            return genotype.contains("Wd");
        }

        public static boolean isHighSpotted(String genotype) {
            return genotype.equals("S-S");
        }

        public static boolean isLowSpotted(String genotype) {
            return genotype.contains("S") && genotype.contains("w");
        }

    }

    public enum Albino {
        NOT_ALBINO("C"),
        SIAMESE("cs"),
        SEPIA("cb"),
        TRUE_ALBINO("c"),

        ;
        private String alelo;

        Albino(String alelo) {
            this.alelo = alelo;
        }

        public String getAlelo() {
            return alelo;
        }

        public static String generateAlelo(RandomSource random) {
            float chance = random.nextFloat();
            if (chance < 0.05f) {
                return TRUE_ALBINO.getAlelo();
            } else if (chance < 0.15f) {
                return SEPIA.getAlelo();
            } else if (chance < 0.25f) {
                return SIAMESE.getAlelo();
            } else {
                return NOT_ALBINO.getAlelo();
            }
        }

        private static String[] alleles(String genotype) {
            return genotype.split("-");
        }

        private static boolean hasAllele(String genotype, String allele) {
            for (String a : alleles(genotype)) {
                if (a.equals(allele)) return true;
            }
            return false;
        }

        public static boolean isNotAlbino(String genotype) {
            return hasAllele(genotype, "C");
        }

        public static boolean isSiamese(String genotype) {
            return hasAllele(genotype, "cs") && (hasAllele(genotype, "c") || hasAllele(genotype, "cs"));
        }

        public static boolean isMink(String genotype) {
            return hasAllele(genotype, "cs") && hasAllele(genotype, "cb");
        }

        public static boolean isSepia(String genotype) {
            return hasAllele(genotype, "cb") && (hasAllele(genotype, "c") || hasAllele(genotype, "cb"));
        }

        public static boolean isTrueAlbino(String genotype) {
            return genotype.equals("c-c");
        }

    }

    public enum Dilute {
        DILUTE("D"),
        NON_DILUTE("d"),

        ;
        private String alelo;

        Dilute(String alelo) {
            this.alelo = alelo;
        }

        public String getAlelo() {
            return alelo;
        }

        public static String generateAlelo(RandomSource random) {
            float chance = random.nextFloat();
            if (chance < 0.3f) {
                return DILUTE.getAlelo();
            } else {
                return NON_DILUTE.getAlelo();
            }
        }

        public static boolean isDilute(String genotype) {
            return genotype.contains("D");
        }

    }

    public enum Agouti {
        TABBY("A"),
        NON_TABBY("a"),

        ;
        private String alelo;

        Agouti(String alelo) {
            this.alelo = alelo;
        }

        public String getAlelo() {
            return alelo;
        }

        public static String generateAlelo(RandomSource random) {
            float chance = random.nextFloat();
            if (chance < 0.2f) {
                return TABBY.getAlelo();
            } else {
                return NON_TABBY.getAlelo();
            }
        }

        public static boolean isTabby(String genotype) {
            return genotype.contains("A");
        }

    }

    public enum TabbyStripeTypes {
        MACKEREL("Mc"),
        CLASSIC("mc"),

        ;
        private String alelo;

        TabbyStripeTypes(String alelo) {
            this.alelo = alelo;
        }

        public String getAlelo() {
            return alelo;
        }

        public static String generateAlelo(RandomSource random) {
            float chance = random.nextFloat();
            if (chance < 0.25f) {
                return MACKEREL.getAlelo();
            } else {
                return CLASSIC.getAlelo();
            }
        }

        public static boolean isMackerel(String genotype) {
            return genotype.contains("Mc");
        }

        public static boolean isClassic(String genotype) {
            return genotype.equals("mc-mc");
        }
    }

    public enum EyeColor {
        YELLOW("yellow"),
        BLUE("blue"),
        GREEN("green"),
        RED("red"),
        BLIND("blind"),

        ;
        private String alelo;

        EyeColor(String alelo) {
            this.alelo = alelo;
        }

        public String getAlelo() {
            return alelo;
        }

        public static String generateAlelo(RandomSource random, String whiteGenotype, String albinoGenotype) {

            if (Albino.isTrueAlbino(albinoGenotype)) {
                return RED.getAlelo();
            }

            if (Albino.isSiamese(albinoGenotype)) {
                return BLUE.getAlelo();
            }

            if (Albino.isMink(albinoGenotype)) {
                if (whiteGenotype.equals("w-w")) {
                    if (random.nextInt(2) == 0) {
                        return GREEN.getAlelo();
                    } else {
                        return BLUE.getAlelo();
                    }
                } else {
                    return GREEN.getAlelo();
                }
            }


            if (whiteGenotype.equals("w-w")) {
                if (random.nextInt(2) == 0) {
                    return GREEN.getAlelo();
                } else {
                    return YELLOW.getAlelo();
                }
            } else {
                int chance2 = random.nextInt(3);
                if (chance2 == 0) {
                    return GREEN.getAlelo();
                } else if (chance2 == 1) {
                    return BLUE.getAlelo();
                } else {
                    return YELLOW.getAlelo();
                }
            }
        }

        public static EyeColor getEyeColor(String genotype) {
            switch (genotype) {
                case "yellow" -> {
                    return  YELLOW;
                }
                case "blue" -> {
                    return  BLUE;
                }
                case "green" -> {
                    return  GREEN;
                }

                case "red" -> {
                    return RED;
                }

                case "blind" -> {
                    return BLIND;
                }

                default -> {
                    return YELLOW;
                }
            }
        }

    }

    public enum EyesAnomaly {
        HETEROCHROMIA("h"),
        NORMAL("H")

        ;
        private String alelo;

        EyesAnomaly(String alelo) {
            this.alelo = alelo;
        }

        public String getAlelo() {
            return alelo;
        }

        public static String generateAlelo(RandomSource random) {
            float chance = random.nextFloat();

            if (chance < 0.1f) {
                return HETEROCHROMIA.getAlelo();
            } else {
                return  NORMAL.getAlelo();
            }
        }


        public static boolean isHeteroChromic(String genotype) {
            return genotype.equals("h-h");
        }

    }

    public enum Chimerism {
        CHIMERA("c"),
        NORMAL("C")

        ;
        private String alelo;

        Chimerism(String alelo) {
            this.alelo = alelo;
        }

        public String getAlelo() {
            return alelo;
        }

        public static String generateAlelo(RandomSource random) {
            float chance = random.nextFloat();

            if (chance < 0.1f) {
                return CHIMERA.getAlelo();
            } else {
                return  NORMAL.getAlelo();
            }
        }

        public static boolean isChimera(String genotype) {
            return genotype.equals("c-c");
        }

    }

    public enum Silver {
        SILVER("I"),
        NON_SILVER("i")

        ;
        private String alelo;

        Silver(String alelo) {
            this.alelo = alelo;
        }

        public String getAlelo() {
            return alelo;
        }

        public static String generateAlelo(RandomSource random) {
            float chance = random.nextFloat();

            if (chance < 0.08f) {
                return SILVER.getAlelo();
            } else {
                return  NON_SILVER.getAlelo();
            }
        }

        private static String[] alleles(String genotype) {
            return genotype.split("-");
        }

        private static boolean hasAllele(String genotype, String allele) {
            for (String a : alleles(genotype)) {
                if (a.equals(allele)) return true;
            }
            return false;
        }

        public static boolean isSilver(String genotype, String agouti, String orange, int gender) {
            return (hasAllele(genotype, "I")) && (Agouti.isTabby(agouti) || OrangeBase.isOrange(orange, gender));
        }

        public static boolean isSmokeTortie(String genotype, String agouti, String orange) {
            return (hasAllele(genotype, "I")) && !Agouti.isTabby(agouti) && OrangeBase.isTortoiseshell(orange);
        }

        public static boolean isSmoke(String genotype, String agouti) {
            return (hasAllele(genotype, "I")) && !Agouti.isTabby(agouti);
        }

    }


    public static String encodeGene(String gene) {
        StringBuilder sb = new StringBuilder();

        for (char c : gene.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append("u").append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static class PackedGeneticData {
        public final WCGenetics genetics;
        public final WCGenetics.GeneticalVariants variants;
        public final WCGenetics chimerasGenetics;
        public final WCGenetics.GeneticalChimeraVariants chimeraVariants;

        public final boolean onGeneticalSkin;
        public final int morphSkin;

        public PackedGeneticData(WCGenetics genetics, WCGenetics.GeneticalVariants variants,
                                 WCGenetics chimerasGenetics, WCGenetics.GeneticalChimeraVariants chimeraVariants, boolean onGeneticalSkin, int morphSkin) {
            this.genetics = genetics;
            this.variants = variants;

            this.chimerasGenetics = chimerasGenetics;
            this.chimeraVariants = chimeraVariants;

            this.onGeneticalSkin = onGeneticalSkin;
            this.morphSkin = morphSkin;
        }

        public static PackedGeneticData empty() {
            return new PackedGeneticData(new WCGenetics(), new WCGenetics.GeneticalVariants(),
                    new WCGenetics(), new WCGenetics.GeneticalChimeraVariants(),
                    false, 0);
        }
    }

    public static void writeGeneticsNBT(CompoundTag morphData, WCGenetics genetics, GeneticalVariants variants,
                                        WCGenetics chimeraGenetics, GeneticalChimeraVariants chimeraVariants,
                                        boolean isOnGeneticalSkin, int morphVariant) {
        morphData.putBoolean("Genetical", isOnGeneticalSkin);
        morphData.putInt("MorphVariant", morphVariant);

        morphData.putString("ChestFur", genetics.chestFur);
        morphData.putString("BellyFur", genetics.bellyFur);
        morphData.putString("LegsFur", genetics.legsFur);
        morphData.putString("HeadFur", genetics.headFur);
        morphData.putString("CheekFur", genetics.cheekFur);
        morphData.putString("BackFur", genetics.backFur);
        morphData.putString("TailFur", genetics.tailFur);
        morphData.putString("Bobtail", genetics.bobtail);

        morphData.putString("Base", genetics.base);
        morphData.putString("OrangeBase", genetics.orangeBase);
        morphData.putString("WhiteRatio", genetics.whiteRatio);
        morphData.putString("Albino", genetics.albino);
        morphData.putString("Dilute", genetics.dilute);
        morphData.putString("Agouti", genetics.agouti);
        morphData.putString("TabbyStripes", genetics.tabbyStripes);
        morphData.putString("EyesAnomaly", genetics.eyesAnomaly);
        morphData.putString("Silver", genetics.silver);

        morphData.putString("EyeColorLeft", variants.eyeColorLeft);
        morphData.putString("EyeColorRight", variants.eyeColorRight);
        morphData.putInt("Rufousing", variants.rufousingVariant);
        morphData.putInt("BlueRufousing", variants.blueRufousingVariant);
        morphData.putInt("OrangeBaseVariant", variants.orangeVar);
        morphData.putInt("WhiteRatioVariant", variants.whiteVar);
        morphData.putInt("AlbinoVariant", variants.albinoVar);
        morphData.putInt("TabbyStripesVariant", variants.tabbyVar);
        morphData.putInt("EyeColorVariantLeft", variants.leftEyeVar);
        morphData.putInt("EyeColorVariantRight", variants.rightEyeVar);
        morphData.putInt("Noise", variants.noise);
        morphData.putInt("SilverVariant", variants.silverVar);
        morphData.putFloat("Size", variants.size);
        morphData.putInt("Scars", variants.scars);
        morphData.putInt("SkinColor", variants.skin_color);

        morphData.putString("BaseChimera", chimeraGenetics.base);
        morphData.putString("OrangeBaseChimera", chimeraGenetics.orangeBase);
        morphData.putString("WhiteRatioChimera", chimeraGenetics.whiteRatio);
        morphData.putString("AlbinoChimera", chimeraGenetics.albino);
        morphData.putString("DiluteChimera", chimeraGenetics.dilute);
        morphData.putString("AgoutiChimera", chimeraGenetics.agouti);
        morphData.putString("TabbyStripesChimera", chimeraGenetics.tabbyStripes);
        morphData.putString("SilverChimera", chimeraGenetics.silver);
        morphData.putInt("RufousingChimera", chimeraVariants.rufousingVariant);
        morphData.putInt("BlueRufousingChimera", chimeraVariants.blueRufousingVariant);
        morphData.putString("ChimeraGene", chimeraGenetics.chimeraGene);


        morphData.putInt("OrangeBaseVariantChimera", chimeraVariants.orangeVar);
        morphData.putInt("WhiteRatioVariantChimera", chimeraVariants.whiteVar);
        morphData.putInt("AlbinoVariantChimera", chimeraVariants.albinoVar);
        morphData.putInt("TabbyStripesVariantChimera", chimeraVariants.tabbyVar);
        morphData.putInt("NoiseChimera", chimeraVariants.noise);
        morphData.putInt("SilverVariantChimera", chimeraVariants.silverVar);
    }

    public static PackedGeneticData loadGeneticsNBT(CompoundTag morphData) {

        boolean genetical = morphData.getBoolean("Genetical");
        int morphVariant = morphData.getInt("MorphVariant");

        WCGenetics genetics = new WCGenetics();

        genetics.chestFur = morphData.getString("ChestFur");
        genetics.bellyFur = morphData.getString("BellyFur");
        genetics.legsFur = morphData.getString("LegsFur");
        genetics.headFur = morphData.getString("HeadFur");
        genetics.cheekFur = morphData.getString("CheekFur");
        genetics.backFur = morphData.getString("BackFur");
        genetics.tailFur = morphData.getString("TailFur");
        genetics.bobtail = morphData.getString("Bobtail");
        genetics.base = morphData.getString("Base");
        genetics.orangeBase = morphData.getString("OrangeBase");
        genetics.whiteRatio = morphData.getString("WhiteRatio");
        genetics.albino = morphData.getString("Albino");
        genetics.dilute = morphData.getString("Dilute");
        genetics.agouti = morphData.getString("Agouti");
        genetics.tabbyStripes = morphData.getString("TabbyStripes");
        genetics.eyesAnomaly = morphData.getString("EyesAnomaly");
        genetics.silver = morphData.getString("Silver");

        WCGenetics.GeneticalVariants variants = new WCGenetics.GeneticalVariants();

        variants.eyeColorLeft = morphData.getString("EyeColorLeft");
        variants.eyeColorRight = morphData.getString("EyeColorRight");
        variants.rufousingVariant = morphData.getInt("Rufousing");
        variants.blueRufousingVariant = morphData.getInt("BlueRufousing");
        variants.orangeVar = morphData.getInt("OrangeBaseVariant");
        variants.whiteVar = morphData.getInt("WhiteRatioVariant");
        variants.albinoVar = morphData.getInt("AlbinoVariant");
        variants.tabbyVar = morphData.getInt("TabbyStripesVariant");
        variants.leftEyeVar = morphData.getInt("EyeColorVariantLeft");
        variants.rightEyeVar = morphData.getInt("EyeColorVariantRight");
        variants.noise = morphData.getInt("Noise");
        variants.silverVar = morphData.getInt("SilverVariant");
        variants.size = morphData.getFloat("Size");
        variants.scars = morphData.getInt("Scars");
        variants.skin_color = morphData.getInt("SkinColor");

        WCGenetics chimeraGenetics = new WCGenetics();

        chimeraGenetics.base = morphData.getString("BaseChimera");
        chimeraGenetics.orangeBase = morphData.getString("OrangeBaseChimera");
        chimeraGenetics.whiteRatio = morphData.getString("WhiteRatioChimera");
        chimeraGenetics.albino = morphData.getString("AlbinoChimera");
        chimeraGenetics.dilute = morphData.getString("DiluteChimera");
        chimeraGenetics.agouti = morphData.getString("AgoutiChimera");
        chimeraGenetics.tabbyStripes = morphData.getString("TabbyStripesChimera");
        chimeraGenetics.silver = morphData.getString("SilverChimera");
        chimeraGenetics.chimeraGene = morphData.getString("ChimeraGene");

        WCGenetics.GeneticalChimeraVariants chimeraVariants = new WCGenetics.GeneticalChimeraVariants();

        chimeraVariants.orangeVar = morphData.getInt("OrangeBaseVariantChimera");
        chimeraVariants.whiteVar = morphData.getInt("WhiteRatioVariantChimera");
        chimeraVariants.albinoVar = morphData.getInt("AlbinoVariantChimera");
        chimeraVariants.tabbyVar = morphData.getInt("TabbyStripesVariantChimera");
        chimeraVariants.noise = morphData.getInt("NoiseChimera");
        chimeraVariants.silverVar = morphData.getInt("SilverVariantChimera");
        chimeraVariants.rufousingVariant = morphData.getInt("RufousingChimera");
        chimeraVariants.blueRufousingVariant = morphData.getInt("BlueRufousingChimera");

        return new PackedGeneticData(
                genetics,
                variants,
                chimeraGenetics,
                chimeraVariants,
                genetical,
                morphVariant
        );
    }

}
