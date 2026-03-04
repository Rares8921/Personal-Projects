package main.backend;

import main.backend.service.MarkdownService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class MarkdownIntegrationTest extends BaseIntegrationTest {

    @Autowired private MarkdownService markdownService;

    @Test
    void testMarkdownRendering() {
        String md = "# Hello\n| A | B |\n|---|---|\n| 1 | 2 |";
        String html = markdownService.render(md);

        assertTrue(html.contains("<h1>Hello</h1>"));
        assertTrue(html.contains("<table>"));
    }
}