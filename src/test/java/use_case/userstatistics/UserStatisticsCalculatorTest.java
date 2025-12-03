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

    private User userWithNullLibrary() {
        return new User(99L, "NullLibUser", new ArrayList<>(), new ArrayList<>(), "user-null") {
            @Override
            public List<Game> getLibrary() {
                return null;
            }
        };
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
    void calculate_withNullLibrary_handlesGracefully() {
        User user = userWithNullLibrary();
        UserStatisticsCalculator calculator = new UserStatisticsCalculator();

        UserStatisticsOutputData output = calculator.calculate(user);

        assertEquals(0, output.getTotalPlaytimeHours());
        assertTrue(output.getTopFiveGames().isEmpty());
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

    @Test
    void calculate_withLowPlaytime_setsLowerThresholdMessages() {
        List<Game> games = List.of(game(1, "Tiny", 120, 30)); // 2h total, 0h recent
        User user = userWithGames(games);
        UserStatisticsCalculator calculator = new UserStatisticsCalculator();

        UserStatisticsOutputData output = calculator.calculate(user);

        assertEquals("New account?", output.getTotalPlaytimeMessage());
        assertEquals("Still enough time for a refund.", output.getMostPlayedMessage());
        assertEquals("Just downloaded, huh?", output.getRecentPlaytimeMessage());
    }

    @Test
    void calculate_coversRemainingBranchesAndCaps() {
        List<Game> games = new ArrayList<>();
        // Most played between 100-200h triggers "Getting really into this game, huh?"
        games.add(game(10, "Top150", 190 * 60, 10));
        // Recent playtime buckets
        games.add(game(11, "Recent4", 50 * 60, 4 * 60));      // recent <5h
        games.add(game(12, "Recent59", 10 * 60, 59 * 60));    // recent <60h
        games.add(game(13, "Recent70", 20 * 60, 70 * 60));    // recent >=60h
        // Old favorites (recent <=1h) more than 5 entries
        for (int i = 0; i < 6; i++) {
            games.add(game(20 + i, "OldFav" + i, 70 * 60, 30));
        }
        // Unplayed more than 5 entries
        for (int i = 0; i < 7; i++) {
            games.add(game(40 + i, "Unplayed" + i, 0, 0));
        }
        // Extra games to exercise distribution bins
        games.add(game(60, "Bin0", 5 * 60, 0));    // exactly 5h
        games.add(game(61, "Bin1", 6 * 60, 0));    // >5h <=15h
        games.add(game(62, "Bin2", 20 * 60, 0));   // >15h <=30h
        games.add(game(63, "Bin3", 40 * 60, 0));   // >30h <=60h
        games.add(game(64, "Bin4", 100 * 60, 0));  // >60h <=150h
        games.add(game(65, "Bin5", 160 * 60, 0));  // >150h

        User user = userWithGames(games);
        UserStatisticsCalculator calculator = new UserStatisticsCalculator();

        UserStatisticsOutputData output = calculator.calculate(user);

        assertEquals("Getting really into this game, huh?", output.getMostPlayedMessage());
        assertEquals("Do we have a trophy hunter?", output.getRecentPlaytimeMessage());

        // Old favorites capped at 5
        assertEquals(5, output.getOldFavorites().size());
        // Unplayed capped at 5
        assertEquals(5, output.getUnplayedGames().size());
        // Top five games by playtime capped at 5 entries
        assertEquals(5, output.getTopFiveGames().size());

        // Distribution bins should account for all games
        int totalCounted = Arrays.stream(output.getPlaytimeDistributionCounts()).sum();
        assertEquals(games.size(), totalCounted);
    }
}
