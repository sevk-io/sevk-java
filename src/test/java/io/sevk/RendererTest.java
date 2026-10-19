package io.sevk;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.sevk.markup.Renderer;
import org.junit.jupiter.api.Test;

class RendererTest {
    @Test
    void rendersMarkupIntoAnHtmlEmailDocument() {
        String html = Renderer.render("<section><paragraph>Hello world</paragraph></section>");

        assertTrue(html.startsWith("<!DOCTYPE html PUBLIC"));
        assertTrue(html.contains("Hello world"));
        assertFalse(html.contains("<paragraph>"));
    }
}
