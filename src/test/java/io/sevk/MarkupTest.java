package io.sevk;

import io.sevk.markup.Renderer;
import org.junit.jupiter.api.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Sevk markup/block/template engine via the public render() method.
 */
class MarkupTest {

    /**
     * Helper: render markup and extract just the body content (strip the full HTML document wrapper).
     */
    private static String renderBody(String markup) {
        String html = Renderer.render(markup);
        Matcher m = Pattern.compile("<body[^>]*>([\\s\\S]*)</body>").matcher(html);
        return m.find() ? m.group(1).trim() : html;
    }

    // ============================================
    // BLOCK: SIMPLE VARIABLE
    // ============================================

    @Test
    void blockWithSimpleVariable() {
        String body = renderBody("<block config=\"{'text':'Hello'}\"><paragraph>{%text%}</paragraph></block>");
        assertTrue(body.contains("Hello"));
    }

    @Test
    void blockMissingVariableRendersEmpty() {
        String body = renderBody("<block config=\"{'other':'val'}\"><paragraph>{%missing%}</paragraph></block>");
        assertFalse(body.contains("{%missing%}"));
    }

    // ============================================
    // BLOCK: FALLBACK VALUES
    // ============================================

    @Test
    void blockFallbackUsedWhenMissing() {
        String body = renderBody("<block config=\"{}\"><paragraph>{%color ?? #000%}</paragraph></block>");
        assertTrue(body.contains("#000"));
    }

    @Test
    void blockFallbackNotUsedWhenPresent() {
        String body = renderBody("<block config=\"{'color':'#fff'}\"><paragraph>{%color ?? #000%}</paragraph></block>");
        assertTrue(body.contains("#fff"));
        assertFalse(body.contains("#000"));
    }

    // ============================================
    // BLOCK: EACH LOOP
    // ============================================

    @Test
    void blockEachLoop() {
        String body = renderBody("<block config=\"{'items':[{'name':'A'},{'name':'B'}]}\">{%#each items as item%}<paragraph>{%item.name%}</paragraph>{%/each%}</block>");
        assertTrue(body.contains("A"));
        assertTrue(body.contains("B"));
    }

    @Test
    void blockEachLoopEmptyArray() {
        String body = renderBody("<block config=\"{'items':[]}\">{%#each items as item%}<paragraph>{%item.name%}</paragraph>{%/each%}</block>");
        assertFalse(body.contains("<p"));
    }

    @Test
    void blockEachLoopMissingArray() {
        String body = renderBody("<block config=\"{}\">{%#each items as item%}<paragraph>{%item.name%}</paragraph>{%/each%}</block>");
        assertFalse(body.contains("<p"));
    }

    // ============================================
    // BLOCK: IF/ELSE
    // ============================================

    @Test
    void blockIfTrue() {
        String body = renderBody("<block config=\"{'show':true}\">{%#if show%}<paragraph>Visible</paragraph>{%/if%}</block>");
        assertTrue(body.contains("Visible"));
    }

    @Test
    void blockIfFalse() {
        String body = renderBody("<block config=\"{'show':false}\">{%#if show%}<paragraph>Visible</paragraph>{%/if%}</block>");
        assertFalse(body.contains("Visible"));
    }

    @Test
    void blockIfElseTrueBranch() {
        String body = renderBody("<block config=\"{'show':true}\">{%#if show%}<paragraph>YES</paragraph>{%else%}<paragraph>NO</paragraph>{%/if%}</block>");
        assertTrue(body.contains("YES"));
        assertFalse(body.contains("NO"));
    }

    @Test
    void blockIfElseFalseBranch() {
        String body = renderBody("<block config=\"{'show':false}\">{%#if show%}<paragraph>YES</paragraph>{%else%}<paragraph>NO</paragraph>{%/if%}</block>");
        assertTrue(body.contains("NO"));
        assertFalse(body.contains("YES"));
    }

    // ============================================
    // BLOCK: NESTED IF
    // ============================================

    @Test
    void blockNestedIfBothTrue() {
        String body = renderBody("<block config=\"{'a':true,'b':true}\">{%#if a%}{%#if b%}<paragraph>AB</paragraph>{%else%}<paragraph>A</paragraph>{%/if%}{%else%}<paragraph>NONE</paragraph>{%/if%}</block>");
        assertTrue(body.contains("AB"));
        assertFalse(body.contains("NONE"));
    }

    @Test
    void blockNestedIfOuterTrueInnerFalse() {
        String body = renderBody("<block config=\"{'a':true,'b':false}\">{%#if a%}{%#if b%}<paragraph>AB</paragraph>{%else%}<paragraph>A-only</paragraph>{%/if%}{%else%}<paragraph>NONE</paragraph>{%/if%}</block>");
        assertTrue(body.contains("A-only"));
        assertFalse(body.contains("NONE"));
    }

    @Test
    void blockNestedIfOuterFalse() {
        String body = renderBody("<block config=\"{'a':false,'b':true}\">{%#if a%}{%#if b%}<paragraph>AB</paragraph>{%else%}<paragraph>A</paragraph>{%/if%}{%else%}<paragraph>NONE</paragraph>{%/if%}</block>");
        assertTrue(body.contains("NONE"));
        assertFalse(body.contains("AB"));
    }

    // ============================================
    // BLOCK: {{VARIABLE}} PRESERVATION
    // ============================================

    @Test
    void blockPreservesDoubleBraceVariables() {
        String body = renderBody("<block config=\"{'text':'Unsub'}\"><paragraph><link href=\"{{unsubscribeUrl}}\">{%text%}</link></paragraph></block>");
        assertTrue(body.contains("{{unsubscribeUrl}}"));
        assertTrue(body.contains("Unsub"));
    }

    // ============================================
    // BLOCK: PARAGRAPH
    // ============================================

    @Test
    void blockWithParagraph() {
        String body = renderBody("<block config=\"{'content':'Hello world'}\"><paragraph>{%content%}</paragraph></block>");
        assertTrue(body.contains("Hello world"));
        assertTrue(body.contains("<p"));
    }

    // ============================================
    // BLOCK: HEADING
    // ============================================

    @Test
    void blockWithHeading() {
        String body = renderBody("<block config=\"{'title':'My Title'}\"><heading level=\"1\">{%title%}</heading></block>");
        assertTrue(body.contains("My Title"));
        assertTrue(body.contains("<h1"));
    }

    // ============================================
    // BLOCK: BUTTON
    // ============================================

    @Test
    void blockWithButton() {
        String body = renderBody("<block config=\"{'url':'https://sevk.io','label':'Click'}\"><button href=\"{%url%}\">{%label%}</button></block>");
        assertTrue(body.contains("https://sevk.io"));
        assertTrue(body.contains("Click"));
        assertTrue(body.contains("<a"));
    }

    // ============================================
    // BLOCK: IMAGE
    // ============================================

    @Test
    void blockWithImage() {
        String body = renderBody("<block config=\"{'src':'https://example.com/img.png'}\"><paragraph><image src=\"{%src%}\" /></paragraph></block>");
        assertTrue(body.contains("https://example.com/img.png"));
    }

    // ============================================
    // BLOCK: SECTION
    // ============================================

    @Test
    void blockWithSection() {
        String body = renderBody("<block config=\"{'bg':'#f0f0f0'}\"><section background-color=\"{%bg%}\"><paragraph>test</paragraph></section></block>");
        assertTrue(body.contains("#f0f0f0"));
        assertTrue(body.contains("<table"));
    }

    // ============================================
    // BLOCK: LINK
    // ============================================

    @Test
    void blockWithLink() {
        String body = renderBody("<block config=\"{'url':'https://sevk.io','text':'Visit'}\"><paragraph><link href=\"{%url%}\">{%text%}</link></paragraph></block>");
        assertTrue(body.contains("href=\"https://sevk.io\""));
        assertTrue(body.contains("Visit"));
        assertTrue(body.contains("<a"));
    }

    // ============================================
    // MULTIPLE BLOCKS
    // ============================================

    @Test
    void multipleBlocksInSameDocument() {
        String body = renderBody(
            "<block config=\"{'title':'Header'}\"><heading level=\"1\">{%title%}</heading></block>" +
            "<paragraph>Middle content</paragraph>" +
            "<block config=\"{'footer':'Footer'}\"><paragraph>{%footer%}</paragraph></block>"
        );
        assertTrue(body.contains("Header"));
        assertTrue(body.contains("Middle content"));
        assertTrue(body.contains("Footer"));
    }

    // ============================================
    // SOCIAL LINKS TEMPLATE
    // ============================================

    @Test
    void socialLinksBlockTemplate() {
        String config = "{'links':[{'href':'https://twitter.com','iconSrc':'x.png','platform':'x-twitter'}],'iconSize':24,'alignment':'center'}";
        String template = "<section text-align=\"{%alignment ?? center%}\">{%#each links as link%}<link href=\"{%link.href%}\"><image src=\"{%link.iconSrc%}\" width=\"{%iconSize%}px\" alt=\"{%link.platform%}\" /></link>{%/each%}</section>";
        String body = renderBody("<block config=\"" + config + "\">" + template + "</block>");
        assertTrue(body.contains("text-align:center"));
        assertTrue(body.contains("https://twitter.com"));
        assertTrue(body.contains("x.png"));
        assertTrue(body.contains("alt=\"x-twitter\""));
    }

    // ============================================
    // HEADER TEMPLATE
    // ============================================

    @Test
    void headerBlockCenteredLayout() {
        String config = "{'centered':true,'title':'My Brand'}";
        String template = "{%#if centered%}<section text-align=\"center\">{%#if title%}<heading level=\"1\">{%title%}</heading>{%/if%}</section>{%else%}<section><row><column><paragraph>{%title%}</paragraph></column></row></section>{%/if%}";
        String body = renderBody("<block config=\"" + config + "\">" + template + "</block>");
        assertTrue(body.contains("text-align:center"));
        assertTrue(body.contains("My Brand"));
    }

    @Test
    void headerBlockSideLayout() {
        String config = "{'centered':false,'title':'My Brand'}";
        String template = "{%#if centered%}<section text-align=\"center\"><heading level=\"1\">{%title%}</heading></section>{%else%}<section><paragraph>{%title%}</paragraph></section>{%/if%}";
        String body = renderBody("<block config=\"" + config + "\">" + template + "</block>");
        assertTrue(body.contains("My Brand"));
        assertFalse(body.contains("text-align:center"));
    }

    // ============================================
    // DOCUMENT STRUCTURE
    // ============================================

    @Test
    void producesValidHtmlDocument() {
        String html = Renderer.render("<paragraph>Hello</paragraph>");
        assertTrue(html.contains("<!DOCTYPE html"));
        assertTrue(html.contains("<html"));
        assertTrue(html.contains("<head>"));
        assertTrue(html.contains("<body"));
        assertTrue(html.contains("</html>"));
    }

    @Test
    void includesMetaTags() {
        String html = Renderer.render("<paragraph>test</paragraph>");
        assertTrue(html.contains("charset=UTF-8"));
        assertTrue(html.contains("viewport"));
    }

    @Test
    void includesEmailSafeStyles() {
        String html = Renderer.render("<paragraph>test</paragraph>");
        assertTrue(html.contains("border-collapse"));
    }

    @Test
    void emptyInputProducesValidHtml() {
        String html = Renderer.render("");
        assertTrue(html.contains("<!DOCTYPE html"));
        assertTrue(html.contains("</html>"));
    }

    // ============================================
    // PARAGRAPH
    // ============================================

    @Test
    void rendersBasicParagraph() {
        String body = renderBody("<paragraph>Hello world</paragraph>");
        assertTrue(body.contains("Hello world"));
        assertTrue(body.contains("<p"));
    }

    @Test
    void paragraphWithColor() {
        String body = renderBody("<paragraph color=\"#ff0000\">Red text</paragraph>");
        assertTrue(body.contains("#ff0000"));
        assertTrue(body.contains("Red text"));
    }

    @Test
    void paragraphWithFontSize() {
        String body = renderBody("<paragraph font-size=\"18px\">Big</paragraph>");
        assertTrue(body.contains("18px"));
    }

    @Test
    void paragraphWithTextAlign() {
        String body = renderBody("<paragraph text-align=\"center\">Centered</paragraph>");
        assertTrue(body.contains("text-align:center"));
    }

    @Test
    void paragraphWithPadding() {
        String body = renderBody("<paragraph padding=\"10px 20px\">Padded</paragraph>");
        assertTrue(body.contains("10px 20px"));
    }

    @Test
    void paragraphWithBackgroundColor() {
        String body = renderBody("<paragraph background-color=\"#eee\">BG</paragraph>");
        assertTrue(body.contains("#eee"));
    }

    // ============================================
    // HEADING
    // ============================================

    @Test
    void rendersH1() {
        String body = renderBody("<heading level=\"1\">Title</heading>");
        assertTrue(body.contains("<h1"));
        assertTrue(body.contains("Title"));
    }

    @Test
    void rendersH2() {
        String body = renderBody("<heading level=\"2\">Subtitle</heading>");
        assertTrue(body.contains("<h2"));
    }

    @Test
    void rendersH3() {
        String body = renderBody("<heading level=\"3\">Small</heading>");
        assertTrue(body.contains("<h3"));
    }

    @Test
    void headingWithColor() {
        String body = renderBody("<heading level=\"1\" color=\"#5227FF\">Colored</heading>");
        assertTrue(body.contains("#5227FF"));
    }

    @Test
    void headingWithFontSize() {
        String body = renderBody("<heading level=\"2\" font-size=\"28px\">Sized</heading>");
        assertTrue(body.contains("28px"));
    }

    // ============================================
    // BUTTON
    // ============================================

    @Test
    void rendersButtonWithHref() {
        String body = renderBody("<button href=\"https://sevk.io\">Click me</button>");
        assertTrue(body.contains("https://sevk.io"));
        assertTrue(body.contains("Click me"));
    }

    @Test
    void buttonWithBackgroundColor() {
        String body = renderBody("<button href=\"#\" background-color=\"#5227FF\">Go</button>");
        assertTrue(body.contains("#5227FF"));
    }

    @Test
    void buttonWithTextColor() {
        String body = renderBody("<button href=\"#\" color=\"#ffffff\">Go</button>");
        assertTrue(body.contains("#ffffff"));
    }

    @Test
    void buttonWithBorderRadius() {
        String body = renderBody("<button href=\"#\" border-radius=\"8px\">Go</button>");
        assertTrue(body.contains("8px"));
    }

    @Test
    void buttonRendersAsAnchorLink() {
        String body = renderBody("<button href=\"https://sevk.io\">CTA</button>");
        assertTrue(body.contains("<a"));
        assertTrue(body.contains("href=\"https://sevk.io\""));
    }

    // ============================================
    // SECTION
    // ============================================

    @Test
    void sectionRendersWithTableWrapper() {
        String body = renderBody("<section><paragraph>Content</paragraph></section>");
        assertTrue(body.contains("Content"));
        assertTrue(body.contains("<table"));
    }

    @Test
    void sectionWithBackgroundColor() {
        String body = renderBody("<section background-color=\"#f0f0f0\"><paragraph>BG</paragraph></section>");
        assertTrue(body.contains("#f0f0f0"));
    }

    @Test
    void sectionWithPadding() {
        String body = renderBody("<section padding=\"40px 0\"><paragraph>Padded</paragraph></section>");
        assertTrue(body.contains("40px"));
    }

    @Test
    void sectionWithTextAlign() {
        String body = renderBody("<section text-align=\"center\"><paragraph>Centered</paragraph></section>");
        assertTrue(body.contains("text-align:center"));
    }

    // ============================================
    // CONTAINER
    // ============================================

    @Test
    void containerWithMaxWidth() {
        String body = renderBody("<container max-width=\"600px\"><paragraph>Content</paragraph></container>");
        assertTrue(body.contains("600px"));
        assertTrue(body.contains("Content"));
    }

    @Test
    void containerWithPadding() {
        String body = renderBody("<container max-width=\"600px\" padding=\"20px\"><paragraph>P</paragraph></container>");
        assertTrue(body.contains("20px"));
    }

    // ============================================
    // ROW & COLUMN
    // ============================================

    @Test
    void rowWithColumns() {
        String body = renderBody("<row><column width=\"50%\"><paragraph>Left</paragraph></column><column width=\"50%\"><paragraph>Right</paragraph></column></row>");
        assertTrue(body.contains("Left"));
        assertTrue(body.contains("Right"));
        assertTrue(body.contains("table"));
    }

    @Test
    void columnRespectsWidth() {
        String body = renderBody("<row><column width=\"33%\"><paragraph>A</paragraph></column><column width=\"67%\"><paragraph>B</paragraph></column></row>");
        assertTrue(body.contains("33%"));
        assertTrue(body.contains("67%"));
    }

    @Test
    void columnWithBackgroundColor() {
        String body = renderBody("<row><column background-color=\"#e3f2fd\"><paragraph>Col</paragraph></column></row>");
        assertTrue(body.contains("#e3f2fd"));
    }

    @Test
    void columnWithBorderRadius() {
        String body = renderBody("<row><column border-radius=\"8px\"><paragraph>Col</paragraph></column></row>");
        assertTrue(body.contains("8px"));
    }

    // ============================================
    // NESTED STRUCTURE
    // ============================================

    @Test
    void sectionContainerParagraphNesting() {
        String body = renderBody("<section><container max-width=\"600px\"><paragraph>Nested</paragraph></container></section>");
        assertTrue(body.contains("Nested"));
        assertTrue(body.contains("600px"));
    }

    @Test
    void sectionContainerRowColumnsNesting() {
        String body = renderBody(
            "<section padding=\"20px 0\">" +
            "<container max-width=\"600px\">" +
            "<row>" +
            "<column width=\"50%\"><paragraph>Left</paragraph></column>" +
            "<column width=\"50%\"><paragraph>Right</paragraph></column>" +
            "</row>" +
            "</container>" +
            "</section>"
        );
        assertTrue(body.contains("Left"));
        assertTrue(body.contains("Right"));
        assertTrue(body.contains("600px"));
    }

    // ============================================
    // BLOCK: TRUTHINESS
    // ============================================

    @Test
    void blockIfEmptyStringIsFalsy() {
        String body = renderBody("<block config=\"{'val':''}\">{%#if val%}<paragraph>T</paragraph>{%else%}<paragraph>F</paragraph>{%/if%}</block>");
        assertTrue(body.contains("F"));
        assertFalse(body.contains(">T<"));
    }

    @Test
    void blockIfZeroIsFalsy() {
        String body = renderBody("<block config=\"{'val':0}\">{%#if val%}<paragraph>T</paragraph>{%else%}<paragraph>F</paragraph>{%/if%}</block>");
        assertTrue(body.contains("F"));
        assertFalse(body.contains(">T<"));
    }

    @Test
    void blockIfFalseIsFalsy() {
        String body = renderBody("<block config=\"{'val':false}\">{%#if val%}<paragraph>T</paragraph>{%else%}<paragraph>F</paragraph>{%/if%}</block>");
        assertTrue(body.contains("F"));
        assertFalse(body.contains(">T<"));
    }

    @Test
    void blockIfEmptyArrayIsFalsy() {
        String body = renderBody("<block config=\"{'val':[]}\">{%#if val%}<paragraph>T</paragraph>{%else%}<paragraph>F</paragraph>{%/if%}</block>");
        assertTrue(body.contains("F"));
        assertFalse(body.contains(">T<"));
    }

    @Test
    void blockIfNonEmptyStringIsTruthy() {
        String body = renderBody("<block config=\"{'val':'hello'}\">{%#if val%}<paragraph>T</paragraph>{%else%}<paragraph>F</paragraph>{%/if%}</block>");
        assertTrue(body.contains(">T<"));
        assertFalse(body.contains(">F<"));
    }

    @Test
    void blockIfNonEmptyArrayIsTruthy() {
        String body = renderBody("<block config=\"{'val':[1]}\">{%#if val%}<paragraph>T</paragraph>{%else%}<paragraph>F</paragraph>{%/if%}</block>");
        assertTrue(body.contains(">T<"));
        assertFalse(body.contains(">F<"));
    }

    @Test
    void blockIfNumberIsTruthy() {
        String body = renderBody("<block config=\"{'val':42}\">{%#if val%}<paragraph>T</paragraph>{%else%}<paragraph>F</paragraph>{%/if%}</block>");
        assertTrue(body.contains(">T<"));
        assertFalse(body.contains(">F<"));
    }

    // ============================================
    // BLOCK: COMBINED IF + EACH
    // ============================================

    @Test
    void blockCombinedIfAndEach() {
        String body = renderBody(
            "<block config=\"{'show':true,'items':[{'name':'X'},{'name':'Y'}]}\">" +
            "{%#if show%}{%#each items as item%}<paragraph>{%item.name%}</paragraph>{%/each%}{%/if%}" +
            "</block>"
        );
        assertTrue(body.contains("X"));
        assertTrue(body.contains("Y"));
    }

    @Test
    void blockIfFalseSkipsEachInside() {
        String body = renderBody(
            "<block config=\"{'show':false,'items':[{'name':'X'},{'name':'Y'}]}\">" +
            "{%#if show%}{%#each items as item%}<paragraph>{%item.name%}</paragraph>{%/each%}{%/if%}" +
            "</block>"
        );
        assertFalse(body.contains("X"));
        assertFalse(body.contains("Y"));
    }

    // ============================================
    // LANG AND DIR SUPPORT
    // ============================================

    @Test
    void defaultLangAndDir() {
        String html = Renderer.render("<paragraph>Hello</paragraph>");
        assertTrue(html.contains("lang=\"en\""));
        assertTrue(html.contains("dir=\"ltr\""));
    }

    @Test
    void customLangFromMailTag() {
        String html = Renderer.render("<mail lang=\"fr\"><body><paragraph>Bonjour</paragraph></body></mail>");
        assertTrue(html.contains("lang=\"fr\""));
        assertTrue(html.contains("dir=\"ltr\""));
    }

    @Test
    void customDirFromMailTag() {
        String html = Renderer.render("<mail dir=\"rtl\"><body><paragraph>مرحبا</paragraph></body></mail>");
        assertTrue(html.contains("lang=\"en\""));
        assertTrue(html.contains("dir=\"rtl\""));
    }

    @Test
    void customLangAndDirFromMailTag() {
        String html = Renderer.render("<mail lang=\"ar\" dir=\"rtl\"><body><paragraph>مرحبا</paragraph></body></mail>");
        assertTrue(html.contains("lang=\"ar\""));
        assertTrue(html.contains("dir=\"rtl\""));
    }

    @Test
    void customLangFromEmailTag() {
        String html = Renderer.render("<email lang=\"de\"><body><paragraph>Hallo</paragraph></body></email>");
        assertTrue(html.contains("lang=\"de\""));
    }

    @Test
    void headingParagraphButtonInSection() {
        String body = renderBody(
            "<section padding=\"20px\">" +
            "<heading level=\"1\" color=\"#333\">Welcome</heading>" +
            "<paragraph color=\"#666\">Description text</paragraph>" +
            "<button href=\"https://sevk.io\" background-color=\"#5227FF\" color=\"#fff\">Get Started</button>" +
            "</section>"
        );
        assertTrue(body.contains("Welcome"));
        assertTrue(body.contains("Description text"));
        assertTrue(body.contains("Get Started"));
        assertTrue(body.contains("#5227FF"));
    }
}
