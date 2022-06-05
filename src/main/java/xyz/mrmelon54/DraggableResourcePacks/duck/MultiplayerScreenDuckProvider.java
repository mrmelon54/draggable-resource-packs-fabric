package xyz.mrmelon54.DraggableResourcePacks.duck;

import net.minecraft.client.network.ServerInfo;

public interface MultiplayerScreenDuckProvider {
    int getIndexOfServerInfo(ServerInfo serverInfo);
}
