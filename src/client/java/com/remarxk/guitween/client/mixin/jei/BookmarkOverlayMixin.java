package com.remarxk.guitween.client.mixin.jei;

import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.util.TweenUtil;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BookmarkOverlay.class)
public class BookmarkOverlayMixin {
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

        if (!GUITweenClient.CONFIG.isEnableJeiLeft())
            return;

        float totalTick = Math.max(GUITweenClient.CONFIG.jeiLeftMoveDuration, 1);
        float progress = GUITweenUtility.openScreenTick / totalTick;

        if (progress > 1){
            return;
        }

        gUITween$inTween = true;

        Matrix3x2fStack poseStack = guiGraphics.getMatrices();
        poseStack.pushMatrix();

        float dx = TweenUtil.tween(GUITweenClient.CONFIG.jeiLeftMoveX, 0, progress, GUITweenClient.CONFIG.jeiLeftMoveEase.get());
        float dY = TweenUtil.tween(GUITweenClient.CONFIG.jeiLeftMoveY, 0, progress, GUITweenClient.CONFIG.jeiLeftMoveEase.get());

        poseStack.translate(dx, dY);
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

        Matrix3x2fStack poseStack = guiGraphics.getMatrices();
        poseStack.popMatrix();
    }
}
