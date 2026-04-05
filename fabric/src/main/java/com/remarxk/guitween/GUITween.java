package com.remarxk.guitween;

import com.remarxk.guitween.config.FabricConfigAdapter;
import com.remarxk.guitween.config.FabricGUITweenConfig;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.dataPack.WindowSlotsLoader;
import com.remarxk.guitween.eventListener.HotbarChangeListener;
import com.remarxk.guitween.eventListener.ScreenRenderListener;
import kotlin.jvm.functions.Function0;
import me.fzzyhmstrs.fzzy_config.api.ConfigApi;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

public class GUITween implements ModInitializer {

    @Override
    public void onInitialize() {

    }
}
