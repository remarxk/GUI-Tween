package com.remarxk.guitween.mixin;

import com.corosus.watut.WatutMod;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import dev.ftb.mods.ftblibrary.FTBLibrary;
import fuzs.overflowingbars.OverflowingBars;
import mezz.jei.api.constants.ModIds;
import net.minecraftforge.fml.loading.LoadingModList;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import org.embeddedt.embeddium.api.EmbeddiumConstants;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class GUITweenMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String s) {

    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("com.remarxk.guitween.mixin.rei")) {
            return modIsLoad("roughlyenoughitems");
        }

        if (mixinClassName.startsWith("com.remarxk.guitween.mixin.emi")) {
            return modIsLoad("emi");
        }

        if (mixinClassName.contains("com.remarxk.guitween.mixin.jei")) {
            return modIsLoad(ModIds.JEI_ID);
        }

        if (mixinClassName.contains("com.remarxk.guitween.mixin.embeddium")) {
            return modIsLoad(EmbeddiumConstants.MODID);
        }

        if (mixinClassName.contains("com.remarxk.guitween.mixin.overflowingbars")) {
            return modIsLoad(OverflowingBars.MOD_ID);
        }

        if (mixinClassName.contains("com.remarxk.guitween.mixin.sophisticated")) {
            return modIsLoad(SophisticatedCore.MOD_ID);
        }

        if (mixinClassName.contains("com.remarxk.guitween.mixin.watut")) {
            return modIsLoad(WatutMod.MODID);
        }

        if (mixinClassName.contains("com.remarxk.guitween.mixin.touhoulittlemaid")) {
            return modIsLoad(TouhouLittleMaid.MOD_ID);
        }

        if (mixinClassName.contains("com.remarxk.guitween.mixin.ftblibrary")) {
            return modIsLoad(FTBLibrary.MOD_ID);
        }

        return true;
    }

    public boolean modIsLoad(String modId) {
        return LoadingModList.get().getModFileById(modId) != null;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }
}
