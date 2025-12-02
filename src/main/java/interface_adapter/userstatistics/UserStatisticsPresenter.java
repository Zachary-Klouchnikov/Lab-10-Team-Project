package interface_adapter.userstatistics;

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
        state.setStatistics(outputData);
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
}
