package use_case.auth;

public interface AuthOutputBoundary {
    void prepareSuccessView(AuthOutputData outputdata);

    void prepareFailureView(String errorMessage);
}
