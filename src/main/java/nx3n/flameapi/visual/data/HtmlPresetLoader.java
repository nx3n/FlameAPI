package nx3n.flameapi.visual.data;

import nx3n.flameapi.visual.*;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML/CSS-style preset loader.
 *
 * File: config/flameapi_visuals.html
 *
 * Example:
 * <pre>
 * <style>
 * vignette { priority: 10; size: 0.22; color: #AA000000; }
 * .combat vignette { size: 0.30; }
 * </style>
 *
 * <scene id="default">
 *   <screen>
 *     <vignette size="0.22" color="#AA000000" />
 *     <solid_overlay color="#33000000" />
 *   </screen>
 * </scene>
 * </pre>
 *
 * Notes:
 * - this is intentionally tiny, not a full HTML parser
 * - only tags we care about are read
 */
public final class HtmlPresetLoader {
    private HtmlPresetLoader() {}

    private static final Pattern STYLE = Pattern.compile("<style>(.*?)</style>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static final Pattern SCENE = Pattern.compile("<scene\\s+([^>]*)>(.*?)</scene>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static final Pattern LAYER_BLOCK = Pattern.compile("<(screen|hud|world|entity)\\b[^>]*>(.*?)</\\1>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static final Pattern EMPTY_TAG = Pattern.compile("<([a-zA-Z0-9_:-]+)\\b([^>]*)/>");

    public static void reloadFromFile(SceneStack scenes, Path path) {
        try {
            String html = Files.readString(path, StandardCharsets.UTF_8);
            parseAndApply(scenes, html);
        } catch (Throwable ignored) {
        }
    }

    static void parseAndApply(SceneStack scenes, String html) {
        if (html == null) return;
        EffectRegistry.bootstrapDefaults();

        String cssText = extractStyle(html);
        List<Css.Rule> rules = Css.parse(cssText);

        Matcher sm = SCENE.matcher(html);
        while (sm.find()) {
            String sceneAttrs = sm.group(1);
            String sceneBody = sm.group(2);

            Map<String, String> sceneA = parseAttrs(sceneAttrs);
            String id = sceneA.getOrDefault("id", "").trim();
            if (id.isEmpty()) continue;

            String clazz = sceneA.getOrDefault("class", "").trim();
            String sceneClass = clazz.isEmpty() ? null : clazz;

            Scene scene = new Scene(id);

            // Optional intensity="screen:1 world:0.5"
            if (sceneA.containsKey("intensity")) {
                applyIntensity(scene, sceneA.get("intensity"));
            }

            Matcher lm = LAYER_BLOCK.matcher(sceneBody);
            while (lm.find()) {
                String layerName = lm.group(1).toLowerCase(Locale.ROOT);
                String layerBody = lm.group(2);
                Layer layer = switch (layerName) {
                    case "hud" -> Layer.HUD;
                    case "world" -> Layer.WORLD;
                    case "entity" -> Layer.ENTITY;
                    default -> Layer.SCREEN;
                };

                // Effects are self-closing tags inside the layer.
                Matcher em = EMPTY_TAG.matcher(layerBody);
                while (em.find()) {
                    String tag = em.group(1);
                    String attrs = em.group(2);
                    if (tag == null) continue;

                    String type = tag.trim();
                    if (type.isEmpty()) continue;

                    Props props = new Props();
                    Map<String, String> a = parseAttrs(attrs);

                    // Apply CSS defaults first, then override by explicit attributes.
                    applyCssDefaults(props, rules, sceneClass, type);
                    applyAttrsToProps(props, a);

                    float mult = 1f;
                    if (a.containsKey("multiplier")) {
                        mult = parseFloat(a.get("multiplier"), 1f);
                    }

                    scene.addEffectId(layer, type, props, mult);
                }
            }

            scenes.register(scene);
        }

        // Optional: <set current="default"/>
        String current = findAttrSingle(html, "set", "current");
        if (current != null) {
            Scene s = scenes.get(current);
            if (s != null) scenes.setCurrent(s);
        }
    }

    private static void applyCssDefaults(Props props, List<Css.Rule> rules, String sceneClass, String tag) {
        for (Css.Rule r : rules) {
            if (!r.tag().equalsIgnoreCase(tag)) continue;
            if (r.sceneClass() != null) {
                if (sceneClass == null) continue;
                if (!r.sceneClass().equalsIgnoreCase(sceneClass)) continue;
            }
            for (Map.Entry<String, String> e : r.props().entrySet()) {
                setPropFromString(props, e.getKey(), e.getValue());
            }
        }
    }

    private static String extractStyle(String html) {
        Matcher m = STYLE.matcher(html);
        return m.find() ? m.group(1) : "";
    }

    private static void applyIntensity(Scene scene, String text) {
        // "screen:1 world:0.5" or "screen=1,world=0.5"
        String t = text.replace(',', ' ').replace(';', ' ');
        for (String part : t.split("\\s+")) {
            if (part.isBlank()) continue;
            String[] kv = part.split("[:=]", 2);
            if (kv.length != 2) continue;
            String k = kv[0].trim().toLowerCase(Locale.ROOT);
            float v = parseFloat(kv[1].trim(), 0f);
            for (Layer l : Layer.values()) {
                if (l.name().toLowerCase(Locale.ROOT).equals(k)) {
                    scene.setIntensity(l, v);
                }
            }
        }
    }

    private static void applyAttrsToProps(Props props, Map<String, String> a) {
        for (Map.Entry<String, String> e : a.entrySet()) {
            String k = e.getKey();
            if (k == null) continue;
            String v = e.getValue();
            if (v == null) continue;
            if (k.equalsIgnoreCase("id") || k.equalsIgnoreCase("class")) continue;
            setPropFromString(props, k, v);
        }
    }

    private static void setPropFromString(Props props, String key, String raw) {
        if (key == null || raw == null) return;
        String v = raw.trim();

        // bool
        if (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false")) {
            props.set(key, Boolean.parseBoolean(v));
            return;
        }

        // hex colors
        if (v.startsWith("#")) {
            String hex = v.substring(1);
            try {
                long n = Long.parseLong(hex, 16);
                if (hex.length() <= 6) {
                    // #RRGGBB
                    n |= 0xFF000000L;
                }
                props.set(key, (int) n);
                return;
            } catch (NumberFormatException ignored) {
            }
        }

        // number
        if (v.matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?")) {
            if (v.contains(".") || v.contains("e") || v.contains("E")) props.set(key, parseFloat(v, 0f));
            else {
                try { props.set(key, Integer.parseInt(v)); } catch (NumberFormatException ex) { props.set(key, parseFloat(v, 0f)); }
            }
            return;
        }

        props.set(key, v);
    }

    private static float parseFloat(String s, float def) {
        try { return Float.parseFloat(s.trim()); } catch (Throwable t) { return def; }
    }

    private static Map<String, String> parseAttrs(String attrs) {
        Map<String, String> m = new LinkedHashMap<>();
        if (attrs == null) return m;
        // key="value" | key='value' | key=value
        Matcher a = Pattern.compile("([a-zA-Z0-9_:-]+)\\s*=\\s*(\"([^\"]*)\"|'([^']*)'|([^\\s>]+))").matcher(attrs);
        while (a.find()) {
            String k = a.group(1);
            String v = a.group(3);
            if (v == null) v = a.group(4);
            if (v == null) v = a.group(5);
            m.put(k, v);
        }
        return m;
    }

    private static String findAttrSingle(String html, String tag, String attr) {
        Pattern p = Pattern.compile("<" + Pattern.quote(tag) + "\\b([^>]*)/>", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(html);
        if (!m.find()) return null;
        Map<String, String> a = parseAttrs(m.group(1));
        return a.get(attr);
    }
}
