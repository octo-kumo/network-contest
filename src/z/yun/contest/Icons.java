package z.yun.contest;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import java.util.Objects;

public class Icons {
    public static FlatSVGIcon PLUS = new FlatSVGIcon(Objects.requireNonNull(Icons.class.getResource("/icons/plus.svg")));
    public static FlatSVGIcon CLOSE = new FlatSVGIcon(Objects.requireNonNull(Icons.class.getResource("/icons/close.svg")));
    public static FlatSVGIcon SAVE = new FlatSVGIcon(Objects.requireNonNull(Icons.class.getResource("/icons/content-save.svg")));
    public static FlatSVGIcon PLAY = new FlatSVGIcon(Objects.requireNonNull(Icons.class.getResource("/icons/play.svg")));
    public static FlatSVGIcon SYNC = new FlatSVGIcon(Objects.requireNonNull(Icons.class.getResource("/icons/sync.svg")));
}
