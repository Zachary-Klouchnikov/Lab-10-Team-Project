package use_case.userstatistics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultUserStatisticsMessageFormatterTest {

    private final DefaultUserStatisticsMessageFormatter formatter = new DefaultUserStatisticsMessageFormatter();

    @Test
    void formatTotalPlaytime_respectsThresholds() {
        assertEquals("New account?", formatter.formatTotalPlaytime(0));
        assertEquals("Getting into gaming...", formatter.formatTotalPlaytime(10));
        assertEquals("Gaming as a hobby.", formatter.formatTotalPlaytime(120));
        assertEquals("We have a true Gamer in our midst...", formatter.formatTotalPlaytime(600));
    }

    @Test
    void formatMostPlayed_handlesNullAndBoundaries() {
        assertEquals("", formatter.formatMostPlayed(null));

        UserStatisticsOutputData.GameStatData lowPlaytime =
                new UserStatisticsOutputData.GameStatData("Low", 1, 0, null);
        UserStatisticsOutputData.GameStatData midPlaytime =
                new UserStatisticsOutputData.GameStatData("Mid", 50, 0, null);
        UserStatisticsOutputData.GameStatData highPlaytime =
                new UserStatisticsOutputData.GameStatData("High", 150, 0, null);
        UserStatisticsOutputData.GameStatData ultraPlaytime =
                new UserStatisticsOutputData.GameStatData("Ultra", 300, 0, null);

        assertEquals("Still enough time for a refund.", formatter.formatMostPlayed(lowPlaytime));
        assertEquals("I can see it's a favourite.", formatter.formatMostPlayed(midPlaytime));
        assertEquals("Getting really into this game, huh?", formatter.formatMostPlayed(highPlaytime));
        assertEquals("You must really love this game...", formatter.formatMostPlayed(ultraPlaytime));
    }

    @Test
    void formatRecentPlaytime_handlesNullAndBoundaries() {
        assertEquals("", formatter.formatRecentPlaytime(null));

        UserStatisticsOutputData.GameStatData justStarted =
                new UserStatisticsOutputData.GameStatData("Start", 0, 1, null);
        UserStatisticsOutputData.GameStatData learning =
                new UserStatisticsOutputData.GameStatData("Learn", 0, 3, null);
        UserStatisticsOutputData.GameStatData progressing =
                new UserStatisticsOutputData.GameStatData("Progress", 0, 10, null);
        UserStatisticsOutputData.GameStatData nearingEnd =
                new UserStatisticsOutputData.GameStatData("End", 0, 20, null);
        UserStatisticsOutputData.GameStatData trophyHunting =
                new UserStatisticsOutputData.GameStatData("Trophy", 0, 80, null);

        assertEquals("Just downloaded, huh?", formatter.formatRecentPlaytime(justStarted));
        assertEquals("Barely past the tutorial.", formatter.formatRecentPlaytime(learning));
        assertEquals("Story's getting good?", formatter.formatRecentPlaytime(progressing));
        assertEquals("Finished the main story?", formatter.formatRecentPlaytime(nearingEnd));
        assertEquals("Do we have a trophy hunter?", formatter.formatRecentPlaytime(trophyHunting));
    }

    @Test
    void playtimeDistributionLabels_areStable() {
        assertArrayEquals(new String[]{"0-5 hrs", "5-15 hrs", "15-30 hrs", "30-60 hrs", "60-150 hrs", "150+ hrs"},
                formatter.playtimeDistributionLabels());
    }
}
