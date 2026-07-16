package net.snowteb.warriorcats_events.compat.jei;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.block.ModBlocks;
import net.snowteb.warriorcats_events.recipes.HerbsRecipe;
import net.snowteb.warriorcats_events.recipes.WCERecipes;
import net.snowteb.warriorcats_events.screen.screens.HerbMixingRockScreen;

import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID,"jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CraftingRockCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {

        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        List<HerbsRecipe> herbsRecipes = recipeManager.getAllRecipesFor(WCERecipes.HERBS.get());
        registration.addRecipes(CraftingRockCategory.HERBS_RECIPE_TYPE, herbsRecipes);
        registration.addItemStackInfo(
                herbsRecipes.stream()
                        .map(recipe -> recipe.getResultItem(Minecraft.getInstance().level.registryAccess()))
                        .toList(),
                Component.literal("")
        );

    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        IModPlugin.super.registerGuiHandlers(registration);
        registration.addGuiContainerHandler(HerbMixingRockScreen.class, new HerbMixingScreenHandler());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.STONE_CRAFTING_TABLE.get()),
                CraftingRockCategory.HERBS_RECIPE_TYPE
        );
    }
}
