package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.Ease;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin <T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
    @Unique
    private boolean gUITween$inScale;

    @Unique
    private int gUITween$animTick;

    @Unique
    private Slot gUITween$lastHoverSlot;

    @Unique
    private boolean gUiTween$inTween;

    @Final
    @Shadow
    protected T menu;

    protected AbstractContainerScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Shadow
    private boolean isHovering(Slot pSlot, double pMouseX, double pMouseY){return false;}

    @Shadow
    public int getSlotColor(int index) { return 0; }

    @Unique
    private long gUITween$openTick;

    @Unique private static final float gUITween$MOVE_Y = 20f;

    @Unique
    private float gUITween$getProgress() {
        return Math.min((float) gUITween$openTick / GUITweenConfig.windowDuration.get(), 1f);
    }

    @Inject(method = "init", at = @At("HEAD"))
    public void init(CallbackInfo ci) {
        gUITween$openTick = 0;
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void renderBefore(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {
        if (!GUITweenConfig.enable.get())
            return;

        Slot hoveredSlot = null;

        for(int k = 0; k < this.menu.slots.size(); ++k) {
            Slot slot = this.menu.slots.get(k);
            if (this.isHovering(slot, (double)pMouseX, (double)pMouseY) && slot.isActive()) {
                hoveredSlot = slot;
                break;
            }
        }

        if (hoveredSlot != gUITween$lastHoverSlot) {
            gUITween$lastHoverSlot = hoveredSlot;
            gUITween$animTick = 0;
        }
    }

    @Inject(method = "renderBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderBg(Lnet/minecraft/client/gui/GuiGraphics;FII)V"))
    public void renderBgBefore(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!GUITweenConfig.enable.get())
            return;

        float t = gUITween$getProgress();

        if (t >= 1)
            return;

        gUITween$openTick++;

        gUiTween$inTween = true;

        float dy = TweenUtil.tween(gUITween$MOVE_Y, 0, t, GUITweenConfig.windowEase.get());

        PoseStack poseStack = guiGraphics.pose();

        // 动画变换
        poseStack.pushPose();
        poseStack.translate(0, dy, 0);  // 上移

        float alpha = TweenUtil.tween(0, 1, t, GUITweenConfig.windowEase.get());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.setColor(1f, 1f, 1f, alpha);
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    public void renderBgAfter(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!GUITweenConfig.enable.get())
            return;

        if (!gUiTween$inTween)
            return;

        gUiTween$inTween = false;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.popPose();
    }

    @Redirect(method = "renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/inventory/Slot;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;isHighlightable()Z"))
    public boolean renderSlotHighlightBefore(Slot instance) {
        if (!GUITweenConfig.enable.get())
            return instance.isHighlightable();

        return false;
    }

    @Inject(method = "renderSlot", at = @At(value = "HEAD"))
    public void renderItemBefore(GuiGraphics pGuiGraphics, Slot pSlot, CallbackInfo ci) {
        if (!GUITweenConfig.enable.get())
            return;

        boolean isHoverSlot = gUITween$lastHoverSlot == pSlot;

        if (isHoverSlot) {
            AbstractContainerScreen.renderSlotHighlight(pGuiGraphics, pSlot.x, pSlot.y, 0, getSlotColor(pSlot.index));

            gUITween$inScale = true;

            PoseStack poseStack = pGuiGraphics.pose();
            poseStack.pushPose();

            float progress = (float) gUITween$animTick / GUITweenConfig.hoverDuration.get();

            float itemSize = 16f; // 物品渲染尺寸（固定16x16）
            float scale = TweenUtil.tween(1, GUITweenConfig.hoverScale.get().floatValue(), progress, GUITweenConfig.hoverEase.get());   // 放大比例
            float centerX = pSlot.x + itemSize / 2; // 物品中心X
            float centerY = pSlot.y + itemSize / 2; // 物品中心Y

            // 矩阵操作：平移到中心 → 缩放 → 平移回原位置
            poseStack.translate(centerX, centerY, 0);
            poseStack.scale(scale, scale, 1.0f); // Z轴缩放不影响2D渲染，设为1
            poseStack.translate(-centerX, -centerY, 50f);

            gUITween$animTick++;
        }
    }

    @Inject(method = "renderFloatingItem", at = @At(value = "HEAD"))
    public void renderFloatingItemBefore(GuiGraphics guiGraphics, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        if (!GUITweenConfig.enable.get())
            return;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        float itemSize = 16f; // 物品渲染尺寸（固定16x16）
        float scale = GUITweenConfig.hoverScale.get().floatValue();   // 放大比例
        float centerX = x + itemSize / 2; // 物品中心X
        float centerY = y + itemSize / 2; // 物品中心Y

        // 矩阵操作：平移到中心 → 缩放 → 平移回原位置
        poseStack.translate(centerX, centerY, 0);
        poseStack.scale(scale, scale, 1.0f); // Z轴缩放不影响2D渲染，设为1
        poseStack.translate(-centerX, -centerY, 50f);
    }

    @Inject(method = "renderFloatingItem", at = @At(value = "TAIL"))
    public void renderFloatingItemAfter(GuiGraphics guiGraphics, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        if (!GUITweenConfig.enable.get())
            return;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.popPose();
    }

    @Inject(method = "renderSlot", at = @At(value = "TAIL"))
    public void renderItemAfter(GuiGraphics pGuiGraphics, Slot pSlot, CallbackInfo ci) {
        if (gUITween$inScale) {
            pGuiGraphics.pose().popPose();
            gUITween$inScale = false;
        }
    }

    @Inject(method = "onClose", at = @At("TAIL"))
    public void onClose(CallbackInfo ci) {
        gUITween$lastHoverSlot = null;
        gUITween$animTick = 0;
        gUITween$inScale = false;
    }
}
