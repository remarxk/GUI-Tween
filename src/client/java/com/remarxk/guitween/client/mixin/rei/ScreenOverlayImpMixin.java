package com.remarxk.guitween.client.mixin.rei;

import com.remarxk.guitween.client.compat.CompatUtility;
import com.remarxk.guitween.client.compat.ReiCompat;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.impl.client.gui.ScreenOverlayImpl;
import me.shedaniel.rei.impl.client.gui.widget.entrylist.EntryListWidget;
import me.shedaniel.rei.impl.client.gui.widget.favorites.FavoritesListWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ScreenOverlayImpl.class)
public class ScreenOverlayImpMixin {
    @Redirect(
            method = "renderWidgets",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/shedaniel/rei/api/client/gui/widgets/Widget;render(Lnet/minecraft/client/gui/DrawContext;IIF)V"
            )
    )
    public void renderWidgets(Widget instance, DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if (com.remarxk.guitween.client.GUITweenUtility.openScreenName != null) {
            if (instance instanceof FavoritesListWidget) {
                CompatUtility.JeiTween jeiTween = CompatUtility.getJeiLeftTween();
                if (jeiTween.inTween) {
                    MatrixStack poseStack = drawContext.getMatrices();
                    ReiCompat.inTween = true;
                    ReiCompat.dx = jeiTween.dx;
                    ReiCompat.dy = jeiTween.dy;

                    poseStack.push();
                    poseStack.translate(jeiTween.dx, jeiTween.dy, 0);
                    instance.render(drawContext, mouseX, mouseY, delta);
                    poseStack.pop();

                    ReiCompat.inTween = false;
                    return;
                }
            }
            else if (instance instanceof EntryListWidget) {
                CompatUtility.JeiTween jeiTween = CompatUtility.getJeiRightTween();
                if (jeiTween.inTween) {
                    MatrixStack poseStack = drawContext.getMatrices();
                    ReiCompat.inTween = true;
                    ReiCompat.dx = jeiTween.dx;
                    ReiCompat.dy = jeiTween.dy;

                    poseStack.push();
                    poseStack.translate(jeiTween.dx, jeiTween.dy, 0);
                    instance.render(drawContext, mouseX, mouseY, delta);
                    poseStack.pop();

                    ReiCompat.inTween = false;
                    return;
                }
            }
            else {
                // 底部组件（搜索栏、配置按钮、可合成过滤按钮等）：
                // REI 覆盖层在 renderContainerBg 中被整体抵消了窗口位移，
                // 这里把窗口位移加回去，使其开/关位移参数与窗体动画完全一致。
                CompatUtility.OpenTween openTween = CompatUtility.getOpenTween();
                if (openTween.inTween) {
                    MatrixStack poseStack = drawContext.getMatrices();
                    poseStack.push();
                    poseStack.translate(openTween.dx, openTween.dy, 0);

                    instance.render(drawContext, mouseX, mouseY, delta);

                    poseStack.pop();
                    return;
                }
            }
        }

        instance.render(drawContext, mouseX, mouseY, delta);
    }
}
