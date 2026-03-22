package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.util.TweenUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    @Unique
    private float gUITween$openTick;

    @Unique
    private boolean gUITween$inTween;

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "HEAD"))
    public void initOpenTick(CallbackInfo ci) {
        gUITween$openTick = 0;
    }

    @Unique
    private void gUITween$renderTween(DrawContext guiGraphics) {
        Matrix3x2fStack poseStack = guiGraphics.getMatrices();
        poseStack.pushMatrix();

        float moveProgress = gUITween$openTick / GUITweenClient.CONFIG.chatOpenMoveDuration;
        float dy = TweenUtil.tween(12f, 0, moveProgress, GUITweenClient.CONFIG.chatOpenMoveEase.get());

        float alphaProgress = gUITween$openTick / GUITweenClient.CONFIG.chatOpenGradientDuration;
        float alpha = TweenUtil.tween(0.01f, 1, alphaProgress, GUITweenClient.CONFIG.chatOpenGradientEase.get());

        poseStack.translate(0, dy);

        GUITweenUtility.pushSpriteAlpha(alpha);
        GUITweenUtility.pushFontAlpha(alpha);
    }

    @Unique
    private void gUITween$popTween(DrawContext guiGraphics) {
        guiGraphics.getMatrices().popMatrix();

        GUITweenUtility.popSpriteAlpha();
        GUITweenUtility.popFontAlpha();
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "HEAD"
            ),
            remap = false
    )
    public void renderBefore(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (!GUITweenClient.CONFIG.isEnableChat())
            return;

        if (gUITween$openTick > GUITweenClient.CONFIG.getChatOpenMaxDuration())
            return;

        gUITween$inTween = true;
        gUITween$renderTween(context);
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V",
                    shift = At.Shift.AFTER
            )
    )
    public void renderBgAfter(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (!gUITween$inTween)
            return;

        gUITween$popTween(context);
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;IIIZZ)V",
                    shift = At.Shift.AFTER
            )
    )
    private void renderInputBefore(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (!gUITween$inTween)
            return;

        gUITween$renderTween(context);
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    public void renderAfter(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (!gUITween$inTween)
            return;

        gUITween$popTween(context);
        gUITween$inTween = false;
        gUITween$openTick += GUITweenUtility.getDeltaTicks();
    }
}
