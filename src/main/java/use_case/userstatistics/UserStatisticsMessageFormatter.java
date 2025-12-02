package use_case.userstatistics;

import use_case.userstatistics.UserStatisticsOutputData.GameStatData;

/**
 * Formats user-facing strings for the user statistics use case.
 * Kept separate from the interactor to avoid coupling business logic to presentation concerns.
 */
public interface UserStatisticsMessageFormatter {
    String formatTotalPlaytime(int totalPlaytimeHours);

    String formatMostPlayed(GameStatData mostPlayed);

    String formatRecentPlaytime(GameStatData mostRecent);

    String[] playtimeDistributionLabels();
}
