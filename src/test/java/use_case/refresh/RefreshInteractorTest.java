package use_case.refresh;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import entity.User;
import entity.SessionManager;
import interface_adapter.loggedin.LoggedinState;
import data_access.UserDataAccessObject;

public class RefreshInteractorTest {
 
    @Test
    void successTest() {
        UserDataAccessObject userDAO = new UserDataAccessObject();
        long testid = 76561198271707701L;
        User user = userDAO.get(testid);
        SessionManager.getInstance().createSession(user.getId(), user);

        LoggedinState state = new LoggedinState();
        state.setId(user.getId());
        state.setUser(user);

        RefreshOutputBoundary outputBoundary = new RefreshOutputBoundary() {
            @Override
            public void prepareSuccessView(LoggedinState state)  {
                assertEquals(user.getId(), state.getId());
            }

            @Override
            public void prepareFailureView(String message) {
                fail("Unreachable");
            }
        };

        RefreshInputBoundary interactor = new RefreshInteractor(outputBoundary, userDAO);
        interactor.execute(state);
    }

    @Test
    void failTest() {
        UserDataAccessObject userDAO = new UserDataAccessObject();
        long testid = 76561198271707701L;
        User user = userDAO.get(testid);
        SessionManager.getInstance().createSession(user.getId(), user);
        SessionManager.getInstance().clearSession();

        LoggedinState state = new LoggedinState();
        state.setId(user.getId());
        state.setUser(user);

        RefreshOutputBoundary outputBoundary = new RefreshOutputBoundary() {
            @Override
            public void prepareSuccessView(LoggedinState state)  {
                fail("Unreachable");
            }

            @Override
            public void prepareFailureView(String message) {
                assertEquals("Session Expired!", message);
            }
        };

        RefreshInputBoundary interactor = new RefreshInteractor(outputBoundary, userDAO);
        interactor.execute(state);
    }
}
