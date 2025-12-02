package data_access;

import entity.Game;
import entity.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReviewAccessObject {
    public ArrayList<String> ReviewDisplay(User friend, OkHttpClient client) {
        long friendId = friend.getId();
        ArrayList<String> out = new ArrayList<>();
        List<Game> lib = friend.getLibrary();
        String cursor = "*";
        for (int i = 0; i < lib.size(); ++i) {
            int flag = 0;
            try {
                do {
                    Request request = new Request.Builder()
                            .url("https://store.steampowered.com/appreviews/" + lib.get(i).getId() + "?json=1&cursor={" + cursor + "}")
                            .build();
                    Response response = client.newCall(request).execute();
                    JSONObject responseBody = new JSONObject(response.body().string());
                    if (responseBody.getInt("success") != 1)
                        break;
                    cursor = responseBody.getString("cursor");
                    JSONArray reviews = responseBody.getJSONArray("reviews");
                    for (int j = 0; j < reviews.length(); ++j) {
                        if (reviews.getJSONObject(j).getJSONObject("author").getString("steamid").equals(friendId)) {
                            out.add(reviews.getJSONObject(j).getString("review"));
                            flag = 1;
                            break;
                        }
                    }
                } while (flag == 0);
            } catch (IOException | JSONException e) {
                // Since a private account with no games can be handled, then we do the same if the call fails.
                // NOTE: This is subject to change.
                return new ArrayList<>();
            }
        }
        return out;
    }
}
