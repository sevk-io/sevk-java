package io.sevk;

import com.codewaves.codehighlight.core.Highlighter;
import com.codewaves.codehighlight.renderer.HtmlRenderer;

public class HighlightTest {
    public static void main(String[] args) {
        String code = "const x = 'hello';";
        Highlighter highlighter = new Highlighter(languageName -> new HtmlRenderer("hljs-"));
        
        try {
            Highlighter.HighlightResult result = highlighter.highlightAuto(code, null);
            System.out.println("Language: " + result.getLanguage());
            System.out.println("HTML: " + result.getResult().toString());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
