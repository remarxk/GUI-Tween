package com.remarxk.guitween.eventListener;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.compat.CompatUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.event.PostScreenTickEvent;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.joml.Matrix3x2fStack;

@EventBusSubscriber(modid = GUITween.MODID, value = Dist.CLIENT)
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
        Matrix3x2fStack poseStack = guiGraphics.pose();

        if (GUITweenConfig.isEnableDebugWindow()) {
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

            float openTick = Mth.clamp(access.getGUITween$openTick() + deltaTicks, 0, GUITweenConfig.getWindowTotalDuration());
            access.setGUITween$openTick(openTick);
            GUITweenUtility.openScreenTick = openTick;

            GUITweenUtility.jeiOpenTick = Mth.clamp(GUITweenUtility.jeiOpenTick + deltaTicks, 0, GUITweenConfig.getJeiTotalDuration());
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
