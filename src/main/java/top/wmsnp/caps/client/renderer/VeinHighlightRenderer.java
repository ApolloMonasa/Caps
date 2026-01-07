package top.wmsnp.caps.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import top.wmsnp.caps.common.CapsConfig;

import java.util.ArrayList;
import java.util.List;

public class VeinHighlightRenderer {
    public static final List<BlockPos> blocks = new ArrayList<>();

    // 一个方块的 12 条棱的连接关系 (索引对应 0-7 的顶点)
    private static final int[][] EDGES = {
            {0, 1}, {1, 2}, {2, 3}, {3, 0}, // 底面 4 条边
            {4, 5}, {5, 6}, {6, 7}, {7, 4}, // 顶面 4 条边
            {0, 4}, {1, 5}, {2, 6}, {3, 7}  // 中间 4 条立柱
    };

    public void render(PoseStack poseStack, Vec3 cameraPos, MultiBufferSource bufferSource) {
        if (blocks.isEmpty()) return;

        // 使用 debugFilledBox (支持半透明颜色)
        VertexConsumer builder = bufferSource.getBuffer(RenderTypes.debugFilledBox());

        // 获取配置参数
        int r = CapsConfig.COLOR_R.get();
        int g = CapsConfig.COLOR_G.get();
        int b = CapsConfig.COLOR_B.get();
        int a = CapsConfig.COLOR_A.get();
        float thickness = CapsConfig.LINE_THICKNESS.get().floatValue();
        float radius = thickness / 2.0f;

        // 微小的膨胀系数，防止和方块表面重叠闪烁
        double expansion = 0.01;

        PoseStack.Pose pose = poseStack.last();

        // --- 暴力遍历：不计算外轮廓，直接画每一个方块的所有边 ---
        for (BlockPos pos : blocks) {
            // 计算当前方块相对于相机的中心坐标
            double dx = pos.getX() - cameraPos.x;
            double dy = pos.getY() - cameraPos.y;
            double dz = pos.getZ() - cameraPos.z;

            // 计算该方块 8 个顶点的坐标 (带膨胀)
            // min = 0 - expansion, max = 1 + expansion
            double min = -expansion;
            double max = 1.0 + expansion;

            // 预计算 8 个顶点坐标
            Vec3[] verts = new Vec3[] {
                    new Vec3(dx + min, dy + min, dz + min), // 0: 000
                    new Vec3(dx + max, dy + min, dz + min), // 1: 100
                    new Vec3(dx + max, dy + min, dz + max), // 2: 101
                    new Vec3(dx + min, dy + min, dz + max), // 3: 001
                    new Vec3(dx + min, dy + max, dz + min), // 4: 010
                    new Vec3(dx + max, dy + max, dz + min), // 5: 110
                    new Vec3(dx + max, dy + max, dz + max), // 6: 111
                    new Vec3(dx + min, dy + max, dz + max)  // 7: 011
            };

            // 画这个方块的 12 条棱
            for (int[] edge : EDGES) {
                Vec3 p1 = verts[edge[0]];
                Vec3 p2 = verts[edge[1]];

                // 绘制连接 p1 和 p2 的粗线条 (长方体)
                drawThickLine(builder, pose, p1, p2, radius, r, g, b, a);
            }
        }
    }

    /**
     * 绘制一条连接 p1 和 p2 的粗线 (长方体)
     * 修复接缝问题：向两端延长 radius 长度，填补顶点缺口
     */
    private void drawThickLine(VertexConsumer builder, PoseStack.Pose pose, Vec3 p1, Vec3 p2, float radius, int r, int g, int b, int a) {
        double x1 = p1.x, y1 = p1.y, z1 = p1.z;
        double x2 = p2.x, y2 = p2.y, z2 = p2.z;

        double minX, minY, minZ, maxX, maxY, maxZ;

        // 判断线的方向，生成对应的包围盒
        // 修改：在主轴方向上，向两头各延长 radius (即减去 radius 和 加上 radius)
        if (Math.abs(x2 - x1) > 0.5) { // X轴方向
            minX = Math.min(x1, x2) - radius; maxX = Math.max(x1, x2) + radius; // 延长 X
            minY = y1 - radius;               maxY = y1 + radius;
            minZ = z1 - radius;               maxZ = z1 + radius;
        } else if (Math.abs(y2 - y1) > 0.5) { // Y轴方向
            minX = x1 - radius;               maxX = x1 + radius;
            minY = Math.min(y1, y2) - radius; maxY = Math.max(y1, y2) + radius; // 延长 Y
            minZ = z1 - radius;               maxZ = z1 + radius;
        } else { // Z轴方向
            minX = x1 - radius;               maxX = x1 + radius;
            minY = y1 - radius;               maxY = y1 + radius;
            minZ = Math.min(z1, z2) - radius; maxZ = Math.max(z1, z2) + radius; // 延长 Z
        }

        addBox(builder, pose, (float)minX, (float)minY, (float)minZ, (float)maxX, (float)maxY, (float)maxZ, r, g, b, a);
    }

    private void addBox(VertexConsumer builder, PoseStack.Pose pose, float x1, float y1, float z1, float x2, float y2, float z2, int r, int g, int b, int a) {
        // 下面
        addQuad(builder, pose, x1, x2, z1, z2, y1, r, g, b, a, false);
        // 上面
        addQuad(builder, pose, x1, x2, z1, z2, y2, r, g, b, a, true);
        // 北面
        addVerticalQuad(builder, pose, x1, x2, y1, y2, z1, r, g, b, a);
        // 南面
        addVerticalQuad(builder, pose, x1, x2, y1, y2, z2, r, g, b, a);
        // 西面
        addSideQuad(builder, pose, z1, z2, y1, y2, x1, r, g, b, a);
        // 东面
        addSideQuad(builder, pose, z1, z2, y1, y2, x2, r, g, b, a);
    }

    private void addQuad(VertexConsumer builder, PoseStack.Pose pose, float minX, float maxX, float minZ, float maxZ, float y, int r, int g, int b, int a, boolean up) {
        if (up) {
            vertex(builder, pose, minX, y, minZ, r, g, b, a);
            vertex(builder, pose, minX, y, maxZ, r, g, b, a);
            vertex(builder, pose, maxX, y, maxZ, r, g, b, a);
            vertex(builder, pose, maxX, y, minZ, r, g, b, a);
        } else {
            vertex(builder, pose, minX, y, minZ, r, g, b, a);
            vertex(builder, pose, maxX, y, minZ, r, g, b, a);
            vertex(builder, pose, maxX, y, maxZ, r, g, b, a);
            vertex(builder, pose, minX, y, maxZ, r, g, b, a);
        }
    }

    private void addVerticalQuad(VertexConsumer builder, PoseStack.Pose pose, float minX, float maxX, float minY, float maxY, float z, int r, int g, int b, int a) {
        vertex(builder, pose, minX, minY, z, r, g, b, a);
        vertex(builder, pose, maxX, minY, z, r, g, b, a);
        vertex(builder, pose, maxX, maxY, z, r, g, b, a);
        vertex(builder, pose, minX, maxY, z, r, g, b, a);
    }

    private void addSideQuad(VertexConsumer builder, PoseStack.Pose pose, float minZ, float maxZ, float minY, float maxY, float x, int r, int g, int b, int a) {
        vertex(builder, pose, x, minY, minZ, r, g, b, a);
        vertex(builder, pose, x, minY, maxZ, r, g, b, a);
        vertex(builder, pose, x, maxY, maxZ, r, g, b, a);
        vertex(builder, pose, x, maxY, minZ, r, g, b, a);
    }

    private void vertex(VertexConsumer builder, PoseStack.Pose pose, float x, float y, float z, int r, int g, int b, int a) {
        builder.addVertex(pose, x, y, z).setColor(r, g, b, a).setNormal(0, 1, 0);
    }
}