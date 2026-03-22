package com.remarxk.guitween.client;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.client.config.GUITweenConfig;
import com.remarxk.guitween.client.dataPack.RemapClassLoader;
import com.remarxk.guitween.client.dataPack.WindowSlotsLoader;
import com.remarxk.guitween.client.eventListener.HotbarChangeListener;
import com.remarxk.guitween.client.eventListener.ScreenRenderListener;
import kotlin.jvm.functions.Function0;
import me.fzzyhmstrs.fzzy_config.api.ConfigApi;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class GUITweenClient implements ClientModInitializer {
    public static GUITweenConfig CONFIG = ConfigApi.registerAndLoadConfig((Function0<? extends GUITweenConfig>) GUITweenConfig::new, RegisterType.CLIENT);

    @Override
    public void onInitializeClient() {
        registerReloadRes();
        registerEvents();
    }

    private void registerReloadRes() {
        ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(Identifier.of(GUITween.MODID, "window_slots"), WindowSlotsLoader.getInstance());
        ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(Identifier.of(GUITween.MODID, "remap_class_name"), RemapClassLoader.getInstance());
    }

    private void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(HotbarChangeListener::onPlayerTick);

        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) ->
                ScreenEvents.afterRender(screen).register(ScreenRenderListener::postRenderScreen)
        );
    }
}
