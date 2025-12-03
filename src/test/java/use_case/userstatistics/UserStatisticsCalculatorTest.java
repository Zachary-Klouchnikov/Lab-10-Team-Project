package use_case.userstatistics;

import entity.Game;
import entity.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserStatisticsCalculatorTest {

    private Game game(long id, String title, int playtimeMinutes, int recentMinutes) {
        return new Game(id, title, playtimeMinutes, "hash-" + id, recentMinutes);
    }

    private User userWithGames(List<Game> games) {
        return new User(42L, "TestUser", new ArrayList<>(), games, "user-hash");
    }

    @Test
    void calculate_withEmptyLibrary_returnsBaselineOutput() {
        User user = userWithGames(new ArrayList<>());
        UserStatisticsCalculator calculator = new UserStatisticsCalculator();

        UserStatisticsOutputData output = calculator.calculate(user);

        assertEquals("TestUser", output.getUsername());
        assertEquals(42L, output.getSteamId());
        assertNotNull(output.getProfileImage());
        assertEquals(0, output.getTotalPlaytimeHours());
        assertEquals("New account?", output.getTotalPlaytimeMessage());
        assertNull(output.getMostPlayedGame());
        assertEquals("", output.getMostPlayedMessage());
        assertTrue(output.getTopFiveGames().isEmpty());
        assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, output.getPlaytimeDistributionCounts());
        assertArrayEquals(new String[]{"0-5 hrs", "5-15 hrs", "15-30 hrs", "30-60 hrs", "60-150 hrs", "150+ hrs"},
                output.getPlaytimeDistributionLabels());
        assertNull(output.getMostRecentGame());
        assertEquals(0, output.getTotalRecentPlaytimeMinutes());
        assertEquals("", output.getRecentPlaytimeMessage());
        assertTrue(output.getTopFiveRecentGames().isEmpty());
        assertTrue(output.getScatterPlotData().isEmpty());
        assertTrue(output.getOldFavorites().isEmpty());
        assertTrue(output.getUnplayedGames().isEmpty());
    }

    @Test
    void calculate_withMultipleGames_computesAllStatistics() {
        List<Game> games = Arrays.asList(
                game(1, "Alpha", 600, 120),      // 10h total, 2h recent
                game(2, "Bravo", 1200, 600),     // 20h total, 10h recent
                game(3, "Charlie", 3000, 30),    // 50h total, 0h recent (rounded)
                game(4, "Delta", 18000, 0),      // 300h total, 0h recent
                game(5, "Echo", 30, 30),         // 0h total, 0h recent (rounded)
                game(6, "Foxtrot", 0, 0)         // Unplayed
        );
        User user = userWithGames(games);
        UserStatisticsCalculator calculator = new UserStatisticsCalculator();

        UserStatisticsOutputData output = calculator.calculate(user);

        assertEquals(380, output.getTotalPlaytimeHours());
        assertEquals("Gaming as a hobby.", output.getTotalPlaytimeMessage());

        UserStatisticsOutputData.GameStatData mostPlayed = output.getMostPlayedGame();
        assertNotNull(mostPlayed);
        assertEquals("Delta", mostPlayed.getTitle());
        assertEquals(300, mostPlayed.getPlaytimeHours());
        assertEquals("You must really love this game...", output.getMostPlayedMessage());

        List<String> topFive = output.getTopFiveGames()
                .stream()
                .map(UserStatisticsOutputData.GameStatData::getTitle)
                .toList();
        assertEquals(Arrays.asList("Delta", "Charlie", "Bravo", "Alpha", "Echo"), topFive);

        assertArrayEquals(new int[]{2, 1, 1, 1, 0, 1}, output.getPlaytimeDistributionCounts());
        assertArrayEquals(new String[]{"0-5 hrs", "5-15 hrs", "15-30 hrs", "30-60 hrs", "60-150 hrs", "150+ hrs"},
                output.getPlaytimeDistributionLabels());

        UserStatisticsOutputData.GameStatData mostRecent = output.getMostRecentGame();
        assertNotNull(mostRecent);
        assertEquals("Bravo", mostRecent.getTitle());
        assertEquals(10, mostRecent.getRecentPlaytimeHours());
        assertEquals(780, output.getTotalRecentPlaytimeMinutes());
        assertEquals("Story's getting good?", output.getRecentPlaytimeMessage());

        List<String> topRecent = output.getTopFiveRecentGames()
                .stream()
                .map(UserStatisticsOutputData.GameStatData::getTitle)
                .toList();
        assertEquals(Arrays.asList("Bravo", "Alpha", "Charlie", "Echo", "Delta"), topRecent);

        List<UserStatisticsOutputData.PlaytimePoint> scatter = output.getScatterPlotData();
        assertEquals(games.size(), scatter.size());
        assertEquals(10, scatter.get(0).getTotalHours());
        assertEquals(2, scatter.get(0).getRecentHours());

        List<String> oldFavorites = output.getOldFavorites()
                .stream()
                .map(UserStatisticsOutputData.GameStatData::getTitle)
                .toList();
        assertEquals(Arrays.asList("Charlie", "Delta"), oldFavorites);

        List<String> unplayed = output.getUnplayedGames()
                .stream()
                .map(UserStatisticsOutputData.GameStatData::getTitle)
                .toList();
        assertEquals(List.of("Foxtrot"), unplayed);
    }
}
