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
import net.minecraft.util.math.MathHelper;

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

        float sign = access.gUITween$inCloseTween() ? -GUITweenClient.CONFIG.closeWindowSpeed : 1;
        float openTick = MathHelper.clamp(access.getGUITween$openTick() + sign * GUITweenUtility.getDeltaTicks(),0, GUITweenClient.CONFIG.getWindowTotalDuration());
        access.setGUITween$openTick(openTick);

        if (sign < 0 && openTick <= 0) {
            access.gUITween$setNeedClose(true);
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
