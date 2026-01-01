package nx3n.flameapi.visual.gfx;

import com.mojang.blaze3d.vertex.PoseStack;

import nx3n.flameapi.visual.Props;

/**
 * Minimal shader-pass abstraction.
 *
 * Implementations typically call RenderSystem.setShader(...) and set uniforms.
 * This interface is intentionally small to keep effect code high-level.
 */
public interface Shader {
    /** Prepare shader state (bind program, set uniforms). */
    void begin(PoseStack pose, Props props, float intensity);

    /** Restore state if needed. */
    default void end() {}
}
