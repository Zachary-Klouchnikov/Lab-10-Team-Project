package use_case.userstatistics;

import use_case.userstatistics.UserStatisticsOutputData.GameStatData;

/**
 * Default formatting rules for user-facing statistics messages.
 */
public class DefaultUserStatisticsMessageFormatter implements UserStatisticsMessageFormatter {

    @Override
    public String formatTotalPlaytime(int hours) {
        if (hours < 10) {
            return "New account?";
        } else if (hours < 100) {
            return "Getting into gaming...";
        } else if (hours < 500) {
            return "Gaming as a hobby.";
        }
        return "We have a true Gamer in our midst...";
    }

    @Override
    public String formatMostPlayed(GameStatData mostPlayed) {
        if (mostPlayed == null) {
            return "";
        }
        int hours = mostPlayed.getPlaytimeHours();
        if (hours < 3) {
            return "Still enough time for a refund.";
        } else if (hours < 100) {
            return "I can see it's a favourite.";
        } else if (hours < 200) {
            return "Getting really into this game, huh?";
        }
        return "You must really love this game...";
    }

    @Override
    public String formatRecentPlaytime(GameStatData mostRecent) {
        if (mostRecent == null) {
            return "";
        }
        int hours = mostRecent.getRecentPlaytimeHours();
        if (hours < 2) {
            return "Just downloaded, huh?";
        } else if (hours < 5) {
            return "Barely past the tutorial.";
        } else if (hours < 15) {
            return "Story's getting good?";
        } else if (hours < 60) {
            return "Finished the main story?";
        }
        return "Do we have a trophy hunter?";
    }

    @Override
    public String[] playtimeDistributionLabels() {
        return new String[]{"0-5 hrs", "5-15 hrs", "15-30 hrs", "30-60 hrs", "60-150 hrs", "150+ hrs"};
    }
}
