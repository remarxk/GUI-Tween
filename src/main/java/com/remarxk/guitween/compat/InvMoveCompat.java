package com.remarxk.guitween.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.util.TweenUtil;
import me.pieking1215.invmove.InvMove;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class InvMoveCompat {
    @SubscribeEvent
    public static void onPreScreenRender(ScreenEvent.Render.Pre event) {
        if (!ModList.get().isLoaded(InvMove.MOD_ID))
            return;

        Screen screen = event.getScreen();

        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
            return;
        }

        if (!(containerScreen instanceof AbstractContainerScreenMixinAccess access)) {
            return;
        }

        if (GUITweenUtility.isCompatWindow(containerScreen.getClass()))
            return;

        if (!InvMove.instance().shouldDisableScreenBackground(screen)) {
            return;
        }

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
}
