package xyz.mrmelon54.DraggableResourcePacks.mixin.rp;

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

    @Shadow
    private PackListWidget availablePackList;

    protected PackScreenMixin(Text title) {
        super(title);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (selectedPackList.isDragging()) selectedPackList.mouseReleased(mouseX, mouseY, button);
        if (availablePackList.isDragging()) availablePackList.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
