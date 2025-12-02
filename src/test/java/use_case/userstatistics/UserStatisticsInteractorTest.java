package use_case.userstatistics;

import entity.Game;
import entity.User;
import org.junit.jupiter.api.Test;

import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class UserStatisticsInteractorTest {

    private User user() {
        List<Game> games = new ArrayList<>();
        games.add(new Game(1L, "Test", 120, "hash", 0));
        return new User(7L, "tester", new ArrayList<>(), games, "profile");
    }

    @Test
    void executeSuccessCallsOutputBoundary() {
        AtomicReference<UserStatisticsOutputData> successRef = new AtomicReference<>();
        AtomicReference<String> failureRef = new AtomicReference<>();

        UserStatisticsOutputBoundary boundary = new UserStatisticsOutputBoundary() {
            @Override
            public void prepareSuccessView(UserStatisticsOutputData outputData) {
                successRef.set(outputData);
            }

            @Override
            public void prepareFailureView(String errorMessage) {
                failureRef.set(errorMessage);
            }
        };

        UserStatisticsInteractor interactor = new UserStatisticsInteractor(boundary);
        interactor.execute(new UserStatisticsInputData(user()));

        assertNotNull(successRef.get());
        assertNull(failureRef.get());
        assertEquals("tester", successRef.get().getUsername());
    }

    @Test
    void executeHandlesMissingInput() {
        AtomicReference<String> failureRef = new AtomicReference<>();

        UserStatisticsOutputBoundary boundary = new UserStatisticsOutputBoundary() {
            @Override
            public void prepareSuccessView(UserStatisticsOutputData outputData) { }

            @Override
            public void prepareFailureView(String errorMessage) {
                failureRef.set(errorMessage);
            }
        };

        UserStatisticsInteractor interactor = new UserStatisticsInteractor(boundary, new UserStatisticsCalculator());

        interactor.execute(null);
        assertEquals("No input provided", failureRef.get());

        failureRef.set(null);
        interactor.execute(new UserStatisticsInputData(null));
        assertEquals("User not found", failureRef.get());
    }
}
