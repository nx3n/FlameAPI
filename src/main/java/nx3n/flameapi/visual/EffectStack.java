package nx3n.flameapi.visual;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Per-scene explicit effect stack. Each entry has an additional multiplier
 * (useful when blending scenes or layering effects).
 */
public final class EffectStack<C> {
    public static final class Entry<C> {
        public final Effect<C> effect;
        public final float multiplier;

        public Entry(Effect<C> effect, float multiplier) {
            this.effect = effect;
            this.multiplier = multiplier;
        }
    }

    private final List<Entry<C>> entries = new ArrayList<>();

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public EffectStack<C> add(Effect<C> effect) {
        return add(effect, 1f);
    }

    public EffectStack<C> add(Effect<C> effect, float multiplier) {
        entries.add(new Entry<>(effect, multiplier));
        entries.sort(Comparator.comparingInt(e -> e.effect.priority()));
        return this;
    }

    /** Render with additional sceneWeight (e.g., 1-blend or blend). */
    public void render(C baseContext, float sceneWeight) {
        if (sceneWeight <= 0f) {
            return;
        }
        if (baseContext instanceof HudContext hud) {
            for (Entry<C> e : entries) {
                if (!e.effect.enabled()) continue;
                float i = hud.intensity() * sceneWeight * e.multiplier;
                HudContext ctx = new HudContext(hud.minecraft(), hud.guiGraphics(), hud.partialTick(), hud.screenWidth(), hud.screenHeight(), i);
                ((Effect<HudContext>) e.effect).render(ctx);
            }
            return;
        }
        if (baseContext instanceof ScreenContext scr) {
            for (Entry<C> e : entries) {
                if (!e.effect.enabled()) continue;
                float i = scr.intensity() * sceneWeight * e.multiplier;
                ScreenContext ctx = new ScreenContext(scr.minecraft(), scr.guiGraphics(), scr.partialTick(), scr.screenWidth(), scr.screenHeight(), i);
                ((Effect<ScreenContext>) e.effect).render(ctx);
            }
            return;
        }
        if (baseContext instanceof WorldContext world) {
            for (Entry<C> e : entries) {
                if (!e.effect.enabled()) continue;
                float i = world.intensity() * sceneWeight * e.multiplier;
                WorldContext ctx = new WorldContext(world.minecraft(), world.poseStack(), world.stage(), world.partialTick(), i);
                ((Effect<WorldContext>) e.effect).render(ctx);
            }
            return;
        }
        if (baseContext instanceof EntityContext ent) {
            for (Entry<C> e : entries) {
                if (!e.effect.enabled()) continue;
                float i = ent.intensity() * sceneWeight * e.multiplier;
                EntityContext ctx = new EntityContext(ent.minecraft(), ent.entity(), ent.event(), ent.partialTick(), i);
                ((Effect<EntityContext>) e.effect).render(ctx);
            }
        }
    }
}
