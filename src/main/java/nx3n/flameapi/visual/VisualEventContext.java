package nx3n.flameapi.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class VisualEventContext {
    private final VisualEventType type;
    private final VisualScene scene;
    private final Minecraft minecraft;
    private final Player player;
    private final Level level;
    private final float value;
    private final float previousValue;

    public VisualEventContext(VisualEventType type, VisualScene scene) {
        this(type, scene, Minecraft.getInstance(), Minecraft.getInstance().player, Minecraft.getInstance().level, 0f, 0f);
    }

    public VisualEventContext(VisualEventType type, VisualScene scene, Minecraft minecraft, Player player, Level level, float value, float previousValue) {
        this.type = type;
        this.scene = scene;
        this.minecraft = minecraft;
        this.player = player;
        this.level = level;
        this.value = value;
        this.previousValue = previousValue;
    }

    public VisualEventType type() {
        return type;
    }

    public VisualScene scene() {
        return scene;
    }

    public Minecraft minecraft() {
        return minecraft;
    }

    public Player player() {
        return player;
    }

    public Level level() {
        return level;
    }

    public float value() {
        return value;
    }

    public float previousValue() {
        return previousValue;
    }
}
