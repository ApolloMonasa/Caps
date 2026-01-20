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
import net.neoforged.neoforge.network.PacketDistributor;
import top.wmsnp.caps.Caps;
import top.wmsnp.caps.client.ClientEvents;
import top.wmsnp.caps.client.ModKeyBindings;
import top.wmsnp.caps.client.adapters.Adapters;
import top.wmsnp.caps.client.adapters.IrisAdapter;
import top.wmsnp.caps.client.renderer.IVeinRenderer;
import top.wmsnp.caps.client.renderer.RenderMode;
import top.wmsnp.caps.client.renderer.VeinFaceRenderer;
import top.wmsnp.caps.client.renderer.VeinLineRenderer;
import top.wmsnp.caps.common.ClientConfig;
import top.wmsnp.caps.common.VeinMine;
import top.wmsnp.caps.network.VeinMinePayload;
import top.wmsnp.caps.common.ServerConfig;

import java.util.Map;

@EventBusSubscriber(modid = Caps.MODID, value = Dist.CLIENT)
public class VeinMineEvents {
    private static final Map<RenderMode, IVeinRenderer> RENDERERS = Map.of(RenderMode.OUTLINE, new VeinLineRenderer(), RenderMode.FACE, new VeinFaceRenderer());
    private static boolean lastState = false;
    public static VeinMine.VeinMineResult last = null;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        boolean currentState = ModKeyBindings.VEIN_MINE.isDown();
        if (currentState != lastState) {
            lastState = currentState;
            PacketDistributor.sendToServer(new VeinMinePayload(currentState, ClientConfig.MAX_VEIN_BLOCKS.get()));
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || !ModKeyBindings.VEIN_MINE.isDown()) return;
        if (last == null || last.poss.isEmpty()) return;
        MutableComponent text = Component.translatable("caps.gui.vein_mine_count", last.poss.size());
        GuiGraphics graphics = event.getGuiGraphics();
        int x = graphics.guiWidth() / 2 + 10;
        int y = graphics.guiHeight() / 2 + 10;
        int width = mc.font.width(text);
        graphics.fill(x - 2, y - 2, x + width + 2, y + mc.font.lineHeight + 2, 0x60000000);
        graphics.drawString(mc.font, text, x, y, 0xFFFFFFFF);
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent.AfterOpaqueBlocks event) {
        if (Adapters.hasIris()) IrisAdapter.handleIrisPipeline(ClientEvents.COLOR_PIPELINE);
        IVeinRenderer renderer = RENDERERS.get(ClientConfig.RENDER_MODE.get());
        if (renderer == null) return;
        Minecraft mc = Minecraft.getInstance();
        BlockHitResult hit = mc.hitResult instanceof BlockHitResult bhr ? bhr : null;
        if (mc.player == null || hit == null || mc.level == null || !ModKeyBindings.VEIN_MINE.isDown()) {
            last = null;
            return;
        }
        BlockPos pos = hit.getBlockPos();
        if (last == null || !pos.equals(last.startPos)) {
            int min = Math.min(ClientConfig.MAX_VEIN_BLOCKS.get(), ServerConfig.SERVER_MAX_VEIN_BLOCKS.get());
            last = VeinMine.collect(mc.player, pos, mc.level.getBlockState(pos), min, false);
        }
        if (last.poss.isEmpty()) return;
        renderer.render(last.poss, event.getPoseStack());
    }
}