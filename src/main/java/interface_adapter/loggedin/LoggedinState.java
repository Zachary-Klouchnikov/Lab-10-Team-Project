package interface_adapter.loggedin;

import entity.User;

public class LoggedinState {
    private User user = null;
    private String errorMessage = ""; 

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
