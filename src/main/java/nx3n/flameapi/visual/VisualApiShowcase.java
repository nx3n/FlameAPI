package nx3n.flameapi.visual;

<<<<<<< ours
import nx3n.flameapi.visual.effects.AuroraPulseEffect;

=======
>>>>>>> theirs
public final class VisualApiShowcase {
    private VisualApiShowcase() {
    }

    public static void registerDefaults() {
<<<<<<< ours
        VisualApi.registerHudLayer(context -> {
            String label = "Flame Visuals: " + VisualApi.scenes().current().id();
            int x = 8;
            int y = 8;
            context.guiGraphics().drawString(context.minecraft().font, label, x, y, 0xFFE2E8FF, false);
        });

        VisualApi.registerScreenEffect(new AuroraPulseEffect());

        VisualScene cinematic = new VisualScene("cinematic")
            .setIntensity(VisualLayerType.HUD, 0.75f)
            .setIntensity(VisualLayerType.SCREEN, 0.65f);
        VisualApi.scenes().blendTo(cinematic, 3f);
=======
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
>>>>>>> theirs
    }
}
