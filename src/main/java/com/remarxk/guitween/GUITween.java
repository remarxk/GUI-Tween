package com.remarxk.guitween;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.remarxk.guitween.config.GUITweenConfig;

import me.fzzyhmstrs.fzzy_config.api.ConfigApi;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(GUITween.MODID)
public class GUITween
{
    public static GUITweenConfig CONFIG;

    public static final String MODID = "guitween";

    public static final Logger LOGGER = LogUtils.getLogger();

    public GUITween(FMLJavaModLoadingContext context)
    {
        CONFIG = ConfigApi.registerAndLoadConfig(GUITweenConfig::new, RegisterType.CLIENT);

        MinecraftForge.EVENT_BUS.register(this);
    }
}
