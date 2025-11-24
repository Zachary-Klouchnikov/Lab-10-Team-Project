package ui;

import entity.User;

import javax.swing.*;
import java.awt.*;

public class ComparisonPanel extends JPanel {

    private final User user;

    private final JPanel leftPanel;
    private final JPanel rightPanel;

    public ComparisonPanel(User user) {

        this.user = user;

        setLayout(new BorderLayout());

        leftPanel = new UserStatisticsPanel(this.user);
        rightPanel = new UserStatisticsPanel(this.user);

        JScrollPane leftScroll = createScrollPane(leftPanel);
        JScrollPane rightScroll = createScrollPane(rightPanel);

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
}
