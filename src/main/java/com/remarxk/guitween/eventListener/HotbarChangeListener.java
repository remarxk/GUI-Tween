package com.remarxk.guitween.eventListener;

import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.anim.Tween;
import com.remarxk.guitween.anim.UseTween;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.anim.TweenPool;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;

@EventBusSubscriber
public class HotbarChangeListener {
    private static boolean hasItem = true;

    public static int lastSelected = -1;

    public static HashMap<Integer, Tween> hotbarAnimStateMap = new HashMap<>();

    public static float animTick = 0;

    public static float lackTick = 0;

    public static int scrollDir = 0;

    public static int scrollSelected = -1;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();

        // 仅处理客户端玩家 + 主 Tick 阶段
        if (level.isClientSide) {
            if (GUITweenConfig.isEnableUse()) {
                UseTween usingTween = GUITweenUtility.getUsingTween();

                if (player.isUsingItem()) {
                    InteractionHand hand = player.getUsedItemHand();

                    int useSlot = -1;

                    if (hand == InteractionHand.MAIN_HAND) {
                        useSlot = player.getInventory().selected;
                    } else if (hand == InteractionHand.OFF_HAND) {
                        useSlot = 9;
                    }

                    usingTween.use(useSlot);
                }
            }

            int index = player.getInventory().selected;

            // 判断是否切换了选中物品
            if (lastSelected != index) {
                if (lastSelected >= 0) {
                    if (GUITweenConfig.hotbar.enableHoldZoomTransition.get()) {
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

                if (GUITweenConfig.isEnableHoldItem()) {
                    animTick = lastSelected >= 0 ? 0 : GUITweenConfig.getHoldItemTotalDuration();
                }
                lastSelected = index;

                lackTick = GUITweenConfig.hotbar.lackDuration.get().floatValue();
                hasItem = player.getInventory().getSelected().getCount() > 0;

                if (GUITweenConfig.isEnableHoldItem()) {
                    Tween tween = hotbarAnimStateMap.getOrDefault(index, null);
                    if (tween == null) {
                        tween = TweenPool.getTween();
                        tween.tick = 0;
                        tween.totalTick = GUITweenConfig.hotbar.holdZoomInDuration.get().floatValue();
                        tween.ease = GUITweenConfig.hotbar.holdZoomInEase.get();
                        tween.startValue = 1;
                        tween.stopValue = GUITweenConfig.hotbar.holdZoomScale.get().floatValue();
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
                ItemStack selectedItem = player.getInventory().getSelected();

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