package z.yun.contest;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.Objects;

public class Icons {
    public static FlatSVGIcon PLUS = new FlatSVGIcon(Objects.requireNonNull(Icons.class.getResource("/icons/plus.svg")));
    public static FlatSVGIcon CLOSE = new FlatSVGIcon(Objects.requireNonNull(Icons.class.getResource("/icons/close.svg")));
    public static FlatSVGIcon SAVE = new FlatSVGIcon(Objects.requireNonNull(Icons.class.getResource("/icons/content-save.svg")));
    public static FlatSVGIcon PLAY = new FlatSVGIcon(Objects.requireNonNull(Icons.class.getResource("/icons/play.svg")));
    public static FlatSVGIcon SYNC = new FlatSVGIcon(Objects.requireNonNull(Icons.class.getResource("/icons/sync.svg")));

    public static FlatSVGIcon POWER = new FlatSVGIcon(Objects.requireNonNull(Icons.class.getResource("/icons/power.svg")));
    public static FlatSVGIcon STOP = new FlatSVGIcon(Objects.requireNonNull(Icons.class.getResource("/icons/stop.svg")));

    public static FlatSVGIcon GOLD_MEDAL = new FlatSVGIcon(Objects.requireNonNull(Icons.class.getResource("/icons/medal.svg"))).derive(40, 40);
    public static FlatSVGIcon SILVER_MEDAL = new FlatSVGIcon(Objects.requireNonNull(Icons.class.getResource("/icons/medal.svg"))) {{
        setColorFilter(new ColorFilter()
                .add(new Color(0xEFC75E), new Color(0xf0f0f0))
                .add(new Color(0xD7B354), new Color(0xd6d6d6)));
    }}.derive(40, 40);
    public static FlatSVGIcon BRONZE_MEDAL = new FlatSVGIcon(Objects.requireNonNull(Icons.class.getResource("/icons/medal.svg"))) {{
        setColorFilter(new ColorFilter()
                .add(new Color(0xEFC75E), new Color(0xcd7f40))
                .add(new Color(0xD7B354), new Color(0xb36f2b)));
    }}.derive(40, 40);
    public static Icon LOADING = new Icon() {
        private final double r = 7;
        private final Arc2D.Double arc = new Arc2D.Double();

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            int a = (int) Utils.map(2_000, 360, System.currentTimeMillis());
            int b = (int) (Utils.map(2_000, 720, System.currentTimeMillis()) - 360);
            arc.setArc(x, y, r * 2, r * 2, a, b, Arc2D.OPEN);
            ((Graphics2D) g).draw(arc);
        }

        @Override
        public int getIconWidth() {
            return (int) (r * 2);
        }

        @Override
        public int getIconHeight() {
            return (int) (r * 2);
        }
    };
}
