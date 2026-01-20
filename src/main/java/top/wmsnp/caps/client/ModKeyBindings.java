package top.wmsnp.caps.client;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static final KeyMapping VEIN_MINE = new KeyMapping("key.caps.vein_mine", GLFW.GLFW_KEY_GRAVE_ACCENT, "key.category.caps.default");

    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(VEIN_MINE);
    }
}
