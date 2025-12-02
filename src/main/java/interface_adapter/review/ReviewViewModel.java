package interface_adapter.review;

import interface_adapter.ViewModel;
import interface_adapter.auth.AuthState;

public class ReviewViewModel extends ViewModel<ReviewState> {
    public ReviewViewModel() {
        super("Reviews");
        this.setState(new ReviewState());
    }
}
