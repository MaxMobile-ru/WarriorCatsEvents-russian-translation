package net.snowteb.warriorcats_events.screen.screens.createmorph;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class AddSubRenderables {

    public static class Genetics {

        public static void addPresetWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.details"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 155);

            class DetailsWidgets {
                FancySwitchButton bobtailSwitch;
                FancySwitchButton chestFurSwitch;
                FancySwitchButton bellyFurSwitch;
                FancySwitchButton legsFurSwitch;
                FancySwitchButton headFurSwitch;
                FancySwitchButton cheekFurSwitch;
                FancySwitchButton backFurSwitch;
                FancySwitchButton tailFurSwitch;

                FancyFloatSlider sizeSlider;
            }

            DetailsWidgets w = new DetailsWidgets();

            yPos = screen.addSubRenderable(text, yPos);

            String key1 = "screen.button.bobtail";
            String key2 = "screen.button.chestfur";
            String key3 = "screen.button.bellyfur";
            String key4 = "screen.button.legsfur";
            String key5 = "screen.button.headfur";
            String key6 = "screen.button.cheekfur";
            String key7 = "screen.button.backfur";
            String key8 = "screen.button.tailfur";

            float scale = 0.83f;

            w.bobtailSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key1, WCGenetics.Bobtail.isBobtail(screen.genetics.bobtail),
                    b -> {
                        screen.genetics.bobtail = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setBobtail : CreateMorphConstants.setFulltail;

                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.bobtailSwitch, yPos);

            w.chestFurSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key2, WCGenetics.FurGene.isLongFur(screen.genetics.chestFur),
                    b -> {
                        screen.genetics.chestFur = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setLongFur : CreateMorphConstants.setShortFur;

                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.chestFurSwitch, yPos);

            w.bellyFurSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key3, WCGenetics.FurGene.isLongFur(screen.genetics.bellyFur),
                    b -> {
                        screen.genetics.bellyFur = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setLongFur : CreateMorphConstants.setShortFur;

                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.bellyFurSwitch, yPos);

            w.legsFurSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key4, WCGenetics.FurGene.isLongFur(screen.genetics.legsFur),
                    b -> {
                        screen.genetics.legsFur = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setLongFur : CreateMorphConstants.setShortFur;

                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.legsFurSwitch, yPos);

            w.headFurSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key5, WCGenetics.FurGene.isLongFur(screen.genetics.headFur),
                    b -> {
                        screen.genetics.headFur = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setLongFur : CreateMorphConstants.setShortFur;

                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.headFurSwitch, yPos);

            w.cheekFurSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key6, WCGenetics.FurGene.isLongFur(screen.genetics.cheekFur),
                    b -> {
                        screen.genetics.cheekFur = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setLongFur : CreateMorphConstants.setShortFur;

                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.cheekFurSwitch, yPos);

            w.backFurSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key7, WCGenetics.FurGene.isLongFur(screen.genetics.backFur),
                    b -> {
                        screen.genetics.backFur = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setLongFur : CreateMorphConstants.setShortFur;

                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.backFurSwitch, yPos);

            w.tailFurSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key8, WCGenetics.FurGene.isLongFur(screen.genetics.tailFur),
                    b -> {
                        screen.genetics.tailFur = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setLongFur : CreateMorphConstants.setShortFur;

                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.tailFurSwitch, yPos);

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

            w.sizeSlider = new FancyFloatSlider(xPos0, yPos,
                    width - padding*2, 15,
                    0.4f, maxSize,
                    screen.variants.size, (button) -> {
                screen.variants.size = button.getActualValue();
            }, Component.translatable("screen.cat.size"));

            yPos = screen.addSubRenderable(w.sizeSlider, yPos);
        }

        public static void addBaseWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.base_color"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 65);

            yPos = screen.addSubRenderable(text, yPos);

            FancyButtonScrollList otherlist = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 65, 20);

            otherlist.setX(xPos0);

            otherlist.addButton(Component.translatable("screen.cat.base_black"), () -> {
                screen.genetics.base = otherlist.getSelectedKey();
            }, Component.empty(), Component.empty(), CreateMorphConstants.setBlack);

            otherlist.addButton(Component.translatable("screen.cat.base_chocolate"), () -> {
                screen.genetics.base = otherlist.getSelectedKey();
            }, Component.empty(), Component.empty(), CreateMorphConstants.setChocolate);

            otherlist.addButton(Component.translatable("screen.cat.base_cinnamon"), () -> {
                screen.genetics.base = otherlist.getSelectedKey();
            }, Component.empty(), Component.empty(), CreateMorphConstants.setCinnamon);

            selectEntry(screen.genetics.base, otherlist);


            yPos = screen.addSubRenderable(otherlist, yPos);

        }

        public static void addOrangeWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.orange_base"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 50);

            class OrangeWidgets {
                FancySwitchButton orangeSwitch;
                FancySwitchButton tortieSwitch;
                FancyButtonScrollList orangeList;
                FancyStringWidget tortieVariantsText;
            }

            OrangeWidgets w = new OrangeWidgets();

            yPos = screen.addSubRenderable(text, yPos);

            String key1 = "screen.button.orange";
            String key2 = "screen.button.tortie";
            float scale = 0.83f;


            w.orangeSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key1, WCGenetics.OrangeBase.isOrange(screen.genetics.orangeBase, 1),
                    b -> {
                        screen.genetics.orangeBase = ((FancySwitchButton) b).getValue() ? CreateMorphConstants.setOrange : CreateMorphConstants.setNotOrange;
                        if (w.orangeSwitch.getValue()) {
                            w.tortieSwitch.setValue(false);
                            screen.removeSubRenderable(w.orangeList, screen.otherListHeight);
                            screen.removeSubRenderable(w.tortieVariantsText, screen.otherListHeight);
                            w.orangeList.setSelected(w.orangeList.getEntryByKey(String.valueOf(screen.variants.orangeVar)));
                            screen.recalculateListHeight();

                            screen.displayMessage(
                                    Component.translatable("screen.message.orange_pattern_available"),
                                    false, 100);
                        }
                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.orangeSwitch, yPos);

            w.tortieSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key2, WCGenetics.OrangeBase.isTortoiseshell(screen.genetics.orangeBase), b -> {
                screen.genetics.orangeBase = ((FancySwitchButton) b).getValue() ? CreateMorphConstants.setTortie : CreateMorphConstants.setNotOrange;
                if (w.tortieSwitch.getValue()) {
                    w.orangeSwitch.setValue(false);
                    screen.addSubRenderable(w.orangeList, screen.otherListHeight);
                    screen.addSubRenderable(w.tortieVariantsText, screen.otherListHeight);
                    w.orangeList.setSelected(w.orangeList.getEntryByKey(String.valueOf(screen.variants.orangeVar)));
                    screen.displayMessage(
                            Component.translatable("screen.message.orange_pattern_available"),
                            false, 100);
                } else {
                    screen.removeSubRenderable(w.orangeList, screen.otherListHeight);
                    screen.removeSubRenderable(w.tortieVariantsText, screen.otherListHeight);
                }
                screen.recalculateListHeight();
            }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.tortieSwitch, yPos);

            w.tortieVariantsText = new FancyStringWidget(Component.translatable("screen.cat.tortie_variants"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 0.9f, false, 105);


            yPos += w.tortieVariantsText.getWidgetHeight() + screen.getSubRenderableSpacing();

            w.orangeList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 100, 20);

            w.orangeList.setX(xPos0);

            for (int i = 0; i < WCGenetics.Constants.MAX_TORTIE_VARIANTS; i++) {
                w.orangeList.addButton(Component.translatable("screen.cat.orange_base_tortie", i+1), () -> {
                    screen.variants.orangeVar = Integer.parseInt(w.orangeList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
            }

            if (WCGenetics.OrangeBase.isTortoiseshell(screen.genetics.orangeBase)) {
                screen.addSubRenderable(w.tortieVariantsText, screen.otherListHeight);
                yPos = screen.addSubRenderable(w.orangeList, yPos);
                w.orangeList.setSelected(w.orangeList.getEntryByKey(String.valueOf(screen.variants.orangeVar)));
            }

        }

        public static void addWhiteWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.white_ratio"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 75);

            class WhiteWidgets {
                FancyButtonScrollList lowWhiteOptionsList;
                FancyButtonScrollList highWhiteOptionsList;
                FancyStringWidget lowWhiteVariantsText;
                FancyStringWidget highWhiteVariantsText;
            }

            WhiteWidgets w = new WhiteWidgets();

            yPos = screen.addSubRenderable(text, yPos);

            float scale = 0.83f;

            FancyListSlider listSlider = new FancyListSlider(xPos0, yPos,
                    width - padding*2, 15,
                    FancyListSlider.EntryBuilder.builder()
                            .add(Component.translatable("screen.button.no_white"), CreateMorphConstants.setNotWhite, key -> !WCGenetics.WhiteRatio.isWhite(key) && !WCGenetics.WhiteRatio.isHighSpotted(key) && !WCGenetics.WhiteRatio.isLowSpotted(key))
                            .add(Component.translatable("screen.button.low_white"), CreateMorphConstants.setLowWhite, WCGenetics.WhiteRatio::isLowSpotted)
                            .add(Component.translatable("screen.button.high_white"), CreateMorphConstants.setHighWhite, WCGenetics.WhiteRatio::isHighSpotted)
                            .add(Component.translatable("screen.button.full_white"), CreateMorphConstants.setFullWhite, WCGenetics.WhiteRatio::isWhite)
                            .build(), but -> {
                screen.genetics.whiteRatio = but.getActualValueKey();
                screen.removeSubRenderable(w.lowWhiteVariantsText, screen.otherListHeight);
                screen.removeSubRenderable(w.lowWhiteOptionsList, screen.otherListHeight);
                screen.removeSubRenderable(w.highWhiteVariantsText, screen.otherListHeight);
                screen.removeSubRenderable(w.highWhiteOptionsList, screen.otherListHeight);

                if (WCGenetics.WhiteRatio.isLowSpotted(screen.genetics.whiteRatio)) {
                    screen.addSubRenderable(w.lowWhiteVariantsText, screen.otherListHeight);
                    screen.addSubRenderable(w.lowWhiteOptionsList, screen.otherListHeight);
                } else if (WCGenetics.WhiteRatio.isHighSpotted(screen.genetics.whiteRatio)) {
                    screen.addSubRenderable(w.highWhiteVariantsText, screen.otherListHeight);
                    screen.addSubRenderable(w.highWhiteOptionsList, screen.otherListHeight);
                }

                screen.recalculateListHeight();
            });

            listSlider.setInitialValue(screen.genetics.whiteRatio);

            yPos = screen.addSubRenderable(listSlider, yPos);

            w.lowWhiteVariantsText = new FancyStringWidget(Component.translatable("screen.cat.low_white_variants"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 0.9f, false, 105);

            w.highWhiteVariantsText = new FancyStringWidget(Component.translatable("screen.cat.high_white_variants"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 0.9f, false, 105);


            yPos += w.highWhiteVariantsText.getWidgetHeight() + screen.getSubRenderableSpacing();


            w.lowWhiteOptionsList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 100, 20);
            w.highWhiteOptionsList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 100, 20);

            w.lowWhiteOptionsList.setX(xPos0);
            w.highWhiteOptionsList.setX(xPos0);

            for (int i = 0; i < WCGenetics.Constants.MAX_WHITE_VARIANTS_LOW; i++) {
                w.lowWhiteOptionsList.addButton(Component.translatable("screen.cat.white_ratio_low", i+1), () -> {
                    screen.variants.whiteVar = Integer.parseInt(w.lowWhiteOptionsList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
            }

            for (int i = 0; i < WCGenetics.Constants.MAX_WHITE_VARIANTS_HIGH; i++) {
                w.highWhiteOptionsList.addButton(Component.translatable("screen.cat.white_ratio_high", i+1), () -> {
                    screen.variants.whiteVar = Integer.parseInt(w.highWhiteOptionsList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
            }


            if (WCGenetics.WhiteRatio.isLowSpotted(screen.genetics.whiteRatio)) {
                screen.addSubRenderable(w.lowWhiteVariantsText, screen.otherListHeight);
                yPos = screen.addSubRenderable(w.lowWhiteOptionsList, yPos);
                w.lowWhiteOptionsList.setSelected(w.lowWhiteOptionsList.getEntryByKey(String.valueOf(screen.variants.whiteVar)));

            } else if (WCGenetics.WhiteRatio.isHighSpotted(screen.genetics.whiteRatio)) {
                screen.addSubRenderable(w.highWhiteVariantsText, screen.otherListHeight);
                yPos = screen.addSubRenderable(w.highWhiteOptionsList, yPos);
                w.highWhiteOptionsList.setSelected(w.highWhiteOptionsList.getEntryByKey(String.valueOf(screen.variants.whiteVar)));

            }

        }

        public static void addAlbinoWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.albinism_section"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 35);

            class AlbinoWidgets {

                FancyButtonScrollList sepiaOptionsList;
                FancyButtonScrollList minkOptionsList;
                FancyButtonScrollList siameseOptionsList;

                FancyStringWidget albinoVariantsText;
            }

            AlbinoWidgets w = new AlbinoWidgets();

            yPos = screen.addSubRenderable(text, yPos);

            float scale = 0.83f;

            FancyListSlider listSlider = new FancyListSlider(xPos0, yPos,
                    width - padding*2, 15,
                    FancyListSlider.EntryBuilder.builder()
                            .add(Component.translatable("screen.button.not_colorpoint"), CreateMorphConstants.setNotAlbino, WCGenetics.Albino::isNotAlbino)
                            .add(Component.translatable("screen.button.sepia"), CreateMorphConstants.setSepia, WCGenetics.Albino::isSepia)
                            .add(Component.translatable("screen.button.mink"), CreateMorphConstants.setMink, WCGenetics.Albino::isMink)
                            .add(Component.translatable("screen.button.siamese"), CreateMorphConstants.setSiamese, WCGenetics.Albino::isSiamese)
                            .add(Component.translatable("screen.button.true_albino"), CreateMorphConstants.setTrueAlbino, WCGenetics.Albino::isTrueAlbino)
                            .build(), but -> {
                screen.genetics.albino = but.getActualValueKey();
                screen.removeSubRenderable(w.albinoVariantsText, screen.otherListHeight);
                screen.removeSubRenderable(w.sepiaOptionsList, screen.otherListHeight);
                screen.removeSubRenderable(w.minkOptionsList, screen.otherListHeight);
                screen.removeSubRenderable(w.siameseOptionsList, screen.otherListHeight);

                if (WCGenetics.Albino.isSepia(screen.genetics.albino)) {
                    screen.addSubRenderable(w.albinoVariantsText, screen.otherListHeight);
                    screen.addSubRenderable(w.sepiaOptionsList, screen.otherListHeight);
                } else if (WCGenetics.Albino.isMink(screen.genetics.albino)) {
                    screen.addSubRenderable(w.albinoVariantsText, screen.otherListHeight);
                    screen.addSubRenderable(w.minkOptionsList, screen.otherListHeight);
                } else if (WCGenetics.Albino.isSiamese(screen.genetics.albino)) {
                    screen.addSubRenderable(w.albinoVariantsText, screen.otherListHeight);
                    screen.addSubRenderable(w.siameseOptionsList, screen.otherListHeight);
                }
                screen.recalculateListHeight();
            });

            listSlider.setInitialValue(screen.genetics.albino);

            yPos = screen.addSubRenderable(listSlider, yPos);


            w.albinoVariantsText = new FancyStringWidget(Component.translatable("screen.cat.albino_variants"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 0.9f, false, 105);

            yPos += w.albinoVariantsText.getWidgetHeight() + screen.getSubRenderableSpacing();


            w.minkOptionsList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 100, 20);
            w.sepiaOptionsList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 100, 20);
            w.siameseOptionsList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 100, 20);

            w.minkOptionsList.setX(xPos0);
            w.sepiaOptionsList.setX(xPos0);
            w.siameseOptionsList.setX(xPos0);

            for (int i = 0; i < WCGenetics.Constants.MAX_ALBINO_VARIANTS; i++) {
                w.minkOptionsList.addButton(Component.translatable("screen.cat.mink_var", i+1), () -> {
                    screen.variants.albinoVar = Integer.parseInt(w.minkOptionsList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
                w.sepiaOptionsList.addButton(Component.translatable("screen.cat.sepia_var", i+1), () -> {
                    screen.variants.albinoVar = Integer.parseInt(w.sepiaOptionsList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
                w.siameseOptionsList.addButton(Component.translatable("screen.cat.siamese_var", i+1), () -> {
                    screen.variants.albinoVar = Integer.parseInt(w.siameseOptionsList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
            }


            if (WCGenetics.Albino.isMink(screen.genetics.albino)) {
                yPos = screen.addSubRenderable(w.minkOptionsList, yPos);
                screen.addSubRenderable(w.albinoVariantsText, yPos);
                w.minkOptionsList.setSelected(w.minkOptionsList.getEntryByKey(String.valueOf(screen.variants.albinoVar)));
            } else if (WCGenetics.Albino.isSiamese(screen.genetics.albino)) {
                yPos = screen.addSubRenderable(w.siameseOptionsList, yPos);
                screen.addSubRenderable(w.albinoVariantsText, yPos);
                w.siameseOptionsList.setSelected(w.siameseOptionsList.getEntryByKey(String.valueOf(screen.variants.albinoVar)));
            } else if (WCGenetics.Albino.isSepia(screen.genetics.albino)) {
                yPos = screen.addSubRenderable(w.sepiaOptionsList, yPos);
                screen.addSubRenderable(w.albinoVariantsText, yPos);
                w.sepiaOptionsList.setSelected(w.sepiaOptionsList.getEntryByKey(String.valueOf(screen.variants.albinoVar)));
            }

        }

        public static void addDiluteWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.dilute_section"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 25);

            FancySwitchButton diluteSwitch;

            yPos = screen.addSubRenderable(text, yPos);

            String key1 = "screen.button.dilute";

            float scale = 0.83f;

            diluteSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key1, WCGenetics.Dilute.isDilute(screen.genetics.dilute),
                    b -> {
                        screen.genetics.dilute = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setDilute : CreateMorphConstants.setNonDilute;
                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(diluteSwitch, yPos);

        }

        public static void addAgoutiWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.agouti"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 25);

            class AgoutiWidgets {
                FancySwitchButton agoutiSwitch;

                FancyButtonScrollList agoutiOptionsList;

                FancyStringWidget patternsText;

                FancyButtonScrollList classicPatternsList;
                FancyButtonScrollList mackerelPatternsList;
            }

            AgoutiWidgets w = new AgoutiWidgets();

            yPos = screen.addSubRenderable(text, yPos);

            String key1 = "screen.button.agouti";

            float scale = 0.83f;

            boolean hasSilverGene = WCGenetics.Silver.isSilver(screen.genetics.silver, screen.genetics.agouti, screen.genetics.orangeBase, 1)
                    || WCGenetics.Silver.isSmoke(screen.genetics.silver, screen.genetics.agouti)
                    || WCGenetics.Silver.isSmokeTortie(screen.genetics.silver, screen.genetics.agouti, screen.genetics.orangeBase);

            boolean addLists = (hasSilverGene && screen.variants.silverVar == 2)
                    || (WCGenetics.OrangeBase.isOrange(screen.genetics.orangeBase, 1)
                    || WCGenetics.OrangeBase.isTortoiseshell(screen.genetics.orangeBase));

            w.agoutiSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key1, WCGenetics.Agouti.isTabby(screen.genetics.agouti),
                    b -> {

                        screen.genetics.agouti = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setAgoutiTabby : CreateMorphConstants.setNonAgoutiTabby;

                        if (w.agoutiSwitch.getValue()) {

                            screen.removeSubRenderable(w.patternsText, screen.otherListHeight);
                            screen.removeSubRenderable(w.agoutiOptionsList, screen.otherListHeight);
                            screen.removeSubRenderable(w.classicPatternsList, screen.otherListHeight);
                            screen.removeSubRenderable(w.mackerelPatternsList, screen.otherListHeight);

                            screen.addSubRenderable(w.patternsText, screen.otherListHeight);
                            screen.addSubRenderable(w.agoutiOptionsList, screen.otherListHeight);

                            if (WCGenetics.TabbyStripeTypes.isClassic(screen.genetics.tabbyStripes)) {
                                screen.addSubRenderable(w.classicPatternsList, screen.otherListHeight);
                            } else if (WCGenetics.TabbyStripeTypes.isMackerel(screen.genetics.tabbyStripes)) {
                                screen.addSubRenderable(w.mackerelPatternsList, screen.otherListHeight);
                            }

                            screen.recalculateListHeight();
                        } else {
                            if (!addLists) {
                                screen.removeSubRenderable(w.patternsText, screen.otherListHeight);
                                screen.removeSubRenderable(w.agoutiOptionsList, screen.otherListHeight);
                                screen.removeSubRenderable(w.classicPatternsList, screen.otherListHeight);
                                screen.removeSubRenderable(w.mackerelPatternsList, screen.otherListHeight);
                            }
                        }

                        screen.recalculateListHeight();
                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.agoutiSwitch, yPos);




            w.patternsText = new FancyStringWidget(Component.translatable("screen.cat.agouti_variants"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 0.9f, false, 155);

            yPos += w.patternsText.getWidgetHeight() + screen.getSubRenderableSpacing();



            w.agoutiOptionsList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 45, 20);

            w.agoutiOptionsList.setX(xPos0);

            w.agoutiOptionsList.addButton(Component.translatable("screen.cat.classic"), () -> {
                screen.genetics.tabbyStripes = w.agoutiOptionsList.getSelectedKey();
                screen.removeSubRenderable(w.classicPatternsList, screen.otherListHeight);
                screen.removeSubRenderable(w.mackerelPatternsList, screen.otherListHeight);

                screen.addSubRenderable(w.classicPatternsList, screen.otherListHeight);
                w.classicPatternsList.setSelected(w.classicPatternsList.getEntryByKey(String.valueOf(screen.variants.tabbyVar)));
                screen.recalculateListHeight();
            }, Component.empty(), Component.empty(), CreateMorphConstants.setTabbyStripesClassic);

            w.agoutiOptionsList.addButton(Component.translatable("screen.cat.mackerel"), () -> {
                screen.genetics.tabbyStripes = w.agoutiOptionsList.getSelectedKey();
                screen.removeSubRenderable(w.classicPatternsList, screen.otherListHeight);
                screen.removeSubRenderable(w.mackerelPatternsList, screen.otherListHeight);

                screen.addSubRenderable(w.mackerelPatternsList, screen.otherListHeight);
                w.mackerelPatternsList.setSelected(w.mackerelPatternsList.getEntryByKey(String.valueOf(screen.variants.tabbyVar)));
                screen.recalculateListHeight();
            }, Component.empty(), Component.empty(), CreateMorphConstants.setTabbyStripesMackerel);

            selectEntry(screen.genetics.tabbyStripes, w.agoutiOptionsList);

            yPos += w.agoutiOptionsList.getWidgetHeight() + screen.getSubRenderableSpacing();


            w.classicPatternsList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 100, 20);
            w.mackerelPatternsList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 100, 20);

            w.classicPatternsList.setX(xPos0);
            w.mackerelPatternsList.setX(xPos0);

            for (int i = 0; i < WCGenetics.Constants.MAX_TABBY_VARIANTS_CLASSIC; i++) {
                w.classicPatternsList.addButton(Component.translatable("screen.cat.classic_stripes", i+1), () -> {
                    screen.variants.tabbyVar = Integer.parseInt(w.classicPatternsList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
            }

            for (int i = 0; i < WCGenetics.Constants.MAX_TABBY_VARIANTS_MACKEREL; i++) {
                w.mackerelPatternsList.addButton(Component.translatable("screen.cat.mackerel_stripes", i+1), () -> {
                    screen.variants.tabbyVar = Integer.parseInt(w.mackerelPatternsList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
            }


            if (WCGenetics.Agouti.isTabby(screen.genetics.agouti) || addLists) {
                screen.addSubRenderable(w.patternsText, screen.otherListHeight);
                screen.addSubRenderable(w.agoutiOptionsList, screen.otherListHeight);

                if (WCGenetics.TabbyStripeTypes.isClassic(screen.genetics.tabbyStripes)) {
                    screen.addSubRenderable(w.classicPatternsList, yPos);
                    w.classicPatternsList.setSelected(w.classicPatternsList.getEntryByKey(String.valueOf(screen.variants.tabbyVar)));

                } else if (WCGenetics.TabbyStripeTypes.isMackerel(screen.genetics.tabbyStripes)) {
                    screen.addSubRenderable(w.mackerelPatternsList, yPos);
                    w.mackerelPatternsList.setSelected(w.mackerelPatternsList.getEntryByKey(String.valueOf(screen.variants.tabbyVar)));

                }
            }

        }

        public static void addSilverWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.silver"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 25);

            class SilverWidgets {
                FancySwitchButton silverSwitch;

                FancyStringWidget silverVariantsText;

                FancyButtonScrollList silverVariantsList;
            }

            SilverWidgets w = new SilverWidgets();

            yPos = screen.addSubRenderable(text, yPos);

            String key1 = "screen.button.silver";

            float scale = 0.83f;

            boolean hasGene = WCGenetics.Silver.isSilver(screen.genetics.silver, screen.genetics.agouti, screen.genetics.orangeBase, 1)
                    || WCGenetics.Silver.isSmoke(screen.genetics.silver, screen.genetics.agouti)
                    || WCGenetics.Silver.isSmokeTortie(screen.genetics.silver, screen.genetics.agouti, screen.genetics.orangeBase);

            w.silverSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key1, hasGene,
                    b -> {

                        screen.genetics.silver = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setSilver : CreateMorphConstants.setNonSilver;

                        if (w.silverSwitch.getValue()) {

                            screen.removeSubRenderable(w.silverVariantsText, screen.otherListHeight);
                            screen.removeSubRenderable(w.silverVariantsList, screen.otherListHeight);

                            screen.addSubRenderable(w.silverVariantsText, screen.otherListHeight);
                            screen.addSubRenderable(w.silverVariantsList, screen.otherListHeight);

                            screen.recalculateListHeight();
                        } else {
                            screen.removeSubRenderable(w.silverVariantsText, screen.otherListHeight);
                            screen.removeSubRenderable(w.silverVariantsList, screen.otherListHeight);
                        }

                        screen.recalculateListHeight();
                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.silverSwitch, yPos);


            w.silverVariantsText = new FancyStringWidget(Component.translatable("screen.cat.silver_variants"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 0.9f, false, 105);

            yPos += w.silverVariantsText.getWidgetHeight() + screen.getSubRenderableSpacing();


            w.silverVariantsList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 100, 20);

            w.silverVariantsList.setX(xPos0);

            for (int i = 0; i < WCGenetics.Constants.MAX_SILVER_VARIANTS; i++) {
                w.silverVariantsList.addButton(Component.translatable("screen.cat.silver_variant", i+1), () -> {
                    screen.variants.silverVar = Integer.parseInt(w.silverVariantsList.getSelectedKey());
                    if (screen.variants.silverVar == 2) {
                        screen.displayMessage(
                                Component.translatable("screen.message.silver_pattern_available"),
                                false, 100);
                    }
                }, Component.empty(), Component.empty(), String.valueOf(i));
            }

            w.silverVariantsList.setSelected(w.silverVariantsList.getEntryByKey(String.valueOf(screen.variants.silverVar)));


            if (hasGene) {
                screen.addSubRenderable(w.silverVariantsText, screen.otherListHeight);
                screen.addSubRenderable(w.silverVariantsList, screen.otherListHeight);
            }

        }

        public static void addEyesWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.eyes"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 125);

            class EyesWidgets {
                FancySwitchButton heterochromiaSwitch;

                FancySelectableSquareButton blueLeft;
                FancySelectableSquareButton greenLeft;
                FancySelectableSquareButton yellowLeft;
                FancySelectableSquareButton redLeft;
                FancySelectableSquareButton foggyLeft;

                FancySelectableSquareButton blueRight;
                FancySelectableSquareButton greenRight;
                FancySelectableSquareButton yellowRight;
                FancySelectableSquareButton redRight;
                FancySelectableSquareButton foggyRight;

                FancyStringWidget eyesVariantsText;

                FancyButtonScrollList eyesVariantsRight;
                FancyButtonScrollList eyesVariantsLeft;
            }
            List<FancySelectableSquareButton> colorButtonListLeft = new ArrayList<>();
            List<FancySelectableSquareButton> colorButtonListRight = new ArrayList<>();

            EyesWidgets w = new EyesWidgets();

            yPos = screen.addSubRenderable(text, yPos);

            String key1 = "screen.button.heterochromia";

            float scale = 0.83f;

            w.heterochromiaSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key1, WCGenetics.EyesAnomaly.isHeteroChromic(screen.genetics.eyesAnomaly),
                    b -> {

                        screen.genetics.eyesAnomaly = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setHeteroChromia : CreateMorphConstants.setNonHeteroChromia;

                        for  (FancySelectableSquareButton button : colorButtonListRight) {
                            if (w.heterochromiaSwitch.getValue()) {
                                screen.removeSubRenderable(button, screen.otherListHeight);
                                screen.addSubRenderable(button, screen.otherListHeight);
                            } else {
                                screen.removeSubRenderable(button, screen.otherListHeight);
                            }
                        }

                        screen.removeSubRenderable(w.eyesVariantsRight, screen.otherListHeight);
                        screen.removeSubRenderable(w.eyesVariantsLeft, screen.otherListHeight);

                        if (WCGenetics.EyesAnomaly.isHeteroChromic(screen.genetics.eyesAnomaly)) {
                            if (WCGenetics.EyeColor.getEyeColor(screen.variants.eyeColorLeft) != WCGenetics.EyeColor.BLIND) {
                                screen.addSubRenderable(w.eyesVariantsLeft, screen.otherListHeight);
                            }
                        }
                        if (WCGenetics.EyeColor.getEyeColor(screen.variants.eyeColorRight) != WCGenetics.EyeColor.BLIND){
                            screen.addSubRenderable(w.eyesVariantsRight, screen.otherListHeight);
                        }

                        if (!w.heterochromiaSwitch.getValue()) {
                            screen.variants.eyeColorLeft = screen.variants.eyeColorRight;
                            screen.variants.leftEyeVar = screen.variants.rightEyeVar;

                            for  (FancySelectableSquareButton button : colorButtonListRight) {
                                button.selectIfMatches(screen.variants.eyeColorLeft);
                            }
                        }

                        if (WCGenetics.EyeColor.getEyeColor(screen.variants.eyeColorLeft) == WCGenetics.EyeColor.BLIND
                                && WCGenetics.EyeColor.getEyeColor(screen.variants.eyeColorRight) == WCGenetics.EyeColor.BLIND) {
                            screen.removeSubRenderable(w.eyesVariantsText, screen.otherListHeight);
                        }

                        screen.recalculateListHeight();
                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.heterochromiaSwitch, yPos);

            int squareSize = 30;
            int squarePosLeft =xPos0 + padding;
            int squarePosRight = squarePosLeft + squareSize + padding;

            w.blueLeft = new FancySelectableSquareButton(squarePosLeft, yPos, squareSize, b -> {
                selectEyesButton(colorButtonListLeft, colorButtonListRight, w.blueLeft, w.blueRight, screen);
                screen.removeSubRenderable(w.eyesVariantsRight, screen.otherListHeight);
                screen.removeSubRenderable(w.eyesVariantsText, screen.otherListHeight);
                screen.addSubRenderable(w.eyesVariantsRight, screen.otherListHeight);
                screen.addSubRenderable(w.eyesVariantsText, screen.otherListHeight);
                screen.recalculateListHeight();
            }, 0xFF57DAFF, CreateMorphConstants.setEyeBlue);
            w.blueRight = new FancySelectableSquareButton(squarePosRight, yPos, squareSize, b -> {
                selectEyesButton(colorButtonListLeft, colorButtonListRight, w.blueRight, w.blueLeft, screen);
                screen.removeSubRenderable(w.eyesVariantsLeft, screen.otherListHeight);
                screen.removeSubRenderable(w.eyesVariantsText, screen.otherListHeight);
                screen.addSubRenderable(w.eyesVariantsLeft, screen.otherListHeight);
                screen.addSubRenderable(w.eyesVariantsText, screen.otherListHeight);
                screen.recalculateListHeight();
            }, 0xFF57DAFF, CreateMorphConstants.setEyeBlue);

            yPos = screen.addSubRenderable(w.blueLeft, yPos);
            screen.addSubRenderable(w.blueRight, yPos);

            w.greenLeft = new FancySelectableSquareButton(squarePosLeft, yPos, squareSize, b -> {
                selectEyesButton(colorButtonListLeft, colorButtonListRight, w.greenLeft, w.greenRight, screen);
                screen.removeSubRenderable(w.eyesVariantsRight, screen.otherListHeight);
                screen.removeSubRenderable(w.eyesVariantsText, screen.otherListHeight);
                screen.addSubRenderable(w.eyesVariantsRight, screen.otherListHeight);
                screen.addSubRenderable(w.eyesVariantsText, screen.otherListHeight);
                screen.recalculateListHeight();
            }, 0xFF00FF00, CreateMorphConstants.setEyeGreen);
            w.greenRight = new FancySelectableSquareButton(squarePosRight, yPos, squareSize, b -> {
                selectEyesButton(colorButtonListLeft, colorButtonListRight, w.greenRight, w.greenLeft, screen);
                screen.removeSubRenderable(w.eyesVariantsLeft, screen.otherListHeight);
                screen.removeSubRenderable(w.eyesVariantsText, screen.otherListHeight);
                screen.addSubRenderable(w.eyesVariantsLeft, screen.otherListHeight);
                screen.addSubRenderable(w.eyesVariantsText, screen.otherListHeight);
                screen.recalculateListHeight();
            }, 0xFF00FF00, CreateMorphConstants.setEyeGreen);

            yPos = screen.addSubRenderable(w.greenLeft, yPos);
            screen.addSubRenderable(w.greenRight, yPos);

            w.yellowLeft = new FancySelectableSquareButton(squarePosLeft, yPos, squareSize, b -> {
                selectEyesButton(colorButtonListLeft, colorButtonListRight, w.yellowLeft, w.yellowRight, screen);
                screen.removeSubRenderable(w.eyesVariantsRight, screen.otherListHeight);
                screen.removeSubRenderable(w.eyesVariantsText, screen.otherListHeight);
                screen.addSubRenderable(w.eyesVariantsRight, screen.otherListHeight);
                screen.addSubRenderable(w.eyesVariantsText, screen.otherListHeight);
                screen.recalculateListHeight();
            }, 0xFFF5C527, CreateMorphConstants.setEyeYellow);
            w.yellowRight = new FancySelectableSquareButton(squarePosRight, yPos, squareSize, b -> {
                selectEyesButton(colorButtonListLeft, colorButtonListRight, w.yellowRight, w.yellowLeft, screen);
                screen.removeSubRenderable(w.eyesVariantsLeft, screen.otherListHeight);
                screen.removeSubRenderable(w.eyesVariantsText, screen.otherListHeight);
                screen.addSubRenderable(w.eyesVariantsLeft, screen.otherListHeight);
                screen.addSubRenderable(w.eyesVariantsText, screen.otherListHeight);
                screen.recalculateListHeight();
            }, 0xFFF5C527, CreateMorphConstants.setEyeYellow);

            yPos = screen.addSubRenderable(w.yellowLeft, yPos);
            screen.addSubRenderable(w.yellowRight, yPos);

            w.redLeft = new FancySelectableSquareButton(squarePosLeft, yPos, squareSize, b -> {
                selectEyesButton(colorButtonListLeft, colorButtonListRight, w.redLeft, w.redRight, screen);
                screen.removeSubRenderable(w.eyesVariantsRight, screen.otherListHeight);
                screen.removeSubRenderable(w.eyesVariantsText, screen.otherListHeight);
                screen.addSubRenderable(w.eyesVariantsRight, screen.otherListHeight);
                screen.addSubRenderable(w.eyesVariantsText, screen.otherListHeight);
                screen.recalculateListHeight();
            }, 0xFFFF0000, CreateMorphConstants.setEyeRed);
            w.redRight = new FancySelectableSquareButton(squarePosRight, yPos, squareSize, b -> {
                selectEyesButton(colorButtonListLeft, colorButtonListRight, w.redRight, w.redLeft, screen);
                screen.removeSubRenderable(w.eyesVariantsLeft, screen.otherListHeight);
                screen.removeSubRenderable(w.eyesVariantsText, screen.otherListHeight);
                screen.addSubRenderable(w.eyesVariantsLeft, screen.otherListHeight);
                screen.addSubRenderable(w.eyesVariantsText, screen.otherListHeight);
                screen.recalculateListHeight();
            }, 0xFFFF0000, CreateMorphConstants.setEyeRed);

            yPos = screen.addSubRenderable(w.redLeft, yPos);
            screen.addSubRenderable(w.redRight, yPos);

            w.foggyLeft = new FancySelectableSquareButton(squarePosLeft, yPos, squareSize, b -> {
                selectEyesButton(colorButtonListLeft, colorButtonListRight, w.foggyLeft, w.foggyRight, screen);
                screen.removeSubRenderable(w.eyesVariantsRight, screen.otherListHeight);
                if (WCGenetics.EyeColor.getEyeColor(screen.variants.eyeColorLeft) == WCGenetics.EyeColor.BLIND
                        && WCGenetics.EyeColor.getEyeColor(screen.variants.eyeColorRight) == WCGenetics.EyeColor.BLIND){
                    screen.removeSubRenderable(w.eyesVariantsText, screen.otherListHeight);
                }
                screen.recalculateListHeight();
            }, 0xFFD2F5FA, CreateMorphConstants.setEyeBlind);
            w.foggyRight = new FancySelectableSquareButton(squarePosRight, yPos, squareSize, b -> {
                selectEyesButton(colorButtonListLeft, colorButtonListRight, w.foggyRight, w.foggyLeft, screen);
                screen.removeSubRenderable(w.eyesVariantsLeft, screen.otherListHeight);
                if (WCGenetics.EyeColor.getEyeColor(screen.variants.eyeColorLeft) == WCGenetics.EyeColor.BLIND
                        && WCGenetics.EyeColor.getEyeColor(screen.variants.eyeColorRight) == WCGenetics.EyeColor.BLIND) {
                    screen.removeSubRenderable(w.eyesVariantsText, screen.otherListHeight);
                }
                screen.recalculateListHeight();
            }, 0xFFD2F5FA, CreateMorphConstants.setEyeBlind);

            colorButtonListLeft.addAll(List.of(
                    w.blueLeft,
                    w.greenLeft,
                    w.yellowLeft,
                    w.redLeft,
                    w.foggyLeft
            ));

            colorButtonListRight.addAll(List.of(
                    w.blueRight,
                    w.greenRight,
                    w.yellowRight,
                    w.redRight,
                    w.foggyRight
            ));

            for (FancySelectableSquareButton button : colorButtonListLeft) {
                button.selectIfMatches(screen.variants.eyeColorRight);
            }
            for  (FancySelectableSquareButton button : colorButtonListRight) {
                button.selectIfMatches(screen.variants.eyeColorLeft);
            }


            yPos = screen.addSubRenderable(w.foggyLeft, yPos);
            screen.addSubRenderable(w.foggyRight, yPos);

            if (!WCGenetics.EyesAnomaly.isHeteroChromic(screen.genetics.eyesAnomaly)){
                for  (FancySelectableSquareButton button : colorButtonListRight) {
                    screen.removeSubRenderable(button, screen.otherListHeight);
                }
                screen.recalculateListHeight();
            }

            w.eyesVariantsText = new FancyStringWidget(Component.translatable("screen.cat.eyes_variants"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 0.9f, false, 105);

            yPos = screen.addSubRenderable(w.eyesVariantsText, yPos);


            w.eyesVariantsRight = new FancyButtonScrollList(screen.getMinecraft(), width/2 - 5,
                    height - 100, yPos,
                    yPos + 100, 20);

            w.eyesVariantsLeft = new FancyButtonScrollList(screen.getMinecraft(), width/2 - 5,
                    height - 100, yPos,
                    yPos + 100, 20);

            w.eyesVariantsRight.setX(xPos0);
            w.eyesVariantsLeft.setX(xPos1 - w.eyesVariantsLeft.getWidth() - padding);

            for (int i = 0; i < WCGenetics.Constants.MAX_EYE_VARIANTS; i++) {
                w.eyesVariantsRight.addButton(Component.translatable("screen.cat.eyes_variant", i+1), () -> {
                    screen.variants.rightEyeVar = Integer.parseInt(w.eyesVariantsRight.getSelectedKey());
                if (!WCGenetics.EyesAnomaly.isHeteroChromic(screen.genetics.eyesAnomaly)) {
                        screen.variants.leftEyeVar = Integer.parseInt(w.eyesVariantsRight.getSelectedKey());
                    }
                }, Component.empty(), Component.empty(), String.valueOf(i), 0.60f);

                w.eyesVariantsLeft.addButton(Component.translatable("screen.cat.eyes_variant", i+1), () -> {
                    screen.variants.leftEyeVar = Integer.parseInt(w.eyesVariantsLeft.getSelectedKey());

                }, Component.empty(), Component.empty(), String.valueOf(i), 0.60f);
            }

            w.eyesVariantsRight.setSelected(w.eyesVariantsRight.getEntryByKey(String.valueOf(screen.variants.rightEyeVar)));
            w.eyesVariantsLeft.setSelected(w.eyesVariantsLeft.getEntryByKey(String.valueOf(screen.variants.leftEyeVar)));


            if (WCGenetics.EyesAnomaly.isHeteroChromic(screen.genetics.eyesAnomaly)) {
                screen.addSubRenderable(w.eyesVariantsLeft, screen.otherListHeight);
            }
            screen.addSubRenderable(w.eyesVariantsRight, screen.otherListHeight);

        }

        public static void addChimerismWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.chimerism"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 25);

            class ChimeraWidgets {
                FancySwitchButton chimeraSwitch;

                FancyStringWidget editChimeraText;
                FancySimpleButton editChimeraButton;
            }

            ChimeraWidgets w = new ChimeraWidgets();

            yPos = screen.addSubRenderable(text, yPos);

            String key1 = "screen.button.chimera";

            float scale = 0.83f;

            w.chimeraSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key1, WCGenetics.Chimerism.isChimera(screen.chimeraGenetics.chimeraGene),
                    b -> {

                        screen.chimeraGenetics.chimeraGene = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setChimera : CreateMorphConstants.setNotChimera;

                        if (w.chimeraSwitch.getValue()) {
                            screen.removeSubRenderable(w.editChimeraButton, screen.otherListHeight);
                            screen.removeSubRenderable(w.editChimeraText, screen.otherListHeight);
                            screen.addSubRenderable(w.editChimeraButton, screen.otherListHeight);
                            screen.addSubRenderable(w.editChimeraText, screen.otherListHeight);
                        } else {
                            screen.removeSubRenderable(w.editChimeraButton, screen.otherListHeight);
                            screen.removeSubRenderable(w.editChimeraText, screen.otherListHeight);
                        }

                        screen.recalculateListHeight();
                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.chimeraSwitch, yPos);

            w.editChimeraText = new FancyStringWidget(Component.translatable("screen.cat.edit_chimera"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 0.9f, false, 25);

            yPos += w.editChimeraText.getWidgetHeight() + screen.getSubRenderableSpacing();

            w.editChimeraButton = new FancySimpleButton(width, 20,
                    xPos0, yPos,
                    Component.translatable("screen.button.open_chimera_menu"),
                    b -> {
                        if (screen instanceof CreateMorphScreen createMorphScreen){
                            createMorphScreen.closing = true;
                            createMorphScreen.animationTime = 0f;
                            createMorphScreen.openChimeraMenu = true;
                        }
                    }, scale);

            if (WCGenetics.Chimerism.isChimera(screen.chimeraGenetics.chimeraGene)) {
                screen.addSubRenderable(w.editChimeraText, yPos);
                screen.addSubRenderable(w.editChimeraButton, yPos);
            }

        }

        public static void addDetailsWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.details"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 155);

            class DetailsWidgets {
                FancySwitchButton bobtailSwitch;
                FancySwitchButton chestFurSwitch;
                FancySwitchButton bellyFurSwitch;
                FancySwitchButton legsFurSwitch;
                FancySwitchButton headFurSwitch;
                FancySwitchButton cheekFurSwitch;
                FancySwitchButton backFurSwitch;
                FancySwitchButton tailFurSwitch;

                FancyIntSlider skinColor;

                FancyStringWidget scarsText;
                FancyButtonScrollList scarsList;
            }

            DetailsWidgets w = new DetailsWidgets();

            yPos = screen.addSubRenderable(text, yPos);

            String key1 = "screen.button.bobtail";
            String key2 = "screen.button.chestfur";
            String key3 = "screen.button.bellyfur";
            String key4 = "screen.button.legsfur";
            String key5 = "screen.button.headfur";
            String key6 = "screen.button.cheekfur";
            String key7 = "screen.button.backfur";
            String key8 = "screen.button.tailfur";


            float scale = 0.83f;

            w.bobtailSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key1, WCGenetics.Bobtail.isBobtail(screen.genetics.bobtail),
                    b -> {
                        screen.genetics.bobtail = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setBobtail : CreateMorphConstants.setFulltail;

                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.bobtailSwitch, yPos);

            w.chestFurSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key2, WCGenetics.FurGene.isLongFur(screen.genetics.chestFur),
                    b -> {
                        screen.genetics.chestFur = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setLongFur : CreateMorphConstants.setShortFur;

                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.chestFurSwitch, yPos);

            w.bellyFurSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key3, WCGenetics.FurGene.isLongFur(screen.genetics.bellyFur),
                    b -> {
                        screen.genetics.bellyFur = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setLongFur : CreateMorphConstants.setShortFur;

                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.bellyFurSwitch, yPos);

            w.legsFurSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key4, WCGenetics.FurGene.isLongFur(screen.genetics.legsFur),
                    b -> {
                        screen.genetics.legsFur = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setLongFur : CreateMorphConstants.setShortFur;

                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.legsFurSwitch, yPos);

            w.headFurSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key5, WCGenetics.FurGene.isLongFur(screen.genetics.headFur),
                    b -> {
                        screen.genetics.headFur = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setLongFur : CreateMorphConstants.setShortFur;

                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.headFurSwitch, yPos);

            w.cheekFurSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key6, WCGenetics.FurGene.isLongFur(screen.genetics.cheekFur),
                    b -> {
                        screen.genetics.cheekFur = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setLongFur : CreateMorphConstants.setShortFur;

                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.cheekFurSwitch, yPos);

            w.backFurSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key7, WCGenetics.FurGene.isLongFur(screen.genetics.backFur),
                    b -> {
                        screen.genetics.backFur = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setLongFur : CreateMorphConstants.setShortFur;

                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.backFurSwitch, yPos);

            w.tailFurSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key8, WCGenetics.FurGene.isLongFur(screen.genetics.tailFur),
                    b -> {
                        screen.genetics.tailFur = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setLongFur : CreateMorphConstants.setShortFur;

                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.tailFurSwitch, yPos);

            w.skinColor = new FancyIntSlider(xPos0, yPos,
                    width - padding*2, 15,
                    0, WCGenetics.Constants.MAX_SKIN_VARIANTS,
                    screen.variants.skin_color, (button) -> {
                screen.variants.skin_color = button.getActualValue();
            }, Component.translatable("screen.cat.skin"));

            yPos = screen.addSubRenderable(w.skinColor, yPos);

            w.scarsText = new FancyStringWidget(Component.translatable("screen.cat.scars"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 0.9f, false, 105);

            yPos = screen.addSubRenderable(w.scarsText, yPos);

            w.scarsList = new FancyButtonScrollList(screen.getMinecraft(), width,
                    height - 100, yPos,
                    yPos + 100, 20);

            w.scarsList.setX(xPos0);


            for (int i = 0; i < WCGenetics.Constants.MAX_SCAR_VARIANTS; i++) {
                Component entryText;
                if (i == 0) {
                    entryText = Component.translatable("screen.cat.scars_no_scars");
                } else if (i == 1) {
                    entryText = Component.translatable("screen.cat.scars_face_right");
                } else if (i == 2) {
                    entryText = Component.translatable("screen.cat.scars_face_left");
                } else if (i == 3) {
                    entryText = Component.translatable("screen.cat.scars_head_right");
                } else if (i == 4) {
                    entryText = Component.translatable("screen.cat.scars_head_left");
                } else if (i == 5) {
                    entryText = Component.translatable("screen.cat.scars_side_right");
                } else if (i == 6) {
                    entryText = Component.translatable("screen.cat.scars_side_left");
                } else if (i == 7) {
                    entryText = Component.translatable("screen.cat.scars_chest_right");
                } else if (i == 8) {
                    entryText = Component.translatable("screen.cat.scars_chest_left");
                } else if (i == 9) {
                    entryText = Component.translatable("screen.cat.scars_thigh_right");
                } else if (i == 10) {
                    entryText = Component.translatable("screen.cat.scars_thigh_left");
                } else {
                    entryText = Component.translatable("screen.cat.scars_var",(i + 1 - 11));
                }

                w.scarsList.addButton(entryText, () -> {
                    screen.variants.scars = Integer.parseInt(w.scarsList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
            }

            w.scarsList.setSelected(w.scarsList.getEntryByKey(String.valueOf(screen.variants.scars)));

            yPos = screen.addSubRenderable(w.scarsList, yPos);

        }

        public static void addExtrasWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.extras"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 59);

            class ExtrasWidgets {
                FancyIntSlider rufousingSlider;
                FancyIntSlider blueTintSlider;

                FancyFloatSlider sizeSlider;

                FancyStringWidget noiseText;
                FancyButtonScrollList noiseList;

            }

            ExtrasWidgets w = new ExtrasWidgets();

            yPos = screen.addSubRenderable(text, yPos);

            float scale = 0.83f;

            w.rufousingSlider = new FancyIntSlider(xPos0, yPos,
                    width - padding*2, 15,
                    0, WCGenetics.Constants.MAX_RUFOUSING_VARIANTS,
                    screen.variants.rufousingVariant, (button) -> {
                screen.variants.rufousingVariant = button.getActualValue();
            }, Component.translatable("screen.cat.rufousing"));

            yPos = screen.addSubRenderable(w.rufousingSlider, yPos);

            w.blueTintSlider = new FancyIntSlider(xPos0, yPos,
                    width - padding*2, 15,
                    0, WCGenetics.Constants.MAX_BLUE_RUFOUSING_VARIANTS,
                    screen.variants.blueRufousingVariant, (button) -> {
                screen.variants.blueRufousingVariant = button.getActualValue();
            }, Component.translatable("screen.cat.blue_tint"));

            yPos = screen.addSubRenderable(w.blueTintSlider, yPos);

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

            w.sizeSlider = new FancyFloatSlider(xPos0, yPos,
                    width - padding*2, 15,
                    0.6f, maxSize,
                    screen.variants.size, (button) -> {
                screen.variants.size = button.getActualValue();
            }, Component.translatable("screen.cat.size"));

            yPos = screen.addSubRenderable(w.sizeSlider, yPos);

            w.noiseText = new FancyStringWidget(Component.translatable("screen.cat.noise"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 0.9f, false, 105);

            yPos = screen.addSubRenderable(w.noiseText, yPos);

            w.noiseList = new FancyButtonScrollList(screen.getMinecraft(), width,
                    height - 100, yPos,
                    yPos + 100, 20);

            w.noiseList.setX(xPos0);

            for (int i = 0; i < WCGenetics.Constants.MAX_NOISE_VARIANTS; i++) {
                if (i == 5) {
                    w.noiseList.addButton(Component.translatable("screen.cat.no_noise_var"), () -> {
                        screen.variants.noise = Integer.parseInt(w.noiseList.getSelectedKey());
                    }, Component.empty(), Component.empty(), String.valueOf(i));
                    continue;
                }
                w.noiseList.addButton(Component.translatable("screen.cat.noise_var", i + 1), () -> {
                    screen.variants.noise = Integer.parseInt(w.noiseList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
            }

            w.noiseList.setSelected(w.noiseList.getEntryByKey(String.valueOf(screen.variants.noise)));

            yPos = screen.addSubRenderable(w.noiseList, yPos);


        }


        private static void selectEyesButton(List<FancySelectableSquareButton> listRight, List<FancySelectableSquareButton> listLeft,
                                             FancySelectableSquareButton button, FancySelectableSquareButton match, BaseMorphScreen screen) {
            if (!WCGenetics.EyesAnomaly.isHeteroChromic(screen.genetics.eyesAnomaly)) {
                for (FancySelectableSquareButton b : listLeft) {
                    b.setSelected(false);
                }
                for (FancySelectableSquareButton b : listRight) {
                    b.setSelected(false);
                }
            } else {
                if (listLeft.contains(button)) {
                    for (FancySelectableSquareButton b : listLeft) {
                        b.setSelected(false);
                    }
                } else if (listRight.contains(button)) {
                    for (FancySelectableSquareButton b : listRight) {
                        b.setSelected(false);
                    }
                }

            }

            if (!WCGenetics.EyesAnomaly.isHeteroChromic(screen.genetics.eyesAnomaly)) {
                button.setSelected(true);
                screen.variants.eyeColorLeft = button.getKey();
                match.setSelected(true);
                screen.variants.eyeColorRight = match.getKey();
            } else {
                if (listLeft.contains(button)) {
                    screen.variants.eyeColorLeft = button.getKey();
                } else if (listRight.contains(button)) {
                    screen.variants.eyeColorRight = match.getKey();
                }
                button.setSelected(true);
            }

        }
    }

    public static class Chimerism {

        public static void addBaseWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.base_color"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 65);

            yPos = screen.addSubRenderable(text, yPos);

            FancyButtonScrollList otherlist = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 65, 20);

            otherlist.setX(xPos0);

            otherlist.addButton(Component.translatable("screen.cat.base_black"), () -> {
                screen.chimeraGenetics.base = otherlist.getSelectedKey();
            }, Component.empty(), Component.empty(), CreateMorphConstants.setBlack);

            otherlist.addButton(Component.translatable("screen.cat.base_chocolate"), () -> {
                screen.chimeraGenetics.base = otherlist.getSelectedKey();
            }, Component.empty(), Component.empty(), CreateMorphConstants.setChocolate);

            otherlist.addButton(Component.translatable("screen.cat.base_cinnamon"), () -> {
                screen.chimeraGenetics.base = otherlist.getSelectedKey();
            }, Component.empty(), Component.empty(), CreateMorphConstants.setCinnamon);

            selectEntry(screen.chimeraGenetics.base, otherlist);

            yPos = screen.addSubRenderable(otherlist, yPos);

            FancyStringWidget chimeraVarText = new FancyStringWidget(Component.translatable("screen.cat.chimera_variants"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 0.9f, false, 105);

            yPos = screen.addSubRenderable(chimeraVarText, yPos);

            FancyButtonScrollList chimeraVarList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 100, 20);

            chimeraVarList.setX(xPos0);

            for (int i = 0; i < WCGenetics.Constants.MAX_CHIMERISM_VARIANTS; i++) {
                chimeraVarList.addButton(Component.translatable("screen.cat.chimera_variant", i+1), () -> {
                    screen.chimeraVariants.chimeraVariant = Integer.parseInt(chimeraVarList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
            }

            chimeraVarList.setSelected(chimeraVarList.getEntryByKey(String.valueOf(screen.chimeraVariants.chimeraVariant)));

            yPos = screen.addSubRenderable(chimeraVarList, yPos);

        }

        public static void addOrangeWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.orange_base"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 50);

            class OrangeWidgets {
                FancySwitchButton orangeSwitch;
                FancySwitchButton tortieSwitch;
                FancyButtonScrollList orangeList;
                FancyStringWidget tortieVariantsText;
            }

            OrangeWidgets w = new OrangeWidgets();

            yPos = screen.addSubRenderable(text, yPos);

            String key1 = "screen.button.orange";
            String key2 = "screen.button.tortie";
            float scale = 0.83f;


            w.orangeSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key1, WCGenetics.OrangeBase.isOrange(screen.chimeraGenetics.orangeBase, 1),
                    b -> {
                        screen.chimeraGenetics.orangeBase = ((FancySwitchButton) b).getValue() ? CreateMorphConstants.setOrange : CreateMorphConstants.setNotOrange;
                        if (w.orangeSwitch.getValue()) {
                            w.tortieSwitch.setValue(false);
                            screen.removeSubRenderable(w.orangeList, screen.otherListHeight);
                            screen.removeSubRenderable(w.tortieVariantsText, screen.otherListHeight);
                            w.orangeList.setSelected(w.orangeList.getEntryByKey(String.valueOf(screen.chimeraVariants.orangeVar)));
                            screen.recalculateListHeight();
                        }
                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.orangeSwitch, yPos);

            w.tortieSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key2, WCGenetics.OrangeBase.isTortoiseshell(screen.chimeraGenetics.orangeBase), b -> {
                screen.chimeraGenetics.orangeBase = ((FancySwitchButton) b).getValue() ? CreateMorphConstants.setTortie : CreateMorphConstants.setNotOrange;
                if (w.tortieSwitch.getValue()) {
                    w.orangeSwitch.setValue(false);
                    screen.addSubRenderable(w.orangeList, screen.otherListHeight);
                    screen.addSubRenderable(w.tortieVariantsText, screen.otherListHeight);
                    w.orangeList.setSelected(w.orangeList.getEntryByKey(String.valueOf(screen.chimeraVariants.orangeVar)));
                } else {
                    screen.removeSubRenderable(w.orangeList, screen.otherListHeight);
                    screen.removeSubRenderable(w.tortieVariantsText, screen.otherListHeight);
                }
                screen.recalculateListHeight();
            }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.tortieSwitch, yPos);

            w.tortieVariantsText = new FancyStringWidget(Component.translatable("screen.cat.tortie_variants"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 0.9f, false, 105);


            yPos += w.tortieVariantsText.getWidgetHeight() + screen.getSubRenderableSpacing();

            w.orangeList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 100, 20);

            w.orangeList.setX(xPos0);

            for (int i = 0; i < WCGenetics.Constants.MAX_TORTIE_VARIANTS; i++) {
                w.orangeList.addButton(Component.translatable("screen.cat.orange_base_tortie", i+1), () -> {
                    screen.chimeraVariants.orangeVar = Integer.parseInt(w.orangeList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
            }

            if (WCGenetics.OrangeBase.isTortoiseshell(screen.chimeraGenetics.orangeBase)) {
                screen.addSubRenderable(w.tortieVariantsText, screen.otherListHeight);
                yPos = screen.addSubRenderable(w.orangeList, yPos);
                w.orangeList.setSelected(w.orangeList.getEntryByKey(String.valueOf(screen.chimeraVariants.orangeVar)));
            }

        }

        public static void addWhiteWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.white_ratio"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 75);

            class WhiteWidgets {
                FancyButtonScrollList lowWhiteOptionsList;
                FancyButtonScrollList highWhiteOptionsList;
                FancyStringWidget lowWhiteVariantsText;
                FancyStringWidget highWhiteVariantsText;
            }

            WhiteWidgets w = new WhiteWidgets();

            yPos = screen.addSubRenderable(text, yPos);

            float scale = 0.83f;


            FancyListSlider listSlider = new FancyListSlider(xPos0, yPos,
                    width - padding*2, 15,
                    FancyListSlider.EntryBuilder.builder()
                            .add(Component.translatable("screen.button.no_white"), CreateMorphConstants.setNotWhite, key -> !WCGenetics.WhiteRatio.isWhite(key))
                            .add(Component.translatable("screen.button.low_white"), CreateMorphConstants.setLowWhite, WCGenetics.WhiteRatio::isLowSpotted)
                            .add(Component.translatable("screen.button.high_white"), CreateMorphConstants.setHighWhite, WCGenetics.WhiteRatio::isHighSpotted)
                            .add(Component.translatable("screen.button.full_white"), CreateMorphConstants.setFullWhite, WCGenetics.WhiteRatio::isWhite)
                            .build(), but -> {
                screen.chimeraGenetics.whiteRatio = but.getActualValueKey();

                screen.removeSubRenderable(w.lowWhiteVariantsText, screen.otherListHeight);
                screen.removeSubRenderable(w.lowWhiteOptionsList, screen.otherListHeight);
                screen.removeSubRenderable(w.highWhiteVariantsText, screen.otherListHeight);
                screen.removeSubRenderable(w.highWhiteOptionsList, screen.otherListHeight);

                if (WCGenetics.WhiteRatio.isLowSpotted(screen.chimeraGenetics.whiteRatio)) {
                    screen.addSubRenderable(w.lowWhiteVariantsText, screen.otherListHeight);
                    screen.addSubRenderable(w.lowWhiteOptionsList, screen.otherListHeight);
                } else if (WCGenetics.WhiteRatio.isHighSpotted(screen.chimeraGenetics.whiteRatio)) {
                    screen.addSubRenderable(w.highWhiteVariantsText, screen.otherListHeight);
                    screen.addSubRenderable(w.highWhiteOptionsList, screen.otherListHeight);
                }

                screen.recalculateListHeight();
            });

            listSlider.setInitialValue(screen.chimeraGenetics.whiteRatio);

            yPos = screen.addSubRenderable(listSlider, yPos);

            w.lowWhiteVariantsText = new FancyStringWidget(Component.translatable("screen.cat.low_white_variants"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 0.9f, false, 105);

            w.highWhiteVariantsText = new FancyStringWidget(Component.translatable("screen.cat.high_white_variants"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 0.9f, false, 105);


            yPos += w.highWhiteVariantsText.getWidgetHeight() + screen.getSubRenderableSpacing();


            w.lowWhiteOptionsList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 100, 20);
            w.highWhiteOptionsList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 100, 20);

            w.lowWhiteOptionsList.setX(xPos0);
            w.highWhiteOptionsList.setX(xPos0);

            for (int i = 0; i < WCGenetics.Constants.MAX_WHITE_VARIANTS_LOW; i++) {
                w.lowWhiteOptionsList.addButton(Component.translatable("screen.cat.white_ratio_low", i+1), () -> {
                    screen.chimeraVariants.whiteVar = Integer.parseInt(w.lowWhiteOptionsList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
            }

            for (int i = 0; i < WCGenetics.Constants.MAX_WHITE_VARIANTS_HIGH; i++) {
                w.highWhiteOptionsList.addButton(Component.translatable("screen.cat.white_ratio_high", i+1), () -> {
                    screen.chimeraVariants.whiteVar = Integer.parseInt(w.highWhiteOptionsList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
            }


            if (WCGenetics.WhiteRatio.isLowSpotted(screen.chimeraGenetics.whiteRatio)) {
                screen.addSubRenderable(w.lowWhiteVariantsText, screen.otherListHeight);
                yPos = screen.addSubRenderable(w.lowWhiteOptionsList, yPos);
                w.lowWhiteOptionsList.setSelected(w.lowWhiteOptionsList.getEntryByKey(String.valueOf(screen.chimeraVariants.whiteVar)));

            } else if (WCGenetics.WhiteRatio.isHighSpotted(screen.chimeraGenetics.whiteRatio)) {
                screen.addSubRenderable(w.highWhiteVariantsText, screen.otherListHeight);
                yPos = screen.addSubRenderable(w.highWhiteOptionsList, yPos);
                w.highWhiteOptionsList.setSelected(w.highWhiteOptionsList.getEntryByKey(String.valueOf(screen.chimeraVariants.whiteVar)));

            }

        }

        public static void addAlbinoWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.albinism_section"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 35);

            class AlbinoWidgets {

                FancyButtonScrollList sepiaOptionsList;
                FancyButtonScrollList minkOptionsList;
                FancyButtonScrollList siameseOptionsList;

                FancyStringWidget albinoVariantsText;
            }

            AlbinoWidgets w = new AlbinoWidgets();

            yPos = screen.addSubRenderable(text, yPos);

            float scale = 0.83f;

            FancyListSlider listSlider = new FancyListSlider(xPos0, yPos,
                    width - padding*2, 15,
                    FancyListSlider.EntryBuilder.builder()
                            .add(Component.translatable("screen.button.not_colorpoint"), CreateMorphConstants.setNotAlbino, WCGenetics.Albino::isNotAlbino)
                            .add(Component.translatable("screen.button.sepia"), CreateMorphConstants.setSepia, WCGenetics.Albino::isSepia)
                            .add(Component.translatable("screen.button.mink"), CreateMorphConstants.setMink, WCGenetics.Albino::isMink)
                            .add(Component.translatable("screen.button.siamese"), CreateMorphConstants.setSiamese, WCGenetics.Albino::isSiamese)
                            .add(Component.translatable("screen.button.true_albino"), CreateMorphConstants.setTrueAlbino, WCGenetics.Albino::isTrueAlbino)
                            .build(), but -> {
                screen.chimeraGenetics.albino = but.getActualValueKey();
                screen.removeSubRenderable(w.albinoVariantsText, screen.otherListHeight);
                screen.removeSubRenderable(w.sepiaOptionsList, screen.otherListHeight);
                screen.removeSubRenderable(w.minkOptionsList, screen.otherListHeight);
                screen.removeSubRenderable(w.siameseOptionsList, screen.otherListHeight);

                if (WCGenetics.Albino.isSepia(screen.chimeraGenetics.albino)) {
                    screen.addSubRenderable(w.albinoVariantsText, screen.otherListHeight);
                    screen.addSubRenderable(w.sepiaOptionsList, screen.otherListHeight);
                } else if (WCGenetics.Albino.isMink(screen.chimeraGenetics.albino)) {
                    screen.addSubRenderable(w.albinoVariantsText, screen.otherListHeight);
                    screen.addSubRenderable(w.minkOptionsList, screen.otherListHeight);
                } else if (WCGenetics.Albino.isSiamese(screen.chimeraGenetics.albino)) {
                    screen.addSubRenderable(w.albinoVariantsText, screen.otherListHeight);
                    screen.addSubRenderable(w.siameseOptionsList, screen.otherListHeight);
                }
                screen.recalculateListHeight();
            });

            listSlider.setInitialValue(screen.chimeraGenetics.albino);

            yPos = screen.addSubRenderable(listSlider, yPos);

            w.albinoVariantsText = new FancyStringWidget(Component.translatable("screen.cat.albino_variants"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 0.9f, false, 105);

            yPos += w.albinoVariantsText.getWidgetHeight() + screen.getSubRenderableSpacing();


            w.minkOptionsList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 100, 20);
            w.sepiaOptionsList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 100, 20);
            w.siameseOptionsList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 100, 20);

            w.minkOptionsList.setX(xPos0);
            w.sepiaOptionsList.setX(xPos0);
            w.siameseOptionsList.setX(xPos0);

            for (int i = 0; i < WCGenetics.Constants.MAX_ALBINO_VARIANTS; i++) {
                w.minkOptionsList.addButton(Component.translatable("screen.cat.mink_var", i+1), () -> {
                    screen.chimeraVariants.albinoVar = Integer.parseInt(w.minkOptionsList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
                w.sepiaOptionsList.addButton(Component.translatable("screen.cat.sepia_var", i+1), () -> {
                    screen.chimeraVariants.albinoVar = Integer.parseInt(w.sepiaOptionsList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
                w.siameseOptionsList.addButton(Component.translatable("screen.cat.siamese_var", i+1), () -> {
                    screen.chimeraVariants.albinoVar = Integer.parseInt(w.siameseOptionsList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
            }


            if (WCGenetics.Albino.isMink(screen.chimeraGenetics.albino)) {
                yPos = screen.addSubRenderable(w.minkOptionsList, yPos);
                screen.addSubRenderable(w.albinoVariantsText, yPos);
                w.minkOptionsList.setSelected(w.minkOptionsList.getEntryByKey(String.valueOf(screen.chimeraVariants.albinoVar)));
            } else if (WCGenetics.Albino.isSiamese(screen.chimeraGenetics.albino)) {
                yPos = screen.addSubRenderable(w.siameseOptionsList, yPos);
                screen.addSubRenderable(w.albinoVariantsText, yPos);
                w.siameseOptionsList.setSelected(w.siameseOptionsList.getEntryByKey(String.valueOf(screen.chimeraVariants.albinoVar)));
            } else if (WCGenetics.Albino.isSepia(screen.chimeraGenetics.albino)) {
                yPos = screen.addSubRenderable(w.sepiaOptionsList, yPos);
                screen.addSubRenderable(w.albinoVariantsText, yPos);
                w.sepiaOptionsList.setSelected(w.sepiaOptionsList.getEntryByKey(String.valueOf(screen.chimeraVariants.albinoVar)));
            }

        }

        public static void addDiluteWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.dilute_section"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 25);

            FancySwitchButton diluteSwitch;

            yPos = screen.addSubRenderable(text, yPos);

            String key1 = "screen.button.dilute";

            float scale = 0.83f;

            diluteSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key1, WCGenetics.Dilute.isDilute(screen.chimeraGenetics.dilute),
                    b -> {
                        screen.chimeraGenetics.dilute = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setDilute : CreateMorphConstants.setNonDilute;
                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(diluteSwitch, yPos);

        }

        public static void addAgoutiWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.agouti"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 25);

            class AgoutiWidgets {
                FancySwitchButton agoutiSwitch;

                FancyButtonScrollList agoutiOptionsList;

                FancyStringWidget patternsText;

                FancyButtonScrollList classicPatternsList;
                FancyButtonScrollList mackerelPatternsList;
            }

            AgoutiWidgets w = new AgoutiWidgets();

            yPos = screen.addSubRenderable(text, yPos);

            String key1 = "screen.button.agouti";

            float scale = 0.83f;

            boolean hasSilverGene = WCGenetics.Silver.isSilver(screen.chimeraGenetics.silver, screen.chimeraGenetics.agouti, screen.chimeraGenetics.orangeBase, 1)
                    || WCGenetics.Silver.isSmoke(screen.chimeraGenetics.silver, screen.chimeraGenetics.agouti)
                    || WCGenetics.Silver.isSmokeTortie(screen.chimeraGenetics.silver, screen.chimeraGenetics.agouti, screen.chimeraGenetics.orangeBase);

            boolean addLists = (hasSilverGene && screen.chimeraVariants.silverVar == 2)
                    || (WCGenetics.OrangeBase.isOrange(screen.chimeraGenetics.orangeBase, 1)
                    || WCGenetics.OrangeBase.isTortoiseshell(screen.chimeraGenetics.orangeBase));

            w.agoutiSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key1, WCGenetics.Agouti.isTabby(screen.chimeraGenetics.agouti),
                    b -> {

                        screen.chimeraGenetics.agouti = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setAgoutiTabby : CreateMorphConstants.setNonAgoutiTabby;

                        if (w.agoutiSwitch.getValue()) {

                            screen.removeSubRenderable(w.patternsText, screen.otherListHeight);
                            screen.removeSubRenderable(w.agoutiOptionsList, screen.otherListHeight);
                            screen.removeSubRenderable(w.classicPatternsList, screen.otherListHeight);
                            screen.removeSubRenderable(w.mackerelPatternsList, screen.otherListHeight);

                            screen.addSubRenderable(w.patternsText, screen.otherListHeight);
                            screen.addSubRenderable(w.agoutiOptionsList, screen.otherListHeight);

                            if (WCGenetics.TabbyStripeTypes.isClassic(screen.chimeraGenetics.tabbyStripes)) {
                                screen.addSubRenderable(w.classicPatternsList, screen.otherListHeight);
                            } else if (WCGenetics.TabbyStripeTypes.isMackerel(screen.chimeraGenetics.tabbyStripes)) {
                                screen.addSubRenderable(w.mackerelPatternsList, screen.otherListHeight);
                            }

                            screen.recalculateListHeight();
                        } else {
                            if (!addLists) {
                                screen.removeSubRenderable(w.patternsText, screen.otherListHeight);
                                screen.removeSubRenderable(w.agoutiOptionsList, screen.otherListHeight);
                                screen.removeSubRenderable(w.classicPatternsList, screen.otherListHeight);
                                screen.removeSubRenderable(w.mackerelPatternsList, screen.otherListHeight);
                            }
                        }

                        screen.recalculateListHeight();
                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.agoutiSwitch, yPos);




            w.patternsText = new FancyStringWidget(Component.translatable("screen.cat.agouti_variants"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 0.9f, false, 155);

            yPos += w.patternsText.getWidgetHeight() + screen.getSubRenderableSpacing();



            w.agoutiOptionsList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 45, 20);

            w.agoutiOptionsList.setX(xPos0);

            w.agoutiOptionsList.addButton(Component.translatable("screen.cat.classic"), () -> {
                screen.chimeraGenetics.tabbyStripes = w.agoutiOptionsList.getSelectedKey();
                screen.removeSubRenderable(w.classicPatternsList, screen.otherListHeight);
                screen.removeSubRenderable(w.mackerelPatternsList, screen.otherListHeight);

                screen.addSubRenderable(w.classicPatternsList, screen.otherListHeight);
                w.classicPatternsList.setSelected(w.classicPatternsList.getEntryByKey(String.valueOf(screen.chimeraVariants.tabbyVar)));
                screen.recalculateListHeight();
            }, Component.empty(), Component.empty(), CreateMorphConstants.setTabbyStripesClassic);

            w.agoutiOptionsList.addButton(Component.translatable("screen.cat.mackerel"), () -> {
                screen.chimeraGenetics.tabbyStripes = w.agoutiOptionsList.getSelectedKey();
                screen.removeSubRenderable(w.classicPatternsList, screen.otherListHeight);
                screen.removeSubRenderable(w.mackerelPatternsList, screen.otherListHeight);

                screen.addSubRenderable(w.mackerelPatternsList, screen.otherListHeight);
                w.mackerelPatternsList.setSelected(w.mackerelPatternsList.getEntryByKey(String.valueOf(screen.chimeraVariants.tabbyVar)));
                screen.recalculateListHeight();
            }, Component.empty(), Component.empty(), CreateMorphConstants.setTabbyStripesMackerel);

            selectEntry(screen.chimeraGenetics.tabbyStripes, w.agoutiOptionsList);

            yPos += w.agoutiOptionsList.getWidgetHeight() + screen.getSubRenderableSpacing();


            w.classicPatternsList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 100, 20);
            w.mackerelPatternsList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 100, 20);

            w.classicPatternsList.setX(xPos0);
            w.mackerelPatternsList.setX(xPos0);

            for (int i = 0; i < WCGenetics.Constants.MAX_TABBY_VARIANTS_CLASSIC; i++) {
                w.classicPatternsList.addButton(Component.translatable("screen.cat.classic_stripes", i+1), () -> {
                    screen.chimeraVariants.tabbyVar = Integer.parseInt(w.classicPatternsList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
            }

            for (int i = 0; i < WCGenetics.Constants.MAX_TABBY_VARIANTS_MACKEREL; i++) {
                w.mackerelPatternsList.addButton(Component.translatable("screen.cat.mackerel_stripes", i+1), () -> {
                    screen.chimeraVariants.tabbyVar = Integer.parseInt(w.mackerelPatternsList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
            }


            if (WCGenetics.Agouti.isTabby(screen.chimeraGenetics.agouti) || addLists) {
                screen.addSubRenderable(w.patternsText, screen.otherListHeight);
                screen.addSubRenderable(w.agoutiOptionsList, screen.otherListHeight);

                if (WCGenetics.TabbyStripeTypes.isClassic(screen.chimeraGenetics.tabbyStripes)) {
                    screen.addSubRenderable(w.classicPatternsList, yPos);
                    w.classicPatternsList.setSelected(w.classicPatternsList.getEntryByKey(String.valueOf(screen.chimeraVariants.tabbyVar)));

                } else if (WCGenetics.TabbyStripeTypes.isMackerel(screen.chimeraGenetics.tabbyStripes)) {
                    screen.addSubRenderable(w.mackerelPatternsList, yPos);
                    w.mackerelPatternsList.setSelected(w.mackerelPatternsList.getEntryByKey(String.valueOf(screen.chimeraVariants.tabbyVar)));

                }
            }

        }

        public static void addSilverWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.silver"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 25);

            class SilverWidgets {
                FancySwitchButton silverSwitch;

                FancyStringWidget silverVariantsText;

                FancyButtonScrollList silverVariantsList;
            }

            SilverWidgets w = new SilverWidgets();

            yPos = screen.addSubRenderable(text, yPos);

            String key1 = "screen.button.silver";

            float scale = 0.83f;

            boolean hasGene = WCGenetics.Silver.isSilver(screen.chimeraGenetics.silver, screen.chimeraGenetics.agouti, screen.chimeraGenetics.orangeBase, 1)
                    || WCGenetics.Silver.isSmoke(screen.chimeraGenetics.silver, screen.chimeraGenetics.agouti)
                    || WCGenetics.Silver.isSmokeTortie(screen.chimeraGenetics.silver, screen.chimeraGenetics.agouti, screen.chimeraGenetics.orangeBase);

            w.silverSwitch = new FancySwitchButton(xPos0, yPos,
                    20, key1, hasGene,
                    b -> {

                        screen.chimeraGenetics.silver = ((FancySwitchButton) b).getValue()
                                ? CreateMorphConstants.setSilver : CreateMorphConstants.setNonSilver;

                        if (w.silverSwitch.getValue()) {

                            screen.removeSubRenderable(w.silverVariantsText, screen.otherListHeight);
                            screen.removeSubRenderable(w.silverVariantsList, screen.otherListHeight);

                            screen.addSubRenderable(w.silverVariantsText, screen.otherListHeight);
                            screen.addSubRenderable(w.silverVariantsList, screen.otherListHeight);

                            screen.recalculateListHeight();
                        } else {
                            screen.removeSubRenderable(w.silverVariantsText, screen.otherListHeight);
                            screen.removeSubRenderable(w.silverVariantsList, screen.otherListHeight);
                        }

                        screen.recalculateListHeight();
                    }, 0xFFE6D1A5, scale);

            yPos = screen.addSubRenderable(w.silverSwitch, yPos);


            w.silverVariantsText = new FancyStringWidget(Component.translatable("screen.cat.silver_variants"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 0.9f, false, 105);

            yPos += w.silverVariantsText.getWidgetHeight() + screen.getSubRenderableSpacing();


            w.silverVariantsList = new FancyButtonScrollList(screen.getMinecraft(), width - 10,
                    height - 100, yPos,
                    yPos + 100, 20);

            w.silverVariantsList.setX(xPos0);

            for (int i = 0; i < WCGenetics.Constants.MAX_SILVER_VARIANTS; i++) {
                w.silverVariantsList.addButton(Component.translatable("screen.cat.silver_variant", i+1), () -> {
                    screen.chimeraVariants.silverVar = Integer.parseInt(w.silverVariantsList.getSelectedKey());
                    if (screen.chimeraVariants.silverVar == 2) {
                        screen.displayMessage(
                                Component.translatable("screen.message.silver_pattern_available"),
                                false);
                    }
                }, Component.empty(), Component.empty(), String.valueOf(i));
            }

            w.silverVariantsList.setSelected(w.silverVariantsList.getEntryByKey(String.valueOf(screen.chimeraVariants.silverVar)));


            if (hasGene) {
                screen.addSubRenderable(w.silverVariantsText, screen.otherListHeight);
                screen.addSubRenderable(w.silverVariantsList, screen.otherListHeight);
            }

        }

        public static void addExtrasWidgets(int xPos0, int xPos1, int yPos, int width, int height, int padding, BaseMorphScreen screen) {
            FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.extras"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 1.1f, false, 59);

            class ExtrasWidgets {
                FancyIntSlider rufousingSlider;
                FancyIntSlider blueTintSlider;

                FancyStringWidget noiseText;
                FancyButtonScrollList noiseList;
            }

            ExtrasWidgets w = new ExtrasWidgets();

            yPos = screen.addSubRenderable(text, yPos);

            float scale = 0.83f;

            w.rufousingSlider = new FancyIntSlider(xPos0, yPos,
                    width - padding*2, 15,
                    0, WCGenetics.Constants.MAX_RUFOUSING_VARIANTS,
                    screen.chimeraVariants.rufousingVariant, (button) -> {
                screen.chimeraVariants.rufousingVariant = button.getActualValue();
            }, Component.translatable("screen.cat.rufousing"));

            yPos = screen.addSubRenderable(w.rufousingSlider, yPos);

            w.blueTintSlider = new FancyIntSlider(xPos0, yPos,
                    width - padding*2, 15,
                    0, WCGenetics.Constants.MAX_BLUE_RUFOUSING_VARIANTS,
                    screen.chimeraVariants.blueRufousingVariant, (button) -> {
                screen.chimeraVariants.blueRufousingVariant = button.getActualValue();
            }, Component.translatable("screen.cat.blue_tint"));

            yPos = screen.addSubRenderable(w.blueTintSlider, yPos);

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

            w.noiseText = new FancyStringWidget(Component.translatable("screen.cat.noise"),
                    xPos0 + padding, xPos1 - padding,
                    yPos, 0.9f, false, 105);

            yPos = screen.addSubRenderable(w.noiseText, yPos);

            w.noiseList = new FancyButtonScrollList(screen.getMinecraft(), width,
                    height - 100, yPos,
                    yPos + 100, 20);

            w.noiseList.setX(xPos0);

            for (int i = 0; i < WCGenetics.Constants.MAX_NOISE_VARIANTS; i++) {
                if (i == 5) {
                    w.noiseList.addButton(Component.translatable("screen.cat.no_noise_var"), () -> {
                        screen.chimeraVariants.noise = Integer.parseInt(w.noiseList.getSelectedKey());
                    }, Component.empty(), Component.empty(), String.valueOf(i));
                    continue;
                }
                w.noiseList.addButton(Component.translatable("screen.cat.noise_var", i + 1), () -> {
                    screen.chimeraVariants.noise = Integer.parseInt(w.noiseList.getSelectedKey());
                }, Component.empty(), Component.empty(), String.valueOf(i));
            }

            w.noiseList.setSelected(w.noiseList.getEntryByKey(String.valueOf(screen.chimeraVariants.noise)));

            yPos = screen.addSubRenderable(w.noiseList, yPos);

        }

    }


    private static final Map<String, Predicate<String>> predicates = new LinkedHashMap<>();
    static {
        predicates.put(CreateMorphConstants.setBlack, WCGenetics.Base::isBlack);
        predicates.put(CreateMorphConstants.setChocolate, WCGenetics.Base::isChocolate);
        predicates.put(CreateMorphConstants.setCinnamon, WCGenetics.Base::isCinnamon);

        predicates.put(CreateMorphConstants.setTabbyStripesClassic, WCGenetics.TabbyStripeTypes::isClassic);
        predicates.put(CreateMorphConstants.setTabbyStripesMackerel, WCGenetics.TabbyStripeTypes::isMackerel);
    }

    private static void selectEntry(String genetic, FancyButtonScrollList widget) {
        for (var entry : predicates.entrySet()) {
            if (entry.getValue().test(genetic)) {
                widget.setSelected(widget.getEntryByKey(entry.getKey()));
                break;
            }
        }
    }
}
