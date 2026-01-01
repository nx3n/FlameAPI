package nx3n.flameapi.visual.effects;

import nx3n.flameapi.visual.VisualColor;
import nx3n.flameapi.visual.VisualDraw;
import nx3n.flameapi.visual.VisualEffect;
import nx3n.flameapi.visual.VisualLayerType;
import nx3n.flameapi.visual.VisualPalette;
import nx3n.flameapi.visual.VisualPalettes;
import nx3n.flameapi.visual.VisualRenderContext;

public final class ColorShiftEffect extends VisualEffect {
    public ColorShiftEffect(VisualLayerType layer) {
        super(layer);
        set("alpha", 0.35f);
        set("speed", 0.2f);
        set("palette", "default");
    }

    public ColorShiftEffect palette(String name) {
        set("palette", name);
        return this;
    }

    @Override
    public void render(VisualRenderContext context) {
        if (context.guiGraphics() == null) {
            return;
        }
        float time = (context.minecraft().level == null ? 0f : context.minecraft().level.getGameTime() * props().getFloat("speed", 0.2f) / 20f);
        int color = VisualPalette.cycle(VisualPalettes.palette(props().getString("palette", "default")), time);
        int tinted = VisualColor.withAlpha(color, props().getFloat("alpha", 0.35f) * context.intensity());
        VisualDraw.drawRect(context.guiGraphics(), 0, 0, context.screenWidth(), context.screenHeight(), tinted);
    }
}
