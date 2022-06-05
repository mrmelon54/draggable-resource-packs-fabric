package xyz.mrmelon54.DraggableResourcePacks.mixin.rp;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import xyz.mrmelon54.DraggableResourcePacks.duck.AbstractPackDuckProvider;
import xyz.mrmelon54.DraggableResourcePacks.duck.ResourcePackEntryDuckProvider;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PackListWidget.class)
@Environment(EnvType.CLIENT)
public abstract class PackListWidgetMixin extends AlwaysSelectedEntryListWidget<PackListWidget.ResourcePackEntry> {
    private PackListWidget.ResourcePackEntry draggingObject = null;
    private double draggingStartX = 0;
    private double draggingStartY = 0;
    private double draggingOffsetX = 0;
    private double draggingOffsetY = 0;
    private long softScrollingTimer = 0;
    private double softScrollingOrigin = 0;

    public PackListWidgetMixin(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && draggingObject == null && isCapMouseY((int) mouseY)) {
            draggingObject = this.getEntryAtPosition(mouseX, mouseY);
            if (draggingObject != null && draggingObject instanceof ResourcePackEntryDuckProvider duckProvider && isValidForDragging(duckProvider)) {
                // Save the mouse origin position and the offset for the top left corner of the widget
                draggingStartX = mouseX;
                draggingStartY = mouseY;
                draggingOffsetX = getRowLeft() - draggingStartX;
                draggingOffsetY = getRowTop(this.children().indexOf(draggingObject)) - draggingStartY;

                // Don't grab if inside the pack icon
                if (draggingOffsetX > -32f) {
                    draggingObject = null;
                    return super.mouseClicked(mouseX, mouseY, button);
                }

                this.setDragging(true);
                this.setFocused(draggingObject);
                duckProvider.setBeingDragged(true);
                softScrollingTimer = 0;
                GLFW.glfwSetCursor(client.getWindow().getHandle(), GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR));
                super.mouseClicked(mouseX, mouseY, button);
                this.setSelected(null);
                return true;
            } else {
                draggingObject = null;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.setDragging(false);
        if (draggingObject != null) {
            GLFW.glfwSetCursor(client.getWindow().getHandle(), GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR));
            if (draggingObject instanceof ResourcePackEntryDuckProvider duckProvider)
                duckProvider.setBeingDragged(false);
        }
        draggingObject = null;
        softScrollingTimer = 0;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && updateDragEvent(mouseX, mouseY)) return true;
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (draggingObject != null) return true;
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    boolean updateDragEvent(double mouseX, double mouseY) {
        if (mouseX < this.left) {
            draggingStartX = mouseX;
        }
        double y = capYCoordinate((int) mouseY, true);

        PackListWidget.ResourcePackEntry hoveredEntry = this.getEntryAtPosition(mouseX, y);

        ResourcePackOrganizer.Pack draggingPack = draggingObject instanceof ResourcePackEntryDuckProvider duckProvider ? duckProvider.getUnderlyingPack() : null;
        ResourcePackOrganizer.Pack hoveredPack = hoveredEntry instanceof ResourcePackEntryDuckProvider duckProvider ? duckProvider.getUnderlyingPack() : null;

        if (draggingPack != null && hoveredPack != null && draggingPack != hoveredPack && draggingObject instanceof ResourcePackEntryDuckProvider underlyingPackProvider) {
            if (draggingPack instanceof AbstractPackDuckProvider abstractPack && dragResourcePack(underlyingPackProvider, draggingStartY, y)) {
                draggingStartY = mouseY;
                int z = abstractPack.getIndexInCurrentList();
                draggingObject = z == -1 ? null : getEntry(z);
                if (draggingObject instanceof ResourcePackEntryDuckProvider duckProvider)
                    duckProvider.setBeingDragged(true);
                this.setSelected(null);
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        if (this.draggingObject instanceof ResourcePackEntryDuckProvider duckProvider) {
            int z = MathHelper.floor(mouseY + draggingOffsetY);
            int x = MathHelper.floor(draggingStartX + draggingOffsetX);
            int y = capYCoordinate(z);
            int entryHeight = this.itemHeight - 4;
            int entryWidth = this.getRowWidth();
            duckProvider.renderPoppedOut(matrices, 0, y, x, entryWidth, entryHeight, mouseX, mouseY, false, delta);

            if (y < z) {
                if (softScrollingTimer == 0) {
                    softScrollingTimer = Util.getMeasuringTimeMs();
                    softScrollingOrigin = getScrollAmount();
                }
                float f = (float) (Util.getMeasuringTimeMs() - softScrollingTimer) / 5f;
                setScrollAmount(softScrollingOrigin + f);
            } else if (y > z) {
                if (softScrollingTimer == 0) {
                    softScrollingTimer = Util.getMeasuringTimeMs();
                    softScrollingOrigin = getScrollAmount();
                }
                float f = (float) (Util.getMeasuringTimeMs() - softScrollingTimer) / 5f;
                setScrollAmount(softScrollingOrigin - f);
            } else {
                softScrollingTimer = 0;
            }

            updateDragEvent(mouseX, mouseY);
        }
    }

    int capYCoordinate(int y, boolean useScreenSpace) {
        int scrollableTop = top + (useScreenSpace ? 0 : (int) Math.max(headerHeight - getScrollAmount() + 2, 0)) + 2;
        int scrollableHeight = bottom - top - (useScreenSpace ? 0 : itemHeight + (int) Math.max(headerHeight - getScrollAmount() + 2, 0));
        if (y < scrollableTop) y = scrollableTop;
        if (y > scrollableTop + scrollableHeight) y = scrollableTop + scrollableHeight;
        return y;
    }

    int capYCoordinate(int y) {
        return capYCoordinate(y, false);
    }

    boolean isCapMouseY(int y) {
        return capYCoordinate(y, true) == y;
    }

    boolean dragResourcePack(ResourcePackEntryDuckProvider underlyingPackProvider, double draggingStartY, double mouseY) {
        int m = MathHelper.floor(mouseY - (double) this.top) - this.headerHeight + (int) this.getScrollAmount() - 4;
        int n = m / this.itemHeight;

        ResourcePackOrganizer.Pack pack = underlyingPackProvider.getUnderlyingPack();
        if (pack instanceof AbstractPackDuckProvider duckProvider)
            duckProvider.moveTo(n);
        return true;
    }

    boolean isValidForDragging(ResourcePackEntryDuckProvider resourcePackEntryDuckProvider) {
        return resourcePackEntryDuckProvider.isPackSelectable() && resourcePackEntryDuckProvider.getUnderlyingPack().isEnabled();
    }
}
