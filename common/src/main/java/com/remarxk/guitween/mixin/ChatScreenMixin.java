package com.remarxk.guitween.mixin;

import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.DebugUtil;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    @Shadow
    public abstract void onClose();

    @Unique
    private boolean gUITween$inCloseTween;

    @Unique
    private float gUITween$openTick;

    @Unique
    private boolean gUITween$inTween;

    @Shadow
    private EditBox input;

    protected ChatScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "HEAD"))
    public void initOpenTick(CallbackInfo ci) {
        gUITween$inCloseTween = false;

        gUITween$openTick = 0;
    }

    @Unique
    private void gUITween$renderTween(GuiGraphicsExtractor guiGraphics) {
        Matrix3x2fStack poseStack = guiGraphics.pose();
        poseStack.pushMatrix();

        float moveProgress = gUITween$openTick / GUITweenConfig.chatOpenMoveDuration();
        float dy = TweenUtil.tween(12f, 0, moveProgress, GUITweenConfig.chatOpenMoveEase());

        float alphaProgress = gUITween$openTick / GUITweenConfig.chatOpenGradientDuration();
        float alpha = TweenUtil.tween(0.01f, 1, alphaProgress, GUITweenConfig.chatOpenGradientEase());

        poseStack.translate(0, dy);

        GUITweenUtility.pushSpriteAlpha(alpha);
        GUITweenUtility.pushFontAlpha(alpha);
    }

    @Unique
    private void gUITween$popTween(GuiGraphicsExtractor guiGraphics) {
        guiGraphics.pose().popMatrix();

        GUITweenUtility.popSpriteAlpha();
        GUITweenUtility.popFontAlpha();
    }

    @Inject(
            method = "extractRenderState",
            at = @At(
                    value = "HEAD"
            ),
            remap = false
    )
    public void renderBefore(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!GUITweenConfig.isEnableChat() && !gUITween$inCloseTween) {
            return;
        }

        if (gUITween$openTick > GUITweenConfig.getChatOpenMaxDuration())
            return;

        gUITween$inTween = true;
        gUITween$renderTween(graphics);
    }

    @Inject(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V",
                    shift = At.Shift.AFTER
            )
    )
    public void renderBgAfter(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!gUITween$inTween)
            return;

        gUITween$popTween(graphics);
    }

    @Inject(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ChatComponent;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V",
                    shift = At.Shift.AFTER
            )
    )
    private void renderInputBefore(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!gUITween$inTween)
            return;

        gUITween$renderTween(graphics);
    }

    @Inject(method = "extractRenderState", at = @At(value = "TAIL"))
    public void renderAfter(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!gUITween$inTween)
            return;

        gUITween$popTween(graphics);
        gUITween$inTween = false;

        float sign = gUITween$inCloseTween ? -GUITweenConfig.closeChatSpeed() : 1;
        gUITween$openTick = gUITween$openTick + sign * GUITweenUtility.getDeltaTicks();

        if (gUITween$openTick <= 0 && gUITween$inCloseTween) {
            onClose();
        }
    }

    @Unique
    private boolean guiTween$playCloseTween(){
        if (!GUITweenConfig.isEnableCloseChat())
            return false;

        if (!GUITweenConfig.isEnableChat()) {
            gUITween$openTick = GUITweenConfig.getChatOpenMaxDuration();
        }

        gUITween$inCloseTween = !gUITween$inCloseTween;

        if (gUITween$inCloseTween) {
            gUITween$openTick = Math.min(gUITween$openTick, GUITweenConfig.getChatOpenMaxDuration());
        }

        return gUITween$inCloseTween;
    }

    @Inject(
            method = "keyPressed",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/screens/ChatScreen;exitReason:Lnet/minecraft/client/gui/screens/ChatScreen$ExitReason;"
            ),
            cancellable = true)
    private void onCloseBefore(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (guiTween$playCloseTween()) {
            input.setValue("");
            cir.setReturnValue(true);
        }
    }
}
