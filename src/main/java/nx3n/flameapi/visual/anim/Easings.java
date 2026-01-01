package nx3n.flameapi.visual.anim;

/** Common easing functions (small set). */
public final class Easings {
    private Easings() {
    }

    public static final Easing LINEAR = t -> t;
    public static final Easing IN_QUAD = t -> t * t;
    public static final Easing OUT_QUAD = t -> 1f - (1f - t) * (1f - t);
    public static final Easing IN_OUT_QUAD = t -> t < 0.5f ? 2f * t * t : 1f - (float) Math.pow(-2f * t + 2f, 2f) / 2f;

    /** Approx elastic-out (not physically perfect, but usable). */
    public static final Easing OUT_ELASTIC = t -> {
        if (t == 0f) return 0f;
        if (t == 1f) return 1f;
        float c4 = (float) ((2 * Math.PI) / 3);
        return (float) (Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * c4) + 1);
    };
}
