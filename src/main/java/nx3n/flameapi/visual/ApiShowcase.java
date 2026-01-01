package nx3n.flameapi.visual;

import nx3n.flameapi.visual.effects.AuroraPulseEffect;

public final class ApiShowcase {
    private ApiShowcase() {
    }

    public static void registerDefaults() {
        Api.registerHudLayer(context -> {
            String label = "Flame Visuals: " + Api.scenes().current().id();
            int x = 8;
            int y = 8;
            context.guiGraphics().drawString(context.minecraft().font, label, x, y, 0xFFE2E8FF, false);
        });

        Api.registerScreenEffect(new AuroraPulseEffect());

        Scene cinematic = new Scene("cinematic")
            .setIntensity(Layer.HUD, 0.75f)
            .setIntensity(Layer.SCREEN, 0.65f);
        Api.scenes().blendTo(cinematic, 3f);
    }
}
