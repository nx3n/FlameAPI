package nx3n.flameapi.visual.anim;

/** One-shot animation from 0..duration. */
public abstract class Tween {
    protected final float duration;
    protected final Easing easing;
    protected float t;
    protected boolean finished;

    protected Tween(float duration, Easing easing) {
        this.duration = Math.max(0.0001f, duration);
        this.easing = easing == null ? Easings.LINEAR : easing;
    }

    public boolean finished() {
        return finished;
    }

    public void tick(float dt) {
        if (finished) return;
        t += dt;
        float p = t / duration;
        if (p >= 1f) {
            p = 1f;
            finished = true;
        }
        apply(easing.apply(clamp01(p)));
    }

    protected abstract void apply(float eased01);

    protected static float clamp01(float v) {
        if (v < 0f) return 0f;
        if (v > 1f) return 1f;
        return v;
    }
}
