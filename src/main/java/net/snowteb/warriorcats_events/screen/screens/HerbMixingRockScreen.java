package net.snowteb.warriorcats_events.screen.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.block.custom.HerbMixingRockBlock;
import net.snowteb.warriorcats_events.block.entity.HerbMixingRockBlockEntity;
import net.snowteb.warriorcats_events.item.ModItems;
import net.snowteb.warriorcats_events.recipes.HerbsRecipe;
import net.snowteb.warriorcats_events.recipes.WCERecipes;
import net.snowteb.warriorcats_events.screen.menus.HerbMixingMenu;
import net.snowteb.warriorcats_events.screen.menus.StoneCleftMenu;
import net.snowteb.warriorcats_events.screen.screens.createmorph.FancySimpleButton;
import net.snowteb.warriorcats_events.screen.screens.createmorph.FancyStringWidget;
import net.snowteb.warriorcats_events.screen.screens.createmorph.SubRenderable;
import net.snowteb.warriorcats_events.screen.widgets.FancyHerbsRecipeScrollList;

import java.util.List;
import java.util.Optional;

public class HerbMixingRockScreen extends AbstractContainerScreen<HerbMixingMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "textures/gui/herb_mixing_rock.png");

    private final List<HerbsRecipe> recipes;

    public HerbMixingRockScreen(HerbMixingMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        recipes = recipeManager.getAllRecipesFor(WCERecipes.HERBS.get());

    }

    private Button showRecipes;
    private FancyHerbsRecipeScrollList recipeList;

    @Override
    protected void init() {
        super.init();
        this.imageWidth = 176;
        this.imageHeight = 200;
        this.inventoryLabelY = 90;
        this.titleLabelY = -2;


        int centerX = this.width / 2;
        int centerY = this.height / 2;

        showRecipes = Button.builder(Component.empty(), btn -> {
                    this.addRenderableWidget(recipeList);

                    FancyStringWidget text = new FancyStringWidget(Component.translatable("screen.herb_mixing.recipes"),
                            recipeList.getLeft(), recipeList.getRight(),
                            recipeList.getTop() - 15, 0xFFF2DCAE,
                            1f, true, 170);

                    this.addRenderableWidget(text);

                    this.addRenderableWidget(Button.builder(Component.literal("X"), b -> {
                                this.removeWidget(recipeList);
                                this.removeWidget(b);
                                this.removeWidget(text);
                                showRecipes.visible = true;
                            }).bounds(recipeList.getRight() - 12, recipeList.getTop() - 20, 12, 12)
                            .build());

                    btn.visible = false;
                }).bounds(centerX - 102, centerY - 50, 22, 21)
                .build();

        FancySimpleButton craftRecipe = new FancySimpleButton(60, 12,
                centerX - 50, centerY - 20,
                Component.translatable("screen.herb_mixing.prepare_recipe"), b -> {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
            }
        }, 0.8f);

        this.recipeList = new FancyHerbsRecipeScrollList(this.getMinecraft(),
                centerX - 95 - 10, 200,
                centerY - 75, centerY + 95);
        this.recipeList.setRenderBackground(false);
        this.recipeList.setRenderTopAndBottom(false);
        this.recipeList.setLeftPos(centerX - 95 - recipeList.getWidth());
        this.recipeList.addRecipes(this.recipes);

        this.addRenderableWidget(showRecipes);
        this.addRenderableWidget(craftRecipe);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (this.renderables.contains(recipeList)) {
            guiGraphics.enableScissor(x, y, x + imageWidth, y + imageHeight);
            guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 176, 200);
            guiGraphics.disableScissor();
        } else {
            guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 176, 200);
        }

    }


    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(pGuiGraphics);

        if (this.renderables.contains(recipeList)) {
            pGuiGraphics.fill(recipeList.getLeft(), recipeList.getTop() - 20,
                    recipeList.getRight(), recipeList.getBottom(),
                    0xFF2B2921);

            pGuiGraphics.renderOutline(recipeList.getLeft(), recipeList.getTop() - 20,
                    recipeList.getWidth(), 20,
                    0xFF59533B);

        }

        super.render(pGuiGraphics, mouseX, mouseY, delta);

        if (showRecipes.visible) {
            pGuiGraphics.renderItem(new ItemStack(ModItems.WARRIORS_GUIDE.get()),
                    showRecipes.getX() + 3, showRecipes.getY() + 2);
            if (showRecipes.isHovered()) {
                pGuiGraphics.renderTooltip(this.font, Component.translatable("screen.herb_mixing.display_recipes"), mouseX, mouseY);
            }
        }

        renderTooltip(pGuiGraphics, mouseX, mouseY);

        recipeList.renderCurrentTooltip(pGuiGraphics, mouseX, mouseY);
    }

    @Override
    protected void containerTick() {

        for (Renderable renderable : this.renderables) {
            if (renderable instanceof SubRenderable sub) {
                sub.tick();
            }
        }
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        boolean superr = this.getFocused() != null && this.isDragging() && pButton == 0 && this.getFocused().mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        if (superr) {
            return true;
        }

        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    protected boolean hasClickedOutside(double pMouseX, double pMouseY, int pGuiLeft, int pGuiTop, int pMouseButton) {
        boolean clickedInsideList = false;
        if (renderables.contains(recipeList)) {
            if (pMouseX > recipeList.getLeft() && pMouseX < recipeList.getRight()
                    && pMouseY > recipeList.getTop() - 20 && pMouseY < recipeList.getBottom()) {
                clickedInsideList = true;
            }
        }

        if (showRecipes.visible && showRecipes.isHovered()) {
            clickedInsideList = true;
        }

        return super.hasClickedOutside(pMouseX, pMouseY, pGuiLeft, pGuiTop, pMouseButton) && !clickedInsideList;
    }
}
