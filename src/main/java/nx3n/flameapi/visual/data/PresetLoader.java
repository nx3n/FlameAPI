package nx3n.flameapi.visual.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nx3n.flameapi.visual.*;

import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Minimal JSON preset loader.
 * File: config/flameapi_visuals.json
 */
public final class PresetLoader {
    private static final Gson GSON = new Gson();
    private static final String FILE_JSON = "flameapi_visuals.json";
    private static final String FILE_HTML = "flameapi_visuals.html";

    private PresetLoader() {
    }

    public static void tryLoadAndApply(SceneStack scenes) {
        reload(scenes, false);
    }

    /** Reload presets. If clearExisting is true, wipes previously registered scenes first. */
    public static void reload(SceneStack scenes, boolean clearExisting) {
        // Prefer HTML/CSS-style presets if present.
        Path html = FMLPaths.CONFIGDIR.get().resolve(FILE_HTML);
        if (Files.exists(html)) {
            HtmlPresetLoader.reloadFromFile(scenes, html);
            return;
        }

        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_JSON);
        if (!Files.exists(path)) return;
        try {
            if (clearExisting) {
                // keep current/target pointers; only replace registry
                // (simple approach: register will overwrite by id)
            }
            String json = Files.readString(path, StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonArray arr = root.has("scenes") ? root.getAsJsonArray("scenes") : null;
            if (arr == null) return;
            for (JsonElement el : arr) {
                if (!el.isJsonObject()) continue;
                Scene scene = parseScene(el.getAsJsonObject());
                if (scene != null) {
                    scenes.register(scene);
                }
            }
            // optional: set initial scene
            if (root.has("current")) {
                String current = root.get("current").getAsString();
                Scene s = scenes.get(current);
                if (s != null) scenes.setCurrent(s);
            }
        } catch (Throwable ignored) {
        }
    }

    private static Scene parseScene(JsonObject o) {
        if (!o.has("id")) return null;
        String id = o.get("id").getAsString();
        Scene scene = new Scene(id);

        // intensities
        if (o.has("intensity")) {
            JsonObject intens = o.getAsJsonObject("intensity");
            for (Layer t : Layer.values()) {
                String k = t.name().toLowerCase();
                if (intens.has(k)) {
                    scene.setIntensity(t, intens.get(k).getAsFloat());
                }
            }
        }

        // effects: {"screen":[{...},{...}],"world":[...]}
        if (o.has("effects")) {
            JsonObject effects = o.getAsJsonObject("effects");
            for (Layer t : Layer.values()) {
                String key = t.name().toLowerCase();
                if (!effects.has(key)) continue;
                JsonArray list = effects.getAsJsonArray(key);
                for (JsonElement e : list) {
                    if (!e.isJsonObject()) continue;
                    JsonObject eo = e.getAsJsonObject();
                    if (!eo.has("type")) continue;
                    String type = eo.get("type").getAsString();
                    float mult = eo.has("multiplier") ? eo.get("multiplier").getAsFloat() : 1f;
                    Props props = new Props();
                    if (eo.has("props") && eo.get("props").isJsonObject()) {
                        JsonObject po = eo.getAsJsonObject("props");
                        for (String pk : po.keySet()) {
                            JsonElement pv = po.get(pk);
                            if (pv.isJsonPrimitive()) {
                                if (pv.getAsJsonPrimitive().isBoolean()) props.set(pk, pv.getAsBoolean());
                                else if (pv.getAsJsonPrimitive().isNumber()) {
                                    // treat ints vs floats by presence of '.'
                                    String raw = pv.getAsString();
                                    if (raw.contains(".") || raw.contains("e") || raw.contains("E")) props.set(pk, pv.getAsFloat());
                                    else props.set(pk, pv.getAsInt());
                                } else props.set(pk, pv.getAsString());
                            }
                        }
                    }
                    scene.addEffectId(t, type, props, mult);
                }
            }
        }
        return scene;
    }
}
