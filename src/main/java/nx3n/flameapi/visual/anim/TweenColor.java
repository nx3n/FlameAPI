package nx3n.flameapi.visual.anim;

import java.util.function.IntConsumer;

public final class TweenColor extends Tween {
    private final int from;
    private final int to;
    private final IntConsumer setter;

    public TweenColor(int from, int to, float duration, Easing easing, IntConsumer setter) {
        super(duration, easing);
        this.from = from;
        this.to = to;
        this.setter = setter;
    }

    @Override
    protected void apply(float eased01) {
        setter.accept(lerpArgb(from, to, eased01));
    }

    private static int lerpArgb(int a, int b, float t) {
        int aA = (a >>> 24) & 0xFF;
        int aR = (a >>> 16) & 0xFF;
        int aG = (a >>> 8) & 0xFF;
        int aB = a & 0xFF;

        int bA = (b >>> 24) & 0xFF;
        int bR = (b >>> 16) & 0xFF;
        int bG = (b >>> 8) & 0xFF;
        int bB = b & 0xFF;

        int oA = (int) (aA + (bA - aA) * t);
        int oR = (int) (aR + (bR - aR) * t);
        int oG = (int) (aG + (bG - aG) * t);
        int oB = (int) (aB + (bB - aB) * t);
        return (oA << 24) | (oR << 16) | (oG << 8) | oB;
    }
}
