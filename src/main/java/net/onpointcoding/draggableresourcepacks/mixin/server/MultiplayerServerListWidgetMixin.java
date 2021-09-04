package net.onpointcoding.draggableresourcepacks.mixin.server;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.onpointcoding.draggableresourcepacks.duck.MultiplayerScreenDuckProvider;
import net.onpointcoding.draggableresourcepacks.duck.ServerEntryDuckProvider;
import net.onpointcoding.draggableresourcepacks.duck.ServerListDuckProvider;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(MultiplayerServerListWidget.class)
public abstract class MultiplayerServerListWidgetMixin extends AlwaysSelectedEntryListWidget<MultiplayerServerListWidget.Entry> {
    @Shadow
    @Final
    private List<MultiplayerServerListWidget.ServerEntry> servers;
    @Shadow
    @Final
    private MultiplayerScreen screen;

    @Shadow
    public abstract void setServers(ServerList servers);

    @Shadow
    public abstract void setSelected(@Nullable MultiplayerServerListWidget.Entry entry);

    private MultiplayerServerListWidget.ServerEntry draggingObject = null;
    private double draggingStartX = 0;
    private double draggingStartY = 0;
    private double draggingOffsetX = 0;
    private double draggingOffsetY = 0;
    private long softScrollingTimer = 0;
    private double softScrollingOrigin = 0;

    public MultiplayerServerListWidgetMixin(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && draggingObject == null && isCapMouseY((int) mouseY)) {
            MultiplayerServerListWidget.Entry a = this.getEntryAtPosition(mouseX, mouseY);
            draggingObject = a instanceof MultiplayerServerListWidget.ServerEntry b ? b : null;
            if (draggingObject != null && draggingObject instanceof ServerEntryDuckProvider duckProvider) {
                // Save the mouse origin position and the offset for the top left corner of the widget
                draggingStartX = mouseX;
                draggingStartY = mouseY;
                draggingOffsetX = getRowLeft() - draggingStartX;
                draggingOffsetY = getRowTop(this.children().indexOf(draggingObject)) - draggingStartY;

                // Don't grab if inside the server icon
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
            if (draggingObject instanceof ServerEntryDuckProvider duckProvider)
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
        double y = capYCoordinate((int) mouseY, true);

        MultiplayerServerListWidget.Entry hoveredEntry = this.getEntryAtPosition(mouseX, y);

        ServerInfo draggingPack = draggingObject instanceof ServerEntryDuckProvider duckProvider ? duckProvider.getUnderlyingServer() : null;
        ServerInfo hoveredPack = hoveredEntry instanceof ServerEntryDuckProvider duckProvider ? duckProvider.getUnderlyingServer() : null;

        if (draggingPack != null && hoveredPack != null && draggingPack != hoveredPack && draggingObject instanceof ServerEntryDuckProvider serverEntryDuckProvider) {
            if (dragServerItem(serverEntryDuckProvider, draggingStartY, y)) {
                draggingStartY = mouseY;
                if (screen instanceof MultiplayerScreenDuckProvider multiplayerScreenDuckProvider) {
                    int z = multiplayerScreenDuckProvider.getIndexOfServerInfo(serverEntryDuckProvider.getUnderlyingServer());
                    draggingObject = z == -1 ? null : (getEntry(z) instanceof MultiplayerServerListWidget.ServerEntry b ? b : null);
                    if (draggingObject instanceof ServerEntryDuckProvider duckProvider)
                        duckProvider.setBeingDragged(true);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        if (this.draggingObject instanceof ServerEntryDuckProvider duckProvider) {
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
        int scrollableTop = top + 2;
        int scrollableHeight = bottom - top - (useScreenSpace ? 0 : itemHeight);
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

    boolean dragServerItem(ServerEntryDuckProvider underlyingServerProvider, double draggingStartY, double mouseY) {
        if (screen instanceof MultiplayerScreenDuckProvider multiplayerScreenDuckProvider) {
            int i = multiplayerScreenDuckProvider.getIndexOfServerInfo(underlyingServerProvider.getUnderlyingServer());
            if (i == -1) return false;

            int m = MathHelper.floor(mouseY - (double) this.top) - this.headerHeight + (int) this.getScrollAmount() - 4;
            int n = m / this.itemHeight;

            if (n >= 0 && n < servers.size()) {
                moveServerEntry(i, n);
                return true;
            }
        }
        return false;
    }

    void moveServerEntry(int a, int b) {
        if (this.screen.getServerList() instanceof ServerListDuckProvider duckProvider) {
            ServerInfo serverInfo = this.screen.getServerList().get(a);
            this.screen.getServerList().remove(serverInfo);
            duckProvider.add(b, serverInfo);
            this.screen.getServerList().saveFile();
            this.setServers(this.screen.getServerList());
        }
    }
}
