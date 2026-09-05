package com.remarxk.guitween;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.remarxk.guitween.compat.SmoothSwappingCompat;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.config.GUITweenConfigFilter;

@Mod(GUITween.MODID)
public class GUITween
{
    public static final String MODID = "guitween";

    public static final Logger LOGGER = LogUtils.getLogger();

    public GUITween(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.register(SmoothSwappingCompat.class);

        modContainer.registerConfig(ModConfig.Type.CLIENT, GUITweenConfig.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (mod, parent) -> {
            return new ConfigurationScreen(mod, parent, new GUITweenConfigFilter());
        });
    }
}
