package nx3n.flameapi.visual;

public final class SceneStack {
    private final java.util.Map<String, Scene> scenes = new java.util.concurrent.ConcurrentHashMap<>();
    private Scene current;
    private Scene target;
    private float blendDuration;
    private float blendProgress;

    public SceneStack() {
        this.current = new Scene("default");
        scenes.put(this.current.id(), this.current);
    }

    /** Register a scene by id (overwrites existing). */
    public void register(Scene scene) {
        if (scene == null) return;
        scenes.put(scene.id(), scene);
    }

    public Scene get(String id) {
        return id == null ? null : scenes.get(id);
    }

    public java.util.Map<String, Scene> all() {
        return java.util.Collections.unmodifiableMap(scenes);
    }

    public Scene current() {
        return current;
    }

    public Scene target() {
        return target;
    }

    public boolean blending() {
        return target != null;
    }

    public void setCurrent(Scene scene) {
        if (scene == null) {
            return;
        }
        current = scene;
        target = null;
        blendProgress = 0f;
        blendDuration = 0f;
    }

    public void blendTo(Scene scene, float seconds) {
        if (scene == null) {
            return;
        }
        if (seconds <= 0f) {
            setCurrent(scene);
            return;
        }
        target = scene;
        blendDuration = seconds;
        blendProgress = 0f;
    }

    public void tick(float deltaSeconds) {
        if (target == null) {
            return;
        }
        blendProgress += deltaSeconds;
        if (blendProgress >= blendDuration) {
            current = target;
            target = null;
            blendProgress = 0f;
            blendDuration = 0f;
        }
    }

    public float blendFactor() {
        if (target == null || blendDuration <= 0f) {
            return 0f;
        }
        return Math.min(1f, blendProgress / blendDuration);
    }
}
