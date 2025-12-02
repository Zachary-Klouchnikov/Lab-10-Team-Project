package use_case.launch;

public interface LaunchOutputBoundary {
    void showLaunchSuccess(String message);
    void showSelectionFailure(String message);
    void showGameNotFoundFailure(String message);
    void showLaunchFailure(String message);
}
