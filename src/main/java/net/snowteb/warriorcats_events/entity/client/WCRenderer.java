package net.snowteb.warriorcats_events.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.client.EntityChatBubbleManager;
import net.snowteb.warriorcats_events.entity.custom.WCatEntity;
import net.snowteb.warriorcats_events.zconfig.WCEClientConfig;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tocraft.walkers.api.PlayerShape;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WCRenderer extends GeoEntityRenderer<WCatEntity> {

    private static final Map<String, ResourceLocation> TEXTURE_CACHE = new HashMap<>();

    private static final ResourceLocation PLAYER_MORPH_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/player_morph_icon.png");

    private static final ResourceLocation PLAYER_TEXT_BUBBLE =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/player_text_bubble.png");

    private static final ResourceLocation BIG_PLAYER_TEXT_BUBBLE =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/hud/big_player_text_bubble.png");

    public WCRenderer(EntityRendererProvider.Context context) {
        super(context, new WCModel());
        this.addRenderLayer(new WCHeldItemLayer(this));
        this.addRenderLayer(new WCAccesoriesLayer(this, context));
        this.shadowRadius = 0.4F;
        this.shadowStrength = 0.5F;
    }

    @Override
    public ResourceLocation getTextureLocation(WCatEntity cat) {


        if (!cat.isOnGeneticalSkin()) {
            int variant = cat.getVariant();
            return WCModel.TEXTURES[Math.max(0, Math.min(variant, WCModel.TEXTURES.length - 1))];
        }

        String[] layers = cat.getTextureLayersPaths();

        String key = cat.getCatTextureKey();

        ResourceLocation texture = TEXTURE_CACHE.get(key);

        if (texture == null) {
            LayerTexture skin = new LayerTexture(layers);
            texture = ResourceLocation.parse(key);
            Minecraft.getInstance().getTextureManager().register(texture, skin);
            TEXTURE_CACHE.put(key, texture);
        }

        return texture;
    }


    /**
     * Depending on the variant, set a different visual size.
     */
    @Override
    public void preRender(PoseStack poseStack, WCatEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {


        float scale = this.getVisualScale(animatable);

        poseStack.scale(scale, scale, scale);
        if (animatable.hasCustomName() && animatable.getCustomName().getString().contains("squished")) {
            poseStack.scale(scale, 0.1f, scale);
            this.shadowRadius = 0.0F;
        } else {
            this.shadowRadius = 0.4f;
            this.shadowStrength = 0.5F;
        }
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void render(WCatEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

        if (entity.getPlayerBoundUuid().equals(ClanData.EMPTY_UUID) && !entity.isAnImage()) {
            float ageMoons = entity.getAgeInMoons();
            float percentage = ageMoons / 12.0F;
            float scale = (float) (0.4 + (percentage * 0.6));
            poseStack.scale(scale, scale, scale);
        } else {
            if (entity.isBaby()) {
                poseStack.scale(0.4f, 0.4f, 0.4f);
            }
            if (entity.isAppScale() && entity.isBaby()) {
                poseStack.scale(1.75f, 1.75f, 1.75f);
            }
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);


        if (entity.shouldShowMorphName()) {
            if (Minecraft.getInstance().player != null) {
                if (!entity.getPlayerBoundUuid().equals(ClanData.EMPTY_UUID)) {

                    UUID boundUuid =  entity.getPlayerBoundUuid();

                    boolean canShowThisName = true;
                    boolean canShowThisMessage = true;

                    if (entity == PlayerShape.getCurrentShape(Minecraft.getInstance().player)) {
                        if (!WCEClientConfig.CLIENT.OWN_CHAT_BUBBLES.get()) {
                            canShowThisMessage = false;
                        }
                        if (!WCEClientConfig.CLIENT.OWN_MORPH_NAME.get() || Minecraft.getInstance().options.hideGui) {
                            canShowThisName = false;
                        }
                    }


                    poseStack.pushPose();

                    float size = 16f;
                    int alpha = 255;

                    int color = 0xFFFFFFFF;
                    float yOffset = 0;

                    if (entity.isDiscrete()) {
                        alpha = 50;
                        color = 0x33FFFFFF;
                        yOffset = 0.07f;
                    }

                    poseStack.translate(0.0D, 1.0D - yOffset + getVisualScale(entity)*0.5, 0.0D);
                    poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

                    poseStack.scale(-0.010F, -0.010F, 1F);

                    VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityTranslucent(PLAYER_MORPH_TEXTURE));
                    Matrix4f matrix = poseStack.last().pose();

                    int redRGB = 255;
                    int greenRGB = 255;
                    int blueRGB = 255;

                    if (canShowThisName) {
                        buffer.vertex(matrix, -size, -size, 0)
                                .color(redRGB, greenRGB, blueRGB, alpha)
                                .uv(0, 0)
                                .overlayCoords(OverlayTexture.NO_OVERLAY)
                                .uv2(packedLight)
                                .normal(0, 1, 0)
                                .endVertex();

                        buffer.vertex(matrix, -size, size, 0)
                                .color(redRGB, greenRGB, blueRGB, alpha)
                                .uv(0, 1)
                                .overlayCoords(OverlayTexture.NO_OVERLAY)
                                .uv2(packedLight)
                                .normal(0, 1, 0)
                                .endVertex();

                        buffer.vertex(matrix, size, size, 0)
                                .color(redRGB, greenRGB, blueRGB, alpha)
                                .uv(1, 1)
                                .overlayCoords(OverlayTexture.NO_OVERLAY)
                                .uv2(packedLight)
                                .normal(0, 1, 0)
                                .endVertex();

                        buffer.vertex(matrix, size, -size, 0)
                                .color(redRGB, greenRGB, blueRGB, alpha)
                                .uv(1, 0)
                                .overlayCoords(OverlayTexture.NO_OVERLAY)
                                .uv2(packedLight)
                                .normal(0, 1, 0)
                                .endVertex();
                    }

                    String morphName = entity.hasCustomName() ? entity.getCustomName().getString() : "Unnamed cat";
                    poseStack.translate(0.0D, -1.7D, 0.0D);

                    poseStack.scale(2.5f, 2.5f, 1F);

                    float x = -Minecraft.getInstance().font.width(morphName) / 2f;

                    if (canShowThisName) {
                        Minecraft.getInstance().font.drawInBatch(morphName, x, 10, color,
                                false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0x44000000, packedLight);
                    }

                    poseStack.scale(1/2.5f, 1/2.5f, 1);


                    if (WCEClientConfig.CLIENT.MORPH_CHAT_BUBBLES.get()) {
                        EntityChatBubbleManager.ChatBubble bubble = EntityChatBubbleManager.bubbles.get(boundUuid);

                        Player owner = Minecraft.getInstance().level.getPlayerByUUID(boundUuid);

                        if (bubble != null && (owner != null && Minecraft.getInstance().player.distanceTo(owner) < 15)) {

                            Font font = Minecraft.getInstance().font;

                            Component message = bubble.message;
                            message = formatOriginalMessage(message);

                            poseStack.translate(0.0D, -20D, 0.0D);
                            if (!canShowThisName) {
                                poseStack.translate(0.0D, 50D, 0.0D);
                            }

                            poseStack.scale(1.2f, 1.2f, 1.2F);

                            List<FormattedCharSequence> lines = font.split(message, 160);


                            int textHeight = lines.size() * 9;
                            int bubbleHeight = textHeight + 10 * 2;

                            int bubbleWidth = 170;

                            int theSize = 0;
                            for (FormattedCharSequence line : lines) {
                                if (font.width(line) > theSize) {
                                    theSize = font.width(line);
                                }
                            }
                            bubbleWidth = Math.max(theSize + 20, 80);
                            if (lines.size() == 1) {
                                bubbleHeight = 28;
                            }

                            VertexConsumer bubbleBuffer = bufferSource.getBuffer(RenderType.entitySmoothCutout(PLAYER_TEXT_BUBBLE));
                            if (lines.size() >= 5) {
                                bubbleBuffer = bufferSource.getBuffer(RenderType.entitySmoothCutout(BIG_PLAYER_TEXT_BUBBLE));
                            }
                            int alphaTextBubble = 255;
                            if (entity.isDiscrete()) {
                                bubbleBuffer = bufferSource.getBuffer(RenderType.entityTranslucent(PLAYER_TEXT_BUBBLE));
                                if (lines.size() >= 5) {
                                    bubbleBuffer = bufferSource.getBuffer(RenderType.entityTranslucent(BIG_PLAYER_TEXT_BUBBLE));
                                }
                                alphaTextBubble = 100;
                                redRGB = 180;
                                greenRGB = 180;
                                blueRGB = 180;
                            }

                            Matrix4f pose = poseStack.last().pose();

                            float left = -bubbleWidth / 2f;
                            float right = bubbleWidth / 2f;

                            float top = -bubbleHeight;
                            float bottom = -1;

                            if (canShowThisMessage) {

                                bubbleBuffer.vertex(pose, left, top, -0.01f)
                                        .color(redRGB, greenRGB, blueRGB, alphaTextBubble)
                                        .uv(0, 0)
                                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                                        .uv2(255)
                                        .normal(0, 1, 0)
                                        .endVertex();

                                bubbleBuffer.vertex(pose, left, bottom + 8, -0.01f)
                                        .color(redRGB, greenRGB, blueRGB, alphaTextBubble)
                                        .uv(0, 1)
                                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                                        .uv2(255)
                                        .normal(0, 1, 0)
                                        .endVertex();

                                bubbleBuffer.vertex(pose, right, bottom + 8, -0.01f)
                                        .color(redRGB, greenRGB, blueRGB, alphaTextBubble)
                                        .uv(1, 1)
                                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                                        .uv2(255)
                                        .normal(0, 1, 0)
                                        .endVertex();

                                bubbleBuffer.vertex(pose, right, top, -0.01f)
                                        .color(redRGB, greenRGB, blueRGB, alphaTextBubble)
                                        .uv(1, 0)
                                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                                        .uv2(255)
                                        .normal(0, 1, 0)
                                        .endVertex();


                                int y = (int) (top + 12);
                                poseStack.translate(0.0D, 0.0D, -0.02D);
                                for (FormattedCharSequence line : lines) {

                                    float x1 = -font.width(line) / 2f;

                                    font.drawInBatch(
                                            line,
                                            x1, y,
                                            color, false,
                                            matrix, bufferSource, Font.DisplayMode.NORMAL,
                                            0x00000000, 255
                                    );

                                    y += 8.2;
                                }


                            }

                        }
                    }

                    poseStack.popPose();


                }
            }
        }

    }

    @Override
    public RenderType getRenderType(WCatEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    protected void renderNameTag(WCatEntity pEntity, Component pDisplayName, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {

        if (Minecraft.getInstance().player != null) {
            if (!WCEClientConfig.CLIENT.OWN_MORPH_NAME.get() && PlayerShape.getCurrentShape(Minecraft.getInstance().player) == pEntity) {
                return;
            }
        }

        if (!pEntity.getPlayerBoundUuid().equals(ClanData.EMPTY_UUID)) {
            return;
        }

        super.renderNameTag(pEntity, pDisplayName, pPoseStack, pBuffer, pPackedLight);


    }


    private float getVisualScale(WCatEntity pEntity) {
        float scale;
        scale = switch (pEntity.getVariant()) {
            case 0 -> 0.8f;
            case 1 -> 1.0f;
            case 2 -> 1.0f;
            case 3 -> 0.9f;
            case 4 -> 0.8f;
            case 5 -> 1.0f;
            case 6 -> 1.0f;
            case 7 -> 1.0f;
            case 8 -> 0.8f;
            case 9 -> 1.0f;
            case 10 -> 1.0f;
            case 11 -> 0.9f;
            case 12 -> 0.9f; //chestnutpatch
            case 13 -> 1.0f; //ratstar
            case 14 -> 0.8f; //twitchstream
            case 15 -> 1.0f; //blazepit
            case 16 -> 1.0f; //bengalpelt
            case 17 -> 1.0f; //sparrowstar
            case 18 -> 0.8f; //foxeater
            case 19 -> 1.0f; //willowsong
            case 20 -> 0.9f; //13
            case 21 -> 0.8f; //14
            case 22 -> 1.0f; //15
            case 23 -> 0.9f; //16
            case 24 -> 0.9f; //17
            case 25 -> 1.0f; //18
            case 26 -> 0.8f; //19
            case 27 -> 0.8f; //20
            case 28 -> 1.0f; //21
            case 29 -> 1.0f; //22

            case 30 -> 0.9f;
            case 31 -> 0.8f;
            case 32 -> 1.0f;
            case 33 -> 0.9f;
            case 34 -> 0.8f;
            case 35 -> 1.0f;
            case 36 -> 0.9f;
            case 37 -> 0.8f;
            case 38 -> 0.9f;
            case 39 -> 0.8f;
            case 40 -> 1.0f;
            case 41 -> 0.9f;
            case 42 -> 1.0f;
            case 43 -> 0.9f;
            case 44 -> 0.8f;
            case 45 -> 0.9f;
            case 46 -> 0.8f;
            case 47 -> 1.0f;
            case 48 -> 0.8f;
            case 49 -> 0.9f;
            case 50 -> 0.8f;
            case 51 -> 1.0f;
            case 52 -> 0.9f;
            default -> 0.8f;
        };

        if (pEntity.getSize() > 0.4) {
            scale = pEntity.getSize();
        }

        return scale;
    }


    public static Component formatOriginalMessage(Component original) {

        String text = original.getString();

        text = text.replace(":sob:", "\uD83D\uDE2D");
        text = text.replace(":cry:", "\uD83D\uDE2D");
        text = text.replace(":sparkles:", "✨");
        text = text.replace(":sparkle:", "✨");
        text = text.replace(":cat:", "\uD83D\uDC08");
        text = text.replace(":cat2:", "\uD83D\uDC31");
        text = text.replace(":plead:", "\uD83E\uDD79");
        text = text.replace(":pleading:", "\uD83E\uDD79");
        text = text.replace(":wilted_rose:", "\uD83E\uDD40");
        text = text.replace(":wil_rose:", "\uD83E\uDD40");
        text = text.replace(":heart:", "❤");
        text = text.replace(":fire:", "\uD83D\uDD25");
        text = text.replace(":flame:", "\uD83D\uDD25");
        text = text.replace(":smile:", "\uD83D\uDE3A");


        MutableComponent result = Component.empty();

        Matcher matcher = Pattern.compile("\\*(.*?)\\*").matcher(text);

        int lastEnd = 0;

        while (matcher.find()) {

            if (matcher.start() > lastEnd) {
                result.append(Component.literal(text.substring(lastEnd, matcher.start())));
            }

            String part = matcher.group(1);

            result.append(Component.literal(part).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));

            lastEnd = matcher.end();
        }

        if (lastEnd < text.length()) {
            result.append(Component.literal(text.substring(lastEnd)));
        }

        return result;
    }

    @Override
    public boolean isShaking(WCatEntity cat) {
        if (!cat.getPlayerBoundUuid().equals(ClanData.EMPTY_UUID)) {
            Player player = cat.level().getPlayerByUUID(cat.getPlayerBoundUuid());
            if (player != null) return (player.isFullyFrozen());
        }
        return super.isShaking(animatable);
    }
}



