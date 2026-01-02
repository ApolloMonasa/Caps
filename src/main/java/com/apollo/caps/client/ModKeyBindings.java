package com.apollo.caps.client;

import com.apollo.caps.Caps;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber
public class ModKeyBindings {
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(Caps.MODID, "default"));
    public static final KeyMapping VEIN_MINE = new KeyMapping("key.caps.vein_mine", GLFW.GLFW_KEY_GRAVE_ACCENT, CATEGORY);

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);
        event.register(VEIN_MINE);
    }}
