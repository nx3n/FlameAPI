package nx3n.flameapi.visual.anim;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Small helper you can embed in an effect to animate parameters. */
public final class Animator {
    private final List<Tween> tweens = new CopyOnWriteArrayList<>();

    public void add(Tween tween) {
        if (tween != null) tweens.add(tween);
    }

    public void tick(float dt) {
        for (Tween t : tweens) {
            t.tick(dt);
            if (t.finished()) {
                tweens.remove(t);
            }
        }
    }

    public void clear() {
        tweens.clear();
    }
}
