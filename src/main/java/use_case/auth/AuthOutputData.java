package use_case.auth;

import entity.User;

public class AuthOutputData {
    private final User user;

    public AuthOutputData(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }
}
