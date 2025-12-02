package use_case.userstatistics;

import entity.Game;
import use_case.userstatistics.UserStatisticsOutputData.GameStatData;
import use_case.userstatistics.UserStatisticsOutputData.PlaytimePoint;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Performs statistics calculations on a user's game library.
 * Kept free of presentation concerns so it can be reused and unit tested easily.
 */
public class UserStatisticsCalculator {
    private static final int[] PLAYTIME_BINS = {5, 15, 30, 60, 150};

    public int totalPlaytimeHours(List<Game> games) {
        int totalMinutes = 0;
        for (Game game : games) {
            totalMinutes += game.getPlaytime();
        }
        return totalMinutes / 60;
    }

    public int totalRecentPlaytimeMinutes(List<Game> games) {
        int totalMinutes = 0;
        for (Game game : games) {
            totalMinutes += game.getRecentPlaytime();
        }
        return totalMinutes;
    }

    public GameStatData mostPlayedGame(List<Game> games) {
        return games.stream()
                .max(Comparator.comparingLong(Game::getPlaytime))
                .map(this::toGameStatData)
                .orElse(null);
    }

    public GameStatData mostRecentGame(List<Game> games) {
        return games.stream()
                .max(Comparator.comparingInt(Game::getRecentPlaytime))
                .map(this::toGameStatData)
                .orElse(null);
    }

    public List<GameStatData> topByPlaytime(List<Game> games, int limit) {
        return games.stream()
                .sorted(Comparator.comparingInt(Game::getPlaytime).reversed())
                .limit(limit)
                .map(this::toGameStatData)
                .collect(Collectors.toList());
    }

    public List<GameStatData> topByRecentPlaytime(List<Game> games, int limit) {
        return games.stream()
                .sorted(Comparator.comparingInt(Game::getRecentPlaytime).reversed())
                .limit(limit)
                .map(this::toGameStatData)
                .collect(Collectors.toList());
    }

    public int[] playtimeDistribution(List<Game> games) {
        int[] counts = new int[PLAYTIME_BINS.length + 1];
        for (Game g : games) {
            int hours = g.getPlaytime() / 60;
            int binIndex = 0;
            while (binIndex < PLAYTIME_BINS.length && hours > PLAYTIME_BINS[binIndex]) {
                binIndex++;
            }
            counts[binIndex]++;
        }
        return counts;
    }

    public List<PlaytimePoint> scatterPlot(List<Game> games) {
        return games.stream()
                .map(g -> new PlaytimePoint(g.getPlaytime() / 60, g.getRecentPlaytime() / 60))
                .collect(Collectors.toList());
    }

    public List<GameStatData> oldFavorites(List<Game> games, int maxResults) {
        List<GameStatData> oldFavorites = new ArrayList<>();
        for (Game g : games) {
            int totalHours = g.getPlaytime() / 60;
            int recentHours = g.getRecentPlaytime() / 60;
            if (totalHours >= 50 && recentHours <= 1 && oldFavorites.size() < maxResults) {
                oldFavorites.add(new GameStatData(g.getTitle(), totalHours, recentHours, g.getImage()));
            }
        }
        return oldFavorites;
    }

    public List<GameStatData> unplayed(List<Game> games, int maxResults) {
        List<GameStatData> unplayed = new ArrayList<>();
        for (Game g : games) {
            if (g.getPlaytime() == 0 && unplayed.size() < maxResults) {
                unplayed.add(new GameStatData(g.getTitle(), 0, 0, g.getImage()));
            }
        }
        return unplayed;
    }

    private GameStatData toGameStatData(Game game) {
        return new GameStatData(
                game.getTitle(),
                game.getPlaytime() / 60,
                game.getRecentPlaytime() / 60,
                game.getImage()
        );
    }

    public List<Game> sanitize(List<Game> games) {
        return games == null ? List.of() : games.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }
}
