package interface_adapter.userstatistics;

public class UserStatisticsState {
    private UserStatisticsViewData statistics = null;
    private String errorMessage = "";

    public UserStatisticsViewData getStatistics() {
        return statistics;
    }

    public void setStatistics(UserStatisticsViewData statistics) {
        this.statistics = statistics;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
