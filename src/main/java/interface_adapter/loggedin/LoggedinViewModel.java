package interface_adapter.loggedin;

import interface_adapter.ViewModel;

public class LoggedinViewModel extends ViewModel<LoggedinState> {
    public LoggedinViewModel() {
        super("loggedin");
        this.setState(new LoggedinState());
    }
}
