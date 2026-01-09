package top.wmsnp.caps.client.adapters;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.api.v0.IrisApiConfig;
import net.irisshaders.iris.api.v0.IrisProgram;

public class IrisAdapter {
    private static final IrisApi api = IrisApi.getInstance();
    private static IrisApiConfig lastConfig = null;
    public static void handleIrisPipeline(RenderPipeline pipeline) {
        IrisApiConfig currentConfig = api.isShaderPackInUse() ? api.getConfig() : null;
        if (currentConfig != lastConfig) {
            lastConfig = currentConfig;
            if (currentConfig == null) return;
            try { api.assignPipeline(pipeline, IrisProgram.BASIC); }
            catch (IllegalStateException ignored) {}
        }
    }
}
