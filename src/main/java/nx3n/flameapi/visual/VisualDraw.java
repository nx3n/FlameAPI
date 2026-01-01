package nx3n.flameapi.visual;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public final class VisualDraw {
    private VisualDraw() {
    }

    public static void drawRect(GuiGraphics guiGraphics, int x, int y, int width, int height, int color) {
        if (guiGraphics == null) {
            return;
        }
        guiGraphics.fill(x, y, x + width, y + height, color);
    }

    public static void drawGradient(GuiGraphics guiGraphics, int x, int y, int width, int height, int topColor, int bottomColor) {
        if (guiGraphics == null) {
            return;
        }
        guiGraphics.fillGradient(x, y, x + width, y + height, topColor, bottomColor);
    }

    public static void drawCircle(GuiGraphics guiGraphics, int centerX, int centerY, int radius, int color) {
        if (guiGraphics == null || radius <= 0) {
            return;
        }
        int rSquared = radius * radius;
        for (int y = -radius; y <= radius; y++) {
            int ySq = y * y;
            int xSpan = (int) Math.floor(Math.sqrt(rSquared - ySq));
            guiGraphics.fill(centerX - xSpan, centerY + y, centerX + xSpan, centerY + y + 1, color);
        }
    }

    public static void drawBlur(GuiGraphics guiGraphics, int x, int y, int width, int height, int color, int passes) {
        if (guiGraphics == null) {
            return;
        }
        int clamped = Math.max(1, Math.min(6, passes));
        for (int i = 0; i < clamped; i++) {
            int inset = i;
            int alpha = (color >> 24) & 0xFF;
            int softened = (Math.max(8, alpha - (i * 18)) << 24) | (color & 0xFFFFFF);
            guiGraphics.fill(x - inset, y - inset, x + width + inset, y + height + inset, softened);
        }
    }

    public static void drawNoise(GuiGraphics guiGraphics, int x, int y, int width, int height, int color, float density) {
        if (guiGraphics == null) {
            return;
        }
        RandomSource random = Minecraft.getInstance().level == null
            ? RandomSource.create()
            : Minecraft.getInstance().level.getRandom();
        int count = Math.max(1, (int) (width * height * Math.min(1f, Math.max(0f, density)) * 0.0025f));
        for (int i = 0; i < count; i++) {
            int px = x + random.nextInt(width);
            int py = y + random.nextInt(height);
            guiGraphics.fill(px, py, px + 1, py + 1, color);
        }
    }

    public static void drawText(GuiGraphics guiGraphics, String text, int x, int y, int color, boolean shadow) {
        if (guiGraphics == null) {
            return;
        }
        guiGraphics.drawString(Minecraft.getInstance().font, text, x, y, color, shadow);
    }

    public static void drawImage(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int width, int height, int u, int v, int textureWidth, int textureHeight) {
        if (guiGraphics == null || texture == null) {
            return;
        }
        guiGraphics.blit(texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    public static void withPose(PoseStack poseStack, Runnable action) {
        if (poseStack == null || action == null) {
            return;
        }
        poseStack.pushPose();
        try {
            action.run();
        } finally {
            poseStack.popPose();
        }
    }
}
