package interface_adapter.logout;

import use_case.logout.LogoutInputBoundary;

public class LogoutController {
    private LogoutInputBoundary logoutInputBoundary;

    public LogoutController(LogoutInputBoundary logoutInputBoundary) {
        this.logoutInputBoundary = logoutInputBoundary;
    }

    public void execute() {
        this.logoutInputBoundary.execute();
    }
}