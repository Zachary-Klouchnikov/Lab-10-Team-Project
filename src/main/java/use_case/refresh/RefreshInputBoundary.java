package use_case.refresh;

import entity.User;

public interface RefreshInputBoundary {
    public void execute(User user);
}
