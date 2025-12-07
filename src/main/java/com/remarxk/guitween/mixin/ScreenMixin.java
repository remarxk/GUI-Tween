package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenAPI;
import com.remarxk.guitween.util.Ease;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractContainerEventHandler implements Renderable {
    @Unique
    private long gUITween$openTick;

    @Unique private static final float gUITween$MOVE_Y = 20f;

    @Unique
    private float gUITween$getProgress() {
        return Math.min((float) gUITween$openTick / GUITween.CONFIG.windowDuration, 1f);
    }

    @Inject(method = "init()V", at = @At("HEAD"))
    public void initMixin(CallbackInfo ci){
        gUITween$openTick = 0;
    }

    @Inject(method = "renderBackground", at = @At("TAIL"))
    public void renderBackgroundAfter(GuiGraphics pGuiGraphics, CallbackInfo ci){
        Object instance = this;

        if (!(instance instanceof AbstractContainerScreen<?>))
            return;

        if (!GUITween.CONFIG.enable)
            return;

        float t = gUITween$getProgress();
        gUITween$openTick++;

        if (t >= 1)
            return;

        float dy = TweenUtil.tween(gUITween$MOVE_Y, 0, t, GUITween.CONFIG.windowEase.get());

        PoseStack poseStack = pGuiGraphics.pose();

        // 动画变换
        poseStack.pushPose();
        poseStack.translate(0, dy, 0);  // 上移

        float alpha = TweenUtil.tween(0, 1, t, GUITween.CONFIG.windowEase.get());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        pGuiGraphics.setColor(1f, 1f, 1f, alpha);
    }
}
