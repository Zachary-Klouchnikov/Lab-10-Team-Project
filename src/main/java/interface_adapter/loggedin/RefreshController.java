package interface_adapter.loggedin;

import use_case.refresh.*;
import entity.User;

public class RefreshController {
    private RefreshInputBoundary refreshInputBoundary;
    
    public RefreshController(RefreshInputBoundary rib) {
        this.refreshInputBoundary = rib;
    }

    public void execute(User user) {
        this.refreshInputBoundary.execute(user);
    }
}
