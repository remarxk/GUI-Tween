package com.remarxk.guitween.mixin;

import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.eventListener.HotbarChangeListener;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class FabricHudMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private int gUITween$lastLevel = -1;

    @Unique
    private boolean gUITween$inLevelTextTween;

    @Unique
    private float gUITween$levelTextTick;

    @Unique
    private boolean gUITween$inSelectedItemNameTween;

    @Inject(
            method = "extractSelectedItemName",
            at = @At(
                    value = "HEAD"
            )
    )
    public void renderSelectedItemNameBefore(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (!GUITweenConfig.isEnableSelectedItemName())
            return;

        // 如果动画结束，直接正常绘制
        if (HotbarChangeListener.animTick > GUITweenConfig.getSelectedItemNameDuration()) {
            return;
        }

        gUITween$inSelectedItemNameTween = true;

        float progress = HotbarChangeListener.animTick / GUITweenConfig.selectedItemNameMoveDuration();
        float dy = TweenUtil.tween(GUITweenConfig.selectedItemNameMoveY(), 0, progress, GUITweenConfig.selectedItemNameMoveEase());

        Matrix3x2fStack poseStack = graphics.pose();
        poseStack.pushMatrix();

        poseStack.translate(0, dy);
    }

    @ModifyArg(
            method = "extractSelectedItemName",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/ARGB;white(I)I"
            ),
            index = 0
    )
    private int modifySelectedItemNameAlpha(int alpha) {
        if (gUITween$inSelectedItemNameTween) {
            float progress = HotbarChangeListener.animTick / GUITweenConfig.selectedItemNameAlphaDuration();
            alpha = (int) TweenUtil.tween(GUITweenUtility.iFontMinAlpha, alpha, progress, GUITweenConfig.selectedItemNameAlphaEase());
        }

        return alpha;
    }

    @Inject(
            method = "extractSelectedItemName",
            at = @At(
                    value = "RETURN"
            )
    )
    public void renderSelectedItemNameAfter(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (!gUITween$inSelectedItemNameTween) {
            return;
        }

        gUITween$inSelectedItemNameTween = false;

        // 推进动画时间
        HotbarChangeListener.animTick += GUITweenUtility.getDeltaTicks();

        Matrix3x2fStack poseStack = graphics.pose();
        poseStack.popMatrix();
    }

    @Inject(
            method = "extractHotbarAndDecorations",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/contextualbar/ContextualBar;extractExperienceLevel(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;I)V"
            )
    )
    public void renderExperienceLevelBefore(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!GUITweenConfig.isEnableExp())
            return;

        if (this.minecraft.player == null)
            return;

        int i = this.minecraft.player.experienceLevel;

        if (gUITween$lastLevel == -1) {
            gUITween$lastLevel = i;
            gUITween$levelTextTick = GUITweenConfig.expDuration();
            return;
        }

        if (gUITween$lastLevel != i) {
            gUITween$lastLevel = i;
            gUITween$levelTextTick = 0;
        }

        if (gUITween$levelTextTick >= GUITweenConfig.expDuration()) {
            return;
        }

        gUITween$inLevelTextTween = true;

        Component component = Component.translatable("gui.experience.level", i);
        int j = (graphics.guiWidth() - minecraft.font.width(component)) / 2;
        int k = graphics.guiHeight() - 24 - 9 - 2;

        Matrix3x2fStack poseStack = graphics.pose();
        poseStack.pushMatrix();

        // 缩放中心为文本中心
        float cx = j + minecraft.font.width(component) / 2f;
        float cy = k + minecraft.font.lineHeight / 2f;

        float progress = gUITween$levelTextTick / GUITweenConfig.expDuration();
        float scale = TweenUtil.tween(GUITweenConfig.expScale(), 1, progress, GUITweenConfig.expEase());

        poseStack.translate(cx, cy);
        poseStack.scale(scale, scale);
        poseStack.translate(-cx, -cy);

        gUITween$levelTextTick += GUITweenUtility.getDeltaTicks();
    }

    @Inject(
            method = "extractHotbarAndDecorations",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/contextualbar/ContextualBar;extractExperienceLevel(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;I)V",
                    shift = At.Shift.AFTER
            )
    )
    public void renderExperienceLevelAfter(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!gUITween$inLevelTextTween)
            return;

        gUITween$inLevelTextTween = false;
        graphics.pose().popMatrix();
    }
}
