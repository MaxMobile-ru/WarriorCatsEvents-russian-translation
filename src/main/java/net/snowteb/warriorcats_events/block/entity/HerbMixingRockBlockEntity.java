package net.snowteb.warriorcats_events.block.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import net.snowteb.warriorcats_events.recipes.HerbsRecipe;
import net.snowteb.warriorcats_events.recipes.WCERecipes;
import net.snowteb.warriorcats_events.screen.menus.HerbMixingMenu;
import net.snowteb.warriorcats_events.screen.menus.StoneCleftMenu;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HerbMixingRockBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                Vec3 position = worldPosition.getCenter();

                if (inventory.getStackInSlot(slot).isEmpty()) {
                    level.playSound(
                            null, position.x, position.y, position.z,
                            SoundEvents.MOSS_STEP, SoundSource.BLOCKS,
                            0.4F, (float) (0.7F + 0.3*level.getRandom().nextFloat())
                    );
                } else {
                    level.playSound(
                            null, position.x, position.y, position.z,
                            SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS,
                            0.15F, 1.5F
                    );
                }
            }
        }
    };

    public ItemStackHandler getItemStackHandler() {
        return inventory;
    }

    public HerbMixingRockBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.HERB_MIXING_ROCK.get(), pPos, pBlockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public List<ItemStack> getRenderStacks() {
        List<ItemStack> list = new ArrayList<>();

        for (int i = 0; i < this.inventory.getSlots(); i++) {
            ItemStack stack = this.inventory.getStackInSlot(i);

            list.add(stack);
            if (list.size() >= 5) {
                break;
            }
        }

        return list;
    }

    public void dropInventory() {
        SimpleContainer inventory = new SimpleContainer(this.inventory.getSlots());
        for (int i = 0; i < this.inventory.getSlots(); i++) {
            inventory.setItem(i, this.inventory.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public boolean isNotEmpty() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private ItemStack recipeResult = ItemStack.EMPTY;
    private long lastCheck = 0;

    public ItemStack recipeResult() {
        if (level == null) return ItemStack.EMPTY;

        ItemStackHandler handler = new ItemStackHandler(5);

        List<ItemStack> items = getRenderStacks();

        for (int i = 0; i < items.size(); i++) {
            handler.setStackInSlot(i, items.get(i));
        }

        RecipeWrapper wrapper = new RecipeWrapper(handler);

        return level.getRecipeManager()
                .getAllRecipesFor(WCERecipes.HERBS.get())
                .stream()
                .filter(recipe -> recipe.value().matches(wrapper, level))
                .findFirst()
                .map(recipe -> recipe.value().assemble(wrapper, level.registryAccess()))
                .orElse(ItemStack.EMPTY);
    }

    public ItemStack getCurrentResult() {
        if (level == null) return ItemStack.EMPTY;

        if (level.getGameTime() - lastCheck > 3) {
            recipeResult = recipeResult();
            lastCheck = level.getGameTime();
        }

        return recipeResult;
    }

    public RecipeWrapper wrapper() {
        return new RecipeWrapper(this.getItemStackHandler());
    }

    public Optional<RecipeHolder<HerbsRecipe>> getRecipe(Level pLevel, RecipeWrapper wrapper) {

        Optional<RecipeHolder<HerbsRecipe>> recipe = pLevel.getRecipeManager()
                .getRecipeFor(WCERecipes.HERBS.get(), wrapper, pLevel);

        return recipe;
    }

    public boolean handleMakeRecipe(Level pLevel, BlockPos pPos) {

        RecipeWrapper wrapper = this.wrapper();
        Optional<RecipeHolder<HerbsRecipe>> recipe = getRecipe(pLevel, wrapper);

        if (recipe.isPresent()) {

            ItemStack result = recipe.get().value().assemble(wrapper, pLevel.registryAccess());

            for (int i = 0; i < this.getItemStackHandler().getSlots(); i++) {
                this.getItemStackHandler().extractItem(i, 1, false);
            }

            Vec3 pos = pPos.getCenter();

            ItemEntity itemEntity = new ItemEntity(pLevel, pos.x, pos.y, pos.z, result);
            itemEntity.setPickUpDelay(10);

            pLevel.addFreshEntity(itemEntity);

            return true;
        }
        return false;
    }

    public boolean handleHerbsRecipeCraftingBlockState(Level pLevel, BlockPos pPos, Player pPlayer) {

        boolean recipeSuccess = this.handleMakeRecipe(pLevel, pPos);

        if (recipeSuccess) {
            if (pLevel instanceof ServerLevel sLevel) {
                Vec3 position = pPos.getCenter();
                sLevel.playSound(
                        null, position.x, position.y, position.z,
                        SoundEvents.MOSS_STEP, SoundSource.BLOCKS,
                        0.6F, (float) (0.7F + 0.3 * sLevel.getRandom().nextFloat())
                );
                sLevel.playSound(
                        null, position.x, position.y, position.z,
                        SoundEvents.SLIME_JUMP_SMALL, SoundSource.BLOCKS,
                        0.6F, (float) (1.2F + 0.3 * sLevel.getRandom().nextFloat())
                );

                sLevel.sendParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        position.x, position.y + 0.4, position.z,
                        10, 0.1, 0.3, 0.1, 0.005);

            }

            if (level != null) {
                Optional<RecipeHolder<HerbsRecipe>> match = this.getRecipe(level, wrapper());
                if (match.isEmpty()) pPlayer.closeContainer();
            }
            return true;
        } else {
            pPlayer.closeContainer();
            pPlayer.displayClientMessage(Component.translatable("blockinteraction.craftingrock.not_recipe").withStyle(ChatFormatting.GRAY), true);
            return false;
        }

    }


    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new HerbMixingMenu(containerId, playerInventory, this, new SimpleContainerData(0));
    }
}
