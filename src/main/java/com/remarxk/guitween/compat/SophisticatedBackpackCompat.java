package com.remarxk.guitween.compat;

import com.remarxk.guitween.GUITweenUtility;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;

@EventBusSubscriber
public class SophisticatedBackpackCompat {
    public static void init() {
        if (ModList.get().isLoaded("sophisticatedbackpacks")) {
            GUITweenUtility.COMPAT_WINDOW.add(BackpackScreen.class);
        }
    }

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded("sophisticatedbackpacks")) {
            init();
        }
    }
}
