package use_case.userstatistics;

public interface UserStatisticsOutputBoundary {
    void prepareSuccessView(UserStatisticsOutputData outputData);
    void prepareFailureView(String errorMessage);
}
