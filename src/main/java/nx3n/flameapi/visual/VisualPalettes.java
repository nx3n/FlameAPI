package nx3n.flameapi.visual;

import java.util.HashMap;
import java.util.Map;

public final class VisualPalettes {
    private static final Map<String, int[]> PALETTES = new HashMap<>();

    static {
        register("default", new int[]{0xFF4B9BFF, 0xFF6DE8FF, 0xFFB9F3FF});
        register("cold", new int[]{0xFF0F172A, 0xFF1E40AF, 0xFF38BDF8});
        register("warm", new int[]{0xFF581C87, 0xFFEA580C, 0xFFFACC15});
    }

    private VisualPalettes() {
    }

    public static void register(String name, int[] colors) {
        PALETTES.put(name, colors);
    }

    public static int[] palette(String name) {
        return PALETTES.getOrDefault(name, PALETTES.get("default"));
    }
}
