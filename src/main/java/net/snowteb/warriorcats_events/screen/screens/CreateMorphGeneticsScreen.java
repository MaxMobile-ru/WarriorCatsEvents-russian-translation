package net.snowteb.warriorcats_events.screen.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.client.ClientClanData;
import net.snowteb.warriorcats_events.client.ClientStoredMorphs;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.cats.SummonCustomCatPacket;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.SavePlayerGeneticsPacket;
import net.snowteb.warriorcats_events.network.packet.c2s.others.CtSShareMorphToChat;
import net.snowteb.warriorcats_events.screen.widgets.GradientSwitchButton;
import net.snowteb.warriorcats_events.screen.widgets.*;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;
import tocraft.walkers.api.PlayerShape;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import static net.snowteb.warriorcats_events.screen.screens.CreateClanScreen.BANNER;
import static net.snowteb.warriorcats_events.screen.screens.CreateClanScreen.BG_TEXTURE;

public class CreateMorphGeneticsScreen extends Screen {

    private int displayErrorTime = 0;
    private String displayErrorText = "";
    private boolean isAnError = false;

    private float animationTime = 0f;
    private float duration = 20f;
    private boolean closing = false;

    private final float startX = 0;
    private final float endX = -700;
    private float menuX = 0;


    private String eyeColorLeft = "green";
    private String eyeColorRight = "green";

    private int rufousingVariant = 0;
    private int blueRufousingVariant = 0;

    private int orangeBaseVariant = 0;
    private int whiteRatioVariant = 0;
    private int tabbyStripesVariant = 0;
    private int albinoVariant = 0;
    private int eyeColorVariantLeft = 0;
    private int eyeColorVariantRight = 0;
    private int noise = 0;
    private int silverVar = 0;
    private float size = 0;
    private int scars = 0;

    private String chimeraGene = setNotChimera;


    // BASE SECTION
    // ORANGE SECTION
    // WHITE SECTION
    // ALBINO SECTION
    // DILUTE SECTION
    // AGOUTI AND TABBY SECTION
    // EYES AND EYES ANOMALY SECTION
    // FUR AND BOBTAIL AND NOISE SECTION

    private GradientButton newMorphButton;
    private GradientButton randomizeGeneticsButton;


    private VariantScrollList variantScrollList;
    private FloatSliderButton sizeSliderPreset;

    private String storedMorphsKey = "";
    private GradientButton storedMorphsMenu;
    private boolean onStoredMorphsMenu = false;
    private GradientButton saveStoredMorph;
    private EditBox saveMorphNameBox;
    private GradientButton cancelMorphSave;
    private GradientButton confirmSaveMorph;
    private GradientButton loadStoredMorph;
    private MorphsScrollList savedMorphsScrollList;
    private GradientButton loadSelectedMorphButton;
    private GradientButton cancelMorphLoad;
    private GradientButton backFromStoredMorphMenu;

    private GradientButton removeSelectedMorph;
    private boolean removeSelectedMorphConfirm = false;
    private GradientButton overwriteSelectedMorph;
    private GradientButton sendSelectedMorphToChat;
    private boolean overwriteSelectedMorphConfirm = false;


    private String mainSectionActiveMenu = "";
    private GradientToggleButton baseSection;
    private GradientToggleButton orangeSection;
    private GradientToggleButton whiteSection;
    private GradientToggleButton albinoSection;
    private GradientToggleButton diluteSection;
    private GradientToggleButton agoutiAndTabbySection;
    private GradientToggleButton eyesAndAnomalySection;
    private GradientToggleButton furBobtailNoiseSection;
    private GradientToggleButton extraSection;
    private GradientToggleButton silverSection;

    private GradientToggleButton chimeraSection;
    private GradientSwitchButton chimeraSwitch;
    private GradientToggleButton editChimera;


    private GradientSwitchButton bobtailSwitch;
    private GradientSwitchButton chestFurSwitch;
    private GradientSwitchButton bellyFurSwitch;
    private GradientSwitchButton legsFurSwitch;
    private GradientSwitchButton headFurSwitch;
    private GradientSwitchButton cheekFurSwitch;
    private GradientSwitchButton backFurSwitch;
    private GradientSwitchButton tailFurSwitch;
    private VariantScrollList noiseList;
    private FloatSliderButton sizeSlider;
    private IntSliderButton rufousingSlider;
    private IntSliderButton blueRufousingSlider;

    private GradientSwitchButton silverSwitch;
    private VariantScrollList silverVariantList;

    private VariantScrollList scarsList;

    private GradientToggleButton setBlackButton;
    private GradientToggleButton setChocolateButton;
    private GradientToggleButton setCinnamonButton;

    private GradientToggleButton setOrangeButton;
    private GradientToggleButton setTortieButton;
    private GradientToggleButton setNotOrangeButton;
    private VariantScrollList orangeVariantList;

    private GradientToggleButton setFullWhiteButton;
    private GradientToggleButton setHighWhiteButton;
    private GradientToggleButton setLowWhiteButton;
    private GradientToggleButton setNoWhiteButton;
    private VariantScrollList whiteVariantList;

    private GradientToggleButton setNotAlbinoButton;
    private GradientToggleButton setTrueAlbinoButton;
    private GradientToggleButton setSepiaButton;
    private GradientToggleButton setMinkButton;
    private GradientToggleButton setSiameseButton;
    private VariantScrollList albinoVariantList;


    private GradientSwitchButton diluteSwitch;

    private GradientSwitchButton agoutiSwitch;
    private GradientToggleButton setMackerelButton;
    private GradientToggleButton setClassicButton;
    private VariantScrollList tabbyVariantList;



    private GradientSwitchButton heterochromiaSwitch;

    private GradientToggleButton setLeftEyeBlueButton;
    private GradientToggleButton setLeftEyeGreenButton;
    private GradientToggleButton setLeftEyeYellowButton;
    private GradientToggleButton setLeftEyeRedButton;
    private GradientToggleButton setLeftEyeBlindButton;
    private VariantScrollList leftEyeVariantList;

    private GradientToggleButton setRightEyeBlueButton;
    private GradientToggleButton setRightEyeGreenButton;
    private GradientToggleButton setRightEyeYellowButton;
    private GradientToggleButton setRightEyeRedButton;
    private GradientToggleButton setRightEyeBlindButton;
    private VariantScrollList rightEyeVariantList;



    // 1 SWITCH BUTTON FOR TAIL
    private static final String setBobtail = "b-b";
    private static final String setFulltail = "B-b";

    // 1 SWITCH BUTTON FOR EACH PART
    private static final String setShortFur = "s-s";
    private static final String setLongFur = "L-s";


    // 3 TOGGLE BUTTON
    private static final String setBlack = "B-b";
    private static final String setChocolate = "b-b1";
    private static final String setCinnamon = "b1-b1";


    // 3 TOGGLE BUTTON
    private static final String setOrange = "O-O";
    private static final String setTortie = "O-o";
    private static final String setNotOrange = "o-o";
    // 1 SCROLL LIST FOR TORTIE VARIANT


    // 4 TOGGLE BUTTON
    private static final String setFullWhite = "Wd-w";
    private static final String setHighWhite = "S-S";
    private static final String setLowWhite = "S-w";
    private static final String setNotWhite = "w-w";
    // 1 SCROLL LIST


    // 5 TOGGLE BUTTON
    private static final String setNotAlbino = "C-cs";

    private static final String setTrueAlbino = "c-c";
    private static final String setSepia = "cb-c";
    private static final String setMink = "cs-cb";
    private static final String setSiamese = "cs-c";
    // 1 SCROLL MENU


    // 1 SWITCH BUTTON
    private static final String setDilute = "D-d";
    private static final String setNonDilute = "d-d";


    // 1 SWITCH BUTTON
    private static final String setAgoutiTabby = "A-a";
    private static final String setNonAgoutiTabby = "a-a";

    // 2 TOGGLE BUTTON
    private static final String setTabbyStripesMackerel = "Mc-mc";
    private static final String setTabbyStripesClassic = "mc-mc";
    // 1 SCROLL MENU

    // 1 SWITCH BUTTON
    private static final String setSilver = "I-i";
    private static final String setNonSilver = "i-i";
    // 1 SCROLL MENU

    // 3 TOGGLE BUTTON FOR EACH EYE
    private static final String setEyeBlue = "blue";
    private static final String setEyeYellow = "yellow";
    private static final String setEyeGreen = "green";
    private static final String setEyeRed = "red";
    private static final String setEyeBlind = "blind";
    // 1 SCROLL MENUS FOR EACH EYE

    // 1 SWITCH BUTTON
    private static final String setHeteroChromia = "h-h";
    private static final String setNonHeteroChromia = "H-h";

    private static final String setChimera = "c-c";
    private static final String setNotChimera = "C-C";



    private boolean onGeneticalSkin = false;
    private GradientToggleButton setOnGeneticalSkinButton;
    private GradientToggleButton setOnPresetSkinButton;
    private GradientToggleButton saveButton;


    WCGenetics genetics = new WCGenetics();
    WCGenetics geneticsChimera = new WCGenetics();
    WCGenetics.GeneticalChimeraVariants variantsChimera = new WCGenetics.GeneticalChimeraVariants();

    private final boolean isSummoning;
    public CreateMorphGeneticsScreen(boolean isSummoning) {
        super(Component.literal("Create Morph Genetics"));
        this.isSummoning = isSummoning;
    }

    private boolean comingBackFromChimeraMenu = false;
    private boolean goingToChimeraMenu = false;


    private String chestFur = setShortFur;
    private String bellyFur = setShortFur;
    private String legsFur = setShortFur;
    private String headFur = setShortFur;
    private String cheekFur = setShortFur;
    private String backFur = setShortFur;
    private String tailFur = setShortFur;

    private String bobtail = setFulltail;

    private String base = setBlack;
    private String orangeBase = setNotOrange;
    private String whiteRatio = setNotWhite;
    private String albino = setNotAlbino;
    private String dilute = setNonDilute;
    private String silver = setNonSilver;
    private String agouti = setNonAgoutiTabby;
    private String tabbyStripes = setTabbyStripesMackerel;
    private String eyesAnomaly = setNonHeteroChromia;
    private int rufousing = 0;
    private int blueRufousing = 0;
    private int storedNoise = 0;


    public CreateMorphGeneticsScreen(boolean isOnGeneticalSkin, WCGenetics genetics, WCGenetics.GeneticalVariants variants,
        WCGenetics chimeraGenetics, WCGenetics.GeneticalChimeraVariants chimeraVariants, boolean isSummoning) {
        super(Component.literal("Create Morph Genetics"));
        this.isSummoning = isSummoning;

        onGeneticalSkin = isOnGeneticalSkin;

        chestFur = genetics.chestFur;
        bellyFur = genetics.bellyFur;
        legsFur = genetics.legsFur;
        headFur = genetics.headFur;
        cheekFur = genetics.cheekFur;
        backFur = genetics.backFur;
        tailFur = genetics.tailFur;
        bobtail = genetics.bobtail;
        base = genetics.base;
        orangeBase = genetics.orangeBase;
        whiteRatio = genetics.whiteRatio;
        albino = genetics.albino;
        dilute = genetics.dilute;
        agouti = genetics.agouti;
        tabbyStripes = genetics.tabbyStripes;
        eyesAnomaly = genetics.eyesAnomaly;
        rufousing = genetics.rufousing;
        blueRufousing = genetics.blueRufousing;
        storedNoise = genetics.noise;
        silver = genetics.silver;

        comingBackFromChimeraMenu = true;

        eyeColorLeft = variants.eyeColorLeft;
        eyeColorRight = variants.eyeColorRight;
        rufousingVariant = variants.rufousingVariant;
        blueRufousingVariant = variants.blueRufousingVariant;
        orangeBaseVariant = variants.orangeVar;
        whiteRatioVariant = variants.whiteVar;
        tabbyStripesVariant = variants.tabbyVar;
        albinoVariant = variants.albinoVar;
        eyeColorVariantLeft = variants.leftEyeVar;
        eyeColorVariantRight = variants.rightEyeVar;
        storedNoise = variants.noise;
        size = variants.size;

        geneticsChimera = chimeraGenetics;
        variantsChimera = chimeraVariants;

        chimeraGene = setChimera;

    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        {
            int listWidth = 100;
            int listHeight = 40;
            int top = centerY - 35;
            int bottom = centerY + 110;

            this.variantScrollList = new VariantScrollList(
                    Minecraft.getInstance(),
                    listWidth,
                    listHeight,
                    top,
                    bottom,
                    30
            );
//            this.variantScrollList.setRenderTopAndBottom(false);
            this.variantScrollList.setX(((this.width - 140)));

            variantScrollList.addOption("Calico", 0);
            variantScrollList.addOption("Siamese", 1);
            variantScrollList.addOption("Gray", 2);
            variantScrollList.addOption("Abyssinian", 3);
            variantScrollList.addOption("Black", 4);
            variantScrollList.addOption("Maine Coon", 5);
            variantScrollList.addOption("Russian Blue", 6);
            variantScrollList.addOption("Dark Brown Tabby", 7);
            variantScrollList.addOption("White", 8);
            variantScrollList.addOption("Calico 2", 9);
            variantScrollList.addOption("Munchkin", 10);
            variantScrollList.addOption("Light Gray Tabby", 11);
            variantScrollList.addOption("Chestnutpatch (Bookwom)", 12);
            variantScrollList.addOption("Ratstar (Telefonjoker)", 13);
            variantScrollList.addOption("Twitchstream (Cat)", 14);
            variantScrollList.addOption("Blazepit (Cat)", 15);
            variantScrollList.addOption("Bengalpelt (Klyonstar)", 16);
            variantScrollList.addOption("Sparrowstar (Whale_shark)", 17);
            variantScrollList.addOption("Foxeater (Sejr)", 18);
            variantScrollList.addOption("Willowsong (Sejr)", 19);
            variantScrollList.addOption("White 2", 20);
            variantScrollList.addOption("Dalmatian", 21);
            variantScrollList.addOption("Gray Tabby", 22);
            variantScrollList.addOption("Brown", 23);
            variantScrollList.addOption("Pale Ginger", 24);
            variantScrollList.addOption("Black 2", 25);
            variantScrollList.addOption("Bengal", 26);
            variantScrollList.addOption("Snowshoe", 27);
            variantScrollList.addOption("Toyger", 28);
            variantScrollList.addOption("Turkish Van", 29);
            variantScrollList.addOption("Albino (CoffeeCat)", 30);
            variantScrollList.addOption("Bengal (CoffeeCat)", 31);
            variantScrollList.addOption("Brindle Tortie (Mswolfy81)", 32);
            variantScrollList.addOption("Cream Calico 1 (Lightley)", 33);
            variantScrollList.addOption("Cream Calico 2 (Lightley)", 34);
            variantScrollList.addOption("Cream Calico 3 (Lightley)", 35);
            variantScrollList.addOption("Caramel (CoffeeCat)", 36);
            variantScrollList.addOption("Frostdawn (whitenoisewife)", 37);
            variantScrollList.addOption("Gray-white Tabby (Slay)", 38);
            variantScrollList.addOption("Hailflake (pvppet, Mswolfy81)", 39);
            variantScrollList.addOption("Karpati (whitenoisewife)", 40);
            variantScrollList.addOption("Leafstar (whitenoisewife)", 41);
            variantScrollList.addOption("Longtail (whitenoisewife)", 42);
            variantScrollList.addOption("Mothpaw (CoffeeCat)", 43);
            variantScrollList.addOption("Redtail (whitenoisewife)", 44);
            variantScrollList.addOption("Salem (CoffeeCat, Mswolfy81)", 45);
            variantScrollList.addOption("Short hair (CoffeeCat)", 46);
            variantScrollList.addOption("Stoneflare (Feathered Melodica)", 47);
            variantScrollList.addOption("Tortie point (whitenoisewife)", 48);
            variantScrollList.addOption("Turtleheart (RainbowServal, Mswolfy81)", 49);
            variantScrollList.addOption("Violetdew (bem te vi, Mswolfy81)", 50);
            variantScrollList.addOption("Patch (Feathered Melodica)", 51);
            variantScrollList.addOption("Parlee (PsychicStudios, CoffeeCat)", 52);

        }

        mainSectionActiveMenu = "base";

        genetics.chestFur = setShortFur;
        genetics.bellyFur = setShortFur;
        genetics.legsFur = setShortFur;
        genetics.headFur = setShortFur;
        genetics.cheekFur = setShortFur;
        genetics.backFur = setShortFur;
        genetics.tailFur = setShortFur;

        genetics.bobtail = setFulltail;

        genetics.base = setBlack;
        genetics.orangeBase = setNotOrange;
        genetics.whiteRatio = setNotWhite;
        genetics.albino = setNotAlbino;
        genetics.dilute = setNonDilute;
        genetics.agouti = setNonAgoutiTabby;
        genetics.tabbyStripes = setTabbyStripesMackerel;
        genetics.eyesAnomaly = setNonHeteroChromia;
        genetics.silver = setNonSilver;
        genetics.rufousing = 0;
        genetics.blueRufousing = 0;
        genetics.noise = 0;

        if (comingBackFromChimeraMenu) {
            genetics.chestFur = chestFur;
            genetics.bellyFur = bellyFur;
            genetics.legsFur = legsFur;
            genetics.headFur = headFur;
            genetics.cheekFur = cheekFur;
            genetics.backFur = backFur;
            genetics.tailFur = tailFur;
            genetics.bobtail = bobtail;
            genetics.base = base;
            genetics.orangeBase = orangeBase;
            genetics.whiteRatio = whiteRatio;
            genetics.albino = albino;
            genetics.dilute = dilute;
            genetics.agouti = agouti;
            genetics.tabbyStripes = tabbyStripes;
            genetics.eyesAnomaly = eyesAnomaly;
            genetics.rufousing = rufousing;
            genetics.blueRufousing = blueRufousing;
            genetics.noise = noise;
            noise = storedNoise;
            genetics.silver = silver;
        } else {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                if (PlayerShape.getCurrentShape(player) instanceof WCatEntity cat) {
                    this.onGeneticalSkin = cat.isOnGeneticalSkin();
                    this.genetics = new WCGenetics(cat.getGenetics());
                    this.geneticsChimera = new WCGenetics(cat.getChimeraGenetics());
                    WCGenetics.GeneticalVariants variants = cat.getGenVariants();
                    eyeColorLeft = variants.eyeColorLeft;
                    eyeColorRight = variants.eyeColorRight;
                    rufousingVariant = variants.rufousingVariant;
                    blueRufousingVariant = variants.blueRufousingVariant;
                    orangeBaseVariant = variants.orangeVar;
                    whiteRatioVariant = variants.whiteVar;
                    tabbyStripesVariant = variants.tabbyVar;
                    albinoVariant = variants.albinoVar;
                    eyeColorVariantLeft = variants.leftEyeVar;
                    eyeColorVariantRight = variants.rightEyeVar;
                    storedNoise = variants.noise;
                    size = variants.size;
                    scars  = variants.scars;
                    silverVar = variants.silverVar;

                    variantsChimera = new WCGenetics.GeneticalChimeraVariants(cat.getChimeraGenVariants());
                    chimeraGene = cat.getGenetics().chimeraGene;
                }
            }
        }

        setOnGeneticalSkinButton = new GradientToggleButton(
                10, 10,
                60, 15,
                Component.literal("Custom morph"),
                btn -> {
                    onGeneticalSkin = true;
                    this.removeWidget(variantScrollList);
                    this.removeWidget(sizeSliderPreset);
                    selectMode(setOnGeneticalSkinButton);
                    mainSectionActiveMenu = "base";
                    if (!baseSection.isSelected()) selectMainSection(baseSection);
                }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20, 0.8f, 0xFFFFFFFF
        );

        // MORPH UTILS
        {
            newMorphButton = new GradientButton(
                    centerX + 65, centerY - 37,
                    22, 10,
                    Component.literal("New").withStyle(ChatFormatting.YELLOW),
                    btn -> {
                        this.genetics = new WCGenetics();
                        this.geneticsChimera = new WCGenetics();
                        this.variantsChimera = new WCGenetics.GeneticalChimeraVariants();

                        eyeColorLeft = "green";
                        eyeColorRight = "green";

                        rufousingVariant = 0;
                        blueRufousingVariant = 0;

                        orangeBaseVariant = 0;
                        whiteRatioVariant = 0;
                        tabbyStripesVariant = 0;
                        albinoVariant = 0;
                        eyeColorVariantLeft = 0;
                        eyeColorVariantRight = 0;
                        noise = 0;
                        silverVar = 0;
                        size = 0;
                        scars = 0;
                        chimeraGene = setNotChimera;

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.80f
            );

            randomizeGeneticsButton = new GradientButton(
                    centerX + 40, centerY + 105,
                    47, 10,
                    Component.literal("Randomize").withStyle(ChatFormatting.GOLD),
                    btn -> {
                        WCGenetics.RandomizedGenetics random = WCGenetics.RandomizedGenetics.randomize(Minecraft.getInstance().player.getRandom());

                        this.genetics = random.genetics;
                        this.geneticsChimera = random.chimeraGenetics;
                        this.variantsChimera = random.chimeraVariants;
                        WCGenetics.GeneticalVariants variants = random.variants;

                        eyeColorLeft = variants.eyeColorLeft;
                        eyeColorRight = variants.eyeColorRight;

                        rufousingVariant = variants.rufousingVariant;
                        blueRufousingVariant = variants.blueRufousingVariant;

                        orangeBaseVariant = variants.orangeVar;
                        whiteRatioVariant = variants.whiteVar;
                        tabbyStripesVariant = variants.tabbyVar;
                        albinoVariant = variants.albinoVar;
                        eyeColorVariantLeft = variants.leftEyeVar;
                        eyeColorVariantRight = variants.rightEyeVar;
                        noise = variants.noise;
                        silverVar = variants.silverVar;
                        size = variants.size;
                        chimeraGene = genetics.chimeraGene;

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.80f
            );
        }


        // STORED MORPHS
        {
            storedMorphsMenu = new GradientButton(
                    centerX - 87, centerY - 37,
                    70, 10,
                    Component.literal("Saved Morphs"),
                    btn -> {
                        this.onStoredMorphsMenu = true;
                        this.removeWidget(saveMorphNameBox);
                        storedMorphsKey = "main";

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.80f
            );

            saveStoredMorph = new GradientButton(
                    centerX - 75, centerY + 30,
                    70, 10,
                    Component.literal("Save"),
                    btn -> {
                        storedMorphsKey = "save";
                        this.removeWidget(saveMorphNameBox);
                        loadSaveMorph();

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.80f
            );
            loadStoredMorph = new GradientButton(
                    centerX + 5, centerY + 30,
                    70, 10,
                    Component.literal("Morphs"),
                    btn -> {
                        storedMorphsKey = "load";
                        loadStoredMorphsList();

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.80f
            );
            backFromStoredMorphMenu = new GradientButton(
                    centerX - 32, centerY + 50,
                    70, 10,
                    Component.literal("Back"),
                    btn -> {
                        this.onStoredMorphsMenu = false;
                        storedMorphsKey = "";
                        this.removeWidget(savedMorphsScrollList);
                        this.removeWidget(saveMorphNameBox);

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.80f
            );

            loadSelectedMorphButton = new GradientButton(
                    centerX - 75, centerY + 87,
                    70, 10,
                    Component.literal("Load"),
                    btn -> {
                        this.onStoredMorphsMenu = false;
                        this.storedMorphsKey = "";
                        this.removeWidget(savedMorphsScrollList);
                        this.removeWidget(saveMorphNameBox);

                        loadSelectedMorph();

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.80f
            );

            cancelMorphLoad = new GradientButton(
                    centerX + 5, centerY + 87,
                    70, 10,
                    Component.literal("Return"),
                    btn -> {
                        this.onStoredMorphsMenu = false;
                        this.storedMorphsKey = "";
                        this.removeWidget(savedMorphsScrollList);
                        this.removeWidget(saveMorphNameBox);

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.80f
            );

            removeSelectedMorph = new GradientButton(
                    centerX - 75, centerY + 102,
                    70, 10,
                    Component.literal("Delete"),
                    btn -> {
                        if (!removeSelectedMorphConfirm) {
                            displayError("Press again to confirm", false);
                            removeSelectedMorphConfirm = true;
                            return;
                        }
                        removeSelectedMorphConfirm = false;

                        this.onStoredMorphsMenu = false;
                        this.storedMorphsKey = "";
                        this.removeWidget(savedMorphsScrollList);
                        this.removeWidget(saveMorphNameBox);

                        deleteSelectedMorph();

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.80f
            );

            overwriteSelectedMorph = new GradientButton(
                    centerX + 5, centerY + 102,
                    70, 10,
                    Component.literal("Overwrite"),
                    btn -> {
                        if (!overwriteSelectedMorphConfirm) {
                            displayError("Press again to confirm", false);
                            overwriteSelectedMorphConfirm = true;
                            return;
                        }
                        overwriteSelectedMorphConfirm = false;

                        this.onStoredMorphsMenu = false;
                        this.storedMorphsKey = "";
                        this.removeWidget(savedMorphsScrollList);
                        this.removeWidget(saveMorphNameBox);

                        overwriteSelectedMorph();

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.80f
            );

            sendSelectedMorphToChat = new GradientButton(
                    centerX + 5, centerY - 32,
                    70, 10,
                    Component.literal("Send to Chat"),
                    btn -> {
                        this.onStoredMorphsMenu = false;
                        this.storedMorphsKey = "";
                        this.removeWidget(savedMorphsScrollList);
                        this.removeWidget(saveMorphNameBox);

                        sendMorphToChat();

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.80f
            );


            confirmSaveMorph = new GradientButton(
                    centerX - 75, centerY + 90,
                    70, 10,
                    Component.literal("Save"),
                    btn -> {
                        this.onStoredMorphsMenu = false;
                        this.storedMorphsKey = "";
                        this.removeWidget(savedMorphsScrollList);
                        this.removeWidget(saveMorphNameBox);

                        saveSelectedMorph();

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.80f
            );

            cancelMorphSave = new GradientButton(
                    centerX + 5, centerY + 90,
                    70, 10,
                    Component.literal("Return"),
                    btn -> {
                        this.onStoredMorphsMenu = false;
                        this.storedMorphsKey = "";
                        this.removeWidget(savedMorphsScrollList);
                        this.removeWidget(saveMorphNameBox);

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.80f
            );

        }

        setOnPresetSkinButton = new GradientToggleButton(
                75, 10,
                60, 15,
                Component.literal("Preset morph"),
                btn -> {
                    onGeneticalSkin = false;
                    this.removeWidget(this.variantScrollList);
                    this.addRenderableWidget(this.variantScrollList);
                    this.removeWidget(sizeSliderPreset);
                    this.addRenderableWidget(sizeSliderPreset);
                    selectMode(setOnPresetSkinButton);
                    mainSectionActiveMenu = "base";
                    selectMainSection(baseSection);
                }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                80, 20, 0.8f, 0xFFFFFFFF
        );

        String text = "Save morph";
        if (this.isSummoning) text = "Summon";

        saveButton = new GradientToggleButton(
                this.width - 70, 10,
                60, 15,
                Component.literal(text),
                btn -> {
                    closing = true;
                    animationTime = 0f;
                    WCEClient.nextMenuSound();

                }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                60, 20, 0.8f, 0xFFFFFFFF
        );

        // MANDATORY INIT
        {
            int top1 = centerY - 35;
            int bottom1 = centerY + 35;
            this.leftEyeVariantList = new VariantScrollList(
                    Minecraft.getInstance(),
                    70,
                    40,
                    top1,
                    bottom1,
                    20
            );
//            this.leftEyeVariantList.setRenderTopAndBottom(false);
            this.leftEyeVariantList.setX(((centerX + 100)));

            for (int i = 0; i < WCGenetics.Values.MAX_EYE_VARIANTS; i++) {
                leftEyeVariantList.addOption("Color " + (i + 1), i);
            }


            int top2 = centerY + 45;
            int bottom2 = centerY + 115;
            this.rightEyeVariantList = new VariantScrollList(
                    Minecraft.getInstance(),
                    70,
                    40,
                    top2,
                    bottom2,
                    20
            );
//            this.rightEyeVariantList.setRenderTopAndBottom(false);
            this.rightEyeVariantList.setX(((centerX + 100)));

            for (int i = 0; i < WCGenetics.Values.MAX_EYE_VARIANTS; i++) {
                rightEyeVariantList.addOption("Color " + (i + 1), i);
            }
        }

        // SECTION BUTTONS
        {
            int buttonHeight = centerY - 80;

            int xOffset1 = -30;

            baseSection = new GradientToggleButton(
                    centerX - 105 + xOffset1, buttonHeight, 50, 15,
                    Component.literal("Base"),
                    btn -> {
                        selectMainSection(baseSection);
                        mainSectionActiveMenu = "base";
                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            orangeSection = new GradientToggleButton(
                    centerX - 50 + xOffset1, buttonHeight, 50, 15,
                    Component.literal("Orange"),
                    btn -> {
                        selectMainSection(orangeSection);
                        mainSectionActiveMenu = "orange";
                        if (setTortieButton.isSelected()) this.addRenderableWidget(orangeVariantList);


                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            whiteSection = new GradientToggleButton(
                    centerX + 5 + xOffset1, buttonHeight, 50, 15,
                    Component.literal("White"),
                    btn -> {
                        selectMainSection(whiteSection);
                        mainSectionActiveMenu = "white";
                        if (setHighWhiteButton.isSelected() || setLowWhiteButton.isSelected()) this.addRenderableWidget(whiteVariantList);


                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            albinoSection = new GradientToggleButton(
                    centerX + 60 + xOffset1, buttonHeight, 50, 15,
                    Component.literal("Albinism"),
                    btn -> {
                        selectMainSection(albinoSection);
                        mainSectionActiveMenu = "albinism";
                        if (setSiameseButton.isSelected() || setSepiaButton.isSelected() || setMinkButton.isSelected())
                            this.addRenderableWidget(albinoVariantList);


                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            chimeraSection = new GradientToggleButton(
                    centerX + 115 + xOffset1, buttonHeight, 50, 15,
                    Component.literal("Chimerism"),
                    btn -> {
                        selectMainSection(chimeraSection);
                        mainSectionActiveMenu = "chimera";

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            buttonHeight += 20;

            int xOffset = -55;

            diluteSection = new GradientToggleButton(
                    centerX - 105 + xOffset, buttonHeight, 50, 15,
                    Component.literal("Dilute"),
                    btn -> {
                        selectMainSection(diluteSection);
                        mainSectionActiveMenu = "dilute";

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            agoutiAndTabbySection = new GradientToggleButton(
                    centerX - 50 + xOffset, buttonHeight, 50, 15,
                    Component.literal("Agouti"),
                    btn -> {
                        selectMainSection(agoutiAndTabbySection);
                        mainSectionActiveMenu = "agouti";
                        if (agoutiSwitch.getValue() || silverSwitch.getValue() || setOrangeButton.isSelected() || setTortieButton.isSelected()) {
                            this.removeWidget(tabbyVariantList);
                            this.addRenderableWidget(tabbyVariantList);
                        }
                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            silverSection = new GradientToggleButton(
                    centerX + 5 + xOffset, buttonHeight, 50, 15,
                    Component.literal("Silver"),
                    btn -> {
                        selectMainSection(silverSection);
                        mainSectionActiveMenu = "silver";
                        if (silverSwitch.getValue()) {
                            this.removeWidget(silverVariantList);
                            this.addRenderableWidget(silverVariantList);
                        }
                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );


            eyesAndAnomalySection = new GradientToggleButton(
                    centerX + 60 + xOffset, buttonHeight, 50, 15,
                    Component.literal("Eyes"),
                    btn -> {
                        selectMainSection(eyesAndAnomalySection);
                        mainSectionActiveMenu = "eyes";
                        this.addRenderableWidget(leftEyeVariantList);
                        if (heterochromiaSwitch.getValue()) this.addRenderableWidget(rightEyeVariantList);

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            furBobtailNoiseSection = new GradientToggleButton(
                    centerX + 115 + xOffset, buttonHeight, 50, 15,
                    Component.literal("Details"),
                    btn -> {
                        selectMainSection(furBobtailNoiseSection);
                        mainSectionActiveMenu = "details";
                        this.removeWidget(noiseList);
                        this.addRenderableWidget(noiseList);
                        this.removeWidget(sizeSlider);
                        this.addRenderableWidget(sizeSlider);
                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            extraSection = new GradientToggleButton(
                    centerX + 170 + xOffset, buttonHeight, 50, 15,
                    Component.literal("Extra"),
                    btn -> {
                        selectMainSection(extraSection);
                        mainSectionActiveMenu = "extra";
                        this.removeWidget(rufousingSlider);
                        this.addRenderableWidget(rufousingSlider);
                        this.removeWidget(blueRufousingSlider);
                        this.addRenderableWidget(blueRufousingSlider);
                        this.removeWidget(scarsList);
                        this.addRenderableWidget(scarsList);
                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );



        }

        // BASE BUTTONS
        {
            setBlackButton = new GradientToggleButton(
                    centerX - 150, centerY - 15, 50, 15,
                    Component.literal("Black"),
                    btn -> {
                        selectBase(setBlackButton);
                        genetics.base = setBlack;
                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            setChocolateButton = new GradientToggleButton(
                    centerX - 150, centerY + 10, 50, 15,
                    Component.literal("Chocolate"),
                    btn -> {
                        selectBase(setChocolateButton);
                        genetics.base = setChocolate;
                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            setCinnamonButton = new GradientToggleButton(
                    centerX - 150, centerY + 35, 50, 15,
                    Component.literal("Cinnamon"),
                    btn -> {
                        selectBase(setCinnamonButton);
                        genetics.base = setCinnamon;
                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );
        }

        // ORANGE BUTTONS
        {
            setOrangeButton = new GradientToggleButton(
                    centerX - 150, centerY - 15, 50, 15,
                    Component.literal("Orange"),
                    btn -> {
                        selectOrange(setOrangeButton);
                        genetics.orangeBase = setOrange;
                        this.removeWidget(orangeVariantList);
                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            setTortieButton = new GradientToggleButton(
                    centerX - 150, centerY + 10, 50, 15,
                    Component.literal("Tortie"),
                    btn -> {
                        selectOrange(setTortieButton);
                        genetics.orangeBase = setTortie;
                        this.addRenderableWidget(orangeVariantList);

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            setNotOrangeButton = new GradientToggleButton(
                    centerX - 150, centerY + 35, 50, 15,
                    Component.literal("No orange"),
                    btn -> {
                        selectOrange(setNotOrangeButton);
                        genetics.orangeBase = setNotOrange;
                        this.removeWidget(orangeVariantList);
                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            int top = centerY - 20;
            int bottom = centerY + 110;
            this.orangeVariantList = new VariantScrollList(
                    Minecraft.getInstance(),
                    65,
                    40,
                    top,
                    bottom,
                    20
            );
//            this.orangeVariantList.setRenderTopAndBottom(false);
            this.orangeVariantList.setX(((centerX + 100)));

            for (int i = 0; i < WCGenetics.Values.MAX_ORANGE_VARIANTS; i++) {
                orangeVariantList.addOption("Tortie " + (i + 1), i);
            }

        }

        // WHITE BUTTONS
        {
            setFullWhiteButton = new GradientToggleButton(
                    centerX - 150, centerY - 15, 50, 15,
                    Component.literal("Full white"),
                    btn -> {
                        selectWhite(setFullWhiteButton);
                        genetics.whiteRatio = setFullWhite;
                        this.removeWidget(whiteVariantList);
                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            setHighWhiteButton = new GradientToggleButton(
                    centerX - 150, centerY + 10, 50, 15,
                    Component.literal("High spots"),
                    btn -> {
                        selectWhite(setHighWhiteButton);
                        genetics.whiteRatio = setHighWhite;
                        this.removeWidget(whiteVariantList);
                        this.addRenderableWidget(whiteVariantList);

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            setLowWhiteButton = new GradientToggleButton(
                    centerX - 150, centerY + 35, 50, 15,
                    Component.literal("Low spots"),
                    btn -> {
                        selectWhite(setLowWhiteButton);
                        genetics.whiteRatio = setLowWhite;
                        this.removeWidget(whiteVariantList);
                        this.addRenderableWidget(whiteVariantList);
                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            setNoWhiteButton = new GradientToggleButton(
                    centerX - 150, centerY + 60, 50, 15,
                    Component.literal("No white"),
                    btn -> {
                        selectWhite(setNoWhiteButton);
                        genetics.whiteRatio = setNotWhite;
                        this.removeWidget(whiteVariantList);
                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );


            int top = centerY - 20;
            int bottom = centerY + 110;
            this.whiteVariantList = new VariantScrollList(
                    Minecraft.getInstance(),
                    65,
                    40,
                    top,
                    bottom,
                    20
            );
//            this.whiteVariantList.setRenderTopAndBottom(false);
            this.whiteVariantList.setX(((centerX + 100)));

            for (int i = 0; i < WCGenetics.Values.MAX_WHITE_VARIANTS; i++) {
                whiteVariantList.addOption("Spots " + (i + 1), i);
            }

        }

        // ALBINO BUTTONS
        {
            setNotAlbinoButton = new GradientToggleButton(
                    centerX - 150, centerY - 15, 50, 15,
                    Component.literal("Not albino"),
                    btn -> {
                        selectAlbino(setNotAlbinoButton);
                        genetics.albino = setNotAlbino;
                        this.removeWidget(albinoVariantList);
                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            setTrueAlbinoButton = new GradientToggleButton(
                    centerX - 150, centerY + 10, 50, 15,
                    Component.literal("True albino"),
                    btn -> {
                        selectAlbino(setTrueAlbinoButton);
                        genetics.albino = setTrueAlbino;
                        this.removeWidget(albinoVariantList);

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            setSepiaButton = new GradientToggleButton(
                    centerX - 150, centerY + 35, 50, 15,
                    Component.literal("Sepia"),
                    btn -> {
                        selectAlbino(setSepiaButton);
                        genetics.albino = setSepia;
                        this.removeWidget(albinoVariantList);
                        this.addRenderableWidget(albinoVariantList);

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            setMinkButton = new GradientToggleButton(
                    centerX - 150, centerY + 60, 50, 15,
                    Component.literal("Mink"),
                    btn -> {
                        selectAlbino(setMinkButton);
                        genetics.albino = setMink;
                        this.removeWidget(albinoVariantList);
                        this.addRenderableWidget(albinoVariantList);
                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            setSiameseButton = new GradientToggleButton(
                    centerX - 150, centerY + 85, 50, 15,
                    Component.literal("Siamese"),
                    btn -> {
                        selectAlbino(setSiameseButton);
                        genetics.albino = setSiamese;
                        this.removeWidget(albinoVariantList);
                        this.addRenderableWidget(albinoVariantList);

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            int top = centerY - 20;
            int bottom = centerY + 110;
            this.albinoVariantList = new VariantScrollList(
                    Minecraft.getInstance(),
                    65,
                    40,
                    top,
                    bottom,
                    20
            );
//            this.albinoVariantList.setRenderTopAndBottom(false);
            this.albinoVariantList.setX(((centerX + 100)));

            for (int i = 0; i < WCGenetics.Values.MAX_ALBINO_VARIANTS; i++) {
                albinoVariantList.addOption("Albino " + (i + 1), i);
            }
        }

        // DILUTE SWITCH
        {
            diluteSwitch = new GradientSwitchButton(
                    centerX - 150, centerY - 15, 50, 15,
                    "Dilute",
                    false,
                    btn -> {
                        if (diluteSwitch.getValue()) {
                            genetics.dilute = setDilute;
                        } else {
                            genetics.dilute = setNonDilute;
                        }
                    }, 0xFFFFFFFF
            );
        }

        // SILVER SWITCH
        {
            silverSwitch = new GradientSwitchButton(
                    centerX - 150, centerY - 15, 50, 15,
                    "Silver",
                    false,
                    btn -> {

                        this.removeWidget(silverVariantList);
                        if (silverSwitch.getValue()) {
                            genetics.silver = setSilver;
                            this.addRenderableWidget(silverVariantList);
                        } else {
                            genetics.silver = setNonSilver;
                        }

                    }, 0xFFFFFFFF
            );

            int top = centerY - 20;
            int bottom = centerY + 110;
            this.silverVariantList = new VariantScrollList(
                    Minecraft.getInstance(),
                    65,
                    40,
                    top,
                    bottom,
                    20
            );
//            this.silverVariantList.setRenderTopAndBottom(false);
            this.silverVariantList.setX(((centerX + 100)));

            for (int i = 0; i < WCGenetics.Values.MAX_SILVER_VARIANTS; i++) {
                silverVariantList.addOption("Silver " + (i + 1), i);
            }
        }

        // AGOUTI SWITCH, BUTTONS, VARIANT LIST
        {
            agoutiSwitch = new GradientSwitchButton(
                    centerX - 150, centerY - 15, 50, 15,
                    "Agouti",
                    false,
                    btn -> {
                        if (agoutiSwitch.getValue()) {
                            genetics.agouti = setAgoutiTabby;
                        } else {
                            genetics.agouti = setNonAgoutiTabby;
                        }
                    }, 0xFFFFFFFF
            );

            setMackerelButton = new GradientToggleButton(
                    centerX - 150, centerY + 15, 50, 15,
                    Component.literal("Mackerel"),
                    btn -> {
                        selectAgoutiPattern(setMackerelButton);
                        genetics.tabbyStripes = setTabbyStripesMackerel;
                        this.removeWidget(tabbyVariantList);
                        this.addRenderableWidget(tabbyVariantList);

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            setClassicButton = new GradientToggleButton(
                    centerX - 150, centerY + 45, 50, 15,
                    Component.literal("Classic"),
                    btn -> {
                        selectAgoutiPattern(setClassicButton);
                        genetics.tabbyStripes = setTabbyStripesClassic;
                        this.removeWidget(tabbyVariantList);
                        this.addRenderableWidget(tabbyVariantList);
                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            int top = centerY - 20;
            int bottom = centerY + 110;
            this.tabbyVariantList = new VariantScrollList(
                    Minecraft.getInstance(),
                    65,
                    40,
                    top,
                    bottom,
                    20
            );
//            this.tabbyVariantList.setRenderTopAndBottom(false);
            this.tabbyVariantList.setX(((centerX + 100)));

            for (int i = 0; i < WCGenetics.Values.MAX_TABBY_VARIANTS; i++) {
                tabbyVariantList.addOption("Tabby " + (i + 1), i);
            }

        }

        // EYES
        {
            heterochromiaSwitch = new GradientSwitchButton(
                    centerX - 155, centerY - 15, 60, 15,
                    "Split",
                    false,
                    btn -> {
                        if (heterochromiaSwitch.getValue()) {
                            genetics.eyesAnomaly = setHeteroChromia;
                            this.addRenderableWidget(rightEyeVariantList);
                        } else {
                            genetics.eyesAnomaly = setNonHeteroChromia;
                            this.removeWidget(rightEyeVariantList);
                        }
                    }, 0xFFFFFFFF, 0.8f
            );

            setLeftEyeBlueButton = new GradientToggleButton(
                    centerX - 158, centerY + 15, 30, 15,
                    Component.literal("Blue"),
                    btn -> {
                        selectLeftEye(setLeftEyeBlueButton);
                        eyeColorRight = setEyeBlue;

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            setLeftEyeGreenButton = new GradientToggleButton(
                    centerX - 158, centerY + 35, 30, 15,
                    Component.literal("Green"),
                    btn -> {
                        selectLeftEye(setLeftEyeGreenButton);
                        eyeColorRight = setEyeGreen;

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );
            setLeftEyeYellowButton = new GradientToggleButton(
                    centerX - 158, centerY + 55, 30, 15,
                    Component.literal("Yellow"),
                    btn -> {
                        selectLeftEye(setLeftEyeYellowButton);
                        eyeColorRight = setEyeYellow;

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );
            setLeftEyeRedButton = new GradientToggleButton(
                    centerX - 158, centerY + 75, 30, 15,
                    Component.literal("Red"),
                    btn -> {
                        selectLeftEye(setLeftEyeRedButton);
                        eyeColorRight = setEyeRed;

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );
            setLeftEyeBlindButton = new GradientToggleButton(
                    centerX - 158, centerY + 95, 30, 15,
                    Component.literal("Foggy"),
                    btn -> {
                        selectLeftEye(setLeftEyeBlindButton);
                        eyeColorRight = setEyeBlind;

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );


            setRightEyeBlueButton = new GradientToggleButton(
                    centerX - 122, centerY + 15, 30, 15,
                    Component.literal("Blue"),
                    btn -> {
                        selectRightEye(setRightEyeBlueButton);
                        eyeColorLeft = setEyeBlue;

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );

            setRightEyeGreenButton = new GradientToggleButton(
                    centerX - 122, centerY + 35, 30, 15,
                    Component.literal("Green"),
                    btn -> {
                        selectRightEye(setRightEyeGreenButton);
                        eyeColorLeft = setEyeGreen;

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );
            setRightEyeYellowButton = new GradientToggleButton(
                    centerX - 122, centerY + 55, 30, 15,
                    Component.literal("Yellow"),
                    btn -> {
                        selectRightEye(setRightEyeYellowButton);
                        eyeColorLeft = setEyeYellow;

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );
            setRightEyeRedButton = new GradientToggleButton(
                    centerX - 122, centerY + 75, 30, 15,
                    Component.literal("Red"),
                    btn -> {
                        selectRightEye(setRightEyeRedButton);
                        eyeColorLeft = setEyeRed;

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );
            setRightEyeBlindButton = new GradientToggleButton(
                    centerX - 122, centerY + 95, 30, 15,
                    Component.literal("Foggy"),
                    btn -> {
                        selectRightEye(setRightEyeBlindButton);
                        eyeColorLeft = setEyeBlind;

                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    80, 20, 0.8f, 0xFFFFFFFF
            );


        }

        // DETAILS
        {
            bobtailSwitch = new GradientSwitchButton(
                    45, centerY - 15, 40, 16,
                    "Bobtail",
                    false,
                    btn -> {
                        if (bobtailSwitch.getValue()) {
                            genetics.bobtail = setBobtail;
                        } else {
                            genetics.bobtail = setFulltail;
                        }
                    }, 0xFFFFFFFF, 0.8f
            );

            chestFurSwitch = new GradientSwitchButton(
                    15, centerY + 15, 45, 16,
                    "Chest fur",
                    false,
                    btn -> {
                        if (chestFurSwitch.getValue()) {
                            genetics.chestFur = setLongFur;
                        } else {
                            genetics.chestFur = setShortFur;
                        }
                    }, 0xFFFFFFFF, 0.8f
            );

            bellyFurSwitch = new GradientSwitchButton(
                    15, centerY + 40, 45, 16,
                    "Belly fur", false,
                    btn -> {
                        if (bellyFurSwitch.getValue()) {
                            genetics.bellyFur = setLongFur;
                        } else {
                            genetics.bellyFur = setShortFur;
                        }
                    }, 0xFFFFFFFF, 0.8f
            );
            legsFurSwitch = new GradientSwitchButton(
                    15, centerY + 65, 45, 16,
                    "Legs fur", false,
                    btn -> {
                        if (legsFurSwitch.getValue()) {
                            genetics.legsFur = setLongFur;
                        } else {
                            genetics.legsFur = setShortFur;
                        }
                    }, 0xFFFFFFFF, 0.8f
            );


            headFurSwitch = new GradientSwitchButton(
                    70, centerY + 15, 45, 16,
                    "Head fur", false,
                    btn -> {
                        if (headFurSwitch.getValue()) {
                            genetics.headFur = setLongFur;
                        } else {
                            genetics.headFur = setShortFur;
                        }
                    }, 0xFFFFFFFF, 0.8f
            );

            cheekFurSwitch = new GradientSwitchButton(
                    70, centerY + 40, 45, 16,
                    "Face fur", false,
                    btn -> {
                        if (cheekFurSwitch.getValue()) {
                            genetics.cheekFur = setLongFur;
                        } else {
                            genetics.cheekFur = setShortFur;
                        }
                    }, 0xFFFFFFFF, 0.8f
            );
            backFurSwitch = new GradientSwitchButton(
                    70, centerY + 65, 45, 16,
                    "Neck fur", false,
                    btn -> {
                        if (backFurSwitch.getValue()) {
                            genetics.backFur = setLongFur;
                        } else {
                            genetics.backFur = setShortFur;
                        }
                    }, 0xFFFFFFFF, 0.8f
            );

            tailFurSwitch = new GradientSwitchButton(
                    40, centerY + 90, 50, 16,
                    "Tail fur",
                    false,
                    btn -> {
                        if (tailFurSwitch.getValue()) {
                            genetics.tailFur = setLongFur;
                        } else {
                            genetics.tailFur = setShortFur;
                        }
                    }, 0xFFFFFFFF, 0.8f
            );

            float maxSize = 1.2f;
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                if (WarriorCatsEvents.Collaborators.isContributor(player.getUUID())) {
                    maxSize = 1.35f;
                }
                if (WarriorCatsEvents.Collaborators.isOwner(player.getUUID())) {
                    maxSize = 5f;
                }
            }

            sizeSlider = new FloatSliderButton(centerX + 95, centerY - 35,
                    70, 20,
                    0.6f, maxSize, 0.9f);

            sizeSliderPreset = new FloatSliderButton(centerX - 50, centerY - 65,
                    100, 20,
                    0.6f, 1.2f, 0.9f);

            int top = centerY - 10;
            int bottom = centerY + 110;
            this.noiseList = new VariantScrollList(
                    Minecraft.getInstance(),
                    65,
                    40,
                    top,
                    bottom,
                    20
            );
//            this.noiseList.setRenderTopAndBottom(false);
            this.noiseList.setX(((centerX + 95)));

            for (int i = 0; i < WCGenetics.Values.MAX_NOISE_VARIANTS; i++) {
                if (i == 5) {
                    noiseList.addOption("No Noise", i);
                    continue;
                }
                noiseList.addOption("Noise " + (i + 1), i);
            }

        }

        // EXTRA
        {
            rufousingSlider = new IntSliderButton(centerX - 160, centerY + 5,
                    65, 20,
                    0, 6, 0, "Rufousing");

            blueRufousingSlider = new IntSliderButton(centerX - 160, centerY + 45,
                    65, 20,
                    0, 6, 0, "Blue tint");

            int top = centerY - 20;
            int bottom = centerY + 110;
            this.scarsList = new VariantScrollList(
                    Minecraft.getInstance(),
                    65,
                    40,
                    top,
                    bottom,
                    20
            );
//            this.scarsList.setRenderTopAndBottom(false);
            this.scarsList.setX(((centerX + 100)));

            for (int i = 0; i < WCGenetics.Values.MAX_SCAR_VARIANTS; i++) {
                if (i == 0) {
                    scarsList.addOption("No Scars", i);
                } else if (i == 1) {
                    scarsList.addOption("Face Right", i);
                } else if (i == 2) {
                    scarsList.addOption("Face Left", i);
                } else if (i == 3) {
                    scarsList.addOption("Head Right", i);
                } else if (i == 4) {
                    scarsList.addOption("Head Left", i);
                } else if (i == 5) {
                    scarsList.addOption("Side Right", i);
                } else if (i == 6) {
                    scarsList.addOption("Side Left", i);
                } else if (i == 7) {
                    scarsList.addOption("Chest Right", i);
                } else if (i == 8) {
                    scarsList.addOption("Chest Left", i);
                } else if (i == 9) {
                    scarsList.addOption("Thigh Right", i);
                } else if (i == 10) {
                    scarsList.addOption("Thigh Left", i);
                } else {
                    scarsList.addOption("Scars " + (i + 1 - 11), i);
                }
            }
        }

        // CHIMERISM
        {
            editChimera = new GradientToggleButton(
                    centerX - 152, centerY +15,
                    55, 15,
                    Component.literal("Edit Chimera"),
                    btn -> {
                        closing = true;
                        animationTime = 0f;
                        goingToChimeraMenu = true;
                    }, ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/empty.png"),
                    60, 20, 0.8f, 0xFFFFFFFF
            );

            chimeraSwitch = new GradientSwitchButton(
                    centerX - 150, centerY - 15, 50, 15,
                    "Chimera",
                    false,
                    btn -> {
                        if (chimeraSwitch.getValue()) {
                            chimeraGene = setChimera;
                        } else {
                            chimeraGene = setNotChimera;
                        }
                    }, 0xFFFFFFFF
            );
        }


        rufousingSlider.setValue(rufousingVariant);
        blueRufousingSlider.setValue(blueRufousingVariant);

        this.addRenderableWidget(bobtailSwitch);
        this.addRenderableWidget(chestFurSwitch);
        this.addRenderableWidget(legsFurSwitch);
        this.addRenderableWidget(headFurSwitch);
        this.addRenderableWidget(bellyFurSwitch);
        this.addRenderableWidget(backFurSwitch);
        this.addRenderableWidget(tailFurSwitch);
        this.addRenderableWidget(cheekFurSwitch);


        this.addRenderableWidget(heterochromiaSwitch);
        this.addRenderableWidget(setLeftEyeYellowButton);
        this.addRenderableWidget(setLeftEyeBlueButton);
        this.addRenderableWidget(setLeftEyeGreenButton);
        this.addRenderableWidget(setLeftEyeRedButton);
        this.addRenderableWidget(setLeftEyeBlindButton);
        this.addRenderableWidget(setRightEyeYellowButton);
        this.addRenderableWidget(setRightEyeGreenButton);
        this.addRenderableWidget(setRightEyeBlueButton);
        this.addRenderableWidget(setRightEyeRedButton);
        this.addRenderableWidget(setRightEyeBlindButton);

        this.addRenderableWidget(agoutiSwitch);
        this.addRenderableWidget(setClassicButton);
        this.addRenderableWidget(setMackerelButton);

        this.addRenderableWidget(diluteSwitch);

        this.addRenderableWidget(setNotAlbinoButton);
        this.addRenderableWidget(setTrueAlbinoButton);
        this.addRenderableWidget(setMinkButton);
        this.addRenderableWidget(setSepiaButton);
        this.addRenderableWidget(setSiameseButton);

        this.addRenderableWidget(setFullWhiteButton);
        this.addRenderableWidget(setHighWhiteButton);
        this.addRenderableWidget(setLowWhiteButton);
        this.addRenderableWidget(setNoWhiteButton);

        this.addRenderableWidget(setOrangeButton);
        this.addRenderableWidget(setTortieButton);
        this.addRenderableWidget(setNotOrangeButton);

        this.addRenderableWidget(silverSwitch);

        this.addRenderableWidget(setBlackButton);
        this.addRenderableWidget(setChocolateButton);
        this.addRenderableWidget(setCinnamonButton);

        this.addRenderableWidget(chimeraSwitch);
        this.addRenderableWidget(editChimera);

        this.addRenderableWidget(baseSection);
        this.addRenderableWidget(orangeSection);
        this.addRenderableWidget(whiteSection);
        this.addRenderableWidget(albinoSection);
        this.addRenderableWidget(chimeraSection);
        this.addRenderableWidget(diluteSection);
        this.addRenderableWidget(agoutiAndTabbySection);
        this.addRenderableWidget(eyesAndAnomalySection);
        this.addRenderableWidget(furBobtailNoiseSection);
        this.addRenderableWidget(extraSection);
        this.addRenderableWidget(silverSection);

        this.addRenderableWidget(setOnGeneticalSkinButton);
        this.addRenderableWidget(setOnPresetSkinButton);
        this.addRenderableWidget(saveButton);
        this.addRenderableWidget(storedMorphsMenu);
        this.addRenderableWidget(saveStoredMorph);
        this.addRenderableWidget(loadStoredMorph);
        this.addRenderableWidget(backFromStoredMorphMenu);
        this.addRenderableWidget(loadSelectedMorphButton);
        this.addRenderableWidget(cancelMorphLoad);
        this.addRenderableWidget(confirmSaveMorph);
        this.addRenderableWidget(cancelMorphSave);
        this.addRenderableWidget(removeSelectedMorph);
        this.addRenderableWidget(overwriteSelectedMorph);
        this.addRenderableWidget(sendSelectedMorphToChat);

        this.addRenderableWidget(newMorphButton);
        this.addRenderableWidget(randomizeGeneticsButton);

        menuX = 500;
        if (comingBackFromChimeraMenu) menuX = -500;

    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

//        pGuiGraphics.blit(BG_TEXTURE, 0, 0, 0, 0, this.width, this.height, this.width, this.height);

        this.renderPanorama(pGuiGraphics, pPartialTick);
        this.renderMenuBackground(pGuiGraphics);

        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        if (closing) {
            animationTime += pPartialTick;

            float progress = Math.min(animationTime / duration, 1f);

            float eased = progress * progress * progress;

            menuX = startX + (endX - startX) * eased;

            if (progress >= 1f) {
                saveAndNext();
            }
        } else {
            if (comingBackFromChimeraMenu) {
                if (menuX < 0) {
                    menuX -= (menuX) * 0.03f;
                    if (menuX > 0) menuX = 0;
                }
            } else {
                if (menuX > 0) {
                    menuX -= (menuX) * 0.03f;
                    if (menuX < 0) menuX = 0;
                }
            }
        }

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(menuX, 0, 0);

//        pGuiGraphics.enableScissor((int) (10 + menuX), 5, (int) (124 + menuX), 5 + 34);
//        pGuiGraphics.blit(
//                BANNER,
//                10, 5,
//                0, 0,
//                800, 225,
//                114, 32
//        );
//        pGuiGraphics.disableScissor();



        pGuiGraphics.blit(WCEClient.WCE_TITLE,
                centerX - 200 - this.width,
                centerY - 62, 0, 0,
                250, 125,250,125);

        pGuiGraphics.blit(WCEClient.WCE_TITLE,
                centerX - 125 + this.width,
                centerY - 62, 0, 0,
                250, 125,250,125);



        storedMorphsMenu.visible = false;
        baseSection.visible = false;
        orangeSection.visible = false;
        whiteSection.visible = false;
        albinoSection.visible = false;
        diluteSection.visible = false;
        eyesAndAnomalySection.visible = false;
        furBobtailNoiseSection.visible = false;
        extraSection.visible = false;
        silverSection.visible = false;
        chimeraSection.visible = false;
        agoutiAndTabbySection.visible = false;
        setBlackButton.visible = false;
        setChocolateButton.visible = false;
        setCinnamonButton.visible = false;
        setOrangeButton.visible = false;
        setTortieButton.visible = false;
        setNotOrangeButton.visible = false;
//        orangeVariantList.setVisible(false);
//        whiteVariantList.setVisible(false);
//        tabbyVariantList.setVisible(false);
        setFullWhiteButton.visible = false;
        setHighWhiteButton.visible = false;
        setLowWhiteButton.visible = false;
        setNoWhiteButton.visible = false;
        setNotAlbinoButton.visible = false;
        setTrueAlbinoButton.visible = false;
        setMinkButton.visible = false;
        setSiameseButton.visible = false;
        setSepiaButton.visible = false;
        diluteSwitch.visible = false;
        agoutiSwitch.visible = false;
        silverSwitch.visible = false;
        setMackerelButton.visible = false;
        setClassicButton.visible = false;
        heterochromiaSwitch.visible = false;
        setLeftEyeYellowButton.visible = false;
        setRightEyeYellowButton.visible = false;
        setLeftEyeGreenButton.visible = false;
        setRightEyeGreenButton.visible = false;
        setLeftEyeBlueButton.visible = false;
        setRightEyeBlueButton.visible = false;
        setLeftEyeRedButton.visible = false;
        setRightEyeRedButton.visible = false;
        setLeftEyeBlindButton.visible = false;
        setRightEyeBlindButton.visible = false;
        bobtailSwitch.visible = false;
        chestFurSwitch.visible = false;
        bellyFurSwitch.visible = false;
        cheekFurSwitch.visible = false;
        legsFurSwitch.visible = false;
        headFurSwitch.visible = false;
        backFurSwitch.visible = false;
        tailFurSwitch.visible = false;
        editChimera.visible = false;
        chimeraSwitch.visible = false;

        saveStoredMorph.visible = false;
        loadStoredMorph.visible = false;
        backFromStoredMorphMenu.visible = false;
        storedMorphsMenu.visible = false;
        loadSelectedMorphButton.visible = false;
        cancelMorphLoad.visible = false;
        confirmSaveMorph.visible = false;
        cancelMorphSave.visible = false;
        overwriteSelectedMorph.visible = false;
        removeSelectedMorph.visible = false;
        sendSelectedMorphToChat.visible = false;
        newMorphButton.visible = false;
        randomizeGeneticsButton.visible = false;

        if (onGeneticalSkin) {
            baseSection.visible = true;
            orangeSection.visible = true;
            whiteSection.visible = true;
            albinoSection.visible = true;
            diluteSection.visible = true;
            eyesAndAnomalySection.visible = true;
            furBobtailNoiseSection.visible = true;
            extraSection.visible = true;
            chimeraSection.visible = true;
            agoutiAndTabbySection.visible = true;
            silverSection.visible = true;

            newMorphButton.visible = true;
            randomizeGeneticsButton.visible = true;

            storedMorphsMenu.visible = true;
            storedMorphsMenu.active = true;
            if (onStoredMorphsMenu) {
                storedMorphsMenu.active = false;
                newMorphButton.visible = false;
                randomizeGeneticsButton.visible = false;

                pGuiGraphics.pose().translate(0,0,390);
                pGuiGraphics.fill(centerX-80, centerY-20, centerX+80, centerY+115, 0x11000000);

                if (storedMorphsKey.equals("main")) {
                    saveStoredMorph.visible = true;
                    loadStoredMorph.visible = true;
                    backFromStoredMorphMenu.visible = true;
                }
                if (storedMorphsKey.equals("load")) {
                    pGuiGraphics.renderOutline(centerX - 66, centerY-17, 135, 100, 0x22FFFFFF);
                    loadSelectedMorphButton.visible = true;
                    cancelMorphLoad.visible = true;
                    overwriteSelectedMorph.visible = true;
                    removeSelectedMorph.visible = true;
                    sendSelectedMorphToChat.visible = true;

                }
                if (storedMorphsKey.equals("save")) {
                    confirmSaveMorph.visible = true;
                    cancelMorphSave.visible = true;
                }


                pGuiGraphics.pose().translate(0,0,-390);

            }


            if (mainSectionActiveMenu.equals("base")) {
                setBlackButton.visible = true;
                setChocolateButton.visible = true;
                setCinnamonButton.visible = true;

                pGuiGraphics.drawCenteredString(font, "Black base", centerX - 125, centerY - 30, 0xFFFFFFFF);
            }
            if (mainSectionActiveMenu.equals("orange")) {
                setOrangeButton.visible = true;
                setTortieButton.visible = true;
                setNotOrangeButton.visible = true;
//                orangeVariantList.setVisible(true);

                if (orangeVariantList.getSelectedEntry() != null) {
                    orangeBaseVariant = orangeVariantList.getSelectedEntry().getId();
                }

                pGuiGraphics.drawCenteredString(font, "Orange base", centerX - 125, centerY - 30, 0xFFFFFFFF);
            }

            if (mainSectionActiveMenu.equals("white")) {
                setFullWhiteButton.visible = true;
                setHighWhiteButton.visible = true;
                setLowWhiteButton.visible = true;
                setNoWhiteButton.visible = true;
//                whiteVariantList.setVisible(true);

                if (whiteVariantList.getSelectedEntry() != null) {
                    whiteRatioVariant = whiteVariantList.getSelectedEntry().getId();
                }

                pGuiGraphics.drawCenteredString(font, "White", centerX - 125, centerY - 30, 0xFFFFFFFF);
            }

            if (mainSectionActiveMenu.equals("albinism")) {
                setNotAlbinoButton.visible = true;
                setTrueAlbinoButton.visible = true;
                setMinkButton.visible = true;
                setSepiaButton.visible = true;
                setSiameseButton.visible = true;
//                albinoVariantList.setVisible(true);

                if (albinoVariantList.getSelectedEntry() != null) {
                    albinoVariant = albinoVariantList.getSelectedEntry().getId();
                }

                pGuiGraphics.drawCenteredString(font, "Albinism", centerX - 125, centerY - 30, 0xFFFFFFFF);
            }

            if (mainSectionActiveMenu.equals("dilute")) {
                diluteSwitch.visible = true;
                pGuiGraphics.drawCenteredString(font, "Dilute", centerX - 125, centerY - 30, 0xFFFFFFFF);
            }

            if (mainSectionActiveMenu.equals("agouti")) {
                agoutiSwitch.visible = true;
                setClassicButton.visible = true;
                setMackerelButton.visible = true;
//                tabbyVariantList.setVisible(true);

                if (tabbyVariantList.getSelectedEntry() != null) {
                    tabbyStripesVariant = tabbyVariantList.getSelectedEntry().getId();
                }

                pGuiGraphics.drawCenteredString(font, "Agouti", centerX - 125, centerY - 30, 0xFFFFFFFF);
            }

            if (mainSectionActiveMenu.equals("silver")) {
                silverSwitch.visible = true;
//                silverVariantList.setVisible(true);

                if (silverVariantList.getSelectedEntry() != null) {
                    silverVar = silverVariantList.getSelectedEntry().getId();
                }

                pGuiGraphics.drawCenteredString(font, "Silver", centerX - 125, centerY - 30, 0xFFFFFFFF);
            }

            if (mainSectionActiveMenu.equals("eyes")) {
                heterochromiaSwitch.visible = true;
                setLeftEyeYellowButton.visible = true;
                setLeftEyeGreenButton.visible = true;
                setLeftEyeBlueButton.visible = true;
                setLeftEyeRedButton.visible = true;
                setLeftEyeBlindButton.visible = true;

                setRightEyeYellowButton.visible = true;
                setRightEyeGreenButton.visible = true;
                setRightEyeBlueButton.visible = true;
                setRightEyeRedButton.visible = true;
                setRightEyeBlindButton.visible = true;


                if (heterochromiaSwitch.getValue()) {

                    pGuiGraphics.pose().pushPose();
                    float eyeTextScale = 0.8f;
                    pGuiGraphics.pose().translate(centerX-125, centerY + 7, 0);
                    pGuiGraphics.pose().scale(eyeTextScale, eyeTextScale, eyeTextScale);
                    pGuiGraphics.drawCenteredString(font, "Left", -25, 0, 0xFFFFFFFF);
                    pGuiGraphics.drawCenteredString(font, "Right", 25, 0, 0xFFFFFFFF);
                    pGuiGraphics.pose().popPose();

                    pGuiGraphics.pose().pushPose();
                    pGuiGraphics.pose().translate(centerX+110, centerY - 3, 0);
                    pGuiGraphics.pose().scale(eyeTextScale*eyeTextScale, eyeTextScale*eyeTextScale, eyeTextScale);
                    pGuiGraphics.drawCenteredString(font, "Left", 0, -60, 0xFFFFFFFF);
                    pGuiGraphics.drawCenteredString(font, "Right", 0, 65, 0xFFFFFFFF);
                    pGuiGraphics.pose().popPose();

                    setRightEyeYellowButton.active = true;
                    setRightEyeGreenButton.active = true;
                    setRightEyeBlueButton.active = true;
                    setRightEyeRedButton.active = true;
                    setRightEyeBlindButton.active = true;
                } else {
                    setRightEyeYellowButton.active = false;
                    setRightEyeGreenButton.active = false;
                    setRightEyeBlueButton.active = false;
                    setRightEyeRedButton.active = false;
                    setRightEyeBlindButton.active = false;

                    eyeColorLeft = eyeColorRight;
                }

                if (leftEyeVariantList.getSelectedEntry() != null) {
                    eyeColorVariantRight = leftEyeVariantList.getSelectedEntry().getId();
                    if (!heterochromiaSwitch.getValue()) eyeColorVariantLeft = leftEyeVariantList.getSelectedEntry().getId();
                    if (heterochromiaSwitch.getValue() && rightEyeVariantList.getSelectedEntry() != null) {
                        eyeColorVariantLeft = rightEyeVariantList.getSelectedEntry().getId();
                    }
                } else {
                    if (rightEyeVariantList.getSelectedEntry() != null) {
                        eyeColorVariantLeft = rightEyeVariantList.getSelectedEntry().getId();
                    }
                }

                pGuiGraphics.drawCenteredString(font, "Eyes", centerX - 125, centerY - 30, 0xFFFFFFFF);
            }

            if (mainSectionActiveMenu.equals("details")) {
                bobtailSwitch.visible = true;
                chestFurSwitch.visible = true;
                bellyFurSwitch.visible = true;
                legsFurSwitch.visible = true;
                headFurSwitch.visible = true;
                backFurSwitch.visible = true;
                cheekFurSwitch.visible = true;
                tailFurSwitch.visible = true;

                if (noiseList.getSelectedEntry() != null) {
                    genetics.noise = noiseList.getSelectedEntry().getId();
                    noise = noiseList.getSelectedEntry().getId();
                }

                size = sizeSlider.getActualValue();

                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().translate(centerX + 130, centerY - 42, 0);
                pGuiGraphics.pose().scale(0.7f,0.7f,0.7f);
                pGuiGraphics.drawCenteredString(font, Component.literal("¡The usual is 0.9!").withStyle(ChatFormatting.ITALIC),
                        0, 0, 0x44FFFFFF);
                pGuiGraphics.pose().popPose();

                pGuiGraphics.drawCenteredString(font, "Details", 66, centerY - 30, 0xFFFFFFFF);

            }

            if (mainSectionActiveMenu.equals("extra")) {

                rufousingVariant = rufousingSlider.getActualValue();
                blueRufousingVariant = blueRufousingSlider.getActualValue();
                if (scarsList.getSelectedEntry() != null) {
                    scars = scarsList.getSelectedEntry().getId();
                }

                pGuiGraphics.drawCenteredString(font, "Extra", centerX - 125, centerY - 30, 0xFFFFFFFF);
                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().translate(centerX - 125, centerY - 3, 0);
                pGuiGraphics.pose().scale(0.8f,0.8f,0.7f);
                pGuiGraphics.drawCenteredString(font, "Rufousing", 0, 0, 0xFFFFFFFF);
                pGuiGraphics.drawCenteredString(font, "Blue tint", 0, 50, 0xFFFFFFFF);
                pGuiGraphics.pose().popPose();

            }

            if (mainSectionActiveMenu.equals("chimera")) {
                if (chimeraSwitch.getValue()) {
                    editChimera.visible = true;
                }
                chimeraSwitch.visible = true;

                pGuiGraphics.drawCenteredString(font, "Chimerism", centerX - 125, centerY - 30, 0xFFFFFFFF);
            }

        } else {

            bobtailSwitch.visible = true;
            chestFurSwitch.visible = true;
            bellyFurSwitch.visible = true;
            legsFurSwitch.visible = true;
            headFurSwitch.visible = true;
            backFurSwitch.visible = true;
            cheekFurSwitch.visible = true;
            tailFurSwitch.visible = true;

            size = sizeSliderPreset.getActualValue();

        }

        {
            WCatEntity entityToRender = new WCatEntity(ModEntities.WCAT.get(), Minecraft.getInstance().level);

            entityToRender.setPlayerBoundUuid(UUID.nameUUIDFromBytes(ModEntities.WCAT.get().toString().getBytes()));
            entityToRender.setShowMorphName(false);

            entityToRender.setOnGeneticalSkin(onGeneticalSkin);
            entityToRender.setGenetics(genetics);
            entityToRender.setGender(1);

            entityToRender.setGeneticalVariants(eyeColorLeft, eyeColorRight, rufousingVariant, blueRufousingVariant,
                    orangeBaseVariant, whiteRatioVariant, tabbyStripesVariant, albinoVariant, eyeColorVariantLeft,
                    eyeColorVariantRight, noise, size, silverVar, scars);

            geneticsChimera.chimeraGene = chimeraGene;
            entityToRender.setChimeraGenetics(geneticsChimera);
            entityToRender.setGeneticalVariantsChimera(variantsChimera.chimeraVariant, variantsChimera.rufousingVariant,
                    variantsChimera.blueRufousingVariant, variantsChimera.orangeVar, variantsChimera.whiteVar, variantsChimera.tabbyVar,
                    variantsChimera.albinoVar, variantsChimera.noise, variantsChimera.silverVar);

            entityToRender.setOnGround(true);
            if (variantScrollList.getSelectedEntry() != null) {
                entityToRender.setVariant(variantScrollList.getSelectedEntry().getId());
            }
            entityToRender.setYRot(0);
            entityToRender.yHeadRot = 0;
            entityToRender.yBodyRot = 0;


            pGuiGraphics.renderOutline(centerX - 90, centerY - 40, 180, 157, 0x44FFFFFF);
            pGuiGraphics.fill(centerX - 90, centerY - 40, centerX + 90, centerY + 115, 0x07FFFFFF);

            {
                pGuiGraphics.pose().pushPose();

                pGuiGraphics.pose().translate(0, 0, 220);

                float scale = 1f;
                if (this.variantScrollList.getSelectedEntry() != null && onGeneticalSkin) {
                    variantScrollList.setSelected(null);
                }

                pGuiGraphics.pose().scale(scale, scale, scale);

//                pGuiGraphics.fill(centerX-90, centerY-40,
//                        centerX+90, centerY+115, 0xFFFFFFFF);

                InventoryScreen.renderEntityInInventoryFollowsMouse(
                        pGuiGraphics,
                         centerX-90,
                        centerY-40,
                        centerX + 90,
                        centerY + 115,
                        100,
                        0.2f,
                        (float) (pMouseX),
                        (float) ((15) + pMouseY),
                        entityToRender
                );

                pGuiGraphics.pose().popPose();
            }
        }
        pGuiGraphics.pose().translate(0, 0, 400);

        if (displayErrorTime > 0) {
            ChatFormatting color;
            if (isAnError) color = ChatFormatting.RED;
            else color = ChatFormatting.GREEN;
            int xOffset = font.width(displayErrorText)/2;
            int xO = centerX - xOffset - 12;

            float alpha = (float) displayErrorTime / 20;

            float finalAlpha = Mth.clamp(alpha, 0f, 1f);

            pGuiGraphics.setColor(1f,1f,1f,finalAlpha);
            pGuiGraphics.renderTooltip(font, Component.literal(displayErrorText).withStyle(color), xO, centerY + 100);
            pGuiGraphics.setColor(1f,1f,1f,1f);
        }

        for(Renderable renderable : this.renderables) {
            renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        pGuiGraphics.pose().popPose();
    }

    private void selectMode(GradientToggleButton button) {
        setOnPresetSkinButton.setSelected(false);
        setOnGeneticalSkinButton.setSelected(false);

        button.setSelected(true);
    }

    private void selectMainSection(GradientToggleButton button) {
        baseSection.setSelected(false);
        orangeSection.setSelected(false);
        whiteSection.setSelected(false);
        albinoSection.setSelected(false);
        diluteSection.setSelected(false);
        agoutiAndTabbySection.setSelected(false);
        eyesAndAnomalySection.setSelected(false);
        furBobtailNoiseSection.setSelected(false);
        extraSection.setSelected(false);
        chimeraSection.setSelected(false);
        silverSection.setSelected(false);

        this.removeWidget(orangeVariantList);
        this.removeWidget(whiteVariantList);
        this.removeWidget(albinoVariantList);
        this.removeWidget(tabbyVariantList);
        this.removeWidget(leftEyeVariantList);
        this.removeWidget(rightEyeVariantList);
        this.removeWidget(noiseList);
        this.removeWidget(sizeSlider);
        this.removeWidget(rufousingSlider);
        this.removeWidget(blueRufousingSlider);
        this.removeWidget(silverVariantList);
        this.removeWidget(scarsList);

        if (button != null) button.setSelected(true);
    }

    private void selectBase(GradientToggleButton button) {
        setBlackButton.setSelected(false);
        setChocolateButton.setSelected(false);
        setCinnamonButton.setSelected(false);

        button.setSelected(true);
    }

    private void selectOrange(GradientToggleButton button) {
        setOrangeButton.setSelected(false);
        setTortieButton.setSelected(false);
        setNotOrangeButton.setSelected(false);

        button.setSelected(true);
    }

    private void selectWhite(GradientToggleButton button) {
        setFullWhiteButton.setSelected(false);
        setHighWhiteButton.setSelected(false);
        setLowWhiteButton.setSelected(false);
        setNoWhiteButton.setSelected(false);

        button.setSelected(true);
    }

    private void selectAlbino(GradientToggleButton button) {
        setNotAlbinoButton.setSelected(false);
        setTrueAlbinoButton.setSelected(false);
        setSiameseButton.setSelected(false);
        setMinkButton.setSelected(false);
        setSepiaButton.setSelected(false);

        button.setSelected(true);
    }

    private void selectAgoutiPattern(GradientToggleButton button) {
        setMackerelButton.setSelected(false);
        setClassicButton.setSelected(false);

        button.setSelected(true);
    }

    private void selectLeftEye(GradientToggleButton button) {
        setLeftEyeYellowButton.setSelected(false);
        setLeftEyeBlueButton.setSelected(false);
        setLeftEyeGreenButton.setSelected(false);
        setLeftEyeRedButton.setSelected(false);
        setLeftEyeBlindButton.setSelected(false);

        button.setSelected(true);
    }

    private void selectRightEye(GradientToggleButton button) {
        setRightEyeYellowButton.setSelected(false);
        setRightEyeBlueButton.setSelected(false);
        setRightEyeGreenButton.setSelected(false);
        setRightEyeRedButton.setSelected(false);
        setRightEyeBlindButton.setSelected(false);

        button.setSelected(true);
    }

    private void saveAndNext() {

        WCGenetics.GeneticalVariants variants =
                new WCGenetics.GeneticalVariants(eyeColorLeft, eyeColorRight,
                        rufousingVariant, blueRufousingVariant, orangeBaseVariant, whiteRatioVariant,
                        tabbyStripesVariant, albinoVariant, eyeColorVariantLeft, eyeColorVariantRight, noise, size, silverVar, scars);

        int defaultVariant = 0;
        if (variantScrollList.getSelectedEntry() != null) defaultVariant = variantScrollList.getSelectedEntry().getId();

        geneticsChimera.chimeraGene = chimeraGene;
        genetics.chimeraGene = chimeraGene;

        if (goingToChimeraMenu) {
            this.minecraft.setScreen(new CreateChimeraMorphGeneticsScreen(onGeneticalSkin, genetics, variants, geneticsChimera, variantsChimera, this.isSummoning));
            return;
        }

        if (this.isSummoning) {
            ModPackets.sendToServer(new SummonCustomCatPacket(onGeneticalSkin, genetics, variants, geneticsChimera, variantsChimera));
            this.minecraft.setScreen(null);
            return;
        }

        ModPackets.sendToServer(new SavePlayerGeneticsPacket(onGeneticalSkin, genetics, variants, geneticsChimera, variantsChimera, defaultVariant));


        this.minecraft.setScreen(null);
        if (!ClientClanData.get().isFirstLoginHandled()){
            if (WCEServerConfig.SERVER.TELEPORT_WHEN_JOIN.get()) this.minecraft.setScreen(new SpawnLocationScreen());
            else this.minecraft.setScreen(null);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            if (!ClientClanData.get().isFirstLoginHandled()) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (variantScrollList.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }


    private void loadStoredMorphsList() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        savedMorphsScrollList = new MorphsScrollList(Minecraft.getInstance(),
                135, 160, centerY-16, centerY+80, 22);

        savedMorphsScrollList.setX(centerX -75);
//        savedMorphsScrollList.setRenderBackground(false);
//        savedMorphsScrollList.setRenderTopAndBottom(false);

        for (Map.Entry<String, ClientStoredMorphs.MorphsFile.MorphData> entry : ClientStoredMorphs.DATA.morphs.entrySet()) {
            savedMorphsScrollList.addOption(entry.getKey(), entry.getValue());
        }

        this.addRenderableWidget(savedMorphsScrollList);

    }

    private void loadSelectedMorph() {
        if (savedMorphsScrollList.getSelectedEntry() != null) {
            String key = savedMorphsScrollList.getSelectedEntry().getKey();
            ClientStoredMorphs.MorphsFile.MorphData data = ClientStoredMorphs.DATA.morphs.get(key);
            if (data != null) {

                WCGenetics geneticsCopy = new WCGenetics(data.genetics());
                WCGenetics chimeraGeneticsCopy = new WCGenetics(data.chimeraGenetics());

                WCGenetics.GeneticalVariants variantsCopy = new WCGenetics.GeneticalVariants(data.variants());
                WCGenetics.GeneticalChimeraVariants chimeraVariantsCopy = new WCGenetics.GeneticalChimeraVariants(data.chimeraVariants());

                genetics = geneticsCopy;
                geneticsChimera = chimeraGeneticsCopy;
                variantsChimera = chimeraVariantsCopy;

                chimeraGene = chimeraGeneticsCopy.chimeraGene;

                eyeColorLeft = variantsCopy.eyeColorLeft;
                eyeColorRight = variantsCopy.eyeColorRight;
                rufousingVariant = variantsCopy.rufousingVariant;
                blueRufousingVariant = variantsCopy.blueRufousingVariant;
                orangeBaseVariant = variantsCopy.orangeVar;
                whiteRatioVariant = variantsCopy.whiteVar;
                tabbyStripesVariant = variantsCopy.tabbyVar;
                albinoVariant = variantsCopy.albinoVar;
                eyeColorVariantLeft = variantsCopy.leftEyeVar;
                eyeColorVariantRight = variantsCopy.rightEyeVar;
                noise = variantsCopy.noise;
                size = variantsCopy.size;
                silverVar = variantsCopy.silverVar;
                scars = variantsCopy.scars;

            } else {
                displayError("Morph data not present.", true);
            }
        } else {
            displayError("No morph selected.", true);
        }
    }

    private void loadSaveMorph() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        saveMorphNameBox = new EditBox(Minecraft.getInstance().font,
                centerX-50, centerY+20, 100, 15, Component.literal("Name"));

        saveMorphNameBox.setHint(Component.literal("Name / Key").withStyle(ChatFormatting.DARK_GRAY));

        this.addRenderableWidget(saveMorphNameBox);
    }

    private void saveSelectedMorph() {

        WCGenetics geneticsCopy = new WCGenetics(genetics.bobtail, genetics.chestFur, genetics.bellyFur, genetics.legsFur
        , genetics.headFur, genetics.cheekFur, genetics.tailFur, genetics.backFur, genetics.base, genetics.orangeBase, genetics.whiteRatio
        , genetics.albino, genetics.dilute, genetics.agouti, genetics.tabbyStripes, genetics.eyesAnomaly, genetics.rufousing
        , genetics.blueRufousing, genetics.noise, genetics.chimeraGene, genetics.silver);

        WCGenetics geneticsChimeraCopy = new WCGenetics(geneticsChimera.bobtail, geneticsChimera.chestFur, geneticsChimera.bellyFur, geneticsChimera.legsFur
                , geneticsChimera.headFur, geneticsChimera.cheekFur, geneticsChimera.tailFur, geneticsChimera.backFur, geneticsChimera.base, geneticsChimera.orangeBase, geneticsChimera.whiteRatio
                , geneticsChimera.albino, geneticsChimera.dilute, geneticsChimera.agouti, geneticsChimera.tabbyStripes, geneticsChimera.eyesAnomaly, geneticsChimera.rufousing
                , geneticsChimera.blueRufousing, geneticsChimera.noise, geneticsChimera.chimeraGene, geneticsChimera.silver);

        WCGenetics.GeneticalVariants variants = new WCGenetics.GeneticalVariants(eyeColorLeft, eyeColorRight,
                        rufousingVariant, blueRufousingVariant, orangeBaseVariant, whiteRatioVariant,
                        tabbyStripesVariant, albinoVariant, eyeColorVariantLeft, eyeColorVariantRight,
                        noise, size, silverVar, scars);

        WCGenetics.GeneticalChimeraVariants chimeraVariantsCopy = new WCGenetics.GeneticalChimeraVariants(variantsChimera.chimeraVariant,
                variantsChimera.rufousingVariant, variantsChimera.blueRufousingVariant, variantsChimera.orangeVar, variantsChimera.whiteVar
        , variantsChimera.tabbyVar, variantsChimera.albinoVar, variantsChimera.noise, variantsChimera.chimeraGene, variantsChimera.silverVar);

        ClientStoredMorphs.MorphsFile.MorphData data =
                new ClientStoredMorphs.MorphsFile.MorphData(geneticsCopy, geneticsChimeraCopy, variants, chimeraVariantsCopy);

        if (!saveMorphNameBox.getValue().equals("")) {
            boolean success = ClientStoredMorphs.add(saveMorphNameBox.getValue(), data, false);

            if (success) {
                displayError("Morph saved with key: " + saveMorphNameBox.getValue(), false);
            } else {
                displayError("A morph with provided key already exists: " + "'" + saveMorphNameBox.getValue() + "'", true);
            }
        } else {
            displayError("No name or key provided.", true);
        }

    }

    private void displayError(String error, boolean isAnError) {
        displayErrorTime = 100;
        displayErrorText = error;
        this.isAnError = isAnError;
    }

    @Override
    public void tick() {
        if (displayErrorTime > 0) displayErrorTime--;
        else {
            removeSelectedMorphConfirm = false;
            overwriteSelectedMorphConfirm = false;
        }
        super.tick();
    }

    private void overwriteSelectedMorph() {
        if (savedMorphsScrollList.getSelectedEntry() != null) {
            String key = savedMorphsScrollList.getSelectedEntry().getKey();
            ClientStoredMorphs.MorphsFile.MorphData data = ClientStoredMorphs.DATA.morphs.get(key);
            if (data != null) {

                WCGenetics geneticsCopy = new WCGenetics(genetics.bobtail, genetics.chestFur, genetics.bellyFur, genetics.legsFur
                        , genetics.headFur, genetics.cheekFur, genetics.tailFur, genetics.backFur, genetics.base, genetics.orangeBase, genetics.whiteRatio
                        , genetics.albino, genetics.dilute, genetics.agouti, genetics.tabbyStripes, genetics.eyesAnomaly, genetics.rufousing
                        , genetics.blueRufousing, genetics.noise, genetics.chimeraGene, genetics.silver);

                WCGenetics geneticsChimeraCopy = new WCGenetics(geneticsChimera.bobtail, geneticsChimera.chestFur, geneticsChimera.bellyFur, geneticsChimera.legsFur
                        , geneticsChimera.headFur, geneticsChimera.cheekFur, geneticsChimera.tailFur, geneticsChimera.backFur, geneticsChimera.base, geneticsChimera.orangeBase, geneticsChimera.whiteRatio
                        , geneticsChimera.albino, geneticsChimera.dilute, geneticsChimera.agouti, geneticsChimera.tabbyStripes, geneticsChimera.eyesAnomaly, geneticsChimera.rufousing
                        , geneticsChimera.blueRufousing, geneticsChimera.noise, geneticsChimera.chimeraGene, geneticsChimera.silver);

                WCGenetics.GeneticalVariants variants = new WCGenetics.GeneticalVariants(eyeColorLeft, eyeColorRight,
                        rufousingVariant, blueRufousingVariant, orangeBaseVariant, whiteRatioVariant,
                        tabbyStripesVariant, albinoVariant, eyeColorVariantLeft, eyeColorVariantRight,
                        noise, size, silverVar, scars);

                WCGenetics.GeneticalChimeraVariants chimeraVariantsCopy = new WCGenetics.GeneticalChimeraVariants(variantsChimera.chimeraVariant,
                        variantsChimera.rufousingVariant, variantsChimera.blueRufousingVariant, variantsChimera.orangeVar, variantsChimera.whiteVar
                        , variantsChimera.tabbyVar, variantsChimera.albinoVar, variantsChimera.noise, variantsChimera.chimeraGene, variantsChimera.silverVar);

                ClientStoredMorphs.MorphsFile.MorphData newData =
                        new ClientStoredMorphs.MorphsFile.MorphData(geneticsCopy, geneticsChimeraCopy, variants, chimeraVariantsCopy);

                boolean success = ClientStoredMorphs.add(key, newData, true);

                if (success) {
                    displayError("Morph overwritten with key: " + key, false);
                } else {
                    displayError("Couldn't overwrite key: " + key, true);
                }

            } else {
                displayError("Key not present.", true);
            }
        } else {
            displayError("No morph selected.", true);
        }
    }

    private void deleteSelectedMorph() {
        if (savedMorphsScrollList.getSelectedEntry() != null) {
            String key = savedMorphsScrollList.getSelectedEntry().getKey();
            ClientStoredMorphs.MorphsFile.MorphData data = ClientStoredMorphs.DATA.morphs.get(key);
            if (data != null) {

                boolean success = ClientStoredMorphs.remove(key);

                if (success) {
                    displayError("Morph deleted with key: " + key, false);
                } else {
                    displayError("Couldn't remove key: " + key, true);
                }

            } else {
                displayError("Key not present.", true);
            }
        } else {
            displayError("No morph selected.", true);
        }
    }

    private void sendMorphToChat() {
        if (savedMorphsScrollList.getSelectedEntry() != null) {
            String key = savedMorphsScrollList.getSelectedEntry().getKey();
            ClientStoredMorphs.MorphsFile.MorphData data = ClientStoredMorphs.DATA.morphs.get(key);
            if (data != null) {

                WCGenetics geneticsCopy = data.genetics();

                WCGenetics geneticsChimeraCopy = data.chimeraGenetics();

                WCGenetics.GeneticalVariants variants = data.variants();

                WCGenetics.GeneticalChimeraVariants chimeraVariantsCopy = data.chimeraVariants();

                ModPackets.sendToServer(new CtSShareMorphToChat(key, geneticsCopy, variants, geneticsChimeraCopy, chimeraVariantsCopy));
                Minecraft.getInstance().setScreen(null);

                if (Minecraft.getInstance().player != null) {
                    LocalPlayer player = Minecraft.getInstance().player;

                    Path path = Minecraft.getInstance().gameDirectory.toPath().resolve("config")
                            .resolve(WarriorCatsEvents.MODID);

                    Component filePath = Component.literal(" [File]")
                            .withStyle(style -> style.withClickEvent(
                                    new ClickEvent(ClickEvent.Action.OPEN_FILE, path.toString())
                            ).withHoverEvent(
                                    new HoverEvent(
                                            HoverEvent.Action.SHOW_TEXT,
                                            Component.literal("Open JSON")
                                                    .withStyle(ChatFormatting.GRAY)
                                    )
                            ));

                    player.sendSystemMessage(Component.literal("Morph with key '" + key + "' shared to chat. From:").withStyle(ChatFormatting.DARK_GRAY)
                            .append(filePath.copy().withStyle(ChatFormatting.DARK_GRAY)));
                }

            } else {
                displayError("Key not present.", true);
            }
        } else {
            displayError("No morph selected.", true);
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(10f,10f,1f);
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.pose().popPose();
    }
}
