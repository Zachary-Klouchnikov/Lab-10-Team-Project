package use_case.refresh;

import entity.User;

public interface RefreshOutputBoundary {
    public void prepareSuccessView(User user);

    public void prepareFailureView(String errMsg);
}
