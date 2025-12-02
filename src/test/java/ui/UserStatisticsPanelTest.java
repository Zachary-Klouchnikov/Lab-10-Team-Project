package ui;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserStatisticsPanelTest {

    private entity.Game game(String title, int play, int recent) {
        return new entity.Game(
                0,
                title,
                play,
                "test_thumbnail",
                recent
        );
    }


    private entity.User makeUser(List<entity.Game> games) {
        return new entity.User(
                12345L,
                "User",
                new java.util.ArrayList<>(),
                games,
                "test_profile"
        );
    }


    @Test
    void testConstructorFullFlow() {
        java.util.List<entity.Game> games = new java.util.ArrayList<>();
        games.add(game("A", 6000, 120));
        entity.User u = makeUser(games);

        ui.UserStatisticsPanel p = new ui.UserStatisticsPanel(u);
        assertNotNull(p);
    }

    @Test
    void testTotalPlaytimeAllTiers() {
        ui.UserStatisticsPanel p = new ui.UserStatisticsPanel(makeUser(new java.util.ArrayList<>()));

        javax.swing.JPanel zero = p.createTotalPlaytimePanel(0);
        javax.swing.JPanel tier1 = p.createTotalPlaytimePanel(5);
        javax.swing.JPanel tier2 = p.createTotalPlaytimePanel(50);
        javax.swing.JPanel tier3 = p.createTotalPlaytimePanel(200);
        javax.swing.JPanel tier4 = p.createTotalPlaytimePanel(600);

        assertTrue(zero.getComponentCount() > 0);
        assertTrue(tier1.getComponentCount() > 0);
        assertTrue(tier2.getComponentCount() > 0);
        assertTrue(tier3.getComponentCount() > 0);
        assertTrue(tier4.getComponentCount() > 0);
    }

    @Test
    void testMostPlayedAllBranches() {
        ui.UserStatisticsPanel p = new ui.UserStatisticsPanel(makeUser(new java.util.ArrayList<>()));

        javax.swing.JPanel empty = p.createMostPlayedPanel(new java.util.ArrayList<>(), 0);
        assertTrue(empty.getComponentCount() > 0);

        java.util.List<entity.Game> games = new java.util.ArrayList<>();
        games.add(game("Low", 120, 0));
        games.add(game("Mid", 3000, 0));
        games.add(game("High", 20000, 0));
        javax.swing.JPanel filled = p.createMostPlayedPanel(games, 400);

        assertTrue(filled.getComponentCount() > 0);
    }

    @Test
    void testTopFiveGamesVariousSizes() {
        ui.UserStatisticsPanel p = new ui.UserStatisticsPanel(makeUser(new java.util.ArrayList<>()));

        javax.swing.JPanel empty = p.createTopFiveGamesPanel(new java.util.ArrayList<>());
        assertTrue(empty.getComponentCount() > 0);

        java.util.List<entity.Game> five = new java.util.ArrayList<>();
        five.add(game("1", 100, 10));
        five.add(game("2", 200, 20));
        five.add(game("3", 300, 30));
        five.add(game("4", 400, 40));
        five.add(game("5", 500, 50));
        five.add(game("6", 600, 60));

        javax.swing.JPanel panel = p.createTopFiveGamesPanel(five);
        assertTrue(panel.getComponentCount() > 0);
    }

    @Test
    void testPlaytimeDistributionBins() {
        ui.UserStatisticsPanel p = new ui.UserStatisticsPanel(makeUser(new java.util.ArrayList<>()));

        java.util.List<entity.Game> bins = new java.util.ArrayList<>();
        bins.add(game("A", 60, 0));     // 1 hr
        bins.add(game("B", 600, 0));    // 10 hr
        bins.add(game("C", 2000, 0));   // 33 hr
        bins.add(game("D", 3600, 0));   // 60 hr
        bins.add(game("E", 6000, 0));   // 100 hr
        bins.add(game("F", 999999, 0)); // huge

        javax.swing.JPanel panel = p.createPlaytimeDistributionPanel(bins);
        assertTrue(panel.getComponentCount() > 0);
    }

    @Test
    void testRecentlyPlayedAllTiers() {
        java.util.List<entity.Game> games = new java.util.ArrayList<>();
        games.add(game("Tiny", 0, 60));      // 1 hr
        games.add(game("Small", 0, 200));    // 3 hr
        games.add(game("Medium", 0, 600));   // 10 hr
        games.add(game("Large", 0, 3000));   // 50 hr
        games.add(game("Huge", 0, 6000));    // 100 hr

        ui.UserStatisticsPanel p = new ui.UserStatisticsPanel(makeUser(games));
        javax.swing.JPanel panel = p.createRecentlyPlayedPanel(games);

        assertTrue(panel.getComponentCount() > 0);
    }

    @Test
    void testTopFiveRecentGames() {
        java.util.List<entity.Game> games = new java.util.ArrayList<>();
        games.add(game("A", 0, 10));
        games.add(game("B", 0, 200));
        games.add(game("C", 0, 300));
        games.add(game("D", 0, 400));
        games.add(game("E", 0, 500));
        games.add(game("F", 0, 600));

        ui.UserStatisticsPanel p = new ui.UserStatisticsPanel(makeUser(games));
        javax.swing.JPanel panel = p.createTopFiveRecentGamesPanel(games);

        assertTrue(panel.getComponentCount() > 0);
    }

    @Test
    void testScatterPlot() {
        java.util.List<entity.Game> games = new java.util.ArrayList<>();
        games.add(game("A", 6000, 120));
        games.add(game("B", 10000, 60));

        ui.UserStatisticsPanel p = new ui.UserStatisticsPanel(makeUser(games));
        javax.swing.JPanel panel = p.createScatterPlaytimeVsRecentPanel(games);

        assertTrue(panel.getComponentCount() > 0);
    }

    @Test
    void testOldFavoritesAndUnplayed() {
        ui.UserStatisticsPanel p = new ui.UserStatisticsPanel(makeUser(new java.util.ArrayList<>()));

        java.util.List<entity.Game> games = new java.util.ArrayList<>();
        games.add(game("OldFav", 3000, 0));
        games.add(game("Unplayed", 0, 0));

        javax.swing.JPanel panel = p.createOldFavoritesAndUnplayedGamesPanel(games);
        assertTrue(panel.getComponentCount() > 0);

        javax.swing.JPanel empty = p.createOldFavoritesAndUnplayedGamesPanel(new java.util.ArrayList<>());
        assertTrue(empty.getComponentCount() > 0);
    }

    @Test
    void testMakeSmallGameLabelBothModes() {
        ui.UserStatisticsPanel p = new ui.UserStatisticsPanel(makeUser(new java.util.ArrayList<>()));

        javax.swing.JPanel withHours = p.makeSmallGameLabel(game("G", 3000, 0), false);
        javax.swing.JPanel noHours = p.makeSmallGameLabel(game("H", 2000, 0), true);

        assertNotNull(withHours);
        assertNotNull(noHours);
    }

    @Test
    void testMostPlayedNullBranch() {
        ui.UserStatisticsPanel p = new ui.UserStatisticsPanel(makeUser(List.of()));

        List<entity.Game> nullList = new ArrayList<>();
        nullList.add(null);
        nullList.add(null);

        JPanel panel = p.createMostPlayedPanel(nullList, 0);

        assertNotNull(panel);
    }


    @Test
    void testNullGamesAcrossPanels() {
        ui.UserStatisticsPanel p = new ui.UserStatisticsPanel(makeUser(List.of()));

        assertNotNull(p.createPlaytimeDistributionPanel(null));
        assertNotNull(p.createTopFiveGamesPanel(null));
        assertNotNull(p.createMostPlayedPanel(null, 0));
        assertNotNull(p.createRecentlyPlayedPanel(null));
    }

    @Test
    void testRecentlyPlayedBranchCoverage() {
        ui.UserStatisticsPanel p = new ui.UserStatisticsPanel(makeUser(new ArrayList<>()));

        // <2
        List<entity.Game> g1 = List.of(game("A",0,60));
        assertNotNull(p.createRecentlyPlayedPanel(g1));

        // <5
        List<entity.Game> g2 = List.of(game("B",0,200));
        assertNotNull(p.createRecentlyPlayedPanel(g2));

        // <15
        List<entity.Game> g3 = List.of(game("C",0,600));
        assertNotNull(p.createRecentlyPlayedPanel(g3));

        // <60
        List<entity.Game> g4 = List.of(game("D",0,3000));
        assertNotNull(p.createRecentlyPlayedPanel(g4));

        // >=60
        List<entity.Game> g5 = List.of(game("E",0,6000));
        assertNotNull(p.createRecentlyPlayedPanel(g5));
    }


    @Test
    void testOldFavoritesAtCapacity() {
        List<entity.Game> games = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            games.add(game("Fav"+i, 3000, 0)); // 50 hours
        }

        ui.UserStatisticsPanel p = new ui.UserStatisticsPanel(makeUser(games));
        JPanel panel = p.createOldFavoritesAndUnplayedGamesPanel(games);

        assertTrue(panel.getComponentCount() > 0);
    }

}
