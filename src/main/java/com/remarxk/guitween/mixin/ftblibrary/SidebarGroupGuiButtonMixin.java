package com.remarxk.guitween.mixin.ftblibrary;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.compat.CompatUtility;
import dev.ftb.mods.ftblibrary.sidebar.SidebarGroupGuiButton;
import net.minecraft.client.gui.GuiGraphics;
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
    private void renderWidgetBefore(GuiGraphics graphics, int mx, int my, float partialTicks, CallbackInfo ci) {
        PoseStack poseStack = graphics.pose();

        CompatUtility.OpenTween openTween = CompatUtility.getOpenTween();
        if (openTween.inTween) {
            poseStack.translate(-openTween.dx, -openTween.dy, 0);
            GUITweenUtility.popAlpha();
        }

        CompatUtility.JeiTween jeiTween = CompatUtility.getJeiLeftTween();
        if (jeiTween.inTween) {
            poseStack.pushPose();

            poseStack.translate(jeiTween.dx, jeiTween.dy, 0);
        }
    }

    /*@Inject(
            method = "renderSidebarButtons",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
                    ordinal = 0
            )
    )
    private void restoreInPush(GuiGraphics graphics, int mx, int my, CallbackInfo ci) {
        if (gUITween$inPush) {
            graphics.pose().popPose();
            gUITween$inPush = false;
        }
    }

    @Inject(
            method = "renderSidebarButtons",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    private void setInPush(GuiGraphics graphics, int mx, int my, CallbackInfo ci) {
        gUITween$inPush = true;
    }

    @Redirect(
            method = "renderSidebarButtons",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V",
                    ordinal = 1
            )
    )
    private void restorePop(PoseStack instance) {
        if (gUITween$inPush) {
            instance.popPose();
            gUITween$inPush = false;
        }
    }*/

    @Inject(
            method = "renderWidget",
            at = @At(
                    value = "RETURN"
            )
    )
    private void renderWidgetAfter(GuiGraphics graphics, int mx, int my, float partialTicks, CallbackInfo ci) {
        PoseStack poseStack = graphics.pose();

        CompatUtility.JeiTween jeiTween = CompatUtility.getJeiLeftTween();
        if (jeiTween.inTween) {
            poseStack.popPose();
        }

        CompatUtility.OpenTween openTween = CompatUtility.getOpenTween();
        if (openTween.inTween) {
            poseStack.translate(openTween.dx, openTween.dy, 0);
            GUITweenUtility.pushAlpha(openTween.alpha);
        }
    }
}
