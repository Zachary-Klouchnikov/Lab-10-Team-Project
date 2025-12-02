package use_case.compareusers;

import entity.User;

public class CompareUsersOutputData {
    private final User loggedInUser;
    private final User comparisonUser;

    public CompareUsersOutputData(User loggedInUser, User comparisonUser) {
        this.loggedInUser = loggedInUser;
        this.comparisonUser = comparisonUser;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public User getComparisonUser() {
        return comparisonUser;
    }
}
