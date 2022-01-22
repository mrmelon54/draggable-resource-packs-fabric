package net.onpointcoding.draggableresourcepacks.mixin.rp;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PackScreen.class)
public class PackScreenMixin extends Screen {
    @Shadow
    private PackListWidget selectedPackList;

    protected PackScreenMixin(Text title) {
        super(title);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (selectedPackList.isDragging()) selectedPackList.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
