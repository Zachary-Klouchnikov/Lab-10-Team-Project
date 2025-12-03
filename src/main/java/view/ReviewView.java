package view;

import interface_adapter.review.ReviewController;
import interface_adapter.review.ReviewState;
import interface_adapter.review.ReviewViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class ReviewView extends JPanel implements PropertyChangeListener {
    private final String viewName;
    private final ReviewViewModel viewModel;
    private final ArrayList<String> lst;
    private ReviewController controller;

    private final JPanel Container = new JPanel(new BorderLayout());
    private final JLabel headerLabel = new JLabel();
    private final JButton backButton = new JButton("Back");

    public ReviewView(ReviewViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewName = viewModel.getViewName();
        this.lst = viewModel.getState().getLst();
        this.viewModel.addPropertyChangeListener(this);
    }

    private void action() {
        setLayout(new BorderLayout());
        setBackground(new Color(42, 42, 42));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(42, 42, 42));

        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topBar.add(headerLabel, BorderLayout.WEST);

        backButton.addActionListener(e -> {
            ReviewState state = viewModel.getState();
            if (controller != null) {
                controller.backToLoggedIn(state.getLoggedInUser());
            }
        });
        topBar.add(backButton, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        String[] data = lst.toArray(new String[0]);
        JList<String> jList = new JList<>(data);


        JScrollPane scrollPane = new JScrollPane(jList);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) {
            return;
        }
        ReviewState state = (ReviewState) evt.getNewValue();
        if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    state.getErrorMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        action();
    }

    public void setController(ReviewController controller) {
        this.controller = controller;
    }

    public String getViewName() {
        return viewName;
    }
}
