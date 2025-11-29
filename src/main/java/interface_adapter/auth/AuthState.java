package interface_adapter.auth;

public class AuthState {
    private String errorMessage = "";

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String msg) {
        this.errorMessage = msg;
    }
}
