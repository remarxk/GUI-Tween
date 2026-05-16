package com.remarxk.guitween.mixinAccess;

import com.remarxk.guitween.anim.Tween;
import com.remarxk.guitween.util.Tuple;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;

public interface AbstractContainerScreenMixinAccess {
    public String getGUITween$screenName();

    public void setGUITween$isDisableScreenTween(boolean isDisableScreenTween);

    public boolean getGUITween$isDisableScreenTween();

    public boolean getGUITween$inTween();

    public void setGUITween$inTween(boolean inScale);

    public float getGUITween$openTick();

    public void setGUITween$openTick(float openTick);

    public Slot getGUITween$lastHoverSlot();

    public void setGUITween$lastHoverSlot(Slot slot);

    public HashMap<Slot, Tween> getGUITween$hoverSlotMap();

    public boolean getGUITween$inTooltipTween();

    public void setGUITween$inTooltipTween(boolean value);

    public float getGUITween$tooltipShowTick();

    public void setGUITween$tooltipShowTick(float tick);

    public HashMap<Slot, Tuple<Integer, Integer>> getGUITween$quickTweenSlots();

    public HashMap<Slot, Float> getGUITween$quickTicks();

    public ItemStack gUITween$getDraggingItem();

    public ItemStack getGUITween$lastDraggingItem();

    public void setGUITween$lastDraggingItem(ItemStack itemStack);

    public float getGUITween$sameItemTick();

    public void setGUITween$sameItemTick(float tick);

    public boolean getGUITween$inSlotTween();

    public void setGUITween$inSlotTween(boolean value);

    public boolean getGUITween$isRenderQuick();

    public void setGUITween$isRenderQuick(boolean value);

    public void setGUITween$clickTime(float time);

    public float getGUITween$clickTime();

    public int gUITween$getGuiLeft();

    public int gUITween$getGuiTop();

    public boolean gUITween$playCloseTween();

    public boolean gUITween$inCloseTween();

    public void gUITween$setNeedClose(boolean close);

    public boolean gUITween$getNeedClose();
}
