package nx3n.flameapi.visual.anim;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Minimal keyframe timeline for a single float value. */
public final class Timeline {
    public static final class Key {
        public final float time;
        public final float value;
        public final Easing easing;
        public Key(float time, float value, Easing easing) {
            this.time = time;
            this.value = value;
            this.easing = easing == null ? Easings.LINEAR : easing;
        }
    }

    private final List<Key> keys = new ArrayList<>();
    private float t;
    private boolean loop;
    private float length;

    public Timeline loop(boolean loop) {
        this.loop = loop;
        return this;
    }

    public Timeline key(float time, float value, Easing easing) {
        keys.add(new Key(time, value, easing));
        keys.sort(Comparator.comparingDouble(k -> k.time));
        length = Math.max(length, time);
        return this;
    }

    public void reset() {
        t = 0f;
    }

    public void tick(float dt) {
        if (keys.isEmpty()) return;
        t += dt;
        if (loop && length > 0f) {
            while (t > length) t -= length;
        }
    }

    /** Sample value at current timeline time. */
    public float value(float def) {
        if (keys.isEmpty()) return def;
        if (keys.size() == 1) return keys.get(0).value;
        Key prev = keys.get(0);
        for (int i = 1; i < keys.size(); i++) {
            Key next = keys.get(i);
            if (t <= next.time) {
                float span = Math.max(0.0001f, next.time - prev.time);
                float p = (t - prev.time) / span;
                p = clamp01(p);
                float e = next.easing.apply(p);
                return prev.value + (next.value - prev.value) * e;
            }
            prev = next;
        }
        return keys.get(keys.size() - 1).value;
    }

    private static float clamp01(float v) {
        if (v < 0f) return 0f;
        if (v > 1f) return 1f;
        return v;
    }
}
