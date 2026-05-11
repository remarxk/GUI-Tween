package com.remarxk.guitween.client.mixin.jei;

import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.util.TweenUtil;
import mezz.jei.gui.overlay.IngredientListOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = IngredientListOverlay.class)
public class IngredientListOverlayMixin {
    @Unique
    private boolean gUITween$inTween;

    @Inject(
            method = "drawScreen",
            at = @At(
                    value = "HEAD"
            )
    )
    public void drawScreenBefore(MinecraftClient minecraft, DrawContext guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (GUITweenUtility.openScreenName == null)
            return;

        if (!GUITweenClient.CONFIG.isEnableJeiRight())
            return;

        float totalTick = Math.max(GUITweenClient.CONFIG.jeiRightMoveDuration, 1);
        float progress = GUITweenUtility.openScreenTick / totalTick;

        if (progress > 1){
            return;
        }

        gUITween$inTween = true;

        MatrixStack poseStack = guiGraphics.getMatrices();
        poseStack.push();

        float dx = TweenUtil.tween(GUITweenClient.CONFIG.jeiRightMoveX, 0, progress, GUITweenClient.CONFIG.jeiRightMoveEase.get());
        float dy = TweenUtil.tween(GUITweenClient.CONFIG.jeiRightMoveY, 0, progress, GUITweenClient.CONFIG.jeiRightMoveEase.get());

        poseStack.translate(dx, dy , 0);
    }

    @Inject(
            method = "drawScreen",
            at = @At(
                    value = "TAIL"
            )
    )
    public void drawScreenAfter(MinecraftClient minecraft, DrawContext guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!gUITween$inTween) {
            return;
        }

        gUITween$inTween = false;

        MatrixStack poseStack = guiGraphics.getMatrices();
        poseStack.pop();
    }
}
