package com.remarxk.guitween.client.dataPack;

import com.google.gson.*;
import com.remarxk.guitween.GUITween;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.profiler.Profiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RemapClassLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();

    private final HashMap<String, String> remap = new HashMap<>();

    private static RemapClassLoader instance;

    public RemapClassLoader() {
        super(GSON, "remap_class_name");
    }

    public static RemapClassLoader getInstance() {
        if (instance == null) {
            instance = new RemapClassLoader();
        }
        return instance;
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.tryParse(GUITween.MODID, "remap_class_name");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> result) {
        result.forEach(((identifier, jsonElement) -> {
            if (jsonElement.isJsonObject()) {
                JsonObject obj = jsonElement.getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();

                for (Map.Entry<String, JsonElement> entry : entries) {
                    String key = entry.getKey();
                    JsonElement valueElement = entry.getValue();
                    if (valueElement.isJsonPrimitive() && ((JsonPrimitive) valueElement).isString()) {
                        remap.put(key, valueElement.getAsString());
                        GUITween.LOGGER.info("添加remap:{}, {}", key, valueElement.getAsString());
                    }
                }
            }
        }));
    }

    public static String getClassName(String className) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment())
            return className;

        String remapName = instance.remap.get(className);
        if (remapName == null)
            return className;

        return remapName;
    }

    public static String getSimpleClassName(String className) {
        String remapName = getClassName(className);
        int index = remapName.lastIndexOf('.');
        return remapName.substring(index + 1);
    }
}
