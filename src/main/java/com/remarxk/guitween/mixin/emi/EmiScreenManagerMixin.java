package com.remarxk.guitween.mixin.emi;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.compat.EmiCompat;
import com.remarxk.guitween.util.TweenUtil;
import dev.emi.emi.config.SidebarSide;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.EmiScreenManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EmiScreenManager.class, remap = false)
public class EmiScreenManagerMixin {
    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/emi/emi/screen/EmiScreenManager$SidebarPanel;render(Ldev/emi/emi/runtime/EmiDrawContext;IIF)V"
            )
    )
    private static void renderSidebarPanel(EmiScreenManager.SidebarPanel instance, EmiDrawContext context, int mouseX, int mouseY, float delta) {
        if (GUITweenUtility.openScreenName != null) {
            if (GUITween.CONFIG.isEnableJei()) {
                boolean closing = GUITweenUtility.isWindowClosing;
                boolean leftSide = instance.side == SidebarSide.LEFT;

                if (closing) {
                    float duration = GUITween.CONFIG.closeJeiMoveDuration;
                    float total = GUITween.CONFIG.getCloseJeiTotalDuration();
                    float elapsed = Math.max(0, total - GUITweenUtility.closeJeiTick);
                    float progress = duration <= 0 ? 1 : Math.min(1, elapsed / duration);

                    EmiCompat.inTween = true;

                    context.push();

                    // X 自动镜像：配置按左侧方向填写，右侧自动取反
                    float dx = TweenUtil.tween(0, leftSide
                            ? GUITween.CONFIG.closeJeiMoveX
                            : -GUITween.CONFIG.closeJeiMoveX, progress, GUITween.CONFIG.closeJeiMoveEase.get());
                    float dY = TweenUtil.tween(0, GUITween.CONFIG.closeJeiMoveY, progress, GUITween.CONFIG.closeJeiMoveEase.get());

                    context.matrices().translate(dx, dY , 0);

                    instance.render(context, mouseX, mouseY, delta);

                    context.pop();

                    EmiCompat.inTween = false;

                    return;
                }
                else if (leftSide) {
                    float totalTick = Math.max(GUITween.CONFIG.jeiMoveDuration, 1);
                    float progress = GUITweenUtility.jeiOpenTick / totalTick;

                    if (progress < 1){
                        EmiCompat.inTween = true;

                        context.push();

                        float dx = TweenUtil.tween(GUITween.CONFIG.jeiMoveX, 0, progress, GUITween.CONFIG.jeiMoveEase.get());
                        float dY = TweenUtil.tween(GUITween.CONFIG.jeiMoveY, 0, progress, GUITween.CONFIG.jeiMoveEase.get());

                        context.matrices().translate(dx, dY , 0);

                        instance.render(context, mouseX, mouseY, delta);

                        context.pop();

                        EmiCompat.inTween = false;

                        return;
                    }
                }
                else {
                    float totalTick = Math.max(GUITween.CONFIG.jeiMoveDuration, 1);
                    float progress = GUITweenUtility.jeiOpenTick / totalTick;

                    if (progress < 1){
                        EmiCompat.inTween = true;

                        context.push();

                        // X 自动镜像：配置按左侧方向填写，右侧自动取反
                        float dx = TweenUtil.tween(-GUITween.CONFIG.jeiMoveX, 0, progress, GUITween.CONFIG.jeiMoveEase.get());
                        float dY = TweenUtil.tween(GUITween.CONFIG.jeiMoveY, 0, progress, GUITween.CONFIG.jeiMoveEase.get());

                        context.matrices().translate(dx, dY , 0);

                        instance.render(context, mouseX, mouseY, delta);

                        context.pop();

                        EmiCompat.inTween = false;

                        return;
                    }
                }
            }
        }

        instance.render(context, mouseX, mouseY, delta);
    }
}
