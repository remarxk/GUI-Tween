package com.remarxk.guitween.client.eventListener;

import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.compat.CompatUtility;
import com.remarxk.guitween.client.mixinAccess.AbstractContainerScreenMixinAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;

public class ScreenRenderListener {
    public static void postRenderScreen(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        if (!(screen instanceof HandledScreen<?> containerScreen)) {
            return;
        }

        if (!(containerScreen instanceof AbstractContainerScreenMixinAccess access)) {
            return;
        }

        MatrixStack poseStack = drawContext.getMatrices();

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

            poseStack.pop();

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

            GUITweenUtility.openScreenTick = Math.min(GUITweenClient.CONFIG.getWindowTotalDuration(), GUITweenUtility.openScreenTick + deltaTicks);
            GUITweenUtility.jeiOpenTick = Math.min(GUITweenClient.CONFIG.getJeiTotalDuration(), GUITweenUtility.jeiOpenTick + deltaTicks);
        }
    }

    public static void postScreenTick(Screen screen) {
        if (!(screen instanceof HandledScreen<?> containerScreen)) {
            return;
        }

        if (!(containerScreen instanceof AbstractContainerScreenMixinAccess access)) {
            return;
        }

        if (access.gUITween$getNeedClose()) {
            screen.close();
        }
    }
}
