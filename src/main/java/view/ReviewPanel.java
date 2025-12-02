package view;

import data_access.AuthDataAccessObject;
import data_access.UserDataAccessObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ReviewPanel extends JPanel {
    public ReviewPanel(ArrayList<String> reviews) {
    setLayout(new BorderLayout());

    JList<String> reviewList = new JList<>(reviews.toArray(new String[0]));
    reviewList.setCellRenderer(new ReviewCellRenderer());
    reviewList.setFixedCellHeight(-1);  // allow variable heights
    reviewList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(reviewList);
    add(scrollPane, BorderLayout.CENTER);
}

    // Custom renderer for multiline reviews
    private static class ReviewCellRenderer extends JTextArea implements ListCellRenderer<String> {

        public ReviewCellRenderer() {
            setWrapStyleWord(true);
            setLineWrap(true);
            setOpaque(true);
            setFont(new Font("Arial", Font.PLAIN, 14));
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends String> list,
                String value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            setText(value);

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }


            setSize(list.getWidth(), Short.MAX_VALUE);

            return this;
        }
    }
}
