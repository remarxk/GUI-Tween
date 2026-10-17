package com.remarxk.guitween.mixin.rei;

import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.compat.CompatUtility;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.impl.client.gui.ScreenOverlayImpl;
import me.shedaniel.rei.impl.client.gui.widget.entrylist.EntryListWidget;
import me.shedaniel.rei.impl.client.gui.widget.favorites.FavoritesListWidget;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ScreenOverlayImpl.class)
public class ScreenOverlayImpMixin {
    @Redirect(
            method = "renderWidgets",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/shedaniel/rei/api/client/gui/widgets/Widget;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"
            )
    )
    public void renderWidgets(Widget instance, GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (GUITweenUtility.openScreenName != null) {
            if (instance instanceof FavoritesListWidget) {
                CompatUtility.JeiTween jeiLeftTween = CompatUtility.getJeiLeftTween();
                if (jeiLeftTween.inTween) {
                    Matrix3x2fStack poseStack = graphics.pose();
                    poseStack.pushMatrix();
                    poseStack.translate(jeiLeftTween.dx, jeiLeftTween.dy);

                    instance.render(graphics, mouseX, mouseY, delta);

                    poseStack.popMatrix();
                    return;
                }
            } else if (instance instanceof EntryListWidget) {
                CompatUtility.JeiTween jeiRightTween = CompatUtility.getJeiRightTween();
                if (jeiRightTween.inTween) {
                    Matrix3x2fStack poseStack = graphics.pose();
                    poseStack.pushMatrix();
                    poseStack.translate(jeiRightTween.dx, jeiRightTween.dy);

                    instance.render(graphics, mouseX, mouseY, delta);

                    poseStack.popMatrix();
                    return;
                }
            } else {
                // 底部组件（搜索栏、配置按钮、可合成过滤按钮等）：
                // REI 覆盖层在 renderContainerBg 中被整体抵消了窗口位移，
                // 这里把窗口位移加回去，使其开/关位移参数与窗体动画完全一致。
                CompatUtility.OpenTween openTween = CompatUtility.getOpenTween();
                if (openTween.inTween) {
                    Matrix3x2fStack poseStack = graphics.pose();
                    poseStack.pushMatrix();
                    poseStack.translate(openTween.dx, openTween.dy);

                    instance.render(graphics, mouseX, mouseY, delta);

                    poseStack.popMatrix();
                    return;
                }
            }
        }

        instance.render(graphics, mouseX, mouseY, delta);
    }
}
