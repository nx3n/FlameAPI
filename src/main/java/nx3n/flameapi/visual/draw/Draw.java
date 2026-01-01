package nx3n.flameapi.visual.draw;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

/**
 * Minimal draw primitives. This is the layer that lets effects avoid touching GuiGraphics directly.
 */
public final class Draw {
    private Draw() {
    }

    public static void rect(GuiGraphics g, int x1, int y1, int x2, int y2, int argb) {
        g.fill(x1, y1, x2, y2, argb);
    }

    public static void gradientRect(GuiGraphics g, int x1, int y1, int x2, int y2, int topArgb, int bottomArgb) {
        g.fillGradient(x1, y1, x2, y2, topArgb, bottomArgb);
    }

    public static void text(GuiGraphics g, String text, int x, int y, int argb) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        g.drawString(mc.font, text, x, y, argb, false);
    }

    public static void image(GuiGraphics g, ResourceLocation tex, int x, int y, int w, int h) {
        g.blit(tex, x, y, 0, 0, w, h, w, h);
    }

    /** Simple filled circle (triangle fan). */
    public static void circle(GuiGraphics g, float cx, float cy, float r, int argb, int segments) {
        if (segments < 12) segments = 12;

        float a = ((argb >> 24) & 0xFF) / 255f;
        float red = ((argb >> 16) & 0xFF) / 255f;
        float green = ((argb >> 8) & 0xFF) / 255f;
        float blue = (argb & 0xFF) / 255f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Matrix4f m = g.pose().last().pose();
        Tesselator t = Tesselator.getInstance();
        BufferBuilder b = t.getBuilder();
        b.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        b.vertex(m, cx, cy, 0).color(red, green, blue, a).endVertex();
        for (int i = 0; i <= segments; i++) {
            double ang = (Math.PI * 2.0) * (i / (double) segments);
            float x = cx + (float) (Math.cos(ang) * r);
            float y = cy + (float) (Math.sin(ang) * r);
            b.vertex(m, x, y, 0).color(red, green, blue, a).endVertex();
        }
        t.end();
    }
}
