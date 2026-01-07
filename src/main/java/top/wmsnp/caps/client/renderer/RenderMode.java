package top.wmsnp.caps.client.renderer;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.jspecify.annotations.NonNull;

public enum RenderMode implements TranslatableEnum {
    NONE(null),
    OUTLINE(new VeinLineRenderer()),
    FACE(new VeinFaceRenderer());

    private final VeinRenderer renderer;

    RenderMode(VeinRenderer renderer) { this.renderer = renderer; }

    public VeinRenderer getRenderer() { return renderer; }

    @Override
    public @NonNull Component getTranslatedName() {
        return Component.translatable("caps.configuration.render_mode." + this.name());
    }
}