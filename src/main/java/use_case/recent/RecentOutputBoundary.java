package use_case.recent;

public interface RecentOutputBoundary {
    void prepareSuccessView(RecentOutputData data);

    void prepareFailureView(String errMsg);
}
