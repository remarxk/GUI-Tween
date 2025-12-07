package com.remarxk.guitween;

import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class HotbarChangeListener {
    // 存储上一帧的选中物品，用于判断是否切换
    public static int lastSelected = -1;

    public static long animTick = 0;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // 仅处理客户端玩家 + 主 Tick 阶段
        if (event.side.isClient() && event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            int index = player.getInventory().selected;

            // 判断是否切换了选中物品
            if (lastSelected != index) {
                animTick = lastSelected >= 0 ? 0 : GUITween.CONFIG.getHoldItemTotalDuration();
                lastSelected = index;

                // 切换时执行逻辑（如更新渲染的放大物品）
                // GUITween.LOGGER.info("玩家切换了快捷栏，新选中物品：" + currentSelected.getItem().getDescriptionId());
            }
        }
    }
}