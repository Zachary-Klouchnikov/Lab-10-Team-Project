package use_case.userstatistics;

import entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserStatisticsInputDataTest {

    @Test
    void storesUser() {
        User user = new User(1L, "u", java.util.List.of(), java.util.List.of(), "hash");
        UserStatisticsInputData input = new UserStatisticsInputData(user);
        assertEquals(user, input.getUser());
    }
}
