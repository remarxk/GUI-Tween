package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.HotbarChangeListener;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "renderSlot", at = @At("HEAD"))
    public void renderHotbarBefore(GuiGraphics guiGraphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack stack, int seed, CallbackInfo ci){
        if (!GUITweenConfig.enable.get())
            return;

        if (HotbarChangeListener.lastSelected + 1 != seed)
            return;

        float centerX = x + 8;
        float centerY = y + 8;

        float scale;
        if (HotbarChangeListener.animTick < GUITweenConfig.holdItemScaleDuration.get()) {
            float progress = (float) HotbarChangeListener.animTick / GUITweenConfig.holdItemScaleDuration.get();
            scale = TweenUtil.tween(1, GUITweenConfig.holdItemScale.get().floatValue(), progress, GUITweenConfig.holdItemScaleEase.get());
        }
        else {
            float progress = (float) (HotbarChangeListener.animTick - GUITweenConfig.holdItemScaleDuration.get()) / GUITweenConfig.holdItemRestoreDuration.get();
            scale = TweenUtil.tween(GUITweenConfig.holdItemScale.get().floatValue(), 1, progress, GUITweenConfig.holdItemRestoreEase.get());
        }

        HotbarChangeListener.animTick++;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, 0);
        poseStack.scale(scale, scale, 1.0F);
        poseStack.translate(-centerX, -centerY, 0);
    }

    @Inject(method = "renderSlot", at = @At("TAIL"))
    public void renderHotbarAfter(GuiGraphics guiGraphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack stack, int seed, CallbackInfo ci){
        if (!GUITweenConfig.enable.get())
            return;

        if (HotbarChangeListener.lastSelected + 1 != seed)
            return;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.popPose();
    }
}
