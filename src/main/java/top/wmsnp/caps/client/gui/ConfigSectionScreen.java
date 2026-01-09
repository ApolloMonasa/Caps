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

import java.util.Map;
import java.util.Set;


public class ConfigSectionScreen extends ConfigurationScreen.ConfigurationSectionScreen {
    public ConfigSectionScreen(Screen parent, ModConfig.Type type, ModConfig modConfig, Component title) {
        super(parent, type, modConfig, title);
    }
    public ConfigSectionScreen(Context parentContext, Screen parent, Map<String, Object> valueSpecs, String key, Set<? extends UnmodifiableConfig.Entry> entrySet, Component title) {
        super(parentContext, parent, valueSpecs, key, entrySet, title);
    }

    @Override
    protected @Nullable Element createSection(@NonNull String key, @NonNull UnmodifiableConfig subconfig, @NonNull UnmodifiableConfig subsection) {
        if (subconfig.isEmpty()) return null;
        Button button = Button.builder(Component.translatable("neoforge.configuration.uitext.sectiontext"), (btn) -> this.minecraft.setScreen(route(key, subconfig, subsection))).tooltip(Tooltip.create(this.getTooltipComponent(key, null))).width(150).build();
        return new Element(Component.translatable("neoforge.configuration.uitext.section", this.getTranslationComponent(key)), this.getTooltipComponent(key, null), button, false);
    }

    private Screen route(String key, UnmodifiableConfig subconfig, UnmodifiableConfig subsection){
        if (key.equals("color")) return new CapsColorScreen(this);
        return this.sectionCache.computeIfAbsent(key, (k) -> new ConfigSectionScreen(this.context, this, subconfig.valueMap(), key, subsection.entrySet(), Component.translatable(getTranslationKey(key))).rebuild());
    }
}
