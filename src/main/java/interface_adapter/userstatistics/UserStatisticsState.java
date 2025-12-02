package interface_adapter.userstatistics;

import use_case.userstatistics.UserStatisticsOutputData;

public class UserStatisticsState {
    private UserStatisticsOutputData statistics = null;
    private String errorMessage = "";

    public UserStatisticsOutputData getStatistics() {
        return statistics;
    }

    public void setStatistics(UserStatisticsOutputData statistics) {
        this.statistics = statistics;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
