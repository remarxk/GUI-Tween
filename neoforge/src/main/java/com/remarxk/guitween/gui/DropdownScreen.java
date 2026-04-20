package com.remarxk.guitween.gui;

import com.remarxk.guitween.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DropdownScreen extends Screen {
    public static final Identifier SEARCH = Identifier.fromNamespaceAndPath(Constants.MODID, "search");
    public static final Identifier SCROLL_BACK = Identifier.fromNamespaceAndPath(Constants.MODID, "scroller");
    public static final Identifier SCROLLBAR = Identifier.fromNamespaceAndPath(Constants.MODID, "scrollbar");
    public static final Identifier SCROLLBAR_BACK = Identifier.fromNamespaceAndPath(Constants.MODID, "scrollbar_back");

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
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        // 渲染原界面
        parent.extractRenderState(graphics, -100, -100, a);

        int visible = Math.min(maxVisible, filtered.size());
        int totalHeight = searchHeight + visible * itemHeight;
        int baseY = openUp ? y - totalHeight : y;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLL_BACK, x, baseY, width, totalHeight + 2);

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

            graphics.text(font, options.get(idx), x + 4, itemY + 3, 0xFFFFFFFF);
        }

        // ===== 滚动条 =====
        Layout l = computeLayout();

        if (l.total > maxVisible) {

            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    SCROLLBAR_BACK,
                    l.barX,
                    l.listY,
                    scrollbarWidth,
                    l.listHeight
            );

            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    SCROLLBAR,
                    l.barX,
                    l.thumbY,
                    scrollbarWidth,
                    l.thumbHeight
            );
        }

        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) return true;

        Layout l = computeLayout();

        if (l.total > maxVisible) {

            if (event.x() >= l.barX && event.x() <= l.barX + scrollbarWidth &&
                    event.y() >= l.listY && event.y() <= l.listY + l.listHeight) {

                if (event.y() >= l.thumbY && event.y() <= l.thumbY + l.thumbHeight) {
                    draggingScrollbar = true;
                    dragOffsetY = (int)event.y() - l.thumbY;
                } else {
                    float ratio = (float)(event.y() - l.listY) / l.listHeight;
                    scroll = Mth.clamp((int)(ratio * l.maxScroll), 0, l.maxScroll);
                }

                return true;
            }
        }

        for (int i = 0; i < l.visible; i++) {
            int itemY = l.baseY + searchHeight + i * itemHeight;

            if (event.x() >= x && event.x() <= x + width &&
                    event.y() >= itemY && event.y() <= itemY + itemHeight) {

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
    public boolean mouseReleased(MouseButtonEvent event) {
        draggingScrollbar = false;
        return super.mouseReleased(event);
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