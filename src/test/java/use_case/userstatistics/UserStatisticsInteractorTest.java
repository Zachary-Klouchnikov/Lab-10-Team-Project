package use_case.userstatistics;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import entity.User;

public class UserStatisticsInteractorTest {
    @Test 
    void successTest() {
        User user = new User(42069, "Name", new ArrayList<>(), new ArrayList<>(), "");
        UserStatisticsInputData inputData = new UserStatisticsInputData(user);

        UserStatisticsOutputBoundary outputBoundary = new UserStatisticsOutputBoundary() {
            @Override
            public void prepareSuccessView(UserStatisticsOutputData data) {
                assertEquals(user.getId(), data.getSteamId());
                assertEquals(user.getUsername(), data.getUsername());
            }

            @Override 
            public void prepareFailureView(String errMsg) {
                fail("Unreachable");
            }
        };

        UserStatisticsInputBoundary interactor = new UserStatisticsInteractor(outputBoundary);
        interactor.execute(inputData);
    }

    @Test 
    void noUserTest() {
        UserStatisticsInputData inputData = new UserStatisticsInputData(null);

        UserStatisticsOutputBoundary outputBoundary = new UserStatisticsOutputBoundary() {
            @Override
            public void prepareSuccessView(UserStatisticsOutputData data) {
                fail("Unreachable");
            }

            @Override 
            public void prepareFailureView(String errMsg) {
                assertEquals("User not found", errMsg);
            }
        };

        UserStatisticsInputBoundary interactor = new UserStatisticsInteractor(outputBoundary);
        interactor.execute(inputData);
    }

    @Test 
    void noDataTest() {
        UserStatisticsOutputBoundary outputBoundary = new UserStatisticsOutputBoundary() {
            @Override
            public void prepareSuccessView(UserStatisticsOutputData data) {
                fail("Unreachable");
            }

            @Override 
            public void prepareFailureView(String errMsg) {
                assertEquals("No input provided", errMsg);
            }
        };

        UserStatisticsInputBoundary interactor = new UserStatisticsInteractor(outputBoundary);
        interactor.execute(null);
    }
}
