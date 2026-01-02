package com.apollo.caps.network;

import com.apollo.caps.common.VeinMine;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = "caps")
public class NetworkHandler {
    @SubscribeEvent
    public static void onRegisterPayload(RegisterPayloadHandlersEvent event) {
        event.registrar("1.0").playToServer(VeinMinePacket.TYPE, VeinMinePacket.CODEC, (packet, context) -> {
            Player player = context.player();
            if (!(player instanceof ServerPlayer)) return;
            context.enqueueWork(() -> VeinMine.veinMine((ServerPlayer) player, packet.pos(), packet.state()));
        });
    }
}