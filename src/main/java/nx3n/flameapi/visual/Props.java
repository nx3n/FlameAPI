package nx3n.flameapi.visual;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple typed property bag for effects ("uniforms" / editor params).
 * Kept intentionally small: float, int (incl ARGB), bool, string.
 */
public final class Props {
    public static final Props EMPTY = new Props(Collections.emptyMap());

    private final Map<String, Object> values;

    public Props() {
        this.values = new HashMap<>();
    }

    private Props(Map<String, Object> values) {
        this.values = values;
    }

    public Map<String, Object> asMap() {
        return Collections.unmodifiableMap(values);
    }

    public Props set(String key, float v) {
        values.put(key, v);
        return this;
    }

    public Props set(String key, int v) {
        values.put(key, v);
        return this;
    }

    public Props set(String key, boolean v) {
        values.put(key, v);
        return this;
    }

    public Props set(String key, String v) {
        values.put(key, v);
        return this;
    }

    // Convenience setters used by the high-level DSL (Fx).
    // Keep these tiny so users can write .prop("x", 1) without caring about internals.
    public Props setInt(String key, int v) { return set(key, v); }
    public Props setFloat(String key, float v) { return set(key, v); }
    public Props setBool(String key, boolean v) { return set(key, v); }
    public Props setString(String key, String v) { return set(key, v); }

    public float getFloat(String key, float def) {
        Object o = values.get(key);
        return o instanceof Number n ? n.floatValue() : def;
    }

    public int getInt(String key, int def) {
        Object o = values.get(key);
        return o instanceof Number n ? n.intValue() : def;
    }

    public boolean getBool(String key, boolean def) {
        Object o = values.get(key);
        return o instanceof Boolean b ? b : def;
    }

    public String getString(String key, String def) {
        Object o = values.get(key);
        return o instanceof String s ? s : def;
    }
}
