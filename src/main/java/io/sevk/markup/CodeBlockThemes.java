package io.sevk.markup;

import java.util.LinkedHashMap;
import java.util.Map;

final class CodeBlockThemes {
    private static final Map<String, Map<String, String>> BASES = new LinkedHashMap<>();
    private static final Map<String, Map<String, Map<String, String>>> TOKENS = new LinkedHashMap<>();

    static {
        BASES.put("vscDarkPlus", entries(
            "color", "#d4d4d4",
            "fontSize", "13px",
            "textShadow", "none",
            "fontFamily", "Menlo, Monaco, Consolas, 'Andale Mono', 'Ubuntu Mono', 'Courier New', monospace",
            "direction", "ltr",
            "textAlign", "left",
            "whiteSpace", "pre",
            "wordSpacing", "normal",
            "wordBreak", "normal",
            "lineHeight", "1.5",
            "MozTabSize", "4",
            "OTabSize", "4",
            "tabSize", "4",
            "WebkitHyphens", "none",
            "MozHyphens", "none",
            "hyphens", "none",
            "padding", "16px",
            "overflow", "auto",
            "backgroundColor", "#1e1e1e"));
        BASES.put("vs", entries(
            "color", "#393A34",
            "fontFamily", "'Consolas', 'Bitstream Vera Sans Mono', 'Courier New', Courier, monospace",
            "direction", "ltr",
            "textAlign", "left",
            "whiteSpace", "pre",
            "wordSpacing", "normal",
            "wordBreak", "normal",
            "fontSize", ".9em",
            "lineHeight", "1.2em",
            "MozTabSize", "4",
            "OTabSize", "4",
            "tabSize", "4",
            "WebkitHyphens", "none",
            "MozHyphens", "none",
            "hyphens", "none",
            "padding", "16px",
            "overflow", "auto",
            "border", "1px solid #dddddd",
            "backgroundColor", "white"));
        BASES.put("oneDark", entries(
            "backgroundColor", "#282c34",
            "color", "#abb2bf",
            "textShadow", "0 1px rgba(0, 0, 0, 0.3)",
            "fontFamily", "'Fira Code', 'Fira Mono', Menlo, Consolas, 'DejaVu Sans Mono', monospace",
            "direction", "ltr",
            "textAlign", "left",
            "whiteSpace", "pre",
            "wordSpacing", "normal",
            "wordBreak", "normal",
            "lineHeight", "1.5",
            "MozTabSize", "2",
            "OTabSize", "2",
            "tabSize", "2",
            "WebkitHyphens", "none",
            "MozHyphens", "none",
            "hyphens", "none",
            "padding", "16px",
            "overflow", "auto",
            "borderRadius", "6px"));
        BASES.put("oneLight", entries(
            "backgroundColor", "#fafafa",
            "color", "#383a42",
            "fontFamily", "'Fira Code', 'Fira Mono', Menlo, Consolas, 'DejaVu Sans Mono', monospace",
            "direction", "ltr",
            "textAlign", "left",
            "whiteSpace", "pre",
            "wordSpacing", "normal",
            "wordBreak", "normal",
            "lineHeight", "1.5",
            "MozTabSize", "2",
            "OTabSize", "2",
            "tabSize", "2",
            "WebkitHyphens", "none",
            "MozHyphens", "none",
            "hyphens", "none",
            "padding", "16px",
            "overflow", "auto",
            "borderRadius", "6px"));

        Map<String, Map<String, String>> vscDarkPlus = new LinkedHashMap<>();
        vscDarkPlus.put("comment", entries("color", "#6a9955"));
        vscDarkPlus.put("prolog", entries("color", "#6a9955"));
        vscDarkPlus.put("punctuation", entries("color", "#d4d4d4"));
        vscDarkPlus.put("property", entries("color", "#9cdcfe"));
        vscDarkPlus.put("tag", entries("color", "#569cd6"));
        vscDarkPlus.put("boolean", entries("color", "#569cd6"));
        vscDarkPlus.put("number", entries("color", "#b5cea8"));
        vscDarkPlus.put("constant", entries("color", "#9cdcfe"));
        vscDarkPlus.put("symbol", entries("color", "#b5cea8"));
        vscDarkPlus.put("inserted", entries("color", "#b5cea8"));
        vscDarkPlus.put("unit", entries("color", "#b5cea8"));
        vscDarkPlus.put("selector", entries("color", "#d7ba7d"));
        vscDarkPlus.put("attr-name", entries("color", "#9cdcfe"));
        vscDarkPlus.put("string", entries("color", "#ce9178"));
        vscDarkPlus.put("char", entries("color", "#ce9178"));
        vscDarkPlus.put("builtin", entries("color", "#ce9178"));
        vscDarkPlus.put("deleted", entries("color", "#ce9178"));
        vscDarkPlus.put("operator", entries("color", "#d4d4d4"));
        vscDarkPlus.put("entity", entries("color", "#569cd6"));
        vscDarkPlus.put("atrule", entries("color", "#ce9178"));
        vscDarkPlus.put("keyword", entries("color", "#569CD6"));
        vscDarkPlus.put("function", entries("color", "#dcdcaa"));
        vscDarkPlus.put("regex", entries("color", "#d16969"));
        vscDarkPlus.put("important", entries("color", "#569cd6"));
        vscDarkPlus.put("italic", entries("font-style", "italic"));
        vscDarkPlus.put("class-name", entries("color", "#4ec9b0"));
        vscDarkPlus.put("maybe-class-name", entries("color", "#4ec9b0"));
        vscDarkPlus.put("console", entries("color", "#9cdcfe"));
        vscDarkPlus.put("parameter", entries("color", "#9cdcfe"));
        vscDarkPlus.put("interpolation", entries("color", "#9cdcfe"));
        vscDarkPlus.put("variable", entries("color", "#9cdcfe"));
        vscDarkPlus.put("escape", entries("color", "#d7ba7d"));
        vscDarkPlus.put("cdata", entries("color", "#808080"));
        vscDarkPlus.put("attr-value", entries("color", "#ce9178"));
        vscDarkPlus.put("namespace", entries("color", "#4ec9b0"));
        TOKENS.put("vscDarkPlus", vscDarkPlus);

        Map<String, Map<String, String>> vs = new LinkedHashMap<>();
        vs.put("comment", entries("color", "#008000", "font-style", "italic"));
        vs.put("prolog", entries("color", "#008000", "font-style", "italic"));
        vs.put("doctype", entries("color", "#008000", "font-style", "italic"));
        vs.put("cdata", entries("color", "#008000", "font-style", "italic"));
        vs.put("namespace", entries("opacity", ".7"));
        vs.put("string", entries("color", "#A31515"));
        vs.put("punctuation", entries("color", "#393A34"));
        vs.put("operator", entries("color", "#393A34"));
        vs.put("url", entries("color", "#36acaa"));
        vs.put("symbol", entries("color", "#36acaa"));
        vs.put("number", entries("color", "#36acaa"));
        vs.put("boolean", entries("color", "#36acaa"));
        vs.put("variable", entries("color", "#36acaa"));
        vs.put("constant", entries("color", "#36acaa"));
        vs.put("inserted", entries("color", "#36acaa"));
        vs.put("atrule", entries("color", "#0000ff"));
        vs.put("keyword", entries("color", "#0000ff"));
        vs.put("attr-value", entries("color", "#0000ff"));
        vs.put("function", entries("color", "#393A34"));
        vs.put("deleted", entries("color", "#9a050f"));
        vs.put("selector", entries("color", "#800000"));
        vs.put("important", entries("color", "#e90", "font-weight", "bold"));
        vs.put("bold", entries("font-weight", "bold"));
        vs.put("italic", entries("font-style", "italic"));
        vs.put("class-name", entries("color", "#2B91AF"));
        vs.put("tag", entries("color", "#800000"));
        vs.put("attr-name", entries("color", "#ff0000"));
        vs.put("property", entries("color", "#ff0000"));
        vs.put("regex", entries("color", "#ff0000"));
        vs.put("entity", entries("color", "#ff0000"));
        TOKENS.put("vs", vs);

        Map<String, Map<String, String>> oneDark = new LinkedHashMap<>();
        oneDark.put("comment", entries("color", "#5c6370", "font-style", "italic"));
        oneDark.put("prolog", entries("color", "#5c6370"));
        oneDark.put("cdata", entries("color", "#5c6370"));
        oneDark.put("doctype", entries("color", "#abb2bf"));
        oneDark.put("punctuation", entries("color", "#abb2bf"));
        oneDark.put("entity", entries("color", "#abb2bf", "cursor", "help"));
        oneDark.put("attr-name", entries("color", "#d19a66"));
        oneDark.put("class-name", entries("color", "#d19a66"));
        oneDark.put("boolean", entries("color", "#d19a66"));
        oneDark.put("constant", entries("color", "#d19a66"));
        oneDark.put("number", entries("color", "#d19a66"));
        oneDark.put("atrule", entries("color", "#d19a66"));
        oneDark.put("keyword", entries("color", "#c678dd"));
        oneDark.put("property", entries("color", "#e06c75"));
        oneDark.put("tag", entries("color", "#e06c75"));
        oneDark.put("symbol", entries("color", "#e06c75"));
        oneDark.put("deleted", entries("color", "#e06c75"));
        oneDark.put("important", entries("color", "#e06c75"));
        oneDark.put("selector", entries("color", "#98c379"));
        oneDark.put("string", entries("color", "#98c379"));
        oneDark.put("char", entries("color", "#98c379"));
        oneDark.put("builtin", entries("color", "#98c379"));
        oneDark.put("inserted", entries("color", "#98c379"));
        oneDark.put("regex", entries("color", "#98c379"));
        oneDark.put("attr-value", entries("color", "#98c379"));
        oneDark.put("variable", entries("color", "#61afef"));
        oneDark.put("operator", entries("color", "#61afef"));
        oneDark.put("function", entries("color", "#61afef"));
        oneDark.put("url", entries("color", "#56b6c2"));
        oneDark.put("bold", entries("font-weight", "bold"));
        oneDark.put("italic", entries("font-style", "italic"));
        oneDark.put("namespace", entries("opacity", "0.8"));
        TOKENS.put("oneDark", oneDark);

        Map<String, Map<String, String>> oneLight = new LinkedHashMap<>();
        oneLight.put("comment", entries("color", "#a0a1a7", "font-style", "italic"));
        oneLight.put("prolog", entries("color", "#a0a1a7"));
        oneLight.put("cdata", entries("color", "#a0a1a7"));
        oneLight.put("doctype", entries("color", "#383a42"));
        oneLight.put("punctuation", entries("color", "#383a42"));
        oneLight.put("entity", entries("color", "#383a42", "cursor", "help"));
        oneLight.put("attr-name", entries("color", "#b76b01"));
        oneLight.put("class-name", entries("color", "#b76b01"));
        oneLight.put("boolean", entries("color", "#b76b01"));
        oneLight.put("constant", entries("color", "#b76b01"));
        oneLight.put("number", entries("color", "#b76b01"));
        oneLight.put("atrule", entries("color", "#b76b01"));
        oneLight.put("keyword", entries("color", "#a626a4"));
        oneLight.put("property", entries("color", "#e45649"));
        oneLight.put("tag", entries("color", "#e45649"));
        oneLight.put("symbol", entries("color", "#e45649"));
        oneLight.put("deleted", entries("color", "#e45649"));
        oneLight.put("important", entries("color", "#e45649"));
        oneLight.put("selector", entries("color", "#50a14f"));
        oneLight.put("string", entries("color", "#50a14f"));
        oneLight.put("char", entries("color", "#50a14f"));
        oneLight.put("builtin", entries("color", "#50a14f"));
        oneLight.put("inserted", entries("color", "#50a14f"));
        oneLight.put("regex", entries("color", "#50a14f"));
        oneLight.put("attr-value", entries("color", "#50a14f"));
        oneLight.put("variable", entries("color", "#4078f2"));
        oneLight.put("operator", entries("color", "#4078f2"));
        oneLight.put("function", entries("color", "#4078f2"));
        oneLight.put("url", entries("color", "#0184bc"));
        oneLight.put("bold", entries("font-weight", "bold"));
        oneLight.put("italic", entries("font-style", "italic"));
        oneLight.put("namespace", entries("opacity", "0.8"));
        TOKENS.put("oneLight", oneLight);
    }

    private CodeBlockThemes() {
    }

    static String resolve(String name) {
        return BASES.containsKey(name) ? name : "oneDark";
    }

    static Map<String, String> base(String name) {
        return BASES.get(resolve(name));
    }

    static Map<String, String> token(String name, String token) {
        return TOKENS.get(resolve(name)).getOrDefault(token, Map.of());
    }

    private static Map<String, String> entries(String... pairs) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put(pairs[i], pairs[i + 1]);
        }
        return map;
    }
}
