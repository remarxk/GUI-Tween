package com.remarxk.guitween.compat;

import com.remarxk.guitween.GUITweenUtility;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;

@Mod.EventBusSubscriber
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
