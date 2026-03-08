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
            if (GUITween.CONFIG.isEnableJeiLeft()) {
                if (instance.side == SidebarSide.LEFT) {
                    float totalTick = Math.max(GUITween.CONFIG.jeiLeftMoveDuration, 1);
                    float progress = GUITweenUtility.openScreenTick / totalTick;

                    if (progress < 1){
                        EmiCompat.inTween = true;

                        context.push();

                        float dx = TweenUtil.tween(GUITween.CONFIG.jeiLeftMoveX, 0, progress, GUITween.CONFIG.jeiLeftMoveEase.get());
                        float dY = TweenUtil.tween(GUITween.CONFIG.jeiLeftMoveY, 0, progress, GUITween.CONFIG.jeiLeftMoveEase.get());

                        context.matrices().translate(dx, dY , 0);

                        instance.render(context, mouseX, mouseY, delta);

                        context.pop();

                        EmiCompat.inTween = false;

                        return;
                    }
                }
                else if (instance.side == SidebarSide.RIGHT) {
                    float totalTick = Math.max(GUITween.CONFIG.jeiRightMoveDuration, 1);
                    float progress = GUITweenUtility.openScreenTick / totalTick;

                    if (progress < 1){
                        EmiCompat.inTween = true;

                        context.push();

                        float dx = TweenUtil.tween(GUITween.CONFIG.jeiRightMoveX, 0, progress, GUITween.CONFIG.jeiRightMoveEase.get());
                        float dY = TweenUtil.tween(GUITween.CONFIG.jeiRightMoveY, 0, progress, GUITween.CONFIG.jeiRightMoveEase.get());

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
