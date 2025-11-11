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
public class UserDataAcessObject {
    private static final String apikey = System.getenv("APIKEY");
    private final OkHttpClient client = new OkHttpClient().newBuilder().build();

    /**
     * Fetches a User object with their profile, friends, and games.
     *
     * @param steamid The Steam ID of the user
     * @return A User object with fetched data
     */
    public User get(long steamid) {
        if (apikey == null || apikey.isEmpty()) {
            System.err.println("Warning: APIKEY environment variable not set. Using demo data.");
            return createDemoUser(steamid);
        } else {
            System.out.println(String.format("APIKEY: %s", apikey));
        }

        String username = fetchUsername(steamid);
        List<User> friends = fetchFriends(steamid);
        List<Game> games = fetchGames(steamid);

        return new User(steamid, username, friends, games);
    }

    /**
     * Fetches the username for a given Steam ID.
     *
     * @param steamid The Steam ID
     * @return The username, or a default if fetch fails
     */
    private String fetchUsername(long steamid) {
        try {
            final Request request = new Request.Builder()
                .url(String.format("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=%s&steamids=%d",
                    apikey, steamid))
                .build();

            final Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                System.err.println(String.format("Failed to fetch username: HTTP %d, %s", response.code(), response.body().string()));
                return "User" + steamid;
            }

            final JSONObject responseBody = new JSONObject(response.body().string());
            final JSONObject players = responseBody.getJSONObject("response");
            final JSONArray playerArray = players.getJSONArray("players");

            if (playerArray.length() > 0) {
                final JSONObject player = playerArray.getJSONObject(0);
                return player.getString("personaname");
            }

        } catch (IOException | JSONException ex) {
            System.err.println(String.format("Error fetching username: %s", ex.getMessage()));
        }

        return "User" + steamid; // Default username
    }

    /**
     * Fetches the friends list for a given Steam ID.
     *
     * @param steamid The Steam ID
     * @return A list of User objects representing friends
     */
    private List<User> fetchFriends(long steamid) {
        List<User> friends = new ArrayList<>();

        try {
            final Request request = new Request.Builder()
                .url(String.format("https://api.steampowered.com/ISteamUser/GetFriendList/v0001/?key=%s&steamid=%d&relationship=friend",
                    apikey, steamid))
                .build();

            final Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                System.err.println(String.format("Failed to fetch friends: HTTP %d, %s", response.code(), response.body().string()));
                return friends;
            }

            final JSONObject responseBody = new JSONObject(response.body().string());

            // Check if friendslist exists (profile might be private)
            if (responseBody.has("friendslist")) {
                final JSONObject friendsList = responseBody.getJSONObject("friendslist");

                if (friendsList.has("friends")) {
                    final JSONArray friendsArray = friendsList.getJSONArray("friends");

                    for (int i = 0; i < friendsArray.length() && i < 10; i++) { // Limit to 10 friends to avoid too many API calls
                        final JSONObject friend = friendsArray.getJSONObject(i);
                        final long friendId = friend.getLong("steamid");
                        final String friendName = fetchUsername(friendId);

                        // Create a simplified friend User object (no recursive friend/game fetching)
                        friends.add(new User(friendId, friendName, new ArrayList<>(), new ArrayList<>()));
                    }
                }
            }

        } catch (IOException | JSONException ex) {
            System.err.println(String.format("Error fetching friends: %s", ex.getMessage()));
        }

        return friends;
    }

    /**
     * Fetches the games library for a given Steam ID.
     *
     * @param steamid The Steam ID
     * @return A list of Game objects
     */
    private List<Game> fetchGames(long steamid) {
        List<Game> games = new ArrayList<>();

        try {
            final Request request = new Request.Builder()
                .url(String.format("https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=%s&steamid=%d&format=json&include_appinfo=true",
                    apikey, steamid))
                .build();

            final Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                System.err.println(String.format("Failed to fetch games: HTTP %d, %s", response.code(), response.body().string()));
                return games;
            }

            final JSONObject responseBody = new JSONObject(response.body().string());
            final JSONObject responseData = responseBody.getJSONObject("response");

            if (responseData.has("games")) {
                final JSONArray gamesArray = responseData.getJSONArray("games");

                for (int i = 0; i < gamesArray.length(); i++) {
                    final JSONObject game = gamesArray.getJSONObject(i);
                    final long appid = game.getLong("appid");
                    final String name = game.getString("name");

                    games.add(new Game(appid, name));
                }
            }

        } catch (IOException | JSONException ex) {
            System.err.println(String.format("Error fetching games: %s", ex.getMessage()));
        }

        return games;
    }

    /**
     * Creates a demo user for testing when API key is not available.
     *
     * @param steamid The Steam ID
     * @return A demo User object
     */
    private User createDemoUser(long steamid) {
        List<User> demoFriends = Arrays.asList(
            new User(123456789L, "Friend1", new ArrayList<>(), new ArrayList<>()),
            new User(987654321L, "Friend2", new ArrayList<>(), new ArrayList<>())
        );

        List<Game> demoGames = Arrays.asList(
            new Game(730L, "Counter-Strike 2"),
            new Game(570L, "Dota 2"),
            new Game(440L, "Team Fortress 2"),
            new Game(271590L, "Grand Theft Auto V"),
            new Game(1245620L, "ELDEN RING")
        );

        return new User(steamid, "DemoUser" + steamid, demoFriends, demoGames);
    }
}
