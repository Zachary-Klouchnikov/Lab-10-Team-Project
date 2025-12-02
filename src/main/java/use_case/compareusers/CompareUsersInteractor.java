package use_case.compareusers;

import entity.User;

public class CompareUsersInteractor implements CompareUsersInputBoundary {
    private final CompareUsersOutputBoundary outputBoundary;

    public CompareUsersInteractor(CompareUsersOutputBoundary outputBoundary) {
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void compare(User loggedInUser, User comparisonUser) {
        if (loggedInUser == null) {
            outputBoundary.presentFailure("No logged in user available for comparison.");
            return;
        }
        if (comparisonUser == null) {
            outputBoundary.presentFailure("Please select a user to compare.");
            return;
        }
        outputBoundary.presentComparison(new CompareUsersOutputData(loggedInUser, comparisonUser));
    }

    @Override
    public void backToLoggedIn(User loggedInUser) {
        if (loggedInUser == null) {
            outputBoundary.presentFailure("No logged in user available.");
            return;
        }
        outputBoundary.presentLoggedIn(loggedInUser);
    }
}
