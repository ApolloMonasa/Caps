package top.wmsnp.caps;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import top.wmsnp.caps.common.CapsConfig;

@Mod("caps")
public class Caps {
    public static final String MODID = "caps";
    public static final int MAX_MAX_VEIN_BLOCKS = 1024;

    public Caps(ModContainer container) {
        CapsConfig.register(container);
    }
}
