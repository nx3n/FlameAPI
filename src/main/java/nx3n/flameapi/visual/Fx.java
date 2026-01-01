package nx3n.flameapi.visual;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * High-level authoring facade.
 *
 * Goal: user code should describe visuals declaratively (scenes + effects + parameters)
 * without directly touching Minecraft rendering classes.
 */
public final class Fx {
    private final SceneStack scenes;
    private final TriggerBus triggers;

    Fx(SceneStack scenes, TriggerBus triggers) {
        this.scenes = Objects.requireNonNull(scenes);
        this.triggers = Objects.requireNonNull(triggers);
    }

    /** Create or overwrite a scene, configure it, and register it. */
    public SceneBuilder scene(String id, Consumer<SceneBuilder> config) {
        SceneBuilder b = scene(id);
        if (config != null) config.accept(b);
        b.register();
        return b;
    }

    /** Create or overwrite a scene builder (call {@link SceneBuilder#register()} when done). */
    public SceneBuilder scene(String id) {
        return new SceneBuilder(new Scene(id), scenes);
    }

    /** Convenience: set current scene by id (no blend). */
    public void use(String id) {
        Scene s = scenes.get(id);
        if (s != null) scenes.setCurrent(s);
    }

    /** Convenience: blend to scene by id. */
    public void blendTo(String id, float seconds) {
        Scene s = scenes.get(id);
        if (s != null) scenes.blendTo(s, seconds);
    }

    /**
     * Quick "flash" helper.
     *
     * Useful for things like hit effects:
     * <pre>
     *   Api.fx().pulse("hurt", 0.12f, 0.35f);
     * </pre>
     */
    public void pulse(String id, float inSeconds, float outSeconds) {
        Scene s = scenes.get(id);
        if (s == null) return;
        Scene prev = scenes.current();
        scenes.blendTo(s, inSeconds);
        triggers.after(outSeconds, () -> {
            if (scenes.current() == s && prev != null) scenes.blendTo(prev, outSeconds);
        });
    }

    /** Expose triggers for higher-level wiring. */
    public TriggerBus triggers() {
        return triggers;
    }

    // ---------------------------------------------------------------------
    // Builders
    // ---------------------------------------------------------------------

    public static final class SceneBuilder {
        private final Scene scene;
        private final SceneStack scenes;

        SceneBuilder(Scene scene, SceneStack scenes) {
            this.scene = scene;
            this.scenes = scenes;
        }

        public SceneBuilder intensity(Layer layer, float value) {
            scene.setIntensity(layer, value);
            return this;
        }

        public LayerBuilder hud() { return new LayerBuilder(scene, Layer.HUD); }
        public LayerBuilder screen() { return new LayerBuilder(scene, Layer.SCREEN); }
        public LayerBuilder world() { return new LayerBuilder(scene, Layer.WORLD); }
        public LayerBuilder entity() { return new LayerBuilder(scene, Layer.ENTITY); }

        /** Registers/overwrites this scene in the scene stack. */
        public Scene register() {
            scenes.register(scene);
            return scene;
        }

        public Scene scene() {
            return scene;
        }
    }

    public static final class LayerBuilder {
        private final Scene scene;
        private final Layer layer;

        LayerBuilder(Scene scene, Layer layer) {
            this.scene = scene;
            this.layer = layer;
        }

        /** Generic effect by registry id. */
        public EffectBuilder effect(String id) {
            return new EffectBuilder(scene, layer, id);
        }

        // Typed convenience wrappers for built-ins
        public EffectBuilder vignette() { return effect("vignette"); }
        public EffectBuilder overlay() { return effect("solid_overlay"); }
        public EffectBuilder aurora() { return effect("aurora_pulse"); }
        public EffectBuilder shader(String shaderId) { return effect("shader_pass").prop("shader", shaderId); }
        public EffectBuilder model(String modelId) { return effect("model3d").prop("model", modelId); }
    }

    public static final class EffectBuilder {
        private final Scene scene;
        private final Layer layer;
        private final String id;
        private final Props props = new Props();
        private float multiplier = 1f;

        EffectBuilder(Scene scene, Layer layer, String id) {
            this.scene = scene;
            this.layer = layer;
            this.id = id;
        }

        public EffectBuilder multiplier(float v) { this.multiplier = v; return this; }

        public EffectBuilder priority(int p) { props.setInt("priority", p); return this; }
        public EffectBuilder prop(String k, float v) { props.setFloat(k, v); return this; }
        public EffectBuilder prop(String k, int v) { props.setInt(k, v); return this; }
        public EffectBuilder prop(String k, boolean v) { props.setBool(k, v); return this; }
        public EffectBuilder prop(String k, String v) { props.setString(k, v); return this; }

        /** Finalize: add effect to the scene layer. */
        public Scene add() {
            scene.addEffectId(layer, id, props, multiplier);
            return scene;
        }
    }
}
