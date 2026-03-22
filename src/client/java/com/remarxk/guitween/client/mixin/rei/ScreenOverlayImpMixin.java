package com.remarxk.guitween.client.mixin.rei;

import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.compat.ReiCompat;
import com.remarxk.guitween.client.util.TweenUtil;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.impl.client.gui.ScreenOverlayImpl;
import me.shedaniel.rei.impl.client.gui.widget.entrylist.EntryListWidget;
import me.shedaniel.rei.impl.client.gui.widget.favorites.FavoritesListWidget;
import net.minecraft.client.gui.DrawContext;
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
                    target = "Lme/shedaniel/rei/api/client/gui/widgets/Widget;render(Lnet/minecraft/client/gui/DrawContext;IIF)V"
            )
    )
    public void renderWidgets(Widget widget, DrawContext guiGraphics, int mouseX, int mouseY, float delta) {
        if (GUITweenUtility.openScreenName != null) {
            if (GUITweenClient.CONFIG.isEnableJeiLeft() && widget instanceof FavoritesListWidget favoritesListWidget) {
                float totalTick = Math.max(GUITweenClient.CONFIG.jeiLeftMoveDuration, 1);
                float progress = GUITweenUtility.openScreenTick / totalTick;

                if (progress < 1){
                    Matrix3x2fStack poseStack = guiGraphics.getMatrices();

                    float dx = TweenUtil.tween(GUITweenClient.CONFIG.jeiLeftMoveX, 0, progress, GUITweenClient.CONFIG.jeiLeftMoveEase.get());
                    float dy = TweenUtil.tween(GUITweenClient.CONFIG.jeiLeftMoveY, 0, progress, GUITweenClient.CONFIG.jeiLeftMoveEase.get());

                    ReiCompat.inTween = true;
                    ReiCompat.dx = dx;
                    ReiCompat.dy = dy;

                    poseStack.pushMatrix();
                    poseStack.translate(dx, dy);

                    widget.render(guiGraphics, mouseX, mouseY, delta);

                    poseStack.popMatrix();

                    ReiCompat.inTween = false;

                    return;
                }
            }

            if (GUITweenClient.CONFIG.isEnableJeiRight() && widget instanceof EntryListWidget entryListWidget) {
                float totalTick = Math.max(GUITweenClient.CONFIG.jeiRightMoveDuration, 1);
                float progress = GUITweenUtility.openScreenTick / totalTick;

                if (progress < 1){
                    Matrix3x2fStack poseStack = guiGraphics.getMatrices();

                    float dx = TweenUtil.tween(GUITweenClient.CONFIG.jeiRightMoveX, 0, progress, GUITweenClient.CONFIG.jeiRightMoveEase.get());
                    float dy = TweenUtil.tween(GUITweenClient.CONFIG.jeiRightMoveY, 0, progress, GUITweenClient.CONFIG.jeiRightMoveEase.get());

                    ReiCompat.inTween = true;
                    ReiCompat.dx = dx;
                    ReiCompat.dy = dy;

                    poseStack.pushMatrix();
                    poseStack.translate(dx, dy);

                    widget.render(guiGraphics, mouseX, mouseY, delta);

                    poseStack.popMatrix();

                    ReiCompat.inTween = false;

                    return;
                }
            }
        }

        widget.render(guiGraphics, mouseX, mouseY, delta);
    }
}
