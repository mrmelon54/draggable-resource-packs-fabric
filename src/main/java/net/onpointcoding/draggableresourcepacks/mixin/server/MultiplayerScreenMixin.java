package net.onpointcoding.draggableresourcepacks.mixin.server;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.text.Text;
import net.onpointcoding.draggableresourcepacks.duck.MultiplayerScreenDuckProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen implements MultiplayerScreenDuckProvider {
    @Shadow
    protected MultiplayerServerListWidget serverListWidget;

    @Shadow
    public abstract ServerList getServerList();

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (serverListWidget.isDragging()) serverListWidget.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public int getIndexOfServerInfo(ServerInfo serverInfo) {
        ServerList serverList = getServerList();
        for (int i = 0; i < serverList.size(); i++)
            if (serverList.get(i) == serverInfo) return i;
        return -1;
    }
}
