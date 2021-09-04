package net.onpointcoding.draggableresourcepacks.mixin.server;

import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.onpointcoding.draggableresourcepacks.duck.ServerListDuckProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ServerList.class)
public class ServerListMixin implements ServerListDuckProvider {
    @Shadow
    @Final
    private List<ServerInfo> servers;

    @Override
    public void add(int index, ServerInfo serverInfo) {
        this.servers.add(index, serverInfo);
    }
}
