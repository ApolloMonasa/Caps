package com.apollo.caps.network;

import com.apollo.caps.Caps;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public record VeinMinePacket(BlockPos pos, BlockState state) implements CustomPacketPayload {
    public static final Type<@NonNull VeinMinePacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Caps.MODID, "vein_mine_packet"));
    public static final StreamCodec<FriendlyByteBuf, VeinMinePacket> CODEC = CustomPacketPayload.codec(VeinMinePacket::toBuf, VeinMinePacket::fromBuf);
    @Override
    public @NonNull Type<@NonNull VeinMinePacket> type() {
        return TYPE;
    }

    private static void toBuf(VeinMinePacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos());
        buf.writeVarInt(Block.getId(packet.state()));
    }

    private static VeinMinePacket fromBuf(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockState state = Block.stateById(buf.readVarInt()); // 读取 BlockState ID
        return new VeinMinePacket(pos, state);
    }

    public static void send(BlockPos targetPos, BlockState state) {
        if (Minecraft.getInstance().player == null) return;
        VeinMinePacket packet = new VeinMinePacket(targetPos, state);
        Minecraft.getInstance().player.connection.send(packet.toVanillaServerbound());
    }
}
