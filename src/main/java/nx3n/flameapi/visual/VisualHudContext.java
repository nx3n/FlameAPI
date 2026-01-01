package nx3n.flameapi.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class VisualHudContext {
    private final Minecraft minecraft;
    private final GuiGraphics guiGraphics;
    private final float partialTick;
    private final int screenWidth;
    private final int screenHeight;
    private final float intensity;

    public VisualHudContext(Minecraft minecraft, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight, float intensity) {
        this.minecraft = minecraft;
        this.guiGraphics = guiGraphics;
        this.partialTick = partialTick;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.intensity = intensity;
    }

    public Minecraft minecraft() {
        return minecraft;
    }

    public GuiGraphics guiGraphics() {
        return guiGraphics;
    }

    public float partialTick() {
        return partialTick;
    }

    public int screenWidth() {
        return screenWidth;
    }

    public int screenHeight() {
        return screenHeight;
    }

    public float intensity() {
        return intensity;
    }
}
