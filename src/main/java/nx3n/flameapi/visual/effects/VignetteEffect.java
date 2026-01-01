package nx3n.flameapi.visual.effects;

import nx3n.flameapi.visual.VisualColor;
import nx3n.flameapi.visual.VisualDraw;
import nx3n.flameapi.visual.VisualEffect;
import nx3n.flameapi.visual.VisualLayerType;
import nx3n.flameapi.visual.VisualRenderContext;

public final class VignetteEffect extends VisualEffect {
    public VignetteEffect(VisualLayerType layer) {
        super(layer);
        set("strength", 0.5f);
        set("color", "#000000");
    }

    public VignetteEffect strength(float strength) {
        set("strength", strength);
        return this;
    }

    @Override
    public void render(VisualRenderContext context) {
        if (context.guiGraphics() == null) {
            return;
        }
        int width = context.screenWidth();
        int height = context.screenHeight();
        float strength = props().getFloat("strength", 0.5f) * context.intensity();
        int baseColor = VisualColor.parseHex(props().getString("color", "#000000"), 0xFF000000);
        int edgeColor = VisualColor.withAlpha(baseColor, strength);
        int centerColor = VisualColor.withAlpha(baseColor, strength * 0.2f);

        int border = Math.max(16, (int) (Math.min(width, height) * 0.15f));
        VisualDraw.drawGradient(context.guiGraphics(), 0, 0, width, border, edgeColor, centerColor);
        VisualDraw.drawGradient(context.guiGraphics(), 0, height - border, width, border, centerColor, edgeColor);
        VisualDraw.drawRect(context.guiGraphics(), 0, border, border, height - border * 2, edgeColor);
        VisualDraw.drawRect(context.guiGraphics(), width - border, border, border, height - border * 2, edgeColor);
    }
}
