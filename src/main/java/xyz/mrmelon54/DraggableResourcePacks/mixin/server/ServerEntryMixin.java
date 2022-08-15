package xyz.mrmelon54.DraggableResourcePacks.mixin.server;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.mrmelon54.DraggableResourcePacks.client.DraggableResourcePacksClient;
import xyz.mrmelon54.DraggableResourcePacks.duck.ServerEntryDuckProvider;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public abstract class ServerEntryMixin extends AlwaysSelectedEntryListWidget.Entry<MultiplayerServerListWidget.Entry> implements ServerEntryDuckProvider {
    @Shadow
    @Final
    private ServerInfo server;

    @Shadow
    protected abstract void swapEntries(int i, int j);

    private boolean isBeingDragged = false;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        if (isBeingDragged)
            ci.cancel();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawableHelper;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIFFIIII)V", ordinal = 3))
    public void removeUpOnButton(MatrixStack matrices, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        if (DraggableResourcePacksClient.getInstance().getConfig().disableServerArrows) return;
        DrawableHelper.drawTexture(matrices, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawableHelper;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIFFIIII)V", ordinal = 4))
    public void removeUpButton(MatrixStack matrices, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        if (DraggableResourcePacksClient.getInstance().getConfig().disableServerArrows) return;
        DrawableHelper.drawTexture(matrices, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawableHelper;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIFFIIII)V", ordinal = 5))
    public void removeDownOnButton(MatrixStack matrices, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        if (DraggableResourcePacksClient.getInstance().getConfig().disableServerArrows) return;
        DrawableHelper.drawTexture(matrices, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawableHelper;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIFFIIII)V", ordinal = 6))
    public void removeDownButton(MatrixStack matrices, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        if (DraggableResourcePacksClient.getInstance().getConfig().disableServerArrows) return;
        DrawableHelper.drawTexture(matrices, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    @Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget$ServerEntry;swapEntries(II)V"))
    public void removeSwapEntries(MultiplayerServerListWidget.ServerEntry instance, int i, int j) {
        if (DraggableResourcePacksClient.getInstance().getConfig().disableServerArrows) return;
        swapEntries(i, j);
    }

    @Override
    public ServerInfo getUnderlyingServer() {
        return server;
    }

    @Override
    public void renderPoppedOut(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        if (isBeingDragged) {
            isBeingDragged = false;
            matrices.push();

            float z = 191f / 255f;
            RenderSystem.setShaderColor(z, z, z, 0.5F);
            DrawableHelper.fill(matrices, x - 1, y - 1, x + entryWidth - 2, y + entryHeight + 1, 0xbfbfbfff);

            render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
            matrices.pop();
            isBeingDragged = true;
        }
    }

    @Override
    public void setBeingDragged(boolean v) {
        isBeingDragged = v;
    }
}
