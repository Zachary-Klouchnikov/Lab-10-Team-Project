package interface_adapter.loggedin;

import use_case.recent.*;

public class RecentController {
    private RecentInputBoundary inputBoundary;

    public RecentController(RecentInputBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
    }

    public void execute() {
        this.inputBoundary.execute();
    }
}
