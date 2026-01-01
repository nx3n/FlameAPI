package nx3n.flameapi.visual.effects;

import nx3n.flameapi.visual.Effect;
import nx3n.flameapi.visual.Props;
import nx3n.flameapi.visual.ScreenContext;
import nx3n.flameapi.visual.draw.Draw;

/**
 * Simple full-screen tint overlay.
 * Props:
 * - color (int ARGB)
 */
public final class SolidOverlayEffect implements Effect<ScreenContext> {
    private final Props props;

    public SolidOverlayEffect(Props props) {
        this.props = props;
    }

    @Override
    public int priority() {
        return props.getInt("priority", 0);
    }

    @Override
    public Props props() {
        return props;
    }

    @Override
    public void render(ScreenContext ctx) {
        int base = props.getInt("color", 0x66000000);
        // Multiply alpha by ctx.intensity.
        int a = (int) (((base >>> 24) & 0xFF) * clamp01(ctx.intensity()));
        int color = (base & 0x00FFFFFF) | (a << 24);
        Draw.rect(ctx.guiGraphics(), 0, 0, ctx.screenWidth(), ctx.screenHeight(), color);
    }

    private float clamp01(float v) {
        if (v < 0f) return 0f;
        if (v > 1f) return 1f;
        return v;
    }
}
