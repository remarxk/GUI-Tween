package com.remarxk.guitween;

import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.config.NeoForgeConfigAdapter;
import com.remarxk.guitween.config.NeoforgeGUITweenConfig;
import com.remarxk.guitween.dataPack.WindowSlotsLoader;
import com.remarxk.guitween.eventListener.ScreenRenderListener;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(Constants.MODID)
public class GUITween {
    public GUITween(IEventBus eventBus, ModContainer modContainer) {
    }
}