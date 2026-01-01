package nx3n.flameapi.visual;

import java.util.EnumMap;
import java.util.Map;

public final class VisualScene {
    private final String id;
    private final Map<VisualLayerType, Float> intensities = new EnumMap<>(VisualLayerType.class);

    public VisualScene(String id) {
        this.id = id;
        for (VisualLayerType type : VisualLayerType.values()) {
            intensities.put(type, 1.0f);
        }
    }

    public String id() {
        return id;
    }

    public VisualScene setIntensity(VisualLayerType type, float intensity) {
        intensities.put(type, clamp(intensity));
        return this;
    }

    public float intensity(VisualLayerType type) {
        return intensities.getOrDefault(type, 1.0f);
    }

    public boolean enabled(VisualLayerType type) {
        return intensity(type) > 0.001f;
    }

    private float clamp(float value) {
        if (value < 0f) {
            return 0f;
        }
        if (value > 1f) {
            return 1f;
        }
        return value;
    }
}
