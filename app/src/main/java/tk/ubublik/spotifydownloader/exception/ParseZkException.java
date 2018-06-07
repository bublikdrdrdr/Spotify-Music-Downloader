package tk.ubublik.spotifydownloader.exception;

import java.io.IOException;

public class ParseZkException extends IOException {
    public ParseZkException() {
    }

    public ParseZkException(String message) {
        super(message);
    }

    public ParseZkException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseZkException(Throwable cause) {
        super(cause);
    }
}
