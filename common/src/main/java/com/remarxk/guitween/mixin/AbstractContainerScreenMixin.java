package com.remarxk.guitween.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.remarxk.guitween.Constants;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.anim.DragTween;
import com.remarxk.guitween.anim.Tween;
import com.remarxk.guitween.anim.TweenPool;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.dataPack.WindowSlotsConfig;
import com.remarxk.guitween.dataPack.WindowSlotsLoader;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.util.Ease;
import com.remarxk.guitween.util.Tuple;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin <T extends AbstractContainerMenu> extends Screen implements MenuAccess<T>, AbstractContainerScreenMixinAccess {
    @Unique
    private static final Identifier COPY_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MODID, "copy");

    @Unique
    private static final Identifier COPY_HOVER_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MODID, "copy_hover");

    @Unique
    private boolean gUITween$inClosingTween;

    @Unique
    private boolean gUITween$needClose;

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
    private HashMap<Integer, ItemStack> gUITween$OutputSlotDatas = new HashMap<>();

    @Unique
    private HashMap<Integer, Tween> gUITween$outputSlotTween = new HashMap<>();

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
    private static Identifier SLOT_HIGHLIGHT_BACK_SPRITE;

    @Shadow
    protected int leftPos;

    @Shadow
    protected int topPos;

    @Final
    @Shadow
    protected T menu;

    @Shadow
    private Slot lastClickSlot;

    @Shadow
    private ItemStack draggingItem;

    protected AbstractContainerScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Shadow
    protected abstract Slot getHoveredSlot(double x, double y);

    @Override
    public int gUITween$getGuiLeft() {
        return leftPos;
    }

    @Override
    public int gUITween$getGuiTop() {
        return topPos;
    }

    @Override
    public String getGUITween$screenName() {
        return gUITween$screenName;
    }

    @Override
    public void setGUITween$isDisableScreenTween(boolean isDisableScreenTween) {
        gUITween$isDisableScreenTween = isDisableScreenTween;
    }

    @Override
    public boolean getGUITween$isDisableScreenTween() {
        return gUITween$isDisableScreenTween;
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
        return gUITween$inTooltip;
    }

    @Override
    public void setGUITween$inTooltipTween(boolean value) {
        gUITween$inTooltip = value;
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
    public HashMap<Integer, Tuple<Integer, Integer>> getGUITween$quickTweenSlots() {
        return gUITween$quickTweenSlots;
    }

    @Override
    public HashMap<Integer, Float> getGUITween$quickTicks() {
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
    public void setGUITween$clickTime(float time) {
        gUITween$clickTime = time;
    }

    @Override
    public float getGUITween$clickTime() {
        return gUITween$clickTime;
    }

    @Override
    public boolean gUITween$playCloseTween() {
        if (!GUITweenConfig.isEnableCloseWindow()) {
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
        gUITween$inClosingTween = false;
        gUITween$needClose = false;

        gUITween$openTick = 0;

        gUITween$screenName = getClass().getSimpleName();
        gUITween$isDisableScreenTween = GUITweenConfig.isDisableTweenWindow(gUITween$screenName) || GUITweenUtility.isCompatWindow(getClass());
        gUITween$OutputSlotDatas.clear();
        gUITween$outputSlotTween.clear();

        WindowSlotsConfig config = WindowSlotsLoader.configs.getOrDefault(gUITween$screenName, null);
        if (config != null) {
            for (int slotIndex : config.outputSlots()) {
                Slot slot = menu.slots.get(slotIndex);
                gUITween$OutputSlotDatas.put(slotIndex, slot.getItem().copy());
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
            int x = this.gUITween$getGuiLeft() + 2;
            int y = this.gUITween$getGuiTop() - 10;
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

    @Inject(method = "extractContents", at = @At("HEAD"))
    public void renderContentsBefore(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!GUITweenConfig.enable())
            return;

        Slot hoverSlot = getHoveredSlot(mouseX, mouseY);
        if (gUITween$lastHoverSlot != hoverSlot) {
            if (gUITween$lastHoverSlot != null) {
                Tween tween = gUITween$hoverSlotMap.getOrDefault(gUITween$lastHoverSlot, null);
                if (tween != null)
                    tween.rewind = true;
            }

            if (gUITween$lastHoverSlot == null || !gUITween$lastHoverSlot.hasItem()) {
                if (GUITweenConfig.isEnableTooltip())
                    gUITween$tooltipShowTick = 0;
            }

            gUITween$lastHoverSlot = hoverSlot;

            if (gUITween$lastHoverSlot != null) {
                Tween tween = gUITween$hoverSlotMap.getOrDefault(gUITween$lastHoverSlot, null);
                if (tween == null) {
                    tween = TweenPool.getTween();
                    tween.tick = 0;
                    tween.totalTick = GUITweenConfig.hoverDuration();
                    tween.ease = GUITweenConfig.hoverEase();
                    tween.startValue = 1;
                    tween.stopValue = GUITweenConfig.hoverScale();
                    tween.rewind = false;
                    gUITween$hoverSlotMap.put(gUITween$lastHoverSlot, tween);
                }
                else {
                    tween.rewind = false;
                }
            }
        }

        if (GUITweenConfig.isEnableSameItem()) {
            ItemStack draggingItem = this.draggingItem.isEmpty() ? this.menu.getCarried() : this.draggingItem;
            if (!ItemStack.isSameItemSameComponents(draggingItem, gUITween$lastDraggingItem)) {
                gUITween$lastDraggingItem = draggingItem;
                gUITween$sameItemTick = 0;
            }
            else if (!gUITween$lastDraggingItem.isEmpty()) {
                gUITween$sameItemTick += GUITweenUtility.getDeltaTicks();
                gUITween$sameItemTick %= GUITweenConfig.getSameItemTotalDuration();
            }
        }
    }

    @Redirect(method = "extractSlotHighlightFront", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;isHighlightable()Z"))
    public boolean renderSlotHighlightFrontBefore(Slot instance) {
        if (!GUITweenConfig.isEnableHoverItem())
            return instance.isHighlightable();

        return false;
    }

    @Unique
    private HashMap<Integer, Tuple<Integer, Integer>> gUITween$quickTweenSlots = new HashMap<>();

    @Unique
    private HashMap<Integer, Float> gUITween$quickTicks = new HashMap<>();

    @Unique
    private boolean gUITween$isRenderQuick = false;

    @Unique
    private ItemStack gUITween$lastDraggingItem = ItemStack.EMPTY;

    @Unique
    private float gUITween$sameItemTick = 0;

    @Inject(
            method = "extractSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z",
                    ordinal = 5,
                    shift = At.Shift.BEFORE
            )
    )
    public void renderItemBefore(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        boolean haveTween = false;
        float scale = 1;
        //        float angle = 0;
        float dx = 0;
        float dy = 0;

        Matrix3x2fStack poseStack = graphics.pose();
        float itemSize = 16f; // 物品渲染尺寸（固定16x16）
        float centerX = slot.x + itemSize / 2; // 物品中心X
        float centerY = slot.y + itemSize / 2; // 物品中心Y

        boolean isEmpty = !slot.hasItem();

        if (GUITweenConfig.isEnableOutput()) {
            if (menu.getSlot(slot.index) == slot && gUITween$OutputSlotDatas.containsKey(slot.index)) {
                ItemStack curItemStack = slot.getItem();
                ItemStack lastItemStack = gUITween$OutputSlotDatas.get(slot.index);
                boolean lastIsEmpty = lastItemStack.isEmpty();

                if (!ItemStack.matches(lastItemStack, slot.getItem())) {
                    gUITween$OutputSlotDatas.put(slot.index, curItemStack);

                    boolean isSameItem = ItemStack.isSameItem(curItemStack, lastItemStack);

                    if ((!isEmpty && lastIsEmpty) || !isSameItem) {
                        Tween tween = TweenPool.getTween();
                        tween.name = "create";
                        tween.ease = GUITweenConfig.outputEase();
                        tween.startValue = 0;
                        tween.stopValue = 1;
                        tween.tick = 0;
                        tween.totalTick = GUITweenConfig.outputDuration();
                        gUITween$outputSlotTween.put(slot.index, tween);
                    }
                    else if (curItemStack.getCount() > lastItemStack.getCount()) {
                        Tween tween = TweenPool.getTween();
                        tween.name = "add";
                        tween.tick = 0;
                        tween.totalTick = GUITweenConfig.outputDuration();
                        tween.startValue = 0.3f;
                        tween.stopValue = 1;
                        gUITween$outputSlotTween.put(slot.index, tween);
                    }
                }

                if (gUITween$outputSlotTween.containsKey(slot.index)) {
                    Tween tween = gUITween$outputSlotTween.get(slot.index);
                    if (tween.tick >= tween.totalTick) {
                        tween = gUITween$outputSlotTween.remove(slot.index);
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

        if (GUITweenConfig.isEnableHoverItem()) {
            Tween tween = gUITween$hoverSlotMap.getOrDefault(slot, null);
            if (tween != null) {
                if (isEmpty) {
                    TweenPool.releaseTween(tween);
                    gUITween$hoverSlotMap.remove(slot);
                }
                else {
                    haveTween = true;

                    scale = TweenUtil.tween(tween.startValue, tween.stopValue, tween.tick, tween.totalTick, tween.ease);   // 放大比例

                    int sign = tween.rewind ? -1 : 1;

                    tween.tick += sign * GUITweenUtility.getDeltaTicks();
                    tween.tick = Math.clamp(tween.tick, 0, tween.totalTick);

                    if (tween.rewind && tween.tick <= 0) {
                        TweenPool.releaseTween(tween);
                        gUITween$hoverSlotMap.remove(slot);
                    }
                }
            }
        }

        Tuple<Integer, Integer> tuple = gUITween$quickTweenSlots.get(slot.index);
        if (tuple != null) {
            if (tuple.getA().equals(tuple.getB())) {
                float quickTick = gUITween$quickTicks.getOrDefault(slot.index, 0f);

                float progress = quickTick / 4f;

                if (progress < 1) {
                    float clickScale = GUITweenConfig.clickItemScale();

                    scale = TweenUtil.tween(clickScale, 1f, progress, Ease.IN_OUT_SINE);

                    haveTween = true;

                    gUITween$quickTicks.put(slot.index, quickTick + GUITweenUtility.getDeltaTicks());
                }
                else {
                    gUITween$quickTweenSlots.remove(slot.index);
                    gUITween$quickTicks.remove(slot.index);
                }
            }
            else {
                tuple.setA(tuple.getB());
            }
        }

        if (!gUITween$lastDraggingItem.isEmpty() && ItemStack.isSameItemSameComponents(gUITween$lastDraggingItem, slot.getItem())) {
            float delay = GUITweenConfig.sameItemDelay();
            float duration = GUITweenConfig.sameItemShakeDuration();

            if (gUITween$sameItemTick > delay && gUITween$sameItemTick < GUITweenConfig.sameItemDelay() + duration) {
                haveTween = true;

                float strength = GUITweenConfig.sameItemShakeStrength();
                float frequency = GUITweenConfig.sameItemShakeFrequency();

                dx = TweenUtil.shake(0, gUITween$sameItemTick - delay, duration, strength, frequency, TweenUtil.DEFAULT_SEED + slot.index * 100L);
                dy = TweenUtil.shake(1, gUITween$sameItemTick - delay, duration, strength, frequency, TweenUtil.DEFAULT_SEED + slot.index * 100L);
//                angle = (TweenUtil.punch(0.15f, 2, gUITween$sameItemTick / 8) - 1) * 100;
            }
        }

        if (haveTween) {
            gUITween$inSlotTween = true;

            poseStack.pushMatrix();

            // 矩阵操作：平移到中心 → 缩放 → 平移回原位置
            poseStack.translate(centerX, centerY);
            poseStack.scale(scale, scale); // Z轴缩放不影响2D渲染，设为1
            //            poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
            poseStack.translate(-centerX, -centerY);

            poseStack.translate(dx, dy);
        }
    }

    @Inject(
            method = "extractSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V",
                    shift = At.Shift.AFTER
            )
    )
    public void renderQuickItem(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        if (!GUITweenConfig.isEnableQuickCraft())
            return;

        gUITween$isRenderQuick = true;

        Matrix3x2fStack poseStack = graphics.pose();

        float centerX = slot.x + 8;
        float centerY = slot.y + 8;
        float scale = GUITweenConfig.clickItemScale();

        poseStack.pushMatrix();

        poseStack.translate(centerX, centerY);
        poseStack.scale(scale, scale);
        poseStack.translate(-centerX, -centerY);

        Tuple<Integer, Integer> tuple = gUITween$quickTweenSlots.get(slot.index);
        if (tuple == null) {
            gUITween$quickTweenSlots.put(slot.index, new Tuple<>(-1, 0));
        }
        else {
            tuple.setB(tuple.getB() + 1);
        }
    }

    @Inject(method = "extractSlot", at = @At(value = "RETURN"))
    public void renderItemAfter(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        if (gUITween$isRenderQuick) {
            gUITween$isRenderQuick = false;
            graphics.pose().popMatrix();
        }

        if (gUITween$inSlotTween) {
            gUITween$inSlotTween = false;
            graphics.pose().popMatrix();
        }

        if (GUITweenConfig.enableDebugWindow()) {
            Font font = Minecraft.getInstance().font;

            // 获取格子左上角坐标
            int x = slot.x; // Slot 的 x 坐标
            int y = slot.y; // Slot 的 y 坐标

            String text = String.valueOf(slot.index);
            int color = 0xFFFF0000; // 白色文字
            boolean shadow = true; // 阴影，让文字在物品上更清晰

            Matrix3x2fStack poseStack = graphics.pose();
            poseStack.pushMatrix();
            poseStack.translate(0, 0);
            graphics.text(font, text, x + 1, y + 1, color, shadow);
            poseStack.popMatrix();
        }
    }

    @Inject(
            method = "mouseClicked",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;lastClickSlot:Lnet/minecraft/world/inventory/Slot;",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    public void restClickTime(MouseButtonEvent event, boolean value, CallbackInfoReturnable<Boolean> cir) {
        if (GUITweenConfig.isEnableClickItem()) {
            if (lastClickSlot == null) {
                gUITween$clickTime = 0;
            }
            else {
                gUITween$clickTime = GUITweenConfig.clickItemDuration();

                DragTween dragTween = GUITweenUtility.getDragTween();
                dragTween.stop();
            }
        }
    }

    @Inject(method = "extractFloatingItem", at = @At(value = "HEAD"))
    public void renderFloatingItemBefore(GuiGraphicsExtractor graphics, ItemStack carried, int x, int y, String itemCount, CallbackInfo ci) {
        boolean hasChange = false;

        float itemSize = 16f; // 物品渲染尺寸（固定16x16）
        float centerX = x + itemSize / 2; // 物品中心X
        float centerY = y + itemSize / 2; // 物品中心Y

        float scale = 1;   // 放大比例
        float angle = 0;

        if (GUITweenConfig.isEnableHoverItem()) {
            scale = GUITweenConfig.clickItemScale();
            hasChange = true;
        }

        if (gUITween$clickTime > 0) {
            float progress = 1 - gUITween$clickTime / GUITweenConfig.clickItemDuration();
            float strength = GUITweenConfig.clickZoomStrength();
            scale = scale * TweenUtil.punch(strength, 1, progress);

            gUITween$clickTime -= GUITweenUtility.getDeltaTicks();

            hasChange = true;
        }

        if (GUITweenConfig.isEnableDragItem()) {
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

            Matrix3x2fStack poseStack = graphics.pose();
            poseStack.pushMatrix();

            // 矩阵操作：平移到中心 → 缩放 → 平移回原位置
            poseStack.translate(centerX, centerY);
            poseStack.scale(scale, scale); // Z轴缩放不影响2D渲染，设为1
            poseStack.rotate((float) Math.toRadians(angle));
            poseStack.translate(-centerX, -centerY);
        }
    }

    @Inject(method = "extractFloatingItem", at = @At(value = "TAIL"))
    public void renderFloatingItemAfter(GuiGraphicsExtractor graphics, ItemStack carried, int x, int y, String itemCount, CallbackInfo ci) {
        Matrix3x2fStack poseStack = graphics.pose();

        if (!gUITween$isFloatingTween)
            return;

        gUITween$isFloatingTween = false;

        poseStack.popMatrix();
    }

    @Inject(method = "extractTooltip", at = @At(value = "HEAD"))
    public void renderTooltipBefore(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (!GUITweenConfig.isEnableTooltip())
            return;

        float duration = GUITweenConfig.tooltipDuration();
        if (gUITween$tooltipShowTick > duration)
            return;

        gUITween$inTooltip = true;

        GUITweenUtility.startTooltipTween(gUITween$tooltipShowTick);
    }

    @Inject(method = "extractTooltip", at = @At(value = "RETURN"))
    public void renderTooltipAfter(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (!gUITween$inTooltip)
            return;

        gUITween$inTooltip = false;

        gUITween$tooltipShowTick += GUITweenUtility.getDeltaTicks();

        GUITweenUtility.endTooltipTween();
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