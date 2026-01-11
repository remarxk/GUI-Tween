package com.remarxk.guitween;

import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.AnimationStatePool;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;

@EventBusSubscriber
public class HotbarChangeListener {
    private static int lastSelected = -1;

    private static boolean hasItem = true;

    public static HashMap<Integer, AnimationState> hotbarAnimStateMap = new HashMap<>();

    public static float animTick = 0;

    public static float lackTick = 0;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();

        // 仅处理客户端玩家 + 主 Tick 阶段
        if (level.isClientSide) {
            int index = player.getInventory().selected;

            // 判断是否切换了选中物品
            if (lastSelected != index) {
                if (lastSelected >= 0) {
//                    AnimationState state = hotbarAnimStateMap.getOrDefault(lastSelected, null);
//                    if (state != null && state.stopValue > 1) { // 放大过程中
//                        state.rewind = true;
//                    }

                    AnimationState state = hotbarAnimStateMap.remove(lastSelected);
                    if (state != null) {
                        AnimationStatePool.releaseAnimationState(state);
                    }
                }

                if (GUITweenConfig.isEnableHoldItem()) {
                    animTick = lastSelected >= 0 ? 0 : GUITweenConfig.getHoldItemTotalDuration();
                }
                lastSelected = index;

                lackTick = GUITweenConfig.lackDuration.get().floatValue();
                hasItem = player.getInventory().getSelected().getCount() > 0;

                if (GUITweenConfig.isEnableHoldItem()) {
                    AnimationState state = hotbarAnimStateMap.getOrDefault(index, null);
                    if (state == null) {
                        state = AnimationStatePool.getAnimationState();
                        state.tick = 0;
                        state.totalTick = GUITweenConfig.holdZoomInDuration.get().floatValue();
                        state.ease = GUITweenConfig.holdZoomInEase.get();
                        state.startValue = 1;
                        state.stopValue = GUITweenConfig.holdZoomScale.get().floatValue();
                        state.rewind = false;
                        hotbarAnimStateMap.put(index, state);
                    }
                    else {
                        if (state.stopValue > 1) {
                            if (state.rewind)
                                state.rewind = false;
                        }
                        else {
                            if (!state.rewind)
                                state.rewind = true;
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