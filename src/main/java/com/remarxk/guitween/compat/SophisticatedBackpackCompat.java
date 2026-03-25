package com.remarxk.guitween.compat;

import com.remarxk.guitween.GUITweenUtility;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;

@EventBusSubscriber
public class SophisticatedBackpackCompat {
    public static void init() {
        if (ModList.get().isLoaded(SophisticatedCore.MOD_ID)) {
            GUITweenUtility.addCompatWindow(StorageScreenBase.class::isAssignableFrom);
        }
    }

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded(SophisticatedCore.MOD_ID)) {
            init();
        }
    }
}
