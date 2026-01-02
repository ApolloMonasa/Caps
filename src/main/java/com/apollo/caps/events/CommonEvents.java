package com.apollo.caps.events;

import com.apollo.caps.Caps;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

@EventBusSubscriber(modid = Caps.MODID)
public class CommonEvents {

    // 最大连锁数量
    private static final int MAX_BLOCKS = 64;
    // 每个方块消耗的饥饿度
    private static final float EXHAUSTION_PER_BLOCK = 0.025F;
    // 【重要】防止递归死循环的开关
    private static boolean isMining = false;

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        // 1. 基础检查
        if (event.getLevel().isClientSide()) return;

        // 如果正在连锁中，立刻停止，防止死循环
        if (isMining) return;

        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        // 【新增功能】只有按住 Shift (潜行) 才会触发
        if (!player.isShiftKeyDown()) {
            return;
        }

        Level level = (Level) event.getLevel();
        BlockPos startPos = event.getPos();
        BlockState state = event.getState();
        Block targetBlock = state.getBlock();
        ItemStack tool = player.getMainHandItem();

        // 2. 【工具逻辑】安全检查
        boolean isValidTool = false;
        float destroySpeed = tool.getDestroySpeed(state);

        if (player.isCreative()) {
            // 创造模式：只有拿着能干活的工具(效率>1.0)才连锁
            if (destroySpeed > 1.0f) {
                isValidTool = true;
            }
        } else {
            // 生存模式：必须能掉落物品 且 工具效率高
            if (player.hasCorrectToolForDrops(state) && destroySpeed > 1.0f) {
                isValidTool = true;
            }
        }

        if (!isValidTool) return;

        // 3. 执行连锁 (包裹在 try-finally 中确保状态重置)
        if (level instanceof ServerLevel serverLevel) {
            isMining = true; // 开启防递归锁
            try {
                veinMine(serverLevel, startPos, targetBlock, player);
            } finally {
                isMining = false; // 无论如何，结束后解锁
            }
        }
    }

    private static void veinMine(ServerLevel level, BlockPos startPos, Block targetBlock, ServerPlayer player) {
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(startPos);
        visited.add(startPos);

        int count = 0;
        ItemStack mainHandItem = player.getMainHandItem();

        while (!queue.isEmpty() && count < MAX_BLOCKS) {
            // 【安全检查】如果工具爆了，立刻停止
            if (mainHandItem.isEmpty() && !player.isCreative()) {
                break;
            }

            BlockPos currentPos = queue.poll();

            // 搜索 3x3x3 范围
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;

                        BlockPos neighborPos = currentPos.offset(dx, dy, dz);

                        // 防止重复处理同一个方块
                        if (visited.contains(neighborPos)) continue;

                        BlockState neighborState = level.getBlockState(neighborPos);

                        // 简单的同类方块判定
                        if (neighborState.is(targetBlock)) {
                            visited.add(neighborPos);
                            queue.add(neighborPos);

                            // 注意：startPos 已经被玩家敲掉了，所以不需要 destroyBlock
                            if (!neighborPos.equals(startPos)) {
                                // 1. 破坏方块 (true 表示掉落物品)
                                level.destroyBlock(neighborPos, true, player);
                                count++;

                                // 2. 扣除耐久
                                if (!player.isCreative() && mainHandItem.isDamageableItem()) {
                                    // 这里的 broadcastEntityEvent((byte)47) 是为了让客户端播放工具破碎声音
                                    mainHandItem.hurtAndBreak(1, level, player, (brokenItem) -> {
                                        level.broadcastEntityEvent(player, (byte) 47);
                                    });
                                }

                                // 3. 扣除饥饿度
                                if (!player.isCreative()) {
                                    player.causeFoodExhaustion(EXHAUSTION_PER_BLOCK);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}