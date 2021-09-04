package net.onpointcoding.draggableresourcepacks.duck;

import net.minecraft.client.network.ServerInfo;

public interface MultiplayerScreenDuckProvider {
    int getIndexOfServerInfo(ServerInfo serverInfo);
}
