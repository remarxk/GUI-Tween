package com.remarxk.guitween.mixinAccess;

import com.remarxk.guitween.anim.Tween;
import net.minecraft.util.Tuple;
import net.minecraft.world.inventory.Slot;

import java.util.HashMap;

public interface AbstractContainerScreenMixinAccess {
    public String getGUITween$screenName();

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

    public HashMap<Integer, Tuple<Integer, Integer>> getGUITween$quickTweenSlots();

    public HashMap<Integer, Float> getGUITween$quickTicks();

    public boolean getGUITween$inSlotTween();

    public void setGUITween$inSlotTween(boolean value);

    public boolean getGUITween$isRenderQuick();

    public void setGUITween$isRenderQuick(boolean value);
}
