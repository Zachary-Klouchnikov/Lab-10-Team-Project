package interface_adapter.userstatistics;

import javax.swing.Icon;
import java.util.Collections;
import java.util.List;

/**
 * View-facing DTO containing all data needed to render user statistics.
 * Keeps the UI layer independent of use-case layer types.
 */
public class UserStatisticsViewData {
    private final String username;
    private final String steamId;
    private final Icon profileImage;

    private final int totalPlaytimeHours;
    private final String totalPlaytimeMessage;

    private final GameStatViewData mostPlayedGame;
    private final String mostPlayedMessage;

    private final List<GameStatViewData> topFiveGames;

    private final int[] playtimeDistributionCounts;
    private final String[] playtimeDistributionLabels;

    private final GameStatViewData mostRecentGame;
    private final int mostRecentPlaytimeMinutes;
    private final int otherRecentPlaytimeMinutes;
    private final String recentPlaytimeMessage;

    private final List<GameStatViewData> topFiveRecentGames;

    private final List<PlaytimePointViewData> scatterPlotData;

    private final List<GameStatViewData> oldFavorites;
    private final List<GameStatViewData> unplayedGames;

    public UserStatisticsViewData(
            String username,
            String steamId,
            Icon profileImage,
            int totalPlaytimeHours,
            String totalPlaytimeMessage,
            GameStatViewData mostPlayedGame,
            String mostPlayedMessage,
            List<GameStatViewData> topFiveGames,
            int[] playtimeDistributionCounts,
            String[] playtimeDistributionLabels,
            GameStatViewData mostRecentGame,
            int mostRecentPlaytimeMinutes,
            int otherRecentPlaytimeMinutes,
            String recentPlaytimeMessage,
            List<GameStatViewData> topFiveRecentGames,
            List<PlaytimePointViewData> scatterPlotData,
            List<GameStatViewData> oldFavorites,
            List<GameStatViewData> unplayedGames) {
        this.username = username;
        this.steamId = steamId;
        this.profileImage = profileImage;
        this.totalPlaytimeHours = totalPlaytimeHours;
        this.totalPlaytimeMessage = totalPlaytimeMessage;
        this.mostPlayedGame = mostPlayedGame;
        this.mostPlayedMessage = mostPlayedMessage;
        this.topFiveGames = topFiveGames == null ? Collections.emptyList() : topFiveGames;
        this.playtimeDistributionCounts = playtimeDistributionCounts;
        this.playtimeDistributionLabels = playtimeDistributionLabels;
        this.mostRecentGame = mostRecentGame;
        this.mostRecentPlaytimeMinutes = mostRecentPlaytimeMinutes;
        this.otherRecentPlaytimeMinutes = otherRecentPlaytimeMinutes;
        this.recentPlaytimeMessage = recentPlaytimeMessage;
        this.topFiveRecentGames = topFiveRecentGames == null ? Collections.emptyList() : topFiveRecentGames;
        this.scatterPlotData = scatterPlotData == null ? Collections.emptyList() : scatterPlotData;
        this.oldFavorites = oldFavorites == null ? Collections.emptyList() : oldFavorites;
        this.unplayedGames = unplayedGames == null ? Collections.emptyList() : unplayedGames;
    }

    public String getUsername() {
        return username;
    }

    public String getSteamId() {
        return steamId;
    }

    public Icon getProfileImage() {
        return profileImage;
    }

    public int getTotalPlaytimeHours() {
        return totalPlaytimeHours;
    }

    public String getTotalPlaytimeMessage() {
        return totalPlaytimeMessage;
    }

    public GameStatViewData getMostPlayedGame() {
        return mostPlayedGame;
    }

    public String getMostPlayedMessage() {
        return mostPlayedMessage;
    }

    public List<GameStatViewData> getTopFiveGames() {
        return topFiveGames;
    }

    public int[] getPlaytimeDistributionCounts() {
        return playtimeDistributionCounts;
    }

    public String[] getPlaytimeDistributionLabels() {
        return playtimeDistributionLabels;
    }

    public GameStatViewData getMostRecentGame() {
        return mostRecentGame;
    }

    public int getMostRecentPlaytimeMinutes() {
        return mostRecentPlaytimeMinutes;
    }

    public int getOtherRecentPlaytimeMinutes() {
        return otherRecentPlaytimeMinutes;
    }

    public String getRecentPlaytimeMessage() {
        return recentPlaytimeMessage;
    }

    public List<GameStatViewData> getTopFiveRecentGames() {
        return topFiveRecentGames;
    }

    public List<PlaytimePointViewData> getScatterPlotData() {
        return scatterPlotData;
    }

    public List<GameStatViewData> getOldFavorites() {
        return oldFavorites;
    }

    public List<GameStatViewData> getUnplayedGames() {
        return unplayedGames;
    }

    public static class GameStatViewData {
        private final String title;
        private final int playtimeHours;
        private final int recentPlaytimeHours;
        private final Icon image;

        public GameStatViewData(String title, int playtimeHours, int recentPlaytimeHours, Icon image) {
            this.title = title;
            this.playtimeHours = playtimeHours;
            this.recentPlaytimeHours = recentPlaytimeHours;
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public int getPlaytimeHours() {
            return playtimeHours;
        }

        public int getRecentPlaytimeHours() {
            return recentPlaytimeHours;
        }

        public Icon getImage() {
            return image;
        }
    }

    public static class PlaytimePointViewData {
        private final int totalHours;
        private final int recentHours;

        public PlaytimePointViewData(int totalHours, int recentHours) {
            this.totalHours = totalHours;
            this.recentHours = recentHours;
        }

        public int getTotalHours() {
            return totalHours;
        }

        public int getRecentHours() {
            return recentHours;
        }
    }
}
