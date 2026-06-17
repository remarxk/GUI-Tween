package com.remarxk.guitween.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.anim.ChatTween;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Objects;

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

    @Final
    @Shadow
    private static Component RESTRICTED_CHAT_MESSAGE;

    @Final
    @Shadow
    private static Component RESTRICTED_CHAT_MESSAGE_WITH_HOVER;

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
    protected abstract double getScale();

    @Shadow
    public abstract int getLinesPerPage();

    @Unique
    private int gUITween$forEachLine(ChatTween.AlphaCalculator alphaCalculator, ChatTween.LineConsumer action) {
        int perPage = this.getLinesPerPage();
        int count = 0;

        for(int i = Math.min(this.trimmedMessages.size() - this.chatScrollbarPos, perPage) - 1; i >= 0; --i) {
            int messageIndex = i + this.chatScrollbarPos;
            GuiMessage.Line message = (GuiMessage.Line)this.trimmedMessages.get(messageIndex);
            float alpha = alphaCalculator.calculate(message);
            if (alpha > 1.0E-5F) {
                ++count;
                action.accept(message, i, alpha);
            }
        }

        return count;
    }

    /**
     * @author remarxk
     * @reason add animation
     */
    @WrapOperation(
            method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ChatComponent;extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V"
            )
    )
    private void render(ChatComponent instance, ChatComponent.ChatGraphicsAccess graphics, int screenHeight, int ticks, ChatComponent.DisplayMode displayMode, Operation<Void> original) {
        if (!GUITweenConfig.isEnableChatComp()) {
            original.call(instance, graphics, screenHeight, ticks, displayMode);
            return;
        }

        boolean isForeground = displayMode.foreground;
        boolean isRestricted = displayMode.showRestrictedPrompt;
        int total = this.trimmedMessages.size();
        if (total > 0 || isRestricted) {
            if (GUITweenConfig.isEnableChatComp()) {
                if (gUITween$lastNewLine != trimmedMessages.getFirst()) {
                    gUITween$lastNewLine = trimmedMessages.getFirst();
                    gUITween$newMessageTick = 0;
                }
            }

            ProfilerFiller profiler = Profiler.get();
            profiler.push("chat");
            float scale = (float)this.getScale();
            int maxWidth = Mth.ceil((float)this.getWidth() / scale);
            final int chatBottom = Mth.floor((float)(screenHeight - 40) / scale);
            final float textOpacity = ((Double)this.minecraft.options.chatOpacity().get()).floatValue() * 0.9F + 0.1F;
            float backgroundOpacity = ((Double)this.minecraft.options.textBackgroundOpacity().get()).floatValue();
            Objects.requireNonNull(this.minecraft.font);
            final int messageHeight = 9;
            int messageBottomToMessageTop = 8;
            double chatLineSpacing = (Double)this.minecraft.options.chatLineSpacing().get();
            final int entryHeight = (int)((double)messageHeight * (chatLineSpacing + (double)1.0F));
            final int entryBottomToMessageY = (int)Math.round((double)8.0F * (chatLineSpacing + (double)1.0F) - (double)4.0F * chatLineSpacing);
            long queueSize = this.minecraft.gui.chatListener().queueSize();
            ChatTween.AlphaCalculator alphaCalculator = isForeground ? ChatTween.AlphaCalculator.FULLY_VISIBLE : ChatTween.AlphaCalculator.timeBased(ticks);
            graphics.updatePose((pose) -> {
                pose.scale(scale, scale);
                pose.translate(4.0F, 0.0F);
            });
            int count = gUITween$forEachLine(alphaCalculator, (line, lineIndex, alphax) -> {
                gUITween$startTween(graphics, line, gUITween$tweenResult);
                if (gUITween$tweenResult.inTween) {
                    alphax *= gUITween$tweenResult.alpha;
                }

                int entryBottom = chatBottom - lineIndex * entryHeight;
                int entryTop = entryBottom - entryHeight;
                graphics.fill(-4, entryTop, maxWidth + 4 + 4, entryBottom, ARGB.black(alphax * backgroundOpacity));

                gUITween$endTween(graphics, line, gUITween$tweenResult);
            });
            int lineAboveMessagesY = chatBottom - (count + 1) * entryHeight;
            if (queueSize > 0L) {
                graphics.fill(-2, chatBottom, maxWidth + 4, chatBottom + messageHeight, ARGB.black(backgroundOpacity));
            }

            if (isRestricted) {
                graphics.fill(-2, lineAboveMessagesY, maxWidth + 4 + 4, lineAboveMessagesY + entryHeight, ARGB.black(backgroundOpacity));
            }

            ChatComponent chatComponent = (ChatComponent) ((Object) this);

            gUITween$forEachLine(alphaCalculator, new ChatTween.LineConsumer() {
                boolean hoveredOverCurrentMessage;

                {
                    Objects.requireNonNull(chatComponent);
                }

                public void accept(final GuiMessage.Line line, final int lineIndex, float alpha) {
                    gUITween$startTween(graphics, line, gUITween$tweenResult);
                    if (gUITween$tweenResult.inTween) {
                        alpha *= gUITween$tweenResult.alpha;
                    }

                    int entryBottom = chatBottom - lineIndex * entryHeight;
                    int entryTop = entryBottom - entryHeight;
                    int textTop = entryBottom - entryBottomToMessageY;
                    boolean hoveredOverCurrentLine = graphics.handleMessage(textTop, alpha * textOpacity, line.content());
                    this.hoveredOverCurrentMessage |= hoveredOverCurrentLine;
                    boolean forceIconRendering;
                    if (line.endOfEntry()) {
                        forceIconRendering = this.hoveredOverCurrentMessage;
                        this.hoveredOverCurrentMessage = false;
                    } else {
                        forceIconRendering = false;
                    }

                    GuiMessageTag tag = line.tag();
                    if (tag != null) {
                        graphics.handleTag(-4, entryTop, -2, entryBottom, alpha * textOpacity, tag);
                        if (tag.icon() != null) {
                            int iconLeft = line.getTagIconLeft(minecraft.font);
                            int textBottom = textTop + messageHeight;
                            graphics.handleTagIcon(iconLeft, textBottom, forceIconRendering, tag, tag.icon());
                        }
                    }

                    gUITween$endTween(graphics, line, gUITween$tweenResult);
                }
            });
            if (queueSize > 0L) {
                int queueLineBottom = chatBottom + messageHeight;
                Component queueMessage = Component.translatable("chat.queue", new Object[]{queueSize}).setStyle(QUEUE_EXPAND_TEXT_STYLE);
                graphics.handleMessage(queueLineBottom - 8, 0.5F * textOpacity, queueMessage.getVisualOrderText());
            }

            if (isRestricted) {
                int restrictedMessageWidth = this.minecraft.font.width(RESTRICTED_CHAT_MESSAGE);
                FormattedCharSequence restrictedMessage = restrictedMessageWidth > maxWidth ? ComponentRenderUtils.clipText(RESTRICTED_CHAT_MESSAGE_WITH_HOVER, this.minecraft.font, maxWidth) : RESTRICTED_CHAT_MESSAGE.getVisualOrderText();
                graphics.handleMessage(lineAboveMessagesY + entryHeight - entryBottomToMessageY - 1, textOpacity, restrictedMessage);
            }

            if (total > 0 && isForeground) {
                int chatHeight = count * entryHeight;
                int virtualHeight = total * entryHeight;
                int y = this.chatScrollbarPos * chatHeight / total - chatBottom;
                int height = chatHeight * chatHeight / virtualHeight;
                if (virtualHeight != chatHeight) {
                    int alpha = y > 0 ? 170 : 96;
                    int color = this.newMessageSinceScroll ? 13382451 : 3355562;
                    int scrollBarStartX = maxWidth + 4;
                    graphics.fill(scrollBarStartX, -y, scrollBarStartX + 2, -y - height, ARGB.color(alpha, color));
                    graphics.fill(scrollBarStartX + 2, -y, scrollBarStartX + 1, -y - height, ARGB.color(alpha, 13421772));
                }
            }

            profiler.pop();

            if (gUITween$newMessageTick < GUITweenConfig.getChatCompMaxDuration()) {
                gUITween$newMessageTick += GUITweenUtility.getDeltaTicks();
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