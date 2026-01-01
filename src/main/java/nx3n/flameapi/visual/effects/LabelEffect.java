package nx3n.flameapi.visual.effects;

import nx3n.flameapi.visual.VisualColor;
import nx3n.flameapi.visual.VisualDraw;
import nx3n.flameapi.visual.VisualEffect;
import nx3n.flameapi.visual.VisualLayerType;
import nx3n.flameapi.visual.VisualRenderContext;

public final class LabelEffect extends VisualEffect {
    public LabelEffect(VisualLayerType layer) {
        super(layer);
        set("text", "Flame Visuals");
        set("x", 8f);
        set("y", 8f);
        set("color", "#E2E8FF");
        set("shadow", 0f);
    }

    public LabelEffect text(String text) {
        set("text", text);
        return this;
    }

    @Override
    public void render(VisualRenderContext context) {
        if (context.guiGraphics() == null) {
            return;
        }
        String text = props().getString("text", "Flame Visuals");
        int x = (int) props().getFloat("x", 8f);
        int y = (int) props().getFloat("y", 8f);
        int color = VisualColor.parseHex(props().getString("color", "#E2E8FF"), 0xFFE2E8FF);
        boolean shadow = props().getFloat("shadow", 0f) > 0.5f;
        VisualDraw.drawText(context.guiGraphics(), text, x, y, color, shadow);
    }
}
