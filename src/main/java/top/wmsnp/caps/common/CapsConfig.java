package top.wmsnp.caps.common;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import org.jspecify.annotations.NonNull;
import top.wmsnp.caps.Caps;
import top.wmsnp.caps.client.renderer.RenderMode;

public class CapsConfig {
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec CLIENT;
    public static final ModConfigSpec SERVER;
    public static final ModConfigSpec.ConfigValue<Integer> MAX_VEIN_BLOCKS;
    public static final ModConfigSpec.ConfigValue<Integer> SERVER_MAX_VEIN_BLOCKS;
    public static final ModConfigSpec.EnumValue<@NonNull RenderMode> RENDER_MODE;
    public static final ModConfigSpec.BooleanValue PARTICLES;

    public static final ModConfigSpec.ConfigValue<Integer> COLOR_R;
    public static final ModConfigSpec.ConfigValue<Integer> COLOR_G;
    public static final ModConfigSpec.ConfigValue<Integer> COLOR_B;
    public static final ModConfigSpec.ConfigValue<Integer> COLOR_A;
    public static final ModConfigSpec.ConfigValue<Double> LINEWIDTH;

    static {
        SERVER_MAX_VEIN_BLOCKS = SERVER_BUILDER.defineInRange("server_max_vein_blocks", Caps.MAX_MAX_VEIN_BLOCKS, 0, Caps.MAX_MAX_VEIN_BLOCKS);

        MAX_VEIN_BLOCKS = CLIENT_BUILDER.defineInRange("max_vein_blocks", 64, 0, Caps.MAX_MAX_VEIN_BLOCKS);

        CLIENT_BUILDER.push("visuals");
        RENDER_MODE = CLIENT_BUILDER.defineEnum("render_mode", RenderMode.FACE);
        PARTICLES = CLIENT_BUILDER.define("particles", false);
        CLIENT_BUILDER.push("color");
        COLOR_R = CLIENT_BUILDER.defineInRange("color_r", 255, 0, 255);
        COLOR_G = CLIENT_BUILDER.defineInRange("color_g", 255, 0, 255);
        COLOR_B = CLIENT_BUILDER.defineInRange("color_b", 255, 0, 255);
        COLOR_A = CLIENT_BUILDER.defineInRange("color_a", 200, 0, 255);
        CLIENT_BUILDER.pop();
        LINEWIDTH = CLIENT_BUILDER.defineInRange("linewidth", 10F, 1, 40);
        CLIENT_BUILDER.pop();

        SERVER = SERVER_BUILDER.build();
        CLIENT = CLIENT_BUILDER.build();
    }

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, CapsConfig.CLIENT);
        container.registerConfig(ModConfig.Type.SERVER, CapsConfig.SERVER);
    }
}
