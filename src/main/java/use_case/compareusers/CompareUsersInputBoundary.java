package use_case.compareusers;

import entity.User;

public interface CompareUsersInputBoundary {
    void compare(User loggedInUser, User comparisonUser);
    void backToLoggedIn(User loggedInUser);
}
