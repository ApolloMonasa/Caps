package top.wmsnp.caps.common;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import top.wmsnp.caps.Caps;

public class CapsConfig {
    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder(); // 新增客户端构建器

    public static final ModConfigSpec COMMON;
    public static final ModConfigSpec SERVER;
    public static final ModConfigSpec CLIENT; // 新增客户端配置

    // 通用/服务端配置
    public static final ModConfigSpec.ConfigValue<Integer> MAX_VEIN_BLOCKS;
    public static final ModConfigSpec.ConfigValue<Integer> SERVER_MAX_VEIN_BLOCKS;

    // 客户端外观配置 (RGB + A + 粗细)
    public static final ModConfigSpec.ConfigValue<Integer> COLOR_R;
    public static final ModConfigSpec.ConfigValue<Integer> COLOR_G;
    public static final ModConfigSpec.ConfigValue<Integer> COLOR_B;
    public static final ModConfigSpec.ConfigValue<Integer> COLOR_A;
    public static final ModConfigSpec.ConfigValue<Double> LINE_THICKNESS;

    static {
        MAX_VEIN_BLOCKS = COMMON_BUILDER.translation("caps.config.max_vein_blocks")
                .defineInRange("max_vein_blocks", 64, 0, Caps.MAX_MAX_VEIN_BLOCKS);
        SERVER_MAX_VEIN_BLOCKS = SERVER_BUILDER.translation("caps.config.server_max_vein_blocks")
                .defineInRange("server_max_vein_blocks", 64, 0, Caps.MAX_MAX_VEIN_BLOCKS);

        // --- RGB客户端配置 ---
        CLIENT_BUILDER.push("visual");
        COLOR_R = CLIENT_BUILDER.comment("Red Component (0-255)").defineInRange("color_r", 255, 0, 255);
        COLOR_G = CLIENT_BUILDER.comment("Green Component (0-255)").defineInRange("color_g", 255, 0, 255);
        COLOR_B = CLIENT_BUILDER.comment("Blue Component (0-255)").defineInRange("color_b", 255, 0, 255);
        COLOR_A = CLIENT_BUILDER.comment("Alpha/Transparency (0-255)").defineInRange("color_a", 200, 0, 255);
        // 默认直径 0.1 (方块边长的 1/10)
        LINE_THICKNESS = CLIENT_BUILDER.comment("Thickness of the highlight lines (0.01 - 0.5)")
                .defineInRange("line_thickness", 0.1, 0.01, 0.5);
        CLIENT_BUILDER.pop();

        SERVER = SERVER_BUILDER.build();
        COMMON = COMMON_BUILDER.build();
        CLIENT = CLIENT_BUILDER.build(); // 构建客户端配置
    }

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, CapsConfig.COMMON);
        container.registerConfig(ModConfig.Type.SERVER, CapsConfig.SERVER);
        container.registerConfig(ModConfig.Type.CLIENT, CapsConfig.CLIENT); // 注册客户端配置
    }
}