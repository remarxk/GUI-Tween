package com.remarxk.guitween.compat;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;

@Mod.EventBusSubscriber(modid = GUITween.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
