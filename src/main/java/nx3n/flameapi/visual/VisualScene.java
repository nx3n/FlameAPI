package nx3n.flameapi.visual;

<<<<<<< ours
import java.util.EnumMap;
import java.util.Map;
=======
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
>>>>>>> theirs

public final class VisualScene {
    private final String id;
    private final Map<VisualLayerType, Float> intensities = new EnumMap<>(VisualLayerType.class);
<<<<<<< ours
=======
    private final List<VisualEffect> effects = new ArrayList<>();
    private final VisualProps props = new VisualProps();
    private final VisualTimeline timeline = new VisualTimeline();
    private final Map<VisualEventType, List<Consumer<VisualEventContext>>> eventHandlers = new EnumMap<>(VisualEventType.class);
    private Runnable onEnter;
    private Runnable onExit;
>>>>>>> theirs

    public VisualScene(String id) {
        this.id = id;
        for (VisualLayerType type : VisualLayerType.values()) {
            intensities.put(type, 1.0f);
        }
    }

    public String id() {
        return id;
    }

<<<<<<< ours
=======
    public VisualProps props() {
        return props;
    }

    public VisualTimeline timeline() {
        return timeline;
    }

>>>>>>> theirs
    public VisualScene setIntensity(VisualLayerType type, float intensity) {
        intensities.put(type, clamp(intensity));
        return this;
    }

    public float intensity(VisualLayerType type) {
        return intensities.getOrDefault(type, 1.0f);
    }

    public boolean enabled(VisualLayerType type) {
        return intensity(type) > 0.001f;
    }

<<<<<<< ours
=======
    public VisualScene addEffect(VisualEffect effect) {
        if (effect != null) {
            effects.add(effect);
        }
        return this;
    }

    public List<VisualEffect> effects() {
        return effects;
    }

    public VisualScene onEnter(Runnable action) {
        this.onEnter = action;
        return this;
    }

    public VisualScene onExit(Runnable action) {
        this.onExit = action;
        return this;
    }

    public VisualScene onEvent(VisualEventType type, Consumer<VisualEventContext> handler) {
        eventHandlers.computeIfAbsent(type, key -> new ArrayList<>()).add(handler);
        return this;
    }

    void enter() {
        if (onEnter != null) {
            onEnter.run();
        }
        dispatchEvent(new VisualEventContext(VisualEventType.ENTER_SCENE, this));
    }

    void exit() {
        if (onExit != null) {
            onExit.run();
        }
        dispatchEvent(new VisualEventContext(VisualEventType.EXIT_SCENE, this));
    }

    void dispatchEvent(VisualEventContext context) {
        List<Consumer<VisualEventContext>> handlers = eventHandlers.get(context.type());
        if (handlers == null) {
            return;
        }
        for (Consumer<VisualEventContext> handler : handlers) {
            handler.accept(context);
        }
    }

    void tick(float deltaSeconds) {
        float time = props.getFloat("time", 0f) + deltaSeconds;
        props.set("time", time);
        timeline.apply(props, time);
        effects.removeIf(effect -> {
            effect.tick(deltaSeconds);
            return effect.expired();
        });
    }

>>>>>>> theirs
    private float clamp(float value) {
        if (value < 0f) {
            return 0f;
        }
        if (value > 1f) {
            return 1f;
        }
        return value;
    }
}
