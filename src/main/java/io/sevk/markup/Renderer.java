package io.sevk.markup;

import java.util.*;
import java.util.regex.*;

import com.google.gson.Gson;

/**
 * Renders Sevk markup to email-compatible HTML.
 */
public class Renderer {
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

        // Always parse to extract clean body content (strips <mail>/<head> wrapper tags)
        ParsedEmailContent parsed = parseEmailHTML(markup);
        EmailHeadSettings settings = headSettings != null ? headSettings : parsed.headSettings;
        String contentToProcess = parsed.body;

        String normalized = normalizeMarkup(contentToProcess);
        List<String> gapStylesList = new ArrayList<>();
        String processed = processMarkup(normalized, gapStylesList);

        String gapStyles = "";
        if (!gapStylesList.isEmpty()) {
            StringBuilder gsb = new StringBuilder();
            gsb.append("@media only screen and (max-width:479px){");
            for (String gs : gapStylesList) gsb.append(gs);
            gsb.append("}");
            gapStyles = gsb.toString();
        }
        String gapStyleTag = !gapStyles.isEmpty()
            ? "<style type=\"text/css\">" + gapStyles + "</style>" : "";

        // Build head content
        String titleTag = settings.title != null && !settings.title.isEmpty()
            ? "<title>" + settings.title + "</title>" : "";

        String fontLinks = generateFontLinks(settings.fonts);

        String customStyles = settings.styles != null && !settings.styles.isEmpty()
            ? "<style type=\"text/css\">" + settings.styles + "</style>" : "";

        String previewText = settings.previewText != null && !settings.previewText.isEmpty()
            ? "<div style=\"display:none;font-size:1px;color:#ffffff;line-height:1px;max-height:0px;max-width:0px;opacity:0;overflow:hidden;\">" + settings.previewText + "</div>"
            : "";

        String lang = (settings.lang != null && !settings.lang.isEmpty()) ? settings.lang : "en";
        String dir = (settings.dir != null && !settings.dir.isEmpty()) ? settings.dir : "ltr";

        return DOCTYPE + "\n" +
            "<html lang=\"" + lang + "\" dir=\"" + dir + "\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">\n" +
            "<head>\n" +
            "<meta content=\"text/html; charset=UTF-8\" http-equiv=\"Content-Type\"/>\n" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
            "<meta name=\"x-apple-disable-message-reformatting\"/>\n" +
            "<meta content=\"IE=edge\" http-equiv=\"X-UA-Compatible\"/>\n" +
            "<meta name=\"format-detection\" content=\"telephone=no,address=no,email=no,date=no,url=no\"/>\n" +
            "<!--[if mso]>\n" +
            "<noscript>\n" +
            "<xml>\n" +
            "<o:OfficeDocumentSettings>\n" +
            "<o:AllowPNG/>\n" +
            "<o:PixelsPerInch>96</o:PixelsPerInch>\n" +
            "</o:OfficeDocumentSettings>\n" +
            "</xml>\n" +
            "</noscript>\n" +
            "<![endif]-->\n" +
            "<style type=\"text/css\">\n" +
            "#outlook a { padding: 0; }\n" +
            "body { margin: 0; padding: 0; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }\n" +
            "table, td { border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; }\n" +
            ".sevk-row-table { border-collapse: separate !important; }\n" +
            "img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; }\n" +
            "@media only screen and (max-width: 479px) {\n" +
            "  .sevk-row-table { width: 100% !important; }\n" +
            "  .sevk-column { display: block !important; width: 100% !important; max-width: 100% !important; box-sizing: border-box !important; }\n" +
            "}\n" +
            "</style>\n" +
            gapStyleTag + "\n" +
            titleTag + "\n" +
            fontLinks + "\n" +
            customStyles + "\n" +
            "</head>\n" +
            "<body style=\"margin:0;padding:0;word-spacing:normal;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;font-family:" + DEFAULT_FONT_FAMILY + "\">\n" +
            "<div aria-roledescription=\"email\" role=\"article\">\n" +
            previewText + "\n" +
            processed + "\n" +
            "</div>\n" +
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

        // Extract lang and dir from root <mail> or <email> tag
        Pattern rootPattern = Pattern.compile("<(?:email|mail)([^>]*)>", Pattern.CASE_INSENSITIVE);
        Matcher rootMatcher = rootPattern.matcher(content);
        if (rootMatcher.find()) {
            String rootAttrs = rootMatcher.group(1);
            Pattern langPattern = Pattern.compile("lang=[\"']([^\"']*)[\"']", Pattern.CASE_INSENSITIVE);
            Matcher langMatcher = langPattern.matcher(rootAttrs);
            if (langMatcher.find()) {
                headSettings.lang = langMatcher.group(1);
            }
            Pattern dirPattern = Pattern.compile("dir=[\"']([^\"']*)[\"']", Pattern.CASE_INSENSITIVE);
            Matcher dirMatcher = dirPattern.matcher(rootAttrs);
            if (dirMatcher.find()) {
                headSettings.dir = dirMatcher.group(1);
            }
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

    private static String processMarkup(String content, List<String> gapStylesCollector) {
        String result = content;

        // Process block tags BEFORE other tags
        result = processTag(result, "block", (attrs, inner) -> processBlockTag(attrs, inner));

        // Process section tags
        result = processTag(result, "section", (attrs, inner) -> {
            Map<String, String> style = extractAllStyleAttributes(attrs);
            Map<String, String> tdStyle = new LinkedHashMap<>();

            // Padding and text-align belong on the <td>, not the <table>
            if (style.containsKey("padding")) { tdStyle.put("padding", style.remove("padding")); }
            if (style.containsKey("text-align")) { tdStyle.put("text-align", style.remove("text-align")); }

            String styleStr = styleToString(style);
            String tdStyleStr = styleToString(tdStyle);
            return "<table align=\"center\" width=\"100%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\" role=\"presentation\" style=\"" + styleStr + "\">\n" +
                "<tbody>\n<tr>\n<td style=\"" + tdStyleStr + "\">" + inner + "</td>\n</tr>\n</tbody>\n</table>";
        });

        // Process column tags first (before row, so row can count them)
        result = processTag(result, "column", (attrs, inner) -> {
            Map<String, String> style = extractAllStyleAttributes(attrs);
            if (!style.containsKey("vertical-align")) {
                style.put("vertical-align", "top");
            }
            String styleStr = styleToString(style);
            return "<td class=\"sevk-column\" style=\"" + styleStr + "\">" + inner + "</td>";
        });

        // Process row tags
        final int[] rowCounter = {0};
        result = processTag(result, "row", (attrs, inner) -> {
            String gap = attrs.getOrDefault("gap", "0");
            Map<String, String> style = extractAllStyleAttributes(attrs);
            style.remove("gap");

            String gapPx = gap.replace("px", "");
            int gapNum = 0;
            try { gapNum = Integer.parseInt(gapPx); } catch (NumberFormatException e) {}
            String rowId = "sevk-row-" + rowCounter[0]++;

            // Assign equal widths to columns if more than one
            String processedInner = inner;
            Pattern colCountPattern = Pattern.compile("class=\"sevk-column\"");
            Matcher colCountMatcher = colCountPattern.matcher(processedInner);
            int columnCount = 0;
            while (colCountMatcher.find()) columnCount++;

            if (columnCount > 1) {
                String equalWidth = (100 / columnCount) + "%";
                // Add width to columns that don't already have one
                processedInner = processedInner.replaceAll(
                    "<td class=\"sevk-column\" style=\"([^\"]*)\"",
                    "<td class=\"sevk-column\" style=\"__EQUAL_WIDTH_PLACEHOLDER__$1\""
                );
                // Only add width if not already present
                StringBuilder sb = new StringBuilder();
                int searchFrom = 0;
                String placeholder = "__EQUAL_WIDTH_PLACEHOLDER__";
                int phIdx;
                while ((phIdx = processedInner.indexOf(placeholder, searchFrom)) != -1) {
                    sb.append(processedInner, searchFrom, phIdx);
                    int styleStart = phIdx + placeholder.length();
                    int quoteIdx = processedInner.indexOf("\"", styleStart);
                    String existingStyle = processedInner.substring(styleStart, quoteIdx);
                    if (existingStyle.contains("width:")) {
                        sb.append(existingStyle);
                    } else {
                        sb.append("width:").append(equalWidth).append(";").append(existingStyle);
                    }
                    searchFrom = quoteIdx;
                }
                sb.append(processedInner, searchFrom, processedInner.length());
                processedInner = sb.toString();
            }

            // Insert spacer <td> between each column for desktop gap
            if (gapNum > 0) {
                String spacerTd = "</td><td class=\"sevk-gap\" style=\"width:" + gapPx + "px;min-width:" + gapPx + "px\" width=\"" + gapPx + "\"></td><td class=\"sevk-column\"";
                processedInner = processedInner.replaceAll("</td>\\s*<td class=\"sevk-column\"", spacerTd);

                // Collect mobile responsive styles
                gapStylesCollector.add(
                    "." + rowId + " .sevk-gap{display:none !important;}" +
                    "." + rowId + " > tbody > tr > td.sevk-column{display:block !important;width:100% !important;margin-bottom:" + gapPx + "px !important;}" +
                    "." + rowId + " > tbody > tr > td.sevk-column:last-of-type{margin-bottom:0 !important;}"
                );
            }

            String styleStr = styleToString(style);
            return "<table align=\"center\" width=\"100%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\" role=\"presentation\" class=\"sevk-row-table " + rowId + "\" style=\"" + styleStr + "\">\n" +
                "<tbody style=\"width:100%\">\n<tr style=\"width:100%\">" + processedInner + "</tr>\n</tbody>\n</table>";
        });

        // Process container tags - split styles between table and td
        result = processTag(result, "container", (attrs, inner) -> {
            Map<String, String> style = extractAllStyleAttributes(attrs);
            Map<String, String> tdStyle = new LinkedHashMap<>();
            Map<String, String> tableStyle = new LinkedHashMap<>();

            // Visual styles on td, layout styles on table
            Set<String> visualKeys = new HashSet<>(Arrays.asList(
                "background-color", "background-image", "background-size", "background-position", "background-repeat",
                "border", "border-top", "border-right", "border-bottom", "border-left",
                "border-color", "border-width", "border-style",
                "border-radius", "border-top-left-radius", "border-top-right-radius",
                "border-bottom-left-radius", "border-bottom-right-radius",
                "padding", "padding-top", "padding-right", "padding-bottom", "padding-left"
            ));

            for (Map.Entry<String, String> entry : style.entrySet()) {
                if (visualKeys.contains(entry.getKey())) {
                    tdStyle.put(entry.getKey(), entry.getValue());
                } else {
                    tableStyle.put(entry.getKey(), entry.getValue());
                }
            }

            // Add border-collapse: separate when border-radius is used
            boolean hasBorderRadius = tdStyle.containsKey("border-radius")
                || tdStyle.containsKey("border-top-left-radius")
                || tdStyle.containsKey("border-top-right-radius")
                || tdStyle.containsKey("border-bottom-left-radius")
                || tdStyle.containsKey("border-bottom-right-radius");
            if (hasBorderRadius) {
                tableStyle.put("border-collapse", "separate");
            }

            // Make fixed widths responsive: width becomes max-width, width set to 100%
            String widthVal = tableStyle.get("width");
            if (widthVal != null && !"100%".equals(widthVal) && !"auto".equals(widthVal)) {
                if (!tableStyle.containsKey("max-width")) {
                    tableStyle.put("max-width", widthVal);
                }
                tableStyle.put("width", "100%");
            }

            String tableStyleStr = styleToString(tableStyle);
            String tdStyleStr = styleToString(tdStyle);
            return "<table align=\"center\" width=\"100%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\" role=\"presentation\" style=\"" + tableStyleStr + "\">\n" +
                "<tbody>\n<tr style=\"width:100%\">\n<td style=\"" + tdStyleStr + "\">" + inner + "</td>\n</tr>\n</tbody>\n</table>";
        });

        // Process heading tags
        result = processTag(result, "heading", (attrs, inner) -> {
            String level = attrs.getOrDefault("level", "1");
            Map<String, String> style = extractAllStyleAttributes(attrs);
            style.putIfAbsent("margin", "0");
            String styleStr = styleToString(style);
            return "<h" + level + " style=\"" + styleStr + "\">" + inner + "</h" + level + ">";
        });

        // Process paragraph tags
        result = processTag(result, "paragraph", (attrs, inner) -> {
            Map<String, String> style = extractAllStyleAttributes(attrs);
            style.putIfAbsent("margin", "0");
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
        result = processTag(result, "button", Renderer::processButton);

        // Process image tags
        result = processTag(result, "image", (attrs, inner) -> {
            String src = attrs.getOrDefault("src", "");
            String alt = attrs.getOrDefault("alt", "");
            String width = attrs.get("width");
            String height = attrs.get("height");

            Map<String, String> style = extractAllStyleAttributes(attrs);
            style.putIfAbsent("vertical-align", "middle");
            style.putIfAbsent("max-width", "100%");
            style.putIfAbsent("outline", "none");
            style.putIfAbsent("border", "none");
            style.putIfAbsent("text-decoration", "none");

            String styleStr = styleToString(style);
            String widthAttr = width != null ? " width=\"" + width.replace("px", "") + "\"" : "";
            String heightAttr = height != null ? " height=\"" + height.replace("px", "") + "\"" : "";

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

        // Clean up stray </divider> closing tags
        result = result.replaceAll("(?i)</divider>", "");

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
            style.putIfAbsent("margin", "0");
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
        result = processTag(result, "codeblock", (attrs, inner) -> processCodeBlock(attrs, inner));

        // Self-closing blocks - <block type="..." config="..." />
        {
            java.util.regex.Pattern blockPattern = java.util.regex.Pattern.compile("<block([^>]*?)\\s*/>", java.util.regex.Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher blockMatcher = blockPattern.matcher(result);
            StringBuffer blockSb = new StringBuffer();
            while (blockMatcher.find()) {
                String attrsStr = blockMatcher.group(1) != null ? blockMatcher.group(1) : "";
                Map<String, String> blockAttrs = parseAttributes(attrsStr);
                blockMatcher.appendReplacement(blockSb, java.util.regex.Matcher.quoteReplacement(processBlockTag(blockAttrs, "")));
            }
            blockMatcher.appendTail(blockSb);
            result = blockSb.toString();
        }

        // Clean up stray Sevk closing tags
        String[] strayClosingTags = {
            "</container>", "</section>", "</row>", "</column>",
            "</heading>", "</paragraph>", "</text>", "</button>", "</sevk-link>"
        };
        for (String tag : strayClosingTags) {
            result = result.replace(tag, "");
        }

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

    /**
     * Process codeblock tag with syntax highlighting using highlight.java.
     */
    private static String processCodeBlock(Map<String, String> attrs, String inner) {
        String language = attrs.getOrDefault("language", "javascript");
        String themeName = attrs.getOrDefault("theme", "oneDark");
        Map<String, String> customStyle = extractAllStyleAttributes(attrs);

        String code = inner;

        // Get theme
        Map<String, Map<String, String>> theme = CodeBlockThemes.getTheme(themeName);
        Map<String, String> baseThemeStyle = theme.get("base");

        // Build base styles from theme
        Map<String, String> baseStyle = new LinkedHashMap<>(baseThemeStyle);
        baseStyle.put("width", "100%");
        baseStyle.put("box-sizing", "border-box");

        // Apply custom styles (override theme defaults)
        baseStyle.putAll(customStyle);

        String styleStr = styleToString(baseStyle);

        // Try to highlight with highlight.java
        String highlightedHtml = null;
        try {
            com.codewaves.codehighlight.core.Highlighter highlighter =
                new com.codewaves.codehighlight.core.Highlighter(
                    languageName -> new com.codewaves.codehighlight.renderer.HtmlRenderer("hljs-"));

            com.codewaves.codehighlight.core.Highlighter.HighlightResult result;
            try {
                // Use highlight() with specific language for accurate results
                result = highlighter.highlight(language, code);
            } catch (Exception langErr) {
                // Language not registered, fall back to auto-detection with common subset
                String[] subset = {"javascript", "typescript", "python", "java", "c", "cpp",
                    "csharp", "go", "rust", "ruby", "php", "swift", "kotlin", "scala",
                    "html", "css", "json", "xml", "yaml", "sql", "bash", "shell",
                    "markdown", "plaintext"};
                result = highlighter.highlightAuto(code, subset);
            }
            String rendered = result.getResult().toString();

            // Verify highlighting actually produced colored spans;
            // if not, fall through to plain text fallback
            if (rendered.contains("<span")) {
                highlightedHtml = rendered;
            }
        } catch (Exception e) {
            // Highlighting failed entirely - fall back to plain text
            highlightedHtml = null;
        }

        StringBuilder linesHTML = new StringBuilder();

        if (highlightedHtml != null) {
            // Post-process: convert CSS classes to inline styles based on theme
            String inlined = convertClassesToInlineStyles(highlightedHtml, theme);

            // Split into lines and wrap each
            String[] lines = inlined.split("\r\n|\r|\n", -1);
            for (String line : lines) {
                linesHTML.append("<p style=\"margin:0;min-height:1em\">").append(line).append("</p>");
            }
        } else {
            // Plain text fallback
            String[] lines = code.split("\r\n|\r|\n", -1);
            for (String line : lines) {
                String escaped = escapeHtml(line);
                linesHTML.append("<p style=\"margin:0;min-height:1em\">").append(escaped).append("</p>");
            }
        }

        return "<pre style=\"" + styleStr + "\"><code>" + linesHTML + "</code></pre>";
    }

    /**
     * Convert highlight.java CSS class spans to inline style spans.
     * Transforms &lt;span class="hljs-keyword"&gt; into &lt;span style="color:#c678dd"&gt; etc.
     */
    private static String convertClassesToInlineStyles(String html, Map<String, Map<String, String>> theme) {
        Pattern spanPattern = Pattern.compile("<span class=\"hljs-([^\"]+)\">");
        Matcher matcher = spanPattern.matcher(html);

        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            result.append(html, lastEnd, matcher.start());
            String className = matcher.group(1);
            Map<String, String> tokenStyle = theme.get(className);
            if (tokenStyle == null) {
                // Try mapping common hljs classes to theme keys
                tokenStyle = mapHljsClassToTheme(className, theme);
            }
            if (tokenStyle != null && !tokenStyle.isEmpty()) {
                result.append("<span style=\"").append(styleToString(tokenStyle)).append("\">");
            } else {
                // No style mapping found - use default color
                Map<String, String> defaultStyle = theme.get("default");
                if (defaultStyle != null && !defaultStyle.isEmpty()) {
                    result.append("<span style=\"").append(styleToString(defaultStyle)).append("\">");
                } else {
                    result.append("<span>");
                }
            }
            lastEnd = matcher.end();
        }
        result.append(html, lastEnd, html.length());

        return result.toString();
    }

    /**
     * Map highlight.java CSS class names to theme token keys.
     */
    private static Map<String, String> mapHljsClassToTheme(String hljsClass, Map<String, Map<String, String>> theme) {
        // Direct mapping
        Map<String, String> direct = theme.get(hljsClass);
        if (direct != null) return direct;

        // Map hljs class names to theme keys
        Map<String, String> classMapping = new HashMap<>();
        classMapping.put("keyword", "keyword");
        classMapping.put("built_in", "builtin");
        classMapping.put("type", "class-name");
        classMapping.put("literal", "boolean");
        classMapping.put("number", "number");
        classMapping.put("string", "string");
        classMapping.put("subst", "variable");
        classMapping.put("symbol", "symbol");
        classMapping.put("class", "class-name");
        classMapping.put("function", "function");
        classMapping.put("title", "function");
        classMapping.put("title.function", "function");
        classMapping.put("title.class", "class-name");
        classMapping.put("title.class.inherited", "class-name");
        classMapping.put("params", "variable");
        classMapping.put("comment", "comment");
        classMapping.put("doctag", "comment");
        classMapping.put("meta", "keyword");
        classMapping.put("meta keyword", "keyword");
        classMapping.put("meta string", "string");
        classMapping.put("section", "tag");
        classMapping.put("tag", "tag");
        classMapping.put("name", "tag");
        classMapping.put("attr", "attr-name");
        classMapping.put("attribute", "attr-name");
        classMapping.put("variable", "variable");
        classMapping.put("template-variable", "variable");
        classMapping.put("regexp", "regex");
        classMapping.put("link", "url");
        classMapping.put("selector-tag", "selector");
        classMapping.put("selector-id", "selector");
        classMapping.put("selector-class", "selector");
        classMapping.put("selector-attr", "selector");
        classMapping.put("selector-pseudo", "selector");
        classMapping.put("property", "property");
        classMapping.put("punctuation", "punctuation");
        classMapping.put("operator", "operator");
        classMapping.put("addition", "inserted");
        classMapping.put("deletion", "deleted");
        classMapping.put("char.escape", "string");
        classMapping.put("formula", "number");
        classMapping.put("quote", "comment");
        classMapping.put("emphasis", "italic");
        classMapping.put("strong", "bold");

        String mappedKey = classMapping.get(hljsClass);
        if (mappedKey != null) {
            return theme.get(mappedKey);
        }

        return null;
    }

    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /**
     * Code block themes matching the Node SDK themes.
     */
    private static class CodeBlockThemes {
        private static final Map<String, Map<String, Map<String, String>>> THEMES = new HashMap<>();

        static {
            // oneDark theme
            Map<String, Map<String, String>> oneDark = new LinkedHashMap<>();
            Map<String, String> oneDarkBase = new LinkedHashMap<>();
            oneDarkBase.put("background-color", "#282c34");
            oneDarkBase.put("color", "#abb2bf");
            oneDarkBase.put("text-shadow", "0 1px rgba(0, 0, 0, 0.3)");
            oneDarkBase.put("font-family", "'Fira Code', 'Fira Mono', Menlo, Consolas, 'DejaVu Sans Mono', monospace");
            oneDarkBase.put("direction", "ltr");
            oneDarkBase.put("text-align", "left");
            oneDarkBase.put("white-space", "pre");
            oneDarkBase.put("word-spacing", "normal");
            oneDarkBase.put("word-break", "normal");
            oneDarkBase.put("line-height", "1.5");
            oneDarkBase.put("-moz-tab-size", "2");
            oneDarkBase.put("-o-tab-size", "2");
            oneDarkBase.put("tab-size", "2");
            oneDarkBase.put("-webkit-hyphens", "none");
            oneDarkBase.put("-moz-hyphens", "none");
            oneDarkBase.put("hyphens", "none");
            oneDarkBase.put("padding", "1em");
            oneDarkBase.put("margin", "0.5em 0");
            oneDarkBase.put("overflow", "auto");
            oneDarkBase.put("border-radius", "0.3em");
            oneDark.put("base", oneDarkBase);
            oneDark.put("comment", styleMap("color", "#5c6370", "font-style", "italic"));
            oneDark.put("prolog", styleMap("color", "#5c6370"));
            oneDark.put("cdata", styleMap("color", "#5c6370"));
            oneDark.put("doctype", styleMap("color", "#abb2bf"));
            oneDark.put("punctuation", styleMap("color", "#abb2bf"));
            oneDark.put("entity", styleMap("color", "#abb2bf"));
            oneDark.put("attr-name", styleMap("color", "#d19a66"));
            oneDark.put("class-name", styleMap("color", "#d19a66"));
            oneDark.put("boolean", styleMap("color", "#d19a66"));
            oneDark.put("constant", styleMap("color", "#d19a66"));
            oneDark.put("number", styleMap("color", "#d19a66"));
            oneDark.put("atrule", styleMap("color", "#d19a66"));
            oneDark.put("keyword", styleMap("color", "#c678dd"));
            oneDark.put("property", styleMap("color", "#e06c75"));
            oneDark.put("tag", styleMap("color", "#e06c75"));
            oneDark.put("symbol", styleMap("color", "#e06c75"));
            oneDark.put("deleted", styleMap("color", "#e06c75"));
            oneDark.put("important", styleMap("color", "#e06c75"));
            oneDark.put("selector", styleMap("color", "#98c379"));
            oneDark.put("string", styleMap("color", "#98c379"));
            oneDark.put("char", styleMap("color", "#98c379"));
            oneDark.put("builtin", styleMap("color", "#98c379"));
            oneDark.put("inserted", styleMap("color", "#98c379"));
            oneDark.put("regex", styleMap("color", "#98c379"));
            oneDark.put("attr-value", styleMap("color", "#98c379"));
            oneDark.put("variable", styleMap("color", "#61afef"));
            oneDark.put("operator", styleMap("color", "#61afef"));
            oneDark.put("function", styleMap("color", "#61afef"));
            oneDark.put("url", styleMap("color", "#56b6c2"));
            oneDark.put("bold", styleMap("font-weight", "bold"));
            oneDark.put("italic", styleMap("font-style", "italic"));
            oneDark.put("default", styleMap("color", "#abb2bf"));
            THEMES.put("oneDark", oneDark);

            // oneLight theme
            Map<String, Map<String, String>> oneLight = new LinkedHashMap<>();
            Map<String, String> oneLightBase = new LinkedHashMap<>();
            oneLightBase.put("background-color", "#fafafa");
            oneLightBase.put("color", "#383a42");
            oneLightBase.put("font-family", "'Fira Code', 'Fira Mono', Menlo, Consolas, 'DejaVu Sans Mono', monospace");
            oneLightBase.put("direction", "ltr");
            oneLightBase.put("text-align", "left");
            oneLightBase.put("white-space", "pre");
            oneLightBase.put("word-spacing", "normal");
            oneLightBase.put("word-break", "normal");
            oneLightBase.put("line-height", "1.5");
            oneLightBase.put("-moz-tab-size", "2");
            oneLightBase.put("-o-tab-size", "2");
            oneLightBase.put("tab-size", "2");
            oneLightBase.put("-webkit-hyphens", "none");
            oneLightBase.put("-moz-hyphens", "none");
            oneLightBase.put("hyphens", "none");
            oneLightBase.put("padding", "1em");
            oneLightBase.put("margin", "0.5em 0");
            oneLightBase.put("overflow", "auto");
            oneLightBase.put("border-radius", "0.3em");
            oneLight.put("base", oneLightBase);
            oneLight.put("comment", styleMap("color", "#a0a1a7", "font-style", "italic"));
            oneLight.put("prolog", styleMap("color", "#a0a1a7"));
            oneLight.put("cdata", styleMap("color", "#a0a1a7"));
            oneLight.put("doctype", styleMap("color", "#383a42"));
            oneLight.put("punctuation", styleMap("color", "#383a42"));
            oneLight.put("entity", styleMap("color", "#383a42"));
            oneLight.put("attr-name", styleMap("color", "#b76b01"));
            oneLight.put("class-name", styleMap("color", "#b76b01"));
            oneLight.put("boolean", styleMap("color", "#b76b01"));
            oneLight.put("constant", styleMap("color", "#b76b01"));
            oneLight.put("number", styleMap("color", "#b76b01"));
            oneLight.put("atrule", styleMap("color", "#b76b01"));
            oneLight.put("keyword", styleMap("color", "#a626a4"));
            oneLight.put("property", styleMap("color", "#e45649"));
            oneLight.put("tag", styleMap("color", "#e45649"));
            oneLight.put("symbol", styleMap("color", "#e45649"));
            oneLight.put("deleted", styleMap("color", "#e45649"));
            oneLight.put("important", styleMap("color", "#e45649"));
            oneLight.put("selector", styleMap("color", "#50a14f"));
            oneLight.put("string", styleMap("color", "#50a14f"));
            oneLight.put("char", styleMap("color", "#50a14f"));
            oneLight.put("builtin", styleMap("color", "#50a14f"));
            oneLight.put("inserted", styleMap("color", "#50a14f"));
            oneLight.put("regex", styleMap("color", "#50a14f"));
            oneLight.put("attr-value", styleMap("color", "#50a14f"));
            oneLight.put("variable", styleMap("color", "#4078f2"));
            oneLight.put("operator", styleMap("color", "#4078f2"));
            oneLight.put("function", styleMap("color", "#4078f2"));
            oneLight.put("url", styleMap("color", "#0184bc"));
            oneLight.put("bold", styleMap("font-weight", "bold"));
            oneLight.put("italic", styleMap("font-style", "italic"));
            oneLight.put("default", styleMap("color", "#383a42"));
            THEMES.put("oneLight", oneLight);

            // vscDarkPlus theme
            Map<String, Map<String, String>> vscDarkPlus = new LinkedHashMap<>();
            Map<String, String> vscDarkPlusBase = new LinkedHashMap<>();
            vscDarkPlusBase.put("background-color", "#1e1e1e");
            vscDarkPlusBase.put("color", "#d4d4d4");
            vscDarkPlusBase.put("font-size", "13px");
            vscDarkPlusBase.put("text-shadow", "none");
            vscDarkPlusBase.put("font-family", "Menlo, Monaco, Consolas, 'Andale Mono', 'Ubuntu Mono', 'Courier New', monospace");
            vscDarkPlusBase.put("direction", "ltr");
            vscDarkPlusBase.put("text-align", "left");
            vscDarkPlusBase.put("white-space", "pre");
            vscDarkPlusBase.put("word-spacing", "normal");
            vscDarkPlusBase.put("word-break", "normal");
            vscDarkPlusBase.put("line-height", "1.5");
            vscDarkPlusBase.put("-moz-tab-size", "4");
            vscDarkPlusBase.put("-o-tab-size", "4");
            vscDarkPlusBase.put("tab-size", "4");
            vscDarkPlusBase.put("-webkit-hyphens", "none");
            vscDarkPlusBase.put("-moz-hyphens", "none");
            vscDarkPlusBase.put("hyphens", "none");
            vscDarkPlusBase.put("padding", "1em");
            vscDarkPlusBase.put("margin", ".5em 0");
            vscDarkPlusBase.put("overflow", "auto");
            vscDarkPlus.put("base", vscDarkPlusBase);
            vscDarkPlus.put("comment", styleMap("color", "#6a9955"));
            vscDarkPlus.put("prolog", styleMap("color", "#6a9955"));
            vscDarkPlus.put("punctuation", styleMap("color", "#d4d4d4"));
            vscDarkPlus.put("property", styleMap("color", "#9cdcfe"));
            vscDarkPlus.put("tag", styleMap("color", "#569cd6"));
            vscDarkPlus.put("boolean", styleMap("color", "#569cd6"));
            vscDarkPlus.put("number", styleMap("color", "#b5cea8"));
            vscDarkPlus.put("constant", styleMap("color", "#9cdcfe"));
            vscDarkPlus.put("symbol", styleMap("color", "#b5cea8"));
            vscDarkPlus.put("inserted", styleMap("color", "#b5cea8"));
            vscDarkPlus.put("selector", styleMap("color", "#d7ba7d"));
            vscDarkPlus.put("attr-name", styleMap("color", "#9cdcfe"));
            vscDarkPlus.put("string", styleMap("color", "#ce9178"));
            vscDarkPlus.put("char", styleMap("color", "#ce9178"));
            vscDarkPlus.put("builtin", styleMap("color", "#ce9178"));
            vscDarkPlus.put("deleted", styleMap("color", "#ce9178"));
            vscDarkPlus.put("operator", styleMap("color", "#d4d4d4"));
            vscDarkPlus.put("entity", styleMap("color", "#569cd6"));
            vscDarkPlus.put("atrule", styleMap("color", "#ce9178"));
            vscDarkPlus.put("keyword", styleMap("color", "#569CD6"));
            vscDarkPlus.put("function", styleMap("color", "#dcdcaa"));
            vscDarkPlus.put("regex", styleMap("color", "#d16969"));
            vscDarkPlus.put("important", styleMap("color", "#569cd6"));
            vscDarkPlus.put("italic", styleMap("font-style", "italic"));
            vscDarkPlus.put("class-name", styleMap("color", "#4ec9b0"));
            vscDarkPlus.put("variable", styleMap("color", "#9cdcfe"));
            vscDarkPlus.put("attr-value", styleMap("color", "#ce9178"));
            vscDarkPlus.put("cdata", styleMap("color", "#808080"));
            vscDarkPlus.put("default", styleMap("color", "#d4d4d4"));
            THEMES.put("vscDarkPlus", vscDarkPlus);

            // vs (Visual Studio light) theme
            Map<String, Map<String, String>> vs = new LinkedHashMap<>();
            Map<String, String> vsBase = new LinkedHashMap<>();
            vsBase.put("background-color", "white");
            vsBase.put("color", "#393A34");
            vsBase.put("font-family", "'Consolas', 'Bitstream Vera Sans Mono', 'Courier New', Courier, monospace");
            vsBase.put("direction", "ltr");
            vsBase.put("text-align", "left");
            vsBase.put("white-space", "pre");
            vsBase.put("word-spacing", "normal");
            vsBase.put("word-break", "normal");
            vsBase.put("font-size", ".9em");
            vsBase.put("line-height", "1.2em");
            vsBase.put("-moz-tab-size", "4");
            vsBase.put("-o-tab-size", "4");
            vsBase.put("tab-size", "4");
            vsBase.put("-webkit-hyphens", "none");
            vsBase.put("-moz-hyphens", "none");
            vsBase.put("hyphens", "none");
            vsBase.put("padding", "1em");
            vsBase.put("margin", ".5em 0");
            vsBase.put("overflow", "auto");
            vsBase.put("border", "1px solid #dddddd");
            vs.put("base", vsBase);
            vs.put("comment", styleMap("color", "#008000", "font-style", "italic"));
            vs.put("prolog", styleMap("color", "#008000", "font-style", "italic"));
            vs.put("doctype", styleMap("color", "#008000", "font-style", "italic"));
            vs.put("cdata", styleMap("color", "#008000", "font-style", "italic"));
            vs.put("string", styleMap("color", "#A31515"));
            vs.put("punctuation", styleMap("color", "#393A34"));
            vs.put("operator", styleMap("color", "#393A34"));
            vs.put("url", styleMap("color", "#36acaa"));
            vs.put("symbol", styleMap("color", "#36acaa"));
            vs.put("number", styleMap("color", "#36acaa"));
            vs.put("boolean", styleMap("color", "#36acaa"));
            vs.put("variable", styleMap("color", "#36acaa"));
            vs.put("constant", styleMap("color", "#36acaa"));
            vs.put("inserted", styleMap("color", "#36acaa"));
            vs.put("atrule", styleMap("color", "#0000ff"));
            vs.put("keyword", styleMap("color", "#0000ff"));
            vs.put("attr-value", styleMap("color", "#0000ff"));
            vs.put("function", styleMap("color", "#393A34"));
            vs.put("deleted", styleMap("color", "#9a050f"));
            vs.put("selector", styleMap("color", "#800000"));
            vs.put("important", styleMap("color", "#e90", "font-weight", "bold"));
            vs.put("bold", styleMap("font-weight", "bold"));
            vs.put("italic", styleMap("font-style", "italic"));
            vs.put("class-name", styleMap("color", "#2B91AF"));
            vs.put("tag", styleMap("color", "#800000"));
            vs.put("attr-name", styleMap("color", "#ff0000"));
            vs.put("property", styleMap("color", "#ff0000"));
            vs.put("regex", styleMap("color", "#ff0000"));
            vs.put("entity", styleMap("color", "#ff0000"));
            vs.put("default", styleMap("color", "#393A34"));
            THEMES.put("vs", vs);
        }

        static Map<String, Map<String, String>> getTheme(String name) {
            Map<String, Map<String, String>> theme = THEMES.get(name);
            return theme != null ? theme : THEMES.get("oneDark");
        }

        private static Map<String, String> styleMap(String... pairs) {
            Map<String, String> map = new LinkedHashMap<>();
            for (int i = 0; i < pairs.length; i += 2) {
                map.put(pairs[i], pairs[i + 1]);
            }
            return map;
        }
    }

    @FunctionalInterface
    private interface TagProcessor {
        String process(Map<String, String> attrs, String inner);
    }

    private static String processTag(String content, String tagName, TagProcessor processor) {
        String result = content;
        String openPattern = "<" + tagName + "([^>]*)>";
        Pattern openRe = Pattern.compile(openPattern, Pattern.CASE_INSENSITIVE);
        Pattern closeRe = Pattern.compile("(?i)</" + tagName + ">");
        Pattern nestedOpenRe = Pattern.compile("(?i)<" + tagName);

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
                Matcher closeMatcher = closeRe.matcher(result);
                if (!closeMatcher.find(innerStart)) continue;
                int closePos = closeMatcher.start();
                int closeEnd = closeMatcher.end();

                String inner = result.substring(innerStart, closePos);

                // Check if there's another opening tag inside
                if (nestedOpenRe.matcher(inner).find()) {
                    // This tag has nested same tags, skip it
                    continue;
                }

                // This is an innermost tag, process it
                Map<String, String> attrs = parseAttributes(attrsStr);
                String replacement = processor.process(attrs, inner);

                result = result.substring(0, start) + replacement + result.substring(closeEnd);
                processed = true;
                break;
            }

            if (!processed) break;
        }

        return result;
    }

    private static Map<String, String> parseAttributes(String attrsStr) {
        Map<String, String> attrs = new HashMap<>();
        Pattern re = Pattern.compile("([\\w-]+)=(?:\"([^\"]*)\"|'([^']*)')");
        Matcher matcher = re.matcher(attrsStr);
        while (matcher.find()) {
            attrs.put(matcher.group(1), matcher.group(2) != null ? matcher.group(2) : matcher.group(3));
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
        if (attrs.get("max-height") != null) style.put("max-height", attrs.get("max-height"));
        if (attrs.get("min-width") != null) style.put("min-width", attrs.get("min-width"));
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

        // Background image
        String backgroundImage = attrs.get("background-image");
        if (backgroundImage != null) {
            style.put("background-image", "url('" + backgroundImage + "')");
            String bgSize = attrs.get("background-size");
            if (bgSize != null) {
                style.put("background-size", bgSize);
            } else {
                style.put("background-size", "cover");
            }
            String bgPosition = attrs.get("background-position");
            if (bgPosition != null) {
                style.put("background-position", bgPosition);
            } else {
                style.put("background-position", "center");
            }
            String bgRepeat = attrs.get("background-repeat");
            if (bgRepeat != null) {
                style.put("background-repeat", bgRepeat);
            } else {
                style.put("background-repeat", "no-repeat");
            }
        } else {
            if (attrs.get("background-size") != null) style.put("background-size", attrs.get("background-size"));
            if (attrs.get("background-position") != null) style.put("background-position", attrs.get("background-position"));
            if (attrs.get("background-repeat") != null) style.put("background-repeat", attrs.get("background-repeat"));
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

    // ── Block / template engine ──────────────────────────────────────

    private static boolean isTruthy(Object val) {
        if (val == null) return false;
        if (val instanceof Boolean) return (Boolean) val;
        if (val instanceof Number) return ((Number) val).doubleValue() != 0;
        if (val instanceof String) return !((String) val).isEmpty();
        if (val instanceof Collection) return !((Collection<?>) val).isEmpty();
        if (val instanceof Map) return !((Map<?, ?>) val).isEmpty();
        if (val.getClass().isArray()) return java.lang.reflect.Array.getLength(val) > 0;
        return true;
    }

    private static boolean evaluateCondition(String expr, Map<String, Object> config) {
        String trimmed = expr.trim();

        // OR: split on ||, return true if any part is true
        if (trimmed.contains("||")) {
            for (String part : trimmed.split("\\|\\|")) {
                if (evaluateCondition(part, config)) return true;
            }
            return false;
        }

        // AND: split on &&, return true if all parts are true
        if (trimmed.contains("&&")) {
            for (String part : trimmed.split("&&")) {
                if (!evaluateCondition(part, config)) return false;
            }
            return true;
        }

        // Equality: key == "value"
        java.util.regex.Matcher eqMatcher = Pattern.compile("^(\\w+)\\s*==\\s*\"([^\"]*)\"$").matcher(trimmed);
        if (eqMatcher.matches()) {
            Object val = config.get(eqMatcher.group(1));
            return (val != null ? val.toString() : "").equals(eqMatcher.group(2));
        }

        // Inequality: key != "value"
        java.util.regex.Matcher neqMatcher = Pattern.compile("^(\\w+)\\s*!=\\s*\"([^\"]*)\"$").matcher(trimmed);
        if (neqMatcher.matches()) {
            Object val = config.get(neqMatcher.group(1));
            return !(val != null ? val.toString() : "").equals(neqMatcher.group(2));
        }

        // Simple truthy check
        return isTruthy(config.get(trimmed));
    }

    @SuppressWarnings("unchecked")
    private static String renderTemplate(String template, Map<String, Object> config) {
        String result = template;

        // Loop until stable (handles nested ifs)
        int maxPasses = 20;
        for (int pass = 0; pass < maxPasses; pass++) {
            String prev = result;

            // #each – iterate arrays
            Pattern eachRe = Pattern.compile("\\{%#each\\s+(\\w+)(?:\\s+as\\s+(\\w+))?%\\}([\\s\\S]*?)\\{%/each%\\}");
            Matcher eachMatcher = eachRe.matcher(result);
            StringBuffer eachSb = new StringBuffer();
            while (eachMatcher.find()) {
                String key = eachMatcher.group(1);
                String alias = eachMatcher.group(2);
                String body = eachMatcher.group(3);
                Object listObj = config.get(key);
                StringBuilder out = new StringBuilder();
                if (listObj instanceof List) {
                    List<?> list = (List<?>) listObj;
                    for (Object item : list) {
                        String rendered = body;
                        if (item instanceof Map) {
                            Map<String, Object> itemMap = (Map<String, Object>) item;
                            for (Map.Entry<String, Object> e : itemMap.entrySet()) {
                                String varName = (alias != null ? alias + "." : "") + e.getKey();
                                String replacement = e.getValue() != null ? e.getValue().toString() : "";
                                rendered = rendered.replace("{%" + varName + "%}", replacement);
                            }
                        } else if (item != null) {
                            String varName = alias != null ? alias : key;
                            rendered = rendered.replace("{%" + varName + "%}", item.toString());
                        }
                        out.append(rendered);
                    }
                }
                eachMatcher.appendReplacement(eachSb, Matcher.quoteReplacement(out.toString()));
            }
            eachMatcher.appendTail(eachSb);
            result = eachSb.toString();

            // #if / else / /if – process innermost first, loop until stable
            Pattern ifRe = Pattern.compile("\\{%#if\\s+([^%]+)%\\}((?:(?!\\{%#if\\s)[\\s\\S])*?)\\{%/if%\\}");
            String ifPrev = "";
            while (!ifPrev.equals(result)) {
                ifPrev = result;
                Matcher ifMatcher = ifRe.matcher(result);
                StringBuffer ifSb = new StringBuffer();
                while (ifMatcher.find()) {
                    String key = ifMatcher.group(1);  // now it's actually a condition expression
                    String body = ifMatcher.group(2);
                    boolean truthy = evaluateCondition(key, config);
                    String replacement;
                    if (body.contains("{%else%}")) {
                        String[] parts = body.split("\\{%else%\\}", 2);
                        replacement = truthy ? parts[0] : parts[1];
                    } else {
                        replacement = truthy ? body : "";
                    }
                    ifMatcher.appendReplacement(ifSb, Matcher.quoteReplacement(replacement));
                }
                ifMatcher.appendTail(ifSb);
                result = ifSb.toString();
            }

            // variable with fallback {%variable ?? fallback%}
            Pattern fallbackRe = Pattern.compile("\\{%(\\w+)\\s*\\?\\?\\s*([^%]+)%\\}");
            Matcher fbMatcher = fallbackRe.matcher(result);
            StringBuffer fbSb = new StringBuffer();
            while (fbMatcher.find()) {
                String key = fbMatcher.group(1);
                String fallback = fbMatcher.group(2).trim();
                Object val = config.get(key);
                String replacement = (val != null && !val.toString().isEmpty()) ? val.toString() : fallback;
                fbMatcher.appendReplacement(fbSb, Matcher.quoteReplacement(replacement));
            }
            fbMatcher.appendTail(fbSb);
            result = fbSb.toString();

            // simple variable {%variable%}
            Pattern simpleRe = Pattern.compile("\\{%(\\w+)%\\}");
            Matcher simpleMatcher = simpleRe.matcher(result);
            StringBuffer simpleSb = new StringBuffer();
            while (simpleMatcher.find()) {
                String key = simpleMatcher.group(1);
                Object val = config.get(key);
                String replacement = val != null ? val.toString() : "";
                simpleMatcher.appendReplacement(simpleSb, Matcher.quoteReplacement(replacement));
            }
            simpleMatcher.appendTail(simpleSb);
            result = simpleSb.toString();

            if (result.equals(prev)) break;
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private static String processBlockTag(Map<String, String> attrs, String inner) {
        String template = (inner != null && !inner.trim().isEmpty()) ? inner.trim() : attrs.getOrDefault("template", "");
        if (template.isEmpty()) return "";
        String configStr = attrs.getOrDefault("config", "{}").replace("'", "\"").replace("&quot;", "\"").replace("&amp;", "&");
        Map<String, Object> config;
        try { config = new Gson().fromJson(configStr, Map.class); }
        catch (Exception e) { config = new HashMap<>(); }
        return renderTemplate(template, config);
    }

    /**
     * Head settings for email generation
     */
    public static class EmailHeadSettings {
        public String title = "";
        public String previewText = "";
        public String styles = "";
        public List<FontConfig> fonts = new ArrayList<>();
        public String lang = "";
        public String dir = "";
    }

    /**
     * Parsed email content
     */
    public static class ParsedEmailContent {
        public String body = "";
        public EmailHeadSettings headSettings = new EmailHeadSettings();
    }
}
