package net.snowteb.warriorcats_events.screen.screens.createmorph;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.client.ClientClanData;
import net.snowteb.warriorcats_events.client.ClientStoredMorphs;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.c2s.cats.SummonCustomCatPacket;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.SavePlayerGeneticsPacket;
import net.snowteb.warriorcats_events.network.packet.c2s.others.CtSShareMorphToChat;
import net.snowteb.warriorcats_events.screen.screens.SpawnLocationScreen;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;
import tocraft.walkers.api.PlayerShape;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.snowteb.warriorcats_events.screen.screens.CreateClanScreen.BG_TEXTURE;

public class CreateMorphScreen extends BaseMorphScreen {

    float animationTime = 0f;
    private float duration = 20f;
    boolean closing = false;
    private final float startX = 0;
    private final float endX = -700;
    private boolean comingBackFromChimeraMenu = false;

    private int minY = 0;
    private int maxY = 0;


    private FancyButtonScrollList optionsList;

    private FancySwitchButton switchButton;
    private FancySimpleButton saveAndNext;


    private FancySimpleButton randomizeCat;
    private FancySimpleButton newCat;

    private FancyMorphsList morphsList;

    private final boolean isSummoning;

    public CreateMorphScreen(boolean isSummoning) {
        super(Component.literal("Create Morph Genetics"));
        menuX = 500;
        this.isSummoning = isSummoning;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            if (PlayerShape.getCurrentShape(player) instanceof WCatEntity cat) {
                this.genetics = new WCGenetics(cat.getGeneticsModule().getGenetics());
                this.variants = new WCGenetics.GeneticalVariants(cat.getGeneticsModule().getGenVariants());
                this.chimeraGenetics = new WCGenetics(cat.getGeneticsModule().getChimeraGenetics());
                this.chimeraVariants = new WCGenetics.GeneticalChimeraVariants(cat.getGeneticsModule().getChimeraGenVariants());
                this.onGeneticalSkin = cat.isOnGeneticalSkin();
                if (!cat.isOnGeneticalSkin()) {
                    this.presetVariant = cat.getVariant();
                }
            }
        }
    }

    public CreateMorphScreen(WCGenetics.PackedGeneticData data, boolean isSummoning) {
        super(Component.literal("Create Morph Genetics"));
        comingBackFromChimeraMenu = true;
        menuX = -500;

        this.genetics = new WCGenetics(data.genetics);
        this.variants = new WCGenetics.GeneticalVariants(data.variants);
        this.chimeraGenetics = new WCGenetics(data.chimerasGenetics);
        this.chimeraVariants = new WCGenetics.GeneticalChimeraVariants(data.chimeraVariants);
        this.onGeneticalSkin = data.onGeneticalSkin;

        this.isSummoning = isSummoning;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        minY = centerY - 90;
        maxY = centerY + 45;

        if (!onGeneticalSkin){
            drawFirstOptionsPreset();
        } else {
            drawFirstOptionsGenetical();
        }

        float scale = 0.80f;

        {
            String key = "screen.button.genetical_skin";
            switchButton = new FancySwitchButton(6, this.optionsList.getY() - 5 - 18,
                    18, key, onGeneticalSkin, b -> {
                if (!switchButton.getValue()) this.drawFirstOptionsPreset();
                else this.drawFirstOptionsGenetical();

                drawSubRenderables(true);
                this.onGeneticalSkin = switchButton.getValue();
            }, 0xFFE6D1A5, scale);

            this.addRenderableWidget(switchButton);
        }

        int middleMinX = optionsList.getWidth() + optionsList.getX() + 7;
        int middleMinY = optionsList.getY();
        int middleMaxX = this.width - (this.width / 4) - 25;
        int middleMaxY = maxY + 45;

        int buttonWidth = (middleMaxX - middleMinX)/2;

        randomizeCat = new FancySimpleButton(buttonWidth - 2, 20,
                middleMinX, middleMaxY + 5,
                Component.empty()
                        .append(Component.literal("\uD83C\uDFB2 "))
                        .append(Component.translatable("screen.button.randomize")),
                b -> {
            WCGenetics.RandomizedGenetics random = WCGenetics.RandomizedGenetics
                    .randomize(Minecraft.getInstance().player.getRandom());

            this.genetics = new  WCGenetics(random.genetics);
            this.variants = new  WCGenetics.GeneticalVariants(random.variants);
            this.chimeraGenetics = new  WCGenetics(random.chimeraGenetics);
            this.chimeraVariants = new  WCGenetics.GeneticalChimeraVariants(random.chimeraVariants);

            this.characterBoxZoom = -8f;

            this.drawSubRenderables(false);

        }, 0.65f, Component.translatable("screen.button.randomize_und").getString(), false);

        this.addRenderableWidget(randomizeCat);

        newCat = new FancySimpleButton(buttonWidth - 2, 20,
                middleMinX + 4 + randomizeCat.getWidth(), middleMaxY + 5,
                Component.empty()
                        .append(Component.literal("✨ ").withStyle(ChatFormatting.BOLD))
                        .append(Component.translatable("screen.button.new_cat")),
                b -> {

            this.genetics = new  WCGenetics();
            this.variants = new  WCGenetics.GeneticalVariants();
            this.chimeraGenetics = new  WCGenetics();
            this.chimeraVariants = new  WCGenetics.GeneticalChimeraVariants();

            this.characterBoxZoom = -8f;

            this.drawSubRenderables(false);

        }, 0.65f, Component.translatable("screen.button.new_cat_und").getString(), false);

        this.addRenderableWidget(newCat);

        FancySelectableButton freeView = new FancySelectableButton(
                21, 12,
                middleMinX + 3,middleMaxY - 15,
                Component.literal("\uD83D\uDC41↺"), button -> {
            catFreeView = button.isSelected();
        }, 1f, 0xFFE6D1A5, "");
        freeView.setSelected(catFreeView);

        this.addRenderableWidget(freeView);


        setupStoredMorphsWidgets();


        setupBaseValues();
        drawSubRenderables(true);


        int saveWidth = (otherListsMaxX - otherListsMinX)/2;
        int saveHeight = 18;
        int saveX = otherListsMaxX - saveWidth;

        Component saveText = this.isSummoning ? Component.translatable("screen.button.summon_cat")
        : Component.translatable("screen.button.save_and_next");

        saveAndNext = new FancySimpleButton(saveWidth,saveHeight,
                saveX, this.switchButton.getY() + 2,
                Component.empty()
                        .append(saveText)
                        .append(" ➡"),
                b -> {
                    closing = true;
                    animationTime = 0f;
                    WCEClient.nextMenuSound();
        }, scale);

        this.addRenderableWidget(saveAndNext);
    }


    boolean openChimeraMenu = false;
    private void saveAndNext() {

        WCGenetics geneticsCopy =
                new WCGenetics(this.genetics);
        WCGenetics.GeneticalVariants variantsCopy =
                new WCGenetics.GeneticalVariants(this.variants);
        WCGenetics geneticsChimeraCopy =
                new WCGenetics(this.chimeraGenetics);
        WCGenetics.GeneticalChimeraVariants chimeraVariantsCopy =
                new WCGenetics.GeneticalChimeraVariants(this.chimeraVariants);

        WCGenetics.PackedGeneticData data = new WCGenetics.PackedGeneticData(geneticsCopy, variantsCopy,
                geneticsChimeraCopy, chimeraVariantsCopy, onGeneticalSkin, presetVariant);

        if (openChimeraMenu) {
            this.minecraft.setScreen(new CreateMorphChimeraScreen(data, this.isSummoning));
            return;
        }

        if (this.isSummoning) {
            ModPackets.sendToServer(new SummonCustomCatPacket(onGeneticalSkin,
                    geneticsCopy, variantsCopy,
                    geneticsChimeraCopy, chimeraVariantsCopy));
            this.minecraft.setScreen(null);
            return;
        }

        ModPackets.sendToServer(new SavePlayerGeneticsPacket(data));


        this.minecraft.setScreen(null);
        if (!ClientClanData.get().isFirstLoginHandled()) {
            if (WCEServerConfig.SERVER.TELEPORT_WHEN_JOIN.get()) this.minecraft.setScreen(new SpawnLocationScreen());
            else this.minecraft.setScreen(null);
        }
    }


    FancySimpleButton loadButton;
    EditBox morphSearchBox;
    FancySimpleButton saveButton;
    FancySimpleButton deleteButton;
    FancySimpleButton shareToChatButton;
    private void setupStoredMorphsWidgets() {
        this.removeWidget(loadButton);
        this.removeWidget(morphSearchBox);
        this.removeWidget(saveButton);
        this.removeWidget(morphsList);
        this.removeWidget(shareToChatButton);

        int morphListWidth = optionsList.getWidth();

        int morphListY0 = optionsList.getBottom() + 20;
        int morphListY1 = maxY + 45 + 12;
        int morphListX0 = optionsList.getX();
        int morphListX1 = morphListX0 + morphListWidth;

        morphsList = new FancyMorphsList(this.getMinecraft(),
                morphListWidth, 120, morphListY0,
                morphListY1, 22);
        morphsList.setX(morphListX0);

        morphsList.setClickableIn((pMouseX, pMouseY) -> {
         return pMouseX > morphListX0 && pMouseX < morphListX1
                 && pMouseY > morphListY0 && pMouseY < morphListY1;
        });

        for (Map.Entry<String, ClientStoredMorphs.MorphsFile.MorphData> entry : ClientStoredMorphs.DATA.morphs.entrySet()) {
            morphsList.addMorph(entry.getKey(), entry.getValue(), () -> {
                if (lastAction != null) {
                    lastAction = null;
                    displayErrorTime = 20;
                }
            });
        }

        this.addRenderableWidget(morphsList);

        loadButton = new FancySimpleButton(37, 12, morphListX0, morphListY0 - 15,
                Component.empty()
                        .append("➜] ")
                        .append(Component.translatable("screen.button.load_morph")),
                but -> {
            if (morphsList.getSelected() == null) {
                this.displayMessage(Component.translatable("screen.error.morph_not_selected"), true);
                return;
            }

            if (lastAction == null || lastAction.button != but) {
                lastAction = new Action(but, this);
                return;
            }
            lastAction = null;
            displayMessage(Component.translatable("screen.message.morph_loaded"), false, 60);

            loadSelectedMorph();
            morphsList.setSelected(null);
        }, 0.7f);

        this.addRenderableWidget(loadButton);

        int textBoxX = morphListX0 + loadButton.getWidth() + 5;
        int textBoxWidth = morphListX1 - morphListX0 - loadButton.getWidth() - 7;
        morphSearchBox = new EditBox(this.font,
                textBoxX, morphListY0 - 14,
                textBoxWidth, 10, Component.empty());
        morphSearchBox.setHint(Component.literal("\uD83D\uDD0D...").withStyle(ChatFormatting.DARK_GRAY));
        morphSearchBox.setResponder(text -> {
            morphsList.setScrollAmount(0);

            morphsList.children().clear();
            for (Map.Entry<String, ClientStoredMorphs.MorphsFile.MorphData> entry : ClientStoredMorphs.DATA.morphs.entrySet()) {
                if (entry.getKey().toLowerCase().contains(text.toLowerCase())) {
                    morphsList.addMorph(entry.getKey(), entry.getValue(), () -> {
                        if (lastAction != null) {
                            lastAction = null;
                            displayErrorTime = 20;
                        }
                    });
                }
            }

            if (text.isEmpty()) {
                for (Map.Entry<String, ClientStoredMorphs.MorphsFile.MorphData> entry : ClientStoredMorphs.DATA.morphs.entrySet()) {
                    morphsList.addMorph(entry.getKey(), entry.getValue(), () -> {
                        if (lastAction != null) {
                            lastAction = null;
                            displayErrorTime = 20;
                        }
                    });
                }
            }
        });

        this.addRenderableWidget(morphSearchBox);

        shareToChatButton = new FancySimpleButton(10, 10, morphListX0, morphListY1 + 3,
                Component.empty().append("\uD83D\uDD17"),
                but -> {
                    if (morphsList.getSelected() == null) {
                        this.displayMessage(Component.translatable("screen.error.morph_not_selected"), true);
                        return;
                    }

                    if (lastAction == null || lastAction.button != but) {
                        lastAction = new Action(but, this,
                                Component.translatable("screen.message.send_morph_to_chat",
                                        Component.literal(morphsList.getSelectedKey()).withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC)),
                                false, 140);
                        return;
                    }
                    lastAction = null;

                    sendMorphToChat();
                }, 0.7f, true);

        this.addRenderableWidget(shareToChatButton);



        int subButtonsWidth = (optionsList.getWidth()/2) - 8;

        saveButton = new FancySimpleButton(subButtonsWidth, 10, morphListX0 + 13, morphListY1 + 3,
                Component.empty()
                        .append("\uD83D\uDCBE ")
                        .append(Component.translatable("screen.button.save_morph")),
                but -> {
                    drawSaveMorphMenuWidgets(but);
                    but.active = false;
                }, 0.7f, true);

        this.addRenderableWidget(saveButton);

        deleteButton = new FancySimpleButton(subButtonsWidth, 10,13 + morphListX0 + subButtonsWidth + 3, morphListY1 + 3,
                Component.empty()
                        .append("\uD83D\uDDD1 ")
                        .append(Component.translatable("screen.button.delete_morph")),
                but -> {
                    if (morphsList.getSelected() == null) {
                        this.displayMessage(Component.translatable("screen.error.morph_not_selected"), true);
                        return;
                    }

                    if (lastAction == null || lastAction.button != but) {
                        lastAction = new Action(but, this,
                                Component.translatable("screen.message.morph_confirm_delete",
                                        Component.literal(morphsList.getSelectedKey()).withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC)),
                                true, 180);
                        return;
                    }
                    lastAction = null;
                    displayMessage(Component.translatable("screen.message.morph_deleted",
                                    Component.literal(morphsList.getSelectedKey()).withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC)),
                            false, 100);

                    deleteSelectedMorph();
                    setupStoredMorphsWidgets();
                    morphsList.setSelected(null);

                }, 0.7f, true);

        this.addRenderableWidget(deleteButton);

    }

    private void sendMorphToChat() {
        if (morphsList.getSelected() != null) {
            String key = morphsList.getSelectedKey();
            ClientStoredMorphs.MorphsFile.MorphData data = ClientStoredMorphs.DATA.morphs.get(key);
            if (data != null) {
                WCGenetics geneticsCopy = data.genetics();
                WCGenetics geneticsChimeraCopy = data.chimeraGenetics();
                WCGenetics.GeneticalVariants variants = data.variants();
                WCGenetics.GeneticalChimeraVariants chimeraVariantsCopy = data.chimeraVariants();

                ModPackets.sendToServer(new CtSShareMorphToChat(key, geneticsCopy, variants,
                        geneticsChimeraCopy, chimeraVariantsCopy,
                        onGeneticalSkin, presetVariant));
                Minecraft.getInstance().setScreen(null);

                if (Minecraft.getInstance().player != null) {
                    LocalPlayer player = Minecraft.getInstance().player;

                    Path path = Minecraft.getInstance().gameDirectory.toPath().resolve("config")
                            .resolve(WarriorCatsEvents.MODID);

                    Component filePath = Component.literal("[File]")
                            .withStyle(style -> style.withClickEvent(
                                    new ClickEvent(ClickEvent.Action.OPEN_FILE, path.toString())
                            ).withHoverEvent(
                                    new HoverEvent(
                                            HoverEvent.Action.SHOW_TEXT,
                                            Component.translatable("mco.notification.visitUrl.buttonText.default")
                                                    .withStyle(ChatFormatting.GRAY)
                                    )
                            ));

                    Component message = Component.empty()
                                    .append(Component.translatable("message.send_morph_to_chat",
                                            key,
                                            filePath.copy().withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.UNDERLINE)
                                    ).withStyle(ChatFormatting.GRAY));


                    player.sendSystemMessage(message);
                }

            } else {
                this.displayMessage(Component.translatable("screen.error.data_not_preset"), true);
            }
        }
    }

    private void loadSelectedMorph() {
        if (morphsList.getSelected() != null) {
            String key = morphsList.getSelectedKey();
            ClientStoredMorphs.MorphsFile.MorphData data = ClientStoredMorphs.DATA.morphs.get(key);
            if (data != null) {

                WCGenetics geneticsCopy = new WCGenetics(data.genetics());
                WCGenetics chimeraGeneticsCopy = new WCGenetics(data.chimeraGenetics());

                WCGenetics.GeneticalVariants variantsCopy = new WCGenetics.GeneticalVariants(data.variants());
                WCGenetics.GeneticalChimeraVariants chimeraVariantsCopy = new WCGenetics.GeneticalChimeraVariants(data.chimeraVariants());

                this.genetics = geneticsCopy;
                this.variants = variantsCopy;
                this.chimeraGenetics = chimeraGeneticsCopy;
                this.chimeraVariants = chimeraVariantsCopy;
                this.onGeneticalSkin = data.onGeneticalSkin();
                this.presetVariant = data.presetVariant();

                this.characterBoxZoom = -5f;
                this.clearWidgets();
                this.init();
            } else {
                this.displayMessage(Component.translatable("screen.error.data_not_preset"), true);
            }
        } else {
            this.displayMessage(Component.translatable("screen.error.morph_not_selected"), true);
        }
    }

    private void deleteSelectedMorph() {
        if (morphsList.getSelected() != null) {
            String key = morphsList.getSelectedKey();
            ClientStoredMorphs.MorphsFile.MorphData data = ClientStoredMorphs.DATA.morphs.get(key);
            if (data != null) {
                ClientStoredMorphs.remove(key);
                characterBoxZoom = -5f;
            } else {
                this.displayMessage(Component.translatable("screen.error.data_not_preset"), true);
            }
        } else {
            this.displayMessage(Component.translatable("screen.error.morph_not_selected"), true);
        }
    }

    private void drawSaveMorphMenuWidgets(FancySimpleButton parentButton) {
        int minX = optionsList.getWidth() + optionsList.getX();
        int maxX = this.width - (this.width / 4) - 5 - 10;

        int spacingFromMiddle = 20;

        int xPos0 = minX + 7 + spacingFromMiddle;
        int xPos1 = maxX - 10 - spacingFromMiddle;
        int yPos0 = optionsList.getY() + spacingFromMiddle + 20;
        int yPos1 = maxY + 45 - spacingFromMiddle - 20;

        int squareWidth = xPos1 - xPos0;
        int squareHeight = yPos1 - yPos0;

        int squareCenterX = xPos0 + squareWidth/2;
        int squareCenterY = yPos0 + squareHeight/2;

        FancySubRenderablesSquare square = new FancySubRenderablesSquare(xPos0, yPos0,
                squareWidth, squareHeight);

        int boxWidth = (int) (squareWidth/1.5);
        EditBox nameBox = new EditBox(this.font,
                squareCenterX - boxWidth/2, squareCenterY - 10,
                boxWidth, 15, Component.empty());

        square.addWidget(nameBox);

        FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.cat.enter_morph_name"),
                xPos0 + 10, xPos1 - 10, squareCenterY - 30,0.9f,true, 40);

        square.addWidget(text);

        int buttonsWidth = (squareWidth - 30)/2;
        int buttonsHeight = 15;

        FancySimpleButton saveButton = new FancySimpleButton(
                buttonsWidth, buttonsHeight,
                xPos0 + 10, squareCenterY + 30,
                Component.empty()
                        .append("\uD83D\uDCBE ")
                        .append(Component.translatable("screen.button.save_morph")),
                but -> {
                    if (nameBox.getValue().isEmpty()) {
                        this.displayMessage(Component.translatable("screen.error.morph_name_empty"),
                                true);
                        return;
                    }

                    boolean overwrite = lastAction != null && lastAction.button == but;

                    if (saveCurrentMorph(nameBox.getValue(), overwrite)) {
                        this.removeWidget(square);
                        parentButton.active = true;
                        this.setupStoredMorphsWidgets();
                    } else {
                        this.lastAction = new Action(but, this,
                                Component.translatable("screen.message.overwrite_morph",
                                        Component.literal(nameBox.getValue()).withStyle(ChatFormatting.GOLD,  ChatFormatting.ITALIC)),
                                true, 140);
                    }
                }, 0.9f, true);

        FancySimpleButton cancelButton = new FancySimpleButton(
                buttonsWidth, buttonsHeight,
                xPos1 - 10 - buttonsWidth, squareCenterY + 30,
                Component.empty()
                        .append("❌ ")
                        .append(Component.translatable("screen.button.cancel")),
                but -> {
                    this.removeWidget(square);
                    parentButton.active = true;
                }, 0.9f, true);

        square.addWidget(cancelButton);
        square.addWidget(saveButton);

        this.addRenderableWidget(square);
    }

    private boolean saveCurrentMorph(String key, boolean overwrite) {
        WCGenetics geneticsCopy = new WCGenetics(genetics);
        WCGenetics geneticsChimeraCopy = new WCGenetics(chimeraGenetics);
        WCGenetics.GeneticalVariants variantsCopy = new WCGenetics.GeneticalVariants(variants);
        WCGenetics.GeneticalChimeraVariants chimeraVariantsCopy = new WCGenetics.GeneticalChimeraVariants(chimeraVariants);

        ClientStoredMorphs.MorphsFile.MorphData data =
                new ClientStoredMorphs.MorphsFile.MorphData(
                        geneticsCopy, geneticsChimeraCopy,
                        variantsCopy, chimeraVariantsCopy,
                        onGeneticalSkin, presetVariant);
        boolean success = false;

        if (!key.isEmpty()) {
            success = ClientStoredMorphs.add(key, data, overwrite);

            if (success) {
                this.displayMessage(
                        Component.translatable("screen.message.morph_saved",
                                Component.literal(key).withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC)),
                        false);
            }
        }

        return success;
    }

    private void drawFirstOptionsGenetical() {
        this.removeWidget(optionsList);

        optionsList = new FancyButtonScrollList(minecraft, this.width / 4 - 10,
                height - 100, minY,
                maxY - 25, 33);

        optionsList.setX(6);


        optionsList.addButton(Component.translatable("screen.cat.base_section"), () -> {
                    drawSubRenderables(true);
                }, Component.translatable("screen.cat.base_section_und"),
                Component.translatable("screen.cat.base_section_ins"), "base");

        optionsList.addButton(Component.translatable("screen.cat.orange_section"), () -> {
                    drawSubRenderables(true);
                }, Component.translatable("screen.cat.orange_section_und"),
                Component.translatable("screen.cat.orange_section_ins"), "orange");

        optionsList.addButton(Component.translatable("screen.cat.white_section"), () -> {
                    drawSubRenderables(true);
                }, Component.translatable("screen.cat.white_section_und"),
                Component.translatable("screen.cat.white_section_ins"), "white");

        optionsList.addButton(Component.translatable("screen.cat.albinism_section"), () -> {
                    drawSubRenderables(true);
                }, Component.translatable("screen.cat.albinism_section_und"),
                Component.translatable("screen.cat.albinism_section_ins"), "albinism");

        optionsList.addButton(Component.translatable("screen.cat.dilute_section"), () -> {
                    drawSubRenderables(true);
                }, Component.translatable("screen.cat.dilute_section_und"),
                Component.translatable("screen.cat.dilute_section_ins"), "dilute");

        optionsList.addButton(Component.translatable("screen.cat.agouti_section"), () -> {
                    drawSubRenderables(true);
                }, Component.translatable("screen.cat.agouti_section_und"),
                Component.translatable("screen.cat.agouti_section_ins"), "agouti");

        optionsList.addButton(Component.translatable("screen.cat.silver_section"), () -> {
                    drawSubRenderables(true);
                }, Component.translatable("screen.cat.silver_section_und"),
                Component.translatable("screen.cat.silver_section_ins"), "silver");

        optionsList.addButton(Component.translatable("screen.cat.eyes_section"), () -> {
                    drawSubRenderables(true);
                }, Component.translatable("screen.cat.eyes_section_und"),
                Component.translatable("screen.cat.eyes_section_ins"), "eyes");

        optionsList.addButton(Component.translatable("screen.cat.details_section"), () -> {
                    drawSubRenderables(true);
                }, Component.translatable("screen.cat.details_section_und"),
                Component.translatable("screen.cat.details_section_ins"), "details");

        optionsList.addButton(Component.translatable("screen.cat.extra_section"), () -> {
                    drawSubRenderables(true);
                }, Component.translatable("screen.cat.extra_section_und"),
                Component.translatable("screen.cat.extra_section_ins"), "extra");

        optionsList.addButton(Component.translatable("screen.cat.chimerism_section"), () -> {
                    drawSubRenderables(true);
                }, Component.translatable("screen.cat.chimerism_section_und"),
                Component.translatable("screen.cat.chimerism_section_ins"), "chimerism");

        optionsList.setSelected(optionsList.getEntryByKey("base"));

        this.addRenderableWidget(optionsList);
    }

    private void drawFirstOptionsPreset() {
        this.removeWidget(optionsList);

        optionsList = new FancyButtonScrollList(minecraft, this.width / 4 - 10,
                height - 100, minY,
                maxY - 25, 25);

        optionsList.setX(6);


        List<CreateMorphConstants.Variant> variants = CreateMorphConstants.getVariants();
        for (CreateMorphConstants.Variant variant : variants) {
            optionsList.addButton(variant.component(), () -> {
                    }, variant.underComponent(),
                    Component.empty(), String.valueOf(variant.id()));
        }
        optionsList.setSelected(optionsList.getEntryByKey(String.valueOf(presetVariant)));

        this.addRenderableWidget(optionsList);
    }

    private void setupBaseValues() {

        int otherListsWidth = this.width / 4 + 12;

        currentOtherListYPos = 0;

        otherListsMinX = this.width - otherListsWidth - 6;
        otherListsMaxX = this.width - 12;
        otherListsMinY = this.optionsList.getY();
        otherListsMaxY = maxY + 45;
    }

    private void drawSubRenderables(boolean onWidgetsReload) {
        this.clearSubRenderables();
        if (onWidgetsReload) this.onOtherWidgetsReload();

        int padding = getSubRenderableSpacing();

        int xPos0 = otherListsMinX + padding;
        int yPos0 = otherListsMinY + padding;

        int xPos1 = otherListsMaxX - padding;
        int yPos1 = otherListsMaxY - padding;

        int width = xPos1 - xPos0;
        int height = yPos1 - yPos0;

        int yPos = yPos0 + 10;

        if (this.optionsList.getSelectedKey().equals("base")) {
            AddSubRenderables.Genetics.addBaseWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        } else if (this.optionsList.getSelectedKey().equals("orange")) {
            AddSubRenderables.Genetics.addOrangeWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        } else if (this.optionsList.getSelectedKey().equals("white")) {
            AddSubRenderables.Genetics.addWhiteWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        } else if (this.optionsList.getSelectedKey().equals("albinism")) {
            AddSubRenderables.Genetics.addAlbinoWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        } else if (this.optionsList.getSelectedKey().equals("dilute")) {
            AddSubRenderables.Genetics.addDiluteWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        } else if (this.optionsList.getSelectedKey().equals("agouti")) {
            AddSubRenderables.Genetics.addAgoutiWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        } else if (this.optionsList.getSelectedKey().equals("silver")) {
            AddSubRenderables.Genetics.addSilverWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        } else if (this.optionsList.getSelectedKey().equals("eyes")) {
            AddSubRenderables.Genetics.addEyesWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        } else if (this.optionsList.getSelectedKey().equals("chimerism")) {
            AddSubRenderables.Genetics.addChimerismWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        } else if (this.optionsList.getSelectedKey().equals("details")) {
            AddSubRenderables.Genetics.addDetailsWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        } else if (this.optionsList.getSelectedKey().equals("extra")) {
            AddSubRenderables.Genetics.addExtrasWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        } else {
            AddSubRenderables.Genetics.addPresetWidgets(xPos0, xPos1, yPos, width, height, padding, this);
        }

        this.recalculateListHeight();
    }

    private void onOtherWidgetsReload() {
        currentOtherListYPos = 0;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        this.randomizeCat.visible = onGeneticalSkin;
        this.newCat.visible = onGeneticalSkin;

        this.renderPanorama(pGuiGraphics, pPartialTick);
        this.renderBlurredBackground(pPartialTick);

        handleScreenOffset(pPartialTick);

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(menuX, 0, 0);

        this.optionsList.setMenuX((int) menuX);
        this.morphsList.setMenuX((int) menuX);

        drawMenuBackground(pGuiGraphics);

        drawOtherListsBackground(pGuiGraphics);

        renderCharacter(pGuiGraphics, pMouseX, pMouseY);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        renderSectionText(pGuiGraphics);

        renderDecorations(pGuiGraphics);

        if (!saveButton.active) {
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0, 0, 980);
            pGuiGraphics.fill(0,0, width, height, 0x88000000);
            pGuiGraphics.pose().popPose();
        }

        pGuiGraphics.pose().popPose();
    }

    private void renderDecorations(GuiGraphics pGuiGraphics) {
        int pieceWidth = 60;
        int pieceHeight = 30;

        int miniPieceWidth = 40;
        int miniPieceHeight = 28;

        int textureWidth = 60;
        int textureHeight = 180;

        int miniTextureWidth = 40;
        int miniTextureHeight = 168;

        float scale = 0.8f;

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0, 0, 300);
        RenderSystem.enableBlend();

        pGuiGraphics.setColor(0.9f,0.8f,0.7f,1f);

        int x = optionsList.getX() - 4;
        int y = optionsList.getY() - 11;

        {
            pGuiGraphics.blit(DECORATIONS,
                    x, y,
                    (int) (pieceWidth * scale), (int) (pieceHeight * scale),
                    0, (pieceHeight * 5) * scale,
                    (int) -(pieceWidth * scale), (int) (pieceHeight * scale),
                    (int) (textureWidth * scale), (int) (textureHeight * scale));
        }
        {
            x = optionsList.getRight() - pieceWidth + 15;
            y = optionsList.getBottom() - 14;

            pGuiGraphics.blit(DECORATIONS,
                    x, y,
                    (int) (pieceWidth * scale), (int) (pieceHeight * scale),
                    0, (pieceHeight * 3) * scale,
                    (int) -(pieceWidth * scale), (int) -(pieceHeight * scale),
                    (int) (textureWidth * scale), (int) (textureHeight * scale));

            x = optionsList.getRight() - miniPieceWidth + 12;
            y = optionsList.getY() - 13;

            pGuiGraphics.blit(MINI_DECORATIONS,
                    x, y,
                    (int) (miniPieceWidth * scale), (int) (miniPieceHeight * scale),
                    0, (miniPieceHeight * 5) * scale,
                    (int) (miniPieceWidth * scale), (int) (miniPieceHeight * scale),
                    (int) (miniPieceWidth * scale), (int) (miniTextureHeight * scale));
        }
        {
            int minX = optionsList.getWidth() + optionsList.getX();
            int maxX = this.width - (this.width / 4) - 5 - 10;

            int xPos0 = minX + 7;
            int xPos1 = maxX - 10;

            int yPos0 = optionsList.getY();
            int yPos1 = maxY + 45;

            x = xPos0 - 5;
            y = yPos1 - 16;

            pGuiGraphics.blit(DECORATIONS,
                    x, y,
                    (int) (pieceWidth * scale), (int) (pieceHeight * scale),
                    0, (pieceHeight * 5) * scale,
                    (int) -(pieceWidth * scale), (int) -(pieceHeight * scale),
                    (int) (textureWidth * scale), (int) (textureHeight * scale));

            x = xPos1 - pieceWidth + 15;
            y = yPos0 - 8;

            pGuiGraphics.blit(DECORATIONS,
                    x, y,
                    (int) (pieceWidth * scale), (int) (pieceHeight * scale),
                    0, (pieceHeight * 1) * scale,
                    (int) -(pieceWidth * scale), (int) (pieceHeight * scale),
                    (int) (textureWidth * scale), (int) (textureHeight * scale));


        }

        {

            x = otherListsMinX - 3;
            y = otherListsMinY - 10;

            pGuiGraphics.blit(DECORATIONS,
                    x, y,
                    (int) (pieceWidth * scale), (int) (pieceHeight * scale),
                    0, (pieceHeight * 2) * scale,
                    (int) (pieceWidth * scale), (int) (pieceHeight * scale),
                    (int) (textureWidth * scale), (int) (textureHeight * scale));

            x = otherListsMaxX - pieceWidth + 18;
            y = otherListsMaxY - 11;

            pGuiGraphics.blit(DECORATIONS,
                    x, y,
                    (int) (pieceWidth * scale), (int) (pieceHeight * scale),
                    0, (pieceHeight * 1) * scale,
                    (int) -(pieceWidth * scale), (int) -(pieceHeight * scale),
                    (int) (textureWidth * scale), (int) (textureHeight * scale));


            x = otherListsMaxX - miniPieceWidth + 16;
            y = otherListsMinY - 12;

            pGuiGraphics.blit(MINI_DECORATIONS,
                    x, y,
                    (int) (miniPieceWidth * scale), (int) (miniPieceHeight * scale),
                    0, (miniPieceHeight * 2) * scale,
                    (int) -(miniPieceWidth * scale), (int) (miniPieceHeight * scale),
                    (int) (miniTextureWidth * scale), (int) (miniTextureHeight * scale));

            x = otherListsMinX - 8;
            y = otherListsMaxY - 13;

            pGuiGraphics.blit(MINI_DECORATIONS,
                    x, y,
                    (int) (miniPieceWidth * scale), (int) (miniPieceHeight * scale),
                    0, (miniPieceHeight * 1) * scale,
                    (int) (miniPieceWidth * scale), (int) (miniPieceHeight * scale),
                    (int) (miniTextureWidth * scale), (int) (miniTextureHeight * scale));


        }
        {
            x = saveAndNext.getX() - 3;
            y = saveAndNext.getY() - 10;

            scale = 0.7f;

            pGuiGraphics.blit(MINI_DECORATIONS,
                    x, y,
                    (int) (miniPieceWidth * scale), (int) (miniPieceHeight * scale),
                    0, (miniPieceHeight * 4) * scale,
                    (int) -(miniPieceWidth * scale), (int) (miniPieceHeight * scale),
                    (int) (miniTextureWidth * scale), (int) (miniTextureHeight * scale));

        }


        pGuiGraphics.setColor(1,1,1,1);

        RenderSystem.disableBlend();
        pGuiGraphics.pose().popPose();

    }



    private void renderSectionText(GuiGraphics pGuiGraphics) {
        if (this.optionsList.getSelected() != null) {
            FancyButtonScrollList.Entry entry = this.optionsList.getSelected();

            int minX = optionsList.getWidth() + optionsList.getX();
            int maxX = this.width - (this.width / 4) - 5 - 40;

            int xPos0 = minX + 7;
            int xPos1 = maxX - 10;

            int yPos0 = optionsList.getY();
            int yPos1 = maxY;

            int xOffset = this.switchButton.getValue() ? 22 : 2;

            int color = 0xFFF2DCAE;

            if (this.switchButton.getValue()) {
                int iconSize = 20;

                String path = "textures/gui/createmorph/icon_" + entry.key + ".png";
                float a = ((color >> 24) & 0xFF) / 255f;
                float r = ((color >> 16) & 0xFF) / 255f;
                float g = ((color >> 8) & 0xFF) / 255f;
                float b = (color & 0xFF) / 255f;

                pGuiGraphics.setColor(r, g, b, a);
                pGuiGraphics.blit(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, path),
                        xPos0, yPos0 - xOffset,
                        0, 0,
                        iconSize, iconSize,
                        iconSize, iconSize);
                pGuiGraphics.setColor(1, 1, 1, 1);
            }


            float scale1 = 1.1f;
            float scale2 = 0.5f;


            int maxWidth = xPos1 - xPos0 - xOffset;
            List<FormattedCharSequence> list =
                    this.font.split(FormattedText.of(entry.instruction),
                            (int) ((maxWidth / scale1) / scale2));

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(xPos0 + xOffset, yPos0 - 12 - (5 * list.size()), 0);
            pGuiGraphics.pose().scale(scale1, scale1, scale1);
            pGuiGraphics.drawString(this.font, entry.button.getMessage(), 0, 0, color);

            pGuiGraphics.pose().translate(0, 10, 0);
            pGuiGraphics.pose().scale(scale2, scale2, scale2);

            {

                int y$ = 0;
                for (FormattedCharSequence cs : list) {
                    pGuiGraphics.drawString(this.font, cs, 0, y$, 0xD4D4B4);
                    y$ += 8;
                }
            }

            pGuiGraphics.pose().popPose();
        }
    }

    private void drawOtherListsBackground(GuiGraphics pGuiGraphics) {
        {
            int x0 = otherListsMinX;
            int x1 = otherListsMaxX;

            int y0 = otherListsMinY;
            int y1 = otherListsMaxY;

            pGuiGraphics.fill(x0, y0, x1 + 5, y1, 0x77000000);
            pGuiGraphics.renderOutline(x0, y0, x1 - x0 + 5, y1 - y0, 0x44EBD798);
        }

        {
            int thisWidth2 = this.width / 4 - 10;
            int x02 = 6;
            int x12 = x02 + thisWidth2;

            int y02 = this.optionsList.getY() - 1;
            int y12 = maxY - 25 + 1;

            pGuiGraphics.fill(x02, y02, x12, y12, 0x55000000);
            pGuiGraphics.renderOutline(x02, y02, x12 - x02, y12 - y02, 0x44EBD798);
        }
    }

    private float characterBoxZoom = 0;
    private float targetZoom = 0;

    private void renderCharacter(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        characterBoxZoom = Mth.lerp(0.05f, characterBoxZoom, targetZoom);

        WCatEntity entityToRender = new WCatEntity(ModEntities.WCAT.get(), Minecraft.getInstance().level);

        entityToRender.setPlayerBoundUuid(UUID.nameUUIDFromBytes(ModEntities.WCAT.get().toString().getBytes()));
        entityToRender.setShowMorphName(false);

        entityToRender.setOnGeneticalSkin(onGeneticalSkin);
        entityToRender.setGender(1);
        entityToRender.setAge(0);
        entityToRender.setAgeInMoons(12);

        entityToRender.getGeneticsModule().setGenetics(genetics);
        entityToRender.getGeneticsModule().setGeneticalVariants(variants);
        entityToRender.getGeneticsModule().setChimeraGenetics(chimeraGenetics);
        entityToRender.getGeneticsModule().setGeneticalVariantsChimera(chimeraVariants);

        entityToRender.setOnGround(true);

        if (optionsList.getSelected() != null) {
            try {
                presetVariant = Integer.parseInt(optionsList.getSelectedKey());
            } catch (NumberFormatException ignored) {
            }
        }

        entityToRender.setVariant(presetVariant);

        entityToRender.setYRot(0);
        entityToRender.yHeadRot = 0;
        entityToRender.yBodyRot = 0;

        int minX = optionsList.getWidth() + optionsList.getX();
        int maxX = this.width - (this.width / 4) - 5 - 10;

        int xPos0 = minX + 7;
        int xPos1 = maxX - 10;

        int yPos0 = optionsList.getY();
        int yPos1 = maxY + 45;


        int centerBoxX = xPos0 + (xPos1 - xPos0) / 2;
        int centerBoxY = yPos0 + (yPos1 - yPos0) / 2;

        pGuiGraphics.renderOutline(xPos0, yPos0, xPos1 - xPos0, yPos1 - yPos0, 0x44EBD798);
        pGuiGraphics.fill(xPos0, yPos0, xPos1, yPos1, 0x45000000);

        if (!pauseCharacter) {
            staticMouseX = Mth.lerpInt(0.2f, staticMouseX, (int) (pMouseX + 3 - menuX));
            staticMouseY = Mth.lerpInt(0.15f, staticMouseY, pMouseY);
        } else {
            pGuiGraphics.pose().pushPose();
            float scale = 0.6f;

            pGuiGraphics.pose().translate(xPos0 + 2, yPos0 + 2, 800);
            pGuiGraphics.pose().scale(scale, scale, scale);

            Component pauseText = Component.empty()
                    .append(" ⏸ ")
                    .append(Component.translatable("screen.cat.morph_paused"));

            List<FormattedCharSequence> lines = font.split(pauseText, (int) ((xPos1 - xPos0) /scale));

            int pauseY = 0;
            for (FormattedCharSequence line : lines) {
                pGuiGraphics.drawString(this.font, line,
                        0, pauseY, 0x88FFFFFF);
                pauseY += 9;
            }
            pGuiGraphics.pose().popPose();
        }

        pMouseX = staticMouseX;
        pMouseY = staticMouseY;

        {
            pGuiGraphics.pose().pushPose();

            pGuiGraphics.pose().translate(centerBoxX, centerBoxY + 40, 0);

            float scale = 3.5f + characterBoxZoom;

            pGuiGraphics.pose().scale(scale, scale, scale);

            pGuiGraphics.enableScissor((int) (xPos0 + 1 + menuX), yPos0 + 1, (int) (xPos1 - 1 + menuX), yPos1 - 1);
            renderCat(
                    pGuiGraphics,
                    0,
                    5,
                    30,
                    (float) (centerBoxX - pMouseX),
                    (float) ((centerBoxY - 3) - pMouseY),
                    entityToRender
            );
            pGuiGraphics.disableScissor();

            pGuiGraphics.pose().popPose();
        }
    }

    private void handleScreenOffset(float pPartialTick) {
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
    }

    @Override
    public void tick() {
        if (lastAction != null) {
            if (lastAction.time > 0) lastAction.time--;
            if (lastAction.time == 0) lastAction = null;
        }

        super.tick();
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double scrollX, double scrollY) {
        boolean superMs = super.mouseScrolled(pMouseX, pMouseY, scrollX, scrollY);

        if (!superMs) {
            int minX = optionsList.getWidth() + optionsList.getX() + 1;
            int maxX = this.width - (this.width / 4) - 6 - 10;

            int xPos0 = minX + 8;
            int xPos1 = maxX - 11;

            int yPos0 = optionsList.getY();
            int yPos1 = maxY + 45;

            if (pMouseX > xPos0 && pMouseX < xPos1 &&
                    pMouseY > yPos0 && pMouseY < yPos1) {
                targetZoom = Mth.clamp(characterBoxZoom + (float)scrollY*2f, -1, 7.5f);
            }
        }
        return superMs;
    }

    private Action lastAction;

    private static class Action {
        public Action(GuiEventListener button, BaseMorphScreen morphScreen) {
            this(button, morphScreen, Component.translatable("screen.error.click_to_confirm"), false, 100);
        }

        public Action(GuiEventListener button, BaseMorphScreen morphScreen,
                      Component component, boolean error, int time) {
            this.button = button;
            this.time = time - 20;
            morphScreen.displayMessage(component, error, time);
        }
        GuiEventListener button;
        int time;
    }

    private boolean pauseCharacter = false;
    private int staticMouseX = 0;
    private int staticMouseY = 0;
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int minX = optionsList.getWidth() + optionsList.getX() + 1;
        int maxX = this.width - (this.width / 4) - 6 - 10;

        int xPos0 = minX + 8;
        int xPos1 = maxX - 11;

        int yPos0 = optionsList.getY();
        int yPos1 = maxY + 45;


        boolean mouseOverSaveMenu = pMouseX > xPos0 && pMouseX < xPos1 &&
                pMouseY > yPos0 && pMouseY < yPos1;
        if (!saveButton.active) {
            if (!(mouseOverSaveMenu)) {
                return false;
            }
        }

        if (!super.mouseClicked(pMouseX, pMouseY, pButton)) {

            if (mouseOverSaveMenu) {
                pauseCharacter = !pauseCharacter;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return super.isMouseOver(pMouseX, pMouseY);
    }
}
