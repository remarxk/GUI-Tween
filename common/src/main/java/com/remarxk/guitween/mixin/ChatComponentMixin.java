package com.remarxk.guitween.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.anim.ChatTween;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.util.List;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {
    @Unique
    private float gUITween$newMessageTick;

    @Unique
    private ChatTween.Result gUITween$tweenResult = new ChatTween.Result();

    @Unique
    private GuiMessage.Line gUITween$lastNewLine;

    @Unique
    private ChatComponent.ChatGraphicsAccess gUITween$currentGraphics;

    @Shadow
    private int chatScrollbarPos;

    @Shadow
    private boolean newMessageSinceScroll;

    @Final
    @Shadow
    private List<GuiMessage.Line> trimmedMessages;

    @Final
    @Shadow
    private Minecraft minecraft;

    @Shadow
    protected abstract int getWidth();

    @Shadow
    public abstract int getLinesPerPage();

    @WrapOperation(
            method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ChatComponent;extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V"
            )
    )
    private void render(ChatComponent instance, ChatComponent.ChatGraphicsAccess graphics, int screenHeight, int ticks, ChatComponent.DisplayMode displayMode, Operation<Void> original) {
        if (GUITweenConfig.isEnableChatComp()) {
            int total = this.trimmedMessages.size();
            if (total > 0 && gUITween$lastNewLine != trimmedMessages.getFirst()) {
                gUITween$lastNewLine = trimmedMessages.getFirst();
                gUITween$newMessageTick = 0;
            }
            gUITween$currentGraphics = graphics;
        }

        original.call(instance, graphics, screenHeight, ticks, displayMode);

        if (GUITweenConfig.isEnableChatComp() && gUITween$newMessageTick < GUITweenConfig.getChatCompMaxDuration()) {
            gUITween$newMessageTick += GUITweenUtility.getDeltaTicks();
        }
    }

    @WrapOperation(
            method = "forEachLine(Lnet/minecraft/client/gui/components/ChatComponent$AlphaCalculator;Lnet/minecraft/client/gui/components/ChatComponent$LineConsumer;)I",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent$LineConsumer;accept(Lnet/minecraft/client/multiplayer/chat/GuiMessage$Line;IF)V")
    )
    private void gUITween$wrapAccept(@Coerce Object consumer, GuiMessage.Line line, int index, float alpha, Operation<Void> original) {
        if (gUITween$currentGraphics != null && GUITweenConfig.isEnableChatComp()) {
            gUITween$startTween(gUITween$currentGraphics, line, gUITween$tweenResult);
            if (gUITween$tweenResult.inTween) {
                alpha *= gUITween$tweenResult.alpha;
            }
        }

        original.call(consumer, line, index, alpha);

        if (gUITween$currentGraphics != null && GUITweenConfig.isEnableChatComp()) {
            gUITween$endTween(gUITween$currentGraphics, line, gUITween$tweenResult);
        }
    }

    @Unique
    private void gUITween$startTween(final ChatComponent.ChatGraphicsAccess chatGraphicsAccess, GuiMessage.Line line, ChatTween.Result result) {
        result.inTween = false;

        if (line != gUITween$lastNewLine)
            return;

        if (gUITween$newMessageTick > GUITweenConfig.getChatCompMaxDuration())
            return;

        result.inTween = true;

        float moveProgress = gUITween$newMessageTick / GUITweenConfig.chatCompMoveDuration();
        float moveX = -getWidth();
        float moveY = 0;
        result.dx = TweenUtil.tween(moveX, 0, moveProgress, GUITweenConfig.chatCompMoveEase());
        result.dy = TweenUtil.tween(moveY, 0, moveProgress, GUITweenConfig.chatCompMoveEase());

        float progress = gUITween$newMessageTick / GUITweenConfig.chatCompGradientDuration();
        result.alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, progress, GUITweenConfig.chatCompGradientEase());

        chatGraphicsAccess.updatePose(matrix3x2f -> {
            matrix3x2f.translate(result.dx, result.dy);
        });
    }

    @Unique
    private void gUITween$endTween(final ChatComponent.ChatGraphicsAccess chatGraphicsAccess, GuiMessage.Line line, ChatTween.Result result) {
        if (!result.inTween)
            return;

        result.inTween = false;

        chatGraphicsAccess.updatePose(matrix3x2f -> {
            matrix3x2f.translate(-result.dx, -result.dy);
        });
    }
}
