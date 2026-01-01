package nx3n.flameapi.visual.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;

import nx3n.flameapi.visual.Effect;
import nx3n.flameapi.visual.Props;
import nx3n.flameapi.visual.WorldContext;
import nx3n.flameapi.visual.gfx.Model;
import nx3n.flameapi.visual.gfx.Models;

/**
 * World-space 3D model effect.
 *
 * This is intentionally generic: the actual model implementation comes from {@link Models}.
 *
 * Props (suggested):
 * - model: registry id in {@link Models}
 * - x,y,z: local translation (float)
 * - yaw,pitch,roll: degrees
 * - scale: float
 * - priority: int
 */
public final class Model3DEffect implements Effect<WorldContext> {
    private final Props props;
    private final int priority;

    public Model3DEffect(Props props) {
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
    public void render(WorldContext ctx) {
        float intensity = ctx.intensity();
        if (intensity <= 0.001f) return;

        String id = props.getString("model", "");
        if (id.isEmpty()) return;

        Model model = Models.create(id, props);
        if (model == null) return;

        PoseStack pose = ctx.poseStack();
        pose.pushPose();

        float x = props.getFloat("x", 0f);
        float y = props.getFloat("y", 0f);
        float z = props.getFloat("z", 0f);
        pose.translate(x, y, z);

        float scale = props.getFloat("scale", 1f);
        scale = Mth.clamp(scale, 0.001f, 1000f);
        pose.scale(scale, scale, scale);

        // Rotate (degrees)
        float yaw = props.getFloat("yaw", 0f);
        float pitch = props.getFloat("pitch", 0f);
        float roll = props.getFloat("roll", 0f);
        if (yaw != 0f) pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(yaw));
        if (pitch != 0f) pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
        if (roll != 0f) pose.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(roll));

        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource buffers = mc.renderBuffers().bufferSource();

        // Let the model decide render types and vertex formats.
        model.render(pose, buffers, 0xF000F0, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);

        pose.popPose();
    }
}
