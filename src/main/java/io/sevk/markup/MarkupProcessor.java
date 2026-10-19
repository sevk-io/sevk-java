package io.sevk.markup;

import static io.sevk.markup.MarkupSupport.assertMarkupDepth;
import static io.sevk.markup.MarkupSupport.extractAllStyleAttributes;
import static io.sevk.markup.MarkupSupport.parseAttributes;
import static io.sevk.markup.MarkupSupport.processTag;
import static io.sevk.markup.MarkupSupport.styleToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class MarkupProcessor {
    static String processMarkup(String content, List<String> gapStylesCollector) {
        assertMarkupDepth(content);
        RenderBudget budget = new RenderBudget();
        String result = processTag(content, "block", (attrs, inner) -> TemplateEngine.processBlockTag(attrs, inner), budget);

        Matcher blockMatcher = Pattern.compile("<block([^>]*?)/>", Pattern.CASE_INSENSITIVE).matcher(result);
        StringBuilder blockSb = new StringBuilder();
        while (blockMatcher.find()) {
            blockMatcher.appendReplacement(
                blockSb,
                Matcher.quoteReplacement(TemplateEngine.processBlockTag(parseAttributes(blockMatcher.group(1)), ""))
            );
        }
        blockMatcher.appendTail(blockSb);
        result = blockSb.toString();
        assertMarkupDepth(result);
        result = result.replaceAll("(?i)<link(\\s+(?:[\\w-]+=(?:\"[^\"]*\"|'[^']*')\\s+)*)href=", "<sevk-link$1href=");
        result = result.replaceAll("(?i)</link>", "</sevk-link>");

        result = processTag(result, "section", (attrs, inner) -> {
            Map<String, String> style = extractAllStyleAttributes(attrs);
            Map<String, String> tdStyle = new LinkedHashMap<>();

            if (style.containsKey("padding")) { tdStyle.put("padding", style.remove("padding")); }
            if (style.containsKey("text-align")) { tdStyle.put("text-align", style.remove("text-align")); }

            // Tables default to border-collapse:collapse via the global reset,
            // which disables border-radius. Flip to separate when any corner
            // radius is set so the rounded border actually renders.
            String[] radiusKeys = {"border-radius", "border-top-left-radius", "border-top-right-radius", "border-bottom-left-radius", "border-bottom-right-radius"};
            for (String rk : radiusKeys) {
                if (style.containsKey(rk)) { style.put("border-collapse", "separate"); break; }
            }

            String styleStr = styleToString(style);
            String tdStyleStr = styleToString(tdStyle);
            return "<table align=\"center\" width=\"100%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\" role=\"presentation\" style=\"" + styleStr + "\">\n" +
                "<tbody>\n<tr>\n<td style=\"" + tdStyleStr + "\">" + inner + "</td>\n</tr>\n</tbody>\n</table>";
        }, budget);

        result = processTag(result, "column", (attrs, inner) -> {
            Map<String, String> style = extractAllStyleAttributes(attrs);
            if (!style.containsKey("vertical-align")) {
                style.put("vertical-align", "top");
            }
            String styleStr = styleToString(style);
            return "<td class=\"sevk-column\" style=\"" + styleStr + "\">" + inner + "</td>";
        }, budget);

        final int[] rowCounter = {0};
        result = processTag(result, "row", (attrs, inner) -> {
            String gap = attrs.getOrDefault("gap", "0");
            Map<String, String> style = extractAllStyleAttributes(attrs);
            style.remove("gap");

            String gapPx = gap.replaceFirst("px", "");
            int gapNum = parsePx(gapPx);

            String rowId = "sevk-row-" + rowCounter[0]++;

            String processedInner = inner;
            Pattern colCountPattern = Pattern.compile("class=\"sevk-column\"");
            Matcher colCountMatcher = colCountPattern.matcher(processedInner);
            int columnCount = 0;

            while (colCountMatcher.find()) {
                columnCount++;
            }

            if (columnCount > 1) {
                String equalWidth = (100 / columnCount) + "%";
                processedInner = processedInner.replaceAll(
                    "<td class=\"sevk-column\" style=\"([^\"]*)\"",
                    "<td class=\"sevk-column\" style=\"__EQUAL_WIDTH_PLACEHOLDER__$1\""
                );
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

            if (gapNum > 0) {
                String spacerTd = "</td><td class=\"sevk-gap\" style=\"width:" + gapPx + "px;min-width:" + gapPx + "px\" width=\"" + gapPx + "\"></td><td class=\"sevk-column\"";
                processedInner = processedInner.replaceAll("</td>\\s*<td class=\"sevk-column\"", spacerTd);

                gapStylesCollector.add(
                    "." + rowId + " .sevk-gap{display:none !important;}" +
                    "." + rowId + " > tbody > tr > td.sevk-column{display:block !important;width:100% !important;margin-bottom:" + gapPx + "px !important;}" +
                    "." + rowId + " > tbody > tr > td.sevk-column:last-of-type{margin-bottom:0 !important;}"
                );
            }

            String styleStr = styleToString(style);
            return "<table align=\"center\" width=\"100%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\" role=\"presentation\" class=\"sevk-row-table " + rowId + "\" style=\"" + styleStr + "\">\n" +
                "<tbody style=\"width:100%\">\n<tr style=\"width:100%\">" + processedInner + "</tr>\n</tbody>\n</table>";
        }, budget);

        result = processTag(result, "container", (attrs, inner) -> {
            Map<String, String> style = extractAllStyleAttributes(attrs);
            Map<String, String> tdStyle = new LinkedHashMap<>();
            Map<String, String> tableStyle = new LinkedHashMap<>();

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

            boolean hasBorderRadius = tdStyle.containsKey("border-radius")
                || tdStyle.containsKey("border-top-left-radius")
                || tdStyle.containsKey("border-top-right-radius")
                || tdStyle.containsKey("border-bottom-left-radius")
                || tdStyle.containsKey("border-bottom-right-radius");
            if (hasBorderRadius) {
                tableStyle.put("border-collapse", "separate");
            }

            String widthVal = tableStyle.get("width");
            if (widthVal != null && !"100%".equals(widthVal) && !"auto".equals(widthVal)) {
                if (!tableStyle.containsKey("max-width")) {
                    tableStyle.put("max-width", widthVal);
                }
                tableStyle.put("width", "100%");
            }

            String tableStyleStr = styleToString(tableStyle);
            String tdStyleStr = styleToString(tdStyle);
            // No `align="center"` on container: centering is opt-in via the
            // user's markup (e.g. `margin="0 auto"`). Editor preview and the
            // rendered email follow the same "no implicit centering" contract.
            return "<table width=\"100%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\" role=\"presentation\" style=\"" + tableStyleStr + "\">\n" +
                "<tbody>\n<tr style=\"width:100%\">\n<td style=\"" + tdStyleStr + "\">" + inner + "</td>\n</tr>\n</tbody>\n</table>";
        }, budget);

        result = processTag(result, "heading", (attrs, inner) -> {
            String level = attrs.getOrDefault("level", "").isEmpty() ? "1" : attrs.get("level");
            Map<String, String> style = extractAllStyleAttributes(attrs);
            style.putIfAbsent("margin", "0");
            String styleStr = styleToString(style);
            return "<h" + level + " style=\"" + styleStr + "\">" + inner + "</h" + level + ">";
        }, budget);

        result = processTag(result, "paragraph", (attrs, inner) -> {
            Map<String, String> style = extractAllStyleAttributes(attrs);
            style.putIfAbsent("margin", "0");
            String styleStr = styleToString(style);
            return "<p style=\"" + styleStr + "\">" + inner + "</p>";
        }, budget);

        result = processTag(result, "text", (attrs, inner) -> {
            Map<String, String> style = extractAllStyleAttributes(attrs);
            String styleStr = styleToString(style);
            return "<span style=\"" + styleStr + "\">" + inner + "</span>";
        }, budget);

        result = processTag(result, "button", MarkupProcessor::processButton, budget);

        // Match opening tags directly so an optional closing tag cannot consume surrounding whitespace.
        Pattern imagePattern = Pattern.compile("<image([^>]*?)/?>", Pattern.CASE_INSENSITIVE);
        Matcher imageMatcher = imagePattern.matcher(result);
        StringBuilder imageSb = new StringBuilder();
        while (imageMatcher.find()) {
            Map<String, String> attrs = parseAttributes(imageMatcher.group(1));
            String src = attrs.getOrDefault("src", "");
            String alt = attrs.getOrDefault("alt", "");
            String width = attrs.get("width");
            String height = attrs.get("height");

            Map<String, String> style = extractAllStyleAttributes(attrs);
            style.putIfAbsent("vertical-align", "middle");
            style.putIfAbsent("max-width", "100%");

            String styleStr = styleToString(style);
            String widthValue = width != null ? width.replaceFirst("px", "") : "";
            String heightValue = height != null ? height.replaceFirst("px", "") : "";
            String widthAttr = widthValue.isEmpty() ? "" : " width=\"" + widthValue + "\"";
            String heightAttr = heightValue.isEmpty() ? "" : " height=\"" + heightValue + "\"";

            String replacement = "<img src=\"" + src + "\" alt=\"" + alt + "\"" + widthAttr + heightAttr + " style=\"" + styleStr + "\" />";
            imageMatcher.appendReplacement(imageSb, Matcher.quoteReplacement(replacement));
        }
        imageMatcher.appendTail(imageSb);
        result = imageSb.toString();

        // Match the opening tag directly because processTag does not accept self-closing dividers.
        Pattern dividerPattern = Pattern.compile("<divider([^>]*?)/?>", Pattern.CASE_INSENSITIVE);
        Matcher dividerMatcher = dividerPattern.matcher(result);
        StringBuilder dividerSb = new StringBuilder();
        while (dividerMatcher.find()) {
            Map<String, String> attrs = parseAttributes(dividerMatcher.group(1));
            Map<String, String> style = extractAllStyleAttributes(attrs);
            String styleStr = styleToString(style);
            String classStr = classAttribute(attrs);
            String replacement = "<hr style=\"" + styleStr + "\"" + classStr + " />";
            dividerMatcher.appendReplacement(dividerSb, Matcher.quoteReplacement(replacement));
        }
        dividerMatcher.appendTail(dividerSb);
        result = dividerSb.toString();

        result = result.replaceAll("(?i)</divider>", "");
        result = result.replaceAll("(?i)</image>", "");

        result = processTag(result, "sevk-link", (attrs, inner) -> {
            String href = attrs.getOrDefault("href", "");
            String target = attrs.getOrDefault("target", "");
            href = href.isEmpty() ? "#" : href;
            target = target.isEmpty() ? "_blank" : target;
            Map<String, String> style = extractAllStyleAttributes(attrs);
            String styleStr = styleToString(style);
            return "<a href=\"" + href + "\" target=\"" + target + "\" style=\"" + styleStr + "\">" + inner + "</a>";
        }, budget);

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
            String classStr = classAttribute(attrs);
            return "<" + tag + " style=\"" + styleStr + "\"" + classStr + ">" + inner + "</" + tag + ">";
        }, budget);

        result = processTag(result, "li", (attrs, inner) -> {
            Map<String, String> style = extractAllStyleAttributes(attrs);
            String styleStr = styleToString(style);
            String classStr = classAttribute(attrs);
            return "<li style=\"" + styleStr + "\"" + classStr + ">" + inner + "</li>";
        }, budget);

        result = processTag(result, "codeblock", (attrs, inner) -> SyntaxHighlighter.processCodeBlock(attrs, inner, budget), budget);

        result = result.replaceAll("(?i)</(?:container|section|row|column|heading|paragraph|text|button|sevk-link)>", "");

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
     * Outlook button padding hack ported from react-email's Button component
     * (https://github.com/resend/react-email/blob/main/packages/components/src/button.tsx).
     * Uses mso-padding-alt:0, hairspace runs scaled by mso-font-width, and
     * mso-text-raise to get Outlook to honour horizontal/vertical padding.
     */
    private static String processButton(Map<String, String> attrs, String inner) {
        String href = attrs.getOrDefault("href", "");
        href = href.isEmpty() ? "#" : href;
        Map<String, String> style = extractAllStyleAttributes(attrs);

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

        buttonStyle.putAll(style);

        buttonStyle.put("padding-top", paddingTop + "px");
        buttonStyle.put("padding-right", paddingRight + "px");
        buttonStyle.put("padding-bottom", paddingBottom + "px");
        buttonStyle.put("padding-left", paddingLeft + "px");

        String styleStr = styleToString(buttonStyle);

        StringBuilder leftMsoSpaces = new StringBuilder();

        for (int i = 0; i < plSpaceCount; i++) {
            leftMsoSpaces.append("&#8202;");
        }

        StringBuilder rightMsoSpaces = new StringBuilder();

        for (int i = 0; i < prSpaceCount; i++) {
            rightMsoSpaces.append("&#8202;");
        }

        return "<a href=\"" + href + "\" target=\"_blank\" style=\"" + styleStr + "\">" +
            "<!--[if mso]><i style=\"mso-font-width:" + Math.round(plFontWidth * 100) + "%;mso-text-raise:" + textRaise + "\" hidden>" + leftMsoSpaces + "</i><![endif]-->" +
            "<span style=\"max-width:100%;display:inline-block;line-height:120%;mso-padding-alt:0px;mso-text-raise:" + pxToPt(paddingBottom) + "\">" + inner + "</span>" +
            "<!--[if mso]><i style=\"mso-font-width:" + Math.round(prFontWidth * 100) + "%\" hidden>" + rightMsoSpaces + "&#8203;</i><![endif]-->" +
            "</a>";
    }

    private static int[] parsePadding(Map<String, String> style) {
        int pt = 0, pr = 0, pb = 0, pl = 0;
        String padding = style.get("padding");
        if (padding != null) {
            String[] parts = padding.trim().split("\\s+");
            switch (parts.length) {
                case 1: {
                    int val = parsePx(parts[0]);
                    pt = pr = pb = pl = val;
                    break;
                }
                case 2: {
                    int vertical = parsePx(parts[0]);
                    int horizontal = parsePx(parts[1]);
                    pt = vertical;
                    pr = horizontal;
                    pb = vertical;
                    pl = horizontal;
                    break;
                }
                case 3: {
                    pt = parsePx(parts[0]);
                    pr = parsePx(parts[1]);
                    pb = parsePx(parts[2]);
                    pl = parsePx(parts[1]);
                    break;
                }
                case 4: {
                    pt = parsePx(parts[0]);
                    pr = parsePx(parts[1]);
                    pb = parsePx(parts[2]);
                    pl = parsePx(parts[3]);
                    break;
                }
            }
        }

        if (style.containsKey("padding-top")) {
            pt = parsePx(style.get("padding-top"));
        }
        if (style.containsKey("padding-right")) {
            pr = parsePx(style.get("padding-right"));
        }
        if (style.containsKey("padding-bottom")) {
            pb = parsePx(style.get("padding-bottom"));
        }
        if (style.containsKey("padding-left")) {
            pl = parsePx(style.get("padding-left"));
        }

        return new int[]{pt, pr, pb, pl};
    }

    private static String classAttribute(Map<String, String> attrs) {
        String value = attrs.getOrDefault("class", "");
        if (value.isEmpty()) {
            value = attrs.getOrDefault("className", "");
        }
        return value.isEmpty() ? "" : " class=\"" + value + "\"";
    }

    private static int parsePx(String s) {
        String value = s.trim();
        int end = value.startsWith("+") || value.startsWith("-") ? 1 : 0;
        while (end < value.length() && value.charAt(end) >= '0' && value.charAt(end) <= '9') {
            end++;
        }
        try {
            return Integer.parseInt(value.substring(0, end));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static int pxToPt(int px) {
        return Math.floorDiv(px * 3 + 2, 4);
    }

    private static double[] computeFontWidthAndSpaceCount(int expectedWidth) {
        if (expectedWidth <= 0) {
            return new double[]{0, 0};
        }

        int spaceCount = Math.min(1000, Math.max(1, (int) Math.ceil(expectedWidth / 10.0)));
        return new double[]{(double) expectedWidth / spaceCount / 2.0, spaceCount};
    }

}
