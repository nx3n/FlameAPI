package nx3n.flameapi.visual.gfx;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;

import nx3n.flameapi.visual.Props;

/**
 * Built-in shader wrapper that uses Minecraft's position/color shader.
 *
 * Props:
 * - color: ARGB int
 */
public final class BasicShader implements Shader {
    @Override
    public void begin(PoseStack pose, Props props, float intensity) {
        int argb = props.getInt("color", 0xFFFFFFFF);
        float a = ((argb >>> 24) & 0xFF) / 255f;
        float r = ((argb >>> 16) & 0xFF) / 255f;
        float g = ((argb >>> 8) & 0xFF) / 255f;
        float b = (argb & 0xFF) / 255f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(r, g, b, a * intensity);
    }

    @Override
    public void end() {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }
}
