package top.wmsnp.caps.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import top.wmsnp.caps.client.ClientEvents;
import top.wmsnp.caps.common.ClientConfig;

import java.util.List;

public interface IVeinRenderer {
    // 适配旧版 API：直接传入 bufferSize(1536), affectsCrumbling(false), sortOnUpload(true), Pipeline 和 CompositeState
    RenderType RENDER_TYPE = RenderType.create("color", RenderType.TRANSIENT_BUFFER_SIZE, false, true, ClientEvents.COLOR_PIPELINE, RenderType.CompositeState.builder().setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING).createCompositeState(false));

    void render(List<BlockPos> poss, PoseStack poseStack);

    default int getColor() {
        return (ClientConfig.COLOR_A.get() & 0xFF) << 24 | (ClientConfig.COLOR_R.get() & 0xFF) << 16|
                (ClientConfig.COLOR_G.get() & 0xFF) << 8 | (ClientConfig.COLOR_B.get() & 0xFF);
    }

    default double getLinewidth() {
        return ClientConfig.LINEWIDTH.get();
    }
}