package tk.ubublik.spotifydownloader.util;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;

import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import tk.ubublik.spotifydownloader.entity.ZkTrack;

public class ZkApi {

    private static void checkMainThread() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread())
            throw new NetworkOnMainThreadException();
    }

    public static Connection getSearchTracksConnection(String name){
        return Jsoup.connect(ApiURLFactory.getURL(ApiURLFactory.UrlMapping.ZF_SEARCH, name));
    }

    public static List<ZkTrack> searchTracks(Connection connection, int count) throws IOException {
        Document document = connection.get();
        Elements liItems = document.getElementsByClass("tracks-item");
        List<ZkTrack> result = new ArrayList<>();
        for (Element element : liItems) {
            if (count<1) break;
            count--;
            result.add(new ZkTrack(element.attr("data-artist"),
                    element.attr("data-title"),
                    Integer.valueOf(element.attr("data-id")),
                    Integer.valueOf(element.attr("data-duration")) * 1000,
                    element.attr("data-url")));
        }
        return result;
    }

    public static void download(Context context, String url, String filename) throws IOException {
        URL oUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) oUrl.openConnection();
        connection.addRequestProperty("Referer", "someRefererHeaderBecauseStupidZkApiDoesNotAllowToJustDownloadMusicByUrl");
        connection.setDoInput(true);
        connection.getContentLength();
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, filename);
        path.mkdirs();
        file.createNewFile();
        FileOutputStream fileStream = new FileOutputStream(file);
        InputStream inputStream = connection.getInputStream();
        IOUtils.copy(inputStream, fileStream);
        fileStream.flush();
        fileStream.close();
        connection.getInputStream().close();
    }

    public static HttpURLConnection getUrlConnection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.addRequestProperty("Referer", "someRefererHeaderBecauseStupidZkApiDoesNotAllowToJustDownloadMusicByUrl");
        connection.setDoInput(true);
        return connection;
    }
}
