package interface_adapter.review;

public class ReviewState {
    private String errorMessage = "";

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String msg) {
        this.errorMessage = msg;
    }
}
