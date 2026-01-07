package top.wmsnp.caps.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import top.wmsnp.caps.common.CapsConfig;

import java.util.*;

public class VeinLineRenderer implements VeinRenderer {
    public static final int[][] LINES = new int[][]{
            {0,1},{1,3},{3,2},{2,0},
            {4,5},{5,7},{7,6},{6,4},
            {0,4},{1,5},{2,6},{3,7}
    };
    public static final Vector3f NORMAL = new Vector3f(0f, 1f, 0f);

    public void render(List<BlockPos> blocks, PoseStack poseStack, Vec3 cameraPos, MultiBufferSource bufferSource) {
        VertexConsumer builder = bufferSource.getBuffer(RenderTypes.lines());
        int color = getColor();
        float thickness = CapsConfig.LINEWIDTH.get().floatValue();

        for (Edge edge : getEdges(blocks)) {
            Vec3 a = new Vec3(edge.a.getX(), edge.a.getY(), edge.a.getZ()).add(EPS).subtract(cameraPos);
            Vec3 b = new Vec3(edge.b.getX(), edge.b.getY(), edge.b.getZ()).add(EPS).subtract(cameraPos);
            addVertex(builder, poseStack, a, color, NORMAL).setLineWidth(thickness);
            addVertex(builder, poseStack, b, color, NORMAL).setLineWidth(thickness);
        }
    }

    private Set<Edge> getEdges(List<BlockPos> blocks) {
        Set<Edge> edgeSet = new HashSet<>();
        for (BlockPos pos : blocks) for (int[] line : LINES) {
            Vec3 cornerA = CORNERS.get(line[0]);
            Vec3 cornerB = CORNERS.get(line[1]);
            BlockPos a = pos.offset((int)cornerA.x, (int)cornerA.y, (int)cornerA.z);
            BlockPos b = pos.offset((int)cornerB.x, (int)cornerB.y, (int)cornerB.z);
            Edge edge = new Edge(a, b);
            if (!edgeSet.add(edge)) edgeSet.remove(edge);
        }
        return edgeSet;
    }

    private record Edge(BlockPos a, BlockPos b) {
        public Edge {
            if (a.asLong() > b.asLong()) {
                BlockPos temp = a;
                a = b;
                b = temp;
            }
        }
    }
}
