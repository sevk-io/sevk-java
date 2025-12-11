package io.sevk;

import io.sevk.markup.MarkupRenderer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestMarkupOutput {
    public static void main(String[] args) throws IOException {
        // Read test markup
        Path testMarkupPath = Paths.get("..", "test-markup.txt");
        String testMarkup = Files.readString(testMarkupPath);

        // Generate output
        String output = MarkupRenderer.render(testMarkup);

        // Write output to file
        Path outputPath = Paths.get("markup-output.html");
        Files.writeString(outputPath, output);

        System.out.println("Java markup output written to markup-output.html");
    }
}
