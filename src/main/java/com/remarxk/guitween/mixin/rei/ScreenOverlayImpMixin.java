package com.remarxk.guitween.mixin.rei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.compat.CompatUtility;
import com.remarxk.guitween.compat.ReiCompat;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.impl.client.gui.ScreenOverlayImpl;
import me.shedaniel.rei.impl.client.gui.widget.entrylist.EntryListWidget;
import me.shedaniel.rei.impl.client.gui.widget.favorites.FavoritesListWidget;
import net.minecraft.client.gui.GuiGraphics;
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
    public void renderWidgets(Widget widget, GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (GUITweenUtility.openScreenName != null) {
            if (widget instanceof FavoritesListWidget) {
                CompatUtility.JeiTween jeiTween = CompatUtility.getJeiLeftTween();
                if (jeiTween.inTween) {
                    PoseStack poseStack = guiGraphics.pose();
                    ReiCompat.inTween = true;
                    ReiCompat.dx = jeiTween.dx;
                    ReiCompat.dy = jeiTween.dy;

                    poseStack.pushPose();
                    poseStack.translate(jeiTween.dx, jeiTween.dy, 0);
                    widget.render(guiGraphics, mouseX, mouseY, delta);
                    poseStack.popPose();

                    ReiCompat.inTween = false;
                    return;
                }
            }
            else if (widget instanceof EntryListWidget) {
                CompatUtility.JeiTween jeiTween = CompatUtility.getJeiRightTween();
                if (jeiTween.inTween) {
                    PoseStack poseStack = guiGraphics.pose();
                    ReiCompat.inTween = true;
                    ReiCompat.dx = jeiTween.dx;
                    ReiCompat.dy = jeiTween.dy;

                    poseStack.pushPose();
                    poseStack.translate(jeiTween.dx, jeiTween.dy, 0);
                    widget.render(guiGraphics, mouseX, mouseY, delta);
                    poseStack.popPose();

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
                    PoseStack poseStack = guiGraphics.pose();
                    poseStack.pushPose();
                    poseStack.translate(openTween.dx, openTween.dy, 0);

                    widget.render(guiGraphics, mouseX, mouseY, delta);

                    poseStack.popPose();
                    return;
                }
            }
        }

        widget.render(guiGraphics, mouseX, mouseY, delta);
    }
}
