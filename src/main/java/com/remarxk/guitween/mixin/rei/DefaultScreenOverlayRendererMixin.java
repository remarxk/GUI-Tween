package com.remarxk.guitween.mixin.rei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.compat.CompatUtility;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import dev.architectury.event.events.client.ClientGuiEvent;
import me.shedaniel.rei.api.client.registry.screen.OverlayRendererProvider;
import me.shedaniel.rei.impl.client.registry.screen.DefaultScreenOverlayRenderer;
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

            PoseStack poseStack = graphics.pose();

            if (access.getGUITween$inTween()) {
                poseStack.popPose();
                GUITweenUtility.popAlpha();
            }

            if (oldRenderContainerBg != null) {
                oldRenderContainerBg.render(screen, graphics, mouseX, mouseY, delta);
            }

            if (access.getGUITween$inTween()) {
                // 开启动画和关闭动画使用不同参数，这里必须重新压入“当前”窗口变换，
                // 否则关闭动画时背景已经位移、后续绘制的格子却留在原位。
                CompatUtility.WindowTween windowTween = CompatUtility.getWindowTween(access);

                poseStack.pushPose();
                poseStack.translate(windowTween.dx, windowTween.dy, 0);

                GUITweenUtility.pushAlpha(windowTween.alpha);
            }
        };
    }
}
