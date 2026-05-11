package com.remarxk.guitween.client.eventListener;

import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.anim.Tween;
import com.remarxk.guitween.client.anim.TweenPool;
import com.remarxk.guitween.client.anim.UseTween;
import com.remarxk.guitween.client.config.GUITweenConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.HashMap;

public class HotbarChangeListener {
    private static boolean hasItem = true;

    public static int lastSelected = -1;

    public static HashMap<Integer, Tween> hotbarAnimStateMap = new HashMap<>();

    public static float animTick = 0;

    public static float lackTick = 0;

    public static int scrollDir = 0;

    public static int scrollSelected = -1;

    public static void onPlayerTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        ClientWorld level = player.clientWorld;

        // 仅处理客户端玩家 + 主 Tick 阶段
        if (level.isClient) {
            if (GUITweenClient.CONFIG.isEnableUse()) {
                UseTween usingTween = GUITweenUtility.getUsingTween();

                if (player.isUsingItem()) {
                    Hand hand = player.getActiveHand();

                    int useSlot = -1;

                    if (hand == Hand.MAIN_HAND) {
                        useSlot = player.getInventory().selectedSlot;
                    } else if (hand == Hand.OFF_HAND) {
                        useSlot = 9;
                    }

                    usingTween.use(useSlot);
                }
            }

            int index = player.getInventory().selectedSlot;

            // 判断是否切换了选中物品
            if (lastSelected != index) {
                if (lastSelected >= 0) {
                    if (GUITweenClient.CONFIG.enableHoldZoomTransition) {
                        Tween tween = hotbarAnimStateMap.getOrDefault(lastSelected, null);
                        if (tween != null && tween.stopValue > 1) { // 放大过程中
                            tween.rewind = true;
                        }
                    }
                    else {
                        Tween tween = hotbarAnimStateMap.remove(lastSelected);
                        if (tween != null) {
                            TweenPool.releaseTween(tween);
                        }
                    }
                }

                if (GUITweenClient.CONFIG.isEnableHoldItem()) {
                    animTick = lastSelected >= 0 ? 0 : GUITweenClient.CONFIG.getHoldItemTotalDuration();
                }
                lastSelected = index;

                lackTick = GUITweenClient.CONFIG.lackDuration;
                hasItem = player.getInventory().getMainHandStack().getCount() > 0;

                if (GUITweenClient.CONFIG.isEnableHoldItem()) {
                    Tween tween = hotbarAnimStateMap.getOrDefault(index, null);
                    if (tween == null) {
                        tween = TweenPool.getTween();
                        tween.tick = 0;
                        tween.totalTick = GUITweenClient.CONFIG.holdZoomInDuration;
                        tween.ease = GUITweenClient.CONFIG.holdZoomInEase.get();
                        tween.startValue = 1;
                        tween.stopValue = GUITweenClient.CONFIG.holdZoomScale;
                        tween.rewind = false;
                        hotbarAnimStateMap.put(index, tween);
                    }
                    else {
                        if (tween.stopValue > 1) {
                            if (tween.rewind)
                                tween.rewind = false;
                        }
                        else {
                            if (!tween.rewind)
                                tween.rewind = true;
                        }
                    }
                }
            }
            else {
                ItemStack selectedItem = player.getInventory().getMainHandStack();

                boolean curHasItem = selectedItem.getCount() > 0;
                if (curHasItem != hasItem) {
                    hasItem = curHasItem;
                    if (!hasItem)
                        lackTick = 0;
                }
            }
        }
    }
}