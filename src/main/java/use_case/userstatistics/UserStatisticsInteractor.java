package use_case.userstatistics;

import entity.Game;
import entity.User;
import use_case.userstatistics.UserStatisticsOutputData.GameStatData;
import use_case.userstatistics.UserStatisticsOutputData.PlaytimePoint;

import java.util.List;
import java.util.Objects;

public class UserStatisticsInteractor implements UserStatisticsInputBoundary {
    private final UserStatisticsOutputBoundary outputBoundary;
    private final UserStatisticsMessageFormatter messageFormatter;
    private final UserStatisticsCalculator calculator;

    public UserStatisticsInteractor(UserStatisticsOutputBoundary outputBoundary) {
        this(outputBoundary, new DefaultUserStatisticsMessageFormatter(), new UserStatisticsCalculator());
    }

    public UserStatisticsInteractor(UserStatisticsOutputBoundary outputBoundary,
                                    UserStatisticsMessageFormatter messageFormatter,
                                    UserStatisticsCalculator calculator) {
        this.outputBoundary = Objects.requireNonNull(outputBoundary, "outputBoundary");
        this.messageFormatter = Objects.requireNonNull(messageFormatter, "messageFormatter");
        this.calculator = Objects.requireNonNull(calculator, "calculator");
    }

    @Override
    public void execute(UserStatisticsInputData inputData) {
        try {
            if (inputData == null) {
                outputBoundary.prepareFailureView("No input provided");
                return;
            }

            User user = inputData.getUser();
            if (user == null) {
                outputBoundary.prepareFailureView("User not found");
                return;
            }

            List<Game> games = calculator.sanitize(user.getLibrary());

            int totalPlaytimeHours = calculator.totalPlaytimeHours(games);
            GameStatData mostPlayed = calculator.mostPlayedGame(games);
            List<GameStatData> topFive = calculator.topByPlaytime(games, 5);
            int[] distributionCounts = calculator.playtimeDistribution(games);
            String[] distributionLabels = messageFormatter.playtimeDistributionLabels();
            GameStatData mostRecent = calculator.mostRecentGame(games);
            int totalRecentMinutes = calculator.totalRecentPlaytimeMinutes(games);
            List<GameStatData> topFiveRecent = calculator.topByRecentPlaytime(games, 5);
            List<PlaytimePoint> scatterData = calculator.scatterPlot(games);
            List<GameStatData> oldFavorites = calculator.oldFavorites(games, 5);
            List<GameStatData> unplayed = calculator.unplayed(games, 5);

            UserStatisticsOutputData outputData = new UserStatisticsOutputData(
                    user.getUsername(),
                    user.getId(),
                    user.getImage(),
                    totalPlaytimeHours,
                    messageFormatter.formatTotalPlaytime(totalPlaytimeHours),
                    mostPlayed,
                    messageFormatter.formatMostPlayed(mostPlayed),
                    topFive,
                    distributionCounts,
                    distributionLabels,
                    mostRecent,
                    totalRecentMinutes,
                    messageFormatter.formatRecentPlaytime(mostRecent),
                    topFiveRecent,
                    scatterData,
                    oldFavorites,
                    unplayed
            );

            outputBoundary.prepareSuccessView(outputData);

        } catch (IllegalArgumentException e) {
            outputBoundary.prepareFailureView(e.getMessage());
        }
    }
}
