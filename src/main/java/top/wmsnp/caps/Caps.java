package top.wmsnp.caps;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import top.wmsnp.caps.common.ClientConfig;
import top.wmsnp.caps.common.ServerConfig;
import top.wmsnp.caps.network.NetworkHandler;

@Mod("caps")
public class Caps {
    public static final String MODID = "caps";
    public static final int MAX_MAX_VEIN_BLOCKS = 1024;

    public Caps(ModContainer container, IEventBus modBus) {
        container.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        modBus.addListener(NetworkHandler::onRegisterPayload);
    }
}
