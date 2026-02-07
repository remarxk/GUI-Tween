package com.remarxk.guitween.config;

import com.remarxk.guitween.GUITween;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PopupScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import org.jetbrains.annotations.Nullable;

public class GUITweenConfigFilter implements ConfigurationScreen.ConfigurationSectionScreen.Filter {
    @Override
    public ConfigurationScreen.ConfigurationSectionScreen.@Nullable Element filterEntry(ConfigurationScreen.ConfigurationSectionScreen.Context context, String key, ConfigurationScreen.ConfigurationSectionScreen.Element original) {
        if (key.contains("Ease")) {
            return new ConfigurationScreen.ConfigurationSectionScreen.Element(
                    original.name(),
                    original.tooltip(),
                    Button.builder(
                            Component.literal("Ease"),
                            new Button.OnPress() {
                                @Override
                                public void onPress(Button button) {
                                    var popupScreen = new PopupScreen.Builder(context.parent(), original.name());
                                    popupScreen.addButton(Component.literal("name1"), screen -> {
                                        GUITween.LOGGER.info("点击1");
                                        button.setMessage(Component.literal("name1"));
                                        screen.onClose();
                                    });

                                    popupScreen.addButton(Component.literal("name2"), screen -> {
                                        GUITween.LOGGER.info("点击2");
                                        button.setMessage(Component.literal("name2"));
                                        screen.onClose();
                                    });

                                    popupScreen.addButton(Component.literal("name3"), screen -> {
                                        GUITween.LOGGER.info("点击3");
                                        button.setMessage(Component.literal("name3"));
                                        screen.onClose();
                                    });

                                    Minecraft.getInstance().setScreen(popupScreen.build());
                                }
                            }
                    ).build()
            );
        }

        return original;
    }
}
