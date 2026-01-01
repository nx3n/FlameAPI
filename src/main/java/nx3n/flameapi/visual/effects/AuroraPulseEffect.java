package nx3n.flameapi.visual.effects;

<<<<<<< ours
import nx3n.flameapi.visual.VisualPalette;
import nx3n.flameapi.visual.VisualScreenContext;
import nx3n.flameapi.visual.VisualScreenEffect;

public class AuroraPulseEffect implements VisualScreenEffect {
=======
import nx3n.flameapi.visual.VisualDraw;
import nx3n.flameapi.visual.VisualEffect;
import nx3n.flameapi.visual.VisualLayerType;
import nx3n.flameapi.visual.VisualPalette;
import nx3n.flameapi.visual.VisualRenderContext;

public class AuroraPulseEffect extends VisualEffect {
>>>>>>> theirs
    private static final int[] PALETTE = new int[]{
        0x55FF7A18,
        0x5532E1FF,
        0x559A5CFF,
        0x55FF4E9B
    };

<<<<<<< ours
    @Override
    public void render(VisualScreenContext context) {
=======
    public AuroraPulseEffect() {
        super(VisualLayerType.SCREEN);
    }

    @Override
    public void render(VisualRenderContext context) {
        if (context.guiGraphics() == null) {
            return;
        }
>>>>>>> theirs
        float time = (context.minecraft().level == null ? 0f : context.minecraft().level.getGameTime() / 240f);
        int color = VisualPalette.cycle(PALETTE, time);
        int width = context.screenWidth();
        int height = context.screenHeight();
        int thickness = Math.max(6, (int) (12 * context.intensity()));

<<<<<<< ours
        context.guiGraphics().fill(0, 0, width, thickness, color);
        context.guiGraphics().fill(0, 0, thickness, height, color);
        context.guiGraphics().fill(width - thickness, 0, width, height, color);
        context.guiGraphics().fill(0, height - thickness, width, height, color);
=======
        VisualDraw.drawRect(context.guiGraphics(), 0, 0, width, thickness, color);
        VisualDraw.drawRect(context.guiGraphics(), 0, 0, thickness, height, color);
        VisualDraw.drawRect(context.guiGraphics(), width - thickness, 0, thickness, height, color);
        VisualDraw.drawRect(context.guiGraphics(), 0, height - thickness, width, thickness, color);
>>>>>>> theirs
    }
}
