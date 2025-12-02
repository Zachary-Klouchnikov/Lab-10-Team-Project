package use_case.userstatistics;

import entity.User;

public class UserStatisticsInteractor implements UserStatisticsInputBoundary {
    private final UserStatisticsOutputBoundary outputBoundary;
    private final UserStatisticsCalculator calculator;

    public UserStatisticsInteractor(UserStatisticsOutputBoundary outputBoundary) {
        this(outputBoundary, new UserStatisticsCalculator());
    }

    public UserStatisticsInteractor(UserStatisticsOutputBoundary outputBoundary, UserStatisticsCalculator calculator) {
        this.outputBoundary = outputBoundary;
        this.calculator = calculator;
    }

    @Override
    public void execute(UserStatisticsInputData inputData) {
        try {
            User user = inputData.getUser();
            if (user == null) {
                outputBoundary.prepareFailureView("User not found");
                return;
            }

            UserStatisticsOutputData outputData = calculator.calculate(user);
            outputBoundary.prepareSuccessView(outputData);

        } catch (Exception e) {
            outputBoundary.prepareFailureView("Failed to load statistics: " + e.getMessage());
        }
    }
}
