package com.remarxk.guitween.client.mixin.ftblibrary;

import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.compat.CompatUtility;
import dev.ftb.mods.ftblibrary.sidebar.SidebarGroupGuiButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SidebarGroupGuiButton.class)
public class SidebarGroupGuiButtonMixin {
    @Unique
    private boolean gUITween$inPush;

    @Inject(
            method = "renderWidget",
            at = @At(
                    value = "HEAD"
            )
    )
    private void renderWidgetBefore(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MatrixStack poseStack = context.getMatrices();

        CompatUtility.OpenTween openTween = CompatUtility.getOpenTween();
        if (openTween.inTween) {
            poseStack.translate(-openTween.dx, -openTween.dy, 0);
            GUITweenUtility.popAlpha();
        }

        CompatUtility.JeiTween jeiTween = CompatUtility.getJeiLeftTween();
        if (jeiTween.inTween) {
            poseStack.push();

            poseStack.translate(jeiTween.dx, jeiTween.dy, 0);
        }
    }

    @Inject(
            method = "renderSidebarButtons",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
                    ordinal = 0
            )
    )
    private void restoreInPush(DrawContext graphics, int mx, int my, CallbackInfo ci) {
        if (gUITween$inPush) {
            graphics.getMatrices().pop();
            gUITween$inPush = false;
        }
    }

    @Inject(
            method = "renderSidebarButtons",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    private void setInPush(DrawContext graphics, int mx, int my, CallbackInfo ci) {
        gUITween$inPush = true;
    }

    @Redirect(
            method = "renderSidebarButtons",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V",
                    ordinal = 1
            )
    )
    private void restorePop(MatrixStack instance) {
        if (gUITween$inPush) {
            instance.pop();
            gUITween$inPush = false;
        }
    }

    @Inject(
            method = "renderWidget",
            at = @At(
                    value = "RETURN"
            )
    )
    private void renderWidgetAfter(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MatrixStack poseStack = context.getMatrices();

        CompatUtility.JeiTween jeiTween = CompatUtility.getJeiLeftTween();
        if (jeiTween.inTween) {
            poseStack.pop();
        }

        CompatUtility.OpenTween openTween = CompatUtility.getOpenTween();
        if (openTween.inTween) {
            poseStack.translate(openTween.dx, openTween.dy, 0);
            GUITweenUtility.pushAlpha(openTween.alpha);
        }
    }
}
