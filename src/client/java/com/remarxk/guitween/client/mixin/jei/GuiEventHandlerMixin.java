package com.remarxk.guitween.client.mixin.jei;

import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.compat.CompatUtility;
import com.remarxk.guitween.client.mixinAccess.AbstractContainerScreenMixinAccess;
import mezz.jei.gui.events.GuiEventHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiEventHandler.class)
public class GuiEventHandlerMixin {
    @Unique
    private float gUITween$dx;
    @Unique
    private float gUITween$dy;

    @Inject(
            method = "drawForContainerScreen",
            at = @At(
                    value = "HEAD"
            )
    )
    private void drawMainContentsBefore(HandledScreen<?> screen, DrawContext guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (!(screen instanceof AbstractContainerScreenMixinAccess access)) {
            return;
        }

        CompatUtility.OpenTween openTween = CompatUtility.getOpenTween();

        if (openTween.inTween) {
            GUITweenUtility.popAlpha();

            gUITween$dx = openTween.dx;
            gUITween$dy = openTween.dy;

            // 取消动画
            Matrix3x2fStack matrix3x2fStack = guiGraphics.getMatrices();
            matrix3x2fStack.translate(-gUITween$dx, -gUITween$dy);
        }
    }

    @Inject(
            method = "drawForContainerScreen",
            at = @At(
                    value = "TAIL"
            )
    )
    private void drawMainContentsAfter(HandledScreen<?> screen, DrawContext guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (!(screen instanceof AbstractContainerScreenMixinAccess access)) {
            return;
        }

        CompatUtility.OpenTween openTween = CompatUtility.getOpenTween();

        if (openTween.inTween) {
            Matrix3x2fStack poseStack = guiGraphics.getMatrices();

            // 还原动画
            poseStack.translate(gUITween$dx, gUITween$dy);

            GUITweenUtility.pushAlpha(openTween.alpha);
        }
    }
}
