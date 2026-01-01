package nx3n.flameapi.visual.anim;

@FunctionalInterface
public interface Easing {
    /** t in [0..1] */
    float apply(float t);
}
