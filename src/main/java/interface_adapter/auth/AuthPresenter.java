package interface_adapter.auth;

import use_case.auth.AuthOutputBoundary;
import use_case.auth.AuthOutputData;
import interface_adapter.ViewManagerModel;
import interface_adapter.loggedin.LoggedinState;
import interface_adapter.loggedin.LoggedinViewModel;

public class AuthPresenter implements AuthOutputBoundary {
    private final AuthViewModel authViewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoggedinViewModel loggedinViewModel;

    public AuthPresenter(AuthViewModel avm, LoggedinViewModel lvm, ViewManagerModel vmm) {
        this.authViewModel = avm;
        this.loggedinViewModel = lvm;
        this.viewManagerModel = vmm;
    }

    @Override
    public void prepareSuccessView(AuthOutputData outputData) {
        LoggedinState state = loggedinViewModel.getState();
        state.setName(outputData.getName());
        state.setProfilePicture(outputData.getProfilePicture());
        state.setId(outputData.getId());
        state.setFriendLabels(outputData.getFriendLabels());
        state.setGameLabels(outputData.getGameLabels());
        this.loggedinViewModel.firePropertyChange();

        this.authViewModel.setState(new AuthState());

        this.viewManagerModel.setState(loggedinViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailureView(String errorMessage) {
        final AuthState state = authViewModel.getState();
        state.setErrorMessage(errorMessage);
        authViewModel.firePropertyChange();
    }
}
