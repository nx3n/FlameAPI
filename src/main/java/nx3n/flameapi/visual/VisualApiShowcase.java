package nx3n.flameapi.visual;

public final class VisualApiShowcase {
    private VisualApiShowcase() {
    }

    public static void registerDefaults() {
        VisualScene cinematic = new VisualScene("cinematic")
            .setIntensity(VisualLayerType.HUD, 0.8f)
            .setIntensity(VisualLayerType.SCREEN, 0.7f)
            .addEffect(Effects.label().text("Flame Visuals: cinematic"))
            .addEffect(Effects.vignette().strength(0.45f))
            .addEffect(Effects.colorShift().palette("cold"))
            .addEffect(Effects.scanlines().animate("alpha", VisualEasing.OUT_QUAD, 0.05f, 0.25f, 0.6f));

        cinematic.onEnter(() -> cinematic.props().set("intro", 1f));
        cinematic.onEvent(VisualEventType.LOW_HEALTH, event -> cinematic.addEffect(Effects.blur().radius(16f).alpha(0.5f).lifetime(0.8f)));

        VisualApi.activateScene(cinematic, 2.5f);
    }
}
