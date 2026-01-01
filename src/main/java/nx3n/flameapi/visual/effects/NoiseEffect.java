package nx3n.flameapi.visual.effects;

import nx3n.flameapi.visual.VisualColor;
import nx3n.flameapi.visual.VisualDraw;
import nx3n.flameapi.visual.VisualEffect;
import nx3n.flameapi.visual.VisualLayerType;
import nx3n.flameapi.visual.VisualRenderContext;

public final class NoiseEffect extends VisualEffect {
    public NoiseEffect(VisualLayerType layer) {
        super(layer);
        set("density", 0.4f);
        set("alpha", 0.2f);
        set("color", "#FFFFFF");
    }

    @Override
    public void render(VisualRenderContext context) {
        if (context.guiGraphics() == null) {
            return;
        }
        float density = props().getFloat("density", 0.4f);
        float alpha = props().getFloat("alpha", 0.2f) * context.intensity();
        int baseColor = VisualColor.parseHex(props().getString("color", "#FFFFFF"), 0xFFFFFFFF);
        int color = VisualColor.withAlpha(baseColor, alpha);
        VisualDraw.drawNoise(context.guiGraphics(), 0, 0, context.screenWidth(), context.screenHeight(), color, density);
    }
}
