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
<<<<<<< ours
=======
        if (current != null) {
            current.exit();
        }
>>>>>>> theirs
        current = scene;
        target = null;
        blendProgress = 0f;
        blendDuration = 0f;
<<<<<<< ours
=======
        current.enter();
>>>>>>> theirs
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
<<<<<<< ours
    }

    public void tick(float deltaSeconds) {
=======
        target.enter();
    }

    public void tick(float deltaSeconds) {
        current.tick(deltaSeconds);
        if (target != null) {
            target.tick(deltaSeconds);
        }
>>>>>>> theirs
        if (target == null) {
            return;
        }
        blendProgress += deltaSeconds;
        if (blendProgress >= blendDuration) {
<<<<<<< ours
=======
            current.exit();
>>>>>>> theirs
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
