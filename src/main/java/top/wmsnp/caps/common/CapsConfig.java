package top.wmsnp.caps.common;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;

import top.wmsnp.caps.Caps;

public class CapsConfig {
    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec COMMON;
    public static final ModConfigSpec SERVER;
    public static final ModConfigSpec.ConfigValue<Integer> MAX_VEIN_BLOCKS;
    public static final ModConfigSpec.ConfigValue<Integer> SERVER_MAX_VEIN_BLOCKS;


    static {
        MAX_VEIN_BLOCKS = COMMON_BUILDER.translation("caps.config.max_vein_blocks")
                .defineInRange("max_vein_blocks", 64, 0, Caps.MAX_MAX_VEIN_BLOCKS);
        SERVER_MAX_VEIN_BLOCKS = SERVER_BUILDER.translation("caps.config.server_max_vein_blocks")
                .defineInRange("server_max_vein_blocks", 64, 0, Caps.MAX_MAX_VEIN_BLOCKS);
        SERVER = SERVER_BUILDER.build();
        COMMON = COMMON_BUILDER.build();
    }

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, CapsConfig.COMMON);
        container.registerConfig(ModConfig.Type.SERVER, CapsConfig.SERVER);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
