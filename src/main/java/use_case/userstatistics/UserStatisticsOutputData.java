package use_case.userstatistics;

import javax.swing.Icon;
import java.util.List;

public class UserStatisticsOutputData {
    // User info
    private final String username;
    private final long steamId;
    private final Icon profileImage;

    // Total playtime
    private final int totalPlaytimeHours;
    private final String totalPlaytimeMessage;

    // Most played game
    private final GameStatData mostPlayedGame;
    private final String mostPlayedMessage;

    // Top 5 games by total playtime
    private final List<GameStatData> topFiveGames;

    // Playtime distribution
    private final int[] playtimeDistributionCounts;
    private final String[] playtimeDistributionLabels;

    // Recently played
    private final GameStatData mostRecentGame;
    private final int totalRecentPlaytimeMinutes;
    private final String recentPlaytimeMessage;

    // Top 5 recent games
    private final List<GameStatData> topFiveRecentGames;

    // Scatter plot data
    private final List<PlaytimePoint> scatterPlotData;

    // Old favorites & unplayed games
    private final List<GameStatData> oldFavorites;
    private final List<GameStatData> unplayedGames;

    public UserStatisticsOutputData(
            String username,
            long steamId,
            Icon profileImage,
            int totalPlaytimeHours,
            String totalPlaytimeMessage,
            GameStatData mostPlayedGame,
            String mostPlayedMessage,
            List<GameStatData> topFiveGames,
            int[] playtimeDistributionCounts,
            String[] playtimeDistributionLabels,
            GameStatData mostRecentGame,
            int totalRecentPlaytimeMinutes,
            String recentPlaytimeMessage,
            List<GameStatData> topFiveRecentGames,
            List<PlaytimePoint> scatterPlotData,
            List<GameStatData> oldFavorites,
            List<GameStatData> unplayedGames) {
        this.username = username;
        this.steamId = steamId;
        this.profileImage = profileImage;
        this.totalPlaytimeHours = totalPlaytimeHours;
        this.totalPlaytimeMessage = totalPlaytimeMessage;
        this.mostPlayedGame = mostPlayedGame;
        this.mostPlayedMessage = mostPlayedMessage;
        this.topFiveGames = topFiveGames;
        this.playtimeDistributionCounts = playtimeDistributionCounts;
        this.playtimeDistributionLabels = playtimeDistributionLabels;
        this.mostRecentGame = mostRecentGame;
        this.totalRecentPlaytimeMinutes = totalRecentPlaytimeMinutes;
        this.recentPlaytimeMessage = recentPlaytimeMessage;
        this.topFiveRecentGames = topFiveRecentGames;
        this.scatterPlotData = scatterPlotData;
        this.oldFavorites = oldFavorites;
        this.unplayedGames = unplayedGames;
    }

    // Getters
    public String getUsername() { return username; }
    public long getSteamId() { return steamId; }
    public Icon getProfileImage() { return profileImage; }
    public int getTotalPlaytimeHours() { return totalPlaytimeHours; }
    public String getTotalPlaytimeMessage() { return totalPlaytimeMessage; }
    public GameStatData getMostPlayedGame() { return mostPlayedGame; }
    public String getMostPlayedMessage() { return mostPlayedMessage; }
    public List<GameStatData> getTopFiveGames() { return topFiveGames; }
    public int[] getPlaytimeDistributionCounts() { return playtimeDistributionCounts; }
    public String[] getPlaytimeDistributionLabels() { return playtimeDistributionLabels; }
    public GameStatData getMostRecentGame() { return mostRecentGame; }
    public int getTotalRecentPlaytimeMinutes() { return totalRecentPlaytimeMinutes; }
    public String getRecentPlaytimeMessage() { return recentPlaytimeMessage; }
    public List<GameStatData> getTopFiveRecentGames() { return topFiveRecentGames; }
    public List<PlaytimePoint> getScatterPlotData() { return scatterPlotData; }
    public List<GameStatData> getOldFavorites() { return oldFavorites; }
    public List<GameStatData> getUnplayedGames() { return unplayedGames; }

    /**
     * DTO for game statistics data.
     */
    public static class GameStatData {
        private final String title;
        private final int playtimeHours;
        private final int recentPlaytimeHours;
        private final Icon image;

        public GameStatData(String title, int playtimeHours, int recentPlaytimeHours, Icon image) {
            this.title = title;
            this.playtimeHours = playtimeHours;
            this.recentPlaytimeHours = recentPlaytimeHours;
            this.image = image;
        }

        public String getTitle() { return title; }
        public int getPlaytimeHours() { return playtimeHours; }
        public int getRecentPlaytimeHours() { return recentPlaytimeHours; }
        public Icon getImage() { return image; }
    }

    /**
     * DTO for scatter plot data points.
     */
    public static class PlaytimePoint {
        private final int totalHours;
        private final int recentHours;

        public PlaytimePoint(int totalHours, int recentHours) {
            this.totalHours = totalHours;
            this.recentHours = recentHours;
        }

        public int getTotalHours() { return totalHours; }
        public int getRecentHours() { return recentHours; }
    }
}
