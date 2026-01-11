package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.Ease;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
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

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/GuiGraphics;IIIZ)V",
                    shift = At.Shift.AFTER
            )
    )
    public void renderBefore(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!GUITweenConfig.isEnableChat())
            return;

        if (gUITween$openTick > GUITweenConfig.getChatOpenMaxDuration())
            return;

        gUITween$inTween = true;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        float moveProgress = gUITween$openTick / GUITweenConfig.chatOpenMoveDuration.get().floatValue();
        float dx = TweenUtil.tween(GUITweenConfig.chatOpenMoveX.get().floatValue(), 0, moveProgress, GUITweenConfig.chatOpenMoveEase.get());
        float dy = TweenUtil.tween(GUITweenConfig.chatOpenMoveY.get().floatValue(), 0, moveProgress, GUITweenConfig.chatOpenMoveEase.get());

        float alphaProgress = gUITween$openTick / GUITweenConfig.chatOpenGradientDuration.get().floatValue();
        float alpha = TweenUtil.tween(0, 1, alphaProgress, GUITweenConfig.chatOpenGradientEase.get());

        poseStack.translate(dx, dy, 0);
        guiGraphics.setColor(1, 1, 1, alpha);

        gUITween$openTick += GUITweenUtility.getDeltaTicks();
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    public void renderAfter(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!gUITween$inTween)
            return;

        guiGraphics.pose().popPose();
        guiGraphics.setColor(1, 1, 1, 1);
    }
}
