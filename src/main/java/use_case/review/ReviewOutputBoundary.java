package use_case.review;

import entity.User;

import java.util.ArrayList;

public interface ReviewOutputBoundary {
    public void prepareSuccessView(ArrayList<String> lst);
}
