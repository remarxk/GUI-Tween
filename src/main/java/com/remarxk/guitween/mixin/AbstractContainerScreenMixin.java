package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.remarxk.guitween.AnimationState;
import com.remarxk.guitween.DataPack.WindowSlotsConfig;
import com.remarxk.guitween.DataPack.WindowSlotsLoader;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.AnimationStatePool;
import com.remarxk.guitween.util.Ease;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.HashMap;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin <T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
    @Unique
    private String gUITween$screenName;

    @Unique
    private boolean gUITween$inScale;

    @Unique
    private Slot gUITween$lastHoverSlot;

    @Unique
    private HashMap<Slot, AnimationState> gUITween$hoverSlotMap = new HashMap<>();

    @Unique
    private HashMap<Integer, Boolean> gUITween$OutputSlotIsEmpty = new HashMap<>();

    @Unique
    private HashMap<Integer, Float> gUITween$outputSlotTicks = new HashMap<>();

    @Unique
    private float gUITween$clickTime = 0;

    @Unique boolean gUITween$isFloatingTween;

    @Unique
    private float gUITween$tooltipShowTick;

    @Final
    @Shadow
    protected T menu;

    @Nullable
    @Shadow
    private Slot lastClickSlot;

    protected AbstractContainerScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Nullable
    @Shadow
    private Slot findSlot(double mouseX, double mouseY) { return null; }

    @Shadow
    public int getSlotColor(int index) { return 0; }

    @Shadow
    protected int leftPos;

    @Shadow
    protected int topPos;

    @Shadow
    protected int imageHeight;

    @Inject(method = "init", at = @At("HEAD"))
    public void init(CallbackInfo ci) {
        gUITween$screenName = getClass().getSimpleName();
        gUITween$OutputSlotIsEmpty.clear();
        gUITween$outputSlotTicks.clear();

        WindowSlotsConfig config = WindowSlotsLoader.configs.getOrDefault(gUITween$screenName, null);
        if (config != null) {
            for (int slotIndex : config.outputSlots) {
                Slot slot = menu.slots.get(slotIndex);
                gUITween$OutputSlotIsEmpty.put(slotIndex, !slot.hasItem());
            }
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void renderBefore(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {
        if (!GUITween.CONFIG.isEnable())
            return;

        Slot hoveredSlot = findSlot(pMouseX, pMouseY);

        if (hoveredSlot != gUITween$lastHoverSlot) {
            if (gUITween$lastHoverSlot != null) {
                AnimationState state = gUITween$hoverSlotMap.getOrDefault(gUITween$lastHoverSlot, null);
                if (state != null)
                    state.rewind = true;
            }

            if (gUITween$lastHoverSlot == null || !gUITween$lastHoverSlot.hasItem()) {
                if (GUITween.CONFIG.isEnableTooltip())
                    gUITween$tooltipShowTick = 0;
            }

            gUITween$lastHoverSlot = hoveredSlot;

            if (gUITween$lastHoverSlot != null) {
                AnimationState state = gUITween$hoverSlotMap.getOrDefault(gUITween$lastHoverSlot, null);
                if (state == null) {
                    state = AnimationStatePool.getAnimationState();
                    state.tick = 0;
                    state.totalTick = GUITween.CONFIG.hoverDuration;
                    state.ease = GUITween.CONFIG.hoverEase.get();
                    state.startValue = 1;
                    state.stopValue = GUITween.CONFIG.hoverScale;
                    state.rewind = false;
                    gUITween$hoverSlotMap.put(gUITween$lastHoverSlot, state);
                }
                else {
                    state.rewind = false;
                }
            }
        }
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    public void renderScreenName(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!GUITween.CONFIG.isEnableDebugWindow())
            return;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, 0, 1000);

        // 左上角偏移（界面内部）
        int x = this.leftPos + 2;
        int y = this.topPos - 10;

        if ((((AbstractContainerScreen) (Object) this) instanceof CreativeModeInventoryScreen)) {
            y -= 30;
        }

        guiGraphics.drawString(
                this.font,
                gUITween$screenName,
                x,
                y,
                0xFF0000, // 浅灰色
                false
        );

        poseStack.popPose();

        if (GUITweenUtility.isInTween(GUITweenUtility.OPEN_WINDOW)) {
            GUITweenUtility.setInTween(GUITweenUtility.OPEN_WINDOW, false);
            poseStack.popPose();
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;isHighlightable()Z"))
    public boolean renderSlotHighlightBefore(Slot instance) {
        if (!GUITween.CONFIG.isEnableHoverItem())
            return instance.isHighlightable();

        return false;
    }

    @Inject(method = "renderSlot", at = @At(value = "HEAD"))
    public void renderItemBefore(GuiGraphics pGuiGraphics, Slot pSlot, CallbackInfo ci) {
        boolean haveTween = false;
        float scale = 1;

        PoseStack poseStack = pGuiGraphics.pose();
        float itemSize = 16f; // 物品渲染尺寸（固定16x16）
        float centerX = pSlot.x + itemSize / 2; // 物品中心X
        float centerY = pSlot.y + itemSize / 2; // 物品中心Y

        boolean isEmpty = !pSlot.hasItem();

        if (GUITween.CONFIG.isEnableOutput()) {
            if (gUITween$OutputSlotIsEmpty.containsKey(pSlot.index)) {
                boolean lastIsEmpty = gUITween$OutputSlotIsEmpty.get(pSlot.index);
                if (lastIsEmpty != isEmpty) {
                    gUITween$OutputSlotIsEmpty.put(pSlot.index, isEmpty);
                    if (!isEmpty) {
                        gUITween$outputSlotTicks.put(pSlot.index, 0f);
                    }
                }

                if (gUITween$outputSlotTicks.containsKey(pSlot.index)) {
                    float tick = gUITween$outputSlotTicks.get(pSlot.index);
                    if (tick >= 8) {
                        gUITween$outputSlotTicks.remove(pSlot.index);
                    }
                    else {
                        float progress = tick / 6;
                        scale = TweenUtil.tween(0, 1, progress, Ease.OUT_BACK);

                        tick += GUITweenUtility.getDeltaTicks();
                        gUITween$outputSlotTicks.put(pSlot.index, tick);

                        haveTween = true;
                    }
                }
            }
        }

        if (GUITween.CONFIG.isEnableHoverItem()) {
            boolean isHoverSlot = gUITween$lastHoverSlot == pSlot;

            if (isHoverSlot) {
                AbstractContainerScreen.renderSlotHighlight(pGuiGraphics, pSlot.x, pSlot.y, 0, getSlotColor(pSlot.index));
            }

            AnimationState state = gUITween$hoverSlotMap.getOrDefault(pSlot, null);
            if (state != null) {
                if (isEmpty) {
                    AnimationStatePool.releaseAnimationState(state);
                    gUITween$hoverSlotMap.remove(pSlot);
                }
                else {
                    haveTween = true;

                    scale = TweenUtil.tween(state.startValue, state.stopValue, state.tick, state.totalTick, state.ease);   // 放大比例

                    int sign = state.rewind ? -1 : 1;

                    state.tick += sign * GUITweenUtility.getDeltaTicks();
                    state.tick = TweenUtil.clamp(state.tick, 0, state.totalTick);

                    if (state.rewind && state.tick <= 0) {
                        AnimationStatePool.releaseAnimationState(state);
                        gUITween$hoverSlotMap.remove(pSlot);
                    }
                }
            }
        }

        if (haveTween) {
            gUITween$inScale = true;

            poseStack.pushPose();

            // 矩阵操作：平移到中心 → 缩放 → 平移回原位置
            poseStack.translate(centerX, centerY, 0);
            poseStack.scale(scale, scale, 1.0f); // Z轴缩放不影响2D渲染，设为1
            poseStack.translate(-centerX, -centerY, 50);
        }
    }

    @Inject(method = "renderSlot", at = @At(value = "TAIL"))
    public void renderItemAfter(GuiGraphics pGuiGraphics, Slot pSlot, CallbackInfo ci) {
        if (gUITween$inScale) {
            pGuiGraphics.pose().popPose();
            gUITween$inScale = false;
        }

        if (GUITween.CONFIG.isEnableDebugWindow()) {
            Font font = Minecraft.getInstance().font;

            // 获取格子左上角坐标
            int x = pSlot.x; // Slot 的 x 坐标
            int y = pSlot.y; // Slot 的 y 坐标

            String text = String.valueOf(pSlot.index);
            int color = 0xFF0000; // 白色文字
            boolean shadow = true; // 阴影，让文字在物品上更清晰

            PoseStack poseStack = pGuiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(0, 0, 1000);
            pGuiGraphics.drawString(font, text, x + 1, y + 1, color, shadow);
            poseStack.popPose();
        }
    }

    @Inject(method = "mouseClicked", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;lastClickSlot:Lnet/minecraft/world/inventory/Slot;", shift = At.Shift.AFTER))
    public void restClickTime(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (GUITween.CONFIG.isEnableClickItem()) {
            if (lastClickSlot == null) {
                gUITween$clickTime = 0;
            }
            else {
                gUITween$clickTime = GUITween.CONFIG.clickItemDuration;
            }
        }
    }

    @Inject(method = "renderFloatingItem", at = @At(value = "HEAD"))
    public void renderFloatingItemBefore(GuiGraphics guiGraphics, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        boolean hasChange = false;

        float itemSize = 16f; // 物品渲染尺寸（固定16x16）
        float centerX = x + itemSize / 2; // 物品中心X
        float centerY = y + itemSize / 2; // 物品中心Y

        float scale = 1;   // 放大比例
        float angle = 0;

        if (GUITween.CONFIG.isEnableHoverItem()) {
            scale = GUITween.CONFIG.hoverScale;
            hasChange = true;
        }

        if (gUITween$clickTime > 0) {
            float progress = 1 - gUITween$clickTime / GUITween.CONFIG.clickItemDuration;
            scale = scale * TweenUtil.punch(scale - 1, 1, progress);
//            angle = (TweenUtil.punch(0.20f, 2, progress) - 1) * 100;

            gUITween$clickTime -= GUITweenUtility.getDeltaTicks();

            hasChange = true;
        }

        if (hasChange) {
            gUITween$isFloatingTween = true;

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();

            // 矩阵操作：平移到中心 → 缩放 → 平移回原位置
            poseStack.translate(centerX, centerY, 0);
            poseStack.scale(scale, scale, 1.0f); // Z轴缩放不影响2D渲染，设为1
            poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
            poseStack.translate(-centerX, -centerY, 0);
        }
    }

    @Inject(method = "renderFloatingItem", at = @At(value = "TAIL"))
    public void renderFloatingItemAfter(GuiGraphics guiGraphics, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        if (!gUITween$isFloatingTween)
            return;

        gUITween$isFloatingTween = false;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.popPose();
    }

    @Inject(method = "renderTooltip", at = @At(value = "HEAD"))
    public void renderTooltipBefore(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci) {
        if (!GUITween.CONFIG.isEnableTooltip())
            return;

        float duration = GUITween.CONFIG.tooltipDuration;
        if (gUITween$tooltipShowTick > duration)
            return;

        float progress = gUITween$tooltipShowTick / duration;
        float alpha = TweenUtil.tween(0, 1, progress, GUITween.CONFIG.tooltipEase.get());
        guiGraphics.setColor(1, 1, 1, alpha);

        GUITweenUtility.setInTween(GUITweenUtility.TOOL_TIP, true);
        GUITweenUtility.setTweenValue(GUITweenUtility.TOOL_TIP_ALPHA, alpha);

        gUITween$tooltipShowTick += GUITweenUtility.getDeltaTicks();
    }

    @Inject(method = "renderTooltip", at = @At(value = "TAIL"))
    public void renderTooltipAfter(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci) {
        if (!GUITweenUtility.isInTween(GUITweenUtility.TOOL_TIP))
            return;

        GUITweenUtility.setInTween(GUITweenUtility.TOOL_TIP, false);
        guiGraphics.setColor(1, 1, 1, 1);
    }

    @Inject(method = "onClose", at = @At("TAIL"))
    public void onClose(CallbackInfo ci) {
        gUITween$lastHoverSlot = null;
        gUITween$inScale = false;

        gUITween$hoverSlotMap.forEach((slot, state) -> {
            AnimationStatePool.releaseAnimationState(state);
        });
        gUITween$hoverSlotMap.clear();

        gUITween$tooltipShowTick = 0;
    }
}
