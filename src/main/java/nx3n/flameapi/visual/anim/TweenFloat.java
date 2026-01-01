package nx3n.flameapi.visual.anim;

import it.unimi.dsi.fastutil.floats.FloatConsumer;

public final class TweenFloat extends Tween {
    private final float from;
    private final float to;
    private final FloatConsumer setter;

    public TweenFloat(float from, float to, float duration, Easing easing, FloatConsumer setter) {
        super(duration, easing);
        this.from = from;
        this.to = to;
        this.setter = setter;
    }

    @Override
    protected void apply(float eased01) {
        setter.accept(from + (to - from) * eased01);
    }
}
