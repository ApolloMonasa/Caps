package top.wmsnp.caps.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.*;

public class VeinLineRenderer implements IVeinRenderer {
    private static final float LIFT = 1e-3f;
    private static final FaceFrame[] FRAMES = Arrays.stream(Direction.values()).map(FaceFrame::of).toArray(FaceFrame[]::new);
    private static final List<Rule> RULES = List.of(
            new Rule(N.L, new Rect(Bound.Z, Bound.T, Bound.T, Bound.INV_T)),
            new Rule(N.R, new Rect(Bound.INV_T, Bound.O, Bound.T, Bound.INV_T)),
            new Rule(N.B, new Rect(Bound.T, Bound.INV_T, Bound.Z, Bound.T)),
            new Rule(N.T, new Rect(Bound.T, Bound.INV_T, Bound.INV_T, Bound.O)),
            new Rule(N.L, N.B, N.BL, new Rect(Bound.Z, Bound.T, Bound.Z, Bound.T)),
            new Rule(N.R, N.B, N.BR, new Rect(Bound.INV_T, Bound.O, Bound.Z, Bound.T)),
            new Rule(N.L, N.T, N.TL, new Rect(Bound.Z, Bound.T, Bound.INV_T, Bound.O)),
            new Rule(N.R, N.T, N.TR, new Rect(Bound.INV_T, Bound.O, Bound.INV_T, Bound.O))
    );
    public void render(List<BlockPos> blocks, PoseStack poseStack) {
        if (blocks.isEmpty()) return;
        final Set<BlockPos> blockSet = new HashSet<>(blocks);
        final float t = (float) getLinewidth();
        final int color = getColor();
        final Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        final Matrix4f matrix = poseStack.last().pose();
        final VertexConsumer builder = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RENDER_TYPE);

        for (BlockPos pos : blocks) {
            Vec3 center = pos.getCenter().subtract(cameraPos);
            for (Direction face : Direction.values()) {
                if (blockSet.contains(pos.relative(face))) continue;
                FaceFrame frame = FRAMES[face.ordinal()];
                int mask = getMask(pos, frame, blockSet);
                for (Rule rule : RULES) {
                    if ((mask & rule.mask) == 0) continue;
                    Rect r = rule.rect();
                    draw(builder, matrix, center, frame, r.u0().eva(t), r.u1().eva(t), r.v0().eva(t), r.v1().eva(t), color);
                }
            }
        }
    }

    private static void draw(VertexConsumer builder, Matrix4f matrix, Vec3 center, FaceFrame frame, float u0, float u1, float v0, float v1, int color) {
        addVtx(builder, matrix, center.add(frame.proj(u0, v0, LIFT)), color, frame.n());
        addVtx(builder, matrix, center.add(frame.proj(u1, v0, LIFT)), color, frame.n());
        addVtx(builder, matrix, center.add(frame.proj(u1, v1, LIFT)), color, frame.n());
        addVtx(builder, matrix, center.add(frame.proj(u0, v1, LIFT)), color, frame.n());
    }
    private static void addVtx(VertexConsumer b, Matrix4f m, Vec3 p, int c, Vec3 normal) {
        b.addVertex(m, (float)p.x, (float)p.y, (float)p.z).setColor(c).setOverlay(OverlayTexture.NO_OVERLAY).setNormal((float)normal.x, (float)normal.y, (float)normal.z);
    }

    private int getMask(BlockPos pos, FaceFrame frame, Set<BlockPos> blockSet) {
        int mask = 0;
        Direction dU = frame.dirU();
        Direction dV = frame.dirV();
        for (N n : N.values()) if (!blockSet.contains(pos.relative(dU, n.du).relative(dV, n.dv))) mask |= n.bit;
        return mask;
    }

    record FaceFrame(Direction face, Vec3 u, Vec3 v, Vec3 n) {
        public static FaceFrame of(Direction face) {
            Vec3 n = face.getUnitVec3();
            Vec3 v = (face.getAxis() == Direction.Axis.Y) ? new Vec3(0, 0, face == Direction.UP ? -1 : 1) : new Vec3(0, 1, 0);
            return new FaceFrame(face, v.cross(n), v, n);
        }
        public Vec3 proj(double uC, double vC, double offset) { return n.scale(offset + 0.5).add(u.scale(uC - 0.5)).add(v.scale(vC - 0.5)); }
        public Direction dirU() { return Direction.getNearest((int)u.x, (int)u.y, (int)u.z, Direction.UP); }
        public Direction dirV() { return Direction.getNearest((int)v.x, (int)v.y, (int)v.z, Direction.UP); }
    }

    enum N {
        TL(-1,  1, 0), T ( 0,  1, 1), TR( 1,  1, 2),
        L (-1,  0, 3),                R ( 1,  0, 4),
        BL(-1, -1, 5), B ( 0, -1, 6), BR( 1, -1, 7);
        public final int du, dv, bit;
        N(int du, int dv, int bit) { this.du = du; this.dv = dv; this.bit = 1 << bit; }
    }

    record Rule(int mask, Rect rect) {
        public Rule(N n, Rect r) { this(n.bit, r); }
        public Rule(N n1, N n2, N dia, Rect r) { this(n1.bit | n2.bit | dia.bit, r); }
    }
    enum Bound {
        Z, T, INV_T, O;
        float eva(float t) { return switch (this) { case Z -> 0; case O -> 1; case T -> t; case INV_T -> 1 - t; }; }
    }
    record Rect(Bound u0, Bound u1, Bound v0, Bound v1) {}
}