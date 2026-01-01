package nx3n.flameapi.visual.effects;

import nx3n.flameapi.visual.Palette;
import nx3n.flameapi.visual.ScreenContext;
import nx3n.flameapi.visual.ScreenEffect;

public class AuroraPulseEffect implements ScreenEffect {
    private static final int[] PALETTE = new int[]{
        0x55FF7A18,
        0x5532E1FF,
        0x559A5CFF,
        0x55FF4E9B
    };

    @Override
    public void render(ScreenContext context) {
        float time = (context.minecraft().level == null ? 0f : context.minecraft().level.getGameTime() / 240f);
        int color = Palette.cycle(PALETTE, time);
        int width = context.screenWidth();
        int height = context.screenHeight();
        int thickness = Math.max(6, (int) (12 * context.intensity()));

        context.guiGraphics().fill(0, 0, width, thickness, color);
        context.guiGraphics().fill(0, 0, thickness, height, color);
        context.guiGraphics().fill(width - thickness, 0, width, height, color);
        context.guiGraphics().fill(0, height - thickness, width, height, color);
    }
}
