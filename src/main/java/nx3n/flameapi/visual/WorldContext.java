package nx3n.flameapi.visual;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderLevelStageEvent;

public final class WorldContext {
    private final Minecraft minecraft;
    private final PoseStack poseStack;
    private final RenderLevelStageEvent.Stage stage;
    private final float partialTick;
    private final float intensity;

    public WorldContext(Minecraft minecraft, PoseStack poseStack, RenderLevelStageEvent.Stage stage, float partialTick, float intensity) {
        this.minecraft = minecraft;
        this.poseStack = poseStack;
        this.stage = stage;
        this.partialTick = partialTick;
        this.intensity = intensity;
    }

    public Minecraft minecraft() {
        return minecraft;
    }

    public PoseStack poseStack() {
        return poseStack;
    }

    public RenderLevelStageEvent.Stage stage() {
        return stage;
    }

    public float partialTick() {
        return partialTick;
    }

    public float intensity() {
        return intensity;
    }
}
