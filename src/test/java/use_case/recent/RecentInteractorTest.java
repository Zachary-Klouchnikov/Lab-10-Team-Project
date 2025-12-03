package use_case.recent;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import entity.User;
import data_access.UserDataAccessObject;

public class RecentInteractorTest {
    @Test
    void successTest() {
        UserDataAccessObject userDAO = new UserDataAccessObject();
        long testid = 76561198271707701L;
        User user = userDAO.get(testid);       

        RecentOutputBoundary outputBoundary = new RecentOutputBoundary() {
            @Override 
            public void prepareSuccessView(RecentOutputData data) {
                int recentGames = 0;
                recentGames += user.getLibrary().stream().filter(g -> g.getRecentPlaytime() > 0).count();
                for (User f : user.getFriends()) {
                    recentGames += f.getLibrary().stream().filter(g -> g.getRecentPlaytime() > 0).count();
                }

                assertEquals(recentGames, data.getLabelList().size());
            }
        };

        RecentInputBoundary interactor = new RecentInteractor(outputBoundary, userDAO);
        interactor.execute();
    }
}
