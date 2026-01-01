package nx3n.flameapi.visual;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class VisualEffect {
    private final VisualLayerType layer;
    private final VisualProps props = new VisualProps();
    private final List<VisualAnimation> animations = new ArrayList<>();
    private final VisualTimeline timeline = new VisualTimeline();
    private float lifetime = -1f;
    private float age;
    private boolean loop;
    private float timelineTime;

    protected VisualEffect(VisualLayerType layer) {
        this.layer = layer;
    }

    public VisualLayerType layer() {
        return layer;
    }

    public VisualProps props() {
        return props;
    }

    public VisualTimeline timeline() {
        return timeline;
    }

    public VisualEffect set(String key, float value) {
        props.set(key, value);
        return this;
    }

    public VisualEffect set(String key, String value) {
        props.set(key, value);
        return this;
    }

    public VisualEffect animate(String property, VisualEasing easing, float from, float to, float duration) {
        return animate(property, easing, from, to, duration, 0f, false);
    }

    public VisualEffect animate(String property, VisualEasing easing, float from, float to, float duration, float delay, boolean loop) {
        animations.add(new VisualAnimation(property, easing, from, to, duration, delay, loop));
        return this;
    }

    public VisualEffect lifetime(float seconds) {
        this.lifetime = seconds;
        return this;
    }

    public VisualEffect loop(boolean loop) {
        this.loop = loop;
        return this;
    }

    public void reset() {
        age = 0f;
        timelineTime = 0f;
        for (VisualAnimation animation : animations) {
            animation.reset();
        }
    }

    public void tick(float deltaSeconds) {
        age += deltaSeconds;
        timelineTime += deltaSeconds;
        timeline.apply(props, timelineTime);
        Iterator<VisualAnimation> iterator = animations.iterator();
        while (iterator.hasNext()) {
            VisualAnimation animation = iterator.next();
            if (animation.tick(deltaSeconds, props)) {
                iterator.remove();
            }
        }
        if (loop && lifetime > 0f && age >= lifetime) {
            reset();
        }
    }

    public boolean expired() {
        return lifetime > 0f && age >= lifetime && !loop;
    }

    public abstract void render(VisualRenderContext context);
}
