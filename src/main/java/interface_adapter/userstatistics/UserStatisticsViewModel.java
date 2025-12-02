package interface_adapter.userstatistics;

import interface_adapter.ViewModel;

public class UserStatisticsViewModel extends ViewModel<UserStatisticsState> {
    public UserStatisticsViewModel() {
        super("userstatistics");
        this.setState(new UserStatisticsState());
    }
}
