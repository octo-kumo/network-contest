package z.yun.contest;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class Fonts {
    public static Font montserrat;
    public static Font opensans;
    public static Font spacemono;

    public static void loadFonts() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(montserrat = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(Fonts.class.getResourceAsStream("/fonts/montserrat.ttf"))));
            ge.registerFont(opensans = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(Fonts.class.getResourceAsStream("/fonts/opensans.ttf"))));
            ge.registerFont(spacemono = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(Fonts.class.getResourceAsStream("/fonts/spacemono.ttf"))));
        } catch (IOException | FontFormatException e) {
            //Handle exception
            e.printStackTrace();
        }
    }
}
