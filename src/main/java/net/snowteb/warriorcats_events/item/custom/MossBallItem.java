package net.snowteb.warriorcats_events.item.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.snowteb.warriorcats_events.datacomponents.ModDataComponents;
import net.snowteb.warriorcats_events.entity.custom.MossBallEntity;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import tocraft.walkers.api.PlayerShape;

import java.util.List;

public class MossBallItem extends Item {

    public MossBallItem(Properties pProperties) {
        super(pProperties.stacksTo(16));
    }

    public static int getWaterLevel(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.WATER_LEVEL, 0);
    }

    public static void setWaterLevel(ItemStack stack, int value) {
        stack.set(ModDataComponents.WATER_LEVEL, value);
    }

    public static int getHoneyLevel(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.HONEY_LEVEL, 0);
    }

    public static void setHoneyLevel(ItemStack stack, int value) {
        stack.set(ModDataComponents.HONEY_LEVEL, value);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        setWaterLevel(stack, 0);
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {

        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);

        BlockHitResult hit = Item.getPlayerPOVHitResult(pLevel, pPlayer, ClipContext.Fluid.SOURCE_ONLY);

        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = hit.getBlockPos();
            BlockState state = pLevel.getBlockState(pos);

            if (state.getFluidState().isSource() && getHoneyLevel(itemstack) <= 0) {

                if (!pLevel.isClientSide) {
                    if (PlayerShape.getCurrentShape(pPlayer) instanceof Animal) {
                        ItemStack single;
                        boolean isMany = false;
                        if (itemstack.getCount() > 1) {
                            isMany = true;
                            single = itemstack.split(1);
                        } else {
                            single = itemstack;
                        }

                        if (pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof MossBallItem item) {
                            if (getWaterLevel(single) >= 10) {
                                pPlayer.displayClientMessage(Component.translatable("item.warriorcats_events.moss_ball.full_of_water").withStyle(ChatFormatting.GRAY), true);
                                return InteractionResultHolder.fail(single);
                            }
                        }

                        if (pPlayer instanceof ServerPlayer sPlayer) {

                            setWaterLevel(single,getWaterLevel(single) + 1);

                            if (isMany) {
                                if (!sPlayer.addItem(single)) {
                                    sPlayer.drop(single, false);
                                }
                            }


                            pLevel.playSound(
                                    null,
                                    sPlayer.blockPosition(),
                                    SoundEvents.CAT_EAT,
                                    SoundSource.PLAYERS,
                                    0.8F,
                                    1.0F
                            );

                            ((ServerLevel) pLevel).sendParticles(
                                    ParticleTypes.SPLASH,
                                    sPlayer.getX(),
                                    sPlayer.getY() + 0.5,
                                    sPlayer.getZ(),
                                    30,
                                    0.4, 0.2, 0.4,
                                    0.02
                            );
                        }


                    }
                }

                return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
            }
        }


        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (stack.getItem() instanceof MossBallItem) {
            pPlayer.startUsingItem(pUsedHand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);

    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        if (!(pLivingEntity instanceof Player player)) return;
        float throwStrength = this.getThrowStrength(this.getUseDuration(pStack, pLivingEntity) - pTimeCharged);
        if (throwStrength < 0.25f) return;
        if (!(player.level() instanceof ServerLevel sLevel)) return;


        sLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (sLevel.getRandom().nextFloat() * 0.4F + 0.8F));
        ItemStack ball = pStack.copy();
        ball.setCount(1);
        MossBallEntity mossBall = new MossBallEntity(sLevel, player);
        mossBall.setStack(ball);
        mossBall.setWater(getWaterLevel(ball));
        mossBall.setHoney(getHoneyLevel(ball));
        mossBall.shootFromRotation(
                player, player.getXRot(), player.getYRot(), 0.0F, throwStrength * 1.2f, 0.25f
        );
        player.swing(InteractionHand.MAIN_HAND);
        sLevel.addFreshEntity(mossBall);

        if (player instanceof ServerPlayer sPlayer) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape instanceof WCatEntity catShape) {

                catShape.setAnimIndex(4);

                PlayerShape.updateShapes(sPlayer, catShape);

                player.getPersistentData().putInt("wcat_animation_playing", sPlayer.server.getTickCount() + 10);
            }
        }

        if (!player.getAbilities().instabuild) {
            pStack.shrink(1);
        }
    }

    private float getThrowStrength(int ticksUsed) {
        float seconds = ticksUsed / 20f;
        return Math.min(seconds, 1.0f);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (getWaterLevel(stack) > 0) {
            tooltipComponents.add(Component.translatable("item.warriorcats_events.moss_ball.water_level", getWaterLevel(stack)).withStyle(ChatFormatting.AQUA));
        }
        if (getHoneyLevel(stack) > 0) {
            tooltipComponents.add(Component.translatable("item.warriorcats_events.moss_ball.honey_level", getHoneyLevel(stack)).withStyle(ChatFormatting.GOLD));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void translateArm(PoseStack matrices, HumanoidArm arm, ItemStack stack, float tickDelta) {
        boolean rightArm = arm == HumanoidArm.RIGHT;
        int sideOffset = rightArm ? 1 : -1;
        matrices.translate((float)sideOffset * -0.25f, 0.0f, 0.1f);
        matrices.mulPose(Axis.YP.rotationDegrees((float)sideOffset * 35.3f));
        matrices.mulPose(Axis.ZP.rotationDegrees((float)sideOffset * -9.785f));
        if (Minecraft.getInstance().player == null) return;
        float timeHeld = (float)stack.getUseDuration(Minecraft.getInstance().player) - ((float) Minecraft.getInstance().player.getUseItemRemainingTicks() - tickDelta + 1.0f);
        float raiseHeight = timeHeld / 10.0f;
        if (raiseHeight > 1.0f) {
            raiseHeight = 1.0f;
        }
        if (raiseHeight > 0.1f) {
            float g = Mth.sin((timeHeld - 0.1f) * 1.3f);
            float h = raiseHeight - 0.1f;
            float j = g * h;
            matrices.translate(j * 0.0f, j * 0.004f, j * 0.0f);
        }
        matrices.translate(0.0f, 0.0f, raiseHeight * 0.05f);
        matrices.scale(1.0f, 1.0f, 1.0f + raiseHeight * 0.05f);
        matrices.mulPose(Axis.YN.rotationDegrees((float)sideOffset * 45.0f));
    }
}
