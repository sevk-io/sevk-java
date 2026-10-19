package io.sevk.markup;

import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

final class TemplateEngine {
    private static final int MAX_IF_PASSES = 2000;
    private static final int MAX_EACH_ITEMS = 1000;
    private static final int MAX_EACH_OUTPUT = 2_000_000;
    private static final long MAX_TEMPLATE_WORK = 20_000_000L;
    private static final String OUTPUT_EXCEEDED = "Email block exceeds the maximum template output size";
    private static final Pattern IF_PATTERN = Pattern.compile("\\{%#if\\s+([^%]+)%\\}((?:(?!\\{%#if\\s)[\\s\\S])*?)\\{%/if%\\}");
    private static final Pattern EACH_OPEN_PATTERN = Pattern.compile("\\{%#each\\s+(\\w+)(?:\\s+as\\s+(\\w+))?%\\}");
    private static final Pattern FALLBACK_PATTERN = Pattern.compile("\\{%(\\w+)\\s*\\?\\?([^%]+)%\\}");
    private static final Pattern VALUE_PATTERN = Pattern.compile("\\{%(\\w+)%\\}");
    private static final Pattern EQUALS_PATTERN = Pattern.compile("^(\\w+)\\s*==\\s*\"([^\"]*)\"$");
    private static final Pattern NOT_EQUALS_PATTERN = Pattern.compile("^(\\w+)\\s*!=\\s*\"([^\"]*)\"$");
    private static final Pattern ENTITY_PATTERN = Pattern.compile("&(?:amp|quot|apos|lt|gt|#39|#x27|#34|#x22);", Pattern.CASE_INSENSITIVE);
    private static final Map<String, String> ENTITIES = Map.of(
        "&amp;", "&", "&quot;", "\"", "&#34;", "\"", "&#x22;", "\"",
        "&apos;", "'", "&#39;", "'", "&#x27;", "'", "&lt;", "<", "&gt;", ">");

    private static final class OutputMeter {
        private long rendered;

        String charge(String value) {
            if (rendered + value.length() > MAX_EACH_OUTPUT) {
                throw new IllegalArgumentException(OUTPUT_EXCEEDED);
            }
            rendered += value.length();
            return value;
        }

        String chargeItem(long[] itemOut, String value) {
            if (rendered + itemOut[0] + value.length() > MAX_EACH_OUTPUT) {
                throw new IllegalArgumentException(OUTPUT_EXCEEDED);
            }
            itemOut[0] += value.length();
            return value;
        }

        void addRendered(int length) {
            rendered += length;
            if (rendered > MAX_EACH_OUTPUT) {
                throw new IllegalArgumentException(OUTPUT_EXCEEDED);
            }
        }
    }

    private TemplateEngine() {
    }

    private static boolean isTruthy(Object val) {
        if (val == null) {
            return false;
        }
        if (val instanceof Boolean) {
            return (Boolean) val;
        }
        if (val instanceof Number) {
            return ((Number) val).doubleValue() != 0;
        }
        if (val instanceof String) {
            return !((String) val).isEmpty();
        }
        if (val instanceof List) {
            return !((List<?>) val).isEmpty();
        }

        return true;
    }

    private static String text(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Number) {
            return JsNumber.format(((Number) value).doubleValue());
        }
        if (value instanceof Map) {
            return "[object Object]";
        }
        if (value instanceof List) {
            return ((List<?>) value).stream()
                .map(item -> item instanceof Map ? "[object Object]" : text(item))
                .collect(Collectors.joining(","));
        }

        return value.toString();
    }

    private static boolean evaluateCondition(String expr, Map<String, Object> config) {
        String trimmed = expr.trim();

        if (trimmed.contains("||")) {
            for (String part : trimmed.split("\\|\\|")) {
                if (evaluateCondition(part, config)) {
                    return true;
                }
            }

            return false;
        }

        if (trimmed.contains("&&")) {
            for (String part : trimmed.split("&&")) {
                if (!evaluateCondition(part, config)) {
                    return false;
                }
            }

            return true;
        }

        Matcher eqMatcher = EQUALS_PATTERN.matcher(trimmed);
        if (eqMatcher.matches()) {
            return text(config.get(eqMatcher.group(1))).equals(eqMatcher.group(2));
        }

        Matcher neqMatcher = NOT_EQUALS_PATTERN.matcher(trimmed);
        if (neqMatcher.matches()) {
            return !text(config.get(neqMatcher.group(1))).equals(neqMatcher.group(2));
        }

        return isTruthy(config.get(trimmed));
    }

    private static String renderTemplate(String template, Map<String, Object> config) {
        OutputMeter meter = new OutputMeter();
        String result = renderConditionals(template, config);
        result = renderLoops(result, config, meter);

        result = FALLBACK_PATTERN.matcher(result).replaceAll(match -> {
            Object value = config.get(match.group(1));
            String replacement = value == null || "".equals(value) ? match.group(2).trim() : text(value);
            return Matcher.quoteReplacement(meter.charge(replacement));
        });

        return VALUE_PATTERN.matcher(result)
            .replaceAll(match -> Matcher.quoteReplacement(meter.charge(text(config.get(match.group(1))))));
    }

    private static String renderConditionals(String template, Map<String, Object> config) {
        String result = template;
        long work = 0;

        for (int pass = 0; pass < MAX_IF_PASSES; pass++) {
            work += result.length();
            if (work > MAX_TEMPLATE_WORK) {
                throw new IllegalArgumentException(RenderBudget.TOO_COMPLEX);
            }

            Matcher matcher = IF_PATTERN.matcher(result);
            if (!matcher.find()) {
                break;
            }

            String body = matcher.group(2);
            boolean truthy = evaluateCondition(matcher.group(1), config);
            int elseIndex = body.indexOf("{%else%}");
            String replacement;
            if (elseIndex < 0) {
                replacement = truthy ? body : "";
            } else {
                replacement = truthy ? body.substring(0, elseIndex) : body.substring(elseIndex + "{%else%}".length());
            }

            result = result.substring(0, matcher.start()) + replacement + result.substring(matcher.end());
        }

        return result;
    }

    private static String renderLoops(String template, Map<String, Object> config, OutputMeter meter) {
        if (!template.contains("{%/each%}")) {
            return template;
        }

        StringBuilder output = new StringBuilder();
        Matcher opening = EACH_OPEN_PATTERN.matcher(template);
        int from = 0;
        long work = 0;

        while (from <= template.length()) {
            if (!opening.find(from)) {
                output.append(template, from, template.length());
                break;
            }

            int bodyStart = opening.end();
            int closeIndex = template.indexOf("{%/each%}", bodyStart);
            if (closeIndex < 0) {
                output.append(template, from, template.length());
                break;
            }

            output.append(template, from, opening.start());
            Object items = config.get(opening.group(1));

            if (items instanceof List && !((List<?>) items).isEmpty()) {
                List<?> list = (List<?>) items;
                if (list.size() > MAX_EACH_ITEMS) {
                    throw new IllegalArgumentException("Email block exceeds the maximum of " + MAX_EACH_ITEMS + " loop items");
                }

                String alias = opening.group(2) != null ? opening.group(2) : "this";
                Pattern propertyPattern = Pattern.compile("\\{%" + Pattern.quote(alias) + "\\.(\\w+)%\\}");
                String body = template.substring(bodyStart, closeIndex);

                for (Object item : list) {
                    work += body.length() + 16;
                    if (work > MAX_TEMPLATE_WORK) {
                        throw new IllegalArgumentException(RenderBudget.TOO_COMPLEX);
                    }
                    output.append(renderLoopItem(body, item, config, propertyPattern, meter));
                }
            }

            from = closeIndex + "{%/each%}".length();
        }

        return output.toString();
    }

    private static String renderLoopItem(String body, Object item, Map<String, Object> config, Pattern propertyPattern, OutputMeter meter) {
        long[] itemOut = {0};

        String rendered = propertyPattern.matcher(body).replaceAll(match ->
            Matcher.quoteReplacement(meter.chargeItem(itemOut, text(itemProperty(item, match.group(1))))));
        rendered = VALUE_PATTERN.matcher(rendered).replaceAll(match ->
            Matcher.quoteReplacement(meter.chargeItem(itemOut, text(config.get(match.group(1))))));
        meter.addRendered(rendered.length());

        return rendered;
    }

    private static Object itemProperty(Object item, String property) {
        if (item instanceof Map) {
            return ((Map<?, ?>) item).get(property);
        }
        if (!"length".equals(property)) {
            return null;
        }
        if (item instanceof String) {
            return ((String) item).length();
        }
        if (item instanceof List) {
            return ((List<?>) item).size();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    static String processBlockTag(Map<String, String> attrs, String inner) {
        String template = (inner != null && !inner.trim().isEmpty()) ? inner.trim() : attrs.getOrDefault("template", "");

        if (template.isEmpty()) {
            return "";
        }

        String configStr = ENTITY_PATTERN.matcher(attrs.getOrDefault("config", "{}"))
            .replaceAll(match -> Matcher.quoteReplacement(ENTITIES.get(match.group().toLowerCase(Locale.ROOT))));
        Map<String, Object> config;

        try {
            config = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create().fromJson(configStr, Map.class);
        } catch (RuntimeException e) {
            try {
                config = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create().fromJson(configStr.replace("'", "\""), Map.class);
            } catch (RuntimeException ignored) {
                config = new HashMap<>();
            }
        }
        if (config == null) {
            config = new HashMap<>();
        }

        return renderTemplate(template, config);
    }
}
