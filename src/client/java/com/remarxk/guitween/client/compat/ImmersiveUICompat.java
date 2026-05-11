package com.remarxk.guitween.client.compat;

import it.hurts.octostudios.immersiveui.ImmersiveUI;
import net.fabricmc.loader.api.FabricLoader;

public class ImmersiveUICompat {
    public static boolean isLoaded = false;

    public static void commonSetup() {
        if (FabricLoader.getInstance().isModLoaded(ImmersiveUI.MOD_ID)) {
            isLoaded = true;
        }
    }
}
