package tk.ubublik.spotifydownloader.exception;

import java.io.IOException;

public class SaveTrackException extends IOException {
    public SaveTrackException() {
    }

    public SaveTrackException(String message) {
        super(message);
    }

    public SaveTrackException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaveTrackException(Throwable cause) {
        super(cause);
    }
}
