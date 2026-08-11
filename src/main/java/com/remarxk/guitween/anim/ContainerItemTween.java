package com.remarxk.guitween.anim;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.event.PlayGuiSoundEvent;
import com.remarxk.guitween.util.Ease;
import com.remarxk.guitween.util.Tuple;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContainerItemTween {
    private final HashMap<Integer, HashMap<Integer, Single>> toMap = new HashMap<>();

    private final HashMap<Slot, Float> finishTween = new HashMap<>();

    private final HashMap<Slot, ItemStack> fakeItems = new HashMap<>();

    private final List<Tuple<Integer, Integer>> waitRemove = new ArrayList<>();

    private final HashMap<Slot, Float> pickupTweenMap = new HashMap<>();

    private final HashMap<Slot, Float> quickCraftTweenMap = new HashMap<>();

    public void clearTween() {
        toMap.clear();
        finishTween.clear();
        fakeItems.clear();
        waitRemove.clear();
        pickupTweenMap.clear();
        quickCraftTweenMap.clear();
    }

    public void addMoveTween(int from, int to, ItemStack itemStack) {
        if (!GUITween.CONFIG.isEnableMoveItem())
            return;

        HashMap<Integer, Single> fromMap = toMap.computeIfAbsent(to, k -> new HashMap<>());
        fromMap.put(from, new Single(from, itemStack));
    }

    public boolean swapMoveTween(Slot slot1, Slot slot2) {
        if (!GUITween.CONFIG.isEnableMoveItem())
            return false;

        Single single1 = null;
        Single single2 = null;

        // slot2 -> slot1
        HashMap<Integer, Single> singleMap1 = toMap.get(slot1.index);

        // slot1 -> slot2
        HashMap<Integer, Single> singleMap2 = toMap.get(slot2.index);

        if (singleMap1 != null) {
            single1 = singleMap1.get(slot2.index);
        }

        if (singleMap2 != null) {
            single2 = singleMap2.get(slot1.index);
        }

        if (single1 != null && single2 != null) {
            single1.rewind = !single1.rewind;
            single2.rewind = !single2.rewind;

            addFakeItem(slot1, ItemStack.EMPTY);
            addFakeItem(slot2, ItemStack.EMPTY);
        }
        else {
            if (single1 != null) {
                single1.rewind = !single1.rewind;
                addFakeItem(slot2, ItemStack.EMPTY);
            }
            else if (single2 != null){
                single2.rewind = !single2.rewind;
                addFakeItem(slot1, ItemStack.EMPTY);
            }
        }

        return single1 != null || single2 != null;
    }

    public void removeTween(int from, int to) {
        if (!GUITween.CONFIG.isEnableMoveItem())
            return;

        HashMap<Integer, Single> fromMap = toMap.get(to);
        if (fromMap != null) {
            fromMap.remove(from);

            if (fromMap.isEmpty()) {
                toMap.remove(to);
            }
        }
    }

    public Tuple<Integer, Integer> getMoveTweenValue(float fromX, float fromY, float toX, float toY, Single single, int to, AbstractContainerMenu menu) {
        if (!GUITween.CONFIG.isEnableMoveItem())
            return null;

        float moveDuration = GUITween.CONFIG.moveDuration;
        Ease moveEase = GUITween.CONFIG.moveEase.get();

        if ((!single.rewind && single.tick >= moveDuration) || (single.rewind && single.tick <= 0)) {
            int arrival = single.rewind ? single.from : to;
            addWaitRemove(single.from, to);
            if (arrival != -1) {
                ItemStack fakeItem = getFakeItem(menu.getSlot(arrival));
                if (fakeItem != null && !fakeItem.isEmpty()) {
                    addFinishTween(menu.getSlot(arrival));
                }
            }
            return null;
        }

        float dx = TweenUtil.tween(fromX, toX, single.tick / moveDuration, moveEase);
        float dy = TweenUtil.tween(fromY, toY, single.tick / moveDuration, moveEase);

        single.tick += single.rewind ? -GUITweenUtility.getDeltaTicks() : GUITweenUtility.getDeltaTicks();

        return new Tuple<>((int) dx, (int) dy);
    }

    public void addFakeItem(Slot slot, ItemStack itemStack) {
        if (!fakeItems.containsKey(slot)) {
            fakeItems.put(slot, itemStack);
        }
    }

    public void removeFakeItem(Slot slot) {
        fakeItems.remove(slot);
    }

    public ItemStack getFakeItem(Slot slot) {
        return fakeItems.get(slot);
    }

    public HashMap<Integer, HashMap<Integer, Single>> getToMap() {
        return toMap;
    }

    public void addWaitRemove(int from, int to) {
        waitRemove.add(new Tuple<>(from, to));
    }

    public void removeUnuseFakeItem(AbstractContainerMenu menu) {
        for (Tuple<Integer, Integer> tuple : waitRemove) {
            int from = tuple.getA();
            int to = tuple.getB();

            HashMap<Integer, Single> fromMap = toMap.get(to);
            if (fromMap != null) {
                Single single = fromMap.remove(from);
                if (single != null) {
                    int arrival = single.rewind ? single.from : to;
                    if (arrival != -1) {
                        ItemStack itemStack = fakeItems.get(menu.getSlot(arrival));
                        if (itemStack != null) {
                            itemStack.setCount(itemStack.getCount() + single.itemStack.getCount());
                        }
                    }

                    if (fromMap.isEmpty()) {
                        toMap.remove(to);
                    }

                    // rewind 时物品实际落在 single.from 而非 to，且可能与其他 Tween 共享格子上的 fakeItem
                    // 因此按“到达格子”移除，并确保没有其他 Tween 还会落到该格子时才移除
                    if (to != -1 && !hasTweenArriveAt(to)) {
                        fakeItems.remove(menu.getSlot(to));
                    }
                    if (arrival != -1 && !hasTweenArriveAt(arrival)) {
                        fakeItems.remove(menu.getSlot(arrival));
                    }
                }
            }
        }

        waitRemove.clear();
    }

    private boolean hasTweenArriveAt(int slotIndex) {
        for (var entry : toMap.entrySet()) {
            int to = entry.getKey();
            for (Single single : entry.getValue().values()) {
                int arrival = single.rewind ? single.from : to;
                if (arrival == slotIndex) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addFinishTween(Slot slot) {
        if (!GUITween.CONFIG.isEnableFinishItem())
            return;

        if (!finishTween.containsKey(slot)) {
            finishTween.put(slot, 0f);

            MinecraftForge.EVENT_BUS.post(new PlayGuiSoundEvent(PlayGuiSoundEvent.SoundType.MOVE_FINISH));
        }
    }

    public Float getFinishScale(Slot slot) {
        if (!GUITween.CONFIG.isEnableFinishItem())
            return null;

        Float tick = finishTween.get(slot);
        if (tick == null) {
            return null;
        }

        float punchStrength = GUITween.CONFIG.finishPunchStrength;
        float finishDuration = GUITween.CONFIG.finishDuration;

        float scale = TweenUtil.punch(punchStrength, 1, tick / finishDuration);

        tick = tick + GUITweenUtility.getDeltaTicks();
        if (tick >= finishDuration) {
            finishTween.remove(slot);
        }
        else {
            finishTween.put(slot, tick);
        }

        return scale;
    }

    public void addPickupTween(Slot slot) {
        if (!GUITween.CONFIG.isEnablePickupItem())
            return;

        pickupTweenMap.put(slot, 0f);

        MinecraftForge.EVENT_BUS.post(new PlayGuiSoundEvent(PlayGuiSoundEvent.SoundType.PICK_UP));
    }

    public Float getPickupScale(Slot slot) {
        if (!GUITween.CONFIG.isEnablePickupItem())
            return null;

        Float tick = pickupTweenMap.get(slot);
        if (tick == null) {
            return null;
        }

        float pickupDuration = GUITween.CONFIG.pickupDuration;
        Ease pickupEase = GUITween.CONFIG.pickupEase.get();

        float scale = TweenUtil.tween(GUITween.CONFIG.clickItemScale, 1, tick / pickupDuration, pickupEase);

        tick = tick + GUITweenUtility.getDeltaTicks();
        if (tick >= pickupDuration) {
            pickupTweenMap.remove(slot);
        }
        else {
            pickupTweenMap.put(slot, tick);
        }

        return scale;
    }

    public void addQuickCraftTween(Slot slot) {
        if (!GUITween.CONFIG.isEnableQuickCraft())
            return;

        quickCraftTweenMap.put(slot, 0f);

        MinecraftForge.EVENT_BUS.post(new PlayGuiSoundEvent(PlayGuiSoundEvent.SoundType.PICK_UP));
    }

    public Float getQuickCraftScale(Slot slot) {
        if (!GUITween.CONFIG.isEnableQuickCraft())
            return null;

        Float tick = quickCraftTweenMap.get(slot);
        if (tick == null) {
            return null;
        }

        float clickScale = GUITween.CONFIG.clickItemScale;
        float quickCraftDuration = GUITween.CONFIG.quickCraftDuration;

        float scale = TweenUtil.tween(clickScale, 1f, tick / quickCraftDuration, Ease.IN_OUT_SINE);

        tick = tick + GUITweenUtility.getDeltaTicks();
        if (tick >= quickCraftDuration) {
            quickCraftTweenMap.remove(slot);
        }
        else {
            quickCraftTweenMap.put(slot, tick);
        }

        return scale;
    }

    public static class Single {
        public int from;
        public boolean rewind;
        public float tick;
        public ItemStack itemStack;

        public Single(int from, ItemStack itemStack) {
            this.from = from;
            this.itemStack = itemStack;
            tick = 0;
        }
    }
}
