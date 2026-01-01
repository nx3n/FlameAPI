package nx3n.flameapi.visual;

import java.util.ArrayList;
import java.util.List;

public final class VisualEffectConfig {
    private final String type;
    private final VisualProps props = new VisualProps();
    private final List<VisualAnimation> animations = new ArrayList<>();
    private final VisualTimeline timeline = new VisualTimeline();
    private VisualLayerType layer = VisualLayerType.SCREEN;
    private float lifetime = -1f;
    private boolean loop;

    public VisualEffectConfig(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }

    public VisualProps props() {
        return props;
    }

    public List<VisualAnimation> animations() {
        return animations;
    }

    public VisualTimeline timeline() {
        return timeline;
    }

    public VisualLayerType layer() {
        return layer;
    }

    public void layer(VisualLayerType layer) {
        this.layer = layer;
    }

    public float lifetime() {
        return lifetime;
    }

    public void lifetime(float lifetime) {
        this.lifetime = lifetime;
    }

    public boolean loop() {
        return loop;
    }

    public void loop(boolean loop) {
        this.loop = loop;
    }

    public void applyTo(VisualEffect effect) {
        for (String key : props.floats().keySet()) {
            effect.set(key, props.getFloat(key, 0f));
        }
        for (String key : props.strings().keySet()) {
            effect.set(key, props.getString(key, ""));
        }
        for (VisualAnimation animation : animations) {
            effect.animate(animation.property(), animation.easing(), animation.from(), animation.to(), animation.duration(), animation.delay(), animation.loop());
        }
        for (VisualKeyframe keyframe : timeline.keyframes()) {
            effect.timeline().at(keyframe.time()).set(keyframe.property(), keyframe.value(), keyframe.easing());
        }
        effect.timeline().loop(loop);
        effect.lifetime(lifetime).loop(loop);
    }
}
