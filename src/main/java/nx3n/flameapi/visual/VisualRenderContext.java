package nx3n.flameapi.visual;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;

public final class VisualRenderContext {
    private final VisualLayerType layer;
    private final Minecraft minecraft;
    private final GuiGraphics guiGraphics;
    private final PoseStack poseStack;
    private final LivingEntity entity;
    private final RenderLivingEvent<?, ?> entityEvent;
    private final RenderLevelStageEvent.Stage stage;
    private final int screenWidth;
    private final int screenHeight;
    private final float partialTick;
    private final float intensity;

    VisualRenderContext(VisualLayerType layer, Minecraft minecraft, GuiGraphics guiGraphics, PoseStack poseStack, LivingEntity entity,
                        RenderLivingEvent<?, ?> entityEvent, RenderLevelStageEvent.Stage stage, int screenWidth, int screenHeight,
                        float partialTick, float intensity) {
        this.layer = layer;
        this.minecraft = minecraft;
        this.guiGraphics = guiGraphics;
        this.poseStack = poseStack;
        this.entity = entity;
        this.entityEvent = entityEvent;
        this.stage = stage;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.partialTick = partialTick;
        this.intensity = intensity;
    }

    public VisualRenderContext withIntensity(float intensity) {
        return new VisualRenderContext(layer, minecraft, guiGraphics, poseStack, entity, entityEvent, stage, screenWidth, screenHeight, partialTick, intensity);
    }

    public static VisualRenderContext hud(Minecraft minecraft, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight, float intensity) {
        return new VisualRenderContext(VisualLayerType.HUD, minecraft, guiGraphics, null, null, null, null, screenWidth, screenHeight, partialTick, intensity);
    }

    public static VisualRenderContext screen(Minecraft minecraft, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight, float intensity) {
        return new VisualRenderContext(VisualLayerType.SCREEN, minecraft, guiGraphics, null, null, null, null, screenWidth, screenHeight, partialTick, intensity);
    }

    public static VisualRenderContext world(Minecraft minecraft, PoseStack poseStack, RenderLevelStageEvent.Stage stage, float partialTick, float intensity) {
        return new VisualRenderContext(VisualLayerType.WORLD, minecraft, null, poseStack, null, null, stage, 0, 0, partialTick, intensity);
    }

    public static VisualRenderContext entity(Minecraft minecraft, LivingEntity entity, RenderLivingEvent<?, ?> event, float partialTick, float intensity) {
        return new VisualRenderContext(VisualLayerType.ENTITY, minecraft, null, null, entity, event, null, 0, 0, partialTick, intensity);
    }

    public VisualLayerType layer() {
        return layer;
    }

    public Minecraft minecraft() {
        return minecraft;
    }

    public GuiGraphics guiGraphics() {
        return guiGraphics;
    }

    public PoseStack poseStack() {
        return poseStack;
    }

    public LivingEntity entity() {
        return entity;
    }

    public RenderLivingEvent<?, ?> entityEvent() {
        return entityEvent;
    }

    public RenderLevelStageEvent.Stage stage() {
        return stage;
    }

    public int screenWidth() {
        return screenWidth;
    }

    public int screenHeight() {
        return screenHeight;
    }

    public float partialTick() {
        return partialTick;
    }

    public float intensity() {
        return intensity;
    }
}
