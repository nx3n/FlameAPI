package nx3n.flameapi.visual;

public final class VisualKeyframe {
    private final float time;
    private final String property;
    private final float value;
    private final VisualEasing easing;

    public VisualKeyframe(float time, String property, float value, VisualEasing easing) {
        this.time = time;
        this.property = property;
        this.value = value;
        this.easing = easing == null ? VisualEasing.LINEAR : easing;
    }

    public float time() {
        return time;
    }

    public String property() {
        return property;
    }

    public float value() {
        return value;
    }

    public VisualEasing easing() {
        return easing;
    }
}
