package nx3n.flameapi.visual;

import java.util.HashMap;
import java.util.Map;

public final class VisualProps {
    private final Map<String, Float> floats = new HashMap<>();
    private final Map<String, String> strings = new HashMap<>();

    public VisualProps set(String key, float value) {
        floats.put(key, value);
        return this;
    }

    public VisualProps set(String key, String value) {
        strings.put(key, value);
        return this;
    }

    public float getFloat(String key, float fallback) {
        return floats.getOrDefault(key, fallback);
    }

    public String getString(String key, String fallback) {
        return strings.getOrDefault(key, fallback);
    }

    public boolean hasFloat(String key) {
        return floats.containsKey(key);
    }

    public Map<String, Float> floats() {
        return floats;
    }

    public Map<String, String> strings() {
        return strings;
    }
}
