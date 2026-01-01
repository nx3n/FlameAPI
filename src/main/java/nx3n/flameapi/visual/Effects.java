package nx3n.flameapi.visual;

import nx3n.flameapi.visual.effects.BlurEffect;
import nx3n.flameapi.visual.effects.ColorShiftEffect;
import nx3n.flameapi.visual.effects.LabelEffect;
import nx3n.flameapi.visual.effects.NoiseEffect;
import nx3n.flameapi.visual.effects.ScanlineEffect;
import nx3n.flameapi.visual.effects.VignetteEffect;

public final class Effects {
    private Effects() {
    }

    public static BlurEffect blur() {
        return new BlurEffect(VisualLayerType.SCREEN);
    }

    public static VignetteEffect vignette() {
        return new VignetteEffect(VisualLayerType.SCREEN);
    }

    public static ColorShiftEffect colorShift() {
        return new ColorShiftEffect(VisualLayerType.SCREEN);
    }

    public static NoiseEffect noise() {
        return new NoiseEffect(VisualLayerType.SCREEN);
    }

    public static ScanlineEffect scanlines() {
        return new ScanlineEffect(VisualLayerType.SCREEN);
    }

    public static LabelEffect label() {
        return new LabelEffect(VisualLayerType.HUD);
    }
}
