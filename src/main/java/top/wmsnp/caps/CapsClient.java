package top.wmsnp.caps;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import top.wmsnp.caps.client.ModKeyBindings;
import top.wmsnp.caps.client.gui.ConfigSectionScreen;

@Mod(value = "caps", dist = Dist.CLIENT)
public class CapsClient {
    public CapsClient(FMLModContainer container, IEventBus modBus) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> new ConfigurationScreen(modContainer, parent, ConfigSectionScreen::new));
        modBus.addListener(ModKeyBindings::registerBindings);
    }
}
