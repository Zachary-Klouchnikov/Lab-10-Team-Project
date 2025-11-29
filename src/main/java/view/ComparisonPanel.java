package view;

import entity.User;

import javax.swing.*;
import java.awt.*;

public class ComparisonPanel extends JPanel {

    private final User user;

    private final JPanel leftPanel;

    private JPanel rightContainer;
    private JComboBox<User> friendCombo;

    public ComparisonPanel(User user) {
        this.user = user;

        setLayout(new BorderLayout());

        leftPanel = new UserStatisticsPanel(this.user);
        JScrollPane leftScroll = createScrollPane(leftPanel);

        rightContainer = new JPanel(new GridBagLayout());
        rightContainer.setBackground(new Color(42, 42, 42));

        friendCombo = new JComboBox<>(user.getFriends().toArray(new User[0]));
        friendCombo.setPreferredSize(new Dimension(200, 30));
        friendCombo.addActionListener(e -> loadFriendPanel());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        rightContainer.add(friendCombo, gbc);

        JScrollPane rightScroll = createScrollPane(rightContainer);

        syncScrollBars(leftScroll, rightScroll);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftScroll,
                rightScroll
        );
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(5);
        splitPane.setContinuousLayout(true);

        add(splitPane, BorderLayout.CENTER);
    }

    private JScrollPane createScrollPane(JPanel panel) {
        JScrollPane sp = new JScrollPane(panel);

        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(20);

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        sp.setBorder(null);
        sp.getViewport().setBackground(new Color(42, 42, 42));

        sp.setPreferredSize(null);
        sp.setMinimumSize(new Dimension(0, 0));

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        return sp;
    }

    private void syncScrollBars(JScrollPane sp1, JScrollPane sp2) {
        BoundedRangeModel model = sp1.getVerticalScrollBar().getModel();
        sp2.getVerticalScrollBar().setModel(model);
    }

    private void loadFriendPanel() {
        User selected = (User) friendCombo.getSelectedItem();
        if (selected == null) return;

        rightContainer.removeAll();
        rightContainer.setLayout(new BorderLayout());

        JPanel statsPanel = new UserStatisticsPanel(selected);
        rightContainer.add(statsPanel, BorderLayout.CENTER);

        // Refresh UI
        rightContainer.revalidate();
        rightContainer.repaint();
    }
}
