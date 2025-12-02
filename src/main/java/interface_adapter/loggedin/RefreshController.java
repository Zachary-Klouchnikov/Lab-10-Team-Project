package interface_adapter.loggedin;

import use_case.refresh.*;

public class RefreshController {
    private RefreshInputBoundary refreshInputBoundary;
    
    public RefreshController(RefreshInputBoundary rib) {
        this.refreshInputBoundary = rib;
    }

    public void execute(LoggedinState state) {
        this.refreshInputBoundary.execute(state);
    }
}
