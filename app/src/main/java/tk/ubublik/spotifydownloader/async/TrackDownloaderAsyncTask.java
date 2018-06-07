package tk.ubublik.spotifydownloader.async;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

import tk.ubublik.spotifydownloader.adapter.TrackAdapter;
import tk.ubublik.spotifydownloader.adapter.TrackAdapterItem;
import tk.ubublik.spotifydownloader.entity.ZkTrack;
import tk.ubublik.spotifydownloader.exception.ParseZkException;
import tk.ubublik.spotifydownloader.exception.SaveTrackException;
import tk.ubublik.spotifydownloader.exception.TaskCanceledException;
import tk.ubublik.spotifydownloader.exception.TrackNotFoundException;
import tk.ubublik.spotifydownloader.util.FileUtils;
import tk.ubublik.spotifydownloader.util.ZkApi;

public class TrackDownloaderAsyncTask extends AsyncTask<TrackAdapterItem, Double, Throwable> {

    private TrackAdapterItem item;
    private Connection searchConnection;
    private HttpURLConnection connection;
    private InputStream input;
    private File file;
    private FileOutputStream output;
    private TrackAdapter trackAdapter;

    public TrackDownloaderAsyncTask(TrackAdapter trackAdapter){
        this.trackAdapter = trackAdapter;
    }

    @Override
    protected void onPostExecute(Throwable throwable) {
        if (throwable!=null) {
            rollback();
            item.cancelVisible = false;
            item.downloadVisible = true;
            if (throwable instanceof TaskCanceledException){
                item.progress = 0;
                item.progressMode = TrackAdapterItem.ProgressMode.DONE;
            } else {
                item.errorText = throwable.getMessage();
                item.progress = 1;
                item.progressMode = TrackAdapterItem.ProgressMode.ERROR;
            }
        } else {
            item.progress = 0;
            item.cancelVisible = false;
            item.errorText = null;
            item.progressMode = TrackAdapterItem.ProgressMode.DONE;
        }
        item.asyncTask = null;
        if (trackAdapter!=null) trackAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onProgressUpdate(Double... values) {
        item.progress = values[0];
        if (trackAdapter!=null) trackAdapter.notifyDataSetChanged();
    }

    private void rollback(){
        try {
            input.close();
        } catch (Exception ignored) {
        }
        try{
            output.close();
        } catch (Exception ignored){

        }
        try {
            connection.disconnect();
        } catch (Exception ignored){

        }
        FileUtils.removeFile(file);
    }

    @Override
    protected void onCancelled() {
        rollback();
        item.cancelVisible = false;
        item.downloadVisible = true;
        item.progress = 0;
        item.progressMode = TrackAdapterItem.ProgressMode.DONE;
        item.errorText = null;
        item.asyncTask = null;
        if (trackAdapter!=null) trackAdapter.notifyDataSetChanged();
    }

    @Override
    protected Throwable doInBackground(TrackAdapterItem... tracks) {
        item = tracks[0];
        item.cancelVisible = true;
        item.downloadVisible = false;
        item.errorText = null;
        final int EOF = -1;
        item.progressMode = TrackAdapterItem.ProgressMode.DOWNLOAD;
        try {
            publishProgress(-1.);
            searchConnection = ZkApi.getSearchTracksConnection(item.track.getFullName());
            List<ZkTrack> results = ZkApi.searchTracks(searchConnection, 1);
            connection = ZkApi.getUrlConnection(results.get(0).getDownloadLink());
        } catch (IOException e) {
            return new ParseZkException("Can't parse zk page", e);
        } catch (IndexOutOfBoundsException e) {
            return new TrackNotFoundException();
        } catch (Exception e){
            return e;
        }
        try {
            int contentLength = connection.getContentLength();
            input = connection.getInputStream();
            file = FileUtils.getWritableFile(item.track.getFullName()+".mp3");
            output = FileUtils.getOutputStream(file);
            byte[] buffer = new byte[4096];
            long count = 0;
            int n;
            while (EOF != (n = input.read(buffer))) {
                if (isCancelled()) {
                    Log.i("AsyncTask", "Canceled");
                    return new TaskCanceledException();
                }
                output.write(buffer, 0, n);
                count += n;
                publishProgress(count / (double) contentLength);
            }
        } catch (Exception e) {
            return new SaveTrackException("Can't write track to internal memory", e);
        }
        return null;
    }
}