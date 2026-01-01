package nx3n.flameapi.visual.gfx;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

/**
 * Minimal 3D model abstraction.
 *
 * Implementations can wrap BakedModel, Geo/Animatable, custom mesh, etc.
 */
public interface Model {
    /**
     * Render model in the current render pass.
     *
     * @param pose pose stack (already positioned)
     * @param buffers buffer source
     * @param packedLight light
     * @param packedOverlay overlay
     */
    void render(PoseStack pose, MultiBufferSource buffers, int packedLight, int packedOverlay);
}
