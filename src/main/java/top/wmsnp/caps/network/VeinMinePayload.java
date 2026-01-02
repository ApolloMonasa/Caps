package top.wmsnp.caps.network;

import top.wmsnp.caps.Caps;
import top.wmsnp.caps.common.VeinMine;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.NonNull;

public record VeinMinePayload(BlockPos pos, BlockState state) implements CustomPacketPayload {
    public static final Type<@NonNull VeinMinePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Caps.MODID, "vein_mine_payload"));
    public static final StreamCodec<FriendlyByteBuf, VeinMinePayload> CODEC = CustomPacketPayload.codec(VeinMinePayload::toBuf, VeinMinePayload::fromBuf);

    public static void send(BlockPos targetPos, BlockState state) {
        if (Minecraft.getInstance().player == null) return;
        VeinMinePayload packet = new VeinMinePayload(targetPos, state);
        Minecraft.getInstance().player.connection.send(packet.toVanillaServerbound());
    }

    public static void handle(VeinMinePayload payload, IPayloadContext context) {
        Player player = context.player();
        if (!(player instanceof ServerPlayer)) return;
        context.enqueueWork(() -> VeinMine.veinMine((ServerPlayer) player, payload.pos(), payload.state()));
    }

    private static void toBuf(VeinMinePayload packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos());
        buf.writeVarInt(Block.getId(packet.state()));
    }

    private static VeinMinePayload fromBuf(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockState state = Block.stateById(buf.readVarInt()); // 读取 BlockState ID
        return new VeinMinePayload(pos, state);
    }

    @Override
    public @NonNull Type<@NonNull VeinMinePayload> type() {
        return TYPE;
    }
}
