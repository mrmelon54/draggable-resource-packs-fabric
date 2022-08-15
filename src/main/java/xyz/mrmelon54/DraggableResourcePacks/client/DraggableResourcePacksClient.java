package xyz.mrmelon54.DraggableResourcePacks.client;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import xyz.mrmelon54.DraggableResourcePacks.config.ConfigStructure;

@Environment(EnvType.CLIENT)
public class DraggableResourcePacksClient implements ClientModInitializer {
    private static DraggableResourcePacksClient instance;
    private ConfigStructure config;

    @Override
    public void onInitializeClient() {
        instance = this;

        AutoConfig.register(ConfigStructure.class, JanksonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ConfigStructure.class).get();
    }

    public static DraggableResourcePacksClient getInstance() {
        return instance;
    }

    public ConfigStructure getConfig() {
        return config;
    }
}
