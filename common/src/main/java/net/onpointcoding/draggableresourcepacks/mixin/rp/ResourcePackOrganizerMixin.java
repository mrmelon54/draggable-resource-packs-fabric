package net.onpointcoding.draggableresourcepacks.mixin.rp;

import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.onpointcoding.draggableresourcepacks.duck.ResourcePackOrganizerDuckProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ResourcePackOrganizer.class)
public class ResourcePackOrganizerMixin implements ResourcePackOrganizerDuckProvider {
    @Shadow
    @Final
    Runnable updateCallback;

    @Override
    public Runnable getUpdateCallback() {
        return updateCallback;
    }
}
