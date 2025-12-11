package io.sevk.markup;

import java.util.*;
import java.util.regex.*;

/**
 * Renders Sevk markup to email-compatible HTML.
 */
public class MarkupRenderer {
    private static final String DOCTYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
    private static final String DEFAULT_FONT_FAMILY = "ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif";

    /**
     * Render Sevk markup to HTML.
     */
    public static String render(String markup) {
        return render(markup, null);
    }

    /**
     * Render Sevk markup to HTML with optional head settings.
     */
    public static String render(String markup, EmailHeadSettings headSettings) {
        if (markup == null) {
            markup = "";
        }

        String contentToProcess;
        EmailHeadSettings settings;

        if (headSettings != null) {
            contentToProcess = markup;
            settings = headSettings;
        } else {
            ParsedEmailContent parsed = parseEmailHTML(markup);
            contentToProcess = parsed.body;
            settings = parsed.headSettings;
        }

        String normalized = normalizeMarkup(contentToProcess);
        String processed = processMarkup(normalized);

        // Build head content
        String titleTag = settings.title != null && !settings.title.isEmpty()
            ? "<title>" + settings.title + "</title>" : "";

        String fontLinks = generateFontLinks(settings.fonts);

        String customStyles = settings.styles != null && !settings.styles.isEmpty()
            ? "<style type=\"text/css\">" + settings.styles + "</style>" : "";

        String previewText = settings.previewText != null && !settings.previewText.isEmpty()
            ? "<div style=\"display:none;font-size:1px;color:#ffffff;line-height:1px;max-height:0px;max-width:0px;opacity:0;overflow:hidden;\">" + settings.previewText + "</div>"
            : "";

        return DOCTYPE + "\n" +
            "<html lang=\"en\" dir=\"ltr\">\n" +
            "<head>\n" +
            "<meta content=\"text/html; charset=UTF-8\" http-equiv=\"Content-Type\"/>\n" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
            titleTag + "\n" +
            fontLinks + "\n" +
            customStyles + "\n" +
            "</head>\n" +
            "<body style=\"margin:0;padding:0;font-family:" + DEFAULT_FONT_FAMILY + ";background-color:#ffffff\">\n" +
            previewText + "\n" +
            processed + "\n" +
            "</body>\n" +
            "</html>";
    }

    private static String normalizeMarkup(String content) {
        String result = content;

        // Replace <link> with <sevk-link>
        if (result.contains("<link")) {
            result = result.replaceAll("(?i)<link\\s+href=", "<sevk-link href=");
            result = result.replace("</link>", "</sevk-link>");
        }

        if (!result.contains("<sevk-email") && !result.contains("<email") && !result.contains("<mail")) {
            result = "<mail><body>" + result + "</body></mail>";
        }

        return result;
    }

    private static String generateFontLinks(List<FontConfig> fonts) {
        if (fonts == null || fonts.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (FontConfig font : fonts) {
            sb.append("<link href=\"").append(font.url).append("\" rel=\"stylesheet\" type=\"text/css\" />\n");
        }
        return sb.toString();
    }

    /**
     * Parse email HTML and extract head settings.
     */
    public static ParsedEmailContent parseEmailHTML(String content) {
        if (content.contains("<email>") || content.contains("<email ") ||
            content.contains("<mail>") || content.contains("<mail ")) {
            return parseSevkMarkup(content);
        }
        ParsedEmailContent result = new ParsedEmailContent();
        result.body = content;
        result.headSettings = new EmailHeadSettings();
        return result;
    }

    private static ParsedEmailContent parseSevkMarkup(String content) {
        EmailHeadSettings headSettings = new EmailHeadSettings();

        // Extract title
        Pattern titlePattern = Pattern.compile("<title[^>]*>([\\s\\S]*?)</title>", Pattern.CASE_INSENSITIVE);
        Matcher titleMatcher = titlePattern.matcher(content);
        if (titleMatcher.find()) {
            headSettings.title = titleMatcher.group(1).trim();
        }

        // Extract preview
        Pattern previewPattern = Pattern.compile("<preview[^>]*>([\\s\\S]*?)</preview>", Pattern.CASE_INSENSITIVE);
        Matcher previewMatcher = previewPattern.matcher(content);
        if (previewMatcher.find()) {
            headSettings.previewText = previewMatcher.group(1).trim();
        }

        // Extract styles
        Pattern stylePattern = Pattern.compile("<style[^>]*>([\\s\\S]*?)</style>", Pattern.CASE_INSENSITIVE);
        Matcher styleMatcher = stylePattern.matcher(content);
        if (styleMatcher.find()) {
            headSettings.styles = styleMatcher.group(1).trim();
        }

        // Extract fonts
        Pattern fontPattern = Pattern.compile("<font[^>]*name=[\"']([^\"']*)[\"'][^>]*url=[\"']([^\"']*)[\"'][^>]*/?>", Pattern.CASE_INSENSITIVE);
        Matcher fontMatcher = fontPattern.matcher(content);
        headSettings.fonts = new ArrayList<>();
        int fontIndex = 0;
        while (fontMatcher.find()) {
            FontConfig font = new FontConfig();
            font.id = "font-" + fontIndex++;
            font.name = fontMatcher.group(1);
            font.url = fontMatcher.group(2);
            headSettings.fonts.add(font);
        }

        // Extract body
        String body;
        Pattern bodyPattern = Pattern.compile("<body[^>]*>([\\s\\S]*?)</body>", Pattern.CASE_INSENSITIVE);
        Matcher bodyMatcher = bodyPattern.matcher(content);
        if (bodyMatcher.find()) {
            body = bodyMatcher.group(1).trim();
        } else {
            body = content;
            String[] patterns = {
                "<email[^>]*>", "</email>",
                "<mail[^>]*>", "</mail>",
                "<head[^>]*>[\\s\\S]*?</head>",
                "<title[^>]*>[\\s\\S]*?</title>",
                "<preview[^>]*>[\\s\\S]*?</preview>",
                "<style[^>]*>[\\s\\S]*?</style>",
                "<font[^>]*>[\\s\\S]*?</font>",
                "<font[^>]*/?>",
            };
            for (String pattern : patterns) {
                body = body.replaceAll("(?i)" + pattern, "");
            }
            body = body.trim();
        }

        ParsedEmailContent result = new ParsedEmailContent();
        result.body = body;
        result.headSettings = headSettings;
        return result;
    }

    private static String processMarkup(String content) {
        String result = content;

        // Process section tags
        result = processTag(result, "section", (attrs, inner) -> {
            Map<String, String> style = extractAllStyleAttributes(attrs);
            String styleStr = styleToString(style);
            return "<table align=\"center\" width=\"100%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\" role=\"presentation\" style=\"" + styleStr + "\">\n" +
                "<tbody>\n<tr>\n<td>" + inner + "</td>\n</tr>\n</tbody>\n</table>";
        });

        // Process row tags
        result = processTag(result, "row", (attrs, inner) -> {
            Map<String, String> style = extractAllStyleAttributes(attrs);
            String styleStr = styleToString(style);
            return "<table align=\"center\" width=\"100%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\" role=\"presentation\" style=\"" + styleStr + "\">\n" +
                "<tbody style=\"width:100%\">\n<tr style=\"width:100%\">" + inner + "</tr>\n</tbody>\n</table>";
        });

        // Process column tags
        result = processTag(result, "column", (attrs, inner) -> {
            Map<String, String> style = extractAllStyleAttributes(attrs);
            String styleStr = styleToString(style);
            return "<td style=\"" + styleStr + "\">" + inner + "</td>";
        });

        // Process container tags
        result = processTag(result, "container", (attrs, inner) -> {
            Map<String, String> style = extractAllStyleAttributes(attrs);
            String styleStr = styleToString(style);
            return "<table align=\"center\" width=\"100%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\" role=\"presentation\" style=\"" + styleStr + "\">\n" +
                "<tbody>\n<tr style=\"width:100%\">\n<td>" + inner + "</td>\n</tr>\n</tbody>\n</table>";
        });

        // Process heading tags
        result = processTag(result, "heading", (attrs, inner) -> {
            String level = attrs.getOrDefault("level", "1");
            Map<String, String> style = extractAllStyleAttributes(attrs);
            String styleStr = styleToString(style);
            return "<h" + level + " style=\"" + styleStr + "\">" + inner + "</h" + level + ">";
        });

        // Process paragraph tags
        result = processTag(result, "paragraph", (attrs, inner) -> {
            Map<String, String> style = extractAllStyleAttributes(attrs);
            String styleStr = styleToString(style);
            return "<p style=\"" + styleStr + "\">" + inner + "</p>";
        });

        // Process text tags
        result = processTag(result, "text", (attrs, inner) -> {
            Map<String, String> style = extractAllStyleAttributes(attrs);
            String styleStr = styleToString(style);
            return "<span style=\"" + styleStr + "\">" + inner + "</span>";
        });

        // Process button tags with MSO compatibility
        result = processTag(result, "button", MarkupRenderer::processButton);

        // Process image tags
        result = processTag(result, "image", (attrs, inner) -> {
            String src = attrs.getOrDefault("src", "");
            String alt = attrs.getOrDefault("alt", "");
            String width = attrs.get("width");
            String height = attrs.get("height");

            Map<String, String> style = extractAllStyleAttributes(attrs);
            style.putIfAbsent("outline", "none");
            style.putIfAbsent("border", "none");
            style.putIfAbsent("text-decoration", "none");

            String styleStr = styleToString(style);
            String widthAttr = width != null ? " width=\"" + width + "\"" : "";
            String heightAttr = height != null ? " height=\"" + height + "\"" : "";

            return "<img src=\"" + src + "\" alt=\"" + alt + "\"" + widthAttr + heightAttr + " style=\"" + styleStr + "\" />";
        });

        // Process divider tags
        result = processTag(result, "divider", (attrs, inner) -> {
            Map<String, String> style = extractAllStyleAttributes(attrs);
            String styleStr = styleToString(style);
            String classAttr = attrs.get("class");
            if (classAttr == null) classAttr = attrs.get("className");
            String classStr = classAttr != null ? " class=\"" + classAttr + "\"" : "";
            return "<hr style=\"" + styleStr + "\"" + classStr + " />";
        });

        // Process link tags
        result = processTag(result, "sevk-link", (attrs, inner) -> {
            String href = attrs.getOrDefault("href", "#");
            String target = attrs.getOrDefault("target", "_blank");
            Map<String, String> style = extractAllStyleAttributes(attrs);
            String styleStr = styleToString(style);
            return "<a href=\"" + href + "\" target=\"" + target + "\" style=\"" + styleStr + "\">" + inner + "</a>";
        });

        // Process list tags
        result = processTag(result, "list", (attrs, inner) -> {
            String listType = attrs.getOrDefault("type", "unordered");
            String tag = "ordered".equals(listType) ? "ol" : "ul";
            Map<String, String> style = extractAllStyleAttributes(attrs);
            String listStyleType = attrs.get("list-style-type");
            if (listStyleType != null) {
                style.put("list-style-type", listStyleType);
            }
            String styleStr = styleToString(style);
            String classAttr = attrs.get("class");
            if (classAttr == null) classAttr = attrs.get("className");
            String classStr = classAttr != null ? " class=\"" + classAttr + "\"" : "";
            return "<" + tag + " style=\"" + styleStr + "\"" + classStr + ">" + inner + "</" + tag + ">";
        });

        // Process list item tags
        result = processTag(result, "li", (attrs, inner) -> {
            Map<String, String> style = extractAllStyleAttributes(attrs);
            String styleStr = styleToString(style);
            String classAttr = attrs.get("class");
            if (classAttr == null) classAttr = attrs.get("className");
            String classStr = classAttr != null ? " class=\"" + classAttr + "\"" : "";
            return "<li style=\"" + styleStr + "\"" + classStr + ">" + inner + "</li>";
        });

        // Process codeblock tags
        result = processTag(result, "codeblock", (attrs, inner) -> {
            Map<String, String> style = extractAllStyleAttributes(attrs);
            style.putIfAbsent("width", "100%");
            style.putIfAbsent("box-sizing", "border-box");
            String styleStr = styleToString(style);
            String escaped = inner.replace("<", "&lt;").replace(">", "&gt;");
            return "<pre style=\"" + styleStr + "\"><code>" + escaped + "</code></pre>";
        });

        // Clean up wrapper tags
        String[] wrapperPatterns = {
            "<sevk-email[^>]*>", "</sevk-email>",
            "<sevk-body[^>]*>", "</sevk-body>",
            "<email[^>]*>", "</email>",
            "<mail[^>]*>", "</mail>",
            "<body[^>]*>", "</body>",
        };
        for (String pattern : wrapperPatterns) {
            result = result.replaceAll("(?i)" + pattern, "");
        }

        return result.trim();
    }

    /**
     * Process button with MSO compatibility (like Node.js)
     */
    private static String processButton(Map<String, String> attrs, String inner) {
        String href = attrs.getOrDefault("href", "#");
        Map<String, String> style = extractAllStyleAttributes(attrs);

        // Parse padding
        int[] padding = parsePadding(style);
        int paddingTop = padding[0];
        int paddingRight = padding[1];
        int paddingBottom = padding[2];
        int paddingLeft = padding[3];

        int y = paddingTop + paddingBottom;
        int textRaise = pxToPt(y);

        double[] plResult = computeFontWidthAndSpaceCount(paddingLeft);
        double plFontWidth = plResult[0];
        int plSpaceCount = (int) plResult[1];

        double[] prResult = computeFontWidthAndSpaceCount(paddingRight);
        double prFontWidth = prResult[0];
        int prSpaceCount = (int) prResult[1];

        Map<String, String> buttonStyle = new LinkedHashMap<>();
        buttonStyle.put("line-height", "100%");
        buttonStyle.put("text-decoration", "none");
        buttonStyle.put("display", "inline-block");
        buttonStyle.put("max-width", "100%");
        buttonStyle.put("mso-padding-alt", "0px");

        // Merge with extracted styles
        buttonStyle.putAll(style);

        // Override padding with parsed values
        buttonStyle.put("padding-top", paddingTop + "px");
        buttonStyle.put("padding-right", paddingRight + "px");
        buttonStyle.put("padding-bottom", paddingBottom + "px");
        buttonStyle.put("padding-left", paddingLeft + "px");

        String styleStr = styleToString(buttonStyle);

        StringBuilder leftMsoSpaces = new StringBuilder();
        for (int i = 0; i < plSpaceCount; i++) leftMsoSpaces.append("&#8202;");

        StringBuilder rightMsoSpaces = new StringBuilder();
        for (int i = 0; i < prSpaceCount; i++) rightMsoSpaces.append("&#8202;");

        return "<a href=\"" + href + "\" target=\"_blank\" style=\"" + styleStr + "\">" +
            "<!--[if mso]><i style=\"mso-font-width:" + Math.round(plFontWidth * 100) + "%;mso-text-raise:" + textRaise + "\" hidden>" + leftMsoSpaces + "</i><![endif]-->" +
            "<span style=\"max-width:100%;display:inline-block;line-height:120%;mso-padding-alt:0px;mso-text-raise:" + pxToPt(paddingBottom) + "\">" + inner + "</span>" +
            "<!--[if mso]><i style=\"mso-font-width:" + Math.round(prFontWidth * 100) + "%\" hidden>" + rightMsoSpaces + "&#8203;</i><![endif]-->" +
            "</a>";
    }

    /**
     * Parse padding values from style
     */
    private static int[] parsePadding(Map<String, String> style) {
        String padding = style.get("padding");
        if (padding != null) {
            String[] parts = padding.trim().split("\\s+");
            switch (parts.length) {
                case 1:
                    int val = parsePx(parts[0]);
                    return new int[]{val, val, val, val};
                case 2:
                    int vertical = parsePx(parts[0]);
                    int horizontal = parsePx(parts[1]);
                    return new int[]{vertical, horizontal, vertical, horizontal};
                case 4:
                    return new int[]{parsePx(parts[0]), parsePx(parts[1]), parsePx(parts[2]), parsePx(parts[3])};
            }
        }

        int pt = parsePx(style.getOrDefault("padding-top", "0"));
        int pr = parsePx(style.getOrDefault("padding-right", "0"));
        int pb = parsePx(style.getOrDefault("padding-bottom", "0"));
        int pl = parsePx(style.getOrDefault("padding-left", "0"));
        return new int[]{pt, pr, pb, pl};
    }

    private static int parsePx(String s) {
        try {
            return Integer.parseInt(s.replace("px", "").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Convert px to pt for MSO
     */
    private static int pxToPt(int px) {
        return (px * 3) / 4;
    }

    /**
     * Compute font width and space count for MSO padding
     */
    private static double[] computeFontWidthAndSpaceCount(int expectedWidth) {
        if (expectedWidth == 0) {
            return new double[]{0, 0};
        }

        int smallestSpaceCount = 0;
        double maxFontWidth = 5.0;

        while (true) {
            double requiredFontWidth;
            if (smallestSpaceCount > 0) {
                requiredFontWidth = (double) expectedWidth / smallestSpaceCount / 2.0;
            } else {
                requiredFontWidth = Double.POSITIVE_INFINITY;
            }

            if (requiredFontWidth <= maxFontWidth) {
                return new double[]{requiredFontWidth, smallestSpaceCount};
            }
            smallestSpaceCount++;
        }
    }

    @FunctionalInterface
    private interface TagProcessor {
        String process(Map<String, String> attrs, String inner);
    }

    private static String processTag(String content, String tagName, TagProcessor processor) {
        String result = content;
        String openPattern = "<" + tagName + "([^>]*)>";
        String closeTag = "</" + tagName + ">";
        String openTagStart = "<" + tagName;
        Pattern openRe = Pattern.compile(openPattern, Pattern.CASE_INSENSITIVE);

        int maxIterations = 10000;
        int iterations = 0;

        while (iterations < maxIterations) {
            iterations++;

            Matcher matcher = openRe.matcher(result);
            List<int[]> matches = new ArrayList<>();
            List<String> attrsStrs = new ArrayList<>();

            while (matcher.find()) {
                matches.add(new int[]{matcher.start(), matcher.end()});
                attrsStrs.add(matcher.group(1));
            }

            if (matches.isEmpty()) break;

            boolean processed = false;

            // Find the innermost tag (one that has no nested same tags)
            for (int i = matches.size() - 1; i >= 0; i--) {
                int start = matches.get(i)[0];
                int innerStart = matches.get(i)[1];
                String attrsStr = attrsStrs.get(i);

                // Find the next close tag after this opening tag
                int closePos = result.toLowerCase().indexOf(closeTag.toLowerCase(), innerStart);
                if (closePos == -1) continue;

                String inner = result.substring(innerStart, closePos);

                // Check if there's another opening tag inside
                if (inner.toLowerCase().contains(openTagStart.toLowerCase())) {
                    // This tag has nested same tags, skip it
                    continue;
                }

                // This is an innermost tag, process it
                Map<String, String> attrs = parseAttributes(attrsStr);
                String replacement = processor.process(attrs, inner);
                int end = closePos + closeTag.length();

                result = result.substring(0, start) + replacement + result.substring(end);
                processed = true;
                break;
            }

            if (!processed) break;
        }

        return result;
    }

    private static Map<String, String> parseAttributes(String attrsStr) {
        Map<String, String> attrs = new HashMap<>();
        Pattern re = Pattern.compile("([\\w-]+)=[\"']([^\"']*)[\"']");
        Matcher matcher = re.matcher(attrsStr);
        while (matcher.find()) {
            attrs.put(matcher.group(1), matcher.group(2));
        }
        return attrs;
    }

    /**
     * Extract all style attributes from element attributes (like Node.js extractStyleAttributes)
     */
    private static Map<String, String> extractAllStyleAttributes(Map<String, String> attrs) {
        Map<String, String> style = new LinkedHashMap<>();

        // Typography attributes
        String textColor = attrs.get("text-color");
        if (textColor != null) {
            style.put("color", textColor);
        } else if (attrs.get("color") != null) {
            style.put("color", attrs.get("color"));
        }

        if (attrs.get("background-color") != null) style.put("background-color", attrs.get("background-color"));
        if (attrs.get("font-size") != null) style.put("font-size", attrs.get("font-size"));
        if (attrs.get("font-family") != null) style.put("font-family", attrs.get("font-family"));
        if (attrs.get("font-weight") != null) style.put("font-weight", attrs.get("font-weight"));
        if (attrs.get("line-height") != null) style.put("line-height", attrs.get("line-height"));
        if (attrs.get("text-align") != null) style.put("text-align", attrs.get("text-align"));
        if (attrs.get("text-decoration") != null) style.put("text-decoration", attrs.get("text-decoration"));

        // Dimensions
        if (attrs.get("width") != null) style.put("width", attrs.get("width"));
        if (attrs.get("height") != null) style.put("height", attrs.get("height"));
        if (attrs.get("max-width") != null) style.put("max-width", attrs.get("max-width"));
        if (attrs.get("min-height") != null) style.put("min-height", attrs.get("min-height"));

        // Spacing - Padding
        if (attrs.get("padding") != null) {
            style.put("padding", attrs.get("padding"));
        } else {
            if (attrs.get("padding-top") != null) style.put("padding-top", attrs.get("padding-top"));
            if (attrs.get("padding-right") != null) style.put("padding-right", attrs.get("padding-right"));
            if (attrs.get("padding-bottom") != null) style.put("padding-bottom", attrs.get("padding-bottom"));
            if (attrs.get("padding-left") != null) style.put("padding-left", attrs.get("padding-left"));
        }

        // Spacing - Margin
        if (attrs.get("margin") != null) {
            style.put("margin", attrs.get("margin"));
        } else {
            if (attrs.get("margin-top") != null) style.put("margin-top", attrs.get("margin-top"));
            if (attrs.get("margin-right") != null) style.put("margin-right", attrs.get("margin-right"));
            if (attrs.get("margin-bottom") != null) style.put("margin-bottom", attrs.get("margin-bottom"));
            if (attrs.get("margin-left") != null) style.put("margin-left", attrs.get("margin-left"));
        }

        // Borders
        if (attrs.get("border") != null) {
            style.put("border", attrs.get("border"));
        } else {
            if (attrs.get("border-top") != null) style.put("border-top", attrs.get("border-top"));
            if (attrs.get("border-right") != null) style.put("border-right", attrs.get("border-right"));
            if (attrs.get("border-bottom") != null) style.put("border-bottom", attrs.get("border-bottom"));
            if (attrs.get("border-left") != null) style.put("border-left", attrs.get("border-left"));
            if (attrs.get("border-color") != null) style.put("border-color", attrs.get("border-color"));
            if (attrs.get("border-width") != null) style.put("border-width", attrs.get("border-width"));
            if (attrs.get("border-style") != null) style.put("border-style", attrs.get("border-style"));
        }

        // Border Radius
        if (attrs.get("border-radius") != null) {
            style.put("border-radius", attrs.get("border-radius"));
        } else {
            if (attrs.get("border-top-left-radius") != null) style.put("border-top-left-radius", attrs.get("border-top-left-radius"));
            if (attrs.get("border-top-right-radius") != null) style.put("border-top-right-radius", attrs.get("border-top-right-radius"));
            if (attrs.get("border-bottom-left-radius") != null) style.put("border-bottom-left-radius", attrs.get("border-bottom-left-radius"));
            if (attrs.get("border-bottom-right-radius") != null) style.put("border-bottom-right-radius", attrs.get("border-bottom-right-radius"));
        }

        return style;
    }

    /**
     * Convert style map to inline style string
     */
    private static String styleToString(Map<String, String> style) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : style.entrySet()) {
            if (sb.length() > 0) sb.append(";");
            sb.append(entry.getKey()).append(":").append(entry.getValue());
        }
        return sb.toString();
    }

    /**
     * Font configuration
     */
    public static class FontConfig {
        public String id = "";
        public String name = "";
        public String url = "";
    }

    /**
     * Head settings for email generation
     */
    public static class EmailHeadSettings {
        public String title = "";
        public String previewText = "";
        public String styles = "";
        public List<FontConfig> fonts = new ArrayList<>();
    }

    /**
     * Parsed email content
     */
    public static class ParsedEmailContent {
        public String body = "";
        public EmailHeadSettings headSettings = new EmailHeadSettings();
    }
}
