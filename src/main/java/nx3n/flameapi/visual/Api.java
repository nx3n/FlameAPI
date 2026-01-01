package nx3n.flameapi.visual;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import nx3n.flameapi.visual.data.PresetLoader;
import nx3n.flameapi.visual.data.ScriptSupport;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class Api {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<HudLayer> HUD_LAYERS = new CopyOnWriteArrayList<>();
    /** Legacy raw callbacks (kept for compatibility). Prefer {@link #registerEffect(Layer, Effect)}. */
    private static final List<WorldEffect> WORLD_EFFECTS = new CopyOnWriteArrayList<>();
    private static final List<EntityEffect> ENTITY_EFFECTS = new CopyOnWriteArrayList<>();
    private static final List<ScreenEffect> SCREEN_EFFECTS = new CopyOnWriteArrayList<>();

    /** New effect pipelines (ordered, tickable, configurable). */
    private static final EffectPipeline<HudContext> HUD_PIPELINE = new EffectPipeline<>();
    private static final EffectPipeline<ScreenContext> SCREEN_PIPELINE = new EffectPipeline<>();
    private static final EffectPipeline<WorldContext> WORLD_PIPELINE = new EffectPipeline<>();
    private static final EffectPipeline<EntityContext> ENTITY_PIPELINE = new EffectPipeline<>();

    /** Trigger/events bus for data-driven scenes and effects. */
    private static final TriggerBus TRIGGERS = new TriggerBus();
    private static final SceneStack SCENES = new SceneStack();
    /** High-level facade (builder-style) for authoring scenes/effects without touching low-level MC rendering APIs. */
    private static final Fx FX = new Fx(SCENES, TRIGGERS);
    private static boolean bootstrapped;

    private Api() {
    }

    public static void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;
        // Register built-in, data-driven effect types.
        EffectRegistry.bootstrapDefaults();
        // Load config presets if present.
        PresetLoader.tryLoadAndApply(SCENES);
        // Optional: run <script> blocks from HTML presets (needs a JS engine on the classpath).
        ScriptSupport.tryRunHtmlScripts(FX, TRIGGERS);
        LOGGER.info("FlameAPI Visuals ready: HUD {}, World {}, Entity {}, Screen {}", HUD_LAYERS.size(), WORLD_EFFECTS.size(), ENTITY_EFFECTS.size(), SCREEN_EFFECTS.size());
    }

    public static void registerHudLayer(HudLayer layer) {
        HUD_LAYERS.add(layer);
    }

    public static void registerWorldEffect(WorldEffect effect) {
        WORLD_EFFECTS.add(effect);
        // Also mirror into the new pipeline so ordering/ticking works.
        WORLD_PIPELINE.add(LegacyAdapters.world(effect));
    }

    public static void registerEntityEffect(EntityEffect effect) {
        ENTITY_EFFECTS.add(effect);
        ENTITY_PIPELINE.add(LegacyAdapters.entity(effect));
    }

    public static void registerScreenEffect(ScreenEffect effect) {
        SCREEN_EFFECTS.add(effect);
        SCREEN_PIPELINE.add(LegacyAdapters.screen(effect));
    }

    /** Preferred registration API (effect objects with priority/props/ticking). */
    public static void registerEffect(Layer layer, Effect effect) {
        switch (layer) {
            case HUD -> HUD_PIPELINE.add(effect);
            case SCREEN -> SCREEN_PIPELINE.add(effect);
            case WORLD -> WORLD_PIPELINE.add(effect);
            case ENTITY -> ENTITY_PIPELINE.add(effect);
        }
    }

    public static TriggerBus triggers() {
        return TRIGGERS;
    }

    /** High-level authoring API (builders, presets, triggers). */
    public static Fx fx() {
        return FX;
    }

    public static SceneStack scenes() {
        return SCENES;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        float dt = 1f / 20f;
        SCENES.tick(dt);
        HUD_PIPELINE.tick(dt);
        SCREEN_PIPELINE.tick(dt);
        WORLD_PIPELINE.tick(dt);
        ENTITY_PIPELINE.tick(dt);
        TRIGGERS.tick(dt);
    }

    @SubscribeEvent
    public static void onHudRender(RenderGuiEvent.Post event) {
        if (HUD_LAYERS.isEmpty() && SCREEN_PIPELINE.isEmpty()) {
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
        float hudIntensity = resolveIntensity(Layer.HUD);
        if (hudIntensity > 0f) {
            HudContext context = new HudContext(minecraft, guiGraphics, partialTick, screenWidth, screenHeight, hudIntensity);
            for (HudLayer layer : HUD_LAYERS) {
                layer.render(context);
            }
        }
        float screenIntensity = resolveIntensity(Layer.SCREEN);
        if (screenIntensity > 0f) {
            ScreenContext screenContext = new ScreenContext(minecraft, guiGraphics, partialTick, screenWidth, screenHeight, screenIntensity);
            renderSceneAware(Layer.SCREEN, screenContext, SCREEN_PIPELINE);
        }
    }

    @SubscribeEvent
    public static void onWorldRender(RenderLevelStageEvent event) {
        if (WORLD_PIPELINE.isEmpty()) {
            return;
        }
        float intensity = resolveIntensity(Layer.WORLD);
        if (intensity <= 0f) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        WorldContext context = new WorldContext(minecraft, event.getPoseStack(), event.getStage(), event.getPartialTick(), intensity);
        renderSceneAware(Layer.WORLD, context, WORLD_PIPELINE);
    }

    @SubscribeEvent
    public static void onEntityRender(RenderLivingEvent.Post<?, ?> event) {
        if (ENTITY_PIPELINE.isEmpty()) {
            return;
        }
        float intensity = resolveIntensity(Layer.ENTITY);
        if (intensity <= 0f) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        EntityContext context = new EntityContext(minecraft, event.getEntity(), event, event.getPartialTick(), intensity);
        renderSceneAware(Layer.ENTITY, context, ENTITY_PIPELINE);
    }

    // --- Triggers / events -------------------------------------------------

    @SubscribeEvent
    public static void onClientDamage(LivingHurtEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) {
            return;
        }
        if (event.getEntity() != mc.player) {
            return;
        }
        TRIGGERS.emit(Trigger.DAMAGE, new TriggerBus.DamagePayload(event.getAmount(), event.getSource()));
    }

    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        TRIGGERS.emit(Trigger.SCREEN_OPEN, new TriggerBus.ScreenPayload(event.getNewScreen()));
    }

    @SubscribeEvent
    public static void onScreenClose(ScreenEvent.Closing event) {
        TRIGGERS.emit(Trigger.SCREEN_CLOSE, new TriggerBus.ScreenPayload(event.getScreen()));
    }

    private static float resolveIntensity(Layer type) {
        Scene current = SCENES.current();
        Scene target = SCENES.target();
        float currentIntensity = current.intensity(type);
        if (target == null) {
            return currentIntensity;
        }
        float blend = SCENES.blendFactor();
        float targetIntensity = target.intensity(type);
        return currentIntensity + (targetIntensity - currentIntensity) * blend;
    }

    private static <C> void renderSceneAware(Layer layer, C baseContext, EffectPipeline<C> pipeline) {
        Scene current = SCENES.current();
        Scene target = SCENES.target();

        // If scenes declare explicit effect stacks, render those; else render the global pipeline.
        EffectStack currentStack = current.effectStack(layer);
        EffectStack targetStack  = (target == null) ? null : target.effectStack(layer);

        if ((currentStack == null || currentStack.isEmpty()) && (targetStack == null || targetStack.isEmpty())) {
            pipeline.render(baseContext);
            return;
        }

        float blend = SCENES.blendFactor();
        if (currentStack != null && !currentStack.isEmpty()) {
            currentStack.render(baseContext, 1f - blend);
        }
        if (targetStack != null && !targetStack.isEmpty()) {
            targetStack.render(baseContext, blend);
        }
    }
}
