package nx3n.flameapi.visual;

import nx3n.flameapi.visual.effects.AuroraPulseEffect;

public final class VisualApiShowcase {
    private VisualApiShowcase() {
    }

    public static void registerDefaults() {
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
    }
}
