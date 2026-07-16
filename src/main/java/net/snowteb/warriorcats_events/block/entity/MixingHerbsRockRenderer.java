package net.snowteb.warriorcats_events.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.snowteb.warriorcats_events.block.custom.StoneCraftingTable;

import java.util.List;

public class MixingHerbsRockRenderer implements BlockEntityRenderer<HerbMixingRockBlockEntity> {

    public MixingHerbsRockRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(HerbMixingRockBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        List<ItemStack> items = pBlockEntity.getRenderStacks();

        ItemStack item1 = !items.isEmpty() ? items.get(0) : ItemStack.EMPTY;
        ItemStack item2 = items.size() > 1 ? items.get(1) : ItemStack.EMPTY;
        ItemStack item3 = items.size() > 2 ? items.get(2) : ItemStack.EMPTY;
        ItemStack item4 = items.size() > 3 ? items.get(3) : ItemStack.EMPTY;
        ItemStack item5 = items.size() > 4 ? items.get(4) : ItemStack.EMPTY;

        BlockState state = pBlockEntity.getBlockState();
        Direction facing = state.getValue(StoneCraftingTable.FACING);

        pPoseStack.pushPose();

        pPoseStack.translate(0.5D, 0.0D, 0.5D);
        switch (facing) {
            case NORTH -> pPoseStack.mulPose(Axis.YP.rotationDegrees(0));
            case SOUTH -> pPoseStack.mulPose(Axis.YP.rotationDegrees(180));
            case WEST -> pPoseStack.mulPose(Axis.YP.rotationDegrees(90));
            case EAST -> pPoseStack.mulPose(Axis.YP.rotationDegrees(-90));
        }


        pPoseStack.translate(0.0D, 0.29D, 0.0D);
        float horizontalScale = 0.13f;
        float vScale = horizontalScale*2.0f;
        pPoseStack.scale(horizontalScale, vScale, horizontalScale);


        pPoseStack.mulPose(Axis.XP.rotationDegrees(90f));

        wrapUnwrapPoseStack(pPoseStack, 0.05D,0.0D,-0.11D);

        itemRenderer.renderStatic(item1, ItemDisplayContext.FIXED, getLightLevel(pBlockEntity.getLevel(),
                        pBlockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, pPoseStack,
                pBuffer, pBlockEntity.getLevel(), 1);

        wrapUnwrapPoseStack(pPoseStack, 0.05D, 0.00D, 0.10D);

        itemRenderer.renderStatic(item2, ItemDisplayContext.FIXED, getLightLevel(pBlockEntity.getLevel(),
                        pBlockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, pPoseStack,
                pBuffer, pBlockEntity.getLevel(), 1);

        wrapUnwrapPoseStack(pPoseStack, -0.08D, 0.00D, 0.10D);

        itemRenderer.renderStatic(item3, ItemDisplayContext.FIXED, getLightLevel(pBlockEntity.getLevel(),
                        pBlockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, pPoseStack,
                pBuffer, pBlockEntity.getLevel(), 1);

        wrapUnwrapPoseStack(pPoseStack, -0.11D, 0.00D, -0.04D);

        itemRenderer.renderStatic(item4, ItemDisplayContext.FIXED, getLightLevel(pBlockEntity.getLevel(),
                        pBlockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, pPoseStack,
                pBuffer, pBlockEntity.getLevel(), 1);

        wrapUnwrapPoseStack(pPoseStack, 0.045D, 0.00D, -0.15D);

        itemRenderer.renderStatic(item5, ItemDisplayContext.FIXED, getLightLevel(pBlockEntity.getLevel(),
                        pBlockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, pPoseStack,
                pBuffer, pBlockEntity.getLevel(), 1);

        pPoseStack.popPose();



        ItemStack recipeResult = pBlockEntity.getCurrentResult();
        if (!recipeResult.isEmpty()) {

            pPoseStack.pushPose();

            pPoseStack.translate(0.5D, 0.0D, 0.5D);

            float time = (pBlockEntity.getLevel().getGameTime());

            y = (float) Math.sin(time * 0.1f) * 0.04f;

            float floating = Mth.lerp(pPartialTick, y0, y);

            pPoseStack.translate(0.0D, 0.6D + floating, 0.0D);
            pPoseStack.scale(0.35F, 0.35F, 0.35F);
            y0 = floating;

            pPoseStack.mulPose(Axis.YP.rotationDegrees(90f));

            rot = time;
            float rotRender = Mth.lerp(pPartialTick, rot0, rot);

            pPoseStack.mulPose(Axis.YP.rotationDegrees(rotRender));
            rot0 = rotRender;

            int light = getLightLevel(pBlockEntity.getLevel(), pBlockEntity.getBlockPos());

            itemRenderer.renderStatic(recipeResult, ItemDisplayContext.FIXED, light,
                    OverlayTexture.NO_OVERLAY, pPoseStack,
                    pBuffer,
                    pBlockEntity.getLevel(), 1);


            pPoseStack.popPose();
        }
    }

    float y0;
    float y;
    float rot;
    float rot0;

    private int getLightLevel(Level world, BlockPos pos) {
        int blockLight = world.getBrightness(LightLayer.BLOCK, pos) + 8;
        int skyLight = world.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }

    private void wrapUnwrapPoseStack(PoseStack pPoseStack, double x, double y, double z) {
        //UNWRAP
        pPoseStack.mulPose(Axis.XP.rotationDegrees(-90f));
        pPoseStack.scale(1/0.10F, 1/0.3F, 1/0.10F);

        pPoseStack.translate(x, y, z);

        pPoseStack.scale(0.10F, 0.3F, 0.10F);
        pPoseStack.mulPose(Axis.XP.rotationDegrees(90f));
        //WRAP
    }
}
