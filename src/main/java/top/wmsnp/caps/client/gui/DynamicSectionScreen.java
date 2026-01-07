package top.wmsnp.caps.client.gui;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;


public class DynamicSectionScreen extends ConfigurationScreen.ConfigurationSectionScreen {
    public DynamicSectionScreen(Screen parent, ModConfig.Type type, ModConfig modConfig, Component title) {
        super(parent, type, modConfig, title);
    }

    @Override
    protected @Nullable Element createSection(String key, @NonNull UnmodifiableConfig subconfig, @NonNull UnmodifiableConfig subsection) {
        if (key.equals("visuals")) {
            Button btn = Button.builder(Component.translatable("neoforge.configuration.uitext.sectiontext"), (button) -> this.minecraft.setScreen(new CapsColorScreen(this)))
                    .tooltip(Tooltip.create(this.getTooltipComponent(key, null)))
                    .build();
            return new Element(Component.translatable("neoforge.configuration.uitext.section", this.getTranslationComponent(key)), this.getTooltipComponent(key, null), btn, false);
        }
        return super.createSection(key, subconfig, subsection);
    }
}
