package data_access;

import entity.User;
import entity.Game;
import java.util.*;
import java.io.IOException;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import data_access.ImageDataAccessObject;

// NOTE: All Steamapi calls are of the format: 
// https://api.steampowered.com/<Interface>/<Method>/v<Version>/?key=<apikey>&<args>

// This is the DAO for a logged in user. (Steamid -> User object)
public class UserDataAccessObject {
    private static final String apikey = System.getenv("APIKEY");
    private final OkHttpClient client = new OkHttpClient().newBuilder().build();

    /**
     * Fetches a User object with their profile, friends, and games.
     *
     * @param steamid The Steam ID of the user
     * @return A User object with fetched data
     * @throw A RuntimeException when the logged-in user's data cannot be instantiated.
     */
    public User get(long steamid) {
        // Friends
        List<Long> ids = getFriendList(steamid, client);
        List<User> friends = getUserData(ids, client);
        //Games
        ArrayList<Game> lib = getUserLibrary(steamid, client);

        String username = "";
        String avatar = "";
        try {
            Request request = new Request.Builder()
                .url(String.format("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=%s&steamids=%d", apikey, steamid))
                .build();
            final Response response = client.newCall(request).execute();
            final JSONObject responseBody = new JSONObject(response.body().string());

            final JSONArray playerList = responseBody.getJSONObject("response").getJSONArray("players");
            if (playerList.length() == 0)
                throw new RuntimeException("Invalid steamid!");

            JSONObject player = playerList.getJSONObject(0);
            username = player.getString("personaname");
            avatar = player.getString("avatarhash");
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e + "[AT: Get]");
        }

        User out = new User(steamid, username, friends, lib, avatar);
        ImageDataAccessObject.downloadImage(out);
        return out;
    }

    private long[][] chunkIdList(List<Long> ids) {
        int length = (ids.size() / 100) + 1;
        long[][] out = new long[length][];

        for( int i = 0; i < length; ++i) {
            List<Long> subList = ids.subList(0, ids.size() >= 100 ? 100 : ids.size());
            long[] subout = new long[subList.size()];
            for (int j = 0; j < subList.size(); ++j) {
                subout[j] = subList.get(j);
            }
            out[i] = subout;
            subList.clear();
        }

        return out;
    }

    private ArrayList<Long> getFriendList(long steamid, OkHttpClient client) {
        ArrayList<Long> out = new ArrayList<>();

        try {
            Request request = new Request.Builder()
                .url(String.format("https://api.steampowered.com/ISteamUser/GetFriendList/v0001/?key=%s&steamid=%d&relationship=%s", apikey, steamid, "all"))
                .build();
            final Response response = client.newCall(request).execute();
            final JSONObject responseBody = new JSONObject(response.body().string());
            if (responseBody.isEmpty())
                throw new RuntimeException("Friendslist is private!");

            final JSONArray friendlist = responseBody.getJSONObject("friendslist").getJSONArray("friends");
            for (int i = 0; i < friendlist.length(); ++i) {
                JSONObject person = friendlist.getJSONObject(i);
                out.add(Long.parseLong(person.getString("steamid")));
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e + "[AT: FriendList]");
        }
        return out;
    }

    // NOTE: (steamid, personaname, avatar, avatarmedium, avatarfull, avatarhash, realname, loccountrycode, gameid, gameextrainfo)
    private ArrayList<User> getUserData(List<Long> ids, OkHttpClient client) {
        ArrayList<User> out = new ArrayList<User>();

        long[][] processedIds = chunkIdList(ids);
        for (int i = 0; i < processedIds.length; ++i) {
            String stringedIds = "";
            for (int j = 0; j < processedIds[i].length; ++j)
                stringedIds += processedIds[i][j] + ",";

            // TODO: additional error handling required here.
            try {
                Request request = new Request.Builder()
                    .url(String.format("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=%s&steamids=%s",apikey, stringedIds))
                    .build();
                final Response response = client.newCall(request).execute();
                final JSONObject responseBody = new JSONObject(response.body().string());

                final JSONArray playerList = responseBody.getJSONObject("response").getJSONArray("players");
                for (int idx = 0; idx < playerList.length(); ++idx) {
                    JSONObject player = playerList.getJSONObject(idx);
                    long playerId = Long.parseLong(player.getString("steamid"));
                    ArrayList<Game> lib = getUserLibrary(playerId, client);

                    // Friends don't get their own friendlist. 
                    User user = new User(playerId, player.getString("personaname"), null, lib, player.getString("avatarhash"));
                    ImageDataAccessObject.downloadImage(user);
                    out.add(user);
                }
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e + "[AT: UserData]");
            }
        }

        return out;
    }

    private ArrayList<Game> getUserLibrary(long steamid, OkHttpClient client) {
        ArrayList<Game> out = new ArrayList<>();
        try {
            Request request = new Request.Builder()
                .url(String.format("https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=%s&steamid=%d&format=json&include_appinfo=true", apikey, steamid))
                .build();
            final Response response = client.newCall(request).execute();
            final JSONObject responseBody = new JSONObject(response.body().string());

            // The response will ALWAYS return with a "response" object
            if (responseBody.getJSONObject("response").isEmpty()) {
                // Let it fall through and handle it at display time.
                // It's the case where an account doesn't have any games or is private.
                return new ArrayList<>();
            }
            final JSONArray library = responseBody.getJSONObject("response").getJSONArray("games");
            for(int i = 0; i < library.length(); i++) {
                JSONObject game = library.getJSONObject(i);
                int recent = game.has("playtime_2weeks") ? game.getInt("playtime_2weeks") : 0;
                Game g = new Game(game.getLong("appid"), game.getString("name"), game.getInt("playtime_forever"), game.getString("img_icon_url"), recent);
                ImageDataAccessObject.downloadImage(g);
                out.add(g);
            }

        } catch (IOException | JSONException e) {
            // Since a private account with no games can be handled, then we do the same if the call fails.
            // NOTE: This is subject to change.
            return new ArrayList<>();
        }
        return out;
    }
    public ArrayList<String> ReviewDisplay(long friendId, OkHttpClient client) {
        ArrayList<String> out = new ArrayList<>();
        ArrayList<Game> lib = getUserLibrary(friendId, client);
        String cursor = "*";
        for  (int i = 0; i < lib.size(); ++i) {
            try {
                int flag = 0;
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
                        if(Long.toString(friendId).equals(reviews.getJSONObject(j).getJSONObject("author").getString("steamid"))){
                            out.add(reviews.getJSONObject(j).getString("review"));
                            flag = 1;
                            break;
                        }
                    }
                    if(flag == 1)
                        break;
                }while(true);
            } catch (IOException | JSONException e) {
                // Since a private account with no games can be handled, then we do the same if the call fails.
                // NOTE: This is subject to change.
                return new ArrayList<>();
            }
        }
        return out;
    }
}
