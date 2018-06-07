package tk.ubublik.spotifydownloader.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ApiURLFactory {

    public static String getURL(UrlMapping mapping, Object... params) {
        String rawUrl = String.format(mapping.mapping, params);
        try {
        URL url = new URL(rawUrl);
        URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        return uri.toASCIIString();
    } catch (MalformedURLException|URISyntaxException e){
            return rawUrl;
        }
    }

    public enum UrlMapping {
        S_PROFILE("https://api.spotify.com/v1/me"),
        S_PLAYLISTS("https://api.spotify.com/v1/me/playlists"),
        S_TRACKS("https://api.spotify.com/v1/users/%s/playlists/%s/tracks?offset=%d&limit=%d"),
        ZF_SEARCH("http://m.zk.fm/mp3/search?keywords=%s"),
        ZF_SONG("http://m.zk.fm/song/%s");

        private String mapping;

        UrlMapping(String mapping) {
            this.mapping = mapping;
        }

        public String getMapping() {
            return mapping;
        }
    }
}
