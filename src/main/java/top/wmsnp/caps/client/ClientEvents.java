package top.wmsnp.caps.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import top.wmsnp.caps.client.renderer.VeinHighlightRenderer;
import top.wmsnp.caps.common.VeinMine;
import top.wmsnp.caps.network.VeinMinePayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = "caps", value = Dist.CLIENT)
public class ClientEvents {
    private static final VeinHighlightRenderer renderer = new VeinHighlightRenderer();

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (ModKeyBindings.VEIN_MINE.isDown()) VeinMinePayload.send(event.getPos(), event.getState());
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent.AfterOpaqueBlocks event) {
        Minecraft mc = Minecraft.getInstance();
        BlockHitResult hit = mc.hitResult instanceof BlockHitResult bhr ? bhr : null;
        if (mc.player == null || hit == null || mc.level == null || !ModKeyBindings.VEIN_MINE.isDown()) return;
        BlockPos startPos = hit.getBlockPos();
        BlockState state = mc.level.getBlockState(startPos);
        VeinMine.VeinMineResult result = VeinMine.collectVeinBlocks(mc.player, startPos, state);
        if (result.poss.isEmpty()) return;

        PoseStack poseStack = event.getPoseStack();
        CameraRenderState cameraState = new CameraRenderState();
        cameraState.pos = mc.gameRenderer.getMainCamera().position();

        VeinHighlightRenderer.blocks.clear();
        VeinHighlightRenderer.blocks.add(startPos);
        VeinHighlightRenderer.blocks.addAll(result.poss);

        renderer.render(poseStack, cameraState, mc.renderBuffers().bufferSource());
    }
}