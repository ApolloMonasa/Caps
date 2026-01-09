package top.wmsnp.caps.client.adapters;

import net.neoforged.fml.ModList;

public class Adapters {
    private static Boolean hasIris = null;
    public static boolean hasIris() {
        if (hasIris == null) hasIris = ModList.get().isLoaded("iris");
        return hasIris;
    }
}
