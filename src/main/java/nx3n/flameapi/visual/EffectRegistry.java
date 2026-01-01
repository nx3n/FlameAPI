package nx3n.flameapi.visual;

import nx3n.flameapi.visual.effects.AuroraPulseEffect;
import nx3n.flameapi.visual.effects.Model3DEffect;
import nx3n.flameapi.visual.effects.ShaderPassEffect;
import nx3n.flameapi.visual.effects.SolidOverlayEffect;
import nx3n.flameapi.visual.effects.VignetteEffect;
import nx3n.flameapi.visual.gfx.BasicShader;
import nx3n.flameapi.visual.gfx.ItemModel;
import nx3n.flameapi.visual.gfx.ObjMeshModel;
import nx3n.flameapi.visual.gfx.Models;
import nx3n.flameapi.visual.gfx.Shaders;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Registry for data-driven effects (id -> factory). */
public final class EffectRegistry {
    private static final Map<String, EffectFactory> FACTORIES = new ConcurrentHashMap<>();
    private static boolean bootstrapped;

    private EffectRegistry() {
    }

    public static void register(String id, EffectFactory factory) {
        FACTORIES.put(id, factory);
    }

    public static EffectFactory factory(String id) {
        return FACTORIES.get(id);
    }

    /** Registers a minimal set of built-in effect types. Safe to call multiple times. */
    public static void bootstrapDefaults() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;

        // Legacy showcase effect
        register("aurora_pulse", props -> (Effect<?>) LegacyAdapters.screen(new AuroraPulseEffect()));

        // New basic building blocks for JSON/editor
        register("solid_overlay", props -> new SolidOverlayEffect(props));
        register("vignette", props -> new VignetteEffect(props));

        // Shader and model processing building blocks
        register("shader_pass", props -> new ShaderPassEffect(props));
        register("model3d", props -> new Model3DEffect(props));

        // Built-in shader/model ids
        Shaders.register("basic", p -> new BasicShader());
        Models.register("item", p -> new ItemModel(p));
        // High-poly support: render arbitrary OBJ meshes from resources (cached).
        // Usage via model3d: {"model":"obj","obj":"modid:models/mesh.obj","texture":"modid:textures/mesh.png"}
        Models.register("obj", p -> ObjMeshModel.fromProps(p));
    }
}
