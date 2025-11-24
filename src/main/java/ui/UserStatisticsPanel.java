package ui;

import java.util.*;

import entity.User;
import entity.Game;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;


// TODO : Java throws error when trying to use org.free.chart.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.HistogramDataset;

import java.awt.*;
import java.util.List;

public class UserStatisticsPanel extends JPanel {
    private final User user;

    public UserStatisticsPanel(User user) {
        this.user = user;
        initUI();
    }

    private void initUI() {
        setBackground(new Color(42, 42, 42));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel nameLabel = new JLabel(user.getUsername());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 20f));

        JLabel idLabel = new JLabel("Steam ID: " + user.getId());
        idLabel.setForeground(Color.LIGHT_GRAY);
        idLabel.setFont(idLabel.getFont().deriveFont(Font.PLAIN, 12f));

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(nameLabel);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(idLabel);

        add(headerPanel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(5, 1, 12, 12));
        statsPanel.setOpaque(false);

        int totalPlaytime = 0;
        for (Game game : user.getLibrary()){
            totalPlaytime += game.getPlaytime();
        }
        totalPlaytime = totalPlaytime / 60;

        statsPanel.add(createMostPlayedPanel(user.getLibrary(), totalPlaytime));

        add(statsPanel, BorderLayout.CENTER);

        JLabel footerLabel = new JLabel("Detailed stats coming soon…");
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setFont(footerLabel.getFont().deriveFont(Font.ITALIC, 11f));

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.add(footerLabel, BorderLayout.WEST);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createMostPlayedPanel(List<Game> games, int totalPlaytime) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel title = new JLabel("MOST PLAYED", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        if (games == null || games.isEmpty()) {
            JLabel none = new JLabel("No Data");
            none.setAlignmentX(Component.CENTER_ALIGNMENT);
            none.setForeground(Color.GRAY);
            panel.add(none);
            return panel;
        }

        Game mostPlayed = games.stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparingLong(Game::getPlaytime))
                .orElse(null);

        if (mostPlayed == null) {
            JLabel none = new JLabel("No Data");
            none.setAlignmentX(Component.CENTER_ALIGNMENT);
            none.setForeground(Color.GRAY);
            panel.add(none);
            return panel;
        }

        JLabel gameName = new JLabel(mostPlayed.getTitle(), SwingConstants.CENTER);
        gameName.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameName.setForeground(Color.LIGHT_GRAY);
        gameName.setFont(gameName.getFont().deriveFont(Font.BOLD, 18f));
        panel.add(gameName);
        panel.add(Box.createVerticalStrut(6));

        int hours = mostPlayed.getPlaytime() / 60;

        JLabel playtime = new JLabel(hours + "hrs", SwingConstants.CENTER);
        playtime.setAlignmentX(Component.CENTER_ALIGNMENT);
        playtime.setForeground(Color.GRAY);
        playtime.setFont(playtime.getFont().deriveFont(Font.PLAIN, 13f));
        panel.add(playtime);
        panel.add(Box.createVerticalStrut(10));

        panel.add(wrapChart(createChartPanel(hours, totalPlaytime, mostPlayed.getTitle())));

        String message;
        if (hours < 3) {
            message = "Still enough time for a refund.";
        } else if (hours < 100) {
            message = "I can see it's a favourite.";
        } else if (hours < 200) {
            message = "Getting really into this game, huh?";
        } else {
            message = "You must really love this game...";
        }

        JLabel footer = new JLabel(message, SwingConstants.CENTER);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.setForeground(Color.GRAY);
        footer.setFont(footer.getFont().deriveFont(Font.ITALIC, 11f));
        panel.add(footer);

        return panel;
    }

    private JPanel wrapChart(ChartPanel chartPanel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(chartPanel, BorderLayout.CENTER);
        wrapper.setPreferredSize(new Dimension(200, 200)); // <-- height control
        return wrapper;
    }

    private ChartPanel createChartPanel(int small, int big, String label) {

        // Build dataset
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue(label, small);   // Only this slice is labeled
        dataset.setValue("", big);        // Unlabeled slice

        // Create chart
        JFreeChart chart = ChartFactory.createPieChart(
                "Small vs Big",
                dataset,
                false,     // no legend since one label is blank
                true,
                false
        );

        // Backgrounds
        chart.setBackgroundPaint(java.awt.Color.GRAY);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(java.awt.Color.GRAY);

        // Remove outline for cleaner look
        plot.setOutlineVisible(false);

        // ChartPanel (auto-resize settings)
        ChartPanel panel = new ChartPanel(chart);

        panel.setPreferredSize(null);
        panel.setMaximumDrawWidth(Integer.MAX_VALUE);
        panel.setMaximumDrawHeight(Integer.MAX_VALUE);
        panel.setMinimumDrawWidth(0);
        panel.setMinimumDrawHeight(0);
        panel.setMouseWheelEnabled(true);

        return panel;
    }

}
