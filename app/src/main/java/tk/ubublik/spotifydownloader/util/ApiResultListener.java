package tk.ubublik.spotifydownloader.util;

import org.json.JSONObject;

public interface ApiResultListener<T> {

    void onSuccess(T result);
    void onFailure(JSONObject errorResponse, Throwable throwable, int code);
}
