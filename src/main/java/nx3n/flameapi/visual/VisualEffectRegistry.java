package nx3n.flameapi.visual;

import nx3n.flameapi.visual.effects.BlurEffect;
import nx3n.flameapi.visual.effects.ColorShiftEffect;
import nx3n.flameapi.visual.effects.NoiseEffect;
import nx3n.flameapi.visual.effects.ScanlineEffect;
import nx3n.flameapi.visual.effects.VignetteEffect;
import nx3n.flameapi.visual.effects.AuroraPulseEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class VisualEffectRegistry {
    private static final Map<String, Function<VisualEffectConfig, VisualEffect>> REGISTRY = new HashMap<>();
    private static boolean bootstrapped;

    private VisualEffectRegistry() {
    }

    public static void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;
        register("blur", config -> new BlurEffect(config.layer()));
        register("vignette", config -> new VignetteEffect(config.layer()));
        register("color_shift", config -> new ColorShiftEffect(config.layer()));
        register("noise", config -> new NoiseEffect(config.layer()));
        register("scanlines", config -> new ScanlineEffect(config.layer()));
        register("aurora_pulse", config -> new AuroraPulseEffect());
    }

    public static void register(String type, Function<VisualEffectConfig, VisualEffect> factory) {
        REGISTRY.put(type, factory);
    }

    public static VisualEffect create(String type, VisualEffectConfig config) {
        Function<VisualEffectConfig, VisualEffect> factory = REGISTRY.get(type);
        if (factory == null) {
            return null;
        }
        VisualEffect effect = factory.apply(config);
        config.applyTo(effect);
        return effect;
    }
}
