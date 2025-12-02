package use_case.refresh;

import entity.User;
import entity.Game;
import entity.SessionManager;
import interface_adapter.loggedin.*;
import data_access.UserDataAccessObject;

public class RefreshInteractor implements RefreshInputBoundary {
    private RefreshOutputBoundary refreshOutputBoundary;
    private UserDataAccessObject userDAO;

    public RefreshInteractor(RefreshOutputBoundary refreshOutputBoundary) {
        this.refreshOutputBoundary = refreshOutputBoundary;
        this.userDAO = new UserDataAccessObject();
    }

    @Override
    public void execute(LoggedinState state) {
        if (!SessionManager.getInstance().hasActiveSession()) {
            refreshOutputBoundary.prepareFailureView("Session Expired!");
        }

        User newUser = userDAO.get(state.getId());
        SessionManager.getInstance().refreshSession();

        LoggedinState newState = new LoggedinState();
        newState.setId(newUser.getId());
        newState.setName(newUser.getUsername());
        newState.setUser(newUser);
        newState.setFriends(newUser.getFriends());
        newState.setFriendLabels(newUser.getFriends().stream().map(User::getImage).toList());
        newState.setGameLabels(newUser.getLibrary().stream().map(Game::getImage).toList());
        newState.setProfilePicture(newUser.getImage());

        refreshOutputBoundary.prepareSuccessView(newState);
    }
}
