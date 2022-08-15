package xyz.mrmelon54.DraggableResourcePacks.mixin.rp;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import xyz.mrmelon54.DraggableResourcePacks.client.DraggableResourcePacksClient;
import xyz.mrmelon54.DraggableResourcePacks.duck.ResourcePackEntryDuckProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PackListWidget.ResourcePackEntry.class)
public abstract class ResourcePackEntryMixin extends AlwaysSelectedEntryListWidget.Entry<PackListWidget.ResourcePackEntry> implements ResourcePackEntryDuckProvider {
    private boolean isBeingDragged = false;

    @Shadow
    @Final
    private ResourcePackOrganizer.Pack pack;

    @Shadow
    protected abstract boolean isSelectable();

    @Override
    public ResourcePackOrganizer.Pack getUnderlyingPack() {
        return pack;
    }

    @Override
    public boolean isPackSelectable() {
        return isSelectable();
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        if (isBeingDragged)
            ci.cancel();
    }

    @Redirect(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawableHelper;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIFFIIII)V"),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/pack/ResourcePackOrganizer$Pack;canMoveTowardStart()Z"))
    )
    public void removeArrowButtons(MatrixStack matrices, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        if (DraggableResourcePacksClient.getInstance().getConfig().disableResourcePackArrows) return;
        DrawableHelper.drawTexture(matrices, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    @Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/pack/ResourcePackOrganizer$Pack;moveTowardStart()V"))
    public void removeMoveTowardStart(ResourcePackOrganizer.Pack instance) {
        if (DraggableResourcePacksClient.getInstance().getConfig().disableResourcePackArrows) return;
        instance.moveTowardStart();
    }

    @Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/pack/ResourcePackOrganizer$Pack;moveTowardEnd()V"))
    public void removeMoveTowardEnd(ResourcePackOrganizer.Pack instance) {
        if (DraggableResourcePacksClient.getInstance().getConfig().disableResourcePackArrows) return;
        instance.moveTowardEnd();
    }

    @Override
    public void renderPoppedOut(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        if (isBeingDragged) {
            isBeingDragged = false;
            matrices.push();

            float z = 191f / 255f;
            RenderSystem.setShaderColor(z, z, z, 0.5F);
            DrawableHelper.fill(matrices, x - 1, y - 1, x + entryWidth - 9, y + entryHeight + 1, 0xbfbfbfff);

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
