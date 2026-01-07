package top.wmsnp.caps.client;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import top.wmsnp.caps.Caps;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import top.wmsnp.caps.client.gui.ConfigSectionScreen;


@EventBusSubscriber(modid = Caps.MODID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ModContainer container = ModList.get().getModContainerById(Caps.MODID).orElseThrow();
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> new ConfigurationScreen(modContainer, parent, ConfigSectionScreen::new));
    }
}