package xyz.mrmelon54.DraggableResourcePacks.mixin.server;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.text.Text;
import xyz.mrmelon54.DraggableResourcePacks.duck.MultiplayerScreenDuckProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen implements MultiplayerScreenDuckProvider {
    @Shadow
    protected MultiplayerServerListWidget serverListWidget;

    @Shadow
    public abstract ServerList getServerList();

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "connect(Lnet/minecraft/client/network/ServerInfo;)V", at = @At("HEAD"))
    private void injectedConnect(CallbackInfo ci) {
        if (serverListWidget.isDragging()) serverListWidget.mouseReleased(0, 0, 0);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (serverListWidget.isDragging()) serverListWidget.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        if (serverListWidget.isDragging()) serverListWidget.mouseReleased(0, 0, 0);
        super.close();
    }

    public int getIndexOfServerInfo(ServerInfo serverInfo) {
        ServerList serverList = getServerList();
        for (int i = 0; i < serverList.size(); i++)
            if (serverList.get(i) == serverInfo) return i;
        return -1;
    }
}
