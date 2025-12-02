package use_case.userstatistics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultUserStatisticsMessageFormatterTest {

    @Test
    void totalPlaytimeMessagesCoverRanges() {
        DefaultUserStatisticsMessageFormatter formatter = new DefaultUserStatisticsMessageFormatter();
        assertEquals("New account?", formatter.formatTotalPlaytime(0));
        assertEquals("Getting into gaming...", formatter.formatTotalPlaytime(50));
        assertEquals("Gaming as a hobby.", formatter.formatTotalPlaytime(200));
        assertEquals("We have a true Gamer in our midst...", formatter.formatTotalPlaytime(600));
    }

    @Test
    void mostPlayedMessagesCoverRanges() {
        DefaultUserStatisticsMessageFormatter formatter = new DefaultUserStatisticsMessageFormatter();
        assertEquals("", formatter.formatMostPlayed(null));
        UserStatisticsOutputData.GameStatData tiny = new UserStatisticsOutputData.GameStatData("t", 2, 0, null);
        UserStatisticsOutputData.GameStatData mid = new UserStatisticsOutputData.GameStatData("m", 50, 0, null);
        UserStatisticsOutputData.GameStatData big = new UserStatisticsOutputData.GameStatData("b", 150, 0, null);

        assertEquals("Still enough time for a refund.", formatter.formatMostPlayed(tiny));
        assertEquals("I can see it's a favourite.", formatter.formatMostPlayed(mid));
        assertEquals("Getting really into this game, huh?", formatter.formatMostPlayed(big));
    }

    @Test
    void recentPlaytimeMessagesCoverRanges() {
        DefaultUserStatisticsMessageFormatter formatter = new DefaultUserStatisticsMessageFormatter();
        assertEquals("", formatter.formatRecentPlaytime(null));
        UserStatisticsOutputData.GameStatData tiny = new UserStatisticsOutputData.GameStatData("t", 0, 1, null);
        UserStatisticsOutputData.GameStatData small = new UserStatisticsOutputData.GameStatData("s", 0, 4, null);
        UserStatisticsOutputData.GameStatData mid = new UserStatisticsOutputData.GameStatData("m", 0, 10, null);
        UserStatisticsOutputData.GameStatData big = new UserStatisticsOutputData.GameStatData("b", 0, 80, null);

        assertEquals("Just downloaded, huh?", formatter.formatRecentPlaytime(tiny));
        assertEquals("Barely past the tutorial.", formatter.formatRecentPlaytime(small));
        assertEquals("Story's getting good?", formatter.formatRecentPlaytime(mid));
        assertEquals("Do we have a trophy hunter?", formatter.formatRecentPlaytime(big));
    }

    @Test
    void distributionLabelsMatchExpectedBuckets() {
        DefaultUserStatisticsMessageFormatter formatter = new DefaultUserStatisticsMessageFormatter();
        assertArrayEquals(
                new String[]{"0-5 hrs", "5-15 hrs", "15-30 hrs", "30-60 hrs", "60-150 hrs", "150+ hrs"},
                formatter.playtimeDistributionLabels()
        );
    }
}
