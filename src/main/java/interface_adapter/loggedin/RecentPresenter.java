package interface_adapter.loggedin;

import use_case.recent.RecentOutputBoundary;
import use_case.recent.RecentOutputData;
import javax.swing.*;

public class RecentPresenter implements RecentOutputBoundary {
    private LoggedinViewModel loggedinViewModel;

    public RecentPresenter(LoggedinViewModel loggedinViewModel) {
        this.loggedinViewModel = loggedinViewModel;
    }

    @Override
    public void prepareSuccessView(RecentOutputData data) {
        this.loggedinViewModel.getState().setRecent(data.getLabelList());       
        this.loggedinViewModel.firePropertyChange();
    }

    @Override 
    public void prepareFailureView(String errMsg) {
        JOptionPane.showMessageDialog(null, errMsg, "Recent Panel Error", JOptionPane.ERROR_MESSAGE);
    }
}
