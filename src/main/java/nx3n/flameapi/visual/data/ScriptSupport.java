package nx3n.flameapi.visual.data;

import com.mojang.logging.LogUtils;
import nx3n.flameapi.visual.Fx;
import nx3n.flameapi.visual.Trigger;
import nx3n.flameapi.visual.TriggerBus;
import nx3n.flameapi.visual.script.JsRuntime;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML preset helper: runs <script> blocks.
 *
 * Users can keep presets in one file:
 * - CSS in <style>
 * - visuals in <scene>
 * - logic in <script>
 */
public final class ScriptSupport {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Pattern SCRIPT = Pattern.compile("<script\\b[^>]*>(.*?)</script>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    private ScriptSupport() {}

    public static void tryRunHtmlScripts(Fx fx, TriggerBus triggers) {
        Path html = FMLPaths.CONFIGDIR.get().resolve("flameapi_visuals.html");
        if (!Files.exists(html)) return;
        String text;
        try {
            text = Files.readString(html, StandardCharsets.UTF_8);
        } catch (Throwable t) {
            return;
        }

        Matcher m = SCRIPT.matcher(text);
        if (!m.find()) return;

        JsRuntime runtime = JsRuntime.createDefault();
        if (!runtime.available()) {
            LOGGER.info("FlameAPI JS: no engine found. Add GraalJS/Rhino to enable <script> in presets.");
            return;
        }

        Map<String, Object> globals = new LinkedHashMap<>();
        globals.put("__fx", new ScriptFx(fx));
        globals.put("__on", new ScriptOn(triggers));
        globals.put("Trigger", Trigger.class);

        // Helper layer so presets can write: on('DAMAGE', fn) and fx.blendTo(...)
        runtime.eval("""
            var fx = __fx;
            function on(name, fn) { __on.call(name, fn); }
        """, globals);

        m.reset();
        while (m.find()) {
            String src = m.group(1);
            if (src == null || src.isBlank()) continue;
            runtime.eval(src, globals);
        }
    }

    /** Small facade exposed to JS (stable, minimal surface). */
    public static final class ScriptFx {
        private final Fx fx;
        ScriptFx(Fx fx) { this.fx = fx; }
        public void use(String sceneId) { fx.use(sceneId); }
        public void blendTo(String sceneId, double seconds) { fx.blendTo(sceneId, (float) seconds); }
        public void pulse(String sceneId, double inSeconds, double outSeconds) { fx.pulse(sceneId, (float) inSeconds, (float) outSeconds); }
    }

    /** on('DAMAGE', function(payload) { ... }) */
    public static final class ScriptOn {
        private final TriggerBus triggers;
        ScriptOn(TriggerBus triggers) { this.triggers = triggers; }

        public void call(String triggerName, Object fn) {
            Trigger t = parse(triggerName);
            if (t == null) return;
            triggers.on(t, (tr, payload) -> tryInvoke(fn, payload));
        }

        private static Trigger parse(String name) {
            if (name == null) return null;
            try { return Trigger.valueOf(name.trim().toUpperCase()); } catch (Throwable t) { return null; }
        }

        private static void tryInvoke(Object fn, Object payload) {
            if (fn == null) return;
            // Graal: org.graalvm.polyglot.Value.execute(...)
            try {
                Class<?> valCls = Class.forName("org.graalvm.polyglot.Value");
                if (valCls.isInstance(fn)) {
                    valCls.getMethod("execute", Object[].class).invoke(fn, (Object) new Object[]{payload});
                    return;
                }
            } catch (Throwable ignored) {
            }

            // JSR-223 engines vary. Try common patterns by reflection.
            try { fn.getClass().getMethod("call", Object.class).invoke(fn, payload); return; } catch (Throwable ignored) {}
            try { fn.getClass().getMethod("run").invoke(fn); } catch (Throwable ignored) {}
        }
    }
}
