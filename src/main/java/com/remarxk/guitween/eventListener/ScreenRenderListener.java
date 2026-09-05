package com.remarxk.guitween.eventListener;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.compat.CompatUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.event.PostScreenTickEvent;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ScreenRenderListener {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderBackground(ScreenEvent.BackgroundRendered event) {
        Screen screen = event.getScreen();

        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
            return;
        }

        if (!(containerScreen instanceof AbstractContainerScreenMixinAccess access)) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();

        if (access.getGUITween$inTween()) { // 某些界面重写了render方法，导致没有取消渲染动画，需要强行终止
            access.setGUITween$inTween(false);
            access.setGUITween$isDisableScreenTween(true);

            GUITweenUtility.popAlpha();
            guiGraphics.pose().popPose();

            CompatUtility.endOpenTween();
        }

        if (GUITweenUtility.isCompatWindow(containerScreen.getClass()))
            return;

        String gUITween$screenName = access.getGUITween$screenName();

        GUITweenUtility.setOpenScreen(gUITween$screenName, GUITweenUtility.openScreenTick);
        GUITweenUtility.jeiOpenTick = Math.max(GUITweenUtility.jeiOpenTick, GUITweenUtility.openScreenTick);

        boolean closing = access.gUITween$inCloseTween();
        GUITweenUtility.isWindowClosing = closing;
        if (!GUITween.CONFIG.isEnableWindow() && !closing)
            return;

        if (access.getGUITween$isDisableScreenTween())
            return;

        float dx;
        float dy;
        float alpha;
        float moveProgress;
        float gradientProgress;

        if (closing) {
            // 独立的关闭动画：从居中位置向 closeMoveX/Y 移动，渐变 alpha 从 1 到 0
            float total = GUITween.CONFIG.getCloseWindowTotalDuration();
            float elapsed = Math.max(0, total - GUITweenUtility.closeScreenTick);
            moveProgress = GUITween.CONFIG.closeMoveDuration <= 0
                    ? 1
                    : Math.min(1, elapsed / GUITween.CONFIG.closeMoveDuration);
            gradientProgress = GUITween.CONFIG.closeGradientDuration <= 0
                    ? 1
                    : Math.min(1, elapsed / GUITween.CONFIG.closeGradientDuration);

            dx = TweenUtil.tween(0, GUITween.CONFIG.closeMoveX, moveProgress, GUITween.CONFIG.closeMoveEase.get());
            dy = TweenUtil.tween(0, GUITween.CONFIG.closeMoveY, moveProgress, GUITween.CONFIG.closeMoveEase.get());
            alpha = TweenUtil.tween(1, 0, gradientProgress, GUITween.CONFIG.closeGradientEase.get());
        }
        else {
            moveProgress = GUITweenUtility.openScreenTick / GUITween.CONFIG.windowMoveDuration;
            gradientProgress = GUITweenUtility.openScreenTick / GUITween.CONFIG.windowGradientDuration;

            if (moveProgress >= 1 && gradientProgress >= 1)
                return;

            dx = TweenUtil.tween(GUITween.CONFIG.windowMoveX, 0, moveProgress, GUITween.CONFIG.windowMoveEase.get());
            dy = TweenUtil.tween(GUITween.CONFIG.windowMoveY, 0, moveProgress, GUITween.CONFIG.windowMoveEase.get());
            alpha = TweenUtil.tween(0.05f, 1, gradientProgress, GUITween.CONFIG.windowGradientEase.get());
        }

        access.setGUITween$inTween(true);

        CompatUtility.startOpenTween(dx, dy, alpha);

        PoseStack poseStack = guiGraphics.pose();

        // 动画变换
        poseStack.pushPose();
        poseStack.translate(dx, dy, 0);  // 上移

        GUITweenUtility.pushAlpha(alpha);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void postRenderScreen(ScreenEvent.Render.Post event) {
        Screen screen = event.getScreen();

        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
            return;
        }

        if (!(containerScreen instanceof AbstractContainerScreenMixinAccess access)) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();

        if (GUITween.CONFIG.isEnableDebugWindow()) {
            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(0, 0, 1000);

            // 左上角偏移（界面内部）
            int x = containerScreen.getGuiLeft() + 12;
            int y = containerScreen.getGuiTop() - 10;

            if ((containerScreen instanceof CreativeModeInventoryScreen)) {
                y -= 30;
            }

            guiGraphics.drawString(
                    Minecraft.getInstance().font,
                    access.getGUITween$screenName(),
                    x,
                    y,
                    0xFF0000, // 浅灰色
                    false
            );

            poseStack.popPose();
        }

        if (access.getGUITween$inTween()) {
            GUITweenUtility.popAlpha();

            PoseStack poseStack = guiGraphics.pose();
            poseStack.popPose();

            CompatUtility.endOpenTween();
        }

        access.setGUITween$inTween(false);

        boolean closing = access.gUITween$inCloseTween();
        GUITweenUtility.isWindowClosing = closing;
        float deltaTicks = GUITweenUtility.getDeltaTicks();

        if (closing) {
            // 关闭动画使用独立计时：按真实帧数推进，速度恒为 1（不再有“关闭速度”）
            GUITweenUtility.closeScreenTick = Math.max(0, GUITweenUtility.closeScreenTick - deltaTicks);
            GUITweenUtility.closeJeiTick = Math.max(0, GUITweenUtility.closeJeiTick - deltaTicks);

            if (GUITweenUtility.closeScreenTick <= 0 && GUITweenUtility.closeJeiTick <= 0) {
                access.gUITween$setNeedClose(true);
            }
        }
        else {
            GUITweenUtility.closeScreenTick = 0;
            GUITweenUtility.closeJeiTick = 0;

            GUITweenUtility.openScreenTick = Math.min(GUITween.CONFIG.getWindowTotalDuration(), GUITweenUtility.openScreenTick + deltaTicks);
            GUITweenUtility.jeiOpenTick = Math.min(GUITween.CONFIG.getJeiTotalDuration(), GUITweenUtility.jeiOpenTick + deltaTicks);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void postScreenTick(PostScreenTickEvent event) {
        Screen screen = event.getScreen();

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
