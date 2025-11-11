package ui;

import auth.SessionManager;
import auth.SteamAuthService;
import data_access.UserDataAcessObject;
import entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.CompletableFuture;

/**
 * Swing panel for Steam login functionality.
 */
public class LoginPanel extends JPanel {
    private JButton loginButton;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private SteamAuthService authService;
    private UserDataAcessObject userDAO;
    private LoginCallback callback;

    /**
     * Interface for login callbacks.
     */
    public interface LoginCallback {
        void onLoginSuccess(User user);
        void onLoginFailure(String error);
    }

    /**
     * Creates a new LoginPanel.
     *
     * @param callback The callback to invoke after login attempts
     */
    public LoginPanel(LoginCallback callback) {
        this.callback = callback;
        this.authService = new SteamAuthService();
        this.userDAO = new UserDataAcessObject();

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
        // Disable button and show progress
        loginButton.setEnabled(false);
        progressBar.setVisible(true);
        statusLabel.setText("Opening Steam login in browser...");

        // Start authentication in background thread
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                // Authenticate with Steam
                CompletableFuture<Long> authFuture = authService.authenticate();

                // Update status on EDT
                SwingUtilities.invokeLater(() ->
                    statusLabel.setText("Waiting for Steam authentication...")
                );

                // Wait for authentication
                Long steamId = authFuture.get();

                // Update status
                SwingUtilities.invokeLater(() ->
                    statusLabel.setText("Fetching user profile...")
                );

                // Fetch user data
                User user = userDAO.get(steamId);

                // Create session
                SessionManager.getInstance().createSession(steamId, user);

                return user;
            }

            @Override
            protected void done() {
                try {
                    User user = get();

                    // Hide progress and update status
                    progressBar.setVisible(false);
                    statusLabel.setText("Login successful!");

                    // Notify callback
                    if (callback != null) {
                        callback.onLoginSuccess(user);
                    }

                } catch (Exception ex) {
                    // Handle error
                    progressBar.setVisible(false);
                    loginButton.setEnabled(true);

                    String errorMessage = "Login failed: " + ex.getMessage();
                    statusLabel.setText(errorMessage);
                    statusLabel.setForeground(new Color(255, 100, 100));

                    // Show error dialog
                    JOptionPane.showMessageDialog(
                        LoginPanel.this,
                        errorMessage,
                        "Authentication Error",
                        JOptionPane.ERROR_MESSAGE
                    );

                    // Notify callback
                    if (callback != null) {
                        callback.onLoginFailure(errorMessage);
                    }

                    // Reset status label color after a delay
                    Timer timer = new Timer(3000, evt -> {
                        statusLabel.setForeground(Color.WHITE);
                        statusLabel.setText(" ");
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        };

        worker.execute();
    }

    /**
     * Cleans up resources.
     */
    public void cleanup() {
        if (authService != null) {
            authService.close();
        }
    }
}