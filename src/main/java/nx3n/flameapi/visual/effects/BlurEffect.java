package nx3n.flameapi.visual.effects;

import nx3n.flameapi.visual.VisualColor;
import nx3n.flameapi.visual.VisualDraw;
import nx3n.flameapi.visual.VisualEffect;
import nx3n.flameapi.visual.VisualLayerType;
import nx3n.flameapi.visual.VisualRenderContext;

public final class BlurEffect extends VisualEffect {
    public BlurEffect(VisualLayerType layer) {
        super(layer);
        set("radius", 12f);
        set("alpha", 0.6f);
        set("color", "#000000");
    }

    public BlurEffect radius(float radius) {
        set("radius", radius);
        return this;
    }

    public BlurEffect alpha(float alpha) {
        set("alpha", alpha);
        return this;
    }

    public BlurEffect color(String hex) {
        set("color", hex);
        return this;
    }

    @Override
    public void render(VisualRenderContext context) {
        if (context.guiGraphics() == null) {
            return;
        }
        int width = context.screenWidth();
        int height = context.screenHeight();
        int passes = Math.max(1, (int) props().getFloat("radius", 12f));
        float alpha = props().getFloat("alpha", 0.6f) * context.intensity();
        int color = VisualColor.parseHex(props().getString("color", "#000000"), 0xFF000000);
        int tinted = VisualColor.withAlpha(color, alpha);
        VisualDraw.drawBlur(context.guiGraphics(), 0, 0, width, height, tinted, passes);
    }
}
