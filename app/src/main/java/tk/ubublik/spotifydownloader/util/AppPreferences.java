package tk.ubublik.spotifydownloader.util;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {

    private SharedPreferences sharedPreferences;
    private final static String fileName = "spotify.downloader.preferences";

    //preference keys
    private static final String termsKey = "terms";

    public AppPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public boolean isTermsAccepted(){
        return sharedPreferences.getBoolean(termsKey, false);
    }

    public void setTermsAccepted(boolean value){
        sharedPreferences.edit().putBoolean(termsKey, value).apply();
    }
}
