package use_case.refresh;

import entity.User;
import entity.SessionManager;
import data_access.UserDataAccessObject;

public class RefreshInteractor implements RefreshInputBoundary {
    private RefreshOutputBoundary refreshOutputBoundary;
    private UserDataAccessObject userDAO;

    public RefreshInteractor(RefreshOutputBoundary refreshOutputBoundary) {
        this.refreshOutputBoundary = refreshOutputBoundary;
        this.userDAO = new UserDataAccessObject();
    }

    @Override
    public void execute(User user) {
        if (!SessionManager.getInstance().hasActiveSession()) {
            refreshOutputBoundary.prepareFailureView("Session Expired!");
        }

        User newUser = userDAO.get(user.getId());
        SessionManager.getInstance().refreshSession();
        refreshOutputBoundary.prepareSuccessView(newUser);
    }
}
