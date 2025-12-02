package use_case.recent;

import javax.swing.JLabel;
import java.util.List;

public class RecentOutputData {
    private List<JLabel> labelList;
    
    public RecentOutputData(List<JLabel> list) {
        this.labelList = list;
    }

    public List<JLabel> getLabelList() {
        return this.labelList;
    }
}
