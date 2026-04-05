package com.remarxk.guitween.eventListener;

import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.compat.CompatUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.util.DebugUtil;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2fStack;

public class ScreenRenderListener {
    public static void postRenderBackground(Screen screen, GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float tickDelta) {
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen))
            return;

        if (!(containerScreen instanceof AbstractContainerScreenMixinAccess access)) {
            return;
        }

        String gUITween$screenName = access.getGUITween$screenName();
        float gUITween$openTick = access.getGUITween$openTick();

        GUITweenUtility.setOpenScreen(gUITween$screenName, gUITween$openTick);

        if (!GUITweenConfig.isEnableWindow())
            return;

        if (access.getGUITween$isDisableScreenTween())
            return;

        float moveProgress = gUITween$openTick / GUITweenConfig.windowMoveDuration();
        float gradientProgress = gUITween$openTick / GUITweenConfig.windowGradientDuration();

        if (moveProgress >= 1 && gradientProgress >= 1)
            return;

        access.setGUITween$inTween(true);

        float dx = TweenUtil.tween(GUITweenConfig.windowMoveX(), 0, moveProgress, GUITweenConfig.windowMoveEase());
        float dy = TweenUtil.tween(GUITweenConfig.windowMoveY(), 0, moveProgress, GUITweenConfig.windowMoveEase());

        Matrix3x2fStack poseStack = drawContext.pose();

        // 动画变换
        poseStack.pushMatrix();
        poseStack.translate(dx, dy);  // 上移

        float alpha = TweenUtil.tween(0.05f, 1, gradientProgress, GUITweenConfig.windowGradientEase());
        GUITweenUtility.pushAlpha(alpha);
    }
    
    public static void postRenderScreen(Screen screen, GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float tickDelta) {
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
            return;
        }

        if (!(containerScreen instanceof AbstractContainerScreenMixinAccess access)) {
            return;
        }

        Matrix3x2fStack poseStack = drawContext.pose();

        if (GUITweenConfig.isEnableDebugWindow()) {
            // 左上角偏移（界面内部）
            int x = access.gUITween$getGuiLeft() + 12;
            int y = access.gUITween$getGuiTop() - 10;

            if ((containerScreen instanceof CreativeModeInventoryScreen)) {
                y -= 30;
            }

            drawContext.text(
                    Minecraft.getInstance().font,
                    access.getGUITween$screenName(),
                    x,
                    y,
                    0xFFFF0000, // 浅灰色
                    false
            );
        }

        if (access.getGUITween$inTween()) {
            GUITweenUtility.popAlpha();
            GUITweenUtility.enablePictureMatrix = false;

            poseStack.popMatrix();

            CompatUtility.endOpenTween();
        }

        access.setGUITween$inTween(false);

        float sign = access.gUITween$inCloseTween() ? -GUITweenConfig.closeWindowSpeed() : 1;
        float openTick = Mth.clamp(access.getGUITween$openTick() + sign * GUITweenUtility.getDeltaTicks(),0, GUITweenConfig.getWindowTotalDuration());
        access.setGUITween$openTick(openTick);

        if (sign < 0 && openTick <= 0) {
            access.gUITween$setNeedClose(true);
        }
    }

    public static void postScreenTick(Screen screen) {
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
            return;
        }

        if (!(containerScreen instanceof AbstractContainerScreenMixinAccess access)) {
            return;
        }

        if (access.gUITween$getNeedClose()) {
            screen.onClose();
        }
    }
}
