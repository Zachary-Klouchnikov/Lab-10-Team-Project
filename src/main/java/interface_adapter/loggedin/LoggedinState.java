package interface_adapter.loggedin;

import java.util.List;
import java.util.ArrayList;
import javax.swing.JLabel;

public class LoggedinState {
    private Long steamId = null;
    private String username = "";
    private JLabel profilePicture = new JLabel();
    private List<JLabel> gameLabels = new ArrayList<>();
    private List<JLabel> userLabels = new ArrayList<>();
    private String errorMessage = ""; 

    public String getError() {
        return this.errorMessage;
    }

    public void setError(String errMsg) {
        this.errorMessage = errMsg;
    }

    public Long getId() {
        return this.steamId;
    }

    public void setId(Long id) {
        this.steamId = id;
    }

    public String getName() {
        return this.username;
    }

    public void setName(String name) {
        this.username = name;
    }

    public JLabel getProfilePicture() {
        return this.profilePicture;
    }

    public void setProfilePicture(JLabel label) {
        this.profilePicture = label;
    }

    public List<JLabel> getGameLabels() {
        return this.gameLabels;
    }

    public void setGameLabels(List<JLabel> list) {
        this.gameLabels = list;
    }

    public List<JLabel> getFriendLabels() {
        return this.userLabels;
    }

    public void setFriendLabels(List<JLabel> list) {
        this.userLabels = list;
    }
}
