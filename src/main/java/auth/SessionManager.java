package auth;

import entity.User;

import java.time.Instant;
import java.time.Duration;
import java.util.Optional;

/**
 * Manages the authenticated user session.
 * This is a singleton class that maintains the current user's authentication state.
 */
public class SessionManager {
    private static SessionManager instance;

    private Long steamId;
    private User currentUser;
    private Instant sessionStartTime;
    private final Duration sessionDuration = Duration.ofHours(24); // Session expires after 24 hours

    /**
     * Private constructor for singleton pattern.
     */
    private SessionManager() {
    }

    /**
     * Gets the singleton instance of SessionManager.
     *
     * @return The SessionManager instance
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Creates a new session for the authenticated user.
     *
     * @param steamId The Steam ID of the authenticated user
     * @param user The User object with profile data
     */
    public synchronized void createSession(Long steamId, User user) {
        this.steamId = steamId;
        this.currentUser = user;
        this.sessionStartTime = Instant.now();
        System.out.println("Session created for Steam ID: " + steamId);
    }

    /**
     * Checks if there is an active session.
     *
     * @return true if there is an active session, false otherwise
     */
    public synchronized boolean hasActiveSession() {
        if (steamId == null || sessionStartTime == null) {
            return false;
        }

        // Check if session has expired
        Instant now = Instant.now();
        Duration elapsed = Duration.between(sessionStartTime, now);

        if (elapsed.compareTo(sessionDuration) > 0) {
            // Session has expired
            clearSession();
            return false;
        }

        return true;
    }

    /**
     * Gets the current authenticated Steam ID.
     *
     * @return An Optional containing the Steam ID if authenticated, empty otherwise
     */
    public synchronized Optional<Long> getSteamId() {
        if (hasActiveSession()) {
            return Optional.ofNullable(steamId);
        }
        return Optional.empty();
    }

    /**
     * Gets the current authenticated user.
     *
     * @return An Optional containing the User if authenticated, empty otherwise
     */
    public synchronized Optional<User> getCurrentUser() {
        if (hasActiveSession()) {
            return Optional.ofNullable(currentUser);
        }
        return Optional.empty();
    }

    /**
     * Updates the current user data.
     *
     * @param user The updated User object
     */
    public synchronized void updateCurrentUser(User user) {
        if (hasActiveSession() && user != null) {
            this.currentUser = user;
        }
    }

    /**
     * Gets the remaining session time.
     *
     * @return The remaining duration of the session
     */
    public synchronized Duration getRemainingSessionTime() {
        if (!hasActiveSession()) {
            return Duration.ZERO;
        }

        Instant now = Instant.now();
        Duration elapsed = Duration.between(sessionStartTime, now);
        return sessionDuration.minus(elapsed);
    }

    /**
     * Refreshes the session, extending its expiration time.
     */
    public synchronized void refreshSession() {
        if (hasActiveSession()) {
            this.sessionStartTime = Instant.now();
            System.out.println("Session refreshed for Steam ID: " + steamId);
        }
    }

    /**
     * Clears the current session (logs out the user).
     */
    public synchronized void clearSession() {
        this.steamId = null;
        this.currentUser = null;
        this.sessionStartTime = null;
        System.out.println("Session cleared");
    }

    /**
     * Checks if a specific Steam ID matches the current session.
     *
     * @param steamId The Steam ID to check
     * @return true if the Steam ID matches the current session
     */
    public synchronized boolean isCurrentUser(Long steamId) {
        return hasActiveSession() && this.steamId != null && this.steamId.equals(steamId);
    }

    /**
     * Gets a string representation of the session status.
     *
     * @return A string describing the current session status
     */
    public synchronized String getSessionStatus() {
        if (!hasActiveSession()) {
            return "No active session";
        }

        Duration remaining = getRemainingSessionTime();
        long hours = remaining.toHours();
        long minutes = remaining.toMinutes() % 60;

        return String.format("Active session for Steam ID %d (expires in %dh %dm)",
                            steamId, hours, minutes);
    }
}