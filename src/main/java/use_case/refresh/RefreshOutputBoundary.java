package use_case.refresh;

import interface_adapter.loggedin.LoggedinState;

public interface RefreshOutputBoundary {
    public void prepareSuccessView(LoggedinState state);

    public void prepareFailureView(String errMsg);
}
