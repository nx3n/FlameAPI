package nx3n.flameapi.visual.effects;

import nx3n.flameapi.visual.VisualDraw;
import nx3n.flameapi.visual.VisualEffect;
import nx3n.flameapi.visual.VisualLayerType;
import nx3n.flameapi.visual.VisualPalette;
import nx3n.flameapi.visual.VisualRenderContext;

public class AuroraPulseEffect extends VisualEffect {
    private static final int[] PALETTE = new int[]{
        0x55FF7A18,
        0x5532E1FF,
        0x559A5CFF,
        0x55FF4E9B
    };

    public AuroraPulseEffect() {
        super(VisualLayerType.SCREEN);
    }

    @Override
    public void render(VisualRenderContext context) {
        if (context.guiGraphics() == null) {
            return;
        }
        float time = (context.minecraft().level == null ? 0f : context.minecraft().level.getGameTime() / 240f);
        int color = VisualPalette.cycle(PALETTE, time);
        int width = context.screenWidth();
        int height = context.screenHeight();
        int thickness = Math.max(6, (int) (12 * context.intensity()));

        VisualDraw.drawRect(context.guiGraphics(), 0, 0, width, thickness, color);
        VisualDraw.drawRect(context.guiGraphics(), 0, 0, thickness, height, color);
        VisualDraw.drawRect(context.guiGraphics(), width - thickness, 0, thickness, height, color);
        VisualDraw.drawRect(context.guiGraphics(), 0, height - thickness, width, thickness, color);
    }
}
