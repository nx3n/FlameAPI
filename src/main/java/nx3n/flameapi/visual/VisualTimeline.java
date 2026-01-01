package nx3n.flameapi.visual;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class VisualTimeline {
    private final List<VisualKeyframe> keyframes = new ArrayList<>();
    private float time;
    private boolean loop;

    public VisualTimeline at(float time) {
        this.time = time;
        return this;
    }

    public VisualTimeline set(String property, float value) {
        return set(property, value, VisualEasing.LINEAR);
    }

    public VisualTimeline set(String property, float value, VisualEasing easing) {
        keyframes.add(new VisualKeyframe(time, property, value, easing));
        keyframes.sort(Comparator.comparing(VisualKeyframe::time));
        return this;
    }

    public VisualTimeline loop(boolean loop) {
        this.loop = loop;
        return this;
    }

    public float duration() {
        return keyframes.stream().map(VisualKeyframe::time).max(Float::compare).orElse(0f);
    }

    public List<VisualKeyframe> keyframes() {
        return new ArrayList<>(keyframes);
    }

    public void apply(VisualProps props, float timelineTime) {
        if (keyframes.isEmpty()) {
            return;
        }
        float maxTime = duration();
        float effectiveTime = loop && maxTime > 0f ? (timelineTime % maxTime) : Math.min(timelineTime, maxTime);

        List<String> properties = keyframes.stream().map(VisualKeyframe::property).distinct().collect(Collectors.toList());
        for (String property : properties) {
            VisualKeyframe previous = null;
            VisualKeyframe next = null;
            for (VisualKeyframe keyframe : keyframes) {
                if (!keyframe.property().equals(property)) {
                    continue;
                }
                if (keyframe.time() <= effectiveTime) {
                    previous = keyframe;
                } else {
                    next = keyframe;
                    break;
                }
            }
            if (previous == null) {
                continue;
            }
            if (next == null || next.time() == previous.time()) {
                props.set(property, previous.value());
                continue;
            }
            float segmentT = (effectiveTime - previous.time()) / (next.time() - previous.time());
            float eased = next.easing().apply(segmentT);
            props.set(property, previous.value() + (next.value() - previous.value()) * eased);
        }
    }
}
