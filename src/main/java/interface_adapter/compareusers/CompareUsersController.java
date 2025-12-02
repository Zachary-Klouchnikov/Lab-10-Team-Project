package interface_adapter.compareusers;

import entity.User;
import use_case.compareusers.CompareUsersInputBoundary;

public class CompareUsersController {
    private final CompareUsersInputBoundary inputBoundary;

    public CompareUsersController(CompareUsersInputBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
    }

    public void compare(User loggedInUser, User comparisonUser) {
        inputBoundary.compare(loggedInUser, comparisonUser);
    }

    public void backToLoggedIn(User loggedInUser) {
        inputBoundary.backToLoggedIn(loggedInUser);
    }
}
