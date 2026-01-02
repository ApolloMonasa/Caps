package com.apollo.caps.common;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class VeinMine {
    private static final int MAX_BLOCKS = 64;
    private static final float EXHAUSTION_PER_BLOCK = 0.025F;
    private static final int[] OFFSETS = {-1, 0, 1};

    public static void veinMine(ServerPlayer player, BlockPos startPos, BlockState state) {
        ServerLevel level = player.level();
        Block targetBlock = state.getBlock();

        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(startPos);
        visited.add(startPos);

        List<ItemStack> drops = new ArrayList<>();
        int xp = 0;

        int count = 0;
        outer:
        while(!queue.isEmpty() && count < MAX_BLOCKS) {
            BlockPos currentPos = queue.poll();
            for (int dx : OFFSETS) for (int dy : OFFSETS) for (int dz : OFFSETS) {
                BlockPos tgtPos = currentPos.offset(dx, dy, dz);
                BlockState tgtState = level.getBlockState(tgtPos);
                BlockEntity tgtEntity = level.getBlockEntity(tgtPos);
                if (!player.hasCorrectToolForDrops(tgtState, level, tgtPos)) break outer;
                if (visited.contains(tgtPos) || !tgtState.is(targetBlock)) continue;
                visited.add(tgtPos);
                queue.add(tgtPos);
                ItemStack tool = player.getMainHandItem();
                drops.addAll(Block.getDrops(tgtState, level, tgtPos, tgtEntity, player, tool));
                xp += tgtState.getExpDrop(level, tgtPos, tgtEntity, player, tool);
                level.destroyBlock(tgtPos, false, player);
                player.getMainHandItem().hurtAndBreak(1, level, player, (brokenItem) -> level.broadcastEntityEvent(player, (byte)47));
                count++;
            }
        }
        if (player.isCreative()) return;
        drops.forEach(drop -> Block.popResource(level, startPos, drop));
        ExperienceOrb.award(level, startPos.getCenter(), xp);
        player.causeFoodExhaustion(EXHAUSTION_PER_BLOCK);
    }
}
