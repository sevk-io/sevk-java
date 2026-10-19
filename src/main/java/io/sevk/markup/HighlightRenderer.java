package io.sevk.markup;

import com.codewaves.codehighlight.core.StyleRenderer;

final class HighlightRenderer implements StyleRenderer {
    private static final String TOKEN_SPACE = "\u00A0\u200D\u200B";

    private final StringBuilder output = new StringBuilder();

    static String escapeText(String text) {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
            .replace(" ", TOKEN_SPACE);
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onFinish() {
    }

    @Override
    public void onPushStyle(String style) {
        output.append("<span class=\"").append(style).append("\">");
    }

    @Override
    public void onPopStyle() {
        output.append("</span>");
    }

    @Override
    public void onPushCodeBlock(CharSequence block) {
        output.append(escapeText(block.toString()));
    }

    @Override
    public void onPushSubLanguage(String name, CharSequence block) {
        output.append(block);
    }

    @Override
    public void onAbort(CharSequence code) {
        output.setLength(0);
        output.append(escapeText(code.toString()));
    }

    @Override
    public CharSequence getResult() {
        return output;
    }
}
