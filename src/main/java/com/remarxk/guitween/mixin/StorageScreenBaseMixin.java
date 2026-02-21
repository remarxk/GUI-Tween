package com.remarxk.guitween.mixin;

import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.InventoryScrollPanel;
import net.p3pp3rf1y.sophisticatedcore.common.gui.StorageContainerMenuBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StorageScreenBase.class, remap = false)
public abstract class StorageScreenBaseMixin<S extends StorageContainerMenuBase<?>> extends AbstractContainerScreen<S> implements InventoryScrollPanel.IInventoryScreen {
    public StorageScreenBaseMixin(S menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "HEAD"
            )
    )
    public void renderBefore(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!(this instanceof AbstractContainerScreenMixinAccess access))
            return;

        GUITweenUtility.setOpenScreen(access.getGUITween$screenName(), access.getGUITween$openTick());

//        if (!GUITweenConfig.isEnableWindow())
//            return;
//
//        if (access.getGUITween$isDisableScreenTween())
//            return;
//
//        float moveProgress = access.getGUITween$openTick() / GUITweenConfig.window.moveDuration.get().floatValue();
//        float gradientProgress = access.getGUITween$openTick() / GUITweenConfig.window.gradientDuration.get().floatValue();
//
//        if (moveProgress >= 1 && gradientProgress >= 1)
//            return;
//
//        access.setGUITween$inTween(true);
//
//        float dx = TweenUtil.tween(GUITweenConfig.window.moveX.get().floatValue(), 0, moveProgress, GUITweenConfig.window.moveEase.get());
//        float dy = TweenUtil.tween(GUITweenConfig.window.moveY.get().floatValue(), 0, moveProgress, GUITweenConfig.window.moveEase.get());
//
//        PoseStack poseStack = guiGraphics.pose();
//
//        // 动画变换
//        poseStack.pushPose();
//        poseStack.translate(dx, dy, 0);  // 上移
//
//        float alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, gradientProgress, GUITweenConfig.window.gradientEase.get());
//        GUITweenUtility.pushAlpha(alpha);
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "TAIL"
            )
    )
    public void renderAfter(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!(this instanceof AbstractContainerScreenMixinAccess access))
            return;

        access.setGUITween$openTick(access.getGUITween$openTick() + GUITweenUtility.getDeltaTicks());

//        if (!access.getGUITween$inTween())
//            return;
//
//        GUITweenUtility.popAlpha();
//
//        PoseStack poseStack = guiGraphics.pose();
//        poseStack.popPose();
//
//        access.setGUITween$inTween(false);
    }
}
