package com.remarxk.guitween.mixin.sophisticated;

import net.p3pp3rf1y.sophisticatedcore.common.gui.StorageContainerMenuBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StorageContainerMenuBase.class)
public interface StorageContainerMenuBaseAccessor {
    @Invoker("isUpgradeSlot")
    boolean gUITween$IsUpgradeSlot(int index);
}
