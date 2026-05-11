package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.util.TweenUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
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

@Mixin(ChatHud.class)
public abstract class ChatComponentMixin {
    @Unique
    private float gUITween$newMessageTick;

    @Unique
    private boolean gUITween$inTween;

    @Unique
    private ChatHudLine.Visible gUITween$lastNewLine;

    @Final
    @Shadow
    private List<ChatHudLine.Visible> visibleMessages;

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
    public void renderBefore(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        if (!GUITweenClient.CONFIG.isEnableChatComp())
            return;

        if (visibleMessages.isEmpty() || gUITween$lastNewLine == visibleMessages.getFirst()) {
            return;
        }

        gUITween$lastNewLine = visibleMessages.getFirst();
        gUITween$newMessageTick = 0;
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V",
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void renderMessage(DrawContext guiGraphics, int tickCount, int mouseX, int mouseY, boolean focused, CallbackInfo ci, int i, int j, float f, int k, int l, int i1, int j1, double d0, double d1, double d2, int k1, int l1, int i2, int j2, int k2, ChatHudLine.Visible line, int l2, double d3, int j3, int k3) {
        if (!GUITweenClient.CONFIG.isEnableChatComp())
            return;

        if (k2 != 0)
            return;

        if (line != gUITween$lastNewLine)
            return;

        if (gUITween$newMessageTick > GUITweenClient.CONFIG.getChatCompMaxDuration())
            return;

        gUITween$inTween = true;

        MatrixStack poseStack = guiGraphics.getMatrices();
        poseStack.push();

        float progress = gUITween$newMessageTick / GUITweenClient.CONFIG.chatCompMoveDuration;
//        float moveX = GUITweenClient.CONFIG.chat.compMoveX;
//        float moveY = GUITweenClient.CONFIG.chat.compMoveY;
        float moveX = -getWidth();
        float moveY = 0;
        float dx = TweenUtil.tween(moveX, 0, progress, GUITweenClient.CONFIG.chatCompMoveEase.get());
        float dy = TweenUtil.tween(moveY, 0, progress, GUITweenClient.CONFIG.chatCompMoveEase.get());
        poseStack.translate(dx, dy, 0);
    }

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V",
                    ordinal = 0
            ),
            index = 4
    )
    public int modifyBgAlpha(int color) {
        if (!gUITween$inTween)
            return color;

        int originAlpha = (color >> 24) & 0xFF;
        int rgb   = color & 0x00FFFFFF;

        float progress = gUITween$newMessageTick / GUITweenClient.CONFIG.chatCompGradientDuration;
        float alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, progress, GUITweenClient.CONFIG.chatCompGradientEase.get());

        int newAlpha = (int)(alpha * originAlpha);

        return (newAlpha << 24) | rgb;
    }

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"
            ),
            index = 4
    )
    public int modifyStringAlpha(int color) {
        if (!gUITween$inTween)
            return color;

        int originAlpha = (color >> 24) & 0xFF;
        int rgb   = color & 0x00FFFFFF;

        float progress = gUITween$newMessageTick / GUITweenClient.CONFIG.chatCompGradientDuration;
        float alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, progress, GUITweenClient.CONFIG.chatCompGradientEase.get());

        int newAlpha = (int)(alpha * originAlpha);

        return (newAlpha << 24) | rgb;
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    public void renderAfter(DrawContext guiGraphics, int tickCount, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        if (gUITween$inTween) {
            gUITween$newMessageTick += GUITweenUtility.getDeltaTicks();
            gUITween$inTween = false;

            guiGraphics.getMatrices().pop();
        }
    }
}
