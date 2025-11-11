package app;

import auth.SessionManager;
import data_access.UserDataAcessObject;
import entity.User;
import ui.LoginPanel;
import ui.UserProfilePanel;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window for Steam Wrapped.
 * This is the entry point for the Swing-based application.
 */
public class SteamWrappedApp extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private LoginPanel loginPanel;
    private UserProfilePanel profilePanel;
    private UserDataAcessObject userDAO;

    // Card names for navigation
    private static final String LOGIN_CARD = "LOGIN";
    private static final String PROFILE_CARD = "PROFILE";

    /**
     * Creates the main application window.
     */
    public SteamWrappedApp() {
        super("Steam Wrapped");
        initializeComponents();
        setupWindow();
    }

    /**
     * Initializes all UI components.
     */
    private void initializeComponents() {
        userDAO = new UserDataAcessObject();

        // Create main panel with card layout for switching between views
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(42, 42, 42));

        // Create login panel
        loginPanel = new LoginPanel(new LoginPanel.LoginCallback() {
            @Override
            public void onLoginSuccess(User user) {
                showProfile(user);
            }

            @Override
            public void onLoginFailure(String error) {
                // Error is already handled by LoginPanel
            }
        });

        // Add panels to card layout
        mainPanel.add(loginPanel, LOGIN_CARD);

        // Check if there's an existing session
        SessionManager session = SessionManager.getInstance();
        if (session.hasActiveSession()) {
            session.getCurrentUser().ifPresent(this::showProfile);
        } else {
            cardLayout.show(mainPanel, LOGIN_CARD);
        }
    }

    /**
     * Sets up the window properties.
     */
    private void setupWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        setSize(600, 500);
        setMinimumSize(new Dimension(500, 400));
        setLocationRelativeTo(null); // Center on screen

        // Set application icon (optional - you can add a Steam icon here)
        try {
            // You could add an icon here if you have one
            // setIconImage(ImageIO.read(getClass().getResource("/icons/steam.png")));
        } catch (Exception e) {
            // Icon not found, use default
        }

        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }

        // Add window listener for cleanup
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cleanup();
            }
        });
    }

    /**
     * Shows the user profile after successful login.
     *
     * @param user The logged-in user
     */
    private void showProfile(User user) {
        // Remove old profile panel if it exists
        if (profilePanel != null) {
            mainPanel.remove(profilePanel);
        }

        // Create new profile panel
        profilePanel = new UserProfilePanel(user, new UserProfilePanel.ProfileCallback() {
            @Override
            public void onLogout() {
                showLogin();
            }

            @Override
            public void onRefresh() {
                refreshProfile();
            }
        });

        // Add to card layout and show
        mainPanel.add(profilePanel, PROFILE_CARD);
        cardLayout.show(mainPanel, PROFILE_CARD);

        // Update window title
        setTitle("Steam Wrapped - " + user.getUsername());
    }

    /**
     * Shows the login panel.
     */
    private void showLogin() {
        cardLayout.show(mainPanel, LOGIN_CARD);
        setTitle("Steam Wrapped");
    }

    /**
     * Refreshes the current user's profile data.
     */
    private void refreshProfile() {
        SessionManager session = SessionManager.getInstance();
        session.getSteamId().ifPresent(steamId -> {
            // Show loading dialog
            JDialog loadingDialog = createLoadingDialog();
            loadingDialog.setVisible(true);

            // Refresh data in background
            SwingWorker<User, Void> worker = new SwingWorker<>() {
                @Override
                protected User doInBackground() throws Exception {
                    return userDAO.get(steamId);
                }

                @Override
                protected void done() {
                    loadingDialog.dispose();
                    try {
                        User user = get();
                        session.updateCurrentUser(user);
                        showProfile(user);
                        JOptionPane.showMessageDialog(
                            SteamWrappedApp.this,
                            "Profile data refreshed successfully!",
                            "Refresh Complete",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                            SteamWrappedApp.this,
                            "Failed to refresh profile: " + ex.getMessage(),
                            "Refresh Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            };

            worker.execute();
        });
    }

    /**
     * Creates a loading dialog.
     *
     * @return The loading dialog
     */
    private JDialog createLoadingDialog() {
        JDialog dialog = new JDialog(this, "Loading", true);
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Refreshing data from Steam...");
        label.setFont(new Font("Arial", Font.PLAIN, 14));

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        panel.add(label, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);

        dialog.setContentPane(panel);
        dialog.setSize(300, 100);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        return dialog;
    }

    /**
     * Cleans up resources before closing.
     */
    private void cleanup() {
        if (loginPanel != null) {
            loginPanel.cleanup();
        }
        SessionManager.getInstance().clearSession();
    }

    /**
     * Main method - application entry point.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Run on EDT
        SwingUtilities.invokeLater(() -> {
            // Check if API key is set (optional warning)
            if (System.getenv("APIKEY") == null || System.getenv("APIKEY").isEmpty()) {
                int result = JOptionPane.showConfirmDialog(
                    null,
                    "Steam API key (APIKEY environment variable) is not set.\n" +
                    "The application will run in demo mode with sample data.\n\n" +
                    "To use real Steam data:\n" +
                    "1. Get an API key from https://steamcommunity.com/dev/apikey\n" +
                    "2. Set the APIKEY environment variable\n\n" +
                    "Continue in demo mode?",
                    "API Key Not Found",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if (result != JOptionPane.YES_OPTION) {
                    System.exit(0);
                    return;
                }
            }

            // Create and show the application
            SteamWrappedApp app = new SteamWrappedApp();
            app.setVisible(true);
        });
    }
}