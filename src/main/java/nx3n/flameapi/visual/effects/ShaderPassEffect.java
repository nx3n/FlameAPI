package nx3n.flameapi.visual.effects;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;

import nx3n.flameapi.visual.Effect;
import nx3n.flameapi.visual.Props;
import nx3n.flameapi.visual.ScreenContext;
import nx3n.flameapi.visual.draw.Draw;
import nx3n.flameapi.visual.gfx.Shader;
import nx3n.flameapi.visual.gfx.Shaders;

/**
 * Screen-space shader pass.
 *
 * Notes:
 * - This is a lightweight abstraction: it calls a registered {@link Shader} by id.
 * - If no shader is registered, it falls back to a simple full-screen overlay (so presets don't hard-crash).
 */
public final class ShaderPassEffect implements Effect<ScreenContext> {
    private final Props props;
    private final int priority;

    public ShaderPassEffect(Props props) {
        this.props = props == null ? new Props() : props;
        this.priority = this.props.getInt("priority", 0);
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public Props props() {
        return props;
    }

    @Override
    public void render(ScreenContext ctx) {
        float intensity = ctx.intensity();
        if (intensity <= 0.001f) return;

        String shaderId = props.getString("shader", "");
        Shader shader = shaderId.isEmpty() ? null : Shaders.create(shaderId, props);

        PoseStack pose = ctx.guiGraphics().pose();
        if (shader != null) {
            shader.begin(pose, props, intensity);
            // The shader typically defines how it shades; we still draw a full-screen quad.
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            Draw.rect(ctx.guiGraphics(), 0, 0, ctx.screenWidth(), ctx.screenHeight(), 0xFFFFFFFF);
            shader.end();
            return;
        }

        // Fallback: simple overlay (configurable) so presets remain usable.
        int color = props.getInt("color", 0x00000000);
        int a = (int) (((color >>> 24) & 0xFF) * intensity);
        int out = (color & 0x00FFFFFF) | (a << 24);
        Draw.rect(ctx.guiGraphics(), 0, 0, ctx.screenWidth(), ctx.screenHeight(), out);
    }
}
