package nx3n.flameapi.visual;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.damagesource.DamageSource;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Tiny client-side trigger bus. Lets scenes/effects react to events without hard-coding hooks.
 */
public final class TriggerBus {
    public interface Listener {
        void onTrigger(Trigger trigger, Object payload);
    }

    public record DamagePayload(float amount, DamageSource source) {
    }

    public record ScreenPayload(Screen screen) {
    }

    private final Map<Trigger, List<Listener>> listeners = new EnumMap<>(Trigger.class);

    // Small scheduler (used by Fx.pulse(), scripts, etc.).
    private static final class Task {
        float left;
        Runnable r;
        Task(float left, Runnable r) { this.left = left; this.r = r; }
    }
    private final List<Task> tasks = new CopyOnWriteArrayList<>();

    public TriggerBus() {
        for (Trigger t : Trigger.values()) {
            listeners.put(t, new CopyOnWriteArrayList<>());
        }
    }

    public void on(Trigger trigger, Listener listener) {
        listeners.get(trigger).add(listener);
    }

    public void off(Trigger trigger, Listener listener) {
        listeners.get(trigger).remove(listener);
    }

    public void emit(Trigger trigger, Object payload) {
        for (Listener l : snapshot(trigger)) {
            try {
                l.onTrigger(trigger, payload);
            } catch (Throwable ignored) {
            }
        }
    }

    public void tick(float dt) {
        if (!tasks.isEmpty()) {
            for (Task t : new ArrayList<>(tasks)) {
                t.left -= dt;
                if (t.left <= 0f) {
                    tasks.remove(t);
                    try { t.r.run(); } catch (Throwable ignored) {}
                }
            }
        }
        emit(Trigger.TICK, dt);
    }

    /** Run a callback after N seconds (client ticks). */
    public void after(float seconds, Runnable r) {
        if (r == null) return;
        tasks.add(new Task(Math.max(0f, seconds), r));
    }

    private List<Listener> snapshot(Trigger trigger) {
        return new ArrayList<>(listeners.get(trigger));
    }
}
