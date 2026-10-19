package io.sevk.markup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class MarkupSupport {
    private static final Set<String> DEPTH_TAGS = Set.of(
        "block", "section", "column", "row", "container", "heading", "paragraph",
        "text", "button", "link", "sevk-link", "list", "li", "codeblock");

    interface TagProcessor {
        String process(Map<String, String> attrs, String inner);
    }

    static void assertMarkupDepth(String content) {
        Map<String, Integer> depths = new HashMap<>();
        int index = 0;

        while (index < content.length()) {
            int open = content.indexOf('<', index);
            if (open < 0) {
                break;
            }
            int close = content.indexOf('>', open + 1);
            if (close < 0) {
                break;
            }
            index = close + 1;

            int position = open + 1;
            boolean closing = content.charAt(position) == '/';
            if (closing) {
                position++;
            }
            int nameEnd = position;
            while (nameEnd < close && isTagNameChar(content.charAt(nameEnd))) {
                nameEnd++;
            }
            String name = content.substring(position, nameEnd).toLowerCase(Locale.ROOT);
            if (!DEPTH_TAGS.contains(name)) {
                continue;
            }

            int depth = depths.getOrDefault(name, 0);
            if (closing) {
                if (depth > 0) {
                    depths.put(name, depth - 1);
                }
            } else if (content.charAt(close - 1) != '/') {
                depth++;
                if (depth > RenderBudget.MAX_MARKUP_DEPTH) {
                    throw new IllegalArgumentException("Email markup nesting exceeds the maximum depth of " + RenderBudget.MAX_MARKUP_DEPTH);
                }
                depths.put(name, depth);
            }
        }
    }

    private static boolean isTagNameChar(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '-';
    }

    static String processTag(String content, String tagName, TagProcessor processor, RenderBudget budget) {
        String result = content;
        Pattern openRe = Pattern.compile("<" + tagName + "([^>]*)>", Pattern.CASE_INSENSITIVE);
        Pattern closeRe = Pattern.compile("(?i)</" + tagName + ">");
        Pattern nestedOpenRe = Pattern.compile("(?i)<" + tagName);

        for (int iteration = 0; iteration < RenderBudget.MAX_TAG_ITERATIONS; iteration++) {
            budget.chargeScanWork(result.length());
            Matcher matcher = openRe.matcher(result);
            List<int[]> matches = new ArrayList<>();
            List<String> attrsStrs = new ArrayList<>();

            while (matcher.find()) {
                matches.add(new int[]{matcher.start(), matcher.end()});
                attrsStrs.add(matcher.group(1));
            }

            if (matches.isEmpty()) {
                break;
            }

            List<int[]> replSpans = new ArrayList<>();
            List<String> replTexts = new ArrayList<>();
            int consumedUntil = 0;

            for (int i = 0; i < matches.size(); i++) {
                int start = matches.get(i)[0];

                if (start < consumedUntil) {
                    continue;
                }

                int innerStart = matches.get(i)[1];
                Matcher closeMatcher = closeRe.matcher(result);

                if (!closeMatcher.find(innerStart)) {
                    budget.chargeScanWork(result.length() - innerStart);
                    continue;
                }

                int closePos = closeMatcher.start();
                int closeEnd = closeMatcher.end();
                budget.chargeScanWork(closePos - innerStart);

                String inner = result.substring(innerStart, closePos);

                if (nestedOpenRe.matcher(inner).find()) {
                    continue;
                }

                replSpans.add(new int[]{start, closeEnd});
                replTexts.add(processor.process(parseAttributes(attrsStrs.get(i)), inner));
                consumedUntil = closeEnd;
            }

            if (replSpans.isEmpty()) {
                break;
            }

            StringBuilder rebuilt = new StringBuilder();
            int cursor = 0;
            for (int i = 0; i < replSpans.size(); i++) {
                rebuilt.append(result, cursor, replSpans.get(i)[0]);
                rebuilt.append(replTexts.get(i));
                cursor = replSpans.get(i)[1];
                if (rebuilt.length() > RenderBudget.MAX_RENDER_OUTPUT) {
                    throw new IllegalArgumentException("Email markup expands beyond the maximum size of " + RenderBudget.MAX_RENDER_OUTPUT + " bytes");
                }
            }
            rebuilt.append(result.substring(cursor));
            String next = rebuilt.toString();
            if (next.equals(result)) {
                break;
            }
            result = next;
        }

        return result;
    }

    static Map<String, String> parseAttributes(String attrsStr) {
        Map<String, String> attrs = new HashMap<>();
        Pattern re = Pattern.compile("([\\w-]+)=(?:\"([^\"]*)\"|'([^']*)')");
        Matcher matcher = re.matcher(attrsStr);
        while (matcher.find()) {
            attrs.put(matcher.group(1), matcher.group(2) != null ? matcher.group(2) : matcher.group(3));
        }
        return attrs;
    }

    static Map<String, String> extractAllStyleAttributes(Map<String, String> attrs) {
        Map<String, String> style = new LinkedHashMap<>();

        String textColor = attrs.get("text-color");
        if (textColor != null && !textColor.isEmpty()) {
            style.put("color", textColor);
        } else {
            putIfPresent(style, "color", attrs.get("color"));
        }

        putIfPresent(style, "background-color", attrs.get("background-color"));

        // Background image group lives between background-color and font-size to
        // match the reference extractStyleAttributes insertion order.
        String backgroundImage = attrs.get("background-image");
        boolean hasBackgroundImage = backgroundImage != null && !backgroundImage.isEmpty();
        if (hasBackgroundImage) {
            style.put("background-image", "url('" + backgroundImage + "')");
        }

        putIfPresent(style, "background-size", attrs.get("background-size"));
        if (!style.containsKey("background-size") && hasBackgroundImage) {
            style.put("background-size", "cover");
        }

        putIfPresent(style, "background-position", attrs.get("background-position"));
        if (!style.containsKey("background-position") && hasBackgroundImage) {
            style.put("background-position", "center");
        }

        putIfPresent(style, "background-repeat", attrs.get("background-repeat"));
        if (!style.containsKey("background-repeat") && hasBackgroundImage) {
            style.put("background-repeat", "no-repeat");
        }

        putIfPresent(style, "font-size", attrs.get("font-size"));
        putIfPresent(style, "font-family", attrs.get("font-family"));
        putIfPresent(style, "font-weight", attrs.get("font-weight"));
        putIfPresent(style, "line-height", attrs.get("line-height"));
        putIfPresent(style, "text-align", attrs.get("text-align"));
        putIfPresent(style, "text-decoration", attrs.get("text-decoration"));

        putIfPresent(style, "width", attrs.get("width"));
        putIfPresent(style, "height", attrs.get("height"));
        putIfPresent(style, "max-width", attrs.get("max-width"));
        putIfPresent(style, "max-height", attrs.get("max-height"));
        putIfPresent(style, "min-width", attrs.get("min-width"));
        putIfPresent(style, "min-height", attrs.get("min-height"));

        putIfPresent(style, "padding", attrs.get("padding"));
        putIfPresent(style, "padding-top", attrs.get("padding-top"));
        putIfPresent(style, "padding-right", attrs.get("padding-right"));
        putIfPresent(style, "padding-bottom", attrs.get("padding-bottom"));
        putIfPresent(style, "padding-left", attrs.get("padding-left"));

        putIfPresent(style, "margin", attrs.get("margin"));
        putIfPresent(style, "margin-top", attrs.get("margin-top"));
        putIfPresent(style, "margin-right", attrs.get("margin-right"));
        putIfPresent(style, "margin-bottom", attrs.get("margin-bottom"));
        putIfPresent(style, "margin-left", attrs.get("margin-left"));

        // Borders cascade: shorthand -> per-property -> per-side (most specific last)
        putIfPresent(style, "border", attrs.get("border"));
        putIfPresent(style, "border-color", attrs.get("border-color"));
        putIfPresent(style, "border-width", attrs.get("border-width"));
        putIfPresent(style, "border-style", attrs.get("border-style"));
        putIfPresent(style, "border-top", attrs.get("border-top"));
        putIfPresent(style, "border-right", attrs.get("border-right"));
        putIfPresent(style, "border-bottom", attrs.get("border-bottom"));
        putIfPresent(style, "border-left", attrs.get("border-left"));

        putIfPresent(style, "border-radius", attrs.get("border-radius"));
        putIfPresent(style, "border-top-left-radius", attrs.get("border-top-left-radius"));
        putIfPresent(style, "border-top-right-radius", attrs.get("border-top-right-radius"));
        putIfPresent(style, "border-bottom-left-radius", attrs.get("border-bottom-left-radius"));
        putIfPresent(style, "border-bottom-right-radius", attrs.get("border-bottom-right-radius"));

        return style;
    }

    private static void putIfPresent(Map<String, String> target, String key, String value) {
        if (value != null && !value.isEmpty()) {
            target.put(key, value);
        }
    }

    private static final String[] STYLE_ORDER = {
        "color", "background-color", "background-image", "background-size", "background-position", "background-repeat",
        "font-size", "font-family", "font-weight", "line-height", "text-align", "text-decoration",
        "width", "height", "max-width", "max-height", "min-width", "min-height",
        "padding", "padding-top", "padding-right", "padding-bottom", "padding-left",
        "margin", "margin-top", "margin-right", "margin-bottom", "margin-left",
        "border", "border-color", "border-width", "border-style",
        "border-top", "border-right", "border-bottom", "border-left",
        "border-radius", "border-top-left-radius", "border-top-right-radius", "border-bottom-left-radius", "border-bottom-right-radius",
        "border-collapse", "list-style-type", "vertical-align"
    };

    static String styleToString(Map<String, String> style) {
        StringBuilder sb = new StringBuilder();
        Set<String> seen = new HashSet<>();
        for (String key : STYLE_ORDER) {
            String value = style.get(key);

            if (value == null || value.isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(";");
            }

            sb.append(key).append(":").append(value);
            seen.add(key);
        }

        for (Map.Entry<String, String> entry : style.entrySet()) {
            if (seen.contains(entry.getKey())) {
                continue;
            }

            String value = entry.getValue();

            if (value == null || value.isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(";");
            }

            sb.append(entry.getKey()).append(":").append(value);
        }

        return sb.toString();
    }
}
