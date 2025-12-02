package use_case.userstatistics;

import entity.Game;
import entity.User;
import org.junit.jupiter.api.Test;

import javax.swing.Icon;
import javax.swing.JLabel;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserStatisticsCalculatorTest {

    private Game game(String title, int playtimeMinutes, int recentMinutes) {
        return new Game(1L, title, playtimeMinutes, "hash_" + title, recentMinutes);
    }

    private User userWithGames(List<Game> games) {
        return new User(42L, "tester", new ArrayList<>(), games, "profile_hash");
    }

    @Test
    void calculatePopulatesStatistics() {
        List<Game> games = new ArrayList<>();
        games.add(game("Low", 120, 0));          // 2h
        games.add(game("Mid", 600, 0));          // 10h
        games.add(game("Recent", 300, 3600));    // 60h recent
        games.add(game("Old", 9000, 0));         // 150h total, old favorite
        games.add(game("Huge", 100000, 10));     // 1666h total
        games.add(game("Unplayed", 0, 0));       // unplayed

        UserStatisticsCalculator calculator = new UserStatisticsCalculator();
        UserStatisticsOutputData output = calculator.calculate(userWithGames(games));

        assertEquals("tester", output.getUsername());
        assertEquals(42L, output.getSteamId());
        assertNotNull(output.getProfileImage());
        assertInstanceOf(Icon.class, output.getProfileImage());

        assertEquals(1833, output.getTotalPlaytimeHours()); // 110020 minutes / 60
        assertFalse(output.getTotalPlaytimeMessage().isEmpty());

        assertNotNull(output.getMostPlayedGame());
        assertEquals("Huge", output.getMostPlayedGame().getTitle());
        assertNotNull(output.getMostPlayedMessage());

        assertNotNull(output.getTopFiveGames());
        assertEquals(5, output.getTopFiveGames().size());

        assertNotNull(output.getPlaytimeDistributionCounts());
        assertEquals(6, output.getPlaytimeDistributionCounts().length);
        int totalCounted = 0;
        for (int count : output.getPlaytimeDistributionCounts()) {
            totalCounted += count;
        }
        assertEquals(6, totalCounted);

        assertNotNull(output.getMostRecentGame());
        assertEquals("Recent", output.getMostRecentGame().getTitle());
        assertEquals(60, output.getMostRecentGame().getRecentPlaytimeHours());
        assertEquals(3610, output.getTotalRecentPlaytimeMinutes());
        assertFalse(output.getRecentPlaytimeMessage().isEmpty());

        assertNotNull(output.getTopFiveRecentGames());
        assertTrue(output.getTopFiveRecentGames().size() >= 1);

        assertNotNull(output.getScatterPlotData());
        assertEquals(6, output.getScatterPlotData().size());

        assertNotNull(output.getOldFavorites());
        assertTrue(output.getOldFavorites().stream().anyMatch(g -> g.getTitle().equals("Old")));

        assertNotNull(output.getUnplayedGames());
        assertTrue(output.getUnplayedGames().stream().anyMatch(g -> g.getTitle().equals("Unplayed")));
    }

    @Test
    void calculateHandlesEmptyLists() {
        List<Game> games = new ArrayList<>();
        games.add(game("Solo", 120, 0));

        UserStatisticsCalculator calculator = new UserStatisticsCalculator();
        UserStatisticsOutputData output = calculator.calculate(userWithGames(games));

        assertNotNull(output.getTopFiveGames());
        assertEquals(1, output.getTopFiveGames().size());
        assertEquals("Solo", output.getTopFiveGames().getFirst().getTitle());
    }
}
