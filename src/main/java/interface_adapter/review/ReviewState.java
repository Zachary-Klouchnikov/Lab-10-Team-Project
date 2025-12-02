package interface_adapter.review;

import entity.User;

import java.util.ArrayList;

public class ReviewState {
    private String errorMessage = "";
    private User user;
    private ArrayList<String> lst;

    public ArrayList<String> getLst() {return lst;}

    public void setLst(ArrayList<String> lst) {this.lst = lst;}

    public User getLoggedInUser() {
        return user;
    }

    public void setLoggedInUser(User user) {
        this.user = user;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String msg) {
        this.errorMessage = msg;
    }
}
