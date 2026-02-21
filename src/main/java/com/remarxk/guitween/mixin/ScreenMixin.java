package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractContainerEventHandler implements Renderable, AbstractContainerScreenMixinAccess {
    @Unique
    private String gUITween$screenName;

    @Unique
    private boolean gUITween$isDisableScreenTween;

    @Unique
    private boolean gUITween$compatStop;

    @Unique
    private float gUITween$openTick;

    @Unique
    private boolean gUiTween$inTween;

    @Override
    public String getGUITween$screenName() {
        return gUITween$screenName;
    }

    @Override
    public boolean getGUITween$isDisableScreenTween() {
        return gUITween$isDisableScreenTween;
    }

    @Override
    public boolean getGUITween$inTween() {
        return gUiTween$inTween;
    }

    @Override
    public void setGUITween$inTween(boolean inTween) {
        gUiTween$inTween = inTween;
    }

    @Override
    public float getGUITween$openTick() {
        return gUITween$openTick;
    }

    @Override
    public void setGUITween$openTick(float openTick) {
        gUITween$openTick = openTick;
    }

    @Inject(
            method = "init(Lnet/minecraft/client/Minecraft;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;init()V"
            )
    )
    public void initBefore(Minecraft pMinecraft, int pWidth, int pHeight, CallbackInfo ci) {
        gUITween$openTick = 0;

        gUITween$screenName = getClass().getSimpleName();
        gUITween$isDisableScreenTween = GUITween.CONFIG.isDisableTweenWindow(gUITween$screenName);
    }

    @Inject(method = "renderBackground", at = @At("TAIL"))
    public void renderBackgroundAfter(GuiGraphics pGuiGraphics, CallbackInfo ci){
        Object instance = this;

        if (!(instance instanceof AbstractContainerScreen<?>))
            return;

        if (GUITweenUtility.COMPAT_WINDOW.contains(getClass()))
            return;

        if (gUiTween$inTween) { // 某些界面重写了render方法，导致没有取消渲染动画，需要强行终止
            gUITween$compatStop = true;
            gUiTween$inTween = false;
            gUITween$isDisableScreenTween = true;

            GUITweenUtility.popAlpha();
            pGuiGraphics.pose().popPose();
        }

        if (gUITween$compatStop) {
            gUITween$openTick += GUITweenUtility.getDeltaTicks();
        }

        GUITweenUtility.setOpenScreen(gUITween$screenName, gUITween$openTick);

        if (!GUITween.CONFIG.isEnableWindow())
            return;

        if (GUITween.CONFIG.isDisableTweenWindow(getClass().getSimpleName()))
            return;

        float moveProgress = gUITween$openTick / GUITween.CONFIG.windowMoveDuration;
        float gradientProgress = gUITween$openTick / GUITween.CONFIG.windowGradientDuration;

        if (moveProgress >= 1 && gradientProgress >= 1)
            return;

        gUITween$openTick += GUITweenUtility.getDeltaTicks();

        float dx = TweenUtil.tween(GUITween.CONFIG.windowMoveX, 0, moveProgress, GUITween.CONFIG.windowMoveEase.get());
        float dy = TweenUtil.tween(GUITween.CONFIG.windowMoveY, 0, moveProgress, GUITween.CONFIG.windowMoveEase.get());

        PoseStack poseStack = pGuiGraphics.pose();

        // 动画变换
        poseStack.pushPose();
        poseStack.translate(dx, dy, 0);  // 上移

        float alpha = TweenUtil.tween(0.05f, 1, gradientProgress, GUITween.CONFIG.windowGradientEase.get());
        GUITweenUtility.pushAlpha(alpha);
    }
}
