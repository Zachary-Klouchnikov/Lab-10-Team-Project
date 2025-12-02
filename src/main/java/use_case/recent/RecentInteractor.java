package use_case.recent;

import data_access.UserDataAccessObject;
import entity.Game;
import entity.User;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.Icon;

public class RecentInteractor implements RecentInputBoundary {
    private RecentOutputBoundary outputBoundary;
    private UserDataAccessObject userDAO;
    
    public RecentInteractor(RecentOutputBoundary outputBoundary, UserDataAccessObject userDAO) {
        this.outputBoundary = outputBoundary; 
        this.userDAO = userDAO;
    }

    @Override
    public void execute(RecentInputData data) {
        long id = data.getId();
        User user = userDAO.getUser();

        if (id != user.getId()) {
            outputBoundary.prepareFailureView("Issue with fetching recent data!");
        }

        List<JLabel> list = getRecentPlayedGames(user);
        for (User friend : user.getFriends()) {
            list.addAll(getRecentPlayedGames(friend));
        }

        outputBoundary.prepareSuccessView(new RecentOutputData(list));
    }

    private List<JLabel> getRecentPlayedGames(User user) {
        List<JLabel> out = new ArrayList<>();
        for (Game g : user.getLibrary()) {
            if (g.getRecentPlaytime() > 0) {
                JLabel label = user.getImage();
                label.setText(String.format("%s has recently played: %s", user.getUsername(), g.getTitle()));
                out.add(label);
            }
        }

        return out;
    }
}
