package z.yun.contest.server.ui;

import z.yun.contest.Fonts;
import z.yun.contest.Icons;
import z.yun.contest.TextPrompt;
import z.yun.contest.Utils;
import z.yun.contest.server.ContestHost;
import z.yun.contest.server.log.LogOutputStreamAppender;
import z.yun.contest.server.log.TextAreaOutputStream;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Paths;

public class ContestSetup extends JPanel {
    private final JFileChooser fileChooser = new JFileChooser(Paths.get("").toFile());

    public ContestSetup(final ContestHost host) {
        setLayout(new BorderLayout());
        add(createOptions(host), BorderLayout.CENTER);
        add(createToolbar(host), BorderLayout.SOUTH);
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files", "json");
        fileChooser.addChoosableFileFilter(filter);
    }

    private JToolBar createToolbar(ContestHost host) {
        JToolBar bar = new JToolBar();
        bar.add(new AbstractAction("sync", Icons.SYNC) {
            @Override
            public void actionPerformed(ActionEvent e) {
                host.refresh();
            }
        });
        return bar;
    }

    private Box createOptions(ContestHost host) {
        Box box = Box.createVerticalBox();
        box.add(Box.createVerticalStrut(10));
        box.add(generalOptions(host));
        box.add(fileOptions(host));
        box.add(socketOptions(host));
        box.add(Box.createVerticalGlue());
        box.add(Box.createVerticalStrut(10));
        return box;
    }

    private Component socketOptions(ContestHost host) {
        JTextArea log = new JTextArea();
        log.setFont(Fonts.spacemono.deriveFont(7.5f));
        log.setEditable(false);
        LogOutputStreamAppender.setStaticOutputStream(new TextAreaOutputStream(log));
        return new JScrollPane(log) {{
            setPreferredSize(new Dimension(9999, 9999));
        }};
    }

    private Box fileOptions(ContestHost host) {
        Box box = Box.createVerticalBox();
        box.setBorder(new TitledBorder("File IO"));
        File file = host.savedTo.getAsOptional().orElse(new File("contest.json"));
        host.savedTo.set(file);
        JTextField saveTo = new JTextField(file.getAbsolutePath());
        host.savedTo.listen(n -> saveTo.setText(n.getAbsolutePath()));
        fileChooser.setSelectedFile(file);

        saveTo.setEditable(false);
        saveTo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(ContestSetup.this))
                    host.savedTo.set(fileChooser.getSelectedFile());
            }
        });
        box.add(saveTo);
        return box;
    }

    private Box generalOptions(ContestHost host) {
        Box box = Box.createVerticalBox();
        box.setBorder(new TitledBorder("General"));

        JTextField title = new JTextField(host.contest.title);
        title.setBorder(new TitledBorder("Title"));
        new TextPrompt("A simple title", title);
        Utils.addChangeListener(title, e -> host.contest.title = title.getText());

        JTextArea desc = new JTextArea(host.contest.description);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setBorder(new TitledBorder("Description"));
        new TextPrompt("Describe this contest", desc);
        Utils.addChangeListener(desc, e -> host.contest.description = desc.getText());

        JTextField by = new JTextField(host.contest.hostedBy);
        by.setBorder(new TitledBorder("Hosted By"));
        new TextPrompt("Your beautiful name~", by);
        Utils.addChangeListener(by, e -> host.contest.hostedBy = by.getText());

        JTextField image = new JTextField(host.contest.image);
        image.setBorder(new TitledBorder("Image"));
        new TextPrompt("URL leading to an image for your contest", image);
        Utils.addChangeListener(image, e -> host.contest.image = image.getText());

        JTextField port = new JTextField(String.valueOf(host.port));
        port.setBorder(new TitledBorder("Port"));
        port.setEditable(false);

        JTextField publicUrl = new JTextField(host.publicURL.or("<unset>"));
        host.publicURL.listen(publicUrl::setText);
        publicUrl.setBorder(new TitledBorder("Ngrok"));
        publicUrl.setEditable(false);
        publicUrl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        publicUrl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setEnabled(false);
                host.ngrok(() -> publicUrl.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)), () -> setEnabled(true));
            }
        });
        Box network = Box.createHorizontalBox();
        network.add(publicUrl);
        network.add(port);
        box.add(title);
        box.add(desc);
        box.add(by);
        box.add(image);
        box.add(network);

        return box;
    }
}
