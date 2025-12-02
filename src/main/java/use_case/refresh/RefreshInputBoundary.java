package use_case.refresh;

import interface_adapter.loggedin.LoggedinState;

public interface RefreshInputBoundary {
    public void execute(LoggedinState state);
}
