package top.wmsnp.caps.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class VeinFaceRenderer implements IVeinRenderer {
    @Override
    public void render(List<BlockPos> blocks, PoseStack poseStack) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        BlockStateModelSet modelSet = mc.getModelManager().getBlockStateModelSet();
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().position();
        ModelBlockRenderer renderer = new ModelBlockRenderer(true, false, mc.getBlockColors());
        VertexConsumer builder = new ColorWrapper(mc.renderBuffers().bufferSource().getBuffer(RENDER_TYPE), getColor());
        BlockQuadOutput output = (x, y, z, quad, instance) -> {
            poseStack.pushPose();
            poseStack.translate(x, y, z);
            builder.putBakedQuad(poseStack.last(), quad, instance);
            poseStack.popPose();
        };
        for (BlockPos pos : blocks) {
            BlockState state = mc.level.getBlockState(pos);
            poseStack.pushPose();
            poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);
            renderer.tesselateBlock(output, 0.0F, 0.0F, 0.0F, mc.level, pos, state, modelSet.get(state), state.getSeed(pos));
            poseStack.popPose();
        }
    }
}
