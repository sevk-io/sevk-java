package io.sevk.markup;

import static io.sevk.markup.MarkupSupport.extractAllStyleAttributes;
import static io.sevk.markup.MarkupSupport.styleToString;

import com.codewaves.codehighlight.core.Highlighter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import org.fife.ui.rsyntaxtextarea.modes.TypeScriptTokenMaker;

final class SyntaxHighlighter {
    private static final Set<String> LANGUAGES = Set.of(
        "plain", "extend", "plaintext", "text", "txt", "markup", "html", "mathml", "svg", "xml", "ssml", "atom", "rss",
        "css", "clike", "javascript", "js", "typescript", "ts", "jsx", "tsx", "python", "py", "java", "c", "cpp",
        "csharp", "cs", "dotnet", "php", "ruby", "rb", "go", "rust", "sql", "bash", "sh", "shell", "json",
        "webmanifest", "yaml", "yml", "markdown", "md");
    private static final Map<String, String> ALIASES = Map.of("jsx", "javascript", "tsx", "typescript");
    private static final Map<String, String> HIGHLIGHT_NAMES = Map.of(
        "markup", "xml", "mathml", "xml", "svg", "xml", "ssml", "xml", "atom", "xml", "rss", "xml",
        "dotnet", "cs", "webmanifest", "json", "shell", "bash");
    private static final Set<String> PLAIN_LANGUAGES = Set.of(
        "plain", "plaintext", "text", "txt", "clike", "ruby", "rb", "go", "rust", "sql", "yaml", "yml",
        "markdown", "md");
    private static final Map<String, String> TOKEN_NAMES = Map.ofEntries(
        Map.entry("keyword", "keyword"), Map.entry("built_in", "builtin"), Map.entry("type", "class-name"),
        Map.entry("literal", "boolean"), Map.entry("number", "number"), Map.entry("string", "string"),
        Map.entry("subst", "variable"), Map.entry("symbol", "symbol"), Map.entry("class", "class-name"),
        Map.entry("function", "function"), Map.entry("title", "function"), Map.entry("params", "variable"),
        Map.entry("comment", "comment"), Map.entry("doctag", "comment"), Map.entry("meta", "keyword"),
        Map.entry("meta-keyword", "keyword"), Map.entry("meta-string", "string"), Map.entry("section", "tag"),
        Map.entry("tag", "tag"), Map.entry("name", "tag"), Map.entry("attr", "attr-name"),
        Map.entry("attribute", "attr-name"), Map.entry("variable", "variable"),
        Map.entry("template-variable", "variable"), Map.entry("template-tag", "tag"),
        Map.entry("regexp", "regex"), Map.entry("link", "url"), Map.entry("selector-tag", "selector"),
        Map.entry("selector-id", "selector"), Map.entry("selector-class", "selector"),
        Map.entry("selector-attr", "selector"), Map.entry("selector-pseudo", "selector"),
        Map.entry("property", "property"), Map.entry("punctuation", "punctuation"),
        Map.entry("operator", "operator"), Map.entry("addition", "inserted"), Map.entry("deletion", "deleted"),
        Map.entry("formula", "number"), Map.entry("quote", "comment"), Map.entry("emphasis", "italic"),
        Map.entry("strong", "bold"), Map.entry("bullet", "punctuation"), Map.entry("code", "string"));
    private static final Pattern SPAN_TAG = Pattern.compile("<span class=\"([^\"]*)\">|</span>");

    private SyntaxHighlighter() {
    }

    static String processCodeBlock(Map<String, String> attrs, String code, RenderBudget budget) {
        String requested = attrs.getOrDefault("language", "");
        requested = (requested.isEmpty() ? "javascript" : requested).toLowerCase(Locale.ROOT);
        String language = ALIASES.getOrDefault(requested, requested);
        String themeName = attrs.getOrDefault("theme", "");
        themeName = CodeBlockThemes.resolve(themeName.isEmpty() ? "oneDark" : themeName);
        Map<String, String> customStyle = extractAllStyleAttributes(attrs);

        if (code.isEmpty()) {
            return "<pre><code></code></pre>";
        }

        if (!LANGUAGES.contains(language) || !budget.admits(code.length())) {
            return "<pre style=\"" + preStyle(themeName, false, customStyle) + "\"><code>"
                + code.replace("<", "&lt;").replace(">", "&gt;") + "</code></pre>";
        }
        budget.charge(code.length());

        Map<String, String> inherited = new LinkedHashMap<>();
        String fontFamily = attrs.getOrDefault("font-family", "");
        if (!fontFamily.isEmpty()) {
            inherited.put("font-family", fontFamily);
        }

        boolean numbered = "true".equals(attrs.get("line-numbers"));
        StringBuilder html = new StringBuilder();
        List<String> lines = highlightLines(code, language, themeName, inherited);
        for (int index = 0; index < lines.size(); index++) {
            html.append("<p style=\"margin:0;min-height:1em\">");
            if (numbered) {
                html.append("<span style=\"width:2em;display:inline-block\">").append(index + 1).append("</span>");
            }
            html.append(lines.get(index)).append("</p>");
        }

        return "<pre style=\"" + preStyle(themeName, true, customStyle) + "\"><code>" + html + "</code></pre>";
    }

    private static String preStyle(String themeName, boolean highlighted, Map<String, String> customStyle) {
        Map<String, String> style = new LinkedHashMap<>(CodeBlockThemes.base(themeName));
        style.put("width", "100%");
        if (highlighted) {
            style.put("box-sizing", "border-box");
        }
        style.putAll(customStyle);

        StringBuilder serialized = new StringBuilder();
        for (Map.Entry<String, String> entry : style.entrySet()) {
            if (serialized.length() > 0) {
                serialized.append(';');
            }
            serialized.append(kebabProperty(entry.getKey())).append(':').append(entry.getValue());
        }
        return serialized.toString();
    }

    private static String kebabProperty(String key) {
        String property = key.replaceAll("[A-Z]", "-$0").toLowerCase(Locale.ROOT);
        return property.startsWith("ms-") ? "-" + property : property;
    }

    private static List<String> highlightLines(String code, String language, String themeName, Map<String, String> inherited) {
        String inheritedStyle = styleToString(inherited);
        List<String> lines = new ArrayList<>();
        lines.add("");
        List<String> open = new ArrayList<>();
        String html = tokenize(code, language);
        Matcher tags = SPAN_TAG.matcher(html);
        int cursor = 0;

        while (true) {
            boolean found = tags.find();
            String text = html.substring(cursor, found ? tags.start() : html.length());
            appendText(lines, open, text, inheritedStyle);
            if (!found) {
                break;
            }
            cursor = tags.end();
            if (tags.group(1) == null) {
                String tag = open.remove(open.size() - 1);
                if (!tag.isEmpty()) {
                    appendLast(lines, "</span>");
                }
                continue;
            }
            String tag = tokenTag(themeName, tags.group(1), inherited);
            open.add(tag);
            appendLast(lines, tag);
        }

        return lines;
    }

    private static void appendText(List<String> lines, List<String> open, String text, String inheritedStyle) {
        boolean wrap = !inheritedStyle.isEmpty() && open.stream().allMatch(String::isEmpty);
        String[] parts = text.split("\n", -1);
        for (int index = 0; index < parts.length; index++) {
            if (index > 0) {
                StringBuilder reopened = new StringBuilder();
                for (String tag : open) {
                    if (!tag.isEmpty()) {
                        appendLast(lines, "</span>");
                        reopened.append(tag);
                    }
                }
                lines.add(reopened.toString());
            }
            if (parts[index].isEmpty()) {
                continue;
            }
            appendLast(lines, wrap ? "<span style=\"" + inheritedStyle + "\">" + parts[index] + "</span>" : parts[index]);
        }
    }

    private static void appendLast(List<String> lines, String text) {
        lines.set(lines.size() - 1, lines.get(lines.size() - 1) + text);
    }

    private static String tokenTag(String themeName, String classes, Map<String, String> inherited) {
        Map<String, String> style = new LinkedHashMap<>(inherited);
        boolean matched = false;
        for (String className : classes.trim().split("\\s+")) {
            String token = TOKEN_NAMES.get(className.startsWith("hljs-") ? className.substring(5) : className);
            if (token == null) {
                continue;
            }
            matched = true;
            style.putAll(CodeBlockThemes.token(themeName, token));
        }
        return matched ? "<span style=\"" + styleToString(style) + "\">" : "";
    }

    private static String tokenize(String code, String language) {
        if (PLAIN_LANGUAGES.contains(language)) {
            return HighlightRenderer.escapeText(code);
        }
        if ("typescript".equals(language) || "ts".equals(language)) {
            return highlightTypescript(code);
        }
        try {
            Highlighter.HighlightResult result = new Highlighter(name -> new HighlightRenderer())
                .highlight(HIGHLIGHT_NAMES.getOrDefault(language, language), code);
            if (result.getLanguage() != null) {
                return result.getResult().toString();
            }
        } catch (RuntimeException ignored) {
            return HighlightRenderer.escapeText(code);
        }
        return HighlightRenderer.escapeText(code);
    }

    private static String highlightTypescript(String code) {
        TypeScriptTokenMaker lexer = new TypeScriptTokenMaker();
        StringBuilder rendered = new StringBuilder();
        int state = TokenTypes.NULL;
        Matcher lines = Pattern.compile("([^\\r\\n]*)(\\r\\n|\\r|\\n|$)").matcher(code);
        while (lines.find() && lines.start() < code.length()) {
            char[] characters = lines.group(1).toCharArray();
            Segment line = new Segment(characters, 0, characters.length);
            Token token = lexer.getTokenList(line, state, lines.start());
            while (token != null && token.isPaintable()) {
                String tokenClass = typescriptTokenClass(token.getType());
                if (tokenClass != null) {
                    rendered.append("<span class=\"").append(tokenClass).append("\">");
                }
                rendered.append(HighlightRenderer.escapeText(token.getLexeme()));
                if (tokenClass != null) {
                    rendered.append("</span>");
                }
                token = token.getNextToken();
            }
            state = lexer.getLastTokenTypeOnLine(line, state);
            rendered.append(lines.group(2));
        }
        return rendered.toString();
    }

    private static String typescriptTokenClass(int type) {
        switch (type) {
            case TokenTypes.COMMENT_EOL:
            case TokenTypes.COMMENT_MULTILINE:
            case TokenTypes.COMMENT_DOCUMENTATION:
            case TokenTypes.COMMENT_KEYWORD:
            case TokenTypes.COMMENT_MARKUP:
                return "comment";
            case TokenTypes.RESERVED_WORD:
            case TokenTypes.RESERVED_WORD_2:
                return "keyword";
            case TokenTypes.DATA_TYPE:
                return "type";
            case TokenTypes.LITERAL_BOOLEAN:
                return "literal";
            case TokenTypes.LITERAL_NUMBER_DECIMAL_INT:
            case TokenTypes.LITERAL_NUMBER_FLOAT:
            case TokenTypes.LITERAL_NUMBER_HEXADECIMAL:
                return "number";
            case TokenTypes.LITERAL_STRING_DOUBLE_QUOTE:
            case TokenTypes.LITERAL_CHAR:
            case TokenTypes.LITERAL_BACKQUOTE:
                return "string";
            case TokenTypes.FUNCTION:
                return "function";
            case TokenTypes.OPERATOR:
                return "operator";
            case TokenTypes.SEPARATOR:
                return "punctuation";
            case TokenTypes.REGEX:
                return "regexp";
            default:
                return null;
        }
    }
}
