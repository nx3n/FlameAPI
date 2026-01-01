package nx3n.flameapi.visual;

/** Wraps legacy functional interfaces into {@link Effect} objects. */
public final class LegacyAdapters {
    private LegacyAdapters() {
    }

    public static Effect<ScreenContext> screen(ScreenEffect fx) {
        return fx::render;
    }

    public static Effect<WorldContext> world(WorldEffect fx) {
        return fx::render;
    }

    public static Effect<EntityContext> entity(EntityEffect fx) {
        return fx::render;
    }
}
