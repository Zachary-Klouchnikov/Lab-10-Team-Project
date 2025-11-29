package interface_adapter.auth;

import interface_adapter.ViewModel;

public class AuthViewModel extends ViewModel<AuthState> {
    public AuthViewModel() {
        super("authentication");
        this.setState(new AuthState());
    }
}
