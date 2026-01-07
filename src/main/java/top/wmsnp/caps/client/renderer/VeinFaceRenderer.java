package top.wmsnp.caps.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import top.wmsnp.caps.common.CapsConfig;

import java.util.List;

public class VeinFaceRenderer {
    private static final Vec3 EPS = new Vec3(0.002, 0.002, 0.002);

    private static final int[][] FACES = {
            {0, 4, 6, 2},
            {1, 3, 7, 5},
            {0, 1, 5, 4},
            {2, 6, 7, 3},
            {0, 2, 3, 1},
            {4, 5, 7, 6}
    };

    private static final Vec3[] NORMALS = {
            new Vec3(-1, 0, 0), new Vec3(1, 0, 0),
            new Vec3(0, -1, 0), new Vec3(0, 1, 0),
            new Vec3(0, 0, -1), new Vec3(0, 0, 1)
    };

    public void render(List<BlockPos> blocks, PoseStack poseStack, Vec3 cameraPos, MultiBufferSource bufferSource) {
        VertexConsumer builder = bufferSource.getBuffer(RenderTypes.lightning());

        int r = CapsConfig.COLOR_R.get();
        int g = CapsConfig.COLOR_G.get();
        int b = CapsConfig.COLOR_B.get();
        int a = CapsConfig.COLOR_A.get();

        for (BlockPos pos : blocks) {
            poseStack.pushPose();
            poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);
            float eps = 0.002f;
            poseStack.translate(-eps, -eps, -eps);
            poseStack.scale(1 + eps * 2, 1 + eps * 2, 1 + eps * 2);

            for (int f = 0; f < FACES.length; f++) {
                Vec3 normal = NORMALS[f];
                for (int idx : FACES[f]) {
                    float vx = (idx & 1);
                    float vy = ((idx >> 1) & 1);
                    float vz = ((idx >> 2) & 1);

                    builder.addVertex(poseStack.last().pose(), vx, vy, vz)
                            .setColor(r, g, b, a)
                            .setNormal((float) normal.x, (float) normal.y, (float) normal.z);
                }
            }
            poseStack.popPose();
        }
    }
}
