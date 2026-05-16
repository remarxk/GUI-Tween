package com.remarxk.guitween.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.anim.DragTween;
import com.remarxk.guitween.client.anim.Tween;
import com.remarxk.guitween.client.anim.TweenPool;
import com.remarxk.guitween.client.compat.CompatUtility;
import com.remarxk.guitween.client.dataPack.RemapClassLoader;
import com.remarxk.guitween.client.dataPack.WindowSlotsConfig;
import com.remarxk.guitween.client.dataPack.WindowSlotsLoader;
import com.remarxk.guitween.client.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.client.util.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
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

@Mixin(HandledScreen.class)
public abstract class AbstractContainerScreenMixin <T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T>, AbstractContainerScreenMixinAccess {
    @Unique
    private static final Identifier COPY_TEXTURE = Identifier.tryParse(GUITween.MODID, "copy");

    @Unique
    private static final Identifier COPY_HOVER_TEXTURE = Identifier.tryParse(GUITween.MODID, "copy_hover");

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
    private HashMap<Slot, ItemStack> gUITween$OutputSlotDatas = new HashMap<>();

    @Unique
    private HashMap<Slot, Tween> gUITween$outputSlotTween = new HashMap<>();

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
    private ButtonWidget gUITween$copyNameBtn;

    @Final
    @Shadow
    protected T handler;

    @Nullable
    @Shadow
    private Slot lastClickedSlot;

    @Shadow
    private ItemStack touchDragStack;

    protected AbstractContainerScreenMixin(Text pTitle) {
        super(pTitle);
    }

    @Nullable
    @Shadow
    private Slot getSlotAt(double mouseX, double mouseY) { return null; }

    @Shadow
    protected int x;

    @Shadow
    protected int y;

    @Override
    public int gUITween$getX() {
        return x;
    }

    @Override
    public int gUITween$getY() {
        return y;
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
        return touchDragStack;
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
        if (!GUITweenClient.CONFIG.isEnableCloseWindow()) {
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
        gUITween$openTick = 0;

        gUITween$screenName = RemapClassLoader.getSimpleClassName(getClass().getName());
        gUITween$isDisableScreenTween = GUITweenClient.CONFIG.isDisableTweenWindow(gUITween$screenName);
        gUITween$OutputSlotDatas.clear();
        gUITween$outputSlotTween.clear();

        WindowSlotsConfig config = WindowSlotsLoader.configs.getOrDefault(gUITween$screenName, null);
        if (config != null) {
            for (int slotIndex : config.outputSlots) {
                Slot slot = handler.slots.get(slotIndex);
                gUITween$OutputSlotDatas.put(slot, slot.getStack().copy());
            }
        }

        if (gUITween$copyNameBtn == null) {
            gUITween$copyNameBtn = new TexturedButtonWidget(
                    0, 0,
                    8, 8,
                    new ButtonTextures(COPY_TEXTURE, COPY_TEXTURE, COPY_HOVER_TEXTURE),
                    button -> {
                        MinecraftClient.getInstance().keyboard.setClipboard(gUITween$screenName);
                    }
            );
        }

        if (GUITweenClient.CONFIG.isEnableDebugWindow()) {
            int x = this.x + 2;
            int y = this.y - 10;
            if ((((HandledScreen<?>) (Object) this) instanceof CreativeInventoryScreen)) {
                y -= 30;
            }
            gUITween$copyNameBtn.setPosition(x, y);

            addDrawableChild(gUITween$copyNameBtn);
        }
        else {
            remove(gUITween$copyNameBtn);
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void renderBefore(DrawContext pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {
        if (!GUITweenClient.CONFIG.isEnable())
            return;

        Slot hoveredSlot = getSlotAt(pMouseX, pMouseY);

        if (hoveredSlot != gUITween$lastHoverSlot) {
            if (gUITween$lastHoverSlot != null) {
                Tween tween = gUITween$hoverSlotMap.getOrDefault(gUITween$lastHoverSlot, null);
                if (tween != null)
                    tween.rewind = true;
            }

            if (gUITween$lastHoverSlot == null || !gUITween$lastHoverSlot.hasStack()) {
                if (GUITweenClient.CONFIG.isEnableTooltip())
                    gUITween$tooltipShowTick = 0;
            }

            gUITween$lastHoverSlot = hoveredSlot;

            if (gUITween$lastHoverSlot != null) {
                Tween tween = gUITween$hoverSlotMap.getOrDefault(gUITween$lastHoverSlot, null);
                if (tween == null) {
                    tween = TweenPool.getTween();
                    tween.tick = 0;
                    tween.totalTick = GUITweenClient.CONFIG.hoverDuration;
                    tween.ease = GUITweenClient.CONFIG.hoverEase.get();
                    tween.startValue = 1;
                    tween.stopValue = GUITweenClient.CONFIG.hoverScale;
                    tween.rewind = false;
                    gUITween$hoverSlotMap.put(gUITween$lastHoverSlot, tween);
                }
                else {
                    tween.rewind = false;
                }
            }
        }

        if (GUITweenClient.CONFIG.isEnableSameItem()) {
            ItemStack draggingItem = this.touchDragStack.isEmpty() ? this.handler.getCursorStack() : this.touchDragStack;
            if (!ItemStack.areItemsAndComponentsEqual(draggingItem, gUITween$lastDraggingItem)) {
                gUITween$lastDraggingItem = draggingItem;
                gUITween$sameItemTick = 0;
            }
            else if (!gUITween$lastDraggingItem.isEmpty()) {
                gUITween$sameItemTick += GUITweenUtility.getDeltaTicks();
                gUITween$sameItemTick %= GUITweenClient.CONFIG.getSameItemTotalDuration();
            }
        }
    }

    @Inject(
            method = "renderBackground",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawBackground(Lnet/minecraft/client/gui/DrawContext;FII)V"
            )
    )
    public void renderBgBefore(DrawContext guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (GUITweenUtility.isCompatWindow(getClass()))
            return;

        if (gUiTween$inTween) { // 某些界面重写了render方法，导致没有取消渲染动画，需要强行终止
            gUiTween$inTween = false;
            gUITween$isDisableScreenTween = true;

            GUITweenUtility.popAlpha();

            CompatUtility.endOpenTween();
        }

        GUITweenUtility.setOpenScreen(gUITween$screenName, gUITween$openTick);

        if (!GUITweenClient.CONFIG.isEnableWindow())
            return;

        if (gUITween$isDisableScreenTween)
            return;

        float moveProgress = gUITween$openTick / GUITweenClient.CONFIG.windowMoveDuration;
        float gradientProgress = gUITween$openTick / GUITweenClient.CONFIG.windowGradientDuration;

        if (moveProgress >= 1 && gradientProgress >= 1)
            return;

        gUiTween$inTween = true;

        float dx = TweenUtil.tween(GUITweenClient.CONFIG.windowMoveX, 0, moveProgress, GUITweenClient.CONFIG.windowMoveEase.get());
        float dy = TweenUtil.tween(GUITweenClient.CONFIG.windowMoveY, 0, moveProgress, GUITweenClient.CONFIG.windowMoveEase.get());
        float alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, gradientProgress, GUITweenClient.CONFIG.windowGradientEase.get());

        CompatUtility.startOpenTween(dx, dy, alpha);

        MatrixStack poseStack = guiGraphics.getMatrices();

        // 动画变换
        poseStack.push();
        poseStack.translate(dx, dy, 0);  // 上移

        GUITweenUtility.pushAlpha(alpha);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;canBeHighlighted()Z"))
    public boolean renderSlotHighlightBefore(Slot instance) {
        if (!GUITweenClient.CONFIG.isEnableHoverItem())
            return instance.canBeHighlighted();

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
            method = "drawSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;push()V")
    )
    public void renderItemBefore(DrawContext pGuiGraphics, Slot pSlot, CallbackInfo ci) {
        boolean haveTween = false;
        float scale = 1;
        //        float angle = 0;
        float dx = 0;
        float dy = 0;

        MatrixStack poseStack = pGuiGraphics.getMatrices();
        float itemSize = 16f; // 物品渲染尺寸（固定16x16）
        float centerX = pSlot.x + itemSize / 2; // 物品中心X
        float centerY = pSlot.y + itemSize / 2; // 物品中心Y

        boolean isEmpty = !pSlot.hasStack();

        if (GUITweenClient.CONFIG.isEnableOutput()) {
            if (gUITween$OutputSlotDatas.containsKey(pSlot)) {
                ItemStack curItemStack = pSlot.getStack();
                ItemStack lastItemStack = gUITween$OutputSlotDatas.get(pSlot);
                boolean lastIsEmpty = lastItemStack.isEmpty();

                if (!ItemStack.areItemsEqual(lastItemStack, pSlot.getStack())) {
                    gUITween$OutputSlotDatas.put(pSlot, curItemStack);

                    boolean isSameItem = ItemStack.areItemsEqual(curItemStack, lastItemStack);

                    if ((!isEmpty && lastIsEmpty) || !isSameItem) {
                        Tween tween = TweenPool.getTween();
                        tween.name = "create";
                        tween.ease = GUITweenClient.CONFIG.outputEase.get();
                        tween.startValue = 0;
                        tween.stopValue = 1;
                        tween.tick = 0;
                        tween.totalTick = GUITweenClient.CONFIG.outputDuration;
                        gUITween$outputSlotTween.put(pSlot, tween);
                    }
                    else if (curItemStack.getCount() > lastItemStack.getCount()) {
                        Tween tween = TweenPool.getTween();
                        tween.name = "add";
                        tween.tick = 0;
                        tween.totalTick = GUITweenClient.CONFIG.outputDuration;
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

        if (GUITweenClient.CONFIG.isEnableHoverItem()) {
            boolean isHoverSlot = gUITween$lastHoverSlot == pSlot;

            if (isHoverSlot) {
                HandledScreen.drawSlotHighlight(pGuiGraphics, pSlot.x, pSlot.y, 0);
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

        Tuple<Integer, Integer> tuple = gUITween$quickTweenSlots.get(pSlot);
        if (tuple != null) {
            if (tuple.getA().equals(tuple.getB())) {
                float quickTick = gUITween$quickTicks.getOrDefault(pSlot, 0f);

                float progress = quickTick / 4f;

                if (progress < 1) {
                    float clickScale = GUITweenClient.CONFIG.clickItemScale;

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

        if (!gUITween$lastDraggingItem.isEmpty() && ItemStack.areItemsAndComponentsEqual(gUITween$lastDraggingItem, pSlot.getStack())) {
            float delay = GUITweenClient.CONFIG.sameItemDelay;
            float duration = GUITweenClient.CONFIG.sameItemShakeDuration;

            if (gUITween$sameItemTick > delay && gUITween$sameItemTick < GUITweenClient.CONFIG.sameItemDelay + duration) {
                haveTween = true;

                float strength = GUITweenClient.CONFIG.sameItemShakeStrength;
                float frequency = GUITweenClient.CONFIG.sameItemShakeFrequency;

                int hashCode = pSlot.hashCode();
                dx = TweenUtil.shake(0, gUITween$sameItemTick - delay, duration, strength, frequency, TweenUtil.DEFAULT_SEED + hashCode * 100L);
                dy = TweenUtil.shake(1, gUITween$sameItemTick - delay, duration, strength, frequency, TweenUtil.DEFAULT_SEED + hashCode * 100L);
//                angle = (TweenUtil.punch(0.15f, 2, gUITween$sameItemTick / 8) - 1) * 100;
            }
        }

        if (haveTween) {
            gUITween$inSlotTween = true;

            poseStack.push();

            // 矩阵操作：平移到中心 → 缩放 → 平移回原位置
            poseStack.translate(centerX, centerY, 0);
            poseStack.scale(scale, scale, 1.0f); // Z轴缩放不影响2D渲染，设为1
            //            poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
            poseStack.translate(-centerX, -centerY, 0);

            poseStack.translate(dx, dy, 0);
        }
    }

    @Inject(
            method = "drawSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V",
                    shift = At.Shift.AFTER
            )
    )
    public void renderQuickItem(DrawContext guiGraphics, Slot slot, CallbackInfo ci) {
        if (!GUITweenClient.CONFIG.isEnableQuickCraft())
            return;

        gUITween$isRenderQuick = true;

        MatrixStack poseStack = guiGraphics.getMatrices();

        float centerX = slot.x + 8;
        float centerY = slot.y + 8;
        float scale = GUITweenClient.CONFIG.clickItemScale;

        poseStack.push();

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
            method = "drawSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V",
                    shift = At.Shift.AFTER)
    )
    public void renderItemAfter(DrawContext pGuiGraphics, Slot pSlot, CallbackInfo ci) {
        if (gUITween$isRenderQuick) {
            gUITween$isRenderQuick = false;
            pGuiGraphics.getMatrices().pop();
        }

        if (gUITween$inSlotTween) {
            gUITween$inSlotTween = false;
            pGuiGraphics.getMatrices().pop();
        }

        if (GUITweenClient.CONFIG.enableDebugWindow) {
            TextRenderer font = MinecraftClient.getInstance().textRenderer;

            // 获取格子左上角坐标
            int x = pSlot.x; // Slot 的 x 坐标
            int y = pSlot.y; // Slot 的 y 坐标

            String text = String.valueOf(pSlot.id);
            int color = 0xFF0000; // 白色文字
            boolean shadow = true; // 阴影，让文字在物品上更清晰

            MatrixStack poseStack = pGuiGraphics.getMatrices();
            poseStack.push();
            poseStack.translate(0, 0, 1000);
            pGuiGraphics.drawText(font, text, x + 1, y + 1, color, shadow);
            poseStack.pop();
        }
    }

    @Inject(
            method = "mouseClicked",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;lastClickedSlot:Lnet/minecraft/screen/slot/Slot;",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    public void restClickTime(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (GUITweenClient.CONFIG.isEnableClickItem()) {
            if (lastClickedSlot == null) {
                gUITween$clickTime = 0;
            }
            else {
                gUITween$clickTime = GUITweenClient.CONFIG.clickItemDuration;

                DragTween dragTween = GUITweenUtility.getDragTween();
                dragTween.stop();
            }
        }
    }

    @Inject(method = "drawItem", at = @At(value = "HEAD"))
    public void renderFloatingItemBefore(DrawContext guiGraphics, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        boolean hasChange = false;

        float itemSize = 16f; // 物品渲染尺寸（固定16x16）
        float centerX = x + itemSize / 2; // 物品中心X
        float centerY = y + itemSize / 2; // 物品中心Y

        float scale = 1;   // 放大比例
        float angle = 0;

        if (GUITweenClient.CONFIG.isEnableHoverItem()) {
            scale = GUITweenClient.CONFIG.clickItemScale;
            hasChange = true;
        }

        if (gUITween$clickTime > 0) {
            float progress = 1 - gUITween$clickTime / GUITweenClient.CONFIG.clickItemDuration;
            float strength = GUITweenClient.CONFIG.clickZoomStrength;
            scale = scale * TweenUtil.punch(strength, 1, progress);

            gUITween$clickTime -= GUITweenUtility.getDeltaTicks();

            hasChange = true;
        }

        if (GUITweenClient.CONFIG.isEnableDragItem()) {
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

            MatrixStack poseStack = guiGraphics.getMatrices();
            poseStack.push();

            // 矩阵操作：平移到中心 → 缩放 → 平移回原位置
            poseStack.translate(centerX, centerY, 0);
            poseStack.scale(scale, scale, 1.0f); // Z轴缩放不影响2D渲染，设为1
            poseStack.multiply(Axis.ZP.rotationDegrees(angle));
            poseStack.translate(-centerX, -centerY, 0);
        }
    }

    @Inject(method = "drawItem", at = @At(value = "TAIL"))
    public void renderFloatingItemAfter(DrawContext guiGraphics, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        MatrixStack poseStack = guiGraphics.getMatrices();

        if (!gUITween$isFloatingTween)
            return;

        gUITween$isFloatingTween = false;

        poseStack.pop();
    }

    @Inject(method = "drawMouseoverTooltip", at = @At(value = "HEAD"))
    public void renderTooltipBefore(DrawContext guiGraphics, int x, int y, CallbackInfo ci) {
        if (!GUITweenClient.CONFIG.isEnableTooltip())
            return;

        float duration = GUITweenClient.CONFIG.tooltipDuration;
        if (gUITween$tooltipShowTick > duration)
            return;

        gUITween$inTooltip = true;

        float progress = gUITween$tooltipShowTick / duration;
        float alpha = TweenUtil.tween(0, 1, progress, GUITweenClient.CONFIG.tooltipEase.get());
        guiGraphics.setShaderColor(1, 1, 1, alpha);

        GUITweenUtility.pushFontAlpha(alpha);

        gUITween$tooltipShowTick += GUITweenUtility.getDeltaTicks();
    }

    @Inject(method = "drawMouseoverTooltip", at = @At(value = "TAIL"))
    public void renderTooltipAfter(DrawContext guiGraphics, int x, int y, CallbackInfo ci) {
        if (!gUITween$inTooltip)
            return;

        gUITween$inTooltip = false;
        GUITweenUtility.popFontAlpha();
        guiGraphics.setShaderColor(1, 1, 1, 1);
    }

    @WrapOperation(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;close()V"
            )
    )
    private void onCloseBefore(HandledScreen<?> screen, Operation<Void> original) {
        if (!gUITween$playCloseTween()) {
            original.call(screen);
        }
    }

    @Inject(method = "close", at = @At("TAIL"))
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