package com.remarxk.guitween.client;

import com.remarxk.guitween.Constants;
import com.remarxk.guitween.config.FabricConfigAdapter;
import com.remarxk.guitween.config.FabricGUITweenConfig;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.dataPack.WindowSlotsLoader;
import com.remarxk.guitween.eventListener.HotbarChangeListener;
import com.remarxk.guitween.eventListener.ScreenRenderListener;
import kotlin.jvm.functions.Function0;
import me.fzzyhmstrs.fzzy_config.api.ConfigApi;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

public class GUITweenClient implements ClientModInitializer {
    public static FabricGUITweenConfig CONFIG = ConfigApi.registerAndLoadConfig((Function0<? extends FabricGUITweenConfig>) FabricGUITweenConfig::new, RegisterType.CLIENT);

    @Override
    public void onInitializeClient() {
        GUITweenConfig.setConfig(new FabricConfigAdapter(CONFIG));

        registerReloadRes();
        registerEvents();
    }

    public void registerReloadRes() {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(Identifier.fromNamespaceAndPath(Constants.MODID, "window_slots"), WindowSlotsLoader.getInstance());
    }

    public void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(HotbarChangeListener::onPlayerTick);

        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
//            ScreenEvents.afterBackground(screen).register(ScreenRenderListener::postRenderBackground);
            ScreenEvents.afterTick(screen).register(ScreenRenderListener::postScreenTick);
            ScreenEvents.afterExtract(screen).register(ScreenRenderListener::postRenderScreen);
        });
    }


}
