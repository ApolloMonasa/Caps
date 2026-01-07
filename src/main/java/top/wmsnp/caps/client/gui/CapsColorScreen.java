package top.wmsnp.caps.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;
import top.wmsnp.caps.common.CapsConfig;
import top.wmsnp.caps.utils.TypeUtils;

import java.awt.Color;

public class CapsColorScreen extends Screen {
    private final Screen lastScreen;

    // 经典调色板颜色预设
    private static final int[] PALETTE = {
            0xFFFFFF, 0xAAAAAA, 0x555555, 0x000000,
            0xFF5555, 0xFFAA00, 0xFFFF55, 0x55FF55,
            0x55FFFF, 0x5555FF, 0xFF55FF, 0xAA0000
    };

    private int r, g, b, a;
    private double thickness;

    private ExtendedSlider rSlider;
    private ExtendedSlider gSlider;
    private ExtendedSlider bSlider;
    private EditBox rBox, gBox, bBox;
    private boolean isUpdating = false;

    public CapsColorScreen(Screen lastScreen) {
        super(Component.translatable("caps.gui.title"));
        this.lastScreen = lastScreen;
        this.r = CapsConfig.COLOR_R.get();
        this.g = CapsConfig.COLOR_G.get();
        this.b = CapsConfig.COLOR_B.get();
        this.a = CapsConfig.COLOR_A.get();
        this.thickness = CapsConfig.LINEWIDTH.get();
    }

    @Override
    protected void init() {
        int midX = this.width / 2;
        // 1. 将起始高度由 40 改为 30，给下面腾出更多空间
        int startY = 30;

        // RGB 滑块起始位置
        int rgbStartY = startY + 25;

        // --- 2. RGB Sliders ---
        rSlider = new ExtendedSlider(midX - 100, rgbStartY, 150, 20, Component.literal("R: "), Component.empty(), 0, 255, r, 1.0, 0, true) {
            @Override
            protected void applyValue() { if (!isUpdating) { r = this.getValueInt(); updateBoxesFromSliders(); } }
        };
        gSlider = new ExtendedSlider(midX - 100, rgbStartY + 25, 150, 20, Component.literal("G: "), Component.empty(), 0, 255, g, 1.0, 0, true) {
            @Override
            protected void applyValue() { if (!isUpdating) { g = this.getValueInt(); updateBoxesFromSliders(); } }
        };
        bSlider = new ExtendedSlider(midX - 100, rgbStartY + 50, 150, 20, Component.literal("B: "), Component.empty(), 0, 255, b, 1.0, 0, true) {
            @Override
            protected void applyValue() { if (!isUpdating) { b = this.getValueInt(); updateBoxesFromSliders(); } }
        };

        this.addRenderableWidget(rSlider);
        this.addRenderableWidget(gSlider);
        this.addRenderableWidget(bSlider);

        // --- 3. Input Boxes ---
        rBox = new EditBox(this.font, midX + 60, rgbStartY, 40, 20, Component.literal("R"));
        gBox = new EditBox(this.font, midX + 60, rgbStartY + 25, 40, 20, Component.literal("G"));
        bBox = new EditBox(this.font, midX + 60, rgbStartY + 50, 40, 20, Component.literal("B"));

        updateBoxesFromSliders();

        rBox.setResponder(s -> tryParse(s, val -> { r = val; updateSlidersFromBoxes(); }));
        gBox.setResponder(s -> tryParse(s, val -> { g = val; updateSlidersFromBoxes(); }));
        bBox.setResponder(s -> tryParse(s, val -> { b = val; updateSlidersFromBoxes(); }));

        this.addRenderableWidget(rBox);
        this.addRenderableWidget(gBox);
        this.addRenderableWidget(bBox);

        // --- 4. Thickness Slider ---
        // 放在 RGB 下方 80 像素处
        int thicknessY = rgbStartY + 80;
        ModConfigSpec.Range<@NonNull Double> thicknessRange = TypeUtils.getRange(CapsConfig.LINEWIDTH.getSpec(), Double.class);
        ExtendedSlider thicknessSlider = new ExtendedSlider(midX - 100, thicknessY, 200, 20, Component.literal("Thickness: "), Component.empty(), thicknessRange.getMin(), thicknessRange.getMax(), thickness, 0.01, 2, true) {
            @Override
            protected void applyValue() {
                if (!isUpdating) thickness = this.getValue();
            }
        };
        this.addRenderableWidget(thicknessSlider);


        // --- 5. Palette (调色板) ---
        // 放在 Thickness 下方 30 像素处
        int pX = midX - 100;
        int pY = thicknessY + 30;

        for (int i = 0; i < PALETTE.length; i++) {
            int color = PALETTE[i];
            int colX = pX + (i % 6) * 25;
            int colY = pY + (i / 6) * 25;

            Button btn = Button.builder(Component.empty(), b -> {
                Color c = new Color(color);
                this.r = c.getRed();
                this.g = c.getGreen();
                this.b = c.getBlue();
                refreshAllWidgets();
            }).bounds(colX, colY, 20, 20).build();

            this.addRenderableWidget(btn);
        }

        // --- 6. Save & Exit ---
        int saveBtnY = pY + 50 + 10;

        this.addRenderableWidget(Button.builder(Component.literal("Save & Close"), b -> onClose())
                .bounds(midX - 50, saveBtnY, 100, 20).build());
    }

    private void refreshAllWidgets() {
        isUpdating = true;
        if (rSlider != null) rSlider.setValue(r);
        if (gSlider != null) gSlider.setValue(g);
        if (bSlider != null) bSlider.setValue(b);
        updateBoxText(rBox, r);
        updateBoxText(gBox, g);
        updateBoxText(bBox, b);
        isUpdating = false;
    }

    private void updateBoxesFromSliders() {
        if (isUpdating) return;
        isUpdating = true;
        updateBoxText(rBox, r);
        updateBoxText(gBox, g);
        updateBoxText(bBox, b);
        isUpdating = false;
    }

    private void updateSlidersFromBoxes() {
        if (isUpdating) return;
        isUpdating = true;
        if (rSlider != null) rSlider.setValue(r);
        if (gSlider != null) gSlider.setValue(g);
        if (bSlider != null) bSlider.setValue(b);
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
        } catch (NumberFormatException ignored) {}
    }

    @Override
    public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF); // 标题也稍微往上挪一点

        // 重新计算坐标以确保预览色块和调色板色块位置正确
        int midX = this.width / 2;
        int startY = 30; // 必须和 init() 保持一致
        int rgbStartY = startY + 25;
        int thicknessY = rgbStartY + 80;
        int pY = thicknessY + 30; // 必须和 init() 保持一致

        // 颜色预览 (在右侧)
        int previewColor = (255 << 24) | (r << 16) | (g << 8) | b;
        graphics.fill(midX + 110, rgbStartY, midX + 150, rgbStartY + 40, previewColor);
        graphics.drawCenteredString(this.font, "Preview", midX + 130, rgbStartY + 45, 0xFFFFFF);

        // 手动绘制调色板颜色块 (覆盖在按钮之上作为显示)
        int pX = midX - 100;
        for (int i = 0; i < PALETTE.length; i++) {
            int colX = pX + (i % 6) * 25;
            int colY = pY + (i / 6) * 25;
            int color = PALETTE[i] | 0xFF000000;
            graphics.fill(colX + 2, colY + 2, colX + 18, colY + 18, color);
        }
    }

    @Override
    public void onClose() {
        CapsConfig.COLOR_R.set(r);
        CapsConfig.COLOR_G.set(g);
        CapsConfig.COLOR_B.set(b);
        CapsConfig.COLOR_A.set(a);
        CapsConfig.LINEWIDTH.set(thickness);
        CapsConfig.CLIENT.save();

        this.minecraft.setScreen(lastScreen);
    }
}