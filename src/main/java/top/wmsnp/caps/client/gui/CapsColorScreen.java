package top.wmsnp.caps.client.gui;

import net.minecraft.client.gui.GuiGraphics;
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

    // 经典调色板颜色预设
    private static final int[] PALETTE = {
            0xFFFFFF, 0xAAAAAA, 0x555555, 0x000000,
            0xFF5555, 0xFFAA00, 0xFFFF55, 0x55FF55,
            0x55FFFF, 0x5555FF, 0xFF55FF, 0xAA0000
    };

    // 核心变量
    private int r, g, b, a;

    // 组件引用
    private ExtendedSlider rSlider, gSlider, bSlider, aSlider;
    private EditBox rBox, gBox, bBox, aBox;

    // 防止循环触发的锁
    private boolean isUpdating = false;

    public CapsColorScreen(Screen lastScreen) {
        super(Component.literal("color_title"));
        this.lastScreen = lastScreen;
        // 初始化读取配置
        this.r = ClientConfig.COLOR_R.get();
        this.g = ClientConfig.COLOR_G.get();
        this.b = ClientConfig.COLOR_B.get();
        this.a = ClientConfig.COLOR_A.get();
    }

    @Override
    protected void init() {
        int midX = this.width / 2;
        // 整体上移：从 Y=30 开始布局
        int startY = 30;

        // --- 1. ARGB Sliders & Boxes ---
        // 依次排列 R, G, B, A 四行

        // Red
        rSlider = createSlider(midX - 100, startY, "R: ", r, val -> { r = val; updateBoxesFromSliders(); });
        rBox = createEditBox(midX + 60, startY, "R", r, val -> { r = val; updateSlidersFromBoxes(); });

        // Green
        gSlider = createSlider(midX - 100, startY + 25, "G: ", g, val -> { g = val; updateBoxesFromSliders(); });
        gBox = createEditBox(midX + 60, startY + 25, "G", g, val -> { g = val; updateSlidersFromBoxes(); });

        // Blue
        bSlider = createSlider(midX - 100, startY + 50, "B: ", b, val -> { b = val; updateBoxesFromSliders(); });
        bBox = createEditBox(midX + 60, startY + 50, "B", b, val -> { b = val; updateSlidersFromBoxes(); });

        // Alpha (新增)
        aSlider = createSlider(midX - 100, startY + 75, "A: ", a, val -> { a = val; updateBoxesFromSliders(); });
        aBox = createEditBox(midX + 60, startY + 75, "A", a, val -> { a = val; updateSlidersFromBoxes(); });

        // 添加 ARGB 组件到屏幕
        this.addRenderableWidget(rSlider); this.addRenderableWidget(rBox);
        this.addRenderableWidget(gSlider); this.addRenderableWidget(gBox);
        this.addRenderableWidget(bSlider); this.addRenderableWidget(bBox);
        this.addRenderableWidget(aSlider); this.addRenderableWidget(aBox);

        // --- 2. Palette (调色板) ---
        // 紧接着 Alpha 下方
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
                this.a = ClientConfig.COLOR_A.getDefault(); // 调色板默认不透明
                refreshAllWidgets();
            }).bounds(colX, colY, 20, 20).build();

            this.addRenderableWidget(btn);
        }

        // --- 3. Standard Buttons (底部按钮栏) ---
        // 计算按钮行的 Y 坐标：调色板下方 60px 处
        int btnY = pY + 60;
        int btnWidth = 60;

        // 布局： [Cancel]  [Reset]  [Done]
        // 总宽度 = 60*3 + 10*2 = 200，刚好居中

        // Cancel (取消) - 直接返回上一屏，不保存
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, b -> {
            this.minecraft.setScreen(lastScreen);
        }).bounds(midX - 100, btnY, btnWidth, 20).build());

        // Reset (重置) - 重置为默认白色
        this.addRenderableWidget(Button.builder(Component.translatable("controls.reset"), b -> {
            this.r = ClientConfig.COLOR_R.getDefault();
            this.g = ClientConfig.COLOR_G.getDefault();
            this.b = ClientConfig.COLOR_B.getDefault();
            this.a = ClientConfig.COLOR_A.getDefault();
            refreshAllWidgets();
        }).bounds(midX - 30, btnY, btnWidth, 20).build());

        // Done (完成) - 保存并退出
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> {
            saveChanges();
            this.minecraft.setScreen(lastScreen);
        }).bounds(midX + 40, btnY, btnWidth, 20).build());
    }

    // --- 辅助构建方法 (减少重复代码) ---

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

    // --- 逻辑刷新方法 ---

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
        } catch (NumberFormatException ignored) {}
    }

    private void saveChanges() {
        ClientConfig.COLOR_R.set(r);
        ClientConfig.COLOR_G.set(g);
        ClientConfig.COLOR_B.set(b);
        ClientConfig.COLOR_A.set(a); // 保存 Alpha
        ClientConfig.SPEC.save();
    }

    @Override
    public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        // 绘制标题 (稍微上移)
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);

        // 计算坐标 (必须与 init 保持一致)
        int midX = this.width / 2;
        int startY = 30;
        // 预览框位置 (在滑块右侧)
        // 从 R 滑块(startY) 到 A 滑块(startY+75) 覆盖的高度

        // 组合 ARGB 颜色
        int previewColor = (a << 24) | (r << 16) | (g << 8) | b;

        // 绘制预览框 (位置在右侧空地)
        // X: midX + 110, Y: startY + 15 (居中对齐RGB区域)
        graphics.fill(midX + 110, startY + 20, midX + 150, startY + 60, previewColor);
        graphics.drawCenteredString(this.font, "Preview", midX + 130, startY + 65, 0xFFFFFF);

        // 绘制调色板色块
        int pX = midX - 100;
        int pY = startY + 110;
        for (int i = 0; i < PALETTE.length; i++) {
            int colX = pX + (i % 6) * 25;
            int colY = pY + (i / 6) * 25;
            // 调色板显示为不透明
            int color = PALETTE[i] | 0xFF000000;
            graphics.fill(colX + 2, colY + 2, colX + 18, colY + 18, color);
        }
    }

    @Override
    public void onClose() {
        // 按 ESC 时的默认行为：视为“取消”，不保存直接退出
        this.minecraft.setScreen(lastScreen);
    }
}