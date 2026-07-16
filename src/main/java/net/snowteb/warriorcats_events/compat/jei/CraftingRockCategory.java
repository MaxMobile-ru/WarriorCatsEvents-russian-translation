package net.snowteb.warriorcats_events.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.recipes.HerbsRecipe;
import org.jetbrains.annotations.Nullable;

public class CraftingRockCategory implements IRecipeCategory<HerbsRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "crafting_rock");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/crafting_rock_jei.png");

    public static final RecipeType<HerbsRecipe> HERBS_RECIPE_TYPE = new RecipeType<>(UID, HerbsRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;


    public CraftingRockCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 100, 90);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.STONE_CRAFTING_TABLE.get()));
    }

    @Override
    public RecipeType<HerbsRecipe> getRecipeType() {
        return HERBS_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.warriorcats_events.stone_crafting_table");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, HerbsRecipe herbsRecipe, IFocusGroup iFocusGroup) {

        int centerX = 90/2;
        int centerY = 90/2;

        int x = centerX - 13;
        int y = 1;
        int spacing = 18;

        int[][] positions = new int[][] {
                {x, y + spacing*3 - 1},
                {x - spacing - 1, y + spacing*3 - 1},
                {4, y + spacing*2 - 3},
                {x - spacing/2, y + spacing - 5},
                {x + spacing/2 - 1, y + spacing*2 - 3}
        };

        for (int i = 0; i < herbsRecipe.getIngredients().size(); i++) {
            iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, positions[i][0], positions[i][1])
                    .addIngredients(herbsRecipe.getIngredients().get(i));
        }

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, centerX + spacing*2 - 1, y + spacing*2 - 3)
                .addItemStack(herbsRecipe.getResultItem(null));

    }
}
