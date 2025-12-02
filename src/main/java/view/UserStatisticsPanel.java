package view;

import java.util.*;

import entity.User;
import entity.Game;

import javax.swing.*;


// TODO : Java throws error when trying to use org.free.chart.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import java.awt.*;

import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotOrientation;

import org.jfree.chart.renderer.xy.XYShapeRenderer;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.geom.Ellipse2D;

import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;


import java.util.List;

public class UserStatisticsPanel extends JPanel {
    private final User user;
    private final Color bgColor;
    private final Color fgColor;
    private final Color textColor;

    public UserStatisticsPanel(User user) {
        this.bgColor = new Color(42, 42, 42);
        this.fgColor = Color.LIGHT_GRAY;
        this.textColor = Color.LIGHT_GRAY;

        this.user = user;
        initUI();
    }

    private void initUI() {

        setBackground(bgColor);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel nameLabel = new JLabel(user.getUsername());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 20f));

        JLabel idLabel = new JLabel("Steam ID: " + user.getId());
        idLabel.setForeground(Color.LIGHT_GRAY);
        idLabel.setFont(idLabel.getFont().deriveFont(Font.PLAIN, 12f));

        JLabel imgLabel = user.getImage();

        ImageIcon icon = (ImageIcon) imgLabel.getIcon();
        int targetHeight = nameLabel.getPreferredSize().height + idLabel.getPreferredSize().height + 4;
        int targetWidth = icon.getIconWidth() * targetHeight / icon.getIconHeight();
        Image scaled = icon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        imgLabel.setIcon(new ImageIcon(scaled));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(idLabel);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(textPanel, BorderLayout.WEST);
        headerPanel.add(imgLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel();
        statsPanel.setOpaque(false);
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));

        int totalPlaytime = 0;
        for (Game game : user.getLibrary()){
            totalPlaytime += game.getPlaytime();
        }
        totalPlaytime = totalPlaytime / 60;

        statsPanel.add(createTotalPlaytimePanel(totalPlaytime));

        statsPanel.add(createMostPlayedPanel(user.getLibrary(), totalPlaytime));

        statsPanel.add(createTopFiveGamesPanel(user.getLibrary()));

        statsPanel.add(createPlaytimeDistributionPanel(user.getLibrary()));

        statsPanel.add(createRecentlyPlayedPanel(user.getLibrary()));

        statsPanel.add(createTopFiveRecentGamesPanel(user.getLibrary()));

        statsPanel.add(createScatterPlaytimeVsRecentPanel(user.getLibrary()));

        statsPanel.add(createOldFavoritesAndUnplayedGamesPanel(user.getLibrary()));

        add(statsPanel, BorderLayout.CENTER);

        JLabel footerLabel = new JLabel("Detailed stats coming soon…");
        footerLabel.setForeground(bgColor);
        footerLabel.setFont(footerLabel.getFont().deriveFont(Font.ITALIC, 11f));

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.add(footerLabel, BorderLayout.WEST);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createTotalPlaytimePanel(int totalPlaytime) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel title = new JLabel("TOTAL PLAYTIME", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        if (totalPlaytime == 0) {
            JLabel none = new JLabel("No Data / No playtime");
            none.setAlignmentX(Component.CENTER_ALIGNMENT);
            none.setForeground(Color.LIGHT_GRAY);
            panel.add(none);
            return panel;
        }

        JLabel hoursLabel = new JLabel(totalPlaytime + " hrs", SwingConstants.CENTER);
        hoursLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hoursLabel.setForeground(Color.LIGHT_GRAY);
        hoursLabel.setFont(hoursLabel.getFont().deriveFont(Font.BOLD, 28f));

        Dimension fixedSize = new Dimension(Integer.MAX_VALUE, hoursLabel.getPreferredSize().height);
        hoursLabel.setMaximumSize(fixedSize);
        hoursLabel.setMinimumSize(hoursLabel.getPreferredSize());
        hoursLabel.setPreferredSize(hoursLabel.getPreferredSize());

        panel.add(hoursLabel);

        panel.add(Box.createVerticalStrut(12));

        String message;
        if (totalPlaytime < 10) {
            message = "New account?";
        } else if (totalPlaytime < 100) {
            message = "Getting into gaming...";
        } else if (totalPlaytime < 500) {
            message = "Gaming as a hobby.";
        } else {
            message = "We have a true Gamer in our midst...";
        }

        JLabel footer = new JLabel(message, SwingConstants.CENTER);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.setForeground(textColor);
        footer.setFont(footer.getFont().deriveFont(Font.ITALIC, 11f));

        Dimension footerSize = new Dimension(Integer.MAX_VALUE, footer.getPreferredSize().height);
        footer.setMaximumSize(footerSize);

        panel.add(footer);

        return panel;
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
            none.setForeground(Color.LIGHT_GRAY);
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
            none.setForeground(bgColor);
            panel.add(none);
            return panel;
        }

        JPanel contentRow = new JPanel();
        contentRow.setOpaque(false);
        contentRow.setLayout(new BoxLayout(contentRow, BoxLayout.X_AXIS));

        JPanel leftContent = new JPanel();
        leftContent.setOpaque(false);
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));

        JLabel gameName = new JLabel(mostPlayed.getTitle(), SwingConstants.CENTER);
        gameName.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameName.setForeground(Color.LIGHT_GRAY);
        gameName.setFont(gameName.getFont().deriveFont(Font.BOLD, 18f));
        leftContent.add(gameName);
        leftContent.add(Box.createVerticalStrut(6));

        int hours = mostPlayed.getPlaytime() / 60;

        JLabel playtime = new JLabel(hours + " hrs", SwingConstants.CENTER);
        playtime.setAlignmentX(Component.CENTER_ALIGNMENT);
        playtime.setForeground(Color.WHITE);
        playtime.setFont(playtime.getFont().deriveFont(Font.PLAIN, 13f));
        leftContent.add(playtime);
        leftContent.add(Box.createVerticalStrut(10));

        contentRow.add(leftContent, BorderLayout.WEST);
        contentRow.add(mostPlayed.getImage(), BorderLayout.EAST);

        panel.add(contentRow);
        panel.add(wrapChart(createMostPlayedChart(hours, totalPlaytime - hours, mostPlayed.getTitle())));

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
        footer.setForeground(textColor);
        footer.setFont(footer.getFont().deriveFont(Font.ITALIC, 11f));
        panel.add(footer);

        return panel;
    }

    private JPanel wrapChart(ChartPanel chartPanel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(chartPanel, BorderLayout.CENTER);
        wrapper.setPreferredSize(new Dimension(200, 200));
        return wrapper;
    }

    private JPanel createTopFiveGamesPanel(List<Game> games) {

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel title = new JLabel("TOP 5 GAMES BY PLAYTIME", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        if (games == null || games.isEmpty()) {
            JLabel none = new JLabel("No Data");
            none.setAlignmentX(Component.CENTER_ALIGNMENT);
            none.setForeground(Color.LIGHT_GRAY);
            panel.add(none);
            return panel;
        }

        List<Game> top = games.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(Game::getPlaytime).reversed())
                .limit(5)
                .toList();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Game g : top) {
            int hours = g.getPlaytime() / 60;
            dataset.addValue(hours, "Playtime (hrs)", g.getTitle());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                null,
                "Game",
                "Hours",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        chart.setBackgroundPaint(bgColor);
        chart.getPlot().setBackgroundPaint(bgColor);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(Color.GRAY);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, new Color(80, 160, 220));

        plot.getDomainAxis().setTickLabelPaint(Color.LIGHT_GRAY);
        plot.getDomainAxis().setLabelPaint(Color.LIGHT_GRAY);
        plot.getRangeAxis().setTickLabelPaint(Color.LIGHT_GRAY);
        plot.getRangeAxis().setLabelPaint(Color.LIGHT_GRAY);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(null);
        chartPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
        chartPanel.setMaximumDrawHeight(Integer.MAX_VALUE);
        chartPanel.setMinimumDrawWidth(0);
        chartPanel.setMinimumDrawHeight(0);
        chartPanel.setMouseWheelEnabled(true);

        JPanel wrapped = wrapChart(chartPanel);
        wrapped.setPreferredSize(new Dimension(300, 240));

        panel.add(wrapped);
        panel.add(Box.createVerticalStrut(12));

        JLabel footer = new JLabel("Your most played games.", SwingConstants.CENTER);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.setForeground(Color.LIGHT_GRAY);
        footer.setFont(footer.getFont().deriveFont(Font.ITALIC, 11f));
        panel.add(footer);

        return panel;
    }

    private ChartPanel createMostPlayedChart(int small, int big, String label) {

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue(label, small);
        dataset.setValue("other games", big);

        JFreeChart chart = ChartFactory.createPieChart(
                label + " as a proportion of total playtime",
                dataset,
                false,
                true,
                false
        );

        chart.getTitle().setPaint(Color.WHITE);

        chart.setBackgroundPaint(bgColor);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(bgColor);

        plot.setOutlineVisible(false);

        ChartPanel panel = new ChartPanel(chart);

        panel.setPreferredSize(null);
        panel.setMaximumDrawWidth(Integer.MAX_VALUE);
        panel.setMaximumDrawHeight(Integer.MAX_VALUE);
        panel.setMinimumDrawWidth(0);
        panel.setMinimumDrawHeight(0);
        panel.setMouseWheelEnabled(true);

        return panel;
    }

    private JPanel createPlaytimeDistributionPanel(List<Game> games) {

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel title = new JLabel("PLAYTIME DISTRIBUTION", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        if (games == null || games.isEmpty()) {
            JLabel none = new JLabel("No Data");
            none.setAlignmentX(Component.CENTER_ALIGNMENT);
            none.setForeground(Color.LIGHT_GRAY);
            panel.add(none);
            return panel;
        }

        int[] bins = new int[] {5, 15, 30, 60, 150};
        int[] counts = new int[bins.length + 1];

        for (Game g : games) {
            int hours = g.getPlaytime() / 60;

            int binIndex = 0;
            while (binIndex < bins.length && hours > bins[binIndex])
                binIndex++;

            counts[binIndex]++;
        }


        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String series = "Games";

        int lower = 0;
        for (int i = 0; i < bins.length; i++) {
            String label = lower + "–" + bins[i] + " hrs";
            dataset.addValue(counts[i], series, label);
            lower = bins[i];
        }
        dataset.addValue(counts[bins.length], series, "150+ hrs");

        JFreeChart chart = ChartFactory.createBarChart(
                null,
                "Playtime Range",
                "Games",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        chart.setBackgroundPaint(bgColor);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(bgColor);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(Color.GRAY);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, new Color(80, 160, 220));
        renderer.setShadowVisible(false);

        plot.getDomainAxis().setTickLabelPaint(Color.LIGHT_GRAY);
        plot.getDomainAxis().setLabelPaint(Color.LIGHT_GRAY);
        plot.getRangeAxis().setTickLabelPaint(Color.LIGHT_GRAY);
        plot.getRangeAxis().setLabelPaint(Color.LIGHT_GRAY);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(null);
        chartPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
        chartPanel.setMaximumDrawHeight(Integer.MAX_VALUE);
        chartPanel.setMinimumDrawWidth(0);
        chartPanel.setMinimumDrawHeight(0);
        chartPanel.setMouseWheelEnabled(true);

        JPanel wrapped = wrapChart(chartPanel);
        wrapped.setPreferredSize(new Dimension(300, 240));

        panel.add(wrapped);
        panel.add(Box.createVerticalStrut(12));

        JLabel footer = new JLabel("How your library's playtime is distributed.", SwingConstants.CENTER);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.setForeground(Color.LIGHT_GRAY);
        footer.setFont(footer.getFont().deriveFont(Font.ITALIC, 11f));
        panel.add(footer);

        return panel;
    }

    private JPanel createRecentlyPlayedPanel(List<Game> games){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel title = new JLabel("RECENTLY PLAYED", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        if (games == null || games.isEmpty()) {
            JLabel none = new JLabel("No Data");
            none.setAlignmentX(Component.CENTER_ALIGNMENT);
            none.setForeground(Color.LIGHT_GRAY);
            panel.add(none);
            return panel;
        }


        Game recentlyPlayed = games.getFirst();
        for (Game g : games) {
            if (g.getRecentPlaytime() > recentlyPlayed.getRecentPlaytime()){
                recentlyPlayed = g;
            }
        }

        JPanel contentRow = new JPanel();
        contentRow.setOpaque(false);
        contentRow.setLayout(new BoxLayout(contentRow, BoxLayout.X_AXIS));

        JPanel leftContent = new JPanel();
        leftContent.setOpaque(false);
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));

        JLabel gameName = new JLabel(recentlyPlayed.getTitle(), SwingConstants.CENTER);
        gameName.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameName.setForeground(Color.LIGHT_GRAY);
        gameName.setFont(gameName.getFont().deriveFont(Font.BOLD, 18f));
        leftContent.add(gameName);
        leftContent.add(Box.createVerticalStrut(6));

        int hours = recentlyPlayed.getRecentPlaytime() / 60;

        JLabel playtime = new JLabel(hours + " hrs", SwingConstants.CENTER);
        playtime.setAlignmentX(Component.CENTER_ALIGNMENT);
        playtime.setForeground(Color.WHITE);
        playtime.setFont(playtime.getFont().deriveFont(Font.PLAIN, 13f));
        leftContent.add(playtime);
        leftContent.add(Box.createVerticalStrut(10));

        contentRow.add(leftContent, BorderLayout.WEST);
        contentRow.add(recentlyPlayed.getImage());
        panel.add(contentRow);

        int totalRecentPlaytime = 0;
        for (Game g : games) {
            totalRecentPlaytime += g.getRecentPlaytime();
        }
        panel.add(wrapChart(createMostPlayedChart(recentlyPlayed.getRecentPlaytime(),
                totalRecentPlaytime - recentlyPlayed.getRecentPlaytime(),
                recentlyPlayed.getTitle())));

        String message;
        if (hours < 2){
            message = "Just downloaded, huh?";
        } else if (hours < 5) {
            message = "Barely past the tutorial.";
        } else if (hours < 15) {
            message = "Story's getting good?";
        } else if (hours < 60) {
            message = "Finished the main story?";
        } else {
            message = "Do we have a trophy hunter?";
        }

        JLabel footer = new JLabel(message, SwingConstants.CENTER);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.setForeground(textColor);
        footer.setFont(footer.getFont().deriveFont(Font.ITALIC, 11f));

        Dimension footerSize = new Dimension(Integer.MAX_VALUE, footer.getPreferredSize().height);
        footer.setMaximumSize(footerSize);

        panel.add(footer);

        return panel;
    }

    private JPanel createTopFiveRecentGamesPanel(List<Game> games) {

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel title = new JLabel("TOP 5 GAMES BY RECENT PLAYTIME", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        if (games == null || games.isEmpty()) {
            JLabel none = new JLabel("No Data");
            none.setAlignmentX(Component.CENTER_ALIGNMENT);
            none.setForeground(Color.LIGHT_GRAY);
            panel.add(none);
            return panel;
        }

        List<Game> top = games.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(Game::getRecentPlaytime).reversed())
                .limit(5)
                .toList();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Game g : top) {
            int hours = g.getRecentPlaytime() / 60;
            dataset.addValue(hours, "Playtime (hrs)", g.getTitle());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                null,
                "Game",
                "Hours",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        chart.setBackgroundPaint(bgColor);
        chart.getPlot().setBackgroundPaint(bgColor);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(Color.GRAY);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, new Color(80, 160, 220));

        plot.getDomainAxis().setTickLabelPaint(Color.LIGHT_GRAY);
        plot.getDomainAxis().setLabelPaint(Color.LIGHT_GRAY);
        plot.getRangeAxis().setTickLabelPaint(Color.LIGHT_GRAY);
        plot.getRangeAxis().setLabelPaint(Color.LIGHT_GRAY);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(null);
        chartPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
        chartPanel.setMaximumDrawHeight(Integer.MAX_VALUE);
        chartPanel.setMinimumDrawWidth(0);
        chartPanel.setMinimumDrawHeight(0);
        chartPanel.setMouseWheelEnabled(true);

        JPanel wrapped = wrapChart(chartPanel);
        wrapped.setPreferredSize(new Dimension(300, 240));

        panel.add(wrapped);
        panel.add(Box.createVerticalStrut(12));

        JLabel footer = new JLabel("Your recently played games.", SwingConstants.CENTER);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.setForeground(Color.LIGHT_GRAY);
        footer.setFont(footer.getFont().deriveFont(Font.ITALIC, 11f));
        panel.add(footer);

        return panel;
    }

    private JPanel createScatterPlaytimeVsRecentPanel(List<Game> games) {

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel title = new JLabel("PLAYTIME vs RECENT PLAYTIME", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        if (games == null || games.isEmpty()) {
            JLabel none = new JLabel("No Data");
            none.setAlignmentX(Component.CENTER_ALIGNMENT);
            none.setForeground(Color.LIGHT_GRAY);
            panel.add(none);
            return panel;
        }

        XYSeries series = new XYSeries("Games");
        for (Game g : games) {
            int total = g.getPlaytime() / 60;
            int recent = g.getRecentPlaytime() / 60;
            series.add(total, recent);
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);

        JFreeChart chart = ChartFactory.createScatterPlot(
                null,
                "Total Playtime (hrs)",
                "Recent Playtime (hrs)",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        chart.setBackgroundPaint(bgColor);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(bgColor);
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setOutlineVisible(false);

        XYShapeRenderer renderer = new XYShapeRenderer();

        renderer.setSeriesShape(0, new Ellipse2D.Double(-3, -3, 6, 6));
        renderer.setSeriesPaint(0, new Color(80, 160, 220));

        plot.setRenderer(renderer);

        plot.getDomainAxis().setLabelPaint(Color.LIGHT_GRAY);
        plot.getDomainAxis().setTickLabelPaint(Color.LIGHT_GRAY);
        plot.getRangeAxis().setLabelPaint(Color.LIGHT_GRAY);
        plot.getRangeAxis().setTickLabelPaint(Color.LIGHT_GRAY);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setPreferredSize(null);

        JPanel wrapped = wrapChart(chartPanel);
        wrapped.setPreferredSize(new Dimension(300, 260));

        panel.add(wrapped);
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    private JPanel createOldFavoritesAndUnplayedGamesPanel(List<Game> games) {

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        // --- FIXED SIZE FOR THE CARD ---
        Dimension fixed = new Dimension(300, 220);
        panel.setPreferredSize(fixed);
        panel.setMinimumSize(fixed);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        // Maximum width allowed to stretch horizontally
        // Height remains fixed; width grows under BoxLayout

        JLabel title = new JLabel("GAMES YOU SHOULD PLAY", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));


        List<Game> oldFavorites = new ArrayList<>();
        for (Game g : games) {
            int total = g.getPlaytime() / 60;
            int recent = g.getRecentPlaytime() / 60;
            if (total >= 50 && recent <= 1 && oldFavorites.size() < 5) {
                oldFavorites.add(g);
            }
        }

        List<Game> dead = new ArrayList<>();
        for (Game g : games) {
            if (g.getPlaytime() == 0 && dead.size() < 5) {
                dead.add(g);
            }
        }

        JPanel columns = new JPanel(new GridLayout(1, 2, 12, 0));
        columns.setOpaque(false);


        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel leftTitle = new JLabel("OLD FAVOURITES");
        leftTitle.setForeground(Color.WHITE);
        leftTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftTitle.setFont(leftTitle.getFont().deriveFont(Font.BOLD, 14f));
        left.add(leftTitle);
        left.add(Box.createVerticalStrut(4));

        if (oldFavorites.isEmpty()) {
            JLabel none = new JLabel("None found");
            none.setForeground(Color.LIGHT_GRAY);
            none.setAlignmentX(Component.CENTER_ALIGNMENT);
            left.add(none);
        } else {
            for (Game g : oldFavorites) {
                left.add(makeSmallGameLabel(g, false));
                left.add(Box.createVerticalStrut(3));
            }
        }

        JLabel leftFooter = new JLabel("Favourites you haven't played in a while.", SwingConstants.CENTER);
        leftFooter.setForeground(Color.LIGHT_GRAY);
        leftFooter.setFont(leftFooter.getFont().deriveFont(Font.ITALIC, 10f));
        leftFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(Box.createVerticalStrut(8));
        left.add(leftFooter);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JLabel rightTitle = new JLabel("UNPLAYED GAMES");
        rightTitle.setForeground(Color.WHITE);
        rightTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightTitle.setFont(rightTitle.getFont().deriveFont(Font.BOLD, 14f));
        right.add(rightTitle);
        right.add(Box.createVerticalStrut(4));

        if (dead.isEmpty()) {
            JLabel none = new JLabel("None found");
            none.setForeground(Color.LIGHT_GRAY);
            none.setAlignmentX(Component.CENTER_ALIGNMENT);
            right.add(none);
        } else {
            for (Game g : dead) {
                right.add(makeSmallGameLabel(g, true));
                right.add(Box.createVerticalStrut(3));
            }
        }

        JLabel rightFooter = new JLabel("How about clearing your backlog?", SwingConstants.CENTER);
        rightFooter.setForeground(Color.LIGHT_GRAY);
        rightFooter.setFont(rightFooter.getFont().deriveFont(Font.ITALIC, 10f));
        rightFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        right.add(Box.createVerticalStrut(8));
        right.add(rightFooter);


        columns.add(left);
        columns.add(right);
        panel.add(columns);

        return panel;
    }

    private JPanel makeSmallGameLabel(Game g, boolean showHours) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel name = new JLabel(g.getTitle());
        name.setForeground(Color.LIGHT_GRAY);
        name.setFont(name.getFont().deriveFont(Font.PLAIN, 12f));

        p.add(name, BorderLayout.WEST);

        String hrs = showHours
                ? ""
                : (g.getPlaytime() / 60) + " hrs";

        if (!hrs.isBlank()) {
            JLabel hrsLabel = new JLabel(hrs);
            hrsLabel.setForeground(Color.GRAY);
            hrsLabel.setFont(hrsLabel.getFont().deriveFont(11f));
            p.add(hrsLabel, BorderLayout.EAST);
        }

        return p;
    }

}
