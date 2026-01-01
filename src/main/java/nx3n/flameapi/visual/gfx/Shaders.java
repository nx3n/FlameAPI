package nx3n.flameapi.visual.gfx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import nx3n.flameapi.visual.Props;

/**
 * High-level shader registry.
 *
 * This does NOT attempt to replace Minecraft's internal shader loading.
 * It provides a stable API surface for effects to request a shader pass by id
 * and set uniforms from {@link Props}.
 */
public final class Shaders {
    private static final Map<String, Function<Props, Shader>> REG = new ConcurrentHashMap<>();

    private Shaders() {
    }

    public static void register(String id, Function<Props, Shader> factory) {
        if (id == null || factory == null) return;
        REG.put(id, factory);
    }

    public static Shader create(String id, Props props) {
        Function<Props, Shader> f = REG.get(id);
        return f == null ? null : f.apply(props);
    }
}
