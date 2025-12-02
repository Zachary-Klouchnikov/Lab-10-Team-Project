package interface_adapter.userstatistics;

import org.junit.jupiter.api.Test;

import javax.swing.ImageIcon;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserStatisticsViewDataTest {

    @Test
    void wrapsDataAndDefaultsNullCollections() {
        UserStatisticsViewData.GameStatViewData game = new UserStatisticsViewData.GameStatViewData("Game", 2, 1, new ImageIcon());
        UserStatisticsViewData.PlaytimePointViewData point = new UserStatisticsViewData.PlaytimePointViewData(3, 1);

        UserStatisticsViewData data = new UserStatisticsViewData(
                "name",
                "steam",
                new ImageIcon(),
                10,
                "total msg",
                game,
                "most msg",
                null,
                new int[]{1, 2},
                new String[]{"a", "b"},
                game,
                5,
                1,
                "recent msg",
                null,
                null,
                null,
                null
        );

        assertEquals("name", data.getUsername());
        assertEquals("steam", data.getSteamId());
        assertNotNull(data.getProfileImage());
        assertEquals(10, data.getTotalPlaytimeHours());
        assertEquals("total msg", data.getTotalPlaytimeMessage());
        assertEquals(game, data.getMostPlayedGame());
        assertEquals("most msg", data.getMostPlayedMessage());
        assertTrue(data.getTopFiveGames().isEmpty());
        assertArrayEquals(new int[]{1, 2}, data.getPlaytimeDistributionCounts());
        assertArrayEquals(new String[]{"a", "b"}, data.getPlaytimeDistributionLabels());
        assertEquals(game, data.getMostRecentGame());
        assertEquals(5, data.getMostRecentPlaytimeMinutes());
        assertEquals(1, data.getOtherRecentPlaytimeMinutes());
        assertEquals("recent msg", data.getRecentPlaytimeMessage());
        assertTrue(data.getTopFiveRecentGames().isEmpty());
        assertTrue(data.getScatterPlotData().isEmpty());
        assertTrue(data.getOldFavorites().isEmpty());
        assertTrue(data.getUnplayedGames().isEmpty());

        UserStatisticsViewData dataWithLists = new UserStatisticsViewData(
                "name",
                "steam",
                new ImageIcon(),
                10,
                "total msg",
                game,
                "most msg",
                List.of(game),
                new int[]{1},
                new String[]{"a"},
                game,
                5,
                1,
                "recent msg",
                List.of(game),
                List.of(point),
                List.of(game),
                List.of(game)
        );
        assertEquals(1, dataWithLists.getTopFiveGames().size());
        assertEquals(1, dataWithLists.getTopFiveRecentGames().size());
        assertEquals(1, dataWithLists.getScatterPlotData().size());
        assertEquals(1, dataWithLists.getOldFavorites().size());
        assertEquals(1, dataWithLists.getUnplayedGames().size());
    }
}
