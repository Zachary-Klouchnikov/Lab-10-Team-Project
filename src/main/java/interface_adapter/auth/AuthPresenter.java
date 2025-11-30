package interface_adapter.auth;

import use_case.auth.AuthOutputBoundary;
import use_case.auth.AuthOutputData;
import interface_adapter.ViewManagerModel;
import entity.User;

public class AuthPresenter implements AuthOutputBoundary {
    private final AuthViewModel authViewModel;
    private final ViewManagerModel viewManagerModel;

    public AuthPresenter(AuthViewModel avm, ViewManagerModel vmm) {
        this.authViewModel = avm;
        this.viewManagerModel = vmm;
    }

    @Override
    public void prepareSuccessView(AuthOutputData outputData) {
        User user = outputData.getUser();

        // Pass the user object into the info panels.
        // this.viewManagerModel.setState(); <-- Change to info panels.
        this.viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailureView(String errorMessage) {
        final AuthState state = authViewModel.getState();
        state.setErrorMessage(errorMessage);
        authViewModel.firePropertyChange();
    }
}
