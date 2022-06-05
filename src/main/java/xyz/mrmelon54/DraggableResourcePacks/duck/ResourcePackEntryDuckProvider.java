package xyz.mrmelon54.DraggableResourcePacks.duck;

import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.util.math.MatrixStack;

public interface ResourcePackEntryDuckProvider {
    ResourcePackOrganizer.Pack getUnderlyingPack();

    boolean isPackSelectable();

    void renderPoppedOut(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);

    void setBeingDragged(boolean v);
}
