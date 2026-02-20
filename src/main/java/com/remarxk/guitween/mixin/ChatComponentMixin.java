package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.Ease;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {
    @Unique
    private float gUITween$newMessageTick;

    @Unique
    private boolean gUITween$inTween;

    @Unique
    private GuiMessage.Line gUITween$lastNewLine;

    @Final
    @Shadow
    private List<GuiMessage.Line> trimmedMessages;

    @Shadow
    public int getWidth() {
        return 0;
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "HEAD"
            )
    )
    public void renderBefore(GuiGraphics guiGraphics, int tickCount, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        if (!GUITweenConfig.isEnableChatComp())
            return;

        if (trimmedMessages.isEmpty() || gUITween$lastNewLine == trimmedMessages.getFirst()) {
            return;
        }

        gUITween$lastNewLine = trimmedMessages.getFirst();
        gUITween$newMessageTick = 0;
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V",
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void renderMessage(GuiGraphics guiGraphics, int tickCount, int mouseX, int mouseY, boolean focused, CallbackInfo ci, int i, int j, float f, int k, int l, int i1, int j1, double d0, double d1, double d2, int k1, int l1, int i2, int j2, int k2, GuiMessage.Line line, int l2, double d3, int j3, int k3) {
        if (!GUITweenConfig.isEnableChatComp())
            return;

        if (k2 != 0)
            return;

        if (line != gUITween$lastNewLine)
            return;

        if (gUITween$newMessageTick > GUITweenConfig.getChatCompMaxDuration())
            return;

        gUITween$inTween = true;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        float progress = gUITween$newMessageTick / GUITweenConfig.chat.compMoveDuration.get().floatValue();
//        float moveX = GUITweenConfig.chat.compMoveX.get().floatValue();
//        float moveY = GUITweenConfig.chat.compMoveY.get().floatValue();
        float moveX = -getWidth();
        float moveY = 0;
        float dx = TweenUtil.tween(moveX, 0, progress, GUITweenConfig.chat.compMoveEase.get());
        float dy = TweenUtil.tween(moveY, 0, progress, GUITweenConfig.chat.compMoveEase.get());
        poseStack.translate(dx, dy, 0);
    }

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V",
                    ordinal = 0
            ),
            index = 4
    )
    public int modifyBgAlpha(int color) {
        if (!gUITween$inTween)
            return color;

        int originAlpha = (color >> 24) & 0xFF;
        int rgb   = color & 0x00FFFFFF;

        float progress = gUITween$newMessageTick / GUITweenConfig.chat.compGradientDuration.get().floatValue();
        float alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, progress, GUITweenConfig.chat.compGradientEase.get());

        int newAlpha = (int)(alpha * originAlpha);

        return (newAlpha << 24) | rgb;
    }

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I"
            ),
            index = 4
    )
    public int modifyStringAlpha(int color) {
        if (!gUITween$inTween)
            return color;

        int originAlpha = (color >> 24) & 0xFF;
        int rgb   = color & 0x00FFFFFF;

        float progress = gUITween$newMessageTick / GUITweenConfig.chat.compGradientDuration.get().floatValue();
        float alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, progress, GUITweenConfig.chat.compGradientEase.get());

        int newAlpha = (int)(alpha * originAlpha);

        return (newAlpha << 24) | rgb;
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    public void renderAfter(GuiGraphics guiGraphics, int tickCount, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        if (gUITween$inTween) {
            gUITween$newMessageTick += GUITweenUtility.getDeltaTicks();
            gUITween$inTween = false;

            guiGraphics.pose().popPose();
        }
    }
}
