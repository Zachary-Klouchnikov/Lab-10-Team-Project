package auth;

/**
 * Custom exception for Steam authentication errors.
 */
public class SteamAuthException extends Exception {

    /**
     * Error types for different authentication failures.
     */
    public enum ErrorType {
        NETWORK_ERROR("Network error occurred"),
        BROWSER_NOT_SUPPORTED("Browser is not supported on this platform"),
        CALLBACK_TIMEOUT("Authentication timed out waiting for callback"),
        INVALID_RESPONSE("Invalid response from Steam"),
        VERIFICATION_FAILED("Steam verification failed"),
        STEAM_ID_EXTRACTION_FAILED("Failed to extract Steam ID from response"),
        SERVER_START_FAILED("Failed to start callback server"),
        USER_CANCELLED("User cancelled authentication"),
        UNKNOWN("Unknown error occurred");

        private final String description;

        ErrorType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private final ErrorType errorType;

    /**
     * Creates a new SteamAuthException with the specified error type.
     *
     * @param errorType The type of error
     */
    public SteamAuthException(ErrorType errorType) {
        super(errorType.getDescription());
        this.errorType = errorType;
    }

    /**
     * Creates a new SteamAuthException with the specified error type and message.
     *
     * @param errorType The type of error
     * @param message The error message
     */
    public SteamAuthException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    /**
     * Creates a new SteamAuthException with the specified error type, message, and cause.
     *
     * @param errorType The type of error
     * @param message The error message
     * @param cause The underlying cause
     */
    public SteamAuthException(ErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    /**
     * Gets the error type.
     *
     * @return The error type
     */
    public ErrorType getErrorType() {
        return errorType;
    }

    /**
     * Gets a user-friendly error message.
     *
     * @return A user-friendly error message
     */
    public String getUserFriendlyMessage() {
        switch (errorType) {
            case NETWORK_ERROR:
                return "Could not connect to Steam. Please check your internet connection.";
            case BROWSER_NOT_SUPPORTED:
                return "Your system does not support opening a browser. Please manually navigate to the authentication URL.";
            case CALLBACK_TIMEOUT:
                return "Authentication timed out. Please try again.";
            case INVALID_RESPONSE:
                return "Received an invalid response from Steam. Please try again.";
            case VERIFICATION_FAILED:
                return "Could not verify your authentication with Steam. Please try again.";
            case STEAM_ID_EXTRACTION_FAILED:
                return "Could not extract your Steam ID. Please try again.";
            case SERVER_START_FAILED:
                return "Could not start the authentication server. Port may be in use.";
            case USER_CANCELLED:
                return "Authentication was cancelled.";
            default:
                return "An unexpected error occurred. Please try again.";
        }
    }
}