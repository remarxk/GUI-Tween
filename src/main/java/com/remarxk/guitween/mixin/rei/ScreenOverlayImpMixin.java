package com.remarxk.guitween.mixin.rei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.compat.ReiCompat;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.TweenUtil;
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
            if (GUITweenConfig.isEnableJeiLeft() && widget instanceof FavoritesListWidget favoritesListWidget) {
                float totalTick = Math.max(GUITweenConfig.window.jeiLeftMoveDuration.get().floatValue(), 1);
                float progress = GUITweenUtility.openScreenTick / totalTick;

                if (progress < 1){
                    PoseStack poseStack = guiGraphics.pose();

                    float dx = TweenUtil.tween(GUITweenConfig.window.jeiLeftMoveX.get().floatValue(), 0, progress, GUITweenConfig.window.jeiLeftMoveEase.get());
                    float dy = TweenUtil.tween(GUITweenConfig.window.jeiLeftMoveY.get().floatValue(), 0, progress, GUITweenConfig.window.jeiLeftMoveEase.get());

                    ReiCompat.inTween = true;
                    ReiCompat.dx = dx;
                    ReiCompat.dy = dy;

                    poseStack.pushPose();
                    poseStack.translate(dx, dy, 0);

                    widget.render(guiGraphics, mouseX, mouseY, delta);

                    poseStack.popPose();

                    ReiCompat.inTween = false;

                    return;
                }
            }

            if (GUITweenConfig.isEnableJeiRight() && widget instanceof EntryListWidget entryListWidget) {
                float totalTick = Math.max(GUITweenConfig.window.jeiRightMoveDuration.get().floatValue(), 1);
                float progress = GUITweenUtility.openScreenTick / totalTick;

                if (progress < 1){
                    PoseStack poseStack = guiGraphics.pose();

                    float dx = TweenUtil.tween(GUITweenConfig.window.jeiRightMoveX.get().floatValue(), 0, progress, GUITweenConfig.window.jeiRightMoveEase.get());
                    float dy = TweenUtil.tween(GUITweenConfig.window.jeiRightMoveY.get().floatValue(), 0, progress, GUITweenConfig.window.jeiRightMoveEase.get());

                    ReiCompat.inTween = true;
                    ReiCompat.dx = dx;
                    ReiCompat.dy = dy;

                    poseStack.pushPose();
                    poseStack.translate(dx, dy, 0);

                    widget.render(guiGraphics, mouseX, mouseY, delta);

                    poseStack.popPose();

                    ReiCompat.inTween = false;

                    return;
                }
            }
        }

        widget.render(guiGraphics, mouseX, mouseY, delta);
    }
}
