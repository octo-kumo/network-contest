package z.yun.contest;

import com.formdev.flatlaf.FlatDarculaLaf;
import z.yun.contest.client.ClientFrame;
import z.yun.contest.server.ServerFrame;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ContestApp extends JFrame {
    public ContestApp() {
        super("Network Contest");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(createLoginForm(), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    public Component createLoginForm() {
        Box loginForm = Box.createVerticalBox();
        loginForm.setBorder(new CompoundBorder(new TitledBorder("Login"), new EmptyBorder(20, 15, 20, 15)));
        JTextField name = new JTextField("Anonymous", 10);
        name.setMaximumSize(new Dimension(Integer.MAX_VALUE, name.getPreferredSize().height));
        name.setBorder(new TitledBorder("Name"));
        JTextField host = new JTextField("http://localhost:9092", 10);
        host.setMaximumSize(new Dimension(Integer.MAX_VALUE, name.getPreferredSize().height));
        host.setBorder(new TitledBorder("Host"));
        loginForm.add(host);
        loginForm.add(name);
        loginForm.add(new JButton("Login") {{
            setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
            addActionListener(e -> new ClientFrame(ContestApp.this, host.getText(), name.getText()).setVisible(true));
        }});
        loginForm.add(Box.createVerticalStrut(10));
        loginForm.add(new JSeparator());
        loginForm.add(Box.createVerticalStrut(10));

        JTextField port = new JTextField("9092", 10);
        port.setMaximumSize(new Dimension(Integer.MAX_VALUE, name.getPreferredSize().height));
        port.setBorder(new TitledBorder("Port"));

        JTextField title = new JTextField("CS6132 Network Contest", 10);
        title.setMaximumSize(new Dimension(Integer.MAX_VALUE, name.getPreferredSize().height));
        title.setBorder(new TitledBorder("Title"));
        loginForm.add(title);
        loginForm.add(port);

        loginForm.add(new JButton("I am host") {{
            setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
            addActionListener(e -> new ServerFrame(ContestApp.this, title.getText(), Integer.parseInt(port.getText())).setVisible(true));
        }});
        loginForm.add(Box.createGlue());
        return loginForm;
    }

    public static void main(String[] args) {
        Fonts.loadFonts();
        Utils.setupIconColors();
        FlatDarculaLaf.setup();
        ContestApp contest = new ContestApp();
        contest.setVisible(true);
    }
}
