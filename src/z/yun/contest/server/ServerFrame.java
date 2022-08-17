package z.yun.contest.server;

import z.yun.contest.ContestApp;

import javax.swing.*;
import java.awt.*;

public class ServerFrame extends JDialog {

    private final ContestHost host;
    private final JTextArea log;
    private final ParticipantList list;

    public ServerFrame(ContestApp contestApp, String text, int port) {
        super(contestApp, "Server", false);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        host = new ContestHost(new Contest(text, "Fun little first ever contest", "yun", "lel"), port);

        add(log = new JTextArea(), BorderLayout.CENTER);
        add(list = new ParticipantList(host), BorderLayout.WEST);

        host.addConnectListener(c -> log.setText(log.getText() + "\n" + c.getSessionId().toString() + " connected!"));
        host.addDisconnectListener(c -> log.setText(log.getText() + "\n" + c.getSessionId().toString() + " disconnected!"));
        setPreferredSize(new Dimension(400, 400));
        pack();
        setLocationRelativeTo(null);
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible)
            try {
                host.start();
            } catch (Exception e) {
                System.out.println("Error");
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
            }
        else host.stop();
    }

    public void dispose() {
        super.dispose();
        host.stop();
    }
}
