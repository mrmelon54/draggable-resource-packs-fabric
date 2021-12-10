package net.onpointcoding.draggableresourcepacks.mixin.rp;

import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.resource.ResourcePackProfile;
import net.onpointcoding.draggableresourcepacks.duck.AbstractPackDuckProvider;
import net.onpointcoding.draggableresourcepacks.duck.ResourcePackOrganizerDuckProvider;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ResourcePackOrganizer.AbstractPack.class)
public abstract class AbstractPackMixin implements AbstractPackDuckProvider {
    @Shadow
    public abstract List<ResourcePackProfile> getCurrentList();

    @Shadow(aliases = {"this$0"})
    @Dynamic("field_25460 is provided by ResourcePackOrganizer.AbstractPack but has no mapping")
    @Final
    private ResourcePackOrganizer field_25460;

    @Shadow
    @Final
    private ResourcePackProfile profile;

    @Override
    public void moveTo(int j) {
        List<ResourcePackProfile> list = this.getCurrentList();
        list.remove(this.profile);
        list.add(j, this.profile);
        if (this.field_25460 instanceof ResourcePackOrganizerDuckProvider duckProvider)
            duckProvider.getUpdateCallback().run();
    }

    @Override
    public int getIndexInCurrentList() {
        return this.getCurrentList().indexOf(this.profile);
    }
}
