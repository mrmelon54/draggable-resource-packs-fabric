package xyz.mrmelon54.DraggableResourcePacks.mixin.rp;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
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
