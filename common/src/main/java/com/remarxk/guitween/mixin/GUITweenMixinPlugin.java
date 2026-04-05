package com.remarxk.guitween.mixin;

import com.remarxk.guitween.platform.Services;
import mezz.jei.api.constants.ModIds;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class GUITweenMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("com.remarxk.guitween.mixin.rei")) {
            return Services.PLATFORM.isModLoaded("roughlyenoughitems");
        }

        if (mixinClassName.startsWith("com.remarxk.guitween.mixin.emi")) {
            return Services.PLATFORM.isModLoaded("emi");
        }

        if (mixinClassName.contains("com.remarxk.guitween.mixin.jei")) {
            return Services.PLATFORM.isModLoaded(ModIds.JEI_ID);
        }

        if (mixinClassName.contains("com.remarxk.guitween.mixin.sodium")) {
            return Services.PLATFORM.isModLoaded("sodium");
        }

//        if (mixinClassName.contains("com.remarxk.guitween.mixin.overflowingbars")) {
//            return modIsLoad(OverflowingBars.MOD_ID);
//        }
//
//        if (mixinClassName.contains("com.remarxk.guitween.mixin.sophisticated")) {
//            return modIsLoad(SophisticatedCore.MOD_ID);
//        }

//        if (mixinClassName.contains("com.remarxk.guitween.mixin.watut")) {
//            return modIsLoad(WatutMod.MODID);
//        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void postApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void preApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
