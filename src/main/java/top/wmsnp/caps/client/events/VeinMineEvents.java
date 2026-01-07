package top.wmsnp.caps.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import top.wmsnp.caps.Caps;
import top.wmsnp.caps.client.ModKeyBindings;
import top.wmsnp.caps.client.renderer.VeinRenderer;
import top.wmsnp.caps.common.CapsConfig;
import top.wmsnp.caps.common.VeinMine;
import top.wmsnp.caps.network.VeinMinePayload;

@EventBusSubscriber(modid = Caps.MODID, value = Dist.CLIENT)
public class VeinMineEvents {
    private static boolean lastState = false;
    public static VeinMine.VeinMineResult last = null;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        boolean currentState = ModKeyBindings.VEIN_MINE.isDown();
        if (currentState != lastState) {
            lastState = currentState;
            ClientPacketDistributor.sendToServer(new VeinMinePayload(currentState, CapsConfig.MAX_VEIN_BLOCKS.get()));
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || !ModKeyBindings.VEIN_MINE.isDown()) return;
        if (last == null) return;
        int count = last.poss.size();
        if (count == 0) return;
        MutableComponent text = Component.translatable("caps.gui.vein_mine_count", count);
        GuiGraphics graphics = event.getGuiGraphics();
        int x = graphics.guiWidth() / 2 + 10;
        int y = graphics.guiHeight() / 2 + 10;
        int width = mc.font.width(text);
        graphics.fill(x - 2, y - 2, x + width + 2, y + mc.font.lineHeight + 2, 0x60000000);
        graphics.drawString(mc.font, text, x, y, 0xFFFFFFFF);
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent.AfterOpaqueBlocks event) {
        VeinRenderer renderer = CapsConfig.RENDER_MODE.get().getRenderer();
        if (renderer == null) return;
        Minecraft mc = Minecraft.getInstance();
        BlockHitResult hit = mc.hitResult instanceof BlockHitResult bhr ? bhr : null;
        if (mc.player == null || hit == null || mc.level == null || !ModKeyBindings.VEIN_MINE.isDown()) {
            last = null;
            return;
        }
        BlockPos pos = hit.getBlockPos();
        if (last == null || !pos.equals(last.startPos)) {
            int min = Math.min(CapsConfig.MAX_VEIN_BLOCKS.get(), CapsConfig.SERVER_MAX_VEIN_BLOCKS.get());
            last = VeinMine.collect(mc.player, pos, mc.level.getBlockState(pos), min, false);
        }
        if (last.poss.size() <= 1) return;
        renderer.render(VeinMineEvents.last.poss, event.getPoseStack(), mc.gameRenderer.getMainCamera().position(), mc.renderBuffers().bufferSource());
    }
}