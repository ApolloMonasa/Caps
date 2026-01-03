package top.wmsnp.caps.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class VeinHighlightRenderer {
    public static final List<BlockPos> blocks = new ArrayList<>();
    public static final int[][] LINES = new int[][]{
            {0,1},{1,3},{3,2},{2,0},
            {4,5},{5,7},{7,6},{6,4},
            {0,4},{1,5},{2,6},{3,7}
    };

    public void render(PoseStack poseStack, CameraRenderState cameraState, MultiBufferSource bufferSource) {
        VertexConsumer builder = bufferSource.getBuffer(RenderTypes.lines());

        for (Edge edge : getEdgesToRender()) {
            Vec3 a = new Vec3(edge.a.getX(), edge.a.getY(), edge.a.getZ()).subtract(cameraState.pos);
            Vec3 b = new Vec3(edge.b.getX(), edge.b.getY(), edge.b.getZ()).subtract(cameraState.pos);

            builder.addVertex(poseStack.last().pose(), (float)a.x(), (float)a.y(), (float)a.z())
                    .setColor(255, 255, 255, 255)
                    .setNormal(0f, 1f, 0f)
                    .setLineWidth(4f);

            builder.addVertex(poseStack.last().pose(), (float)b.x(), (float)b.y(), (float)b.z())
                    .setColor(255, 255, 255, 255)
                    .setNormal(0f, 1f, 0f)
                    .setLineWidth(4f);
        }
    }

    private Set<Edge> getEdgesToRender() {
        Set<Edge> edgeSet = new HashSet<>();

        for (BlockPos pos : VeinHighlightRenderer.blocks) {
            Vec3[] corners = IntStream.range(0, 8)
                    .mapToObj(i -> new Vec3(pos.getX() + (i & 1), pos.getY() + ((i >> 1) & 1), pos.getZ() + ((i >> 2) & 1)))
                    .toArray(Vec3[]::new);

            for (int[] line : LINES) {
                BlockPos a = new BlockPos((int)corners[line[0]].x(), (int)corners[line[0]].y(), (int)corners[line[0]].z());
                BlockPos b = new BlockPos((int)corners[line[1]].x(), (int)corners[line[1]].y(), (int)corners[line[1]].z());
                Edge edge = new Edge(a, b);
                if (!edgeSet.add(edge)) edgeSet.remove(edge);
            }
        }
        return edgeSet;
    }

    private record Edge(BlockPos a, BlockPos b) {
        public Edge(BlockPos a, BlockPos b) {
            if (a.asLong() < b.asLong()) {
                this.a = a;
                this.b = b;
            } else {
                this.a = b;
                this.b = a;
            }
        }
    }
}
