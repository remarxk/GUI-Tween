package com.remarxk.guitween.gui;

import com.remarxk.guitween.GUITween;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DropdownScreen extends Screen {
    public static final ResourceLocation SEARCH = ResourceLocation.fromNamespaceAndPath(GUITween.MODID, "search");
    public static final ResourceLocation SCROLL_BACK = ResourceLocation.fromNamespaceAndPath(GUITween.MODID, "scroller");
    public static final ResourceLocation SCROLLBAR = ResourceLocation.fromNamespaceAndPath(GUITween.MODID, "scrollbar");
    public static final ResourceLocation SCROLLBAR_BACK = ResourceLocation.fromNamespaceAndPath(GUITween.MODID, "scrollbar_back");

    private final int searchHeight = Button.DEFAULT_HEIGHT;
    private final int scrollbarWidth = 6;

    private final Screen parent;
    private final List<Component> options;
    private final List<Integer> filtered = new ArrayList<>();

    private final int x, y, width;
    private int selectedIndex;
    private int scroll = 0;
    private final Consumer<Integer> onSelect;

    private final int itemHeight = 14;
    private final int maxVisible = 6;
    private boolean openUp = false;

    private EditBox editBox;

    // ⭐ 拖拽状态
    private boolean draggingScrollbar = false;
    private int dragOffsetY = 0;

    public DropdownScreen(Screen parent,
                          int x, int y, int width,
                          List<Component> options,
                          int selectedIndex,
                          Consumer<Integer> onSelect) {
        super(Component.empty());
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.options = options;
        this.selectedIndex = selectedIndex;
        this.onSelect = onSelect;
        resetFilter();
    }

    private void resetFilter() {
        filtered.clear();
        for (int i = 0; i < options.size(); i++) filtered.add(i);
    }

    private void updateFilter(String search) {
        filtered.clear();
        String s = search.toLowerCase();
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).getString().toLowerCase().contains(s)) {
                filtered.add(i);
            }
        }
        scroll = 0;
    }

    @Override
    protected void init() {
        int visible = Math.min(maxVisible, filtered.size());
        int totalHeight = searchHeight + visible * itemHeight;

        openUp = y + totalHeight > this.height;

        int baseY = openUp ? y - totalHeight : y;

        editBox = new EditBox(
                Minecraft.getInstance().font,
                x + 2,
                baseY,
                width - 4,
                searchHeight,
                Component.empty()
        );
        editBox.setHint(Component.translatable("guitween.search"));

        editBox.setResponder(this::updateFilter);
        addRenderableWidget(editBox);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
        // 渲染原界面
        parent.render(graphics, -100, -100, a);

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 100);

        int visible = Math.min(maxVisible, filtered.size());
        int totalHeight = searchHeight + visible * itemHeight;
        int baseY = openUp ? y - totalHeight : y;

        graphics.blitSprite(SCROLL_BACK, x, baseY, width, totalHeight + 2);

        // ===== 列表 =====
        for (int i = 0; i < visible; i++) {

            int idx = filtered.get(i + scroll);
            int itemY = baseY + searchHeight + i * itemHeight;

            boolean hover =
                    mouseX >= x && mouseX <= x + width &&
                            mouseY >= itemY && mouseY <= itemY + itemHeight;

            if (hover) {
                graphics.fill(x + 2, itemY, x + width - 2, itemY + itemHeight, 0xFF797979);
            }

            graphics.drawString(font, options.get(idx), x + 4, itemY + 3, 0xFFFFFFFF);
        }

        // ===== 滚动条 =====
        Layout l = computeLayout();

        if (l.total > maxVisible) {

            graphics.blitSprite(
                    SCROLLBAR_BACK,
                    l.barX,
                    l.listY,
                    scrollbarWidth,
                    l.listHeight
            );

            graphics.blitSprite(
                    SCROLLBAR,
                    l.barX,
                    l.thumbY,
                    scrollbarWidth,
                    l.thumbHeight
            );
        }

        super.render(graphics, mouseX, mouseY, a);

        graphics.pose().popPose();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
//        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        Layout l = computeLayout();

        if (l.total > maxVisible) {

            if (mouseX >= l.barX && mouseX <= l.barX + scrollbarWidth &&
                    mouseY >= l.listY && mouseY <= l.listY + l.listHeight) {

                if (mouseY >= l.thumbY && mouseY <= l.thumbY + l.thumbHeight) {
                    draggingScrollbar = true;
                    dragOffsetY = (int)mouseY - l.thumbY;
                } else {
                    float ratio = (float)(mouseY - l.listY) / l.listHeight;
                    scroll = Mth.clamp((int)(ratio * l.maxScroll), 0, l.maxScroll);
                }

                return true;
            }
        }

        for (int i = 0; i < l.visible; i++) {
            int itemY = l.baseY + searchHeight + i * itemHeight;

            if (mouseX >= x && mouseX <= x + width &&
                    mouseY >= itemY && mouseY <= itemY + itemHeight) {

                int idx = filtered.get(i + scroll);
                onSelect.accept(idx);
                Minecraft.getInstance().setScreen(parent);
                return true;
            }
        }

        Minecraft.getInstance().setScreen(parent);
        return true;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (draggingScrollbar) {

            Layout l = computeLayout();

            int newThumbY = (int)mouseY - dragOffsetY;
            newThumbY = Mth.clamp(newThumbY, l.listY, l.listY + l.listHeight - l.thumbHeight);

            float ratio = (float)(newThumbY - l.listY) / (l.listHeight - l.thumbHeight);
            scroll = Mth.clamp((int)(ratio * l.maxScroll), 0, l.maxScroll);
        }

        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingScrollbar = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (super.mouseScrolled(x, y, scrollX, scrollY)) return true;

        int visible = Math.min(maxVisible, filtered.size());
        scroll -= (int) Math.signum(scrollY);
        scroll = Mth.clamp(scroll, 0, Math.max(0, filtered.size() - visible));

        return true;
    }

    private Layout computeLayout() {
        Layout l = new Layout();

        l.total = filtered.size();
        l.visible = Math.min(maxVisible, l.total);

        int totalHeight = searchHeight + l.visible * itemHeight;
        l.baseY = openUp ? y - totalHeight : y;

        l.listY = l.baseY + searchHeight - 1;
        l.listHeight = l.visible * itemHeight + 2;

        l.maxScroll = Math.max(0, l.total - l.visible);

        l.barX = x + width - scrollbarWidth - 2;

        if (l.total > l.visible && l.maxScroll > 0) {
            l.thumbHeight = Math.max(10, l.listHeight * l.visible / l.total);
            l.thumbY = l.listY + (int)((l.listHeight - l.thumbHeight) * (scroll / (float) l.maxScroll));
        }

        return l;
    }

    private static class Layout {
        int baseY;
        int listY;
        int listHeight;
        int visible;
        int total;
        int maxScroll;

        int barX;
        int thumbHeight;
        int thumbY;
    }
}