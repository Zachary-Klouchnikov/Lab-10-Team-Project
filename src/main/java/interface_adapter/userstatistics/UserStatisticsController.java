package interface_adapter.userstatistics;

import entity.User;
import use_case.userstatistics.UserStatisticsInputBoundary;
import use_case.userstatistics.UserStatisticsInputData;

public class UserStatisticsController {
    private final UserStatisticsInputBoundary inputBoundary;

    public UserStatisticsController(UserStatisticsInputBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
    }

    public void loadStatistics(User user) {
        UserStatisticsInputData inputData = new UserStatisticsInputData(user);
        inputBoundary.execute(inputData);
    }
}
