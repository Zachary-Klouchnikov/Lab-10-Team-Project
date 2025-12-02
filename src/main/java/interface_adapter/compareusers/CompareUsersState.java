package interface_adapter.compareusers;

import entity.User;

public class CompareUsersState {
    private User loggedInUser;
    private User comparisonUser;
    private String errorMessage = "";

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public User getComparisonUser() {
        return comparisonUser;
    }

    public void setComparisonUser(User comparisonUser) {
        this.comparisonUser = comparisonUser;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
