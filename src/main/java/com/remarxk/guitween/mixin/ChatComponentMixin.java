package com.remarxk.guitween.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.anim.ChatTween;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {
    @Unique
    private float gUITween$newMessageTick;

    @Unique
    private ChatTween.Result gUITween$tweenResult = new ChatTween.Result();

    @Unique
    private GuiMessage.Line gUITween$lastNewLine;

    @Final
    @Shadow
    private static Style QUEUE_EXPAND_TEXT_STYLE;

    @Shadow
    private int chatScrollbarPos;

    @Shadow
    private boolean newMessageSinceScroll;

    @Final
    @Shadow
    private List<GuiMessage.Line> trimmedMessages;

    @Final
    @Shadow
    Minecraft minecraft;

    @Shadow
    protected abstract int getWidth();

    @Shadow
    protected abstract boolean isChatHidden();

    @Shadow
    public abstract double getScale();

    @Shadow
    public abstract int getLinesPerPage();

    @Unique
    private int gUITween$forEachLine(ChatTween.AlphaCalculator alphaCalculator, ChatTween.LineConsumer action) {
        int i = this.getLinesPerPage();
        int j = 0;

        for(int k = Math.min(this.trimmedMessages.size() - this.chatScrollbarPos, i) - 1; k >= 0; --k) {
            int l = k + this.chatScrollbarPos;
            GuiMessage.Line guimessage$line = (GuiMessage.Line)this.trimmedMessages.get(l);
            float f = alphaCalculator.calculate(guimessage$line);
            if (f > 1.0E-5F) {
                ++j;
                action.accept(guimessage$line, k, f);
            }
        }

        return j;
    }

    /**
     * @author remarxk
     * @reason add animation
     */
    @WrapOperation(
            method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIZZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IIZ)V"
            )
    )
    private void render(ChatComponent instance, ChatComponent.ChatGraphicsAccess chatGraphicsAccess, int height, int tickCount, boolean focused, Operation<Void> original) {
        if (!GUITweenConfig.isEnableChatComp()) {
            original.call(instance, chatGraphicsAccess, height, tickCount, focused);
            return;
        }

        if (!this.isChatHidden()) {
            int i = this.trimmedMessages.size();
            if (i > 0) {
                if (GUITweenConfig.isEnableChatComp()) {
                    if (gUITween$lastNewLine != trimmedMessages.getFirst()) {
                        gUITween$lastNewLine = trimmedMessages.getFirst();
                        gUITween$newMessageTick = 0;
                    }
                }

                ProfilerFiller profilerfiller = Profiler.get();
                profilerfiller.push("chat");
                float f = (float)this.getScale();
                int j = Mth.ceil((float)this.getWidth() / f);
                final int k = Mth.floor((float)(height - 40) / f);
                final float f1 = ((Double)this.minecraft.options.chatOpacity().get()).floatValue() * 0.9F + 0.1F;
                float f2 = ((Double)this.minecraft.options.textBackgroundOpacity().get()).floatValue();
                int l = 9;
                int i1 = 8;
                double d0 = (Double)this.minecraft.options.chatLineSpacing().get();
                final int j1 = (int)((double)9.0F * (d0 + (double)1.0F));
                final int k1 = (int)Math.round((double)8.0F * (d0 + (double)1.0F) - (double)4.0F * d0);
                long l1 = this.minecraft.getChatListener().queueSize();
                ChatTween.AlphaCalculator chatcomponent$alphacalculator = focused ? ChatTween.AlphaCalculator.FULLY_VISIBLE : ChatTween.AlphaCalculator.timeBased(tickCount);
                chatGraphicsAccess.updatePose((p_457333_) -> {
                    p_457333_.scale(f, f);
                    p_457333_.translate(4.0F, 0.0F);
                });
                this.gUITween$forEachLine(chatcomponent$alphacalculator, (line, index, alpha) -> {
                    gUITween$startTween(chatGraphicsAccess, line, gUITween$tweenResult);
                    if (gUITween$tweenResult.inTween) {
                        alpha *= gUITween$tweenResult.alpha;
                    }

                    int j4 = k - index * j1;
                    int k4 = j4 - j1;
                    chatGraphicsAccess.fill(-4, k4, j + 4 + 4, j4, ARGB.black(alpha * f2));

                    gUITween$endTween(chatGraphicsAccess, line, gUITween$tweenResult);
                });
                if (l1 > 0L) {
                    chatGraphicsAccess.fill(-2, k, j + 4, k + 9, ARGB.black(f2));
                }

                int i2 = this.gUITween$forEachLine(chatcomponent$alphacalculator, new ChatTween.LineConsumer() {
                    boolean hoveredOverCurrentMessage;

                    public void accept(GuiMessage.Line line, int index, float alpha) {
                        gUITween$startTween(chatGraphicsAccess, line, gUITween$tweenResult);
                        if (gUITween$tweenResult.inTween) {
                            alpha *= gUITween$tweenResult.alpha;
                        }

                        int j4 = k - index * j1;
                        int k4 = j4 - j1;
                        int l4 = j4 - k1;
                        boolean flag = chatGraphicsAccess.handleMessage(l4, alpha * f1, line.content());
                        this.hoveredOverCurrentMessage |= flag;
                        boolean flag1;
                        if (line.endOfEntry()) {
                            flag1 = this.hoveredOverCurrentMessage;
                            this.hoveredOverCurrentMessage = false;
                        } else {
                            flag1 = false;
                        }

                        GuiMessageTag guimessagetag = line.tag();
                        if (guimessagetag != null) {
                            chatGraphicsAccess.handleTag(-4, k4, -2, j4, alpha * f1, guimessagetag);
                            if (guimessagetag.icon() != null) {
                                int i5 = line.getTagIconLeft(minecraft.font);
                                int j5 = l4 + 9;
                                chatGraphicsAccess.handleTagIcon(i5, j5, flag1, guimessagetag, guimessagetag.icon());
                            }
                        }

                        gUITween$endTween(chatGraphicsAccess, line, gUITween$tweenResult);
                    }
                });
                if (l1 > 0L) {
                    int j2 = k + 9;
                    Component component = Component.translatable("chat.queue", new Object[]{l1}).setStyle(QUEUE_EXPAND_TEXT_STYLE);
                    chatGraphicsAccess.handleMessage(j2 - 8, 0.5F * f1, component.getVisualOrderText());
                }

                if (focused) {
                    int l3 = i * j1;
                    int i4 = i2 * j1;
                    int k2 = this.chatScrollbarPos * i4 / i - k;
                    int l2 = i4 * i4 / l3;
                    if (l3 != i4) {
                        int i3 = k2 > 0 ? 170 : 96;
                        int j3 = this.newMessageSinceScroll ? 13382451 : 3355562;
                        int k3 = j + 4;
                        chatGraphicsAccess.fill(k3, -k2, k3 + 2, -k2 - l2, ARGB.color(i3, j3));
                        chatGraphicsAccess.fill(k3 + 2, -k2, k3 + 1, -k2 - l2, ARGB.color(i3, 13421772));
                    }
                }

                profilerfiller.pop();

                if (gUITween$newMessageTick < GUITweenConfig.getChatCompMaxDuration()) {
                    gUITween$newMessageTick += GUITweenUtility.getDeltaTicks();
                }
            }
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

        float moveProgress = gUITween$newMessageTick / GUITweenConfig.chat.compMoveDuration.get().floatValue();
        float moveX = -getWidth();
        float moveY = 0;
        result.dx = TweenUtil.tween(moveX, 0, moveProgress, GUITweenConfig.chat.compMoveEase.get());
        result.dy = TweenUtil.tween(moveY, 0, moveProgress, GUITweenConfig.chat.compMoveEase.get());

        float progress = gUITween$newMessageTick / GUITweenConfig.chat.compGradientDuration.get().floatValue();
        result.alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, progress, GUITweenConfig.chat.compGradientEase.get());

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