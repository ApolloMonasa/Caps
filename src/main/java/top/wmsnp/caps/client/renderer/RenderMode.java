package top.wmsnp.caps.client.renderer;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.jspecify.annotations.NonNull;

public enum RenderMode implements TranslatableEnum {
    NONE,
    OUTLINE,
    FACE;

    @Override
    public @NonNull Component getTranslatedName() {
        return Component.translatable("caps.configuration.render_mode." + this.name());
    }
}