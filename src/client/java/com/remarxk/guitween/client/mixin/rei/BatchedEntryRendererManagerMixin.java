package com.remarxk.guitween.client.mixin.rei;

import me.shedaniel.rei.impl.client.gui.widget.BatchedEntryRendererManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BatchedEntryRendererManager.class)
public class BatchedEntryRendererManagerMixin {
//    @Inject(
//            method = "render(ZLorg/apache/commons/lang3/mutable/MutableInt;Lorg/apache/commons/lang3/mutable/MutableLong;Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lme/shedaniel/rei/impl/client/gui/widget/BatchedEntryRendererManager;renderBatched(ZLorg/apache/commons/lang3/mutable/MutableInt;Lorg/apache/commons/lang3/mutable/MutableLong;Lnet/minecraft/client/gui/GuiGraphics;IIFLjava/lang/Iterable;[Ljava/lang/Object;)V"
//            ),
//            remap = false
//    )
//    private void renderBefore(boolean debugTime, MutableInt size, MutableLong time, GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
//        if (ReiCompat.inTween) {
//            graphics.pose().translate(-ReiCompat.dx + ReiCompat.dx / 20f, -ReiCompat.dy, 0);
//        }
//    }
//
//    @Inject(
//            method = "render(ZLorg/apache/commons/lang3/mutable/MutableInt;Lorg/apache/commons/lang3/mutable/MutableLong;Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lme/shedaniel/rei/impl/client/gui/widget/BatchedEntryRendererManager;renderBatched(ZLorg/apache/commons/lang3/mutable/MutableInt;Lorg/apache/commons/lang3/mutable/MutableLong;Lnet/minecraft/client/gui/GuiGraphics;IIFLjava/lang/Iterable;[Ljava/lang/Object;)V",
//                    shift = At.Shift.AFTER
//            ),
//            remap = false
//    )
//    private void renderAfter(boolean debugTime, MutableInt size, MutableLong time, GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
//        if (ReiCompat.inTween) {
//            graphics.pose().translate(ReiCompat.dx - ReiCompat.dx / 20f, ReiCompat.dy, 0);
//        }
//    }
}
