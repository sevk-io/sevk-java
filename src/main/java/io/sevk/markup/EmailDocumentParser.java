package io.sevk.markup;

import io.sevk.markup.Renderer.EmailHeadSettings;
import io.sevk.markup.Renderer.FontConfig;
import io.sevk.markup.Renderer.ParsedEmailContent;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class EmailDocumentParser {
    private static final Pattern SEVK_DOCUMENT = Pattern.compile("<(?:email|mail)[\\s>]", Pattern.CASE_INSENSITIVE);
    private static final Pattern NAME = Pattern.compile("name=[\"']([^\"']*)[\"']", Pattern.CASE_INSENSITIVE);
    private static final Pattern URL = Pattern.compile("url=[\"']([^\"']*)[\"']", Pattern.CASE_INSENSITIVE);
    private static final Pattern HREF = Pattern.compile("href=[\"']([^\"']*)[\"']", Pattern.CASE_INSENSITIVE);
    private static final Pattern FAMILY = Pattern.compile("family=([^:&]+)");
    private static final Pattern LANG = Pattern.compile("lang=[\"']([^\"']*)[\"']", Pattern.CASE_INSENSITIVE);
    private static final Pattern DIR = Pattern.compile("dir=[\"']([^\"']*)[\"']", Pattern.CASE_INSENSITIVE);

    private EmailDocumentParser() {
    }

    static String normalizeMarkup(String content) {
        String result = content.replaceAll("(?i)<link(\\s+(?:[\\w-]+=(?:\"[^\"]*\"|'[^']*')\\s+)*)href=", "<sevk-link$1href=");
        result = result.replaceAll("(?i)</link>", "</sevk-link>");

        if (!result.contains("<sevk-email") && !result.contains("<email") && !result.contains("<mail")) {
            result = "<mail><body>" + result + "</body></mail>";
        }

        return result;
    }

    static String generateFontLinks(List<FontConfig> fonts) {
        if (fonts == null || fonts.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (FontConfig font : fonts) {
            sb.append("<link href=\"").append(font.url).append("\" rel=\"stylesheet\" type=\"text/css\" />\n");
        }

        return sb.toString().stripTrailing();
    }

    static ParsedEmailContent parseEmailHTML(String content) {
        if (SEVK_DOCUMENT.matcher(content).find()) {
            return parseSevkMarkup(content);
        }

        return parseHtmlDocument(content);
    }

    private static ParsedEmailContent parseHtmlDocument(String content) {
        EmailHeadSettings headSettings = new EmailHeadSettings();
        headSettings.title = trimmed(TagScanner.getFirstTagContent(content, "title"));
        headSettings.styles = String.join("\n", TagScanner.getAllTagContents(content, "style"));

        for (String attributes : TagScanner.getTagAttributes(content, "link")) {
            Matcher hrefMatcher = HREF.matcher(attributes);
            if (!hrefMatcher.find() || !hrefMatcher.group(1).toLowerCase(Locale.ROOT).contains("fonts")) {
                continue;
            }

            Matcher familyMatcher = FAMILY.matcher(hrefMatcher.group(1));
            FontConfig font = new FontConfig();
            font.id = "font-" + headSettings.fonts.size();
            font.name = familyMatcher.find()
                ? familyMatcher.group(1).replace("+", " ")
                : "Font " + (headSettings.fonts.size() + 1);
            font.url = hrefMatcher.group(1);
            headSettings.fonts.add(font);
        }

        String bodyContent = TagScanner.getFirstTagContent(content, "body");
        String body = bodyContent != null ? bodyContent : content;

        String[] preview = TagScanner.findHiddenPreviewDiv(content);
        if (preview != null) {
            headSettings.previewText = preview[0].trim();
            body = body.replace(preview[1], "");
        }

        ParsedEmailContent result = new ParsedEmailContent();
        result.body = body.trim();
        result.headSettings = headSettings;
        return result;
    }

    private static ParsedEmailContent parseSevkMarkup(String content) {
        EmailHeadSettings headSettings = new EmailHeadSettings();

        List<String> rootAttributes = TagScanner.getTagAttributes(content, "email");
        if (rootAttributes.isEmpty()) {
            rootAttributes = TagScanner.getTagAttributes(content, "mail");
        }
        if (!rootAttributes.isEmpty()) {
            Matcher langMatcher = LANG.matcher(rootAttributes.get(0));
            if (langMatcher.find()) {
                headSettings.lang = langMatcher.group(1);
            }
            Matcher dirMatcher = DIR.matcher(rootAttributes.get(0));
            if (dirMatcher.find()) {
                headSettings.dir = dirMatcher.group(1);
            }
        }

        headSettings.title = trimmed(TagScanner.getFirstTagContent(content, "title"));
        headSettings.previewText = trimmed(TagScanner.getFirstTagContent(content, "preview"));
        headSettings.styles = trimmed(TagScanner.getFirstTagContent(content, "style"));

        Set<String> seenUrls = new HashSet<>();
        for (String attributes : TagScanner.getTagAttributes(content, "font")) {
            Matcher nameMatcher = NAME.matcher(attributes);
            Matcher urlMatcher = URL.matcher(attributes);
            if (!nameMatcher.find() || !urlMatcher.find() || !seenUrls.add(urlMatcher.group(1))) {
                continue;
            }

            FontConfig font = new FontConfig();
            font.id = "font-" + headSettings.fonts.size();
            font.name = nameMatcher.group(1);
            font.url = urlMatcher.group(1);
            headSettings.fonts.add(font);
        }

        String body = trimmed(TagScanner.getFirstTagContent(content, "body"));
        if (body.isEmpty()) {
            String stripped = TagScanner.replaceSelfClosing(content, "email", false, attributes -> "");
            stripped = TagScanner.replaceSelfClosing(stripped, "mail", false, attributes -> "")
                .replaceAll("(?i)</(?:email|mail)>", "");
            for (String tag : new String[] {"head", "title", "preview", "style"}) {
                stripped = TagScanner.stripTagPairs(stripped, tag);
            }
            body = TagScanner.replaceSelfClosing(stripped, "font", false, attributes -> "")
                .replaceAll("(?i)</font>", "")
                .trim();
        }

        ParsedEmailContent result = new ParsedEmailContent();
        result.body = body;
        result.headSettings = headSettings;
        return result;
    }

    private static String trimmed(String content) {
        return content == null ? "" : content.trim();
    }
}
