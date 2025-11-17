package data_access;

import entity.User;
import entity.Game;
import java.util.*;
import java.io.IOException;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

// NOTE: All Steamapi calls are of the format: 
// https://api.steampowered.com/<Interface>/<Method>/v<Version>/?key=<apikey>&<args>

// This is the DAO for a logged in user. (Steamid -> User object)
public class UserDataAccessObject {
    private static final String apikey = System.getenv("APIKEY");

    //Override this later in accordance with furture interfaces.
    public User get(long steamid) {
        final OkHttpClient client = new OkHttpClient().newBuilder().build();

        // Friends
        List<Long> ids = getFriendList(steamid, client);
        List<User> friends = getUserData(ids, client);

        ArrayList<Game> lib = getUserLibrary(steamid, client);
        ArrayList<Game> recent = getUserRecent(steamid, client);

        String username = "";
        String avatar = "";

        // NOTE: (steamid: String, personaname: Stirng, avatar, avatarmedium, avatarfull, avatarhash, realname, loccountrycode, gameid, gameextrainfo)
        try {
            Request request = new Request.Builder()
                .url(String.format("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=%s&steamids=%d", apikey, steamid))
                .build();
            final Response response = client.newCall(request).execute();
            final JSONObject responseBody = new JSONObject(response.body().string());

            final JSONArray playerList = responseBody.getJSONObject("response").getJSONArray("players");
            JSONObject player = playerList.getJSONObject(0);

            username = player.getString("personaname");
            avatar = player.getString("avatarfull");
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e + "[AT: Get]");
        }

        return new User(steamid, username, friends, lib, recent, avatar);
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

    private ArrayList<User> getUserData(List<Long> ids, OkHttpClient client) {
        ArrayList<User> out = new ArrayList<User>();

        long[][] processedIds = chunkIdList(ids);
        for (int i = 0; i < processedIds.length; ++i) {
            String stringedIds = "";
            for (int j = 0; j < processedIds[i].length; ++j)
                stringedIds += processedIds[i][j] + ",";

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
                    ArrayList<Game> recent = getUserRecent(playerId, client);

                    // Friends don't get their own friendlist. 
                    out.add(new User(playerId, player.getString("personaname"), null, lib, recent, player.getString("avatarfull")));
                }
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e + "[AT: UserData]");
            }
        }

        return out;
    }

    private ArrayList<Game> getUserLibrary(long steamid, OkHttpClient client) {
        ArrayList<Game> lib = new ArrayList<>();
        try {
            Request request = new Request.Builder()
                .url(String.format("https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=%s&steamid=%d&format=json&include_appinfo=true", apikey, steamid))
                .build();

            final Response response = client.newCall(request).execute();
            final JSONObject responseBody = new JSONObject(response.body().string());

            final JSONArray library = responseBody.getJSONObject("response").getJSONArray("games");
            for(int i = 0; i < library.length(); i++) {
                JSONObject game = library.getJSONObject(i);
                Game g = new Game(game.getLong("appid"), game.getString("name"), game.getString("img_icon_url"), game.getLong("playtime_forever"));
                lib.add(g);
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e + "[AT: Library]");
        }
        return lib;
    }

    // TODO: Add error handling and fallthrough on failure.
    private ArrayList<Game> getUserRecent(long steamid, OkHttpClient client) {
        ArrayList<Game> out = new ArrayList<>();
        try {
            Request request = new Request.Builder()
            .url(String.format("https://api.steampowered.com/IPlayerService/GetRecentlyPlayedGames/v0001/?key=%s&steamid=%d&format=json", apikey, steamid))
            .build();

            final Response response = client.newCall(request).execute();
            final JSONObject responseBody = new JSONObject(response.body().string());
            if (responseBody.getJSONObject("response").isEmpty()) {
                // Private profile, unsupported feature.
                return new ArrayList<>();
            }

            if(responseBody.getJSONObject("response").getInt("total_count") == 0) {
                // No games in the past 2 weeks.
                return new ArrayList<>();
            }
            final JSONArray recentGames = responseBody.getJSONObject("response").getJSONArray("games");
            for(int i = 0; i < recentGames.length(); ++i) {
                JSONObject game = recentGames.getJSONObject(i);
                Game g = new Game(game.getLong("appid"), game.getString("name"), game.getString("img_icon_url"), game.getLong("playtime_forever"));
                out.add(g);
            }

        } catch (IOException | JSONException e) {
            throw new RuntimeException(e + "[AT: Recent Games]");
        }
        return out;
    }

}
