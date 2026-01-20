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
    private static final float EXHAUSTION_PER_BLOCK = 0.025F;
    private static final int[] OFFSETS = {-1, 0, 1};

    public static class VeinMineResult {
        public final BlockPos startPos;
        public List<ItemStack> drops = new ArrayList<>();
        public final List<BlockPos> poss = new ArrayList<>();
        public int xp = 0;

        public VeinMineResult(BlockPos startPos){
            super();
            this.startPos = startPos;
        }
    }

    public static VeinMineResult collect(Player player, BlockPos startPos, BlockState state, int max, boolean isBreak) {
        Level level = player.level();
        Block targetBlock = state.getBlock();
        VeinMineResult result = new VeinMineResult(startPos);
        if (state.isAir() || state.getFluidState().isSource() || !(player.hasCorrectToolForDrops(state, level, startPos) || player.isCreative())) return result;

        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(startPos);
        visited.add(startPos);

        int count = 0;
        outer:
        while (!queue.isEmpty() && count < max - 1) {
            BlockPos currentPos = queue.poll();
            for (int dx : OFFSETS) for (int dy : OFFSETS) for (int dz : OFFSETS) {
                if (count >= max - 1) break outer;
                BlockPos tgtPos = currentPos.offset(dx, dy, dz);
                BlockState tgtState = level.getBlockState(tgtPos);
                BlockEntity tgtEntity = level.getBlockEntity(tgtPos);
                if (visited.contains(tgtPos) || !tgtState.is(targetBlock)) continue;
                if (!player.isCreative() && !player.hasCorrectToolForDrops(tgtState, level, tgtPos)) break outer;
                visited.add(tgtPos);
                queue.add(tgtPos);
                result.poss.add(tgtPos);
                ItemStack tool = player.getMainHandItem();
                count++;
                if (isBreak && level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
                    level.destroyBlock(tgtPos, false, player);
                    tool.hurtAndBreak(1, serverLevel, serverPlayer, (brokenItem) -> level.broadcastEntityEvent(player, (byte) 47));
                    result.drops.addAll(Block.getDrops(tgtState, serverLevel, tgtPos, tgtEntity, player, tool));
                    result.xp += tgtState.getExpDrop(level, tgtPos, tgtEntity, player, tool);
                    player.causeFoodExhaustion(EXHAUSTION_PER_BLOCK);
                }
            }
        }
        result.poss.add(startPos);
        return result;
    }

    public static void veinMine(ServerPlayer player, BlockPos startPos, BlockState state) {
        Level level = player.level();
        if (!(level instanceof ServerLevel)) return;
        int min = Math.min(player.getPersistentData().getInt("vein_mine_max"),  ServerConfig.SERVER_MAX_VEIN_BLOCKS.get());
        VeinMineResult result = collect(player, startPos, state, min, true);
        if (player.isCreative()) return;
        ExperienceOrb.award((ServerLevel) level, startPos.getCenter(), result.xp);
        result.drops.forEach(drop -> Block.popResource(level, startPos, drop));
    }
}