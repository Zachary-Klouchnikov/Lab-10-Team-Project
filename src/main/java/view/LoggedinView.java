package view;

import entity.User;
import entity.Game;
import interface_adapter.loggedin.LoggedinState;
import interface_adapter.logout.LogoutController;
import interface_adapter.loggedin.LoggedinViewModel;
import interface_adapter.loggedin.RefreshController;

import javax.swing.*;

import data_access.ImageDataAccessObject;

import java.util.List;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class LoggedinView extends JPanel implements ActionListener, PropertyChangeListener{
    private final String viewName = "loggedin";
    private User user;
    private LoggedinViewModel loggedinViewModel;   

    private JLabel profilePicture;
    private JLabel usernameLabel;
    private JLabel steamIdLabel;
    private JLabel friendCountLabel;
    private JLabel gameCountLabel;
    private JList<JLabel> gameList;
    private JList<JLabel> friendList;
    private JButton logoutButton;
    private JButton refreshButton;

    private JButton launchButton;
    private JButton compareButton;
    private JButton reviewButton;

    private LogoutController logoutController = null;
    private RefreshController refreshController = null;

    public LoggedinView(LoggedinViewModel loggedinViewModel) {
        this.loggedinViewModel = loggedinViewModel;
        this.loggedinViewModel.addPropertyChangeListener(this);

        initializeUI();
    }

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
        JPanel panel = new JPanel();
        panel.setBackground(new Color(42, 42, 42));

        JPanel leftPanel = new JPanel();
        profilePicture = ImageDataAccessObject.getDefaultImage();
        leftPanel.add(profilePicture);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(42, 42, 42));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel titleLabel = new JLabel("Hello, ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        rightPanel.add(titleLabel, gbc);

        // Username
        usernameLabel = new JLabel();
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        usernameLabel.setForeground(new Color(0, 200, 83)); // Steam green
        gbc.gridy = 1;
        gbc.gridx = 1;
        rightPanel.add(usernameLabel, gbc);

        // Steam ID
        steamIdLabel = new JLabel();
        steamIdLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        steamIdLabel.setForeground(new Color(150, 150, 150));
        gbc.gridy = 2;
        gbc.gridx = 1;
        rightPanel.add(steamIdLabel, gbc);

        panel.add(leftPanel);
        panel.add(rightPanel);
        return panel;
    }

    /**
     * Creates the content panel with stats and game list.
     *
     * @return The content panel
     */
    private JPanel createContentPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;


        JPanel gamePanel = new JPanel(new GridBagLayout());
        gamePanel.setBackground(new Color(42, 42, 42));

        // Games section
        JLabel gamesLabel = new JLabel("Your Games:");
        gamesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gamesLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gamePanel.add(gamesLabel, gbc);

        // Game list
        gameList = new JList<>();
        gameList.setBackground(new Color(30, 30, 30));
        gameList.setForeground(Color.WHITE);
        gameList.setFont(new Font("Arial", Font.PLAIN, 12));
        gameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gameList.setCellRenderer(new ListCellRenderer<JLabel>() {
            private final JLabel label = new JLabel();
            @Override
            public Component getListCellRendererComponent(
                JList<? extends JLabel> list,
                JLabel value,
                int index, 
                boolean isSelected, 
                boolean cellHasFocus) {
                    label.setText(value.getText());
                    label.setIcon(value.getIcon());

                    label.setOpaque(true);
                    if(isSelected) {
                        label.setBackground(list.getSelectionBackground());
                        label.setForeground(list.getSelectionForeground());
                    } else {
                        label.setBackground(list.getBackground());
                        label.setForeground(list.getForeground());
                    }

                    return label;
            }
        });


        JScrollPane gameScrollPane = new JScrollPane(gameList);
        gameScrollPane.setPreferredSize(new Dimension(400, 200));
        gameScrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gamePanel.add(gameScrollPane, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0f;
        gbc.weighty = 0f;

        // Game count
        gameCountLabel = new JLabel();
        gameCountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gameCountLabel.setForeground(Color.WHITE);
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gamePanel.add(gameCountLabel, gbc);

        // PlayButton.
        launchButton = createButton("Launch", new Color(0, 200, 83));
        launchButton.addActionListener(e -> {
            System.err.println("TODO: Implement Launch View!");
        });
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gamePanel.add(launchButton, gbc);
        gbc.anchor = GridBagConstraints.WEST;

        JPanel friendPanel = new JPanel(new GridBagLayout());
        friendPanel.setBackground(new Color(42, 42, 42));

        // friend section
        JLabel friendLabel = new JLabel("Your Friends: ");
        friendLabel.setFont(new Font("Arial", Font.BOLD, 14));
        friendLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        friendPanel.add(friendLabel, gbc);

        // friend list
        friendList = new JList<>();
        friendList.setBackground(new Color(30, 30, 30));
        friendList.setForeground(Color.WHITE);
        friendList.setFont(new Font("Arial", Font.PLAIN, 12));
        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendList.setCellRenderer(new ListCellRenderer<JLabel>() {
            private final JLabel label = new JLabel();
            @Override
            public Component getListCellRendererComponent(
                JList<? extends JLabel> list, 
                JLabel value, 
                int index, 
                boolean isSelected, 
                boolean cellHasFocus) {
                    label.setText(value.getText());
                    label.setIcon(value.getIcon());

                    label.setOpaque(true);
                    if(isSelected) {
                        label.setBackground(list.getSelectionBackground());
                        label.setForeground(list.getSelectionForeground());
                    } else {
                        label.setBackground(list.getBackground());
                        label.setForeground(list.getForeground());
                    }

                    return label;
            }
        });


        JScrollPane friendScollPane = new JScrollPane(friendList);
        friendScollPane.setPreferredSize(new Dimension(400, 200));
        friendScollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        friendPanel.add(friendScollPane, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0f;
        gbc.weighty = 0f;

        // Friend count
        friendCountLabel = new JLabel();
        friendCountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        friendCountLabel.setForeground(Color.WHITE);
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        friendPanel.add(friendCountLabel, gbc);

        // Compare Button.
        compareButton = createButton("Compare", new Color(0, 200, 83));
        compareButton.addActionListener(e -> {
            System.err.println("TODO: Implement Compare View!");
        });
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        friendPanel.add(compareButton, gbc);
        gbc.anchor = GridBagConstraints.WEST;


        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(42, 42, 42));
        gbc.gridy = 0; 
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(gamePanel, gbc);
        panel.add(friendPanel, gbc);
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

        reviewButton = createButton("Reviews", new Color(0, 200, 83));
        reviewButton.addActionListener(e -> {
            System.err.println("TODO: Implement Review View!");
        }); 
        panel.add(reviewButton);

        // Refresh button
        refreshButton = createButton("Refresh Data", new Color(0, 114, 188));
        refreshButton.addActionListener(e -> {
            refreshController.execute(this.user);
        });
        panel.add(refreshButton);

        // Logout button
        logoutButton = createButton("Logout", new Color(188, 0, 0));
        logoutButton.addActionListener(e -> {
            logoutController.execute();
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
            
            if (friends != null && !friends.isEmpty()) {
                JLabel[] friendLabels = new JLabel[friends.size()];
                for (int i = 0; i < friends.size(); ++i) {
                    User f = friends.get(i);
                    JLabel label = new JLabel();
                    label.setIcon(f.getImage());
                    label.setText(f.getUsername());
                    friendLabels[i] = label;
                }
                friendList.setListData(friendLabels);
            } else {
                friendList.setListData(new JLabel[] { new JLabel("No friends found") });
            }

            // Update game count and list
            List<Game> games = user.getLibrary();
            gameCountLabel.setText("Games: " + (games != null ? games.size() : 0));

            if (games != null && !games.isEmpty()) {
                JLabel[] gameNames = new JLabel[games.size()];
                for (int i = 0; i < games.size(); ++i) {
                    Game g = games.get(i);
                    JLabel label = new JLabel();
                    label.setIcon(g.getImage());
                    label.setText(g.getTitle());
                    gameNames[i] = label;
                }
                gameList.setListData(gameNames);
            } else {
                gameList.setListData(new JLabel[] { new JLabel("No games found") });
            }

            profilePicture.setIcon(user.getImage());
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

    @Override
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Clicked: " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final LoggedinState state = (LoggedinState) evt.getNewValue();
        if (state.getUser() == null || !state.getError().isEmpty()) {
            JOptionPane.showMessageDialog(
                LoggedinView.this,
                state.getError(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        setUser(state.getUser());
    }

    public String getViewName() {
        return this.viewName;
    }

    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
    }

    public void setRefreshController(RefreshController refreshController) {
        this.refreshController = refreshController;
    }
}
