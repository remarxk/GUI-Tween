package com.remarxk.guitween.mixin;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
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

    protected ChatScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "HEAD"))
    public void initOpenTick(CallbackInfo ci) {
        gUITween$openTick = 0;
    }

    @Unique
    private void gUITween$renderTween(GuiGraphics guiGraphics) {
        Matrix3x2fStack poseStack = guiGraphics.pose();
        poseStack.pushMatrix();

        float moveProgress = gUITween$openTick / GUITweenConfig.chat.openMoveDuration.get().floatValue();
        float dy = TweenUtil.tween(12f, 0, moveProgress, GUITweenConfig.chat.openMoveEase.get());

        float alphaProgress = gUITween$openTick / GUITweenConfig.chat.openGradientDuration.get().floatValue();
        float alpha = TweenUtil.tween(0.01f, 1, alphaProgress, GUITweenConfig.chat.openGradientEase.get());

        poseStack.translate(0, dy);

        GUITweenUtility.pushSpriteAlpha(alpha);
        GUITweenUtility.pushFontAlpha(alpha);
    }

    @Unique
    private void gUITween$popTween(GuiGraphics guiGraphics) {
        guiGraphics.pose().popMatrix();

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
    public void renderBefore(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!GUITweenConfig.isEnableChat())
            return;

        if (gUITween$openTick > GUITweenConfig.getChatOpenMaxDuration())
            return;

        gUITween$inTween = true;
        gUITween$renderTween(guiGraphics);
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V",
                    shift = At.Shift.AFTER
            )
    )
    public void renderBgAfter(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!gUITween$inTween)
            return;

        gUITween$popTween(guiGraphics);
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIZZ)V",
                    shift = At.Shift.AFTER
            )
    )
    private void renderInputBefore(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!gUITween$inTween)
            return;

        gUITween$renderTween(guiGraphics);
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    public void renderAfter(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!gUITween$inTween)
            return;

        gUITween$popTween(guiGraphics);
        gUITween$inTween = false;
        gUITween$openTick += GUITweenUtility.getDeltaTicks();
    }
}
