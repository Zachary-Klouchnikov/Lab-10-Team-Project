package view;

import interface_adapter.auth.AuthController;
import interface_adapter.auth.AuthState;
import interface_adapter.auth.AuthViewModel;
import use_case.auth.AuthInputData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Swing panel for Steam login functionality.
 */
public class AuthView extends JPanel implements ActionListener, PropertyChangeListener {
    private final String viewName = "authentication";
    private JButton loginButton;
    private JLabel statusLabel;
    private JProgressBar progressBar;

    private AuthController authController = null;
    private AuthViewModel authViewModel;
    /**
     * Creates a new LoginPanel.
     */
    public AuthView(AuthViewModel authViewModel) {
        this.authViewModel = authViewModel;
        this.authViewModel.addPropertyChangeListener(this);

        initializeUI();
    }

    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(42, 42, 42)); // Dark background
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title label
        JLabel titleLabel = new JLabel("Steam Wrapped");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Sign in with your Steam account");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 200, 200));
        gbc.gridy = 1;
        add(subtitleLabel, gbc);

        // Steam login button (styled to look like Steam's button)
        loginButton = createSteamButton();
        gbc.gridy = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        add(loginButton, gbc);

        // Progress bar (initially hidden)
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(200, 20));
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(progressBar, gbc);

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 4;
        add(statusLabel, gbc);
    }

    /**
     * Creates a Steam-styled login button.
     *
     * @return The styled button
     */
    private JButton createSteamButton() {
        JButton button = new JButton("Sign in through Steam");
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(27, 40, 56)); // Steam blue
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 114, 188), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 114, 188));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(27, 40, 56));
            }
        });

        // Add action listener
        button.addActionListener(this::handleLogin);

        return button;
    }

    /**
     * Handles the login button click.
     *
     * @param e The action event
     */
    private void handleLogin(ActionEvent e) {
        // Start authentication
        authController.execute(new AuthInputData(loginButton, progressBar, statusLabel));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Clicked: " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final AuthState state = (AuthState) evt.getNewValue();

        JOptionPane.showMessageDialog(
            AuthView.this,
            state.getErrorMessage(),
            "Authentication Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public String getViewName() {
        return this.viewName;
    }

    public void setAuthController(AuthController authController) {
        this.authController = authController;
    }
}
