package xyz.mrmelon54.DraggableResourcePacks.duck;

import net.minecraft.client.network.ServerInfo;

public interface ServerListDuckProvider {
    void add(int index, ServerInfo serverInfo);
}
