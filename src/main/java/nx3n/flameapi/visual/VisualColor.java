package nx3n.flameapi.visual;

public final class VisualColor {
    private VisualColor() {
    }

    public static int withAlpha(int color, float alpha) {
        int clamped = (int) (Math.max(0f, Math.min(1f, alpha)) * 255f);
        return (clamped << 24) | (color & 0xFFFFFF);
    }

    public static int parseHex(String value, int fallback) {
        if (value == null) {
            return fallback;
        }
        String normalized = value.startsWith("#") ? value.substring(1) : value;
        try {
            long parsed = Long.parseLong(normalized, 16);
            if (normalized.length() <= 6) {
                return 0xFF000000 | (int) parsed;
            }
            return (int) parsed;
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
