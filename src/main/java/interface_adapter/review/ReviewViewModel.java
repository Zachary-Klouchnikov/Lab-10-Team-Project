package interface_adapter.review;

import interface_adapter.ViewModel;
import interface_adapter.auth.AuthState;

import java.util.ArrayList;

public class ReviewViewModel extends ViewModel<ReviewState> {
    public ReviewViewModel() {
        super("Reviews");
        this.setState(new ReviewState());
    }
}
