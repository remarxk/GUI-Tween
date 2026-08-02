package com.remarxk.guitween.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.anim.TooltipTween;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(GuiGraphics.class)
public class GuiGraphicMixin {

    /*@Inject(
            method = "renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawManaged(Ljava/lang/Runnable;)V")
    )
    private void gUITween$initTooltipValue(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY, ClientTooltipPositioner tooltipPositioner, CallbackInfo ci, @Local(ordinal = 4) int i, @Local(ordinal = 5) int j, @Local(ordinal = 6) int l, @Local(ordinal = 7) int i1) {
        TooltipTween tween = GUITweenUtility.getTooltipTween();
        if (!tween.inTween) return;

        int x = l;
        int y = i1;
        int width = i;
        int height = j;

        tween.updateSize(x, y, width, height, mouseX);
    }

    @ModifyArgs(
            method = "lambda$renderTooltipInternal$3",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderTooltipBackground(Lnet/minecraft/client/gui/GuiGraphics;IIIIIIIII)V")
    )
    private void gUITween$renderTooltipBackground(Args args) {
        TooltipTween tween = GUITweenUtility.getTooltipTween();
        if (!tween.inTween)
            return;

        int x = tween.getX();
        int width = tween.getWidth();
        int height = tween.getHeight();

        if (tween.isLeftSide()) {
            args.set(1, x + (int) tween.getRenderWidth() - width);
        }
        else {
            args.set(1, x);
        }
        args.set(2, tween.getY());
        args.set(3, width);
        args.set(4, height);

        tween.lastZ = args.get(5);
        tween.lastBgColor = args.get(6);
        tween.lastBorderTop = args.get(7);
        tween.lastBorderBottom = args.get(8);
        tween.lastBorderCenter = args.get(9);
    }

    @Inject(
            method = "renderTooltipInternal",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawManaged(Ljava/lang/Runnable;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void gUITween$enableScissorTooltip(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY, ClientTooltipPositioner tooltipPositioner, CallbackInfo ci, @Local(ordinal = 6) int l, @Local(ordinal = 7) int i1) {
        TooltipTween tween = GUITweenUtility.getTooltipTween();
        if (!tween.inTween) return;

        int x = tween.getX();
        int y = tween.getY();

        int curWidth = tween.getWidth();
        int curHeight = tween.getHeight();

        GuiGraphics self = (GuiGraphics) (Object) this;
        if (tween.isLeftSide()) {
            int minX = x + tween.targetWidth - curWidth;
            self.enableScissor(minX, y, minX + curWidth, y + curHeight);
        }
        else {
            self.enableScissor(x, y, x + curWidth, y + curHeight);
        }
    }

    @ModifyArgs(
            method = "renderTooltipInternal",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;renderText(Lnet/minecraft/client/gui/Font;IILorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V"
            )
    )
    private void gUITween$modifyTooltipRenderText(Args args) {
        TooltipTween tween = GUITweenUtility.getTooltipTween();
        if (tween.inTween) {
            int curWidth = tween.getWidth();
            if (tween.isLeftSide()) {
                args.set(1, tween.getX() + tween.targetWidth - curWidth);
            }
            else {
                args.set(1, tween.getX());
            }

            int curY = args.get(2);
            int delY = curY - tween.targetY;
            args.set(2, tween.getY() + delY);
        }
    }

    @ModifyArgs(
            method = "renderTooltipInternal",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;renderImage(Lnet/minecraft/client/gui/Font;IILnet/minecraft/client/gui/GuiGraphics;)V"
            )
    )
    private void gUITween$modifyTooltipRenderImage(Args args) {
        TooltipTween tween = GUITweenUtility.getTooltipTween();
        if (tween.inTween) {
            int curWidth = tween.getWidth();
            if (tween.isLeftSide()) {
                args.set(1, tween.getX() + tween.targetWidth - curWidth);
            }
            else {
                args.set(1, tween.getX());
            }

            int curY = args.get(2);
            int delY = curY - tween.targetY;
            args.set(2, tween.getY() + delY);
        }
    }

    @Inject(
            method = "renderTooltipInternal",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"
            )
    )
    private void gUITween$disableScissorTooltip(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY, ClientTooltipPositioner tooltipPositioner, CallbackInfo ci) {
        TooltipTween tween = GUITweenUtility.getTooltipTween();
        if (!tween.inTween)
            return;

        GuiGraphics self = (GuiGraphics) (Object) this;
        self.bufferSource().endBatch();
        self.disableScissor();
    }*/
}
