package top.wmsnp.caps.utils;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;

public class TypeUtils {
    public static <T extends Comparable<? super T>>
    ModConfigSpec.Range<@NonNull T> getRange(ModConfigSpec.ValueSpec spec, Class<T> type) {
        ModConfigSpec.Range<?> range = spec.getRange();
        if (range == null) throw new IllegalStateException("Range is null");
        Object min = range.getMin();
        Object max = range.getMax();
        if (!type.isInstance(min) || !type.isInstance(max)) throw new IllegalStateException("Range type mismatch, expected %s but got min=%s, max=%s".formatted(type.getSimpleName(), min.getClass().getSimpleName(), max.getClass().getSimpleName()));
        @SuppressWarnings("unchecked")
        ModConfigSpec.Range<@NonNull T> casted = (ModConfigSpec.Range<@NonNull T>) range;
        return casted;
    }
}
