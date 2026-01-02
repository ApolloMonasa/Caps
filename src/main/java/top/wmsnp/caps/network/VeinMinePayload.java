package top.wmsnp.caps.network;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.NonNull;

public record VeinMinePayload(boolean isPressed) implements CustomPacketPayload {
    public static final Type<@org.jetbrains.annotations.NotNull VeinMinePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("caps", "vein_mine_state"));
    public static final StreamCodec<FriendlyByteBuf, VeinMinePayload> CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, VeinMinePayload::isPressed, VeinMinePayload::new);

    @Override
    public @NonNull Type<@org.jetbrains.annotations.NotNull VeinMinePayload> type() { return TYPE; }

    public static void handle(VeinMinePayload payload, IPayloadContext context) {
        context.player().getPersistentData().putBoolean("vein_mine_active", payload.isPressed());
    }
}
