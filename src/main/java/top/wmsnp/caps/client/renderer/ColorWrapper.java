package top.wmsnp.caps.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jspecify.annotations.NonNull;

public class ColorWrapper implements VertexConsumer {
    private final VertexConsumer parent;
    private final int color;

    public ColorWrapper(VertexConsumer parent, int color) {
        this.parent = parent;
        this.color = color;
    }

    @Override
    public @NonNull VertexConsumer setColor(int i) {
        return parent.setColor(this.color);
    }
    @Override
    public @NonNull VertexConsumer setColor(int r, int g, int b, int a) {
        return parent.setColor(color);
    }

    @Override public @NonNull VertexConsumer addVertex(float x, float y, float z) { return parent.addVertex(x, y, z); }
    @Override public @NonNull VertexConsumer setUv(float u, float v) { return parent.setUv(u, v); }
    @Override public @NonNull VertexConsumer setUv2(int u, int v) { return parent.setUv2(u, v); }
    @Override public @NonNull VertexConsumer setNormal(float x, float y, float z) { return parent.setNormal(x, y, z); }
    @Override public @NonNull VertexConsumer setUv1(int i, int i1) { return parent.setUv1(i, i1); }
    @Override public @NonNull VertexConsumer setLineWidth(float v) { return parent.setLineWidth(v); }
}
