package com.remarxk.guitween.mixin.rei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.compat.ReiCompat;
import com.remarxk.guitween.util.TweenUtil;
import me.shedaniel.rei.impl.client.gui.widget.BatchedEntryRendererManager;
import me.shedaniel.rei.impl.client.gui.widget.EntryWidget;
import net.minecraft.client.gui.GuiGraphics;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BatchedEntryRendererManager.class)
public class BatchedEntryRendererManagerMixin {
    @Inject(
            method = "render(ZLorg/apache/commons/lang3/mutable/MutableInt;Lorg/apache/commons/lang3/mutable/MutableLong;Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/shedaniel/rei/impl/client/gui/widget/BatchedEntryRendererManager;renderBatched(ZLorg/apache/commons/lang3/mutable/MutableInt;Lorg/apache/commons/lang3/mutable/MutableLong;Lnet/minecraft/client/gui/GuiGraphics;IIFLjava/lang/Iterable;[Ljava/lang/Object;)V"
            ),
            remap = false
    )
    private void renderBefore(boolean debugTime, MutableInt size, MutableLong time, GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (ReiCompat.inTween) {
            graphics.pose().translate(-ReiCompat.dx + ReiCompat.dx / 20f, -ReiCompat.dy, 0);
        }
    }

    @Inject(
            method = "render(ZLorg/apache/commons/lang3/mutable/MutableInt;Lorg/apache/commons/lang3/mutable/MutableLong;Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/shedaniel/rei/impl/client/gui/widget/BatchedEntryRendererManager;renderBatched(ZLorg/apache/commons/lang3/mutable/MutableInt;Lorg/apache/commons/lang3/mutable/MutableLong;Lnet/minecraft/client/gui/GuiGraphics;IIFLjava/lang/Iterable;[Ljava/lang/Object;)V",
                    shift = At.Shift.AFTER
            ),
            remap = false
    )
    private void renderAfter(boolean debugTime, MutableInt size, MutableLong time, GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (ReiCompat.inTween) {
            graphics.pose().translate(ReiCompat.dx - ReiCompat.dx / 20f, ReiCompat.dy, 0);
        }
    }
}
