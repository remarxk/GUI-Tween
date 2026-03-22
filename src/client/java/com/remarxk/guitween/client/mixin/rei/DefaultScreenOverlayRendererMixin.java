package com.remarxk.guitween.client.mixin.rei;

import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.client.util.TweenUtil;
import dev.architectury.event.events.client.ClientGuiEvent;
import me.shedaniel.rei.api.client.registry.screen.OverlayRendererProvider;
import me.shedaniel.rei.impl.client.registry.screen.DefaultScreenOverlayRenderer;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DefaultScreenOverlayRenderer.class)
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

            Matrix3x2fStack poseStack = graphics.getMatrices();

            if (access.getGUITween$inTween()) {
                poseStack.popMatrix();
                GUITweenUtility.popAlpha();
            }

            if (oldRenderContainerBg != null) {
                oldRenderContainerBg.render(screen, graphics, mouseX, mouseY, delta);
            }

            if (access.getGUITween$inTween()) {
                float openTick = access.getGUITween$openTick();
                float moveProgress = openTick / GUITweenClient.CONFIG.windowMoveDuration;
                float gradientProgress = openTick / GUITweenClient.CONFIG.windowGradientDuration;

                float dx = TweenUtil.tween(GUITweenClient.CONFIG.windowMoveX, 0, moveProgress, GUITweenClient.CONFIG.windowMoveEase.get());
                float dy = TweenUtil.tween(GUITweenClient.CONFIG.windowMoveY, 0, moveProgress, GUITweenClient.CONFIG.windowMoveEase.get());

                // 动画变换
                poseStack.pushMatrix();
                poseStack.translate(dx, dy);  // 上移

                float alpha = TweenUtil.tween(0.05f, 1, gradientProgress, GUITweenClient.CONFIG.windowGradientEase.get());
                GUITweenUtility.pushAlpha(alpha);
            }
        };
    }
}
