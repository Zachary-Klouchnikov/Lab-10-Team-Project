package ui;

import java.util.Random;

import entity.User;
import entity.Game;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;


// TODO : Java throws error when trying to use org.free.chart.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.HistogramDataset;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
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

        long totalPlaytimeMinutes = calculateTotalPlaytime(user.getLibrary());
        double totalPlaytimeHours = totalPlaytimeMinutes / 60.0;

        statsPanel.add(friendsStatCard(user.getFriends()));

        statsPanel.add(gamesStatCard(user.getLibrary()));

        statsPanel.add(createStatCard(
                "Total Playtime (hrs)",
                String.format("%.1f", totalPlaytimeHours)));

        statsPanel.add(gamesPieChart(user.getLibrary()));

        statsPanel.add(gameHistogramChart(user.getLibrary().get(0)));

        add(statsPanel, BorderLayout.CENTER);

        JLabel footerLabel = new JLabel("Detailed stats coming soon…");
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setFont(footerLabel.getFont().deriveFont(Font.ITALIC, 11f));

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.add(footerLabel, BorderLayout.WEST);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private long calculateTotalPlaytime(List<Game> games) {
        if (games == null) {
            return 0;
        }
        long sum = 0;
        for (Game g : games) {
            if (g != null) {
                sum += g.getPlaytime(); // int minutes
            }
        }
        return sum;
    }

    private JPanel createStatCard(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        Border padding = BorderFactory.createEmptyBorder(8, 8, 8, 8);
        Border outline = BorderFactory.createLineBorder(new Color(70, 70, 70));
        panel.setBorder(BorderFactory.createCompoundBorder(outline, padding));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.LIGHT_GRAY);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.PLAIN, 12f));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 18f));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel friendsStatCard(List<User> friends) {
        JPanel panel = new JPanel(new BorderLayout());

        Border padding = BorderFactory.createEmptyBorder(8, 8, 8, 8);
        Border outline = BorderFactory.createLineBorder(new Color(70, 70, 70));

        panel.setBackground(new Color(42, 42, 42));
        panel.setBorder(BorderFactory.createCompoundBorder(padding, outline));

        JLabel title = new JLabel("TOTAL FRIENDS : " + Integer.toString(friends.size()), SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(42, 42, 42));

        for (User friend : friends) {
            JLabel nameLabel = new JLabel(friend.getUsername());
            nameLabel.setForeground(Color.WHITE);
            content.add(nameLabel);

        }

        JScrollPane scrollPane = new JScrollPane(content);

        scrollPane.getVerticalScrollBar().setBackground(new Color(35, 35, 35));

        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                trackColor = new Color(35, 35, 35);
                thumbColor = new Color(70, 70, 70);
            }
        });

        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel gamesStatCard(List<Game> games) {
        List<Game> sorted = new ArrayList<>(games);
        sorted.sort(Comparator.comparingInt(Game::getPlaytime).reversed());

        JPanel panel = new JPanel(new BorderLayout());

        Border padding = BorderFactory.createEmptyBorder(8, 8, 8, 8);
        Border outline = BorderFactory.createLineBorder(new Color(70, 70, 70));

        panel.setBackground(new Color(42, 42, 42));
        panel.setBorder(BorderFactory.createCompoundBorder(padding, outline));

        JLabel title = new JLabel("TOTAL GAMES : " + Integer.toString(games.size()), SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(42, 42, 42));

        for (Game game : sorted) {
            JLabel nameLabel = new JLabel(game.getTitle() + " : " + (game.getPlaytime() / 60) + " hrs");
            nameLabel.setForeground(Color.WHITE);
            content.add(nameLabel);
        }

        JScrollPane scrollPane = new JScrollPane(content);

        scrollPane.getVerticalScrollBar().setBackground(new Color(35, 35, 35));

        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                trackColor = new Color(35, 35, 35);
                thumbColor = new Color(70, 70, 70);
            }
        });

        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel gamesPieChart(List<Game> games) {
        List<Game> sorted = new ArrayList<>(games);
        sorted.sort(Comparator.comparingInt(Game::getPlaytime).reversed());

        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        int limit = Math.min(sorted.size(), 5);
        int otherTotal = 0;

        for (int i = 0; i < sorted.size(); i++) {
            Game g = sorted.get(i);
            int playtime = g.getPlaytime();

            if (i < limit) {
                dataset.setValue(g.getTitle(), playtime);
            } else {
                otherTotal += playtime;
            }
        }

        if (otherTotal > 0) {
            dataset.setValue("Other", otherTotal);
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Top 5 Games",
                dataset,
                true,
                true,
                false 
        );

        chart.setBackgroundPaint(new Color(42, 42, 42));
        chart.getPlot().setBackgroundPaint(new Color(42, 42, 42));

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setOutlineVisible(false);
        plot.setLabelBackgroundPaint(new Color(60, 60, 60));
        plot.setLabelPaint(Color.WHITE);
        plot.setLabelOutlinePaint(null);
        plot.setLabelShadowPaint(null);

        chart.getLegend().setBackgroundPaint(new Color(42, 42, 42));
        chart.getLegend().setItemPaint(Color.WHITE);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(null);
        chartPanel.setMaximumSize(null);
        chartPanel.setMinimumSize(new Dimension(0, 0));
        chartPanel.setLayout(new BorderLayout());
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setBackground(new Color(42, 42, 42));

        return chartPanel;
    }

    private JPanel gameHistogramChart(Game game) {
        // TODO: this returns a FAKE chart, need to consult to see feasabiliity

        int sampleSize = 1000;
        double mean = 50.0;
        double stdDev = 15.0;

        double[] values = new double[sampleSize];
        Random rng = new Random(42);

        for (int i = 0; i < sampleSize; i++) {
            values[i] = mean + stdDev * rng.nextGaussian();
        }

        HistogramDataset dataset = new HistogramDataset();
        int bins = 20;
        dataset.addSeries("Players", values, bins);

        String title = "Global Playtime (synthetic)";
        if (game != null) {
            title = "Playtime Distribution (synthetic) - " + game.getTitle();
        }

        JFreeChart chart = ChartFactory.createHistogram(
                title,
                "Hours played",
                "Number of players",
                dataset);

        chart.setBackgroundPaint(new Color(18, 18, 18));
        if (chart.getTitle() != null) {
            chart.getTitle().setPaint(Color.WHITE);
        }

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(new Color(24, 24, 24));
        plot.setDomainGridlinePaint(new Color(60, 60, 60));
        plot.setRangeGridlinePaint(new Color(60, 60, 60));

        plot.getDomainAxis().setLabelPaint(Color.LIGHT_GRAY);
        plot.getDomainAxis().setTickLabelPaint(Color.LIGHT_GRAY);
        plot.getRangeAxis().setLabelPaint(Color.LIGHT_GRAY);
        plot.getRangeAxis().setTickLabelPaint(Color.LIGHT_GRAY);

        if (plot.getRenderer() instanceof org.jfree.chart.renderer.xy.XYBarRenderer) {
            org.jfree.chart.renderer.xy.XYBarRenderer renderer = (org.jfree.chart.renderer.xy.XYBarRenderer) plot
                    .getRenderer();
            renderer.setSeriesPaint(0, new Color(80, 180, 200));
            renderer.setBarPainter(new org.jfree.chart.renderer.xy.StandardXYBarPainter());
            renderer.setDrawBarOutline(false);
        }

        double userHours = mean + 0.67 * stdDev;

        ValueMarker marker = new ValueMarker(userHours);
        marker.setPaint(new Color(255, 99, 71));
        marker.setStroke(new BasicStroke(2.0f));
        marker.setLabel("You");
        marker.setLabelPaint(Color.WHITE);
        marker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        marker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);

        plot.addDomainMarker(marker);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        chartPanel.setBackground(new Color(18, 18, 18));
        chartPanel.setForeground(Color.WHITE);

        return chartPanel;
    }
}
