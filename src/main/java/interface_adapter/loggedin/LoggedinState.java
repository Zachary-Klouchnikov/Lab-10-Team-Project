package interface_adapter.loggedin;

import entity.User;

public class LoggedinState {
    private User user = null;
    // Only way for this to appear is if the authentication process is somehow skipped.
    private String errorMessage = "Authentication process did not succeed."; 

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public String getError() {
        return this.errorMessage;
    }

    public void setError(String errMsg) {
        this.errorMessage = errMsg;
    }
}
