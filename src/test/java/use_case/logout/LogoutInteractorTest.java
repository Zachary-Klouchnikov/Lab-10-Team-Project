package use_case.logout;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import entity.User;
import entity.SessionManager;

public class LogoutInteractorTest {
    // This should literally not fail.
    @Test 
    void successTest() {
        User user = new User(0, "", new ArrayList<>(), new ArrayList<>(), "");
        SessionManager.getInstance().createSession(user.getId(), user);

        LogoutOutputBoundary presenter = new LogoutOutputBoundary() {
            @Override 
            public void prepareSuccessView() {}
        };

        LogoutInputBoundary interactor = new LogoutInteractor(presenter);
        interactor.execute();
    }   
}
