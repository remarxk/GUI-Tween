package com.remarxk.guitween.mixin.rei;

import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.compat.CompatUtility;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import dev.architectury.event.events.client.ClientGuiEvent;
import me.shedaniel.rei.api.client.registry.screen.OverlayRendererProvider;
import me.shedaniel.rei.impl.client.registry.screen.DefaultScreenOverlayRenderer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DefaultScreenOverlayRenderer.class)
public class DefaultScreenOverlayRendererMixin {
    @Shadow
    private ClientGuiEvent.@Nullable ContainerScreenRenderBackground renderContainerBg;

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

            CompatUtility.OpenTween openTween = CompatUtility.getOpenTween();

            if (openTween.inTween) {
                GUITweenUtility.popAlpha();

                // 取消动画
                Matrix3x2fStack matrix3x2fStack = graphics.pose();
                matrix3x2fStack.translate(-openTween.dx, -openTween.dy);
            }

            if (oldRenderContainerBg != null) {
                oldRenderContainerBg.render(screen, graphics, mouseX, mouseY, delta);
            }

            if (openTween.inTween) {
                // 取消动画
                Matrix3x2fStack matrix3x2fStack = graphics.pose();
                matrix3x2fStack.translate(openTween.dx, openTween.dy);

                GUITweenUtility.pushAlpha(openTween.alpha);
            }
        };
    }
}
