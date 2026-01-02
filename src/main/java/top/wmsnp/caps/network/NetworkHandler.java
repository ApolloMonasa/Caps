package top.wmsnp.caps.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = "caps")
public class NetworkHandler {
    @SubscribeEvent
    public static void onRegisterPayload(RegisterPayloadHandlersEvent event) {
        event.registrar("1.0").playToServer(VeinMinePayload.TYPE, VeinMinePayload.CODEC, VeinMinePayload::handle);
    }
}