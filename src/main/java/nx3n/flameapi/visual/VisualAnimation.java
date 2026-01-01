package nx3n.flameapi.visual;

public final class VisualAnimation {
    private final String property;
    private final VisualEasing easing;
    private final float from;
    private final float to;
    private final float duration;
    private final float delay;
    private final boolean loop;
    private float elapsed;

    public VisualAnimation(String property, VisualEasing easing, float from, float to, float duration, float delay, boolean loop) {
        this.property = property;
        this.easing = easing == null ? VisualEasing.LINEAR : easing;
        this.from = from;
        this.to = to;
        this.duration = Math.max(0.001f, duration);
        this.delay = Math.max(0f, delay);
        this.loop = loop;
    }

    public String property() {
        return property;
    }

    public VisualEasing easing() {
        return easing;
    }

    public float from() {
        return from;
    }

    public float to() {
        return to;
    }

    public float duration() {
        return duration;
    }

    public float delay() {
        return delay;
    }

    public boolean loop() {
        return loop;
    }

    public void reset() {
        elapsed = 0f;
    }

    public boolean tick(float deltaSeconds, VisualProps props) {
        elapsed += deltaSeconds;
        if (elapsed < delay) {
            return false;
        }
        float t = Math.min(1f, (elapsed - delay) / duration);
        float eased = easing.apply(t);
        props.set(property, from + (to - from) * eased);
        if (t >= 1f) {
            if (loop) {
                elapsed = delay;
                return false;
            }
            return true;
        }
        return false;
    }
}
