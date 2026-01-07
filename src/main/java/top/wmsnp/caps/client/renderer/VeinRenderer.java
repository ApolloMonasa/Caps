package top.wmsnp.caps.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import top.wmsnp.caps.common.CapsConfig;

import java.util.List;
import java.util.stream.IntStream;

public interface VeinRenderer {
    float EPS = 0.002f;
    List<Vec3> CORNERS = IntStream.range(0, 8).mapToObj(i -> new Vec3(i & 1, (i >> 1) & 1, (i >> 2) & 1)).toList();

    void render(List<BlockPos> blocks, PoseStack poseStack, Vec3 cameraPos, MultiBufferSource bufferSource);

    default int getColor() {
        return (CapsConfig.COLOR_A.get() & 0xFF) << 24 | (CapsConfig.COLOR_R.get() & 0xFF) << 16|
                (CapsConfig.COLOR_G.get() & 0xFF) << 8 | (CapsConfig.COLOR_B.get() & 0xFF);
    }
    default VertexConsumer addVertex(VertexConsumer builder, PoseStack poseStack, Vec3 a, int color, Vector3f normal){
        return builder.addVertex(poseStack.last(), a.toVector3f()).setColor(color).setNormal(poseStack.last(), normal);
    }
}
