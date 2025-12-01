package use_case.auth;

import javax.swing.*;

public class AuthInputData {
    private final JLabel status;
    private final JButton button;
    private final JProgressBar bar;

    public AuthInputData(JButton button, JProgressBar bar, JLabel label) {
        this.button = button;
        this.bar = bar;
        this.status = label;
    }

    public JLabel getStatus() {
        return this.status;
    }

    public JButton getButton() {
        return this.button;
    }

    public JProgressBar getBar() {
        return this.bar;
    }
}
