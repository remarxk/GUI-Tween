package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.anim.Tween;
import com.remarxk.guitween.dataPack.WindowSlotsConfig;
import com.remarxk.guitween.dataPack.WindowSlotsLoader;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.anim.TweenPool;
import com.remarxk.guitween.util.Ease;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
public abstract class AbstractContainerScreenMixin <T extends AbstractContainerMenu> extends Screen implements MenuAccess<T>, AbstractContainerScreenMixinAccess {
    @Unique
    private static final ResourceLocation COPY_TEXTURE = ResourceLocation.fromNamespaceAndPath(GUITween.MODID, "copy");

    @Unique
    private static final ResourceLocation COPY_HOVER_TEXTURE = ResourceLocation.fromNamespaceAndPath(GUITween.MODID, "copy_hover");

    @Unique
    private boolean gUITween$isDisableScreenTween;

    @Unique
    private String gUITween$screenName;

    @Unique
    private boolean gUITween$inSlotTween;

    @Unique
    private Slot gUITween$lastHoverSlot;

    @Unique
    private HashMap<Slot, Tween> gUITween$hoverSlotMap = new HashMap<>();

    @Unique
    private HashMap<Integer, Boolean> gUITween$OutputSlotIsEmpty = new HashMap<>();

    @Unique
    private HashMap<Integer, Float> gUITween$outputSlotTicks = new HashMap<>();

    @Unique
    private float gUITween$clickTime = 0;

    @Unique boolean gUITween$isFloatingTween;

    @Unique
    private float gUITween$tooltipShowTick;
    
    @Unique
    private boolean gUiTween$inTween;

    @Unique
    private float gUITween$openTick;

    @Unique
    private boolean gUITween$inTooltip;

    @Unique
    private Button gUITween$copyNameBtn;

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

    @Override
    public boolean getGUITween$inTween() {
        return gUiTween$inTween;
    }

    @Override
    public void setGUITween$inTween(boolean inTween) {
        gUiTween$inTween = inTween;
    }

    @Override
    public float getGUITween$openTick() {
        return gUITween$openTick;
    }

    @Override
    public void setGUITween$openTick(float openTick) {
        gUITween$openTick = openTick;
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        gUITween$openTick = 0;

        gUITween$screenName = getClass().getSimpleName();
        gUITween$isDisableScreenTween = GUITweenConfig.isDisableTweenWindow(gUITween$screenName);
        gUITween$OutputSlotIsEmpty.clear();
        gUITween$outputSlotTicks.clear();

        WindowSlotsConfig config = WindowSlotsLoader.configs.getOrDefault(gUITween$screenName, null);
        if (config != null) {
            for (int slotIndex : config.outputSlots) {
                Slot slot = menu.slots.get(slotIndex);
                gUITween$OutputSlotIsEmpty.put(slotIndex, !slot.hasItem());
            }
        }

        if (gUITween$copyNameBtn == null) {
            gUITween$copyNameBtn = new ImageButton(
                    0, 0,
                    8, 8,
                    new WidgetSprites(COPY_TEXTURE, COPY_TEXTURE, COPY_HOVER_TEXTURE),
                    button -> {
                        Minecraft.getInstance().keyboardHandler.setClipboard(gUITween$screenName);
                    }
            );
        }

        if (GUITweenConfig.isEnableDebugWindow()) {
            int x = this.leftPos + 2;
            int y = this.topPos - 10;
            if ((((AbstractContainerScreen) (Object) this) instanceof CreativeModeInventoryScreen)) {
                y -= 30;
            }
            gUITween$copyNameBtn.setPosition(x, y);

            addRenderableWidget(gUITween$copyNameBtn);
        }
        else {
            removeWidget(gUITween$copyNameBtn);
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void renderBefore(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {
        if (!GUITweenConfig.isEnable())
            return;

        Slot hoveredSlot = findSlot(pMouseX, pMouseY);

        if (hoveredSlot != gUITween$lastHoverSlot) {
            if (gUITween$lastHoverSlot != null) {
                Tween tween = gUITween$hoverSlotMap.getOrDefault(gUITween$lastHoverSlot, null);
                if (tween != null)
                    tween.rewind = true;
            }

            if (gUITween$lastHoverSlot == null || !gUITween$lastHoverSlot.hasItem()) {
                if (GUITweenConfig.isEnableTooltip())
                    gUITween$tooltipShowTick = 0;
            }

            gUITween$lastHoverSlot = hoveredSlot;

            if (gUITween$lastHoverSlot != null) {
                Tween tween = gUITween$hoverSlotMap.getOrDefault(gUITween$lastHoverSlot, null);
                if (tween == null) {
                    tween = TweenPool.getTween();
                    tween.tick = 0;
                    tween.totalTick = GUITweenConfig.windowItem.hoverDuration.get().floatValue();
                    tween.ease = GUITweenConfig.windowItem.hoverEase.get();
                    tween.startValue = 1;
                    tween.stopValue = GUITweenConfig.windowItem.hoverScale.get().floatValue();
                    tween.rewind = false;
                    gUITween$hoverSlotMap.put(gUITween$lastHoverSlot, tween);
                }
                else {
                    tween.rewind = false;
                }
            }
        }
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    public void renderScreenName(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!GUITweenConfig.isEnableDebugWindow())
            return;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, 0, 1000);

        // 左上角偏移（界面内部）
        int x = this.leftPos + 12;
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
    }

    @Inject(method = "renderBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderBg(Lnet/minecraft/client/gui/GuiGraphics;FII)V"))
    public void renderBgBefore(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!GUITweenConfig.isEnableWindow())
            return;

        if (gUITween$isDisableScreenTween)
            return;

        float moveProgress = gUITween$openTick / GUITweenConfig.window.moveDuration.get().floatValue();
        float gradientProgress = gUITween$openTick / GUITweenConfig.window.gradientDuration.get().floatValue();

        if (moveProgress >= 1 && gradientProgress >= 1)
            return;

        gUiTween$inTween = true;

        float dx = TweenUtil.tween(GUITweenConfig.window.moveX.get().floatValue(), 0, moveProgress, GUITweenConfig.window.moveEase.get());
        float dy = TweenUtil.tween(GUITweenConfig.window.moveY.get().floatValue(), 0, moveProgress, GUITweenConfig.window.moveEase.get());

        PoseStack poseStack = guiGraphics.pose();

        // 动画变换
        poseStack.pushPose();
        poseStack.translate(dx, dy, 0);  // 上移

        float alpha = TweenUtil.tween(0.05f, 1, gradientProgress, GUITweenConfig.window.gradientEase.get());
        GUITweenUtility.pushAlpha(alpha);
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    public void renderAfter(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!gUiTween$inTween)
            return;

        GUITweenUtility.popAlpha();

        PoseStack poseStack = guiGraphics.pose();
        poseStack.popPose();

        if (GUITweenUtility.WINDOW_DELAY_TICK.contains(getClass()))
            return;

        gUiTween$inTween = false;
        gUITween$openTick += GUITweenUtility.getDeltaTicks();
    }

    @Redirect(method = "renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/inventory/Slot;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;isHighlightable()Z"))
    public boolean renderSlotHighlightBefore(Slot instance) {
        if (!GUITweenConfig.isEnableHoverItem())
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

        if (GUITweenConfig.isEnableOutput()) {
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

        if (GUITweenConfig.isEnableHoverItem()) {
            boolean isHoverSlot = gUITween$lastHoverSlot == pSlot;

            if (isHoverSlot) {
                AbstractContainerScreen.renderSlotHighlight(pGuiGraphics, pSlot.x, pSlot.y, 0, getSlotColor(pSlot.index));
            }

            Tween tween = gUITween$hoverSlotMap.getOrDefault(pSlot, null);
            if (tween != null) {
                if (isEmpty) {
                    TweenPool.releaseTween(tween);
                    gUITween$hoverSlotMap.remove(pSlot);
                }
                else {
                    haveTween = true;

                    scale = TweenUtil.tween(tween.startValue, tween.stopValue, tween.tick, tween.totalTick, tween.ease);   // 放大比例

                    int sign = tween.rewind ? -1 : 1;

                    tween.tick += sign * GUITweenUtility.getDeltaTicks();
                    tween.tick = Math.clamp(tween.tick, 0, tween.totalTick);

                    if (tween.rewind && tween.tick <= 0) {
                        TweenPool.releaseTween(tween);
                        gUITween$hoverSlotMap.remove(pSlot);
                    }
                }
            }
        }

        if (haveTween) {
            gUITween$inSlotTween = true;

            poseStack.pushPose();

            // 矩阵操作：平移到中心 → 缩放 → 平移回原位置
            poseStack.translate(centerX, centerY, 0);
            poseStack.scale(scale, scale, 1.0f); // Z轴缩放不影响2D渲染，设为1
            poseStack.translate(-centerX, -centerY, 50);
        }
    }

    @Inject(method = "renderSlot", at = @At(value = "TAIL"))
    public void renderItemAfter(GuiGraphics pGuiGraphics, Slot pSlot, CallbackInfo ci) {
        if (gUITween$inSlotTween) {
            pGuiGraphics.pose().popPose();
            gUITween$inSlotTween = false;
        }

        if (GUITweenConfig.enableDebugWindow.get()) {
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
        if (GUITweenConfig.isEnableClickItem()) {
            if (lastClickSlot == null) {
                gUITween$clickTime = 0;
            }
            else {
                gUITween$clickTime = GUITweenConfig.windowItem.clickItemDuration.get().floatValue();
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
//        float angle = 0;

        if (GUITweenConfig.isEnableHoverItem()) {
            scale = GUITweenConfig.windowItem.clickItemScale.get().floatValue();
            hasChange = true;
        }

        if (gUITween$clickTime > 0) {
            float progress = 1 - gUITween$clickTime / GUITweenConfig.windowItem.clickItemDuration.get().floatValue();
            float strength = GUITweenConfig.windowItem.clickZoomStrength.get().floatValue();
            scale = scale * TweenUtil.punch(strength, 1, progress);
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
//            poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
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
        if (!GUITweenConfig.isEnableTooltip())
            return;

        float duration = GUITweenConfig.windowItem.tooltipDuration.get().floatValue();
        if (gUITween$tooltipShowTick > duration)
            return;

        gUITween$inTooltip = true;

        float progress = gUITween$tooltipShowTick / duration;
        float alpha = TweenUtil.tween(0, 1, progress, GUITweenConfig.windowItem.tooltipEase.get());
        guiGraphics.setColor(1, 1, 1, alpha);

        GUITweenUtility.pushFontAlpha(alpha);

        gUITween$tooltipShowTick += GUITweenUtility.getDeltaTicks();
    }

    @Inject(method = "renderTooltip", at = @At(value = "TAIL"))
    public void renderTooltipAfter(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci) {
        if (!gUITween$inTooltip)
            return;

        gUITween$inTooltip = false;
        GUITweenUtility.popFontAlpha();
        guiGraphics.setColor(1, 1, 1, 1);
    }

    @Inject(method = "onClose", at = @At("TAIL"))
    public void onClose(CallbackInfo ci) {
        gUITween$lastHoverSlot = null;
        gUITween$inSlotTween = false;

        gUITween$hoverSlotMap.forEach((slot, tween) -> {
            TweenPool.releaseTween(tween);
        });
        gUITween$hoverSlotMap.clear();

        gUITween$tooltipShowTick = 0;
    }
}
