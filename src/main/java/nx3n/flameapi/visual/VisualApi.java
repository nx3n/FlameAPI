package nx3n.flameapi.visual;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class VisualApi {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<VisualHudLayer> HUD_LAYERS = new CopyOnWriteArrayList<>();
    private static final List<VisualWorldEffect> WORLD_EFFECTS = new CopyOnWriteArrayList<>();
    private static final List<VisualEntityEffect> ENTITY_EFFECTS = new CopyOnWriteArrayList<>();
    private static final List<VisualScreenEffect> SCREEN_EFFECTS = new CopyOnWriteArrayList<>();
    private static final VisualSceneStack SCENES = new VisualSceneStack();
    private static boolean bootstrapped;

    private VisualApi() {
    }

    public static void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;
        LOGGER.info("FlameAPI Visuals ready: HUD {}, World {}, Entity {}, Screen {}", HUD_LAYERS.size(), WORLD_EFFECTS.size(), ENTITY_EFFECTS.size(), SCREEN_EFFECTS.size());
    }

    public static void registerHudLayer(VisualHudLayer layer) {
        HUD_LAYERS.add(layer);
    }

    public static void registerWorldEffect(VisualWorldEffect effect) {
        WORLD_EFFECTS.add(effect);
    }

    public static void registerEntityEffect(VisualEntityEffect effect) {
        ENTITY_EFFECTS.add(effect);
    }

    public static void registerScreenEffect(VisualScreenEffect effect) {
        SCREEN_EFFECTS.add(effect);
    }

    public static VisualSceneStack scenes() {
        return SCENES;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        SCENES.tick(1f / 20f);
    }

    @SubscribeEvent
    public static void onHudRender(RenderGuiEvent.Post event) {
        if (HUD_LAYERS.isEmpty() && SCREEN_EFFECTS.isEmpty()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return;
        }
        GuiGraphics guiGraphics = event.getGuiGraphics();
        float partialTick = event.getPartialTick();
        int screenWidth = event.getWindow().getGuiScaledWidth();
        int screenHeight = event.getWindow().getGuiScaledHeight();
        float hudIntensity = resolveIntensity(VisualLayerType.HUD);
        if (hudIntensity > 0f) {
            VisualHudContext context = new VisualHudContext(minecraft, guiGraphics, partialTick, screenWidth, screenHeight, hudIntensity);
            for (VisualHudLayer layer : HUD_LAYERS) {
                layer.render(context);
            }
        }
        float screenIntensity = resolveIntensity(VisualLayerType.SCREEN);
        if (screenIntensity > 0f) {
            VisualScreenContext screenContext = new VisualScreenContext(minecraft, guiGraphics, partialTick, screenWidth, screenHeight, screenIntensity);
            for (VisualScreenEffect effect : SCREEN_EFFECTS) {
                effect.render(screenContext);
            }
        }
    }

    @SubscribeEvent
    public static void onWorldRender(RenderLevelStageEvent event) {
        if (WORLD_EFFECTS.isEmpty()) {
            return;
        }
        float intensity = resolveIntensity(VisualLayerType.WORLD);
        if (intensity <= 0f) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        VisualWorldContext context = new VisualWorldContext(minecraft, event.getPoseStack(), event.getStage(), event.getPartialTick(), intensity);
        for (VisualWorldEffect effect : WORLD_EFFECTS) {
            effect.render(context);
        }
    }

    @SubscribeEvent
    public static void onEntityRender(RenderLivingEvent.Post<?, ?> event) {
        if (ENTITY_EFFECTS.isEmpty()) {
            return;
        }
        float intensity = resolveIntensity(VisualLayerType.ENTITY);
        if (intensity <= 0f) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        VisualEntityContext context = new VisualEntityContext(minecraft, event.getEntity(), event, event.getPartialTick(), intensity);
        for (VisualEntityEffect effect : ENTITY_EFFECTS) {
            effect.render(context);
        }
    }

    private static float resolveIntensity(VisualLayerType type) {
        VisualScene current = SCENES.current();
        VisualScene target = SCENES.target();
        float currentIntensity = current.intensity(type);
        if (target == null) {
            return currentIntensity;
        }
        float blend = SCENES.blendFactor();
        float targetIntensity = target.intensity(type);
        return currentIntensity + (targetIntensity - currentIntensity) * blend;
    }
}
