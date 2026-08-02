package com.remarxk.guitween.mixin.rei;

import com.mojang.blaze3d.vertex.PoseStack;
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

        if (widget instanceof EntryListWidget) {
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

        widget.render(guiGraphics, mouseX, mouseY, delta);
    }
}
