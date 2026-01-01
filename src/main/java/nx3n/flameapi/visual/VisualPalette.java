package nx3n.flameapi.visual;

public final class VisualPalette {
    private VisualPalette() {
    }

    public static int blend(int colorA, int colorB, float t) {
        float clamped = Math.max(0f, Math.min(1f, t));
        int a1 = (colorA >> 24) & 0xFF;
        int r1 = (colorA >> 16) & 0xFF;
        int g1 = (colorA >> 8) & 0xFF;
        int b1 = colorA & 0xFF;
        int a2 = (colorB >> 24) & 0xFF;
        int r2 = (colorB >> 16) & 0xFF;
        int g2 = (colorB >> 8) & 0xFF;
        int b2 = colorB & 0xFF;
        int a = (int) (a1 + (a2 - a1) * clamped);
        int r = (int) (r1 + (r2 - r1) * clamped);
        int g = (int) (g1 + (g2 - g1) * clamped);
        int b = (int) (b1 + (b2 - b1) * clamped);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int cycle(int[] palette, float phase) {
        if (palette == null || palette.length == 0) {
            return 0xFFFFFFFF;
        }
        if (palette.length == 1) {
            return palette[0];
        }
        float wrapped = phase - (float) Math.floor(phase);
        float scaled = wrapped * palette.length;
        int index = (int) Math.floor(scaled);
        int nextIndex = (index + 1) % palette.length;
        float local = scaled - index;
        return blend(palette[index], palette[nextIndex], local);
    }
}
