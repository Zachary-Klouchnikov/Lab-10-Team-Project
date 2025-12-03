package use_case.userstatistics;

import org.junit.jupiter.api.Test;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class UserStatisticsOutputDataTest {

    @Test
    void getters_returnValuesProvidedToConstructor() {
        Icon icon = new ImageIcon();
        UserStatisticsOutputData.GameStatData mostPlayed =
                new UserStatisticsOutputData.GameStatData("Top", 10, 1, icon);
        UserStatisticsOutputData.GameStatData mostRecent =
                new UserStatisticsOutputData.GameStatData("Recent", 5, 2, icon);
        UserStatisticsOutputData.GameStatData unplayedGame =
                new UserStatisticsOutputData.GameStatData("Unplayed", 0, 0, null);

        int[] counts = {1, 2, 3, 4, 5, 6};
        String[] labels = {"a", "b", "c", "d", "e", "f"};

        UserStatisticsOutputData data = new UserStatisticsOutputData(
                "bob",
                99L,
                icon,
                120,
                "total message",
                mostPlayed,
                "most played message",
                List.of(mostPlayed),
                counts,
                labels,
                mostRecent,
                45,
                "recent message",
                List.of(mostRecent),
                List.of(new UserStatisticsOutputData.PlaytimePoint(100, 3)),
                List.of(mostPlayed),
                List.of(unplayedGame)
        );

        assertEquals("bob", data.getUsername());
        assertEquals(99L, data.getSteamId());
        assertSame(icon, data.getProfileImage());
        assertEquals(120, data.getTotalPlaytimeHours());
        assertEquals("total message", data.getTotalPlaytimeMessage());
        assertSame(mostPlayed, data.getMostPlayedGame());
        assertEquals("most played message", data.getMostPlayedMessage());
        assertEquals(List.of(mostPlayed), data.getTopFiveGames());
        assertSame(counts, data.getPlaytimeDistributionCounts());
        assertSame(labels, data.getPlaytimeDistributionLabels());
        assertSame(mostRecent, data.getMostRecentGame());
        assertEquals(45, data.getTotalRecentPlaytimeMinutes());
        assertEquals("recent message", data.getRecentPlaytimeMessage());
        assertEquals(List.of(mostRecent), data.getTopFiveRecentGames());
        assertEquals(100, data.getScatterPlotData().get(0).getTotalHours());
        assertEquals(3, data.getScatterPlotData().get(0).getRecentHours());
        assertEquals(List.of(mostPlayed), data.getOldFavorites());
        assertEquals(List.of(unplayedGame), data.getUnplayedGames());
    }
}
