package com.remarxk.guitween.config;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.gui.DropdownWidget;
import com.remarxk.guitween.util.Ease;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PopupScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GUITweenConfigFilter implements ConfigurationScreen.ConfigurationSectionScreen.Filter {
    @Override
    public ConfigurationScreen.ConfigurationSectionScreen.@Nullable Element filterEntry(ConfigurationScreen.ConfigurationSectionScreen.Context context, String key, ConfigurationScreen.ConfigurationSectionScreen.Element original) {
        if (key.contains("Ease")) {
            ModConfigSpec.EnumValue<Ease> configValue = null;

            for (var entry : context.entries()) {
                if (entry.getKey().equals(key)) {
                    configValue = entry.getRawValue();
                    break;
                }
            }

            DropdownWidget dropdownWidget = new DropdownWidget(
                    0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
                    Arrays.stream(Ease.values())
                            .map(e -> Component.literal(e.name()))
                            .collect(Collectors.toList()),
                    index -> {
                        for (var entry : context.entries()) {
                            if (entry.getKey().equals(key)) {
                                var cv = (ModConfigSpec.EnumValue<Ease>) entry.getRawValue();
                                cv.set(Ease.values()[index]);
                                break;
                            }
                        }
                    }
            );

            dropdownWidget.setSelectedIndex(configValue.get().ordinal());

            return new ConfigurationScreen.ConfigurationSectionScreen.Element(
                    original.name(),
                    original.tooltip(),
                    dropdownWidget
            );
        }

        return original;
    }
}
