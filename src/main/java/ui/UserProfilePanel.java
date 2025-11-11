package ui;

import auth.SessionManager;
import entity.User;
import entity.Game;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel that displays the authenticated user's profile information.
 */
public class UserProfilePanel extends JPanel {
    private User user;
    private JLabel usernameLabel;
    private JLabel steamIdLabel;
    private JLabel friendCountLabel;
    private JLabel gameCountLabel;
    private JList<String> gameList;
    private JButton logoutButton;
    private JButton refreshButton;

    /**
     * Interface for profile panel callbacks.
     */
    public interface ProfileCallback {
        void onLogout();
        void onRefresh();
    }

    private ProfileCallback callback;

    /**
     * Creates a new UserProfilePanel.
     *
     * @param user The user to display
     * @param callback The callback for button actions
     */
    public UserProfilePanel(User user, ProfileCallback callback) {
        this.user = user;
        this.callback = callback;
        initializeUI();
        updateUserInfo();
    }

    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(42, 42, 42));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);

        // Footer panel with buttons
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the header panel with user info.
     *
     * @return The header panel
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(42, 42, 42));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel titleLabel = new JLabel("Steam Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Username
        usernameLabel = new JLabel();
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        usernameLabel.setForeground(new Color(0, 200, 83)); // Steam green
        gbc.gridy = 1;
        panel.add(usernameLabel, gbc);

        // Steam ID
        steamIdLabel = new JLabel();
        steamIdLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        steamIdLabel.setForeground(new Color(150, 150, 150));
        gbc.gridy = 2;
        panel.add(steamIdLabel, gbc);

        return panel;
    }

    /**
     * Creates the content panel with stats and game list.
     *
     * @return The content panel
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(42, 42, 42));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Stats section
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        statsPanel.setBackground(new Color(42, 42, 42));

        // Friend count
        friendCountLabel = new JLabel();
        friendCountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        friendCountLabel.setForeground(Color.WHITE);
        statsPanel.add(friendCountLabel);

        // Game count
        gameCountLabel = new JLabel();
        gameCountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gameCountLabel.setForeground(Color.WHITE);
        statsPanel.add(gameCountLabel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(statsPanel, gbc);

        // Games section
        JLabel gamesLabel = new JLabel("Your Games:");
        gamesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gamesLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(gamesLabel, gbc);

        // Game list
        gameList = new JList<>();
        gameList.setBackground(new Color(30, 30, 30));
        gameList.setForeground(Color.WHITE);
        gameList.setFont(new Font("Arial", Font.PLAIN, 12));
        gameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(gameList);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(scrollPane, gbc);

        return panel;
    }

    /**
     * Creates the footer panel with action buttons.
     *
     * @return The footer panel
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(new Color(42, 42, 42));

        // Refresh button
        refreshButton = createButton("Refresh Data", new Color(0, 114, 188));
        refreshButton.addActionListener(e -> {
            if (callback != null) {
                callback.onRefresh();
            }
        });
        panel.add(refreshButton);

        // Logout button
        logoutButton = createButton("Logout", new Color(188, 0, 0));
        logoutButton.addActionListener(e -> {
            SessionManager.getInstance().clearSession();
            if (callback != null) {
                callback.onLogout();
            }
        });
        panel.add(logoutButton);

        return panel;
    }

    /**
     * Creates a styled button.
     *
     * @param text The button text
     * @param color The button color
     * @return The styled button
     */
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        Color hoverColor = color.brighter();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    /**
     * Updates the displayed user information.
     */
    private void updateUserInfo() {
        if (user != null) {
            usernameLabel.setText(user.getUsername());
            steamIdLabel.setText("Steam ID: " + user.getId());

            // Update friend count
            List<User> friends = user.getFriends();
            friendCountLabel.setText("Friends: " + (friends != null ? friends.size() : 0));

            // Update game count and list
            List<Game> games = user.getLibrary();
            gameCountLabel.setText("Games: " + (games != null ? games.size() : 0));

            if (games != null && !games.isEmpty()) {
                String[] gameNames = games.stream()
                        .map(Game::getTitle)
                        .toArray(String[]::new);
                gameList.setListData(gameNames);
            } else {
                gameList.setListData(new String[]{"No games found"});
            }
        }
    }

    /**
     * Updates the user data.
     *
     * @param user The updated user
     */
    public void setUser(User user) {
        this.user = user;
        updateUserInfo();
    }
}