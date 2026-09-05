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
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = GUITween.MODID)
public class ScreenRenderListener {
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
        PoseStack poseStack = guiGraphics.pose();

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

            GUITweenUtility.openScreenTick = Math.min(GUITweenConfig.getWindowTotalDuration(), GUITweenUtility.openScreenTick + deltaTicks);
            GUITweenUtility.jeiOpenTick = Math.min(GUITweenConfig.getJeiTotalDuration(), GUITweenUtility.jeiOpenTick + deltaTicks);
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
