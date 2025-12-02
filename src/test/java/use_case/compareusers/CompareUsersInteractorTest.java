package use_case.compareusers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import entity.User;
import java.util.ArrayList;

public class CompareUsersInteractorTest {
    @Test
    void successCompareTest() {
        // Just need bare bones users.
        User user1 = new User(1, null, null, new ArrayList<>(), null);
        User user2 = new User(2, null, null, new ArrayList<>(), null);
    
        CompareUsersOutputBoundary successPresenter = new CompareUsersOutputBoundary() {
            @Override
            public void presentComparison(CompareUsersOutputData outputData) {
                assertEquals(user1.getId(), outputData.getLoggedInUser().getId());
                assertEquals(user2.getId(), outputData.getComparisonUser().getId());
            }
            @Override
            public void presentLoggedIn(User loggedInUser) {
                fail("Unreachable");
            }
            @Override
            public void presentFailure(String errorMessage) {
                fail("Unreachable");
            }
        };

        CompareUsersInputBoundary interactor = new CompareUsersInteractor(successPresenter);
        interactor.compare(user1, user2);
    }   

    @Test
    void noComparedUserTest() {
        // Just need bare bones users.
        User user = new User(1, null, null, new ArrayList<>(), null);
    
        CompareUsersOutputBoundary successPresenter = new CompareUsersOutputBoundary() {
            @Override
            public void presentComparison(CompareUsersOutputData outputData) {
                fail("Unreachable");
            }
            @Override
            public void presentLoggedIn(User loggedInUser) {
                fail("Unreachable");
            }
            @Override
            public void presentFailure(String errorMessage) {
                assertTrue(errorMessage != null && !errorMessage.isEmpty());
                assertEquals("Please select a user to compare.", errorMessage);
            }
        };

        CompareUsersInputBoundary interactor = new CompareUsersInteractor(successPresenter);
        interactor.compare(user, null);
    }   

    @Test
    void noLoggedinUserCompareTest() {
        // Just need bare bones users.
        User user = new User(1, null, null, new ArrayList<>(), null);
    
        CompareUsersOutputBoundary successPresenter = new CompareUsersOutputBoundary() {
            @Override
            public void presentComparison(CompareUsersOutputData outputData) {
                fail("Unreachable");
            }
            @Override
            public void presentLoggedIn(User loggedInUser) {
                fail("Unreachable");
            }
            @Override
            public void presentFailure(String errorMessage) {
                assertTrue(errorMessage != null && !errorMessage.isEmpty());
                assertEquals("No logged in user available for comparison.", errorMessage);
            }
        };

        CompareUsersInputBoundary interactor = new CompareUsersInteractor(successPresenter);
        interactor.compare(null, user);
    }   

    @Test
    void returnToLoggedinTest() {
        // Just need bare bones users.
        User user = new User(1, null, null, new ArrayList<>(), null);
    
        CompareUsersOutputBoundary successPresenter = new CompareUsersOutputBoundary() {
            @Override
            public void presentComparison(CompareUsersOutputData outputData) {
                fail("Unreachable");
            }
            @Override
            public void presentLoggedIn(User loggedInUser) {
                assertEquals(user.getId(), loggedInUser.getId());
            }
            @Override
            public void presentFailure(String errorMessage) {
                fail("Unreachable");
            }
        };

        CompareUsersInputBoundary interactor = new CompareUsersInteractor(successPresenter);
        interactor.backToLoggedIn(user);
    }   

    @Test
    void noLoggedinUserReturnTest() {
        CompareUsersOutputBoundary successPresenter = new CompareUsersOutputBoundary() {
            @Override
            public void presentComparison(CompareUsersOutputData outputData) {
                fail("Unreachable");
            }
            @Override
            public void presentLoggedIn(User loggedInUser) {
                fail("Unreachable");
            }
            @Override
            public void presentFailure(String errorMessage) {
                assertTrue(errorMessage != null && !errorMessage.isEmpty());
                assertEquals("No logged in user available.", errorMessage);
            }
        };

        CompareUsersInputBoundary interactor = new CompareUsersInteractor(successPresenter);
        interactor.backToLoggedIn(null);
    }   
}
