package com.remarxk.guitween.dataPack;

import com.google.gson.*;
import com.remarxk.guitween.GUITween;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public class WindowSlotsLoader extends SimpleJsonResourceReloadListener<WindowSlotsConfig> {
    private static WindowSlotsLoader instance;

    public final static HashMap<String, WindowSlotsConfig> configs = new HashMap<>();

    protected WindowSlotsLoader() {
        super(
                WindowSlotsConfig.CODEC,
                FileToIdConverter.json("window_slots")
        );
    }

    public static WindowSlotsLoader getInstance() {
        if (instance == null) {
            instance = new WindowSlotsLoader();
        }

        return instance;
    }

    @Override
    protected void apply(Map<Identifier, WindowSlotsConfig> identifierWindowSlotsConfigMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        configs.clear();

        identifierWindowSlotsConfigMap.forEach(((identifier, windowSlotsConfig) -> {
            if (windowSlotsConfig.name() != null && !windowSlotsConfig.name().isEmpty()) {
                configs.put(windowSlotsConfig.name(), windowSlotsConfig);
//                GUITween.LOGGER.info("添加数据包:{}", windowSlotsConfig.name());
            }
        }));
    }

    @SubscribeEvent
    public static void onAddReloadListenerEvent(AddClientReloadListenersEvent event) {
        event.addListener(Identifier.fromNamespaceAndPath(GUITween.MODID, "window_slots"), getInstance());
    }
}
