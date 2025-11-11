package auth;

import okhttp3.*;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for authenticating users with Steam using OpenID 2.0.
 * This class orchestrates the entire authentication flow.
 */
public class SteamAuthService {
    private static final String STEAM_OPENID_URL = "https://steamcommunity.com/openid/login";
    private static final Pattern STEAM_ID_PATTERN = Pattern.compile("https://steamcommunity\\.com/openid/id/(\\d+)");

    private final OkHttpClient httpClient;
    private LocalCallbackServer callbackServer;
    private final int callbackPort;

    /**
     * Creates a new SteamAuthService with default callback port 8080.
     */
    public SteamAuthService() {
        this(8080);
    }

    /**
     * Creates a new SteamAuthService with specified callback port.
     *
     * @param callbackPort The port for the callback server
     */
    public SteamAuthService(int callbackPort) {
        this.callbackPort = callbackPort;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Initiates the Steam authentication flow.
     *
     * @return A CompletableFuture containing the authenticated Steam ID
     * @throws SteamAuthException if authentication fails
     */
    public CompletableFuture<Long> authenticate() throws SteamAuthException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Start the callback server
                callbackServer = new LocalCallbackServer(callbackPort);
                CompletableFuture<Map<String, String>> callbackFuture;

                try {
                    callbackFuture = callbackServer.start();
                } catch (IOException e) {
                    throw new SteamAuthException(
                        SteamAuthException.ErrorType.SERVER_START_FAILED,
                        "Failed to start callback server on port " + callbackPort + ": " + e.getMessage(),
                        e
                    );
                }

                // Build and open the authentication URL
                String authUrl = buildAuthenticationUrl();

                try {
                    openInBrowser(authUrl);
                } catch (UnsupportedOperationException e) {
                    throw new SteamAuthException(
                        SteamAuthException.ErrorType.BROWSER_NOT_SUPPORTED,
                        e.getMessage(),
                        e
                    );
                }

                // Wait for the callback with a timeout
                Map<String, String> callbackParams;
                try {
                    callbackParams = callbackFuture.get(5, TimeUnit.MINUTES);
                } catch (TimeoutException e) {
                    throw new SteamAuthException(
                        SteamAuthException.ErrorType.CALLBACK_TIMEOUT,
                        "Authentication timed out after 5 minutes",
                        e
                    );
                } catch (InterruptedException | ExecutionException e) {
                    throw new SteamAuthException(
                        SteamAuthException.ErrorType.UNKNOWN,
                        "Error waiting for authentication callback: " + e.getMessage(),
                        e
                    );
                }

                // Check if user cancelled (openid.mode=cancel)
                String mode = callbackParams.get("openid.mode");
                if ("cancel".equals(mode)) {
                    throw new SteamAuthException(
                        SteamAuthException.ErrorType.USER_CANCELLED,
                        "User cancelled Steam authentication"
                    );
                }

                // Verify the authentication response
                boolean isValid;
                try {
                    isValid = verifyAuthentication(callbackParams);
                } catch (IOException e) {
                    throw new SteamAuthException(
                        SteamAuthException.ErrorType.NETWORK_ERROR,
                        "Network error during verification: " + e.getMessage(),
                        e
                    );
                }

                if (!isValid) {
                    throw new SteamAuthException(
                        SteamAuthException.ErrorType.VERIFICATION_FAILED,
                        "Steam authentication verification failed"
                    );
                }

                // Extract Steam ID from the response
                String claimedId = callbackParams.get("openid.claimed_id");
                Long steamId = extractSteamId(claimedId);

                if (steamId == null) {
                    throw new SteamAuthException(
                        SteamAuthException.ErrorType.STEAM_ID_EXTRACTION_FAILED,
                        "Failed to extract Steam ID from claimed_id: " + claimedId
                    );
                }

                System.out.println("Successfully authenticated Steam ID: " + steamId);
                return steamId;

            } catch (SteamAuthException e) {
                throw new RuntimeException(e.getUserFriendlyMessage(), e);
            } catch (Exception e) {
                throw new RuntimeException("Unexpected authentication error: " + e.getMessage(), e);
            } finally {
                // Always stop the callback server
                if (callbackServer != null) {
                    callbackServer.stop();
                }
            }
        });
    }

    /**
     * Builds the Steam OpenID authentication URL.
     *
     * @return The authentication URL
     */
    private String buildAuthenticationUrl() {
        String returnUrl = "http://localhost:" + callbackPort + "/callback";
        String realm = "http://localhost:" + callbackPort;

        return STEAM_OPENID_URL + "?" +
                "openid.ns=" + encode("http://specs.openid.net/auth/2.0") +
                "&openid.mode=checkid_setup" +
                "&openid.return_to=" + encode(returnUrl) +
                "&openid.realm=" + encode(realm) +
                "&openid.identity=" + encode("http://specs.openid.net/auth/2.0/identifier_select") +
                "&openid.claimed_id=" + encode("http://specs.openid.net/auth/2.0/identifier_select");
    }

    /**
     * URL encodes a string.
     *
     * @param value The string to encode
     * @return The encoded string
     */
    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * Opens a URL in the system's default browser.
     *
     * @param url The URL to open
     * @throws Exception if the browser cannot be opened
     */
    private void openInBrowser(String url) throws Exception {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(url));
            System.out.println("Opening Steam authentication in browser...");
        } else {
            throw new UnsupportedOperationException("Desktop browsing is not supported on this platform");
        }
    }

    /**
     * Verifies the authentication response with Steam.
     *
     * @param params The parameters received from the callback
     * @return true if the authentication is valid, false otherwise
     * @throws IOException if the verification request fails
     */
    private boolean verifyAuthentication(Map<String, String> params) throws IOException {
        // Build the verification request
        FormBody.Builder formBuilder = new FormBody.Builder();

        // Copy all parameters but change mode to check_authentication
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if ("openid.mode".equals(entry.getKey())) {
                formBuilder.add("openid.mode", "check_authentication");
            } else {
                formBuilder.add(entry.getKey(), entry.getValue());
            }
        }

        Request request = new Request.Builder()
                .url(STEAM_OPENID_URL)
                .post(formBuilder.build())
                .build();

        // Send the verification request
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }

            String responseBody = response.body().string();
            System.out.println("Steam verification response: " + responseBody);

            // Check if the response indicates valid authentication
            return responseBody.contains("is_valid:true");
        }
    }

    /**
     * Extracts the Steam ID from the claimed_id parameter.
     *
     * @param claimedId The claimed_id from the OpenID response
     * @return The Steam ID, or null if extraction fails
     */
    private Long extractSteamId(String claimedId) {
        if (claimedId == null) {
            return null;
        }

        Matcher matcher = STEAM_ID_PATTERN.matcher(claimedId);
        if (matcher.find()) {
            try {
                return Long.parseLong(matcher.group(1));
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse Steam ID: " + e.getMessage());
            }
        }

        return null;
    }

    /**
     * Closes the HTTP client and releases resources.
     */
    public void close() {
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }
}