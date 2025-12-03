package use_case.userstatistics;

import entity.Game;
import entity.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserStatisticsInteractorTest {

    private static class CapturingOutputBoundary implements UserStatisticsOutputBoundary {
        UserStatisticsOutputData successData;
        String failureMessage;

        @Override
        public void prepareSuccessView(UserStatisticsOutputData outputData) {
            this.successData = outputData;
        }

        @Override
        public void prepareFailureView(String errorMessage) {
            this.failureMessage = errorMessage;
        }
    }

    private static class StubCalculator extends UserStatisticsCalculator {
        private final UserStatisticsOutputData outputData;
        private final RuntimeException toThrow;

        StubCalculator(UserStatisticsOutputData outputData) {
            this.outputData = outputData;
            this.toThrow = null;
        }

        StubCalculator(RuntimeException toThrow) {
            this.outputData = null;
            this.toThrow = toThrow;
        }

        @Override
        public UserStatisticsOutputData calculate(User user) {
            if (toThrow != null) {
                throw toThrow;
            }
            return outputData;
        }
    }

    private User user() {
        return new User(1L, "Alice", new ArrayList<>(), new ArrayList<Game>(), "pic");
    }

    private UserStatisticsOutputData emptyOutput() {
        return new UserStatisticsOutputData(
                "Alice",
                1L,
                null,
                0,
                "",
                null,
                "",
                List.of(),
                new int[6],
                new String[6],
                null,
                0,
                "",
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    @Test
    void execute_withValidInput_callsSuccess() {
        CapturingOutputBoundary presenter = new CapturingOutputBoundary();
        UserStatisticsOutputData output = emptyOutput();
        UserStatisticsInteractor interactor = new UserStatisticsInteractor(presenter, new StubCalculator(output));

        interactor.execute(new UserStatisticsInputData(user()));

        assertSame(output, presenter.successData);
        assertNull(presenter.failureMessage);
    }

    @Test
    void execute_withNullInput_callsFailure() {
        CapturingOutputBoundary presenter = new CapturingOutputBoundary();
        UserStatisticsInteractor interactor = new UserStatisticsInteractor(presenter, new StubCalculator(emptyOutput()));

        interactor.execute(null);

        assertEquals("No input provided", presenter.failureMessage);
        assertNull(presenter.successData);
    }

    @Test
    void execute_withNullUser_callsFailure() {
        CapturingOutputBoundary presenter = new CapturingOutputBoundary();
        UserStatisticsInteractor interactor = new UserStatisticsInteractor(presenter, new StubCalculator(emptyOutput()));

        interactor.execute(new UserStatisticsInputData(null));

        assertEquals("User not found", presenter.failureMessage);
        assertNull(presenter.successData);
    }

    @Test
    void execute_whenCalculatorThrows_callsFailure() {
        CapturingOutputBoundary presenter = new CapturingOutputBoundary();
        UserStatisticsInteractor interactor =
                new UserStatisticsInteractor(presenter, new StubCalculator(new RuntimeException("boom")));

        interactor.execute(new UserStatisticsInputData(user()));

        assertEquals("Failed to load statistics: boom", presenter.failureMessage);
        assertNull(presenter.successData);
    }
}
