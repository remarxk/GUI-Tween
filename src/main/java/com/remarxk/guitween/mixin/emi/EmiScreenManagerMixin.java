package com.remarxk.guitween.mixin.emi;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.compat.EmiCompat;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.TweenUtil;
import dev.emi.emi.config.SidebarSide;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.EmiScreenManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EmiScreenManager.class)
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
            if (GUITweenConfig.isEnableJei()) {
                boolean closing = GUITweenUtility.isWindowClosing;
                boolean leftSide = instance.side == SidebarSide.LEFT;

                if (closing) {
                    float duration = GUITweenConfig.window.closeJeiMoveDuration.get().floatValue();
                    float total = GUITweenConfig.getCloseJeiTotalDuration();
                    float elapsed = Math.max(0, total - GUITweenUtility.closeJeiTick);
                    float progress = duration <= 0 ? 1 : Math.min(1, elapsed / duration);

                    EmiCompat.inTween = true;

                    context.push();

                    // X 自动镜像：配置按左侧方向填写，右侧自动取反
                    float dx = TweenUtil.tween(0, leftSide
                            ? GUITweenConfig.window.closeJeiMoveX.get().floatValue()
                            : -GUITweenConfig.window.closeJeiMoveX.get().floatValue(), progress, GUITweenConfig.window.closeJeiMoveEase.get());
                    float dY = TweenUtil.tween(0, GUITweenConfig.window.closeJeiMoveY.get().floatValue(), progress, GUITweenConfig.window.closeJeiMoveEase.get());

                    context.matrices().translate(dx, dY, 0);

                    instance.render(context, mouseX, mouseY, delta);

                    context.pop();

                    EmiCompat.inTween = false;

                    return;
                }
                else if (leftSide) {
                    float totalTick = Math.max(GUITweenConfig.window.jeiMoveDuration.get().floatValue(), 1);
                    float progress = GUITweenUtility.jeiOpenTick / totalTick;

                    if (progress < 1){
                        EmiCompat.inTween = true;

                        context.push();

                        float dx = TweenUtil.tween(GUITweenConfig.window.jeiMoveX.get().floatValue(), 0, progress, GUITweenConfig.window.jeiMoveEase.get());
                        float dY = TweenUtil.tween(GUITweenConfig.window.jeiMoveY.get().floatValue(), 0, progress, GUITweenConfig.window.jeiMoveEase.get());

                        context.matrices().translate(dx, dY , 0);

                        instance.render(context, mouseX, mouseY, delta);

                        context.pop();

                        EmiCompat.inTween = false;

                        return;
                    }
                }
                else {
                    float totalTick = Math.max(GUITweenConfig.window.jeiMoveDuration.get().floatValue(), 1);
                    float progress = GUITweenUtility.jeiOpenTick / totalTick;

                    if (progress < 1){
                        EmiCompat.inTween = true;

                        context.push();

                        // X 自动镜像：配置按左侧方向填写，右侧自动取反
                        float dx = TweenUtil.tween(-GUITweenConfig.window.jeiMoveX.get().floatValue(), 0, progress, GUITweenConfig.window.jeiMoveEase.get());
                        float dY = TweenUtil.tween(GUITweenConfig.window.jeiMoveY.get().floatValue(), 0, progress, GUITweenConfig.window.jeiMoveEase.get());

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
