package top.wmsnp.caps.common;

import net.neoforged.neoforge.common.ModConfigSpec;
import top.wmsnp.caps.Caps;

public class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<Integer> SERVER_MAX_VEIN_BLOCKS;

    static {
        SERVER_MAX_VEIN_BLOCKS = BUILDER.defineInRange("server_max_vein_blocks", Caps.MAX_MAX_VEIN_BLOCKS, 0, Caps.MAX_MAX_VEIN_BLOCKS);
        SPEC = BUILDER.build();
    }
}
