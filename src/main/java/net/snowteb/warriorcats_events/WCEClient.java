package net.snowteb.warriorcats_events;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.snowteb.warriorcats_events.diseases.Disease;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import net.snowteb.warriorcats_events.managers.ClimbDataAccessor;
import net.snowteb.warriorcats_events.screen.screens.WCEConfigScreen;
import net.snowteb.warriorcats_events.sound.ModSounds;
import net.snowteb.warriorcats_events.zconfig.WCEServerConfig;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@Mod(value = WarriorCatsEvents.MODID, dist =  Dist.CLIENT)
public class WCEClient {

    public WCEClient(ModContainer container) {
        container.registerExtensionPoint(
                IConfigScreenFactory.class,
                (mc, parent) -> {
                    WCEClient.playLocalSound(
                            ModSounds.MENU_OPEN.get(),
                            SoundSource.NEUTRAL,
                            1.0f,
                            1.3f
                    );
                    return new WCEConfigScreen(parent);
                }
        );
    }

    public static boolean isRenderingEmoteMenu = false;
    public static int emoteOffset = 0;
    public static final KeyMapping EMOTES_HUD_MENU_KEY = new KeyMapping("key.warriorcats_events.emotes", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Y, "key.category.warriorcats_events.key");

    public static final ResourceLocation WCE_TITLE = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/wce_title_hd.png");

    public static final ResourceLocation WCE_LOGO = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/wce_logo.png");

    public static final Music GENERATIONS_BG_MUSIC = new Music(
            ModSounds.GENERATIONS_BG,
            0,
            0,
            true
    );

    public static class EmoteIndexData {
        public static final int MAX_EMOTES = 13;

        private static final ResourceLocation ICON_NONE = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/emotes/none_icon.png");
        private static final ResourceLocation ICON_MORPH = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/emotes/morph_icon.png");
        private static final ResourceLocation ICON_ATTACK = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/emotes/attack_icon.png");
        private static final ResourceLocation ICON_GROOM = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/emotes/groom_icon.png");
        private static final ResourceLocation ICON_LAY = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/emotes/lay_icon.png");
        private static final ResourceLocation ICON_SCRATCH = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/emotes/scratch_icon.png");
        private static final ResourceLocation ICON_STAND = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/emotes/stand_icon.png");
        private static final ResourceLocation ICON_STRETCH = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/emotes/stretch_icon.png");
        private static final ResourceLocation ICON_SIT = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/emotes/sit_icon.png");
        private static final ResourceLocation ICON_LOAF = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/emotes/loaf_icon.png");
        private static final ResourceLocation ICON_BACKFLIP = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/emotes/backflip_icon.png");
        private static final ResourceLocation ICON_SLEEP = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/emotes/sleep_icon.png");
        private static final ResourceLocation ICON_DEAD = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/emotes/dead_icon.png");
        private static final ResourceLocation ICON_ROLL = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/emotes/roll_icon.png");
        private static final ResourceLocation ICON_SCARED = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/emotes/scared_icon.png");
        private static final ResourceLocation ICON_PREMIUM_STAND = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/emotes/twoleg_stand_icon.png");
        private static final ResourceLocation ICON_DROP = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/emotes/drop_icon.png");

        public static ResourceLocation getIcon(int index) {
            return switch (index) {
                case -3 -> ICON_PREMIUM_STAND;
                case -2 -> ICON_BACKFLIP;
                case -1 -> ICON_MORPH;
                case 0 -> ICON_NONE;
                case 1 -> ICON_GROOM;
                case 2 -> ICON_STRETCH;
                case 3 -> ICON_SCRATCH;
                case 4 -> ICON_ATTACK;
                case 5 -> ICON_STAND;
                case 6 -> ICON_LAY;
                case 7 -> ICON_SIT;
                case 8 -> ICON_LOAF;
                case 9 -> ICON_SLEEP;
                case 10 -> ICON_DEAD;
                case 11 -> ICON_ROLL;
                case 12 -> ICON_SCARED;
                case 13 -> ICON_DROP;
                default -> ICON_NONE;
            };
        }

        public static String getText(int index) {
            return switch (index) {
                case -3 -> "Twoleg Stand";
                case -2 -> "Backflip";
                case -1 -> "Morph";
                case 0 -> "";
                case 1 -> "Groom";
                case 2 -> "Stretch";
                case 3 -> "Scratch";
                case 4 -> "Attack";
                case 5 -> "Stand";
                case 6 -> "Lay";
                case 7 -> "Sit";
                case 8 -> "Loaf";
                case 9 -> "Sleep";
                case 10 -> "Play dead";
                case 11 -> "Roll";
                case 12 -> "Scared";
                case 13 -> "Drop";
                default -> "Unnamed";
            };
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static void nextMenuSound() {
        playLocalSound(ModSounds.MENU_ACCEPT.get(), SoundSource.MASTER, 0.2f, 1f);
    }

    @OnlyIn(Dist.CLIENT)
    public static void playLocalSound(SoundEvent sound, SoundSource soundSource, float volume, float pitch) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            mc.level.playLocalSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                    sound, soundSource, volume,pitch, false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void playLocalSound(SoundEvent sound, SoundSource soundSource, float volume, float pitch, Vec3 pos) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            mc.level.playLocalSound(pos.x, pos.y, pos.z,
                    sound, soundSource, volume,pitch, false);
        }
    }


    public static boolean isBeingLatched = false;
    public static int setFreeCounter = 0;

    public static String lookingAtParticle = "";
    public static int newParticleTime = 0;

    public static boolean isRenderingSoundMenu = false;
    public static int soundOffset = 0;
    public static final int MAX_SOUNDS = 7;

    public static class SoundIndexData {

        public static String getText(int index) {
            return switch (index) {
                case 0 -> "";
                case 1 -> "Hiss";
                case 2 -> "Meow";
                case 3 -> "Purr";
                case 4 -> "Purreow";
                case 5 -> "Pitiful Meow";
                case 6 -> "Cry";
                case 7 -> "Dramatic cry";
                default -> "Unnamed";
            };
        }

    }

    public static int climbCooldown = 0;
    public static int displayCannotClimb = 0;
    public static boolean cannotClimbBlink = false;
    public static int cannotClimbBlinkCount = 3;
    public static final int CLIM_COOLDOWN = 60;

    public static int exhaustionLevel = 0;
    public static int MAX_EXHAUSTION_LEVEL = 100;

    public static void setExhaustionLevel(int exhaustionLevel) {
        WCEClient.exhaustionLevel = exhaustionLevel;
    }

    @OnlyIn(Dist.CLIENT)
    public static void climbClientTick() {

        if (climbCooldown > 0) climbCooldown--;

        if (displayCannotClimb > 0) {
            displayCannotClimb--;
        } else {
            cannotClimbBlink = false;
            cannotClimbBlinkCount = 3;
        }

        if (displayCannotClimb > 0) {
            if (displayCannotClimb % 3 == 0) {
                cannotClimbBlink = !cannotClimbBlink;

                if (cannotClimbBlinkCount > 0) cannotClimbBlinkCount--;
            }

        } else {
            cannotClimbBlink = false;
            cannotClimbBlinkCount = 6;
        }

        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer instanceof ClimbDataAccessor data) {
            if (data.wce$isClimbing() && localPlayer.input.jumping) {
                launchFromLookAngle(data, localPlayer);
            }
        }
    }

    private static void launchFromLookAngle(ClimbDataAccessor data, LocalPlayer localPlayer) {
        data.wce$stopClimb();
        Vec3 angle = Minecraft.getInstance().cameraEntity.getLookAngle().reverse();
        float strenght = 0.6f;
        localPlayer.setDeltaMovement(angle.scale(strenght));
        climbCooldown = 15;
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderDiseaseTooltipsDtU(Diseaseable<?> entity, GuiGraphics pGuiGraphics, int x, int y, int pMouseX, int pMouseY) {
        if (!WCEServerConfig.SERVER.DISEASES.get()) return;

        List<Disease<?>> list = entity.getList();
        if (list.isEmpty()) return;

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(x, y, 0);
        float scale = 0.8f;
        pGuiGraphics.pose().scale(scale, scale, scale);

        int y1 = 0;
        for (Disease<?> disease : list) {
            int color = diseaseTextColor(disease);

            Component text = Component.literal(disease.getType().getName().getString()).withStyle(Style.EMPTY.withColor(color));
            renderDiseaseTooltip(pGuiGraphics, text, 0, y1, disease, pMouseX, pMouseY);
            y1 -= 18;
        }
        pGuiGraphics.pose().popPose();

    }

    @OnlyIn(Dist.CLIENT)
    public static void renderDiseaseTooltipsUtD(Diseaseable<?> entity, GuiGraphics pGuiGraphics, int x, int y, int pMouseX, int pMouseY) {
        if (!WCEServerConfig.SERVER.DISEASES.get()) return;

        List<Disease<?>> list = entity.getList();
        if (list.isEmpty()) return;

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(x, y, 0);
        float scale = 0.7f;
        pGuiGraphics.pose().scale(scale, scale, scale);

        int y1 = 0;
        for (Disease<?> disease : list) {
            int color = diseaseTextColor(disease);

            Component text = Component.literal(disease.getType().getName().getString()).withStyle(Style.EMPTY.withColor(color));

            int localMouseX = (int)((pMouseX - x) / scale);
            int localMouseY = (int)((pMouseY - y) / scale);

            renderDiseaseTooltip(pGuiGraphics, text, 0, y1, disease, localMouseX, localMouseY);
            y1 += 18;
        }
        pGuiGraphics.pose().popPose();

    }

    private static void renderDiseaseTooltip(GuiGraphics pGuiGraphics, Component pText, int x, int y, Disease<?> disease, int pMouseX, int pMouseY) {
        Component finalText = Component.literal("   ")
                .append(pText.copy())
                .append(Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal("҉ " + disease.getLevel() + "/" + disease.getType().getMaxLevel()).withStyle(ChatFormatting.DARK_RED))
                .append(Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal("➕ " + disease.getHealedLevel() + "/" + disease.getType().getMaxHealLevel()).withStyle(ChatFormatting.DARK_GREEN))
                ;

        if (!disease.shouldShowUnhealed()) {
            finalText = finalText.copy()
                    .append(Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY))
                    .append(Component.literal("⏱").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD));
        } else {
            finalText = finalText.copy()
                    .append(Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY));

            if (disease.shouldShowUnhealed()){
                for (int i = 0; i < disease.getCures().size(); i++) {
                    finalText = finalText.copy().append("   ");
                }
            } else {
                finalText = finalText.copy().append("   ");
            }
        }

        pGuiGraphics.renderTooltip(Minecraft.getInstance().font, finalText, x, y);

        int iconSize = 10;
        int offset = Minecraft.getInstance().font.width(finalText);

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0,0, 500);

        boolean isHovered = pMouseX >= x+iconSize && pMouseX <= x+offset+iconSize+5
                && pMouseY >= y-iconSize-5 && pMouseY <= y-2;
        if (isHovered) {
            renderDiseaseDescriptionTooltip(pGuiGraphics, disease, pMouseX, pMouseY);
        }
        pGuiGraphics.pose().popPose();

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0, 0, 401);
        pGuiGraphics.blit(disease.getType().getIcon(),
                x + iconSize + 1,y - iconSize - 3,
                0,0,
                iconSize, iconSize,
                iconSize, iconSize);


        if (disease.shouldShowUnhealed()) {
            int x2 = 0;

            float scale = 0.70f;
            pGuiGraphics.pose().translate(offset, y - iconSize - 5, 0);
            pGuiGraphics.pose().scale(scale, scale, scale);

            for (ItemStack stack : disease.getCures()) {
                pGuiGraphics.renderItem(stack, x2, 1);
                x2 -= iconSize + 8;
            }

        }


        pGuiGraphics.pose().popPose();
    }

    private static void renderDiseaseDescriptionTooltip(GuiGraphics pGuiGraphics, Disease<?> disease, int x, int y) {

        String healedLevel = "Healed: " + disease.getHealedLevel() + "/" + disease.getType().getMaxHealLevel();
        String severenessLevel = "Stage: " + disease.getLevel() + "/" + disease.getType().getMaxLevel();

        Component description = Component.empty()
                .append(disease.getType().getName().copy()).withStyle(Style.EMPTY.withColor(diseaseTextColor(disease)))
                .append(Component.literal(" | ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(severenessLevel).withStyle(ChatFormatting.RED))
                .append(Component.literal(" | ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(healedLevel).withStyle(ChatFormatting.GREEN))
                ;



        List<FormattedCharSequence> lines = new ArrayList<>();
        lines.add(description.getVisualOrderText());
        lines.add(Component.literal("").getVisualOrderText());

        lines.addAll(Minecraft.getInstance().font.split(
                        disease.getType()
                                .getDescription()
                                .copy()
                                .withStyle(ChatFormatting.GRAY),
                250
        ));

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(x, y + 10, 0);
        float scale = 0.70f;
        pGuiGraphics.pose().scale(scale, scale, scale);
        pGuiGraphics.renderTooltip(Minecraft.getInstance().font,
                lines,
                0, 0
        );
        pGuiGraphics.pose().popPose();

    }

    public static int diseaseTextColor(Disease<?> disease) {

        float progress = (float)(disease.getLevel() - 1) / (disease.getType().getMaxLevel() - 1);

        int red = 255;
        float curve = progress * progress;

        int green = (int)(255 * (1f - curve));
        int blue = (int)(255 * (1f - progress * 4f));

        blue = Math.max(0, blue);
        green = Math.max(0, green);

        return (red << 16) | (green << 8) | blue;
    }

}
