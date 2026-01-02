package top.wmsnp.caps.server;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import top.wmsnp.caps.common.VeinMine;

@EventBusSubscriber(modid = "caps")
public class ServerEvents {
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if(!(player instanceof ServerPlayer && player.getPersistentData().getBoolean("vein_mine_active").orElse(false))) return;
        VeinMine.veinMine((ServerPlayer) player, event.getPos(), event.getState());
    }
}
