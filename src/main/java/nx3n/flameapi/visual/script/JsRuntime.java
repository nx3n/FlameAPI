package nx3n.flameapi.visual.script;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

/**
 * JavaScript runtime used by HTML presets.
 *
 * "Full JS" requires a JS engine to be present at runtime (GraalJS or Rhino).
 * This class keeps FlameAPI lightweight by discovering engines dynamically:
 * - If Graal Polyglot JS is present (org.graalvm.polyglot.*), we use it.
 * - Otherwise we try JSR-223 engines ("graal.js", "javascript", "nashorn").
 * - If none exist, scripts are skipped (with a clear log message).
 */
public interface JsRuntime {
    Logger LOGGER = LogUtils.getLogger();

    boolean available();

    /** Executes a script with given globals bound (fx, on, etc.). */
    void eval(String source, Map<String, Object> globals);

    static JsRuntime createDefault() {
        JsRuntime graal = GraalRuntime.tryCreate();
        if (graal != null) return graal;
        JsRuntime jsr = Jsr223Runtime.tryCreate();
        if (jsr != null) return jsr;
        return new DisabledRuntime();
    }

    final class DisabledRuntime implements JsRuntime {
        @Override public boolean available() { return false; }
        @Override public void eval(String source, Map<String, Object> globals) { }
    }

    /** JSR-223 runtime (works if a JS engine is on classpath). */
    final class Jsr223Runtime implements JsRuntime {
        private final ScriptEngine engine;

        private Jsr223Runtime(ScriptEngine engine) {
            this.engine = engine;
        }

        static JsRuntime tryCreate() {
            try {
                ScriptEngineManager m = new ScriptEngineManager();
                ScriptEngine e = m.getEngineByName("graal.js");
                if (e == null) e = m.getEngineByName("javascript");
                if (e == null) e = m.getEngineByName("nashorn");
                if (e == null) return null;
                LOGGER.info("FlameAPI JS: using JSR-223 engine: {}", e.getFactory().getEngineName());
                return new Jsr223Runtime(e);
            } catch (Throwable t) {
                return null;
            }
        }

        @Override public boolean available() { return true; }

        @Override
        public void eval(String source, Map<String, Object> globals) {
            try {
                if (globals != null) {
                    for (Map.Entry<String, Object> e : globals.entrySet()) engine.put(e.getKey(), e.getValue());
                }
                engine.eval(source);
            } catch (Throwable t) {
                LOGGER.warn("FlameAPI JS script failed: {}", t.toString());
            }
        }
    }

    /** Graal Polyglot runtime (reflection to avoid hard dependency). */
    final class GraalRuntime implements JsRuntime {
        private final Object context; // org.graalvm.polyglot.Context
        private final Method eval;
        private final Method getBindings;
        private final Method putMember;

        private GraalRuntime(Object context, Method eval, Method getBindings, Method putMember) {
            this.context = context;
            this.eval = eval;
            this.getBindings = getBindings;
            this.putMember = putMember;
        }

        static JsRuntime tryCreate() {
            try {
                Class<?> ctxCls = Class.forName("org.graalvm.polyglot.Context");
                Method create = ctxCls.getMethod("create", String[].class);
                Object ctx = create.invoke(null, (Object) new String[]{"js"});

                Method eval = ctxCls.getMethod("eval", String.class, CharSequence.class);
                Method getBindings = ctxCls.getMethod("getBindings", String.class);

                Class<?> valCls = Class.forName("org.graalvm.polyglot.Value");
                Method putMember = valCls.getMethod("putMember", String.class, Object.class);

                LOGGER.info("FlameAPI JS: using Graal Polyglot JS");
                return new GraalRuntime(ctx, eval, getBindings, putMember);
            } catch (Throwable t) {
                return null;
            }
        }

        @Override public boolean available() { return true; }

        @Override
        public void eval(String source, Map<String, Object> globals) {
            try {
                Object bindings = getBindings.invoke(context, "js");
                if (globals != null) {
                    for (Map.Entry<String, Object> e : globals.entrySet()) {
                        putMember.invoke(bindings, e.getKey(), e.getValue());
                    }
                }
                eval.invoke(context, "js", source);
            } catch (Throwable t) {
                LOGGER.warn("FlameAPI JS script failed: {}", t.toString());
            }
        }
    }

    static Map<String, Object> emptyGlobals() {
        return Collections.emptyMap();
    }
}
