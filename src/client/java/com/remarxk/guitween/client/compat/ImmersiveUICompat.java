package com.remarxk.guitween.client.compat;

import net.fabricmc.loader.api.FabricLoader;

public class ImmersiveUICompat {
    public static boolean isLoaded = false;

    public static void commonSetup() {
        if (FabricLoader.getInstance().isModLoaded("immersiveui")) {
            isLoaded = true;
        }
    }
}
