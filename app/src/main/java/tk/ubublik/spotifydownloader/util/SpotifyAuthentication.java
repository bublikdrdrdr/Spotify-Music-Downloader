package tk.ubublik.spotifydownloader.util;

import android.app.Activity;
import android.content.Intent;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import tk.ubublik.spotifydownloader.exception.SpotifyAuthenticationStoppedException;

public class SpotifyAuthentication {

    private static final int REQUEST_CODE = 1337;
    private static final String CLIENT_ID = "5b5e65580fa14c8d9dfd62f342c1fe77";
    private static final String REDIRECT_URI = "scheme://callback";

    public SpotifyAuthentication() {
    }

    public SpotifyAuthentication(String lastToken) {
        this.lastToken = lastToken;
    }

    public void login(Activity activity) {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(activity, REQUEST_CODE, request);
    }

    private String lastToken;

    public void invalidateToken(){
        lastToken = null;
    }

    public String getToken(){
        return lastToken;
    }

    public String getTokenOnActivityResult(int requestCode, int resultCode, Intent intent){
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                //Config playerConfig = new Config(context, response.getAccessToken(), CLIENT_ID);
                lastToken = response.getAccessToken();
                return lastToken;
            } else throw new SpotifyAuthenticationStoppedException();
        }
        return null;
    }

}
