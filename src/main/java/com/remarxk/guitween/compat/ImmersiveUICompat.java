package com.remarxk.guitween.compat;

import com.remarxk.guitween.GUITween;
import it.hurts.shatterbyte.immersiveui.ImmersiveUI;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = GUITween.MODID)
public class ImmersiveUICompat {
    public static boolean isLoaded = false;

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded(ImmersiveUI.MOD_ID)) {
            isLoaded = true;
        }
    }
}
