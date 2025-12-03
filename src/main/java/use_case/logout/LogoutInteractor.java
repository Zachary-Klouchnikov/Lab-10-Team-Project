package use_case.logout;

import entity.SessionManager;

public class LogoutInteractor implements LogoutInputBoundary {
    private final LogoutOutputBoundary logoutOutputBoundary;

    public LogoutInteractor(LogoutOutputBoundary logoutOutputBoundary) {
        this.logoutOutputBoundary = logoutOutputBoundary;
    }

    @Override
    public void execute() {
        SessionManager.getInstance().clearSession();
        logoutOutputBoundary.prepareSuccessView();
    }
}
