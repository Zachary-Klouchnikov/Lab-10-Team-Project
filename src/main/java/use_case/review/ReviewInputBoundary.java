package use_case.review;

import entity.User;

public interface ReviewInputBoundary {
    public void execute(User user);
}
