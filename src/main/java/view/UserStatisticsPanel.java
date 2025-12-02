package view;

import interface_adapter.userstatistics.UserStatisticsController;
import interface_adapter.userstatistics.UserStatisticsState;
import interface_adapter.userstatistics.UserStatisticsPresenter;
import interface_adapter.userstatistics.UserStatisticsViewData;
import interface_adapter.userstatistics.UserStatisticsViewData.GameStatViewData;
import interface_adapter.userstatistics.UserStatisticsViewData.PlaytimePointViewData;
import interface_adapter.userstatistics.UserStatisticsViewModel;
import use_case.userstatistics.UserStatisticsInteractor;
import entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class UserStatisticsPanel extends JPanel implements PropertyChangeListener {
    private final Color bgColor = new Color(42, 42, 42);
    private final Color textColor = Color.LIGHT_GRAY;

    private UserStatisticsViewModel viewModel;
    private UserStatisticsController controller;
    private JPanel statsContainer;

    public UserStatisticsPanel(UserStatisticsViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);
        initUI();
    }

    public static UserStatisticsPanel createForUser(User user) {
        UserStatisticsViewModel viewModel = new UserStatisticsViewModel();
        UserStatisticsPresenter presenter = new UserStatisticsPresenter(viewModel);
        UserStatisticsInteractor interactor = new UserStatisticsInteractor(presenter);
        UserStatisticsController controller = new UserStatisticsController(interactor);

        UserStatisticsPanel panel = new UserStatisticsPanel(viewModel);
        panel.setController(controller);
        controller.loadStatistics(user);
        return panel;
    }

    public void setController(UserStatisticsController controller) {
        this.controller = controller;
    }

    public UserStatisticsController getController() {
        return controller;
    }

    private void initUI() {
        setBackground(bgColor);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        statsContainer = new JPanel();
        statsContainer.setOpaque(false);
        statsContainer.setLayout(new BoxLayout(statsContainer, BoxLayout.Y_AXIS));
        add(statsContainer, BorderLayout.CENTER);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        UserStatisticsState state = viewModel.getState();

        if (!state.getErrorMessage().isEmpty()) {
            showError(state.getErrorMessage());
            return;
        }

        if (state.getStatistics() != null) {
            renderStatistics(state.getStatistics());
        }
    }

    private void showError(String message) {
        statsContainer.removeAll();
        JLabel errorLabel = new JLabel("Error: " + message);
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsContainer.add(errorLabel);
        statsContainer.revalidate();
        statsContainer.repaint();
    }

    private void renderStatistics(UserStatisticsViewData stats) {
        statsContainer.removeAll();

        // Header panel
        statsContainer.add(createHeaderPanel(stats));

        // Total playtime panel
        statsContainer.add(createTotalPlaytimePanel(stats.getTotalPlaytimeHours(), stats.getTotalPlaytimeMessage()));

        // Most played panel
        if (stats.getMostPlayedGame() != null) {
            statsContainer.add(createMostPlayedPanel(stats.getMostPlayedGame(), stats.getTotalPlaytimeHours(), stats.getMostPlayedMessage()));
        }

        // Top 5 games
        if (stats.getTopFiveGames() != null && !stats.getTopFiveGames().isEmpty()) {
            statsContainer.add(createTopFiveGamesPanel(stats.getTopFiveGames()));
        }

        // Playtime distribution
        if (stats.getPlaytimeDistributionCounts() != null) {
            statsContainer.add(createPlaytimeDistributionPanel(stats.getPlaytimeDistributionCounts(), stats.getPlaytimeDistributionLabels()));
        }

        // Recently played
        if (stats.getMostRecentGame() != null) {
            statsContainer.add(createRecentlyPlayedPanel(
                    stats.getMostRecentGame(),
                    stats.getMostRecentPlaytimeMinutes(),
                    stats.getOtherRecentPlaytimeMinutes(),
                    stats.getRecentPlaytimeMessage()
            ));
        }

        // Top 5 recent games
        if (stats.getTopFiveRecentGames() != null && !stats.getTopFiveRecentGames().isEmpty()) {
            statsContainer.add(createTopFiveRecentGamesPanel(stats.getTopFiveRecentGames()));
        }

        // Scatter plot
        if (stats.getScatterPlotData() != null && !stats.getScatterPlotData().isEmpty()) {
            statsContainer.add(createScatterPlaytimeVsRecentPanel(stats.getScatterPlotData()));
        }

        // Old favorites & unplayed games
        statsContainer.add(createOldFavoritesAndUnplayedGamesPanel(stats.getOldFavorites(), stats.getUnplayedGames()));

        // Footer
        JLabel footerLabel = new JLabel("Detailed stats coming soon…");
        footerLabel.setForeground(bgColor);
        footerLabel.setFont(footerLabel.getFont().deriveFont(Font.ITALIC, 11f));
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.add(footerLabel, BorderLayout.WEST);
        statsContainer.add(footerPanel);

        statsContainer.revalidate();
        statsContainer.repaint();
    }

    private JPanel createHeaderPanel(UserStatisticsViewData stats) {
        JLabel nameLabel = new JLabel(stats.getUsername());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 20f));

        JLabel idLabel = new JLabel("Steam ID: " + stats.getSteamId());
        idLabel.setForeground(Color.LIGHT_GRAY);
        idLabel.setFont(idLabel.getFont().deriveFont(Font.PLAIN, 12f));

        JLabel imgLabel = new JLabel(stats.getProfileImage());

        if (stats.getProfileImage() instanceof ImageIcon) {
            ImageIcon icon = (ImageIcon) stats.getProfileImage();
            int targetHeight = nameLabel.getPreferredSize().height + idLabel.getPreferredSize().height + 4;
            int targetWidth = icon.getIconWidth() * targetHeight / icon.getIconHeight();
            Image scaled = icon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
        }

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

        return headerPanel;
    }

    private JPanel createTotalPlaytimePanel(int totalPlaytime, String message) {
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

        JLabel footer = new JLabel(message, SwingConstants.CENTER);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.setForeground(textColor);
        footer.setFont(footer.getFont().deriveFont(Font.ITALIC, 11f));

        Dimension footerSize = new Dimension(Integer.MAX_VALUE, footer.getPreferredSize().height);
        footer.setMaximumSize(footerSize);

        panel.add(footer);

        return panel;
    }

    private JPanel createMostPlayedPanel(GameStatViewData mostPlayed, int totalPlaytime, String message) {
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

        int hours = mostPlayed.getPlaytimeHours();

        JLabel playtime = new JLabel(hours + " hrs", SwingConstants.CENTER);
        playtime.setAlignmentX(Component.CENTER_ALIGNMENT);
        playtime.setForeground(Color.WHITE);
        playtime.setFont(playtime.getFont().deriveFont(Font.PLAIN, 13f));
        leftContent.add(playtime);
        leftContent.add(Box.createVerticalStrut(10));

        contentRow.add(leftContent, BorderLayout.WEST);
        contentRow.add(new JLabel(mostPlayed.getImage()), BorderLayout.EAST);

        panel.add(contentRow);
        panel.add(wrapChart(createPieChart(hours, totalPlaytime - hours, mostPlayed.getTitle())));

        JLabel footer = new JLabel(message, SwingConstants.CENTER);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.setForeground(textColor);
        footer.setFont(footer.getFont().deriveFont(Font.ITALIC, 11f));
        panel.add(footer);

        return panel;
    }

    JPanel wrapChart(ChartPanel chartPanel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(chartPanel, BorderLayout.CENTER);
        wrapper.setPreferredSize(new Dimension(200, 200));
        return wrapper;
    }

    private JPanel createTopFiveGamesPanel(List<GameStatViewData> topFive) {
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

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (GameStatViewData g : topFive) {
            dataset.addValue(g.getPlaytimeHours(), "Playtime (hrs)", g.getTitle());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                null, "Game", "Hours", dataset,
                PlotOrientation.VERTICAL, false, true, false
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

    private ChartPanel createPieChart(int small, int big, String label) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue(label, small);
        dataset.setValue("other games", big);

        JFreeChart chart = ChartFactory.createPieChart(
                label + " as a proportion of total playtime",
                dataset, false, true, false
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

    private JPanel createPlaytimeDistributionPanel(int[] counts, String[] labels) {
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

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String series = "Games";
        for (int i = 0; i < counts.length && i < labels.length; i++) {
            dataset.addValue(counts[i], series, labels[i]);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                null, "Playtime Range", "Games", dataset,
                PlotOrientation.VERTICAL, false, true, false
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

    private JPanel createRecentlyPlayedPanel(GameStatViewData mostRecent, int mostRecentMinutes, int otherRecentMinutes, String message) {
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

        JPanel contentRow = new JPanel();
        contentRow.setOpaque(false);
        contentRow.setLayout(new BoxLayout(contentRow, BoxLayout.X_AXIS));

        JPanel leftContent = new JPanel();
        leftContent.setOpaque(false);
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));

        JLabel gameName = new JLabel(mostRecent.getTitle(), SwingConstants.CENTER);
        gameName.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameName.setForeground(Color.LIGHT_GRAY);
        gameName.setFont(gameName.getFont().deriveFont(Font.BOLD, 18f));
        leftContent.add(gameName);
        leftContent.add(Box.createVerticalStrut(6));

        int hours = mostRecent.getRecentPlaytimeHours();

        JLabel playtime = new JLabel(hours + " hrs", SwingConstants.CENTER);
        playtime.setAlignmentX(Component.CENTER_ALIGNMENT);
        playtime.setForeground(Color.WHITE);
        playtime.setFont(playtime.getFont().deriveFont(Font.PLAIN, 13f));
        leftContent.add(playtime);
        leftContent.add(Box.createVerticalStrut(10));

        contentRow.add(leftContent, BorderLayout.WEST);
        contentRow.add(new JLabel(mostRecent.getImage()));
        panel.add(contentRow);

        panel.add(wrapChart(createPieChart(
                mostRecentMinutes,
                otherRecentMinutes,
                mostRecent.getTitle()
        )));

        JLabel footer = new JLabel(message, SwingConstants.CENTER);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.setForeground(textColor);
        footer.setFont(footer.getFont().deriveFont(Font.ITALIC, 11f));

        Dimension footerSize = new Dimension(Integer.MAX_VALUE, footer.getPreferredSize().height);
        footer.setMaximumSize(footerSize);

        panel.add(footer);

        return panel;
    }

    private JPanel createTopFiveRecentGamesPanel(List<GameStatViewData> topFive) {
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

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (GameStatViewData g : topFive) {
            dataset.addValue(g.getRecentPlaytimeHours(), "Playtime (hrs)", g.getTitle());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                null, "Game", "Hours", dataset,
                PlotOrientation.VERTICAL, false, true, false
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

    private JPanel createScatterPlaytimeVsRecentPanel(List<PlaytimePointViewData> scatterData) {
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

        XYSeries series = new XYSeries("Games");
        for (PlaytimePointViewData p : scatterData) {
            series.add(p.getTotalHours(), p.getRecentHours());
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);

        JFreeChart chart = ChartFactory.createScatterPlot(
                null, "Total Playtime (hrs)", "Recent Playtime (hrs)",
                dataset, PlotOrientation.VERTICAL, false, true, false
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

    private JPanel createOldFavoritesAndUnplayedGamesPanel(List<GameStatViewData> oldFavorites, List<GameStatViewData> unplayedGames) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        Dimension fixed = new Dimension(300, 220);
        panel.setPreferredSize(fixed);
        panel.setMinimumSize(fixed);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JLabel title = new JLabel("GAMES YOU SHOULD PLAY", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        JPanel columns = new JPanel(new GridLayout(1, 2, 12, 0));
        columns.setOpaque(false);

        // Left column - Old Favorites
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel leftTitle = new JLabel("OLD FAVOURITES");
        leftTitle.setForeground(Color.WHITE);
        leftTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftTitle.setFont(leftTitle.getFont().deriveFont(Font.BOLD, 14f));
        left.add(leftTitle);
        left.add(Box.createVerticalStrut(4));

        if (oldFavorites == null || oldFavorites.isEmpty()) {
            JLabel none = new JLabel("None found");
            none.setForeground(Color.LIGHT_GRAY);
            none.setAlignmentX(Component.CENTER_ALIGNMENT);
            left.add(none);
        } else {
            for (GameStatViewData g : oldFavorites) {
                left.add(makeSmallGameLabel(g.getTitle(), g.getPlaytimeHours(), false));
                left.add(Box.createVerticalStrut(3));
            }
        }

        JLabel leftFooter = new JLabel("Favourites you haven't played in a while.", SwingConstants.CENTER);
        leftFooter.setForeground(Color.LIGHT_GRAY);
        leftFooter.setFont(leftFooter.getFont().deriveFont(Font.ITALIC, 10f));
        leftFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(Box.createVerticalStrut(8));
        left.add(leftFooter);

        // Right column - Unplayed Games
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JLabel rightTitle = new JLabel("UNPLAYED GAMES");
        rightTitle.setForeground(Color.WHITE);
        rightTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightTitle.setFont(rightTitle.getFont().deriveFont(Font.BOLD, 14f));
        right.add(rightTitle);
        right.add(Box.createVerticalStrut(4));

        if (unplayedGames == null || unplayedGames.isEmpty()) {
            JLabel none = new JLabel("None found");
            none.setForeground(Color.LIGHT_GRAY);
            none.setAlignmentX(Component.CENTER_ALIGNMENT);
            right.add(none);
        } else {
            for (GameStatViewData g : unplayedGames) {
                right.add(makeSmallGameLabel(g.getTitle(), 0, true));
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

    private JPanel makeSmallGameLabel(String title, int playtimeHours, boolean hideHours) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel name = new JLabel(title);
        name.setForeground(Color.LIGHT_GRAY);
        name.setFont(name.getFont().deriveFont(Font.PLAIN, 12f));

        p.add(name, BorderLayout.WEST);

        if (!hideHours) {
            JLabel hrsLabel = new JLabel(playtimeHours + " hrs");
            hrsLabel.setForeground(Color.GRAY);
            hrsLabel.setFont(hrsLabel.getFont().deriveFont(11f));
            p.add(hrsLabel, BorderLayout.EAST);
        }

        return p;
    }
}
