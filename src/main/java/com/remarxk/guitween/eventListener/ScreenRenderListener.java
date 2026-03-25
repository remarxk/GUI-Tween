package com.remarxk.guitween.eventListener;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.compat.CompatUtility;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;

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
        }

        if (GUITweenUtility.isCompatWindow(containerScreen.getClass()))
            return;

        String gUITween$screenName = access.getGUITween$screenName();
        float gUITween$openTick = access.getGUITween$openTick();

        GUITweenUtility.setOpenScreen(gUITween$screenName, gUITween$openTick);

        if (!GUITween.CONFIG.isEnableWindow())
            return;

        if (access.getGUITween$isDisableScreenTween())
            return;

        float moveProgress = gUITween$openTick / GUITween.CONFIG.windowMoveDuration;
        float gradientProgress = gUITween$openTick / GUITween.CONFIG.windowGradientDuration;

        if (moveProgress >= 1 && gradientProgress >= 1)
            return;

        access.setGUITween$inTween(true);

        float dx = TweenUtil.tween(GUITween.CONFIG.windowMoveX, 0, moveProgress, GUITween.CONFIG.windowMoveEase.get());
        float dy = TweenUtil.tween(GUITween.CONFIG.windowMoveY, 0, moveProgress, GUITween.CONFIG.windowMoveEase.get());
        float alpha = TweenUtil.tween(0.05f, 1, gradientProgress, GUITween.CONFIG.windowGradientEase.get());
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

        access.setGUITween$openTick(access.getGUITween$openTick() + GUITweenUtility.getDeltaTicks());
    }
}
