package com.remarxk.guitween.compat;

import com.remarxk.guitween.GUITween;
import it.hurts.octostudios.immersiveui.ImmersiveUI;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = GUITween.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ImmersiveUICompat {
    public static boolean isLoaded = false;

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded(ImmersiveUI.MOD_ID)) {
            isLoaded = true;
        }
    }
}
