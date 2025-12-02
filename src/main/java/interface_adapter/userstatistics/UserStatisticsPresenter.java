package interface_adapter.userstatistics;

import java.util.List;

import use_case.userstatistics.UserStatisticsOutputBoundary;
import use_case.userstatistics.UserStatisticsOutputData;

public class UserStatisticsPresenter implements UserStatisticsOutputBoundary {
    private final UserStatisticsViewModel viewModel;

    public UserStatisticsPresenter(UserStatisticsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(UserStatisticsOutputData outputData) {
        UserStatisticsState state = viewModel.getState();
        state.setStatistics(mapToViewData(outputData));
        state.setErrorMessage("");
        viewModel.firePropertyChange();
    }

    @Override
    public void prepareFailureView(String errorMessage) {
        UserStatisticsState state = viewModel.getState();
        state.setStatistics(null);
        state.setErrorMessage(errorMessage);
        viewModel.firePropertyChange();
    }

    private UserStatisticsViewData mapToViewData(UserStatisticsOutputData outputData) {
        UserStatisticsViewData.GameStatViewData mostPlayed = mapGame(outputData.getMostPlayedGame());
        UserStatisticsViewData.GameStatViewData mostRecent = mapGame(outputData.getMostRecentGame());
        int mostRecentMinutes = mostRecent == null ? 0 : mostRecent.getRecentPlaytimeHours() * 60;
        int otherRecentMinutes = Math.max(0, outputData.getTotalRecentPlaytimeMinutes() - mostRecentMinutes);

        return new UserStatisticsViewData(
                outputData.getUsername(),
                String.valueOf(outputData.getSteamId()),
                outputData.getProfileImage(),
                outputData.getTotalPlaytimeHours(),
                outputData.getTotalPlaytimeMessage(),
                mostPlayed,
                outputData.getMostPlayedMessage(),
                outputData.getTopFiveGames() == null
                        ? List.of()
                        : outputData.getTopFiveGames().stream().map(this::mapGame).toList(),
                outputData.getPlaytimeDistributionCounts(),
                outputData.getPlaytimeDistributionLabels(),
                mostRecent,
                mostRecentMinutes,
                otherRecentMinutes,
                outputData.getRecentPlaytimeMessage(),
                outputData.getTopFiveRecentGames() == null
                        ? List.of()
                        : outputData.getTopFiveRecentGames().stream().map(this::mapGame).toList(),
                outputData.getScatterPlotData() == null
                        ? List.of()
                        : outputData.getScatterPlotData().stream().map(p ->
                        new UserStatisticsViewData.PlaytimePointViewData(p.getTotalHours(), p.getRecentHours())
                ).toList(),
                outputData.getOldFavorites() == null
                        ? List.of()
                        : outputData.getOldFavorites().stream().map(this::mapGame).toList(),
                outputData.getUnplayedGames() == null
                        ? List.of()
                        : outputData.getUnplayedGames().stream().map(this::mapGame).toList()
        );
    }

    private UserStatisticsViewData.GameStatViewData mapGame(UserStatisticsOutputData.GameStatData data) {
        if (data == null) {
            return null;
        }
        return new UserStatisticsViewData.GameStatViewData(
                data.getTitle(),
                data.getPlaytimeHours(),
                data.getRecentPlaytimeHours(),
                data.getImage()
        );
    }
}
