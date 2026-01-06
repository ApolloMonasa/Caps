package top.wmsnp.caps.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import top.wmsnp.caps.Caps;
import top.wmsnp.caps.client.renderer.VeinHighlightRenderer;
import top.wmsnp.caps.common.VeinMine;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import top.wmsnp.caps.common.CapsConfig;
import top.wmsnp.caps.network.VeinMinePayload;

@EventBusSubscriber(modid = "caps", value = Dist.CLIENT)
public class ClientEvents {
    private static final VeinHighlightRenderer renderer = new VeinHighlightRenderer();
    private static boolean lastState = false;
    private static BlockPos lastPos = null;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ModContainer container = ModList.get().getModContainerById(Caps.MODID).get();
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

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
        int count = VeinHighlightRenderer.blocks.size();
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
        Minecraft mc = Minecraft.getInstance();
        BlockHitResult hit = mc.hitResult instanceof BlockHitResult bhr ? bhr : null;
        if (mc.player == null || hit == null || mc.level == null || !ModKeyBindings.VEIN_MINE.isDown()) {
            lastPos = null;
            VeinHighlightRenderer.blocks.clear();
            return;
        }
        BlockPos pos = hit.getBlockPos();
        if (!pos.equals(lastPos)) {
            lastPos = pos;
            int min = Math.min(CapsConfig.MAX_VEIN_BLOCKS.get(), CapsConfig.SERVER_MAX_VEIN_BLOCKS.get());
            VeinMine.VeinMineResult result = VeinMine.collect(mc.player, pos, mc.level.getBlockState(pos), min, false);
            VeinHighlightRenderer.blocks.clear();
            VeinHighlightRenderer.blocks.add(lastPos);
            VeinHighlightRenderer.blocks.addAll(result.poss);
        }
        if (VeinHighlightRenderer.blocks.size() <= 1) return;
        CameraRenderState cameraState = new CameraRenderState();
        cameraState.pos = mc.gameRenderer.getMainCamera().position();
        renderer.render(event.getPoseStack(), cameraState, mc.renderBuffers().bufferSource());
    }
}