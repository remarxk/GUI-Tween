package com.remarxk.guitween.dataPack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record WindowSlotsConfig (String name, List<Integer> outputSlots) {
    public static final Codec<WindowSlotsConfig> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.optionalFieldOf("name", "")
                            .forGetter(WindowSlotsConfig::name),

                    Codec.list(Codec.INT)
                            .fieldOf("output_slots")
                            .forGetter(WindowSlotsConfig::outputSlots)
            ).apply(instance, WindowSlotsConfig::new));
}
