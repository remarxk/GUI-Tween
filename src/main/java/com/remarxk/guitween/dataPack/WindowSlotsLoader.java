package com.remarxk.guitween.dataPack;

import com.google.gson.*;
import com.remarxk.guitween.GUITween;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public class WindowSlotsLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();

    private static WindowSlotsLoader instance;

    public final static HashMap<String, WindowSlotsConfig> configs = new HashMap<>();

    public WindowSlotsLoader() {
        super(GSON,  "window_slots"); // 对应 data/<modid>/custom_slots
    }

    public WindowSlotsLoader(Gson gson, String directory) {
        super(gson, directory);
    }

    public static WindowSlotsLoader getInstance() {
        if (instance == null) {
            instance = new WindowSlotsLoader();
        }

        return instance;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        configs.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
            try {
                WindowSlotsConfig config = WindowSlotsConfig.fromJson(entry.getValue().getAsJsonObject());
                if (config.name != null && !config.name.isEmpty()) {
                    configs.put(config.name, config);
                    GUITween.LOGGER.info("添加数据包:{}", config.name);
                }
            } catch (Exception ex) {
                System.err.println("Failed to load output slots: " + entry.getKey());
                ex.printStackTrace();
            }
        }
    }

    public static boolean isOutputSlot(String name, int slot) {
        WindowSlotsConfig config = configs.getOrDefault(name, null);
        if (config == null)
            return false;

        return config.outputSlots.contains(slot);
    }

    @SubscribeEvent
    public static void onAddReloadListenerEvent(AddReloadListenerEvent event) {
        event.addListener(getInstance());
    }
}
