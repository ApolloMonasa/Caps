package top.wmsnp.caps.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import top.wmsnp.caps.common.CapsConfig;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private EditBox maxVeinBox;

    public ConfigScreen(Screen parent) {
        super(Component.literal("模组配置界面"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.maxVeinBox = new EditBox(this.font, this.width / 2 - 100, 80, 200, 20, Component.literal("数字配置"));
        this.maxVeinBox.setValue(String.valueOf(CapsConfig.MAX_VEIN_BLOCKS.get()));
        this.maxVeinBox.setFilter(s -> s.isEmpty() || s.matches("\\d*"));
        this.addRenderableWidget(this.maxVeinBox);
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, this::onPress).bounds(this.width / 2 - 100, 120, 200, 20).build());
    }

    private void onPress(Button btn) {
        String val = this.maxVeinBox.getValue();
        int finalValue = val.isEmpty() ? 0 : Integer.parseInt(val);
        CapsConfig.MAX_VEIN_BLOCKS.set(finalValue);
        CapsConfig.COMMON.save();
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

//    public static register(ModContainer container){
//        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
//    }
}
