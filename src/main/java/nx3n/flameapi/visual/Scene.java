package nx3n.flameapi.visual;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class Scene {
    private final String id;
    private final Map<Layer, Float> intensities = new EnumMap<>(Layer.class);
    private final Map<Layer, EffectStack<?>> effectStacks = new EnumMap<>(Layer.class);

    public Scene(String id) {
        this.id = id;
        for (Layer type : Layer.values()) {
            intensities.put(type, 1.0f);
            effectStacks.put(type, new EffectStack<>());
        }
    }

    public String id() {
        return id;
    }

    public Scene setIntensity(Layer type, float intensity) {
        intensities.put(type, clamp(intensity));
        return this;
    }

    public float intensity(Layer type) {
        return intensities.getOrDefault(type, 1.0f);
    }

    public boolean enabled(Layer type) {
        return intensity(type) > 0.001f;
    }

    /** Scene-local explicit stack (if empty, the global pipeline is used). */
    @SuppressWarnings("unchecked")
    public EffectStack<?> effectStack(Layer type) {
        return effectStacks.get(type);
    }

    /** Add a concrete effect instance to this scene for a given layer. */
    public Scene addEffect(Layer type, Effect<?> effect, float multiplier) {
        // Stack type is resolved at render-time (it recreates typed contexts internally).
        ((EffectStack) effectStack(type)).add((Effect) effect, multiplier);
        return this;
    }

    /** Add an effect by registry id (data-driven). */
    public Scene addEffectId(Layer type, String effectId, Props props, float multiplier) {
        EffectFactory factory = EffectRegistry.factory(effectId);
        if (factory == null) {
            return this;
        }
        Effect<?> effect = factory.create(props == null ? new Props() : props);
        ((EffectStack) effectStack(type)).add((Effect) effect, multiplier);
        return this;
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
