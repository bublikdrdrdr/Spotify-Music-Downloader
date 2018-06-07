package tk.ubublik.spotifydownloader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import tk.ubublik.spotifydownloader.R;
import tk.ubublik.spotifydownloader.util.ApiResultListener;
import tk.ubublik.spotifydownloader.util.AppPreferences;
import tk.ubublik.spotifydownloader.util.Appearance;
import tk.ubublik.spotifydownloader.util.SpotifyApi;
import tk.ubublik.spotifydownloader.util.SpotifyAuthentication;

public class TestActivity extends AppCompatActivity implements View.OnClickListener{

    private AppPreferences preferences;
    private SpotifyAuthentication authentication = new SpotifyAuthentication();
    private AsyncHttpClient asyncClient = new AsyncHttpClient();
    private SpotifyApi spotifyApi = new SpotifyApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Appearance.setFullscreenFlags(this);
        Appearance.setLayoutUnderStatusBar(this, R.id.mainLayout);
        preferences = new AppPreferences(this);
        setAllListeners(findViewById(R.id.mainLayout));
    }

    private void setAllListeners(View parent){
        if (parent instanceof Button) {
            parent.setOnClickListener(this);
            return;
        }
        if (parent instanceof ViewGroup){
            for (int i = 0; i < ((ViewGroup) parent).getChildCount(); i++){
                setAllListeners(((ViewGroup) parent).getChildAt(i));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.resetTermsButton: preferences.setTermsAccepted(false); break;
            case R.id.restartAppButton: restartApp(); break;
            case R.id.loginButton: login(); break;
            case R.id.getNameButton: getMyName(); break;
            case R.id.getPlaylists: getPlaylists(); break;
            case R.id.searchZkSongs: searchZk(); break;
        }
    }

    private void restartApp(){
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        assert i != null;
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private void login(){
        authentication.login(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        authentication.getTokenOnActivityResult(requestCode, resultCode, data);
        spotifyApi.setToken(authentication.getToken());
    }

    private void getMyName(){
        /*asyncClient.get(this, "https://api.spotify.com/v1/me",
                new Header[]{new BasicHeader("Authorization", "Bearer "+authentication.getToken())},
                null, getResponseHandler(getToastConsumer("display_name")));*/
        spotifyApi.getUserName(this, new ApiResultListener<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(TestActivity.this, result, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(JSONObject errorResponse, Throwable throwable, int code) {
                Toast.makeText(TestActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getPlaylists(){
        asyncClient.get(this, "https://api.spotify.com/v1/me/playlists",
                new Header[]{new BasicHeader("Authorization", "Bearer "+authentication.getToken())},
                null, getResponseHandler(new JSONConsumer() {
                    @Override
                    public void consume(JSONObject object) {
                        StringBuilder stringBuilder = new StringBuilder();
                        try{
                            JSONArray map = (JSONArray) object.get("items");
                            for (int i = 0; i < map.length(); i++){
                                JSONObject mappedItem = (JSONObject)map.get(i);
                                stringBuilder.append(i);
                                stringBuilder.append(':');
                                stringBuilder.append(mappedItem.get("name"));
                                stringBuilder.append(':');
                                stringBuilder.append(mappedItem.get("id"));
                                stringBuilder.append('\n');
                            }
                        } catch (JSONException e){
                            stringBuilder = new StringBuilder(e.toString());
                        }
                        Toast.makeText(TestActivity.this, stringBuilder.toString(), Toast.LENGTH_LONG).show();
                    }
                }));
    }

    private interface JSONConsumer{
        void consume(JSONObject object);
    }

    private JSONConsumer getToastConsumer(String key){
        return object -> {
            String message;
            try{
                message = object.get(key).toString();
            } catch (JSONException e){
                message = e.getMessage();
            }
            Toast.makeText(TestActivity.this, message, Toast.LENGTH_LONG).show();
        };
    }

    private JsonHttpResponseHandler getResponseHandler(JSONConsumer jsonConsumer){
        return new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                jsonConsumer.consume(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(TestActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(TestActivity.this, errorResponse.toString(), Toast.LENGTH_LONG).show();
            }
        };
    }

    private void searchZk(){
            /*Thread thread = new Thread(() -> {
                try {
                    ZkApi zkApi = new ZkApi();
                    List<ZkTrack> tracks = ZkApi.searchTracks("ofenbach be mine", 0);
                    if (tracks.size()>0){
                        ZkApi.download(TestActivity.this, tracks.get(0).getDownloadLink(), "test.mp3");
                    }
                } catch (Exception e){
                    Log.e("testActivity", "can't search", e);
                }
            });//findViewById(R.id.searchZkSongs)
        thread.start();*/
    }
}
