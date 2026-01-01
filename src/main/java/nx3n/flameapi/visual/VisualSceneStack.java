package nx3n.flameapi.visual;

public final class VisualSceneStack {
    private VisualScene current;
    private VisualScene target;
    private float blendDuration;
    private float blendProgress;

    public VisualSceneStack() {
        this.current = new VisualScene("default");
    }

    public VisualScene current() {
        return current;
    }

    public VisualScene target() {
        return target;
    }

    public boolean blending() {
        return target != null;
    }

    public void setCurrent(VisualScene scene) {
        if (scene == null) {
            return;
        }
        current = scene;
        target = null;
        blendProgress = 0f;
        blendDuration = 0f;
    }

    public void blendTo(VisualScene scene, float seconds) {
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
