package top.wmsnp.caps.client;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import top.wmsnp.caps.Caps;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import top.wmsnp.caps.client.gui.ConfigSectionScreen;


@EventBusSubscriber(modid = Caps.MODID, value = Dist.CLIENT)
public class ClientEvents {
    public static final RenderPipeline.Snippet COLOR_SNIPPET = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET).withVertexShader("core/position_color").withFragmentShader("core/position_color").withBlend(BlendFunction.TRANSLUCENT).withDepthWrite(false).withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS).buildSnippet();
    public static final RenderPipeline COLOR_PIPELINE = RenderPipeline.builder(COLOR_SNIPPET).withLocation(Identifier.fromNamespaceAndPath(Caps.MODID, "pipeline/color")).build();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ModContainer container = ModList.get().getModContainerById(Caps.MODID).orElseThrow();
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> new ConfigurationScreen(modContainer, parent, ConfigSectionScreen::new));
    }
    @SubscribeEvent
    public static void registerRenderPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(COLOR_PIPELINE);
    }
}