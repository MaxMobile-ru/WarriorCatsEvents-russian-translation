package net.snowteb.warriorcats_events.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.snowteb.warriorcats_events.clan.ClanData;
import org.joml.Matrix4f;

public class MossBedRenderer implements BlockEntityRenderer<NestBlockEntity> {

    public MossBedRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(NestBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        if (mc.player == null) return;
        if (!mc.player.isShiftKeyDown()) return;
        if (mc.player.distanceToSqr(pBlockEntity.getBlockPos().getCenter()) > 6*6) return;
        HitResult hit = mc.hitResult;

        if (!(hit instanceof BlockHitResult blockHitResult)) return;
        Vec3 hitPos = blockHitResult.getLocation();
        AABB box = new AABB(pBlockEntity.getBlockPos())
                .inflate(0.6);

        if (!box.contains(hitPos)) return;

        pPoseStack.pushPose();

        pPoseStack.translate(0.5, 1.0, 0.5);

        pPoseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());

        pPoseStack.scale(0.017f, -0.017f, -1);

        Matrix4f matrix = pPoseStack.last().pose();

        Component occupiedText = pBlockEntity.getAssignedUUID().equals(ClanData.EMPTY_UUID) ?
                Component.translatable("blockentity.nestblock.free").withStyle(ChatFormatting.GREEN)
                : Component.translatable("blockentity.nestblock.occupied").withStyle(ChatFormatting.GOLD);
        String text = pBlockEntity.getCatName();

        float x = -font.width(text) / 2f;
        float x2 = -font.width(occupiedText.getString()) / 2f;

        int y = 20;
        if (!text.isEmpty()) {
            font.drawInBatch(text, x, y, 0xFFFFFFFF,
                    false, matrix, pBuffer, Font.DisplayMode.NORMAL, 0x44000000, pPackedLight);
        } else {
            y -=5;
        }
        y += 10;
        font.drawInBatch(occupiedText, x2, y, 0xFFFFFFFF,
                false, matrix, pBuffer, Font.DisplayMode.NORMAL, 0x44000000, pPackedLight);


        pPoseStack.popPose();

    }

}
