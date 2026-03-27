package top.wmsnp.caps.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import org.jspecify.annotations.NonNull;
import top.wmsnp.caps.common.ClientConfig;

import java.awt.*;

public class CapsColorScreen extends Screen {
    private final Screen lastScreen;

    private static final int[] PALETTE = {
            0xFFFFFF, 0xAAAAAA, 0x555555, 0x000000,
            0xFF5555, 0xFFAA00, 0xFFFF55, 0x55FF55,
            0x55FFFF, 0x5555FF, 0xFF55FF, 0xAA0000
    };

    private int r, g, b, a;

    private ExtendedSlider rSlider, gSlider, bSlider, aSlider;
    private EditBox rBox, gBox, bBox, aBox;

    private boolean isUpdating = false;

    public CapsColorScreen(Screen lastScreen) {
        super(Component.literal("color_title"));
        this.lastScreen = lastScreen;
        this.r = ClientConfig.COLOR_R.get();
        this.g = ClientConfig.COLOR_G.get();
        this.b = ClientConfig.COLOR_B.get();
        this.a = ClientConfig.COLOR_A.get();
    }

    @Override
    protected void init() {
        int midX = this.width / 2;
        int startY = 30;

        rSlider = createSlider(midX - 100, startY, "R: ", r, val -> {
            r = val;
            updateBoxesFromSliders();
        });
        rBox = createEditBox(midX + 60, startY, "R", r, val -> {
            r = val;
            updateSlidersFromBoxes();
        });

        gSlider = createSlider(midX - 100, startY + 25, "G: ", g, val -> {
            g = val;
            updateBoxesFromSliders();
        });
        gBox = createEditBox(midX + 60, startY + 25, "G", g, val -> {
            g = val;
            updateSlidersFromBoxes();
        });

        bSlider = createSlider(midX - 100, startY + 50, "B: ", b, val -> {
            b = val;
            updateBoxesFromSliders();
        });
        bBox = createEditBox(midX + 60, startY + 50, "B", b, val -> {
            b = val;
            updateSlidersFromBoxes();
        });

        aSlider = createSlider(midX - 100, startY + 75, "A: ", a, val -> {
            a = val;
            updateBoxesFromSliders();
        });
        aBox = createEditBox(midX + 60, startY + 75, "A", a, val -> {
            a = val;
            updateSlidersFromBoxes();
        });

        this.addRenderableWidget(rSlider);
        this.addRenderableWidget(rBox);
        this.addRenderableWidget(gSlider);
        this.addRenderableWidget(gBox);
        this.addRenderableWidget(bSlider);
        this.addRenderableWidget(bBox);
        this.addRenderableWidget(aSlider);
        this.addRenderableWidget(aBox);

        int pX = midX - 100;
        int pY = startY + 110;

        for (int i = 0; i < PALETTE.length; i++) {
            int color = PALETTE[i];
            int colX = pX + (i % 6) * 25;
            int colY = pY + (i / 6) * 25;

            Button btn = Button.builder(Component.empty(), b -> {
                Color c = new Color(color);
                this.r = c.getRed();
                this.g = c.getGreen();
                this.b = c.getBlue();
                this.a = ClientConfig.COLOR_A.getDefault();
                refreshAllWidgets();
            }).bounds(colX, colY, 20, 20).build();

            this.addRenderableWidget(btn);
        }

        int btnY = pY + 60;
        int btnWidth = 60;

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, b -> {
            this.minecraft.setScreen(lastScreen);
        }).bounds(midX - 100, btnY, btnWidth, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("controls.reset"), b -> {
            this.r = ClientConfig.COLOR_R.getDefault();
            this.g = ClientConfig.COLOR_G.getDefault();
            this.b = ClientConfig.COLOR_B.getDefault();
            this.a = ClientConfig.COLOR_A.getDefault();
            refreshAllWidgets();
        }).bounds(midX - 30, btnY, btnWidth, 20).build());

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> {
            saveChanges();
            this.minecraft.setScreen(lastScreen);
        }).bounds(midX + 40, btnY, btnWidth, 20).build());
    }

    private ExtendedSlider createSlider(int x, int y, String prefix, int currentVal, java.util.function.IntConsumer action) {
        return new ExtendedSlider(x, y, 150, 20, Component.literal(prefix), Component.empty(), 0, 255, currentVal, 1.0, 0, true) {
            @Override
            protected void applyValue() {
                if (!isUpdating) action.accept(this.getValueInt());
            }
        };
    }

    private EditBox createEditBox(int x, int y, String label, int currentVal, java.util.function.IntConsumer action) {
        EditBox box = new EditBox(this.font, x, y, 40, 20, Component.literal(label));
        box.setValue(String.valueOf(currentVal));
        box.setResponder(s -> tryParse(s, action));
        return box;
    }

    private void refreshAllWidgets() {
        isUpdating = true;
        if (rSlider != null) rSlider.setValue(r);
        if (gSlider != null) gSlider.setValue(g);
        if (bSlider != null) bSlider.setValue(b);
        if (aSlider != null) aSlider.setValue(a);

        updateBoxText(rBox, r);
        updateBoxText(gBox, g);
        updateBoxText(bBox, b);
        updateBoxText(aBox, a);
        isUpdating = false;
    }

    private void updateBoxesFromSliders() {
        if (isUpdating) return;
        isUpdating = true;
        updateBoxText(rBox, r);
        updateBoxText(gBox, g);
        updateBoxText(bBox, b);
        updateBoxText(aBox, a);
        isUpdating = false;
    }

    private void updateSlidersFromBoxes() {
        if (isUpdating) return;
        isUpdating = true;
        if (rSlider != null) rSlider.setValue(r);
        if (gSlider != null) gSlider.setValue(g);
        if (bSlider != null) bSlider.setValue(b);
        if (aSlider != null) aSlider.setValue(a);
        isUpdating = false;
    }

    private void updateBoxText(EditBox box, int val) {
        if (box != null) {
            String newVal = String.valueOf(val);
            if (!newVal.equals(box.getValue())) box.setValue(newVal);
        }
    }

    private void tryParse(String s, java.util.function.IntConsumer setter) {
        if (isUpdating) return;
        try {
            int val = Integer.parseInt(s);
            if (val >= 0 && val <= 255) setter.accept(val);
        } catch (NumberFormatException ignored) {
        }
    }

    private void saveChanges() {
        ClientConfig.COLOR_R.set(r);
        ClientConfig.COLOR_G.set(g);
        ClientConfig.COLOR_B.set(b);
        ClientConfig.COLOR_A.set(a);
        ClientConfig.SPEC.save();
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(this.font, this.title, this.width / 2, 10, 0xFFFFFF);
        int midX = this.width / 2;
        int startY = 30;
        int previewColor = (a << 24) | (r << 16) | (g << 8) | b;
        graphics.fill(midX + 110, startY + 20, midX + 150, startY + 60, previewColor);
        graphics.centeredText(this.font, "Preview", midX + 130, startY + 65, 0xFFFFFF);
        int pX = midX - 100;
        int pY = startY + 110;
        for (int i = 0; i < PALETTE.length; i++) {
            int colX = pX + (i % 6) * 25;
            int colY = pY + (i / 6) * 25;
            int color = PALETTE[i] | 0xFF000000;
            graphics.fill(colX + 2, colY + 2, colX + 18, colY + 18, color);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(lastScreen);
    }
}