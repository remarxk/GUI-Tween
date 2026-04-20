package com.remarxk.guitween;

import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.config.GUITweenConfigFilter;
import com.remarxk.guitween.config.NeoForgeConfigAdapter;
import com.remarxk.guitween.config.NeoforgeGUITweenConfig;
import com.remarxk.guitween.dataPack.WindowSlotsLoader;
import com.remarxk.guitween.event.PostScreenTickEvent;
import com.remarxk.guitween.eventListener.HotbarChangeListener;
import com.remarxk.guitween.eventListener.ScreenRenderListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Constants.MODID, dist = Dist.CLIENT)
public class GUITweenClient {
    public GUITweenClient(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, NeoforgeGUITweenConfig.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, (mod, parent) -> new ConfigurationScreen(mod, parent, new GUITweenConfigFilter()));

        GUITweenConfig.setConfig(new NeoForgeConfigAdapter());
    }

    @EventBusSubscriber(modid = Constants.MODID, value = Dist.CLIENT)
    public static class DataReloadEventListener {
        @SubscribeEvent
        public static void onClientReload(final AddClientReloadListenersEvent event) {
            event.addListener(Identifier.fromNamespaceAndPath(Constants.MODID, "window_slots"), WindowSlotsLoader.getInstance());
        }
    }

    @EventBusSubscriber(modid = Constants.MODID, value = Dist.CLIENT)
    public static class ClientTickEventListener {
        @SubscribeEvent
        public static void onClientTick(final ClientTickEvent.Post event) {
            HotbarChangeListener.onPlayerTick(Minecraft.getInstance());
        }
    }

    @EventBusSubscriber(modid = Constants.MODID, value = Dist.CLIENT)
    public static class ScreenEventListener {
        @SubscribeEvent
        public static void onPostRenderScreen(final ScreenEvent.Render.Post event) {
            Screen screen = event.getScreen();
            GuiGraphicsExtractor guiGraphics = event.getGuiGraphics();
            int mouseX = event.getMouseX();
            int mouseY = event.getMouseY();
            float partialTick = event.getPartialTick();

            ScreenRenderListener.postRenderScreen(screen, guiGraphics, mouseX, mouseY, partialTick);
        }

        @SubscribeEvent
        public static void onPostScreenTick(final PostScreenTickEvent event) {
            ScreenRenderListener.postScreenTick(event.getScreen());
        }
    }
}
