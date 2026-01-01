package nx3n.flameapi.visual;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
<<<<<<< ours
=======
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
>>>>>>> theirs
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
<<<<<<< ours
=======
import net.minecraftforge.event.entity.living.LivingHurtEvent;
>>>>>>> theirs
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
<<<<<<< ours
=======
    private static ResourceKey<Level> lastDimension;
    private static float lastHealth = -1f;
    private static float lastSpeed = -1f;
>>>>>>> theirs
    private static boolean bootstrapped;

    private VisualApi() {
    }

    public static void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;
<<<<<<< ours
=======
        VisualEffectRegistry.bootstrap();
>>>>>>> theirs
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

<<<<<<< ours
=======
    public static void registerEffect(VisualEffect effect) {
        SCENES.current().addEffect(effect);
    }

    public static void activateScene(VisualScene scene, float blendSeconds) {
        SCENES.blendTo(scene, blendSeconds);
    }

>>>>>>> theirs
    public static VisualSceneStack scenes() {
        return SCENES;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        SCENES.tick(1f / 20f);
<<<<<<< ours
=======
        handlePlayerSignals();
>>>>>>> theirs
    }

    @SubscribeEvent
    public static void onHudRender(RenderGuiEvent.Post event) {
        if (HUD_LAYERS.isEmpty() && SCREEN_EFFECTS.isEmpty()) {
<<<<<<< ours
            return;
=======
            if (SCENES.current().effects().isEmpty() && (SCENES.target() == null || SCENES.target().effects().isEmpty())) {
                return;
            }
>>>>>>> theirs
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
<<<<<<< ours
=======

        float blend = SCENES.blendFactor();
        renderSceneLayer(SCENES.current(), VisualLayerType.HUD, VisualRenderContext.hud(minecraft, guiGraphics, partialTick, screenWidth, screenHeight, 1f), 1f - blend);
        renderSceneLayer(SCENES.current(), VisualLayerType.SCREEN, VisualRenderContext.screen(minecraft, guiGraphics, partialTick, screenWidth, screenHeight, 1f), 1f - blend);
        if (SCENES.target() != null) {
            renderSceneLayer(SCENES.target(), VisualLayerType.HUD, VisualRenderContext.hud(minecraft, guiGraphics, partialTick, screenWidth, screenHeight, 1f), blend);
            renderSceneLayer(SCENES.target(), VisualLayerType.SCREEN, VisualRenderContext.screen(minecraft, guiGraphics, partialTick, screenWidth, screenHeight, 1f), blend);
        }
>>>>>>> theirs
    }

    @SubscribeEvent
    public static void onWorldRender(RenderLevelStageEvent event) {
<<<<<<< ours
        if (WORLD_EFFECTS.isEmpty()) {
            return;
        }
        float intensity = resolveIntensity(VisualLayerType.WORLD);
        if (intensity <= 0f) {
=======
        float intensity = resolveIntensity(VisualLayerType.WORLD);
        if (intensity <= 0f && (SCENES.current().effects().isEmpty() && (SCENES.target() == null || SCENES.target().effects().isEmpty()))) {
>>>>>>> theirs
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        VisualWorldContext context = new VisualWorldContext(minecraft, event.getPoseStack(), event.getStage(), event.getPartialTick(), intensity);
        for (VisualWorldEffect effect : WORLD_EFFECTS) {
            effect.render(context);
        }
<<<<<<< ours
=======

        float blend = SCENES.blendFactor();
        renderSceneLayer(SCENES.current(), VisualLayerType.WORLD, VisualRenderContext.world(minecraft, event.getPoseStack(), event.getStage(), event.getPartialTick(), 1f), 1f - blend);
        if (SCENES.target() != null) {
            renderSceneLayer(SCENES.target(), VisualLayerType.WORLD, VisualRenderContext.world(minecraft, event.getPoseStack(), event.getStage(), event.getPartialTick(), 1f), blend);
        }
>>>>>>> theirs
    }

    @SubscribeEvent
    public static void onEntityRender(RenderLivingEvent.Post<?, ?> event) {
<<<<<<< ours
        if (ENTITY_EFFECTS.isEmpty()) {
            return;
        }
        float intensity = resolveIntensity(VisualLayerType.ENTITY);
        if (intensity <= 0f) {
=======
        float intensity = resolveIntensity(VisualLayerType.ENTITY);
        if (intensity <= 0f && (SCENES.current().effects().isEmpty() && (SCENES.target() == null || SCENES.target().effects().isEmpty()))) {
>>>>>>> theirs
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        VisualEntityContext context = new VisualEntityContext(minecraft, event.getEntity(), event, event.getPartialTick(), intensity);
        for (VisualEntityEffect effect : ENTITY_EFFECTS) {
            effect.render(context);
        }
<<<<<<< ours
=======

        float blend = SCENES.blendFactor();
        renderSceneLayer(SCENES.current(), VisualLayerType.ENTITY, VisualRenderContext.entity(minecraft, event.getEntity(), event, event.getPartialTick(), 1f), 1f - blend);
        if (SCENES.target() != null) {
            renderSceneLayer(SCENES.target(), VisualLayerType.ENTITY, VisualRenderContext.entity(minecraft, event.getEntity(), event, event.getPartialTick(), 1f), blend);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player) || player != Minecraft.getInstance().player) {
            return;
        }
        VisualEventContext context = new VisualEventContext(VisualEventType.DAMAGE, SCENES.current(), Minecraft.getInstance(), player, player.level(), event.getAmount(), 0f);
        dispatchEvent(context);
>>>>>>> theirs
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
<<<<<<< ours
=======

    private static void renderSceneLayer(VisualScene scene, VisualLayerType layer, VisualRenderContext baseContext, float weight) {
        if (scene == null || weight <= 0f || !scene.enabled(layer)) {
            return;
        }
        float intensity = scene.intensity(layer) * weight;
        if (intensity <= 0f) {
            return;
        }
        VisualRenderContext context = baseContext.withIntensity(intensity);
        for (VisualEffect effect : scene.effects()) {
            if (effect.layer() == layer) {
                effect.render(context);
            }
        }
    }

    private static void dispatchEvent(VisualEventContext context) {
        SCENES.current().dispatchEvent(context);
        if (SCENES.target() != null) {
            SCENES.target().dispatchEvent(context);
        }
    }

    private static void handlePlayerSignals() {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) {
            return;
        }
        float healthRatio = player.getHealth() / player.getMaxHealth();
        if (lastHealth >= 0f && lastHealth > 0.25f && healthRatio <= 0.25f) {
            dispatchEvent(new VisualEventContext(VisualEventType.LOW_HEALTH, SCENES.current(), minecraft, player, player.level(), healthRatio, lastHealth));
        }
        lastHealth = healthRatio;

        float speed = (float) player.getDeltaMovement().length();
        if (lastSpeed >= 0f && Math.abs(speed - lastSpeed) > 0.05f) {
            dispatchEvent(new VisualEventContext(VisualEventType.SPEED_CHANGE, SCENES.current(), minecraft, player, player.level(), speed, lastSpeed));
        }
        lastSpeed = speed;

        Level level = player.level();
        if (level != null) {
            ResourceKey<Level> dimension = level.dimension();
            if (lastDimension != null && !lastDimension.equals(dimension)) {
                dispatchEvent(new VisualEventContext(VisualEventType.DIMENSION_CHANGE, SCENES.current(), minecraft, player, level, 0f, 0f));
            }
            lastDimension = dimension;
        }
    }
>>>>>>> theirs
}
