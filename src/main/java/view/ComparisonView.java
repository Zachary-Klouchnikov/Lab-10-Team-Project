package view;

import entity.User;
import interface_adapter.compareusers.CompareUsersController;
import interface_adapter.compareusers.CompareUsersState;
import interface_adapter.compareusers.CompareUsersViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ComparisonView extends JPanel implements PropertyChangeListener {
    private final String viewName;
    private final CompareUsersViewModel viewModel;
    private CompareUsersController controller;

    private final JPanel leftContainer = new JPanel(new BorderLayout());
    private final JPanel rightContainer = new JPanel(new BorderLayout());
    private final JLabel headerLabel = new JLabel();
    private final JButton backButton = new JButton("Back");

    public ComparisonView(CompareUsersViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewName = viewModel.getViewName();
        this.viewModel.addPropertyChangeListener(this);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(42, 42, 42));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(42, 42, 42));

        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topBar.add(headerLabel, BorderLayout.WEST);

        backButton.addActionListener(e -> {
            CompareUsersState state = viewModel.getState();
            if (controller != null) {
                controller.backToLoggedIn(state.getLoggedInUser());
            }
        });
        topBar.add(backButton, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        JPanel comparisonGrid = new JPanel(new GridLayout(1, 2, 0, 0));
        comparisonGrid.setBackground(new Color(42, 42, 42));

        leftContainer.setOpaque(false);
        rightContainer.setOpaque(false);

        comparisonGrid.add(leftContainer);
        comparisonGrid.add(rightContainer);

        JScrollPane scrollPane = new JScrollPane(comparisonGrid);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void renderUserPanels(User loggedInUser, User comparisonUser) {
        leftContainer.removeAll();
        rightContainer.removeAll();

        if (loggedInUser != null) {
            leftContainer.add(UserStatisticsPanel.createForUser(loggedInUser), BorderLayout.CENTER);
        } else {
            leftContainer.add(new JLabel("No logged in user available"), BorderLayout.CENTER);
        }

        if (comparisonUser != null) {
            rightContainer.add(UserStatisticsPanel.createForUser(comparisonUser), BorderLayout.CENTER);
        } else {
            rightContainer.add(new JLabel("Select a user to compare"), BorderLayout.CENTER);
        }

        leftContainer.revalidate();
        leftContainer.repaint();
        rightContainer.revalidate();
        rightContainer.repaint();

        updateHeader(loggedInUser, comparisonUser);
    }

    private void updateHeader(User loggedInUser, User comparisonUser) {
        String leftName = loggedInUser != null ? loggedInUser.getUsername() : "Unknown";
        String rightName = comparisonUser != null ? comparisonUser.getUsername() : "Select user";
        headerLabel.setText("Comparing " + leftName + " vs " + rightName);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) {
            return;
        }
        CompareUsersState state = (CompareUsersState) evt.getNewValue();
        if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    state.getErrorMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        renderUserPanels(state.getLoggedInUser(), state.getComparisonUser());
    }

    public void setController(CompareUsersController controller) {
        this.controller = controller;
    }

    public String getViewName() {
        return viewName;
    }
}
