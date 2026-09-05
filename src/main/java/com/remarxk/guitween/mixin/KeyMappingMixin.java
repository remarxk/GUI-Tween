package com.remarxk.guitween.mixin;

import com.remarxk.guitween.GUITweenUtility;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Forge/NeoForge 会给 {@link KeyMapping#isDown()} 追加冲突上下文判断（例如 IN_GAME 上下文要求
 * {@code gui.screen() == null}）。关闭动画期间屏幕仍需保留以播放动画，因此即使 keyPress 中
 * 已把键位置为按下，读取时仍会被判定为未按下，导致无法移动。
 *
 * 这里在关闭动画阶段直接返回内部 isDown 状态，跳过冲突上下文门控。
 * Fabric 原版没有该冲突上下文门控，因此此修复只放在 Forge/NeoForge 工程下。
 */
@Mixin(KeyMapping.class)
public class KeyMappingMixin {
    @Shadow
    private boolean isDown;

    @Inject(
            method = "isDown",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void ignoreConflictContextWhileClosing(CallbackInfoReturnable<Boolean> cir) {
        if (GUITweenUtility.isWindowClosing) {
            cir.setReturnValue(isDown);
        }
    }
}
