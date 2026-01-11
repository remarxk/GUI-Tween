package com.remarxk.guitween.DataPack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class WindowSlotsConfig {
    public String name;

    public List<Integer> outputSlots = new ArrayList<>();

    public static WindowSlotsConfig fromJson(JsonObject json) {
        WindowSlotsConfig config = new WindowSlotsConfig();

        if (json.has("name")) {
            config.name = json.get("name").getAsString();
        }

        if (json.has("output_slots")) {
            json.getAsJsonArray("output_slots").forEach(e -> config.outputSlots.add(e.getAsInt()));
        }
        return config;
    }
}
