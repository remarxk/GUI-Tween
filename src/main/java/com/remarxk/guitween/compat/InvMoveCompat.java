package com.remarxk.guitween.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.util.TweenUtil;
import me.pieking1215.invmove.InvMove;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.joml.Matrix3x2fStack;

@EventBusSubscriber
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

        if (!GUITweenConfig.isEnableWindow())
            return;

        if (access.getGUITween$isDisableScreenTween())
            return;

        float moveProgress = gUITween$openTick / GUITweenConfig.window.moveDuration.get().floatValue();
        float gradientProgress = gUITween$openTick / GUITweenConfig.window.gradientDuration.get().floatValue();

        if (moveProgress >= 1 && gradientProgress >= 1)
            return;

        access.setGUITween$inTween(true);

        float dx = TweenUtil.tween(GUITweenConfig.window.moveX.get().floatValue(), 0, moveProgress, GUITweenConfig.window.moveEase.get());
        float dy = TweenUtil.tween(GUITweenConfig.window.moveY.get().floatValue(), 0, moveProgress, GUITweenConfig.window.moveEase.get());

        Matrix3x2fStack poseStack = event.getGuiGraphics().pose();

        // 动画变换
        poseStack.pushMatrix();
        poseStack.translate(dx, dy);  // 上移

        float alpha = TweenUtil.tween(0.05f, 1, gradientProgress, GUITweenConfig.window.gradientEase.get());
        GUITweenUtility.pushAlpha(alpha);
    }
}
