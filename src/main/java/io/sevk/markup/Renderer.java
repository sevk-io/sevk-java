package io.sevk.markup;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders sevk markup to email-compatible HTML.
 */
public class Renderer {
    private static final String DOCTYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
    private static final String DEFAULT_FONT_FAMILY = "ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif";

    /**
     * Render sevk markup to HTML.
     */
    public static String render(String markup) {
        return render(markup, null);
    }

    /**
     * Render sevk markup to HTML with optional head settings.
     */
    public static String render(String markup, EmailHeadSettings headSettings) {
        if (markup == null) {
            markup = "";
        }

        ParsedEmailContent parsed = parseEmailHTML(markup);
        EmailHeadSettings settings = headSettings != null ? headSettings : parsed.headSettings;
        String contentToProcess = parsed.body;

        String normalized = EmailDocumentParser.normalizeMarkup(contentToProcess);
        List<String> gapStylesList = new ArrayList<>();
        String processed = MarkupProcessor.processMarkup(normalized, gapStylesList);

        String gapStyles = "";
        if (!gapStylesList.isEmpty()) {
            StringBuilder gsb = new StringBuilder();
            gsb.append("@media only screen and (max-width:479px){");

            for (String gs : gapStylesList) {
                gsb.append(gs);
            }

            gsb.append("}");
            gapStyles = gsb.toString();
        }

        String gapStyleTag = !gapStyles.isEmpty()
            ? "<style type=\"text/css\">" + gapStyles + "</style>" : "";

        String titleTag = settings.title != null && !settings.title.isEmpty()
            ? "<title>" + settings.title + "</title>" : "";

        String fontLinks = EmailDocumentParser.generateFontLinks(settings.fonts);

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
            "<body style=\"margin:0;padding:0;word-spacing:normal;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;font-family:" + DEFAULT_FONT_FAMILY + ";\">\n" +
            "<div aria-roledescription=\"email\" role=\"article\">\n" +
            previewText + "\n" +
            processed + "\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
    }

    /**
     * Parse email markup into its body and head settings.
     */
    public static ParsedEmailContent parseEmailHTML(String content) {
        return EmailDocumentParser.parseEmailHTML(content);
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
