package nx3n.flameapi.visual;

/**
 * New-style effect object.
 * <p>
 * - Supports priority ordering
 * - Supports ticking (animations)
 * - Supports properties (for data-driven config / editor)
 */
public interface Effect<C> {

    /** Higher renders later (on top). */
    default int priority() {
        return 0;
    }

    /** Per-tick update at 20 TPS (dt ~= 0.05). */
    default void tick(float dt) {
    }

    /** Render hook. */
    void render(C context);

    /** Optional typed properties for data-driven/editor usage. */
    default Props props() {
        return Props.EMPTY;
    }

    /** If false, pipeline skips the effect. */
    default boolean enabled() {
        return true;
    }

    /** Called when effect is removed/disposed. */
    default void dispose() {
    }
}
