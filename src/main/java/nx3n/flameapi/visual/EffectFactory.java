package nx3n.flameapi.visual;

/** Creates effects from properties (used by JSON presets / future editor). */
public interface EffectFactory {
    Effect<?> create(Props props);
}
