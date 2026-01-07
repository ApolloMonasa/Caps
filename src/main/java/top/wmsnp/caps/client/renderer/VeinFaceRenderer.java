package top.wmsnp.caps.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class VeinFaceRenderer implements VeinRenderer {
    private static final int[][] FACES = {
            {0, 4, 6, 2}, {1, 3, 7, 5}, {0, 1, 5, 4},
            {2, 6, 7, 3}, {0, 2, 3, 1}, {4, 5, 7, 6}
    };
    private static final Vector3f[] NORMALS = {
            new Vector3f(-1, 0, 0), new Vector3f(1, 0, 0), new Vector3f(0, -1, 0),
            new Vector3f(0, 1, 0), new Vector3f(0, 0, -1), new Vector3f(0, 0, 1)
    };

    @Override
    public void render(List<BlockPos> blocks, PoseStack poseStack, Vec3 cameraPos, MultiBufferSource bufferSource) {
        VertexConsumer builder = bufferSource.getBuffer(RenderTypes.lightning());
        int color = getColor();
        for (BlockPos pos : blocks) {
            poseStack.pushPose();
            poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);
            poseStack.translate(-EPS, -EPS, -EPS);
            poseStack.scale(1 + EPS * 2, 1 + EPS * 2, 1 + EPS * 2);
            for (int f = 0; f < FACES.length; f++) for (int idx : FACES[f]) addVertex(builder, poseStack, CORNERS.get(idx), color, NORMALS[f]);
            poseStack.popPose();
        }
    }
}
