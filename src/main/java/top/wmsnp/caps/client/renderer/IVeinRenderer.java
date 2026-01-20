package top.wmsnp.caps.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import top.wmsnp.caps.common.ClientConfig;

import java.util.List;

public interface IVeinRenderer {
    RenderType RENDER_TYPE = RenderType.create("color", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 1536, false, true,RenderType.CompositeState.builder().setShaderState(RenderStateShard.POSITION_COLOR_SHADER).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING).setCullState(RenderStateShard.NO_CULL).createCompositeState(false));
    void render(List<BlockPos> poss, PoseStack poseStack);

    default int getColor() {
        return (ClientConfig.COLOR_A.get() & 0xFF) << 24 | (ClientConfig.COLOR_R.get() & 0xFF) << 16|
                (ClientConfig.COLOR_G.get() & 0xFF) << 8 | (ClientConfig.COLOR_B.get() & 0xFF);
    }

    default double getLinewidth() {
        return ClientConfig.LINEWIDTH.get();
    }
}