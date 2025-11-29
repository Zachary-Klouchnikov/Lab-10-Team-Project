package interface_adapter.auth;

import use_case.auth.AuthInputBoundary;
import use_case.auth.AuthInputData;

public class AuthController {
    private final AuthInputBoundary inputBoundary;

    public AuthController(AuthInputBoundary boundary) {
        this.inputBoundary = boundary;
    }

    public void execute(AuthInputData data) {
        this.inputBoundary.execute(data);
    }
}
