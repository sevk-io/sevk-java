package io.sevk.markup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class TagScanner {
    private static final Pattern DIV_OPEN = Pattern.compile("<div", Pattern.CASE_INSENSITIVE);
    private static final Pattern DIV_CLOSE = Pattern.compile("</div>", Pattern.CASE_INSENSITIVE);
    private static final Pattern HIDDEN_STYLE = Pattern.compile("style=[\"'][^\"']*display:\\s*none", Pattern.CASE_INSENSITIVE);

    private TagScanner() {
    }

    static String replaceSelfClosing(String source, String tag, boolean selfCloseOnly, Function<String, String> processor) {
        Matcher opening = openingPattern(tag).matcher(source);
        int nameLength = tag.length() + 1;
        StringBuilder output = new StringBuilder();
        int lastIndex = 0;
        int position = 0;

        while (position <= source.length() && opening.find(position)) {
            int open = opening.start();
            int closingBracket = source.indexOf('>', open + nameLength);
            if (closingBracket == -1) {
                break;
            }
            position = closingBracket + 1;
            if (selfCloseOnly && source.charAt(closingBracket - 1) != '/') {
                continue;
            }

            int attributesEnd = selfCloseOnly ? closingBracket - 1 : closingBracket;
            output.append(source, lastIndex, open).append(processor.apply(source.substring(open + nameLength, attributesEnd)));
            lastIndex = closingBracket + 1;
        }

        return output.append(source, lastIndex, source.length()).toString();
    }

    static String getFirstTagContent(String source, String tag) {
        Matcher opening = openingPattern(tag).matcher(source);
        if (!opening.find()) {
            return null;
        }
        int closingBracket = source.indexOf('>', opening.start() + tag.length() + 1);
        if (closingBracket == -1) {
            return null;
        }
        Matcher closing = closingPattern(tag).matcher(source);
        if (!closing.find(closingBracket + 1)) {
            return null;
        }
        return source.substring(closingBracket + 1, closing.start());
    }

    static List<String> getAllTagContents(String source, String tag) {
        Matcher opening = openingPattern(tag).matcher(source);
        Matcher closing = closingPattern(tag).matcher(source);
        List<String> contents = new ArrayList<>();
        int position = 0;

        while (position <= source.length() && opening.find(position)) {
            int closingBracket = source.indexOf('>', opening.start() + tag.length() + 1);
            if (closingBracket == -1 || !closing.find(closingBracket + 1)) {
                break;
            }
            contents.add(source.substring(closingBracket + 1, closing.start()));
            position = closing.end();
        }

        return contents;
    }

    static String stripTagPairs(String source, String tag) {
        Matcher opening = openingPattern(tag).matcher(source);
        Matcher closing = closingPattern(tag).matcher(source);
        StringBuilder output = new StringBuilder();
        int lastIndex = 0;
        int position = 0;

        while (position <= source.length() && opening.find(position)) {
            int closingBracket = source.indexOf('>', opening.start() + tag.length() + 1);
            if (closingBracket == -1 || !closing.find(closingBracket + 1)) {
                break;
            }
            output.append(source, lastIndex, opening.start());
            lastIndex = closing.end();
            position = lastIndex;
        }

        return output.append(source, lastIndex, source.length()).toString();
    }

    static List<String> getTagAttributes(String source, String tag) {
        Matcher opening = openingPattern(tag).matcher(source);
        List<String> attributes = new ArrayList<>();
        int position = 0;

        while (position <= source.length() && opening.find(position)) {
            int closingBracket = source.indexOf('>', opening.start() + tag.length() + 1);
            if (closingBracket == -1) {
                break;
            }
            attributes.add(source.substring(opening.start() + tag.length() + 1, closingBracket));
            position = closingBracket + 1;
        }

        return attributes;
    }

    static String[] findHiddenPreviewDiv(String html) {
        Matcher opening = DIV_OPEN.matcher(html);
        Matcher closing = DIV_CLOSE.matcher(html);
        int position = 0;

        while (position <= html.length() && opening.find(position)) {
            int open = opening.start();
            int closingBracket = html.indexOf('>', open + 4);
            if (closingBracket == -1) {
                return null;
            }
            if (HIDDEN_STYLE.matcher(html.substring(open, closingBracket + 1)).find()) {
                if (!closing.find(closingBracket + 1)) {
                    return null;
                }
                return new String[] {html.substring(closingBracket + 1, closing.start()), html.substring(open, closing.end())};
            }
            position = closingBracket + 1;
        }

        return null;
    }

    private static Pattern openingPattern(String tag) {
        return Pattern.compile("<" + Pattern.quote(tag), Pattern.CASE_INSENSITIVE);
    }

    private static Pattern closingPattern(String tag) {
        return Pattern.compile("</" + Pattern.quote(tag) + ">", Pattern.CASE_INSENSITIVE);
    }
}
