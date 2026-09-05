package com.remarxk.guitween.client.anim;

import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.event.PlayGuiSoundEvent;
import com.remarxk.guitween.client.util.Ease;
import com.remarxk.guitween.client.util.Tuple;
import com.remarxk.guitween.client.util.TweenUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

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

    /**
     * 直接清空所有“假物品”与等待移除记录（动画被禁用/界面异常时兜底，防止槽位被永久隐藏）。
     */
    public void clearAllFakeItem() {
        fakeItems.clear();
        waitRemove.clear();
    }

    /**
     * 清理已没有飞行任务对应的空假物品（例如中间路径异常导致 EMPTY 残留时，槽位不再被隐藏）。
     */
    public void removeStaleEmptyFake(ScreenHandler menu) {
        if (fakeItems.isEmpty()) {
            return;
        }

        List<Slot> stale = new ArrayList<>();
        for (var entry : fakeItems.entrySet()) {
            Slot slot = entry.getKey();
            ItemStack value = entry.getValue();
            if (value != null && value.isEmpty() && !hasActiveTweenOn(slot.id)) {
                stale.add(slot);
            }
        }

        for (Slot slot : stale) {
            fakeItems.remove(slot);
        }
    }

    private boolean hasActiveTweenOn(int slotIndex) {
        for (var entry : toMap.entrySet()) {
            int to = entry.getKey();
            for (Single single : entry.getValue().values()) {
                if (!single.rewind && to == slotIndex) {
                    return true;
                }
                if (single.rewind && single.from == slotIndex) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addMoveTween(int from, int to, ItemStack itemStack) {
        if (!GUITweenClient.CONFIG.isEnableMoveItem())
            return;

        HashMap<Integer, Single> fromMap = toMap.computeIfAbsent(to, k -> new HashMap<>());
        fromMap.put(from, new Single(from, itemStack));
    }

    public boolean swapMoveTween(Slot slot1, Slot slot2) {
        if (!GUITweenClient.CONFIG.isEnableMoveItem())
            return false;

        Single single1 = null;
        Single single2 = null;

        // slot2 -> slot1
        HashMap<Integer, Single> singleMap1 = toMap.get(slot1.id);

        // slot1 -> slot2
        HashMap<Integer, Single> singleMap2 = toMap.get(slot2.id);

        if (singleMap1 != null) {
            single1 = singleMap1.get(slot2.id);
        }

        if (singleMap2 != null) {
            single2 = singleMap2.get(slot1.id);
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
        if (!GUITweenClient.CONFIG.isEnableMoveItem())
            return;

        HashMap<Integer, Single> fromMap = toMap.get(to);
        if (fromMap != null) {
            fromMap.remove(from);

            if (fromMap.isEmpty()) {
                toMap.remove(to);
            }
        }
    }

    public Tuple<Integer, Integer> getMoveTweenValue(float fromX, float fromY, float toX, float toY, Single single, int to, ScreenHandler menu) {
        if (!GUITweenClient.CONFIG.isEnableMoveItem())
            return null;

        float moveDuration = GUITweenClient.CONFIG.moveDuration;
        Ease moveEase = GUITweenClient.CONFIG.moveEase.get();

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

    public int getFakeItemCount() {
        return fakeItems.size();
    }

    public int getPickupCount() {
        return pickupTweenMap.size();
    }

    public int getQuickCraftCount() {
        return quickCraftTweenMap.size();
    }

    public void addWaitRemove(int from, int to) {
        waitRemove.add(new Tuple<>(from, to));
    }

    public void removeUnuseFakeItem(ScreenHandler menu) {
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
        if (!GUITweenClient.CONFIG.isEnableFinishItem())
            return;

        if (!finishTween.containsKey(slot)) {
            finishTween.put(slot, 0f);

            PlayGuiSoundEvent.post(new PlayGuiSoundEvent(PlayGuiSoundEvent.SoundType.MOVE_FINISH));
        }
    }

    public Float getFinishScale(Slot slot) {
        if (!GUITweenClient.CONFIG.isEnableFinishItem())
            return null;

        Float tick = finishTween.get(slot);
        if (tick == null) {
            return null;
        }

        float punchStrength = GUITweenClient.CONFIG.finishPunchStrength;
        float finishDuration = GUITweenClient.CONFIG.finishDuration;

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
        if (!GUITweenClient.CONFIG.isEnablePickupItem())
            return;

        pickupTweenMap.put(slot, 0f);

        PlayGuiSoundEvent.post(new PlayGuiSoundEvent(PlayGuiSoundEvent.SoundType.PICK_UP));
    }

    public Float getPickupScale(Slot slot) {
        if (!GUITweenClient.CONFIG.isEnablePickupItem())
            return null;

        Float tick = pickupTweenMap.get(slot);
        if (tick == null) {
            return null;
        }

        float pickupDuration = GUITweenClient.CONFIG.pickupDuration;
        Ease pickupEase = GUITweenClient.CONFIG.pickupEase.get();

        float scale = TweenUtil.tween(GUITweenClient.CONFIG.clickItemScale, 1, tick / pickupDuration, pickupEase);

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
        if (!GUITweenClient.CONFIG.isEnableQuickCraft())
            return;

        quickCraftTweenMap.put(slot, 0f);

        PlayGuiSoundEvent.post(new PlayGuiSoundEvent(PlayGuiSoundEvent.SoundType.PICK_UP));
    }

    public Float getQuickCraftScale(Slot slot) {
        if (!GUITweenClient.CONFIG.isEnableQuickCraft())
            return null;

        Float tick = quickCraftTweenMap.get(slot);
        if (tick == null) {
            return null;
        }

        float clickScale = GUITweenClient.CONFIG.clickItemScale;
        float quickCraftDuration = GUITweenClient.CONFIG.quickCraftDuration;

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
