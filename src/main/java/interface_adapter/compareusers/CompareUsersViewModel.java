package interface_adapter.compareusers;

import interface_adapter.ViewModel;

public class CompareUsersViewModel extends ViewModel<CompareUsersState> {
    public CompareUsersViewModel() {
        super("comparison");
        this.setState(new CompareUsersState());
    }
}
