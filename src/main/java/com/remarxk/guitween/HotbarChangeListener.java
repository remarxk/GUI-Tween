package com.remarxk.guitween;

import com.remarxk.guitween.anim.Tween;
import com.remarxk.guitween.anim.TweenPool;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;

@Mod.EventBusSubscriber
public class HotbarChangeListener {
    private static int lastSelected = -1;

    private static boolean hasItem = true;

    public static HashMap<Integer, Tween> hotbarAnimStateMap = new HashMap<>();

    public static float animTick = 0;

    public static float lackTick = 0;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Level level = player.level();

        // 仅处理客户端玩家 + 主 Tick 阶段
        if (level.isClientSide) {
            int index = player.getInventory().selected;

            // 判断是否切换了选中物品
            if (lastSelected != index) {
                if (lastSelected >= 0) {
//                    Tween tween = hotbarAnimStateMap.getOrDefault(lastSelected, null);
//                    if (tween != null && tween.stopValue > 1) { // 放大过程中
//                        tween.rewind = true;
//                    }

                    Tween tween = hotbarAnimStateMap.remove(lastSelected);
                    if (tween != null) {
                        TweenPool.releaseTween(tween);
                    }
                }

                if (GUITween.CONFIG.isEnableHoldItem()) {
                    animTick = lastSelected >= 0 ? 0 : GUITween.CONFIG.getHoldItemTotalDuration();
                }
                lastSelected = index;

                lackTick = GUITween.CONFIG.lackDuration;
                hasItem = player.getInventory().getSelected().getCount() > 0;

                if (GUITween.CONFIG.isEnableHoldItem()) {
                    Tween tween = hotbarAnimStateMap.getOrDefault(index, null);
                    if (tween == null) {
                        tween = TweenPool.getTween();
                        tween.tick = 0;
                        tween.totalTick = GUITween.CONFIG.holdZoomInDuration;
                        tween.ease = GUITween.CONFIG.holdZoomInEase.get();
                        tween.startValue = 1;
                        tween.stopValue = GUITween.CONFIG.holdZoomScale;
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