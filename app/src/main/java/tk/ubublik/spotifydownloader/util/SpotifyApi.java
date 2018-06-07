package tk.ubublik.spotifydownloader.util;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import tk.ubublik.spotifydownloader.entity.Playlist;
import tk.ubublik.spotifydownloader.entity.Track;
import tk.ubublik.spotifydownloader.exception.InvalidTokenException;

public class SpotifyApi {

    private String token;
    private AsyncHttpClient asyncClient = new AsyncHttpClient();

    public void setToken(String token) {
        this.token = token;
    }

    public void cancelAllRequests() {
        asyncClient.cancelAllRequests(true);
    }

    private Header[] getAuthorizationHeaders() {
        return new Header[]{new BasicHeader("Authorization",
                "Bearer " + (token == null ? "" : token))};
    }

    private JsonHttpResponseHandler getJsonHandler(ApiResultListener resultListener, MappingFunction mapper) {
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    mapper.map(statusCode, response);
                } catch (Exception e) {
                    resultListener.onFailure(response, e, statusCode);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (statusCode == 401) throwable = new InvalidTokenException(throwable);
                resultListener.onFailure(errorResponse, throwable, statusCode);
            }
        };
    }

    public RequestHandle getUserName(Context context, ApiResultListener<String> listener) {
        return asyncClient.get(context, ApiURLFactory.getURL(ApiURLFactory.UrlMapping.S_PROFILE),
                getAuthorizationHeaders(), null, getJsonHandler(listener,
                        (statusCode, response) -> listener.onSuccess(response.get("display_name").toString())));
    }

    public RequestHandle getPlaylists(Context context, ApiResultListener<List<Playlist>> listener) {
        return asyncClient.get(context, ApiURLFactory.getURL(ApiURLFactory.UrlMapping.S_PLAYLISTS),
                getAuthorizationHeaders(), null, getJsonHandler(listener,
                        (statusCode, response) -> {
                            JSONArray array = (JSONArray) response.get("items");
                            List<Playlist> list = new ArrayList<>(array.length());
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject item = array.getJSONObject(i);
                                list.add(new Playlist(item.getString("id"), item.getString("name"), item.getJSONObject("owner").getString("id")));
                            }
                            listener.onSuccess(list);
                        }));
    }

    public RequestHandle getTracks(Context context, String owner, String playlist, int offset, int limit, ApiResultListener<List<Track>> listener) {
        // TODO: 06-Jun-18 offset, limit
        return asyncClient.get(context, ApiURLFactory.getURL(ApiURLFactory.UrlMapping.S_TRACKS, owner, playlist, offset, limit),
                getAuthorizationHeaders(), null, getJsonHandler(listener,
                        (statusCode, response) -> {
                            JSONArray array = (JSONArray) response.get("items");
                            List<Track> list = new ArrayList<>(array.length());
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject item = array.getJSONObject(i).getJSONObject("track");
                                JSONArray artistsArray = item.getJSONArray("artists");
                                String[] artistsStringArray = new String[artistsArray.length()];
                                for (int j = 0; j < artistsArray.length(); j++) {
                                    artistsStringArray[j] = artistsArray.getJSONObject(j).getString("name");
                                }
                                list.add(new Track(item.getString("id"), item.getString("name"),
                                        item.getString("preview_url"), artistsStringArray, item.getInt("duration_ms")));
                            }
                            listener.onSuccess(list);
                        }));
    }

    private interface MappingFunction {
        void map(int statusCode, JSONObject response) throws Exception;
    }
}
