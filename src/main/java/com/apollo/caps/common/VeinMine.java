package com.apollo.caps.common;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class VeinMine {
    private static final int MAX_BLOCKS = 64;
    private static final float EXHAUSTION_PER_BLOCK = 0.025F;
    private static final int[] OFFSETS = {-1, 0, 1};

    public static void veinMine(ServerPlayer player, BlockPos startPos, BlockState state) {
        ServerLevel level = player.level();
        Block targetBlock = state.getBlock();
        if (!player.hasCorrectToolForDrops(state, level, startPos)) return;

        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(startPos);
        visited.add(startPos);

        int count = 0;
        while(!queue.isEmpty() && count < MAX_BLOCKS) {
            BlockPos currentPos = queue.poll();
            for (int dx : OFFSETS) for (int dy : OFFSETS) for (int dz : OFFSETS) {
                BlockPos tgtPos = currentPos.offset(dx, dy, dz);
                if (visited.contains(tgtPos) || !level.getBlockState(tgtPos).is(targetBlock)) continue;
                visited.add(tgtPos);
                queue.add(tgtPos);
                level.destroyBlock(tgtPos, !player.isCreative(), player);
                player.getMainHandItem().hurtAndBreak(1, level, player, (brokenItem) -> level.broadcastEntityEvent(player, (byte)47));
                if (!player.isCreative()) player.causeFoodExhaustion(EXHAUSTION_PER_BLOCK);
                count++;
            }
        }
    }
}
