package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.anim.ChatTween;
import com.remarxk.guitween.client.util.TweenUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.spongepowered.asm.mixin.*;

import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatComponentMixin {
    @Unique
    private float gUITween$newMessageTick;

    @Unique
    private ChatTween.Result gUITween$tweenResult = new ChatTween.Result();

    @Unique
    private ChatHudLine.Visible gUITween$lastNewLine;

    @Final
    @Shadow
    private static Style CHAT_QUEUE_STYLE;

    @Shadow
    private int scrolledLines;

    @Shadow
    private boolean hasUnreadNewMessages;

    @Final
    @Shadow
    private List<ChatHudLine.Visible> visibleMessages;

    @Final
    @Shadow
    MinecraftClient client;

    @Shadow
    protected abstract int getWidth();

    @Shadow
    protected abstract boolean isChatHidden();

    @Shadow
    protected abstract double getChatScale();

    @Shadow
    public abstract int getVisibleLineCount();

    @Unique
    private int gUITween$forEachLine(ChatTween.AlphaCalculator alphaCalculator, ChatTween.LineConsumer action) {
        int i = this.getVisibleLineCount();
        int j = 0;

        for(int k = Math.min(this.visibleMessages.size() - this.scrolledLines, i) - 1; k >= 0; --k) {
            int l = k + this.scrolledLines;
            ChatHudLine.Visible guimessage$line = (ChatHudLine.Visible)this.visibleMessages.get(l);
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
    @Overwrite
    private void render(final ChatHud.Backend chatGraphicsAccess, int height, int tickCount, boolean focused) {
        if (!this.isChatHidden()) {
            int i = this.visibleMessages.size();
            if (i > 0) {
                if (GUITweenClient.CONFIG.isEnableChatComp()) {
                    if (gUITween$lastNewLine != visibleMessages.getFirst()) {
                        gUITween$lastNewLine = visibleMessages.getFirst();
                        gUITween$newMessageTick = 0;
                    }
                }

                Profiler profilerfiller = Profilers.get();
                profilerfiller.push("chat");
                float f = (float)this.getChatScale();
                int j = MathHelper.ceil((float)this.getWidth() / f);
                final int k = MathHelper.floor((float)(height - 40) / f);
                final float f1 = ((Double)this.client.options.getChatOpacity().getValue()).floatValue() * 0.9F + 0.1F;
                float f2 = ((Double)this.client.options.getTextBackgroundOpacity().getValue()).floatValue();
                int l = 9;
                int i1 = 8;
                double d0 = (Double)this.client.options.getChatLineSpacing().getValue();
                final int j1 = (int)((double)9.0F * (d0 + (double)1.0F));
                final int k1 = (int)Math.round((double)8.0F * (d0 + (double)1.0F) - (double)4.0F * d0);
                long l1 = this.client.getMessageHandler().getUnprocessedMessageCount();
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
                    chatGraphicsAccess.fill(-4, k4, j + 4 + 4, j4, ColorHelper.toAlpha(alpha * f2));

                    gUITween$endTween(chatGraphicsAccess, line, gUITween$tweenResult);
                });
                if (l1 > 0L) {
                    chatGraphicsAccess.fill(-2, k, j + 4, k + 9, ColorHelper.toAlpha(f2));
                }

                int i2 = this.gUITween$forEachLine(chatcomponent$alphacalculator, new ChatTween.LineConsumer() {
                    boolean hoveredOverCurrentMessage;

                    public void accept(ChatHudLine.Visible line, int index, float alpha) {
                        gUITween$startTween(chatGraphicsAccess, line, gUITween$tweenResult);
                        if (gUITween$tweenResult.inTween) {
                            alpha *= gUITween$tweenResult.alpha;
                        }

                        int j4 = k - index * j1;
                        int k4 = j4 - j1;
                        int l4 = j4 - k1;
                        boolean flag = chatGraphicsAccess.text(l4, alpha * f1, line.content());
                        this.hoveredOverCurrentMessage |= flag;
                        boolean flag1;
                        if (line.endOfEntry()) {
                            flag1 = this.hoveredOverCurrentMessage;
                            this.hoveredOverCurrentMessage = false;
                        } else {
                            flag1 = false;
                        }

                        MessageIndicator guimessagetag = line.indicator();
                        if (guimessagetag != null) {
                            chatGraphicsAccess.indicator(-4, k4, -2, j4, alpha * f1, guimessagetag);
                            if (guimessagetag.icon() != null) {
                                int i5 = line.getWidth(client.textRenderer);
                                int j5 = l4 + 9;
                                chatGraphicsAccess.indicatorIcon(i5, j5, flag1, guimessagetag, guimessagetag.icon());
                            }
                        }

                        gUITween$endTween(chatGraphicsAccess, line, gUITween$tweenResult);
                    }
                });
                if (l1 > 0L) {
                    int j2 = k + 9;
                    Text component = Text.translatable("chat.queue", new Object[]{l1}).setStyle(CHAT_QUEUE_STYLE);
                    chatGraphicsAccess.text(j2 - 8, 0.5F * f1, component.asOrderedText());
                }

                if (focused) {
                    int l3 = i * j1;
                    int i4 = i2 * j1;
                    int k2 = this.scrolledLines * i4 / i - k;
                    int l2 = i4 * i4 / l3;
                    if (l3 != i4) {
                        int i3 = k2 > 0 ? 170 : 96;
                        int j3 = this.hasUnreadNewMessages ? 13382451 : 3355562;
                        int k3 = j + 4;
                        chatGraphicsAccess.fill(k3, -k2, k3 + 2, -k2 - l2, ColorHelper.withAlpha(i3, j3));
                        chatGraphicsAccess.fill(k3 + 2, -k2, k3 + 1, -k2 - l2, ColorHelper.withAlpha(i3, 13421772));
                    }
                }

                profilerfiller.pop();

                if (gUITween$newMessageTick < GUITweenClient.CONFIG.getChatCompMaxDuration()) {
                    gUITween$newMessageTick += GUITweenUtility.getDeltaTicks();
                }
            }
        }
    }

    @Unique
    private void gUITween$startTween(final ChatHud.Backend chatGraphicsAccess, ChatHudLine.Visible line, ChatTween.Result result) {
        result.inTween = false;

        if (line != gUITween$lastNewLine)
            return;

        if (gUITween$newMessageTick > GUITweenClient.CONFIG.getChatCompMaxDuration())
            return;

        result.inTween = true;

        float moveProgress = gUITween$newMessageTick / GUITweenClient.CONFIG.chatCompMoveDuration;
        float moveX = -getWidth();
        float moveY = 0;
        result.dx = TweenUtil.tween(moveX, 0, moveProgress, GUITweenClient.CONFIG.chatCompMoveEase.get());
        result.dy = TweenUtil.tween(moveY, 0, moveProgress, GUITweenClient.CONFIG.chatCompMoveEase.get());

        float progress = gUITween$newMessageTick / GUITweenClient.CONFIG.chatCompGradientDuration;
        result.alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, progress, GUITweenClient.CONFIG.chatCompGradientEase.get());

        chatGraphicsAccess.updatePose(matrix3x2f -> {
            matrix3x2f.translate(result.dx, result.dy);
        });
    }

    @Unique
    private void gUITween$endTween(final ChatHud.Backend chatGraphicsAccess, ChatHudLine.Visible line, ChatTween.Result result) {
        if (!result.inTween)
            return;

        result.inTween = false;

        chatGraphicsAccess.updatePose(matrix3x2f -> {
            matrix3x2f.translate(-result.dx, -result.dy);
        });
    }
}