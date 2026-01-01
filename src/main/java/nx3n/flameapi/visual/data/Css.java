package nx3n.flameapi.visual.data;

import java.util.*;

/**
 * Tiny CSS parser used only for presets.
 *
 * Supported syntax:
 * <pre>
 * vignette { size: 0.22; color: #AA000000; priority: 10; }
 * .combat vignette { size: 0.30; }
 * </pre>
 *
 * Selectors:
 * - tag selector: <code>vignette</code>
 * - optional scene class: <code>.combat vignette</code>
 *
 * Values:
 * - numbers (int/float)
 * - booleans (true/false)
 * - hex colors (#RRGGBB or #AARRGGBB)
 * - strings (raw text)
 */
final class Css {
    record Rule(String sceneClass, String tag, Map<String, String> props) {}

    static List<Rule> parse(String cssText) {
        if (cssText == null || cssText.isBlank()) return List.of();

        String s = stripComments(cssText);
        List<Rule> out = new ArrayList<>();

        int i = 0;
        while (i < s.length()) {
            int open = s.indexOf('{', i);
            if (open < 0) break;
            String selector = s.substring(i, open).trim();
            int close = findMatchingBrace(s, open);
            if (close < 0) break;
            String body = s.substring(open + 1, close).trim();

            String sceneClass = null;
            String tag;

            // Allow: ".combat vignette" or just "vignette"
            String[] parts = selector.split("\\s+");
            if (parts.length == 1) {
                tag = parts[0].trim();
            } else {
                String p0 = parts[0].trim();
                if (p0.startsWith(".")) {
                    sceneClass = p0.substring(1);
                    tag = parts[parts.length - 1].trim();
                } else {
                    tag = parts[parts.length - 1].trim();
                }
            }

            if (!tag.isEmpty()) {
                Map<String, String> props = parseProps(body);
                out.add(new Rule(sceneClass, tag, props));
            }

            i = close + 1;
        }
        return out;
    }

    private static Map<String, String> parseProps(String body) {
        Map<String, String> m = new LinkedHashMap<>();
        for (String stmt : body.split(";")) {
            String t = stmt.trim();
            if (t.isEmpty()) continue;
            int colon = t.indexOf(':');
            if (colon < 0) continue;
            String k = t.substring(0, colon).trim();
            String v = t.substring(colon + 1).trim();
            if (!k.isEmpty() && !v.isEmpty()) m.put(k, v);
        }
        return m;
    }

    private static int findMatchingBrace(String s, int open) {
        int depth = 0;
        for (int i = open; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    private static String stripComments(String s) {
        // /* ... */ only
        StringBuilder out = new StringBuilder(s.length());
        int i = 0;
        while (i < s.length()) {
            int start = s.indexOf("/*", i);
            if (start < 0) {
                out.append(s, i, s.length());
                break;
            }
            out.append(s, i, start);
            int end = s.indexOf("*/", start + 2);
            if (end < 0) break;
            i = end + 2;
        }
        return out.toString();
    }
}
