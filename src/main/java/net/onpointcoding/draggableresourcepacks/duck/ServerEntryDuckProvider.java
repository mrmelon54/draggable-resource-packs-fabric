package net.onpointcoding.draggableresourcepacks.duck;

import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;

public interface ServerEntryDuckProvider {
    ServerInfo getUnderlyingServer();

    void renderPoppedOut(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);

    void setBeingDragged(boolean v);
}
