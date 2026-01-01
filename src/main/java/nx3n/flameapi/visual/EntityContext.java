package nx3n.flameapi.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RenderLivingEvent;

public final class EntityContext {
    private final Minecraft minecraft;
    private final LivingEntity entity;
    private final RenderLivingEvent<?, ?> event;
    private final float partialTick;
    private final float intensity;

    public EntityContext(Minecraft minecraft, LivingEntity entity, RenderLivingEvent<?, ?> event, float partialTick, float intensity) {
        this.minecraft = minecraft;
        this.entity = entity;
        this.event = event;
        this.partialTick = partialTick;
        this.intensity = intensity;
    }

    public Minecraft minecraft() {
        return minecraft;
    }

    public LivingEntity entity() {
        return entity;
    }

    public RenderLivingEvent<?, ?> event() {
        return event;
    }

    public float partialTick() {
        return partialTick;
    }

    public float intensity() {
        return intensity;
    }
}
