package com.remarxk.guitween.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.remarxk.guitween.anim.DragTween;
import com.remarxk.guitween.anim.Tween;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.dataPack.WindowSlotsConfig;
import com.remarxk.guitween.dataPack.WindowSlotsLoader;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.anim.TweenPool;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.util.Ease;
import com.remarxk.guitween.util.Tuple;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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
    private static final ResourceLocation COPY_TEXTURE = ResourceLocation.fromNamespaceAndPath(GUITween.MODID, "textures/gui/sprites/copy.png");

    @Unique
    private static final ResourceLocation COPY_HOVER_TEXTURE = ResourceLocation.fromNamespaceAndPath(GUITween.MODID, "textures/gui/sprites/copy_hover.png");

    @Unique
    private boolean gUITween$inClosingTween;

    @Unique
    private boolean gUITween$needClose;

    @Unique
    private String gUITween$screenName;

    @Unique
    private boolean gUITween$isDisableScreenTween;

    @Unique
    private float gUITween$openTick;

    @Unique
    private boolean gUiTween$inTween;

    @Unique
    private boolean gUITween$inSlotTween;

    @Unique
    private Slot gUITween$lastHoverSlot;

    @Unique
    private HashMap<Slot, Tween> gUITween$hoverSlotMap = new HashMap<>();

    @Unique
    private HashMap<Slot, ItemStack> gUITween$OutputSlotDatas = new HashMap<>();

    @Unique
    private HashMap<Slot, Tween> gUITween$outputSlotTween = new HashMap<>();

    @Unique
    private float gUITween$clickTime = 0;

    @Unique boolean gUITween$isFloatingTween;

    @Unique
    private float gUITween$tooltipShowTick;

    @Unique
    private boolean gUITween$inTooltipTween;

    @Unique
    private Button gUITween$copyNameBtn;

    @Final
    @Shadow
    protected T menu;

    @Nullable
    @Shadow
    private Slot lastClickSlot;

    @Shadow
    private ItemStack draggingItem;

    protected AbstractContainerScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Nullable
    @Shadow
    private Slot findSlot(double mouseX, double mouseY) { return null; }

    @Shadow(remap = false)
    public int getSlotColor(int index) { return 0; }

    @Shadow
    protected int leftPos;

    @Shadow
    protected int topPos;

    @Override
    public String getGUITween$screenName() {
        return gUITween$screenName;
    }

    @Override
    public boolean getGUITween$isDisableScreenTween() {
        return gUITween$isDisableScreenTween;
    }

    @Override
    public void setGUITween$isDisableScreenTween(boolean disable) {
        gUITween$isDisableScreenTween = disable;
    }

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

    @Override
    public Slot getGUITween$lastHoverSlot() {
        return gUITween$lastHoverSlot;
    }

    @Override
    public void setGUITween$lastHoverSlot(Slot slot) {
        gUITween$lastHoverSlot = slot;
    }

    @Override
    public HashMap<Slot, Tween> getGUITween$hoverSlotMap() {
        return gUITween$hoverSlotMap;
    }

    @Override
    public boolean getGUITween$inTooltipTween() {
        return gUITween$inTooltipTween;
    }

    @Override
    public void setGUITween$inTooltipTween(boolean value) {
        gUITween$inTooltipTween = value;
    }

    @Override
    public float getGUITween$tooltipShowTick() {
        return gUITween$tooltipShowTick;
    }

    @Override
    public void setGUITween$tooltipShowTick(float tick) {
        gUITween$tooltipShowTick = tick;
    }

    @Override
    public HashMap<Slot, Tuple<Integer, Integer>> getGUITween$quickTweenSlots() {
        return gUITween$quickTweenSlots;
    }

    @Override
    public HashMap<Slot, Float> getGUITween$quickTicks() {
        return gUITween$quickTicks;
    }

    @Override
    public boolean getGUITween$inSlotTween() {
        return gUITween$inSlotTween;
    }

    @Override
    public void setGUITween$inSlotTween(boolean value) {
        gUITween$inSlotTween = value;
    }

    @Override
    public boolean getGUITween$isRenderQuick() {
        return gUITween$isRenderQuick;
    }

    @Override
    public void setGUITween$isRenderQuick(boolean value) {
        gUITween$isRenderQuick = value;
    }

    @Override
    public ItemStack gUITween$getDraggingItem() {
        return draggingItem;
    }

    @Override
    public ItemStack getGUITween$lastDraggingItem() {
        return gUITween$lastDraggingItem;
    }

    @Override
    public void setGUITween$lastDraggingItem(ItemStack itemStack) {
        gUITween$lastDraggingItem = itemStack;
    }

    @Override
    public float getGUITween$sameItemTick() {
        return gUITween$sameItemTick;
    }

    @Override
    public void setGUITween$sameItemTick(float tick) {
        gUITween$sameItemTick = tick;
    }

    @Override
    public boolean gUITween$playCloseTween() {
        if (!GUITween.CONFIG.isEnableCloseWindow()) {
            return false;
        }

        gUITween$inClosingTween = !gUITween$inClosingTween;
        return gUITween$inClosingTween;
    }

    @Override
    public boolean gUITween$inCloseTween() {
        return gUITween$inClosingTween;
    }

    @Override
    public void gUITween$setNeedClose(boolean close) {
        gUITween$needClose = close;
    }

    @Override
    public boolean gUITween$getNeedClose() {
        return gUITween$needClose;
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        gUITween$OutputSlotDatas.clear();
        gUITween$outputSlotTween.clear();

        gUITween$screenName = getClass().getSimpleName();
        gUITween$isDisableScreenTween = GUITween.CONFIG.isDisableTweenWindow(gUITween$screenName);

        WindowSlotsConfig config = WindowSlotsLoader.configs.getOrDefault(gUITween$screenName, null);

        if (config != null) {
            for (int slotIndex : config.outputSlots) {
                Slot slot = menu.slots.get(slotIndex);
                gUITween$OutputSlotDatas.put(slot, slot.getItem().copy());
            }
        }

        if (gUITween$copyNameBtn == null) {
            gUITween$copyNameBtn = new ImageButton(
                    0, 0,
                    8, 8,
                    0, 0, 0,
                    COPY_TEXTURE,
                    8, 8,
                    button -> {
                        Minecraft.getInstance().keyboardHandler.setClipboard(gUITween$screenName);
                    }
            );
        }

        if (GUITween.CONFIG.isEnableDebugWindow()) {
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
        if (!GUITween.CONFIG.isEnable())
            return;

        Slot hoveredSlot = findSlot(pMouseX, pMouseY);

        if (hoveredSlot != gUITween$lastHoverSlot) {
            if (gUITween$lastHoverSlot != null) {
                Tween tween = gUITween$hoverSlotMap.getOrDefault(gUITween$lastHoverSlot, null);
                if (tween != null)
                    tween.rewind = true;
            }

            if (gUITween$lastHoverSlot == null || !gUITween$lastHoverSlot.hasItem()) {
                if (GUITween.CONFIG.isEnableTooltip())
                    gUITween$tooltipShowTick = 0;
            }

            gUITween$lastHoverSlot = hoveredSlot;

            if (gUITween$lastHoverSlot != null) {
                Tween tween = gUITween$hoverSlotMap.getOrDefault(gUITween$lastHoverSlot, null);
                if (tween == null) {
                    tween = TweenPool.getTween();
                    tween.tick = 0;
                    tween.totalTick = GUITween.CONFIG.hoverDuration;
                    tween.ease = GUITween.CONFIG.hoverEase.get();
                    tween.startValue = 1;
                    tween.stopValue = GUITween.CONFIG.hoverScale;
                    tween.rewind = false;
                    gUITween$hoverSlotMap.put(gUITween$lastHoverSlot, tween);
                }
                else {
                    tween.rewind = false;
                }
            }
        }

        if (GUITween.CONFIG.isEnableSameItem()) {
            ItemStack draggingItem = this.draggingItem.isEmpty() ? this.menu.getCarried() : this.draggingItem;
            if (!ItemStack.isSameItemSameTags(draggingItem, gUITween$lastDraggingItem)) {
                gUITween$lastDraggingItem = draggingItem;
                gUITween$sameItemTick = 0;
            }
            else if (!gUITween$lastDraggingItem.isEmpty()) {
                gUITween$sameItemTick += GUITweenUtility.getDeltaTicks();
                gUITween$sameItemTick %= GUITween.CONFIG.getSameItemTotalDuration();
            }
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;isHighlightable()Z"))
    public boolean renderSlotHighlightBefore(Slot instance) {
        if (!GUITween.CONFIG.isEnableHoverItem())
            return instance.isHighlightable();

        return false;
    }

    @Unique
    private HashMap<Slot, Tuple<Integer, Integer>> gUITween$quickTweenSlots = new HashMap<>();

    @Unique
    private HashMap<Slot, Float> gUITween$quickTicks = new HashMap<>();

    @Unique
    private boolean gUITween$isRenderQuick = false;

    @Unique
    private ItemStack gUITween$lastDraggingItem = ItemStack.EMPTY;

    @Unique
    private float gUITween$sameItemTick = 0;

    @Inject(
            method = "renderSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"
            )
    )
    public void renderItemBefore(GuiGraphics pGuiGraphics, Slot pSlot, CallbackInfo ci) {
        boolean haveTween = false;
        float scale = 1;
//        float angle = 0;
        float dx = 0;
        float dy = 0;

        PoseStack poseStack = pGuiGraphics.pose();
        float itemSize = 16f; // 物品渲染尺寸（固定16x16）
        float centerX = pSlot.x + itemSize / 2; // 物品中心X
        float centerY = pSlot.y + itemSize / 2; // 物品中心Y

        boolean isEmpty = !pSlot.hasItem();

        if (GUITween.CONFIG.isEnableOutput()) {
            if (gUITween$OutputSlotDatas.containsKey(pSlot)) {
                ItemStack curItemStack = pSlot.getItem();
                ItemStack lastItemStack = gUITween$OutputSlotDatas.get(pSlot);
                boolean lastIsEmpty = lastItemStack.isEmpty();

                if (!ItemStack.matches(lastItemStack, pSlot.getItem())) {
                    gUITween$OutputSlotDatas.put(pSlot, curItemStack);

                    boolean isSameItem = ItemStack.isSameItem(curItemStack, lastItemStack);

                    if ((!isEmpty && lastIsEmpty) || !isSameItem) {
                        Tween tween = TweenPool.getTween();
                        tween.name = "create";
                        tween.ease = GUITween.CONFIG.outputEase.get();
                        tween.startValue = 0;
                        tween.stopValue = 1;
                        tween.tick = 0;
                        tween.totalTick = GUITween.CONFIG.outputDuration;
                        gUITween$outputSlotTween.put(pSlot, tween);
                    }
                    else if (curItemStack.getCount() > lastItemStack.getCount()) {
                        Tween tween = TweenPool.getTween();
                        tween.name = "add";
                        tween.tick = 0;
                        tween.totalTick = GUITween.CONFIG.outputDuration;
                        tween.startValue = 0.3f;
                        tween.stopValue = 1;
                        gUITween$outputSlotTween.put(pSlot, tween);
                    }
                }

                if (gUITween$outputSlotTween.containsKey(pSlot)) {
                    Tween tween = gUITween$outputSlotTween.get(pSlot);
                    if (tween.tick >= tween.totalTick) {
                        tween = gUITween$outputSlotTween.remove(pSlot);
                        TweenPool.releaseTween(tween);
                    }
                    else {
                        float progress = tween.tick / tween.totalTick;

                        if (tween.name.equals("create")) {
                            scale = TweenUtil.tween(0, 1, progress, tween.ease);
                        }
                        else if (tween.name.equals("add")) {
                            scale = TweenUtil.punch(tween.startValue, (int) tween.stopValue, progress);
                        }

                        tween.tick += GUITweenUtility.getDeltaTicks();

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
                    tween.tick = Mth.clamp(tween.tick, 0, tween.totalTick);

                    if (tween.rewind && tween.tick <= 0) {
                        TweenPool.releaseTween(tween);
                        gUITween$hoverSlotMap.remove(pSlot);
                    }
                }
            }
        }

        Tuple<Integer, Integer> tuple = gUITween$quickTweenSlots.get(pSlot);
        if (tuple != null) {
            if (tuple.getA().equals(tuple.getB())) {
                float quickTick = gUITween$quickTicks.getOrDefault(pSlot, 0f);

                float progress = quickTick / 4f;

                if (progress < 1) {
                    float clickScale = GUITween.CONFIG.clickItemScale;

                    scale = TweenUtil.tween(clickScale, 1f, progress, Ease.IN_OUT_SINE);

                    haveTween = true;

                    gUITween$quickTicks.put(pSlot, quickTick + GUITweenUtility.getDeltaTicks());
                }
                else {
                    gUITween$quickTweenSlots.remove(pSlot);
                    gUITween$quickTicks.remove(pSlot);
                }
            }
            else {
                tuple.setA(tuple.getB());
            }
        }

        if (!gUITween$lastDraggingItem.isEmpty() && ItemStack.isSameItemSameTags(gUITween$lastDraggingItem, pSlot.getItem())) {
            float delay = GUITween.CONFIG.sameItemDelay;
            float duration = GUITween.CONFIG.sameItemShakeDuration;

            if (gUITween$sameItemTick > delay && gUITween$sameItemTick < GUITween.CONFIG.sameItemDelay + duration) {
                haveTween = true;

                float strength = GUITween.CONFIG.sameItemShakeStrength;
                float frequency = GUITween.CONFIG.sameItemShakeFrequency;

                int hashCode = pSlot.hashCode();
                dx = TweenUtil.shake(0, gUITween$sameItemTick - delay, duration, strength, frequency, TweenUtil.DEFAULT_SEED + hashCode * 100L);
                dy = TweenUtil.shake(1, gUITween$sameItemTick - delay, duration, strength, frequency, TweenUtil.DEFAULT_SEED + hashCode * 100L);
//                angle = (TweenUtil.punch(0.15f, 2, gUITween$sameItemTick / 8) - 1) * 100;
            }
        }

        if (haveTween) {
            gUITween$inSlotTween = true;

            poseStack.pushPose();

            // 矩阵操作：平移到中心 → 缩放 → 平移回原位置
            poseStack.translate(centerX, centerY, 0);
            poseStack.scale(scale, scale, 1.0f); // Z轴缩放不影响2D渲染，设为1
//            poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
            poseStack.translate(-centerX, -centerY, 0);

            poseStack.translate(dx, dy, 0);
        }
    }

    @Inject(
            method = "renderSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"
            )
    )
    public void renderQuickItem(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        if (!GUITween.CONFIG.isEnableQuickCraft())
            return;

        gUITween$isRenderQuick = true;

        PoseStack poseStack = guiGraphics.pose();

        float centerX = slot.x + 8;
        float centerY = slot.y + 8;
        float scale = GUITween.CONFIG.clickItemScale;

        poseStack.pushPose();

        poseStack.translate(centerX, centerY , 0);
        poseStack.scale(scale, scale, 1);
        poseStack.translate(-centerX, -centerY, 0);

        Tuple<Integer, Integer> tuple = gUITween$quickTweenSlots.get(slot);
        if (tuple == null) {
            gUITween$quickTweenSlots.put(slot, new Tuple<>(-1, 0));
        }
        else {
            tuple.setB(tuple.getB() + 1);
        }
    }

    @Inject(
            method = "renderSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V",
                    shift = At.Shift.AFTER
            )
    )
    public void renderItemAfter(GuiGraphics pGuiGraphics, Slot pSlot, CallbackInfo ci) {
        if (gUITween$isRenderQuick) {
            gUITween$isRenderQuick = false;
            pGuiGraphics.pose().popPose();
        }

        if (gUITween$inSlotTween) {
            gUITween$inSlotTween = false;
            pGuiGraphics.pose().popPose();
        }

        if (GUITween.CONFIG.enableDebugWindow) {
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

                DragTween dragTween = GUITweenUtility.getDragTween();
                dragTween.stop();
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
            scale = GUITween.CONFIG.clickItemScale;
            hasChange = true;
        }

        if (gUITween$clickTime > 0) {
            float progress = 1 - gUITween$clickTime / GUITween.CONFIG.clickItemDuration;
            float strength = GUITween.CONFIG.clickZoomStrength;
            scale = scale * TweenUtil.punch(strength, 1, progress);

            gUITween$clickTime -= GUITweenUtility.getDeltaTicks();

            hasChange = true;
        }

        if (GUITween.CONFIG.isEnableDragItem()) {
            DragTween dragTween = GUITweenUtility.getDragTween();
            dragTween.setPos(x, y);

            if (dragTween.isRunning()) {
                dragTween.update();
                angle = dragTween.getAngle();

                hasChange = true;
            }
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
        PoseStack poseStack = guiGraphics.pose();

        if (!gUITween$isFloatingTween)
            return;

        gUITween$isFloatingTween = false;

        poseStack.popPose();
    }

    @Inject(method = "renderTooltip", at = @At(value = "HEAD"))
    public void renderTooltipBefore(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci) {
        if (!GUITween.CONFIG.isEnableTooltip())
            return;

        float duration = GUITween.CONFIG.tooltipDuration;
        if (gUITween$tooltipShowTick > duration)
            return;

        gUITween$inTooltipTween = true;

        float progress = gUITween$tooltipShowTick / duration;
        float alpha = TweenUtil.tween(0, 1, progress, GUITween.CONFIG.tooltipEase.get());
        guiGraphics.setColor(1, 1, 1, alpha);

        GUITweenUtility.pushFontAlpha(alpha);

        gUITween$tooltipShowTick += GUITweenUtility.getDeltaTicks();
    }

    @Inject(method = "renderTooltip", at = @At(value = "TAIL"))
    public void renderTooltipAfter(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci) {
        if (!gUITween$inTooltipTween)
            return;

        gUITween$inTooltipTween = false;
        GUITweenUtility.popFontAlpha();
        guiGraphics.setColor(1, 1, 1, 1);
    }

    @WrapOperation(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;onClose()V"
            )
    )
    private void onCloseBefore(AbstractContainerScreen screen, Operation<Void> original) {
        if (!gUITween$playCloseTween()) {
            original.call(screen);
        }
    }

    @Inject(method = "onClose", at = @At("TAIL"))
    public void onClose(CallbackInfo ci) {
        gUITween$openTick = 0;

        gUITween$lastHoverSlot = null;
        gUITween$inSlotTween = false;

        gUITween$hoverSlotMap.forEach((slot, tween) -> {
            TweenPool.releaseTween(tween);
        });
        gUITween$hoverSlotMap.clear();

        gUITween$tooltipShowTick = 0;

        GUITweenUtility.deleteOpenScreen();
    }
}
