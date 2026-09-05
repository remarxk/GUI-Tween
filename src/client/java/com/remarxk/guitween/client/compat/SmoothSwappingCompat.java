package com.remarxk.guitween.client.compat;

import net.fabricmc.loader.api.FabricLoader;

public class SmoothSwappingCompat {
    public static boolean isLoaded = false;

    public static void init() {
        isLoaded = FabricLoader.getInstance().isModLoaded("smoothswapping");
    }
}
