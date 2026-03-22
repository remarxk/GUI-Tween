package com.remarxk.guitween.client.dataPack;

import com.remarxk.guitween.GUITween;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.HashMap;
import java.util.Map;

public class WindowSlotsLoader extends JsonDataLoader<WindowSlotsConfig> {
    private static WindowSlotsLoader instance;

    public final static HashMap<String, WindowSlotsConfig> configs = new HashMap<>();

    protected WindowSlotsLoader() {
        super(
                WindowSlotsConfig.CODEC,
                ResourceFinder.json("window_slots")
        );
    }

    public static WindowSlotsLoader getInstance() {
        if (instance == null) {
            instance = new WindowSlotsLoader();
        }

        return instance;
    }

    @Override
    protected void apply(Map<Identifier, WindowSlotsConfig> identifierWindowSlotsConfigMap, ResourceManager resourceManager, Profiler profilerFiller) {
        configs.clear();

        identifierWindowSlotsConfigMap.forEach(((identifier, windowSlotsConfig) -> {
            if (windowSlotsConfig.name() != null && !windowSlotsConfig.name().isEmpty()) {
                configs.put(windowSlotsConfig.name(), windowSlotsConfig);
//                GUITween.LOGGER.info("添加数据包:{}", windowSlotsConfig.name());
            }
        }));
    }
}
