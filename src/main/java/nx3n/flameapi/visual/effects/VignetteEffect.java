package nx3n.flameapi.visual.effects;

import nx3n.flameapi.visual.Effect;
import nx3n.flameapi.visual.Props;
import nx3n.flameapi.visual.ScreenContext;
import nx3n.flameapi.visual.draw.Draw;

/**
 * Cheap vignette using gradient rectangles.
 * Props:
 * - color (int ARGB) default 0x99000000
 * - size (float 0..1) default 0.22 (fraction of min dimension)
 * - priority (int)
 */
public final class VignetteEffect implements Effect<ScreenContext> {
    private final Props props;

    public VignetteEffect(Props props) {
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
        int base = props.getInt("color", 0x99000000);
        float sizeFrac = clamp01(props.getFloat("size", 0.22f));
        int w = ctx.screenWidth();
        int h = ctx.screenHeight();
        int t = (int) (Math.min(w, h) * sizeFrac);
        if (t <= 0) return;

        // fade alpha by scene intensity
        int a = (int) (((base >>> 24) & 0xFF) * clamp01(ctx.intensity()));
        int edge = (base & 0x00FFFFFF) | (a << 24);
        int center = (base & 0x00FFFFFF); // alpha=0

        // top
        Draw.gradientRect(ctx.guiGraphics(), 0, 0, w, t, edge, center);
        // bottom
        Draw.gradientRect(ctx.guiGraphics(), 0, h - t, w, h, center, edge);
        // left
        Draw.gradientRect(ctx.guiGraphics(), 0, 0, t, h, edge, center);
        // right
        Draw.gradientRect(ctx.guiGraphics(), w - t, 0, w, h, center, edge);
    }

    private float clamp01(float v) {
        if (v < 0f) return 0f;
        if (v > 1f) return 1f;
        return v;
    }
}
