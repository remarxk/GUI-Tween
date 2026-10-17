package com.remarxk.guitween.client.compat;

import net.fabricmc.loader.api.FabricLoader;

public class SmoothSwappingCompat {
    public static boolean isLoaded = false;

    public static void commonSetup() {
        if (FabricLoader.getInstance().isModLoaded("smoothswapping")) {
            isLoaded = true;
        }
    }
}
