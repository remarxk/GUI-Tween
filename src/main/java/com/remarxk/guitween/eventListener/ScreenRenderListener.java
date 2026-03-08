package com.remarxk.guitween.eventListener;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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

        if (GUITweenUtility.COMPAT_WINDOW.contains(containerScreen.getClass()))
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

        PoseStack poseStack = event.getGuiGraphics().pose();

        // 动画变换
        poseStack.pushPose();
        poseStack.translate(dx, dy, 0);  // 上移

        float alpha = TweenUtil.tween(0.05f, 1, gradientProgress, GUITween.CONFIG.windowGradientEase.get());
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

        if (!access.getGUITween$inTween())
            return;

        GuiGraphics guiGraphics = event.getGuiGraphics();

        if (access.getGUITween$inTween()) {
            GUITweenUtility.popAlpha();

            PoseStack poseStack = guiGraphics.pose();
            poseStack.popPose();
        }

        access.setGUITween$inTween(false);

        access.setGUITween$openTick(access.getGUITween$openTick() + GUITweenUtility.getDeltaTicks());
    }
}
