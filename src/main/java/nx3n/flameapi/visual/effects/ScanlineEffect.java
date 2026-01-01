package nx3n.flameapi.visual.effects;

import nx3n.flameapi.visual.VisualColor;
import nx3n.flameapi.visual.VisualDraw;
import nx3n.flameapi.visual.VisualEffect;
import nx3n.flameapi.visual.VisualLayerType;
import nx3n.flameapi.visual.VisualRenderContext;

public final class ScanlineEffect extends VisualEffect {
    public ScanlineEffect(VisualLayerType layer) {
        super(layer);
        set("spacing", 4f);
        set("alpha", 0.2f);
        set("color", "#0F172A");
    }

    @Override
    public void render(VisualRenderContext context) {
        if (context.guiGraphics() == null) {
            return;
        }
        int width = context.screenWidth();
        int height = context.screenHeight();
        int spacing = Math.max(2, (int) props().getFloat("spacing", 4f));
        float alpha = props().getFloat("alpha", 0.2f) * context.intensity();
        int baseColor = VisualColor.parseHex(props().getString("color", "#0F172A"), 0xFF0F172A);
        int color = VisualColor.withAlpha(baseColor, alpha);
        for (int y = 0; y < height; y += spacing) {
            VisualDraw.drawRect(context.guiGraphics(), 0, y, width, 1, color);
        }
    }
}
