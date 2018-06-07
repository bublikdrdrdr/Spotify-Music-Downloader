package tk.ubublik.spotifydownloader.exception;

public class InvalidTokenException extends RuntimeException {

    private static final String defaultMessage = "Token is invalid or expired";

    public InvalidTokenException() {
        this(defaultMessage);
    }

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTokenException(Throwable cause) {
        this(defaultMessage, cause);
    }
}
