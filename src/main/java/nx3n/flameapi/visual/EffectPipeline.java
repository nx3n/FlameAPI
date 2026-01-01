package nx3n.flameapi.visual;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Ordered, tickable list of effects for one layer/context type. */
public final class EffectPipeline<C> {
    private final List<Effect<C>> effects = new CopyOnWriteArrayList<>();

    public boolean isEmpty() {
        return effects.isEmpty();
    }

    public List<Effect<C>> snapshot() {
        return new ArrayList<>(effects);
    }

    public void add(Effect<C> effect) {
        effects.add(effect);
        sort();
    }

    public void remove(Effect<C> effect) {
        effects.remove(effect);
        try {
            effect.dispose();
        } catch (Throwable ignored) {
        }
    }

    public void clear() {
        for (Effect<C> e : effects) {
            try {
                e.dispose();
            } catch (Throwable ignored) {
            }
        }
        effects.clear();
    }

    public void tick(float dt) {
        for (Effect<C> e : effects) {
            if (!e.enabled()) {
                continue;
            }
            e.tick(dt);
        }
    }

    public void render(C context) {
        for (Effect<C> e : effects) {
            if (!e.enabled()) {
                continue;
            }
            e.render(context);
        }
    }

    private void sort() {
        effects.sort(Comparator.comparingInt(Effect::priority));
    }
}
