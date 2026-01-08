package top.wmsnp.caps.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class VeinFaceRenderer implements VeinRenderer {
    @Override
    public void render(List<BlockPos> blocks, PoseStack poseStack, Vec3 cameraPos, MultiBufferSource bufferSource) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        VertexConsumer builder = new ColorWrapper(bufferSource.getBuffer(RenderTypes.debugFilledBox()), getColor());
        for (BlockPos pos : blocks) {
            BlockState state = level.getBlockState(pos);
            poseStack.pushPose();
            poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);
            dispatcher.renderBatched(state, pos, level, poseStack, (l) -> builder, true, dispatcher.getBlockModelShaper().getBlockModel(state).collectParts(level, pos, state, level.random));
            poseStack.popPose();
        }
    }
}
