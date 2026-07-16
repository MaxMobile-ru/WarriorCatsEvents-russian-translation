package net.snowteb.warriorcats_events.screen.widgets;

import com.eliotlash.mclib.math.functions.limit.Min;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.snowteb.warriorcats_events.recipes.HerbsRecipe;
import net.snowteb.warriorcats_events.screen.screens.createmorph.FancyButtonScrollList;
import net.snowteb.warriorcats_events.screen.screens.createmorph.FancySelectableButton;
import net.snowteb.warriorcats_events.screen.screens.createmorph.FancySimpleButton;
import net.snowteb.warriorcats_events.screen.screens.createmorph.SubRenderable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class FancyHerbsRecipeScrollList extends AbstractSelectionList<FancyHerbsRecipeScrollList.Entry> implements SubRenderable {

    final int originalYPos0;
    final int originalYPos1;
    private BiPredicate<Double, Double> isClickableIn;

    List<Component> currentTooltip = new ArrayList<>();

    public FancyHerbsRecipeScrollList(Minecraft mc, int width, int height, int top, int bottom) {
        super(mc, width, height, top, bottom, 50);
        originalYPos0 = top;
        originalYPos1 = bottom;
    }

    public void addRecipes(List<HerbsRecipe> recipes) {
        for (HerbsRecipe recipe : recipes) {
            addEntry(new Entry(recipe));
        }
    }

    public void setCurrentTooltip(List<Component> currentTooltip) {
        this.currentTooltip = currentTooltip;
    }

    public List<Component> getCurrentTooltip() {
        return currentTooltip;
    }

    public void renderCurrentTooltip(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        if (!getCurrentTooltip().isEmpty()) {
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, getCurrentTooltip(), Optional.empty(), pMouseX, pMouseY);
            getCurrentTooltip().clear();
        }
    }

    @Override
    public int getRowWidth() {
        return width - 20;
    }


    @Override
    protected int getScrollbarPosition() {
        return getLeft() + width - 6;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
    }

    @Override
    protected void renderDecorations(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        super.renderDecorations(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBackground(GuiGraphics pGuiGraphics) {
        super.renderBackground(pGuiGraphics);
    }

    @Override
    protected void renderHeader(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderHeader(pGuiGraphics, pX, pY);
    }

    @Override
    protected void renderSelection(GuiGraphics pGuiGraphics, int pTop, int pWidth, int pHeight, int pOuterColor, int pInnerColor) {
    }

    @Override
    protected void renderItem(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, int pIndex, int pLeft, int pTop, int pWidth, int pHeight) {
        super.renderItem(pGuiGraphics, pMouseX, pMouseY, pPartialTick, pIndex, pLeft, pTop, pWidth, pHeight);
    }

    @Override
    protected void renderList(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderList(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    int menuX = 0;

    public void setMenuX(int i) {
        this.menuX = i;
    }

    @Override
    protected void enableScissor(GuiGraphics pGuiGraphics) {
        pGuiGraphics.enableScissor(this.x0 + menuX, this.y0, this.x1 + menuX, this.y1);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

        pGuiGraphics.fill(this.x0, this.y0,
                this.x0 + width, this.y1,
                0x55000000);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.setColor(0.25F, 0.25F, 0.25F, 1.0F);
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        int color = 0x990D0C08;

        pGuiGraphics.fillGradient(RenderType.guiOverlay(), this.x0, this.y0, this.x1, this.y0 + 6, color, 0, 0);
        pGuiGraphics.fillGradient(RenderType.guiOverlay(), this.x0, this.y1 - 6, this.x1, this.y1, 0, color, 0);

        pGuiGraphics.renderOutline(this.x0, this.y0 - 1,
                this.width, this.y1 - this.y0 + 2,
                0xFF59533B);
    }

    @Override
    public void setSelected(@Nullable FancyHerbsRecipeScrollList.Entry pSelected) {

    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (this.isClickableIn() != null) {
            if (!this.isClickableIn().test(pMouseX, pMouseY)) {
                return false;
            }
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        if (this.isClickableIn() != null) {
            if (!this.isClickableIn().test(pMouseX, pMouseY)) {
                return false;
            }
        }
        return super.isMouseOver(pMouseX, pMouseY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.isClickableIn() != null) {
            if (!this.isClickableIn().test(pMouseX, pMouseY)) {
                return false;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void adjustYPos(int yOffset) {
        this.y0 = getOriginalYPos0() + yOffset;
        this.y1 = getOriginalYPos1() + yOffset;
    }

    @Override
    public int getOriginalYPos0() {
        return originalYPos0;
    }

    @Override
    public int getOriginalYPos1() {
        return originalYPos1;
    }

    @Override
    public void setClickableIn(BiPredicate<Double, Double> predicate) {
        isClickableIn = predicate;
    }

    @Override
    public BiPredicate<Double, Double> isClickableIn() {
        return isClickableIn;
    }

    public class Entry extends AbstractSelectionList.Entry<Entry> {
        List<Ingredient> ingredients;
        ItemStack result;

        public Entry(HerbsRecipe recipe) {
            ingredients = recipe.getIngredients();
            result = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return false;
        }

        @Override
        public void render(GuiGraphics pGuiGraphics, int pIndex, int pTop, int pLeft,
                           int pWidth, int pHeight, int pMouseX, int pMouseY,
                           boolean pHovering, float pPartialTick) {

            int xPos = pLeft - 5;
            int yPos = pTop;

            int widgetWidth = FancyHerbsRecipeScrollList.this.getRowWidth();
            int widgetHeight = FancyHerbsRecipeScrollList.this.itemHeight - 10;

            boolean hovered = pMouseX > xPos && pMouseX < xPos + widgetWidth
                    && pMouseY > yPos && pMouseY < yPos + widgetHeight;

            RenderSystem.enableBlend();
            pGuiGraphics.setColor(1,1,1,0.8f);
            pGuiGraphics.blitNineSliced(FancySimpleButton.FANCY_BUTTON_TEXTURE,
                    xPos, yPos,
                    widgetWidth, widgetHeight,
                    20, 5,
                    200, 20,
                    0, 46 + 20 * (hovered ? 2 : 1));
            pGuiGraphics.setColor(1,1,1,1);

            pGuiGraphics.fillGradient(xPos, yPos,
                    xPos + widgetWidth, yPos + widgetHeight,
                    0x00000000, hovered ? 0xff000000 : 0x77000000);
            RenderSystem.disableBlend();


            pGuiGraphics.renderOutline(xPos, yPos,
                    widgetWidth, widgetHeight,
                    hovered ? 0xbbF2DCAE : 0x61F2DCAE);


            {
                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().translate(xPos + 5, yPos + 5, 0);
                float scale = 0.7f;
                Font font = Minecraft.getInstance().font;
                int textWidth = font.width(result.getHoverName());

                int maxTextWidth = widgetWidth - 10;

                float finalScale = scale;
                if (textWidth > 0 && maxTextWidth > 0) {
                    float scaledTextWidth = textWidth * scale;
                    if (scaledTextWidth > maxTextWidth) {
                        finalScale = Math.max(maxTextWidth / (float) textWidth, 0.5f);
                    }
                }

                pGuiGraphics.pose().scale(finalScale, finalScale, finalScale);

                {
                    List<FormattedCharSequence> titleLines = Minecraft.getInstance().font.split(result.getHoverName(), (int) (maxTextWidth/finalScale));

                    int y= 0;
                    for (FormattedCharSequence titleLine : titleLines) {
                        pGuiGraphics.drawString(Minecraft.getInstance().font, titleLine, 0, y, 0xFFFFFFFF);
                        y += 10;
                    }
                    yPos += (int) ((titleLines.size()-1)*(1/finalScale));
                }

                pGuiGraphics.pose().popPose();
            }

            List<Component> current = new ArrayList<>(result.getTooltipLines(Minecraft.getInstance().player, TooltipFlag.NORMAL));
            current.add(Component.empty());
            current.add(Component.translatable("screen.herb_mixing.ingredients").withStyle(ChatFormatting.GOLD));

            int xIng = xPos + 5;
            int yIng = yPos + 15;
            for (int i = 0; i < ingredients.size(); i++) {
                Ingredient ingredient = ingredients.get(i);

                ItemStack[] stacks = ingredient.getItems();

                float itemScale = 0.8f;
                int iconsWidth = 18*(ingredients.size()-1);

                int maxIconsWidth = widgetWidth - 16;

                float iconsFinalScale = itemScale;
                if (iconsWidth > 0 && maxIconsWidth > 0) {
                    float scaledIconWidth = iconsWidth * itemScale;
                    if (scaledIconWidth > maxIconsWidth) {
                        iconsFinalScale = Math.max(maxIconsWidth / (float) iconsWidth, 0.55f);
                    }
                }

                if (i == 3 && iconsFinalScale < 0.6f) {
                    xIng = xPos + 5;
                    yIng = yPos + 25;
                }

                if (stacks.length > 0) {
                    int index = (int) ((System.currentTimeMillis() / 1000L) % stacks.length);
                    ItemStack stack = stacks[index];

                    pGuiGraphics.pose().pushPose();
                    pGuiGraphics.pose().translate(xIng, yIng, 0);


                    pGuiGraphics.pose().scale(iconsFinalScale, iconsFinalScale, iconsFinalScale);


                    pGuiGraphics.renderItem(stack, 0, 0);
                    pGuiGraphics.renderItemDecorations(minecraft.font, stack, 0, 0);

                    pGuiGraphics.pose().popPose();

                    if (hovered) current.add(Component.literal("• ")
                            .append(stack.getHoverName()));

                }

                xIng += (int) (18*iconsFinalScale);

            }


            if (hovered) FancyHerbsRecipeScrollList.this.setCurrentTooltip(current);

        }
    }
}
