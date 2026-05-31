package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.util.DebugUtil;
import com.remarxk.guitween.client.util.TweenUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    @Unique
    private boolean gUITween$inCloseTween;

    @Unique
    private float gUITween$openTick;

    @Unique
    private boolean gUITween$inTween;

    @Shadow
    protected TextFieldWidget chatField;

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "HEAD"))
    public void initOpenTick(CallbackInfo ci) {
        gUITween$inCloseTween = false;

        gUITween$openTick = 0;
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"
            ),
            remap = false
    )
    public void renderBefore(DrawContext guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!GUITweenClient.CONFIG.isEnableChat())
            return;

        if (gUITween$openTick > GUITweenClient.CONFIG.getChatOpenMaxDuration())
            return;

        gUITween$inTween = true;

        MatrixStack poseStack = guiGraphics.getMatrices();
        poseStack.push();

        float moveProgress = gUITween$openTick / GUITweenClient.CONFIG.chatOpenMoveDuration;
        float dy = TweenUtil.tween(12f, 0, moveProgress, GUITweenClient.CONFIG.chatOpenMoveEase.get());

        float alphaProgress = gUITween$openTick / GUITweenClient.CONFIG.chatOpenGradientDuration;
        float alpha = TweenUtil.tween(0.01f, 1, alphaProgress, GUITweenClient.CONFIG.chatOpenGradientEase.get());

        poseStack.translate(0, dy, 0);
        guiGraphics.setShaderColor(1, 1, 1, alpha);
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "RETURN"
            )
    )
    public void renderAfter(DrawContext guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!gUITween$inTween)
            return;

        gUITween$inTween = false;
        guiGraphics.getMatrices().pop();
        guiGraphics.setShaderColor(1, 1, 1, 1);

        float sign = gUITween$inCloseTween ? -GUITweenClient.CONFIG.closeChatSpeed : 1;
        gUITween$openTick = gUITween$openTick + sign * GUITweenUtility.getDeltaTicks();

        if (gUITween$openTick <= 0 && gUITween$inCloseTween) {
            close();
        }
    }

    @Unique
    private boolean guiTween$playCloseTween(){
        if (!GUITweenClient.CONFIG.isEnableCloseChat())
            return false;

        gUITween$inCloseTween = !gUITween$inCloseTween;

        if (gUITween$inCloseTween) {
            gUITween$openTick = Math.min(gUITween$openTick, GUITweenClient.CONFIG.getChatOpenMaxDuration());
        }

        return gUITween$inCloseTween;
    }

    @Inject(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"
            ),
            cancellable = true)
    private void onCloseBefore(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (guiTween$playCloseTween()) {
            chatField.setText("");
            cir.setReturnValue(true);
        }
    }
}
