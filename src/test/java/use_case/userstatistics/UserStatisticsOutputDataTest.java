package use_case.userstatistics;

import org.junit.jupiter.api.Test;

import javax.swing.ImageIcon;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserStatisticsOutputDataTest {

    @Test
    void gettersReturnConstructorValues() {
        UserStatisticsOutputData.GameStatData game = new UserStatisticsOutputData.GameStatData("title", 2, 1, new ImageIcon());
        UserStatisticsOutputData.PlaytimePoint point = new UserStatisticsOutputData.PlaytimePoint(4, 2);

        UserStatisticsOutputData data = new UserStatisticsOutputData(
                "name",
                9L,
                new ImageIcon(),
                5,
                "total",
                game,
                "most",
                List.of(game),
                new int[]{1, 2},
                new String[]{"a", "b"},
                game,
                30,
                "recent",
                List.of(game),
                List.of(point),
                List.of(game),
                List.of(game)
        );

        assertEquals("name", data.getUsername());
        assertEquals(9L, data.getSteamId());
        assertNotNull(data.getProfileImage());
        assertEquals(5, data.getTotalPlaytimeHours());
        assertEquals("total", data.getTotalPlaytimeMessage());
        assertEquals(game, data.getMostPlayedGame());
        assertEquals("most", data.getMostPlayedMessage());
        assertEquals(1, data.getTopFiveGames().size());
        assertArrayEquals(new int[]{1, 2}, data.getPlaytimeDistributionCounts());
        assertArrayEquals(new String[]{"a", "b"}, data.getPlaytimeDistributionLabels());
        assertEquals(game, data.getMostRecentGame());
        assertEquals(30, data.getTotalRecentPlaytimeMinutes());
        assertEquals("recent", data.getRecentPlaytimeMessage());
        assertEquals(1, data.getTopFiveRecentGames().size());
        assertEquals(1, data.getScatterPlotData().size());
        assertEquals(1, data.getOldFavorites().size());
        assertEquals(1, data.getUnplayedGames().size());
    }
}
