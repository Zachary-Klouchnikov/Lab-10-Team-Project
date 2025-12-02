package view;

import entity.User;

import javax.swing.*;
import java.awt.*;

public class ComparisonPanel extends JPanel {

    private JPanel rightContainer;
    private JComboBox<User> friendCombo;
    private CardLayout cardLayout;
    private JPanel statsCard;
    private JButton backButton;

    public ComparisonPanel(User user) {

        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(42, 42, 42));

        JLabel infoLabel = new JLabel("");
        infoLabel.setForeground(Color.WHITE);
        topBar.add(infoLabel, BorderLayout.WEST);

        backButton = new JButton("Back");
        backButton.setEnabled(false);
        topBar.add(backButton, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        JPanel leftPanel = UserStatisticsPanel.createForUser(user);

        cardLayout = new CardLayout();
        rightContainer = new JPanel(cardLayout);
        rightContainer.setBackground(new Color(42, 42, 42));

        JPanel selectorPanel = new JPanel(new GridBagLayout());
        selectorPanel.setOpaque(false);

        GridBagConstraints sGbc = new GridBagConstraints();
        sGbc.gridx = 0;
        sGbc.gridy = 0;
        sGbc.insets = new Insets(10, 10, 10, 10);
        sGbc.anchor = GridBagConstraints.CENTER;

        JLabel selectLabel = new JLabel("Select a friend:");
        selectLabel.setForeground(Color.WHITE);
        selectorPanel.add(selectLabel, sGbc);

        friendCombo = new JComboBox<>(user.getFriends().toArray(new User[0]));
        friendCombo.addActionListener(e -> loadFriendPanel());

        friendCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof User u) {
                    setText(u.getUsername());
                }
                return this;
            }
        });

        sGbc.gridy = 1;
        selectorPanel.add(friendCombo, sGbc);

        rightContainer.add(selectorPanel, "selector");

        statsCard = new JPanel(new BorderLayout());
        statsCard.setOpaque(false);
        rightContainer.add(statsCard, "stats");

        JPanel combined = new JPanel(new GridLayout(1, 2, 0, 0));
        combined.setBackground(new Color(42, 42, 42));

        combined.add(leftPanel);
        combined.add(rightContainer);

        JScrollPane scrollPane = new JScrollPane(combined);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        add(scrollPane, BorderLayout.CENTER);

        backButton.addActionListener(e -> {
            cardLayout.show(rightContainer, "selector");
            backButton.setEnabled(false);
        });
    }

    private void loadFriendPanel() {
        User selected = (User) friendCombo.getSelectedItem();
        if (selected == null) return;

        statsCard.removeAll();
        statsCard.add(UserStatisticsPanel.createForUser(selected), BorderLayout.CENTER);

        statsCard.revalidate();
        statsCard.repaint();

        cardLayout.show(rightContainer, "stats");
        backButton.setEnabled(true);
    }
}
