package com.remarxk.guitween.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

public class DropdownWidget extends AbstractWidget {

    private final List<Component> options;
    private int selectedIndex = 0;
    private Consumer<Integer> onSelect;

    private final Button button;

    public DropdownWidget(int x, int y, int width, int height, List<Component> options, Consumer<Integer> onSelect) {
        super(x, y, width, height, Component.empty());
        this.options = options;
        this.onSelect = onSelect;

        button = Button.builder(Component.empty(), new Button.OnPress() {
            @Override
            public void onPress(Button button) {
                Minecraft mc = Minecraft.getInstance();
                mc.setScreen(new DropdownScreen(
                        mc.screen,       // 当前 Screen 作为 parent
                        getX(), getY() + height, width,
                        options,
                        selectedIndex,
                        index -> setSelectedIndex(index) // 回调
                ));
            }
        }).build();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int i) {
        if (i < 0 || i >= options.size()) return;
        selectedIndex = i;
        if (onSelect != null) onSelect.accept(i);
    }

    public Component getSelected() {
        return options.get(selectedIndex);
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float v) {
        button.setX(getX());
        button.setY(getY());
        button.setWidth(width);
        button.setHeight(height);
        button.setMessage(getSelected());

        button.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, v);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (button.mouseClicked(event, doubleClick)) {
            return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}