package top.wmsnp.caps.common;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;
import top.wmsnp.caps.Caps;
import top.wmsnp.caps.client.renderer.RenderMode;

public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<Integer> MAX_VEIN_BLOCKS;
    public static final ModConfigSpec.EnumValue<@NonNull RenderMode> RENDER_MODE;
    public static final ModConfigSpec.BooleanValue PARTICLES;

    public static final ModConfigSpec.ConfigValue<Integer> COLOR_R;
    public static final ModConfigSpec.ConfigValue<Integer> COLOR_G;
    public static final ModConfigSpec.ConfigValue<Integer> COLOR_B;
    public static final ModConfigSpec.ConfigValue<Integer> COLOR_A;
    public static final ModConfigSpec.ConfigValue<Double> LINEWIDTH;

    static {
        MAX_VEIN_BLOCKS = BUILDER.defineInRange("max_vein_blocks", 64, 0, Caps.MAX_MAX_VEIN_BLOCKS);

        BUILDER.push("visuals");
        RENDER_MODE = BUILDER.defineEnum("render_mode", RenderMode.OUTLINE);
        PARTICLES = BUILDER.define("particles", false);
        BUILDER.push("color");
        COLOR_R = BUILDER.defineInRange("color_r", 85, 0, 255);
        COLOR_G = BUILDER.defineInRange("color_g", 255, 0, 255);
        COLOR_B = BUILDER.defineInRange("color_b", 255, 0, 255);
        COLOR_A = BUILDER.defineInRange("color_a", 120, 0, 255);
        BUILDER.pop();
        LINEWIDTH = BUILDER.defineInRange("linewidth", 0.1, 0, 1);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
