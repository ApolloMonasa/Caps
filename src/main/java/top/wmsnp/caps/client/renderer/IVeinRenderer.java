package top.wmsnp.caps.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import top.wmsnp.caps.client.ClientEvents;
import top.wmsnp.caps.common.ClientConfig;

import java.util.List;

public interface IVeinRenderer {
    RenderType RENDER_TYPE = RenderType.create("color", RenderSetup.builder(ClientEvents.COLOR_PIPELINE).sortOnUpload().setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup());
    void render(List<BlockPos> poss, PoseStack poseStack);
    default int getColor() {
        return (ClientConfig.COLOR_A.get() & 0xFF) << 24 | (ClientConfig.COLOR_R.get() & 0xFF) << 16|
                (ClientConfig.COLOR_G.get() & 0xFF) << 8 | (ClientConfig.COLOR_B.get() & 0xFF);
    }
    default double getLinewidth() {
        return ClientConfig.LINEWIDTH.get();
    }
}
