package use_case.userstatistics;

import entity.User;

public class UserStatisticsInputData {
    private final User user;

    public UserStatisticsInputData(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
