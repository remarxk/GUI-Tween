package com.remarxk.guitween.client.dataPack;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.remarxk.guitween.GUITween;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class SimpleJsonResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    private final Gson gson;

    private final String prefix;

    public SimpleJsonResourceReloadListener(Gson gson, String prefix) {
        this.gson = gson;
        this.prefix = prefix;
    }

    @Override
    public abstract Identifier getFabricId();

    @Override
    public void reload(ResourceManager manager) {
        HashMap<Identifier, JsonElement> result = new HashMap<>();

        ResourceFinder resourceFinder = ResourceFinder.json(prefix);
        for (Map.Entry<Identifier, Resource> entry : resourceFinder.findResources(manager).entrySet()) {
            Identifier identifier = entry.getKey();
            Identifier identifier2 = resourceFinder.toResourceId(identifier);
            try {
                try (BufferedReader reader = entry.getValue().getReader()) {
                    JsonElement jsonElement = JsonHelper.deserialize(gson, reader, JsonElement.class);
                    JsonElement jsonElement2 = result.put(identifier2, jsonElement);
                    if (jsonElement2 != null) {
                        throw new IllegalStateException("Duplicate data file ignored with ID " + identifier2);
                    }
                }
            } catch (JsonParseException | IOException | IllegalArgumentException exception) {
                GUITween.LOGGER.error("Couldn't parse data file {} from {}", identifier2, identifier, exception);
            }
        }

        apply(result);
    }

    protected abstract void apply(Map<Identifier, JsonElement> result);
}
