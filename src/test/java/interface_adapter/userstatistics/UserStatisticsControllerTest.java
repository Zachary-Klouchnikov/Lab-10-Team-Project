package interface_adapter.userstatistics;

import entity.User;
import org.junit.jupiter.api.Test;
import use_case.userstatistics.UserStatisticsInputBoundary;
import use_case.userstatistics.UserStatisticsInputData;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class UserStatisticsControllerTest {

    @Test
    void loadStatisticsDelegatesToInputBoundary() {
        AtomicReference<UserStatisticsInputData> captured = new AtomicReference<>();

        UserStatisticsInputBoundary boundary = captured::set;
        User user = new User(1L, "u", java.util.List.of(), java.util.List.of(), "hash");

        UserStatisticsController controller = new UserStatisticsController(boundary);
        controller.loadStatistics(user);

        assertNotNull(captured.get());
        assertEquals(user, captured.get().getUser());
    }
}
