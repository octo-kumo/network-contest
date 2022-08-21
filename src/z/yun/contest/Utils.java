package z.yun.contest;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import okhttp3.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

public class Utils {
    /**
     * Installs a listener to receive notification when the text of any
     * {@code JTextComponent} is changed. Internally, it installs a
     * {@link DocumentListener} on the text component's {@link Document},
     * and a {@link PropertyChangeListener} on the text component to detect
     * if the {@code Document} itself is replaced.
     *
     * @param text           any text component, such as a {@link JTextField}
     *                       or {@link JTextArea}
     * @param changeListener a listener to receive {@link ChangeEvent}s
     *                       when the text is changed; the source object for the events
     *                       will be the text component
     * @throws NullPointerException if either parameter is null
     */
    public static void addChangeListener(JTextComponent text, ChangeListener changeListener) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(changeListener);
        DocumentListener dl = new DocumentListener() {
            private int lastChange = 0, lastNotifiedChange = 0;

            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lastChange++;
                SwingUtilities.invokeLater(() -> {
                    if (lastNotifiedChange != lastChange) {
                        lastNotifiedChange = lastChange;
                        changeListener.stateChanged(new ChangeEvent(text));
                    }
                });
            }
        };
        text.addPropertyChangeListener("document", e -> {
            Document d1 = (Document) e.getOldValue();
            Document d2 = (Document) e.getNewValue();
            if (d1 != null) d1.removeDocumentListener(dl);
            if (d2 != null) d2.addDocumentListener(dl);
            dl.changedUpdate(null);
        });
        Document d = text.getDocument();
        if (d != null) d.addDocumentListener(dl);
    }

    public static void setupIconColors() {
        FlatSVGIcon.ColorFilter.getInstance().add(Color.black, null, Color.white).add(Color.white, null, Color.black);
    }

    private static final HashMap<String, BufferedImage> CACHE = new HashMap<>();

    public static void downloadImage(String image, Consumer<Image> consumer) {

        if (CACHE.containsKey(image)) consumer.accept(CACHE.get(image));
        else {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(image).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    consumer.accept(null);
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        BufferedImage read = ImageIO.read(Objects.requireNonNull(response.body()).byteStream());
                        CACHE.put(image, read);
                        consumer.accept(read);
                    } catch (Exception e) {
                        e.printStackTrace();
                        consumer.accept(null);
                    }
                }
            });
        }
    }
}
