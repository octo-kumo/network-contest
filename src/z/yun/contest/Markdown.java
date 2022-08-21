package z.yun.contest;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class Markdown {
    private static final Parser parser = Parser.builder().build();
    private static final HtmlRenderer renderer = HtmlRenderer.builder().build();

    public static String parse(String markdown) {
        return renderer.render(parser.parse(markdown));
    }
}
