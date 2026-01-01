package nx3n.flameapi.visual;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class VisualSceneSerializer {
    private VisualSceneSerializer() {
    }

    public static VisualScene fromJson(String json) {
        VisualEffectRegistry.bootstrap();
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        String id = getString(root, "scene", getString(root, "id", "scene"));
        VisualScene scene = new VisualScene(id);
        applySceneProps(scene, root);
        JsonArray effects = root.has("effects") ? root.getAsJsonArray("effects") : new JsonArray();
        for (JsonElement element : effects) {
            if (!element.isJsonObject()) {
                continue;
            }
            VisualEffect effect = parseEffect(element.getAsJsonObject());
            if (effect != null) {
                scene.addEffect(effect);
            }
        }
        return scene;
    }

    public static VisualScene fromResource(ResourceLocation resourceLocation) throws IOException {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.getResourceManager() == null) {
            throw new IOException("Resource manager not available");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
            minecraft.getResourceManager().getResource(resourceLocation).orElseThrow().open(),
            StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
            return fromJson(builder.toString());
        }
    }

    private static void applySceneProps(VisualScene scene, JsonObject root) {
        if (root.has("intensities") && root.get("intensities").isJsonObject()) {
            JsonObject intensities = root.getAsJsonObject("intensities");
            for (VisualLayerType layer : VisualLayerType.values()) {
                String key = layer.name().toLowerCase();
                if (intensities.has(key)) {
                    scene.setIntensity(layer, intensities.get(key).getAsFloat());
                }
            }
        }
        if (root.has("props") && root.get("props").isJsonObject()) {
            JsonObject props = root.getAsJsonObject("props");
            for (String key : props.keySet()) {
                JsonElement value = props.get(key);
                if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
                    scene.props().set(key, value.getAsFloat());
                } else if (value.isJsonPrimitive()) {
                    scene.props().set(key, value.getAsString());
                }
            }
        }
        if (root.has("timeline") && root.get("timeline").isJsonArray()) {
            parseTimeline(scene.timeline(), root.getAsJsonArray("timeline"));
        }
    }

    private static VisualEffect parseEffect(JsonObject object) {
        String type = getString(object, "type", "");
        if (type.isEmpty()) {
            return null;
        }
        VisualEffectConfig config = new VisualEffectConfig(type);
        if (object.has("layer")) {
            config.layer(parseLayer(object.get("layer").getAsString()));
        }
        if (object.has("lifetime")) {
            config.lifetime(object.get("lifetime").getAsFloat());
        }
        if (object.has("loop")) {
            config.loop(object.get("loop").getAsBoolean());
        }
        if (object.has("props") && object.get("props").isJsonObject()) {
            parseProps(config.props(), object.getAsJsonObject("props"));
        }
        for (String key : object.keySet()) {
            if (isReservedKey(key)) {
                continue;
            }
            JsonElement value = object.get(key);
            if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
                config.props().set(key, value.getAsFloat());
            } else if (value.isJsonPrimitive()) {
                config.props().set(key, value.getAsString());
            }
        }
        if (object.has("animations") && object.get("animations").isJsonArray()) {
            parseAnimations(config, object.getAsJsonArray("animations"));
        }
        if (object.has("timeline") && object.get("timeline").isJsonArray()) {
            parseTimeline(config.timeline(), object.getAsJsonArray("timeline"));
        }
        return VisualEffectRegistry.create(type, config);
    }

    private static void parseProps(VisualProps props, JsonObject json) {
        for (String key : json.keySet()) {
            JsonElement value = json.get(key);
            if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
                props.set(key, value.getAsFloat());
            } else if (value.isJsonPrimitive()) {
                props.set(key, value.getAsString());
            }
        }
    }

    private static void parseAnimations(VisualEffectConfig config, JsonArray animations) {
        for (JsonElement element : animations) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject anim = element.getAsJsonObject();
            String property = getString(anim, "property", "");
            if (property.isEmpty()) {
                continue;
            }
            VisualEasing easing = parseEasing(getString(anim, "easing", "linear"));
            float from = getFloat(anim, "from", 0f);
            float to = getFloat(anim, "to", 0f);
            float duration = getFloat(anim, "duration", 0.3f);
            float delay = getFloat(anim, "delay", 0f);
            boolean loop = getBoolean(anim, "loop", false);
            config.animations().add(new VisualAnimation(property, easing, from, to, duration, delay, loop));
        }
    }

    private static void parseTimeline(VisualTimeline timeline, JsonArray frames) {
        for (JsonElement element : frames) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject frame = element.getAsJsonObject();
            float time = getFloat(frame, "time", 0f);
            String property = getString(frame, "property", "");
            if (property.isEmpty()) {
                continue;
            }
            float value = getFloat(frame, "value", 0f);
            VisualEasing easing = parseEasing(getString(frame, "easing", "linear"));
            timeline.at(time).set(property, value, easing);
        }
    }

    private static VisualLayerType parseLayer(String value) {
        for (VisualLayerType layer : VisualLayerType.values()) {
            if (layer.name().equalsIgnoreCase(value)) {
                return layer;
            }
        }
        return VisualLayerType.SCREEN;
    }

    private static VisualEasing parseEasing(String value) {
        for (VisualEasing easing : VisualEasing.values()) {
            if (easing.name().equalsIgnoreCase(value)) {
                return easing;
            }
        }
        return VisualEasing.LINEAR;
    }

    private static boolean isReservedKey(String key) {
        return key.equals("type") || key.equals("layer") || key.equals("props") || key.equals("animations")
            || key.equals("timeline") || key.equals("lifetime") || key.equals("loop");
    }

    private static String getString(JsonObject object, String key, String fallback) {
        return object.has(key) ? object.get(key).getAsString() : fallback;
    }

    private static float getFloat(JsonObject object, String key, float fallback) {
        return object.has(key) ? object.get(key).getAsFloat() : fallback;
    }

    private static boolean getBoolean(JsonObject object, String key, boolean fallback) {
        return object.has(key) ? object.get(key).getAsBoolean() : fallback;
    }
}
