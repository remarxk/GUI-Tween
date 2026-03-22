package com.remarxk.guitween.client.eventListener;

import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.mixinAccess.AbstractContainerScreenMixinAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.joml.Matrix3x2fStack;

public class ScreenRenderListener {
//    @SubscribeEvent(priority = EventPriority.HIGHEST)
//    public static void onRenderBackground(ContainerScreenEvent.Render.Foreground event) {
//        AbstractContainerScreen<?> containerScreen = event.getContainerScreen();
//
//        if (!(containerScreen instanceof AbstractContainerScreenMixinAccess access)) {
//            return;
//        }
//
//        if (GUITweenUtility.COMPAT_WINDOW.contains(containerScreen.getClass()))
//            return;
//
//        String gUITween$screenName = access.getGUITween$screenName();
//        float gUITween$openTick = access.getGUITween$openTick();
//
//        GUITweenUtility.setOpenScreen(gUITween$screenName, gUITween$openTick);
//
//        if (!GUITweenConfig.isEnableWindow())
//            return;
//
//        if (access.getGUITween$isDisableScreenTween())
//            return;
//
//        float moveProgress = gUITween$openTick / GUITweenConfig.window.moveDuration.get().floatValue();
//        float gradientProgress = gUITween$openTick / GUITweenConfig.window.gradientDuration.get().floatValue();
//
//        if (moveProgress >= 1 && gradientProgress >= 1)
//            return;
//
//        access.setGUITween$inTween(true);
//
//        float dx = TweenUtil.tween(GUITweenConfig.window.moveX.get().floatValue(), 0, moveProgress, GUITweenConfig.window.moveEase.get());
//        float dy = TweenUtil.tween(GUITweenConfig.window.moveY.get().floatValue(), 0, moveProgress, GUITweenConfig.window.moveEase.get());
//
//        PoseStack poseStack = event.getGuiGraphics().pose();
//
//        // 动画变换
//        poseStack.pushPose();
//        poseStack.translate(dx, dy, 0);  // 上移
//
//        float alpha = TweenUtil.tween(0.05f, 1, gradientProgress, GUITweenConfig.window.gradientEase.get());
//        GUITweenUtility.pushAlpha(alpha);
//    }
    
    public static void postRenderScreen(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        if (!(screen instanceof HandledScreen<?> containerScreen)) {
            return;
        }

        if (!(containerScreen instanceof AbstractContainerScreenMixinAccess access)) {
            return;
        }

        Matrix3x2fStack poseStack = drawContext.getMatrices();

        if (GUITweenClient.CONFIG.isEnableDebugWindow()) {
            // 左上角偏移（界面内部）
            int x = access.gUITween$getX() + 12;
            int y = access.gUITween$getY() - 10;

            if ((containerScreen instanceof CreativeInventoryScreen)) {
                y -= 30;
            }

            drawContext.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    access.getGUITween$screenName(),
                    x,
                    y,
                    0xFFFF0000, // 浅灰色
                    false
            );
        }

        if (access.getGUITween$inTween()) {
            GUITweenUtility.popAlpha();

            poseStack.popMatrix();
        }

        access.setGUITween$inTween(false);

        access.setGUITween$openTick(access.getGUITween$openTick() + GUITweenUtility.getDeltaTicks());
    }
}
