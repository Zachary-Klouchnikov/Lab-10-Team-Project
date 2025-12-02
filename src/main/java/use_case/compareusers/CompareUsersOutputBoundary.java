package use_case.compareusers;

import entity.User;

public interface CompareUsersOutputBoundary {
    void presentComparison(CompareUsersOutputData outputData);
    void presentLoggedIn(User loggedInUser);
    void presentFailure(String errorMessage);
}
