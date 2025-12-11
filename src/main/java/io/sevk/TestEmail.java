package io.sevk;

import io.sevk.markup.MarkupRenderer;
import io.sevk.types.Types.SendEmailRequest;

public class TestEmail {
    public static void main(String[] args) {
        String markup = "<mail>\n" +
            "  <head>\n" +
            "    <title>Java SDK Test Email</title>\n" +
            "    <preview>Testing Sevk Java SDK Markup Renderer</preview>\n" +
            "  </head>\n" +
            "  <body>\n" +
            "    <section padding=\"40px 20px\" background-color=\"#f8f9fa\">\n" +
            "      <container max-width=\"600px\">\n" +
            "        <section padding=\"0\" text-align=\"center\">\n" +
            "          <heading level=\"1\" color=\"#1a1a1a\">\n" +
            "            Sevk Email Editor Components\n" +
            "          </heading>\n" +
            "        </section>\n" +
            "        <section padding=\"20px 0\">\n" +
            "          <paragraph color=\"#333333\" font-size=\"16px\">\n" +
            "            This playground showcases all available components in the Sevk email editor.\n" +
            "          </paragraph>\n" +
            "        </section>\n" +
            "        <section padding=\"20px 0\">\n" +
            "          <heading level=\"2\" color=\"#5227FF\" font-size=\"24px\">\n" +
            "            1. Headings &amp; Paragraphs\n" +
            "          </heading>\n" +
            "          <paragraph padding=\"10px 0\" color=\"#666666\">\n" +
            "            Use headings (levels 1-3) and paragraphs to structure your content.\n" +
            "          </paragraph>\n" +
            "        </section>\n" +
            "        <section padding=\"20px 0\">\n" +
            "          <heading level=\"2\" color=\"#5227FF\" font-size=\"24px\">\n" +
            "            2. Buttons\n" +
            "          </heading>\n" +
            "          <section padding=\"10px 0\" text-align=\"center\">\n" +
            "            <button padding=\"15px 30px\" background-color=\"#5227FF\" color=\"#ffffff\" border-radius=\"8px\" href=\"https://sevk.io\">Primary Button</button>\n" +
            "          </section>\n" +
            "        </section>\n" +
            "        <section padding=\"30px 0\" margin=\"20px 0\" background-color=\"#f0f4ff\" text-align=\"center\" border-radius=\"12px\">\n" +
            "          <heading level=\"2\" color=\"#1a1a1a\" font-size=\"28px\">\n" +
            "            Ready to Build?\n" +
            "          </heading>\n" +
            "          <button padding=\"15px 40px\" background-color=\"#5227FF\" color=\"#ffffff\" border-radius=\"8px\" href=\"https://sevk.io\">Get Started Now</button>\n" +
            "        </section>\n" +
            "      </container>\n" +
            "    </section>\n" +
            "  </body>\n" +
            "</mail>";

        // Render markup to HTML
        String html = MarkupRenderer.render(markup);

        // Send email via API
        SevkOptions options = new SevkOptions().baseUrl("http://localhost:4000");
        Sevk sevk = new Sevk("sevk_full_344bd4e8a7d486b1b136f555aedd5123177cbc67616e733e0e7b23367906bf8b", options);

        try {
            SendEmailRequest request = new SendEmailRequest()
                .from("no-reply@sevk.io")
                .to("kubilay@kubilay.io")
                .subject("[Java SDK] Markup Render Test")
                .html(html);

            var result = sevk.emails().send(request);
            System.out.println("Email sent successfully!");
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
