package com.remarxk.guitween.mixin.rei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.util.TweenUtil;
import dev.architectury.event.events.client.ClientGuiEvent;
import me.shedaniel.rei.api.client.gui.screen.DisplayScreen;
import me.shedaniel.rei.api.client.registry.screen.OverlayRendererProvider;
import me.shedaniel.rei.impl.client.registry.screen.DefaultScreenOverlayRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.shedaniel.rei.RoughlyEnoughItemsCoreClient.resetFocused;
import static me.shedaniel.rei.RoughlyEnoughItemsCoreClient.shouldReturn;

@Mixin(value = DefaultScreenOverlayRenderer.class, remap = false)
public class DefaultScreenOverlayRendererMixin {
    @Shadow
    private ClientGuiEvent.ContainerScreenRenderBackground renderContainerBg;

    @Inject(
            method = "onApplied",
            at = @At(value = "TAIL")
    )
    private void modifyRenderContainerBg(OverlayRendererProvider.Sink sink, CallbackInfo ci) {
        var oldRenderContainerBg = renderContainerBg;

        renderContainerBg = (screen, graphics, mouseX, mouseY, delta) -> {
            if (!(screen instanceof AbstractContainerScreenMixinAccess access)) {
                return;
            }

            PoseStack poseStack = graphics.pose();

            if (access.getGUITween$inTween()) {
                poseStack.popPose();
                GUITweenUtility.popAlpha();
            }

            if (oldRenderContainerBg != null) {
                oldRenderContainerBg.render(screen, graphics, mouseX, mouseY, delta);
            }

            if (access.getGUITween$inTween()) {
                float openTick = GUITweenUtility.openScreenTick;
                float moveProgress = openTick / GUITween.CONFIG.windowMoveDuration;
                float gradientProgress = openTick / GUITween.CONFIG.windowGradientDuration;

                float dx = TweenUtil.tween(GUITween.CONFIG.windowMoveX, 0, moveProgress, GUITween.CONFIG.windowMoveEase.get());
                float dy = TweenUtil.tween(GUITween.CONFIG.windowMoveY, 0, moveProgress, GUITween.CONFIG.windowMoveEase.get());

                // 动画变换
                poseStack.pushPose();
                poseStack.translate(dx, dy, 0);  // 上移

                float alpha = TweenUtil.tween(0.05f, 1, gradientProgress, GUITween.CONFIG.windowGradientEase.get());
                GUITweenUtility.pushAlpha(alpha);
            }
        };
    }
}
