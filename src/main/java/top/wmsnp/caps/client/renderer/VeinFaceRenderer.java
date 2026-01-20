package top.wmsnp.caps.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class VeinFaceRenderer implements IVeinRenderer {
    @Override
    public void render(List<BlockPos> blocks, PoseStack poseStack) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        VertexConsumer builder = new ColorWrapper(mc.renderBuffers().bufferSource().getBuffer(RENDER_TYPE), getColor());
        for (BlockPos pos : blocks) {
            BlockState state = mc.level.getBlockState(pos);
            poseStack.pushPose();
            poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);
            dispatcher.renderBatched(state, pos, mc.level, poseStack, (type) -> builder, true, dispatcher.getBlockModelShaper().getBlockModel(state).collectParts(mc.level, pos, state, mc.level.random));
            poseStack.popPose();
        }
    }
}
