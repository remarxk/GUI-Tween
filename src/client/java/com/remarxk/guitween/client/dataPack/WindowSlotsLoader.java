package com.remarxk.guitween.client.dataPack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.remarxk.guitween.GUITween;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;
import java.util.HashMap;
import java.util.Map;

public class WindowSlotsLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();

    private static WindowSlotsLoader instance;

    public final static HashMap<String, WindowSlotsConfig> configs = new HashMap<>();

    public WindowSlotsLoader() {
        super(GSON, "window_slots");
    }

    public static WindowSlotsLoader getInstance() {
        if (instance == null) {
            instance = new WindowSlotsLoader();
        }

        return instance;
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(GUITween.MODID, "window_slots");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> result) {
        result.forEach(((identifier, jsonElement) -> {
            try {
                WindowSlotsConfig config = WindowSlotsConfig.fromJson(jsonElement.getAsJsonObject());
                if (config.name != null && !config.name.isEmpty()) {
                    configs.put(config.name, config);
                    GUITween.LOGGER.info("添加数据包:{}", config.name);
                }
            }
            catch (Exception exception) {
                GUITween.LOGGER.info("Failed to load output slots: {}", identifier.toString());
            }
        }));
    }
}
