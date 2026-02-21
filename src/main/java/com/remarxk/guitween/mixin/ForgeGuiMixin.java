package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ForgeGui.class)
public abstract class ForgeGuiMixin extends Gui {
    public ForgeGuiMixin(Minecraft pMinecraft, ItemRenderer pItemRenderer) {
        super(pMinecraft, pItemRenderer);
    }

    @Unique
    private static int gUITween$curArmorValue = -1;

    @Unique
    private static int gUITween$lastArmorValue = -1;

    @Unique
    private static float gUITween$armorChangeTick;

    @Shadow(remap = false)
    public int leftHeight;;

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    protected void renderArmor(GuiGraphics guiGraphics, int width, int height) {
        minecraft.getProfiler().push("armor");

        RenderSystem.enableBlend();
        int left = width / 2 - 91;
        int top = height - leftHeight;

        int level = minecraft.player.getArmorValue();

        float duration = GUITween.CONFIG.armorDuration;

        if (GUITween.CONFIG.isEnableArmor()) {
            if (level != gUITween$curArmorValue) {
                if (gUITween$curArmorValue != -1) {
                    gUITween$lastArmorValue = gUITween$curArmorValue;
                    gUITween$armorChangeTick = 0;
                }
                else {
                    gUITween$lastArmorValue = level;
                    gUITween$armorChangeTick = duration;
                }

                gUITween$curArmorValue = level;
            }
        }

        float progress = gUITween$armorChangeTick / duration;
        if (GUITween.CONFIG.isEnableArmor() && progress < 1) {
            boolean isUp = gUITween$curArmorValue > gUITween$lastArmorValue;

            float originScale = GUITween.CONFIG.upArmorScale;
            float scale = isUp ? TweenUtil.tween(originScale, 1, progress, GUITween.CONFIG.upArmorEase.get()) : 1;

            float shakeStrength = GUITween.CONFIG.downArmorShakeStrength;
            float dx = !isUp ? TweenUtil.shake(0, gUITween$armorChangeTick, duration, shakeStrength) : 0;
            float dy = !isUp ? TweenUtil.shake(1, gUITween$armorChangeTick, duration, shakeStrength) : 0;

            PoseStack poseStack = guiGraphics.pose();

            int targetArmor = isUp ? gUITween$curArmorValue : gUITween$lastArmorValue;

            for (int i = 1; i < 20; i += 2)
            {
                if (i <= targetArmor) {
                    int spriteOffset = i == targetArmor ? 25 : 34;

                    poseStack.pushPose();

                    if (isUp) {
                        float centerX = left + 4.5f;
                        float centerY = top + 4.5f;

                        poseStack.translate(centerX, centerY, 0);
                        poseStack.scale(scale, scale, 1);
                        poseStack.translate(-centerX, -centerY, 0);

                    }
                    else {
                        poseStack.translate(dx, dy, 0);
                    }
                    guiGraphics.blit(GUI_ICONS_LOCATION, left, top, spriteOffset, 9, 9, 9);
                    poseStack.popPose();
                }

                if (i > targetArmor) {
                    guiGraphics.blit(GUI_ICONS_LOCATION, left, top, 16, 9, 9, 9);
                }

                left += 8;
            }

            gUITween$armorChangeTick += GUITweenUtility.getDeltaTicks();
        }
        else {
            for (int i = 1; level > 0 && i < 20; i += 2)
            {
                if (i < level)
                {
                    guiGraphics.blit(GUI_ICONS_LOCATION, left, top, 34, 9, 9, 9);
                }
                else if (i == level)
                {
                    guiGraphics.blit(GUI_ICONS_LOCATION, left, top, 25, 9, 9, 9);
                }
                else if (i > level)
                {
                    guiGraphics.blit(GUI_ICONS_LOCATION, left, top, 16, 9, 9, 9);
                }

                left += 8;
            }
        }

        leftHeight += 10;

        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }
}
