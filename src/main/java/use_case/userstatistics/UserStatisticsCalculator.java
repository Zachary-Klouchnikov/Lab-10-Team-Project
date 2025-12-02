package use_case.userstatistics;

import entity.Game;
import entity.User;
import use_case.userstatistics.UserStatisticsOutputData.GameStatData;
import use_case.userstatistics.UserStatisticsOutputData.PlaytimePoint;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserStatisticsCalculator {

    public UserStatisticsOutputData calculate(User user) {
        List<Game> games = user.getLibrary();
        if (games == null) {
            games = new ArrayList<>();
        }

        int totalPlaytimeHours = calculateTotalPlaytime(games);
        String totalPlaytimeMessage = getTotalPlaytimeMessage(totalPlaytimeHours);

        GameStatData mostPlayed = findMostPlayedGame(games);
        String mostPlayedMessage = getMostPlayedMessage(mostPlayed);

        List<GameStatData> topFive = getTopFiveByPlaytime(games);

        int[] distributionCounts = calculatePlaytimeDistribution(games);
        String[] distributionLabels = getDistributionLabels();

        GameStatData mostRecent = findMostRecentGame(games);
        int totalRecentMinutes = calculateTotalRecentPlaytime(games);
        String recentMessage = getRecentPlaytimeMessage(mostRecent);

        List<GameStatData> topFiveRecent = getTopFiveByRecentPlaytime(games);

        List<PlaytimePoint> scatterData = calculateScatterData(games);

        List<GameStatData> oldFavorites = findOldFavorites(games);
        List<GameStatData> unplayed = findUnplayedGames(games);

        return new UserStatisticsOutputData(
                user.getUsername(),
                user.getId(),
                user.getImage(),
                totalPlaytimeHours,
                totalPlaytimeMessage,
                mostPlayed,
                mostPlayedMessage,
                topFive,
                distributionCounts,
                distributionLabels,
                mostRecent,
                totalRecentMinutes,
                recentMessage,
                topFiveRecent,
                scatterData,
                oldFavorites,
                unplayed
        );
    }

    private int calculateTotalPlaytime(List<Game> games) {
        int totalMinutes = 0;
        for (Game game : games) {
            totalMinutes += game.getPlaytime();
        }
        return totalMinutes / 60;
    }

    private String getTotalPlaytimeMessage(int hours) {
        if (hours < 10) {
            return "New account?";
        } else if (hours < 100) {
            return "Getting into gaming...";
        } else if (hours < 500) {
            return "Gaming as a hobby.";
        } else {
            return "We have a true Gamer in our midst...";
        }
    }

    private GameStatData findMostPlayedGame(List<Game> games) {
        return games.stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparingLong(Game::getPlaytime))
                .map(g -> new GameStatData(
                        g.getTitle(),
                        g.getPlaytime() / 60,
                        g.getRecentPlaytime() / 60,
                        g.getImage()))
                .orElse(null);
    }

    private String getMostPlayedMessage(GameStatData mostPlayed) {
        if (mostPlayed == null) {
            return "";
        }
        int hours = mostPlayed.getPlaytimeHours();
        if (hours < 3) {
            return "Still enough time for a refund.";
        } else if (hours < 100) {
            return "I can see it's a favourite.";
        } else if (hours < 200) {
            return "Getting really into this game, huh?";
        } else {
            return "You must really love this game...";
        }
    }

    private List<GameStatData> getTopFiveByPlaytime(List<Game> games) {
        return games.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(Game::getPlaytime).reversed())
                .limit(5)
                .map(g -> new GameStatData(
                        g.getTitle(),
                        g.getPlaytime() / 60,
                        g.getRecentPlaytime() / 60,
                        g.getImage()))
                .collect(Collectors.toList());
    }

    private int[] calculatePlaytimeDistribution(List<Game> games) {
        int[] bins = {5, 15, 30, 60, 150};
        int[] counts = new int[bins.length + 1];

        for (Game g : games) {
            int hours = g.getPlaytime() / 60;
            int binIndex = 0;
            while (binIndex < bins.length && hours > bins[binIndex]) {
                binIndex++;
            }
            counts[binIndex]++;
        }
        return counts;
    }

    private String[] getDistributionLabels() {
        return new String[]{"0-5 hrs", "5-15 hrs", "15-30 hrs", "30-60 hrs", "60-150 hrs", "150+ hrs"};
    }

    private GameStatData findMostRecentGame(List<Game> games) {
        return games.stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(Game::getRecentPlaytime))
                .map(g -> new GameStatData(
                        g.getTitle(),
                        g.getPlaytime() / 60,
                        g.getRecentPlaytime() / 60,
                        g.getImage()))
                .orElse(null);
    }

    private int calculateTotalRecentPlaytime(List<Game> games) {
        int totalMinutes = 0;
        for (Game g : games) {
            totalMinutes += g.getRecentPlaytime();
        }
        return totalMinutes;
    }

    private String getRecentPlaytimeMessage(GameStatData mostRecent) {
        if (mostRecent == null) {
            return "";
        }
        int hours = mostRecent.getRecentPlaytimeHours();
        if (hours < 2) {
            return "Just downloaded, huh?";
        } else if (hours < 5) {
            return "Barely past the tutorial.";
        } else if (hours < 15) {
            return "Story's getting good?";
        } else if (hours < 60) {
            return "Finished the main story?";
        } else {
            return "Do we have a trophy hunter?";
        }
    }

    private List<GameStatData> getTopFiveByRecentPlaytime(List<Game> games) {
        return games.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(Game::getRecentPlaytime).reversed())
                .limit(5)
                .map(g -> new GameStatData(
                        g.getTitle(),
                        g.getPlaytime() / 60,
                        g.getRecentPlaytime() / 60,
                        g.getImage()))
                .collect(Collectors.toList());
    }

    private List<PlaytimePoint> calculateScatterData(List<Game> games) {
        return games.stream()
                .filter(Objects::nonNull)
                .map(g -> new PlaytimePoint(g.getPlaytime() / 60, g.getRecentPlaytime() / 60))
                .collect(Collectors.toList());
    }

    private List<GameStatData> findOldFavorites(List<Game> games) {
        List<GameStatData> oldFavorites = new ArrayList<>();
        for (Game g : games) {
            int total = g.getPlaytime() / 60;
            int recent = g.getRecentPlaytime() / 60;
            if (total >= 50 && recent <= 1 && oldFavorites.size() < 5) {
                oldFavorites.add(new GameStatData(
                        g.getTitle(),
                        total,
                        recent,
                        g.getImage()));
            }
        }
        return oldFavorites;
    }

    private List<GameStatData> findUnplayedGames(List<Game> games) {
        List<GameStatData> unplayed = new ArrayList<>();
        for (Game g : games) {
            if (g.getPlaytime() == 0 && unplayed.size() < 5) {
                unplayed.add(new GameStatData(
                        g.getTitle(),
                        0,
                        0,
                        g.getImage()));
            }
        }
        return unplayed;
    }
}
