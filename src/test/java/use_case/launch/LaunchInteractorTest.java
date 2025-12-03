package use_case.launch;

import data_access.SteamGameLauncher;
import entity.Game;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for LaunchInteractor
 */
public class LaunchInteractorTest {

    @Test
    void execute_withNullGame_showsGameNotFoundFailure() {
        // Arrange
        SteamGameLauncher launcher = new SteamGameLauncher();
        TestLaunchPresenter presenter = new TestLaunchPresenter();
        LaunchInteractor interactor = new LaunchInteractor(launcher, presenter);

        // Act
        interactor.execute(null);

        // Assert
        assertTrue(presenter.gameNotFoundCalled, "Expected game-not-found failure to be shown");
        assertEquals("Game not found.", presenter.gameNotFoundMessage);

        // launcher should never be called
        assertFalse(launcher.launchCalled, "Launcher should not be called when game is null");
    }

    @Test
    void execute_withValidGame_andLaunchSucceeds_showsSuccessMessage() {
        // Arrange
        SteamGameLauncher launcher = new SteamGameLauncher();
        TestLaunchPresenter presenter = new TestLaunchPresenter();
        LaunchInteractor interactor = new LaunchInteractor(launcher, presenter);

        Game game = createGame(1934680, "Test Game");

        // Act
        interactor.execute(game);

        // Assert
        assertTrue(launcher.launchCalled, "Launcher should be called");

        assertTrue(presenter.launchSuccessCalled, "Expected success view to be shown");
        assertEquals("Launching Test Game", presenter.launchSuccessMessage);
        assertFalse(presenter.launchFailureCalled, "Failure view should not be called on success");
        assertFalse(presenter.gameNotFoundCalled, "Game-not-found should not be called for a valid game");
    }

    @Test
    void execute_withValidGame_andLaunchFails_showsFailureMessage() {
        // Arrange
        SteamGameLauncher launcher = new SteamGameLauncher();
        TestLaunchPresenter presenter = new TestLaunchPresenter();
        LaunchInteractor interactor = new LaunchInteractor(launcher, presenter);

        Game game = createGame(1934680, "Another Game");

        // Act
        interactor.execute(game);

        // Assert
        assertTrue(launcher.launchCalled, "Launcher should be called");

        assertTrue(presenter.launchFailureCalled, "Expected failure view to be shown");
        assertEquals("Failed to launch Another Game", presenter.launchFailureMessage);
        assertFalse(presenter.launchSuccessCalled, "Success view should not be called on failure");
        assertFalse(presenter.gameNotFoundCalled, "Game-not-found should not be called for a valid game");
    }

    /**
     * Helper to create a Game instance for testing.
     */
    private Game createGame(long id, String title) {
        return new Game(id, title, 0, "", 0) {
            @Override
            public long getId() {
                return id;
            }

            @Override
            public String getTitle() {
                return title;
            }
        };
    }

    /**
     * Simple test presenter to capture which methods are called and with what messages.
     */
    private static class TestLaunchPresenter implements LaunchOutputBoundary {

        boolean launchSuccessCalled = false;
        boolean launchFailureCalled = false;
        boolean gameNotFoundCalled = false;

        String launchSuccessMessage;
        String launchFailureMessage;
        String gameNotFoundMessage;

        @Override
        public void showLaunchSuccess(String message) {
            this.launchSuccessCalled = true;
            this.launchSuccessMessage = message;
        }

        @Override
        public void showLaunchFailure(String message) {
            this.launchFailureCalled = true;
            this.launchFailureMessage = message;
        }

        @Override
        public void showGameNotFoundFailure(String message) {
            this.gameNotFoundCalled = true;
            this.gameNotFoundMessage = message;
        }
    }
}
