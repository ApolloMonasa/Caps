package com.apollo.caps.client;

import com.apollo.caps.network.VeinMinePacket;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = "caps", value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (ModKeyBindings.VEIN_MINE.isDown()) VeinMinePacket.send(event.getPos(), event.getState());
    }
}