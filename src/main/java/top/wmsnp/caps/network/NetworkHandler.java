package top.wmsnp.caps.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class NetworkHandler {
    public static void onRegisterPayload(RegisterPayloadHandlersEvent event) {
        event.registrar("1.0").playToServer(VeinMinePayload.TYPE, VeinMinePayload.CODEC, VeinMinePayload::handle);
    }
}