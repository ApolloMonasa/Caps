package top.wmsnp.caps.common;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class VeinMine {
    private static final int MAX_BLOCKS = 64;
    private static final float EXHAUSTION_PER_BLOCK = 0.025F;
    private static final int[] OFFSETS = {-1, 0, 1};

    public static class VeinMineResult {
        public List<ItemStack> drops = new ArrayList<>();
        public final List<BlockPos> poss = new ArrayList<>();
        public int xp = 0;
    }

    public static VeinMineResult collectVeinBlocks(Player player, BlockPos startPos, BlockState state) {
        Level level = player.level();
        Block targetBlock = state.getBlock();
        VeinMineResult result = new VeinMineResult();
        if (state.isAir() || state.getFluidState().isSource()) return result;

        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(startPos);
        visited.add(startPos);

        int count = 0;
        outer:
        while (!queue.isEmpty() && count < MAX_BLOCKS) {
            BlockPos currentPos = queue.poll();
            for (int dx : OFFSETS) for (int dy : OFFSETS) for (int dz : OFFSETS) {
                BlockPos tgtPos = currentPos.offset(dx, dy, dz);
                BlockState tgtState = level.getBlockState(tgtPos);
                BlockEntity tgtEntity = level.getBlockEntity(tgtPos);
                if (!player.hasCorrectToolForDrops(tgtState, level, tgtPos)) break outer;
                if (visited.contains(tgtPos) || !tgtState.is(targetBlock)) continue;
                visited.add(tgtPos);
                queue.add(tgtPos);
                result.poss.add(tgtPos);
                ItemStack tool = player.getMainHandItem();
                count++;
                if (!(level instanceof ServerLevel)) continue;
                result.drops.addAll(Block.getDrops(tgtState, (ServerLevel) level, tgtPos, tgtEntity, player, tool));
                result.xp += tgtState.getExpDrop(level, tgtPos, tgtEntity, player, tool);
            }
        }
        return result;
    }

    public static void veinMine(ServerPlayer player, BlockPos startPos, BlockState state) {
        ServerLevel level = player.level();
        VeinMineResult result = collectVeinBlocks(player, startPos, state);
        ItemStack tool = player.getMainHandItem();
        result.poss.forEach(pos -> {
            level.destroyBlock(pos, false, player);
            tool.hurtAndBreak(1, level, player, (brokenItem) -> level.broadcastEntityEvent(player, (byte) 47));
        });
        if (player.isCreative()) return;
        result.drops.forEach(drop -> Block.popResource(level, startPos, drop));
        ExperienceOrb.award(level, startPos.getCenter(), result.xp);
        player.causeFoodExhaustion(EXHAUSTION_PER_BLOCK * result.poss.size());
    }
}