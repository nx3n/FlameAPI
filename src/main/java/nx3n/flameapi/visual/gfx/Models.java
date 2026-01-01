package nx3n.flameapi.visual.gfx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import nx3n.flameapi.visual.Props;

/** Registry of 3D model providers for effects. */
public final class Models {
    private static final Map<String, Function<Props, Model>> REG = new ConcurrentHashMap<>();

    private Models() {
    }

    public static void register(String id, Function<Props, Model> factory) {
        if (id == null || factory == null) return;
        REG.put(id, factory);
    }

    public static Model create(String id, Props props) {
        Function<Props, Model> f = REG.get(id);
        return f == null ? null : f.apply(props);
    }
}
