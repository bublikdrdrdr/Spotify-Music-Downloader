package tk.ubublik.spotifydownloader;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tk.ubublik.spotifydownloader.activity.TermsActivity;
import tk.ubublik.spotifydownloader.activity.TestActivity;
import tk.ubublik.spotifydownloader.adapter.EndlessRecyclerViewScrollListener;
import tk.ubublik.spotifydownloader.adapter.PlaylistAdapter;
import tk.ubublik.spotifydownloader.adapter.TrackAdapter;
import tk.ubublik.spotifydownloader.adapter.TrackAdapterItem;
import tk.ubublik.spotifydownloader.async.TrackDownloaderAsyncTask;
import tk.ubublik.spotifydownloader.entity.Playlist;
import tk.ubublik.spotifydownloader.entity.Track;
import tk.ubublik.spotifydownloader.exception.SpotifyAuthenticationStoppedException;
import tk.ubublik.spotifydownloader.util.ApiResultListener;
import tk.ubublik.spotifydownloader.util.AppPreferences;
import tk.ubublik.spotifydownloader.util.Appearance;
import tk.ubublik.spotifydownloader.util.SpotifyApi;
import tk.ubublik.spotifydownloader.util.SpotifyAuthentication;

public class MainActivity extends AppCompatActivity implements PlaylistAdapter.OnPlaylistClickListener, TrackAdapter.OnListItemButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int termsRequestCode = 9247;
    private static final int grantedPermissionRequestCode = 5551;
    private final int ONE_PAGE_TRACKS_COUNT = 20;
    private AppPreferences preferences;
    private SpotifyAuthentication authentication = new SpotifyAuthentication();
    private SpotifyApi spotifyApi = new SpotifyApi();
    private PlaylistAdapter playlistAdapter;
    private TrackAdapter trackAdapter;
    private ApiResultListener<List<Playlist>> playlistResultListener = new ApiResultListener<List<Playlist>>() {
        @Override
        public void onSuccess(List<Playlist> result) {
            playlistAdapter = new PlaylistAdapter(MainActivity.this,
                    result instanceof ArrayList ? (ArrayList<Playlist>) result : new ArrayList<>(result));
            setAdapter(playlistAdapter);
            trackAdapter = null;
        }

        @Override
        public void onFailure(JSONObject errorResponse, Throwable throwable, int code) {
            onSpotifyApiFail(errorResponse, throwable, code);
        }
    };
    private Playlist activePlaylist;
    private int lastIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = new AppPreferences(this);
        setupView();
        if (checkTerms()) authenticate();
        //findViewById(R.id.testActivity).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TestActivity.class)));
    }

    private void setupView() {
        Appearance.setFullscreenFlags(this);
        Appearance.setLayoutUnderStatusBar(this, R.id.mainLayout);
    }

    private boolean checkTerms() {
        if (!preferences.isTermsAccepted()) {
            Intent intent = new Intent(this, TermsActivity.class);
            startActivityForResult(intent, termsRequestCode);
            return false;
        }
        return true;
    }

    private void authenticate() {
        authentication.login(MainActivity.this);
        setLoadingAnimation(true);
    }

    private void afterAuthentication() {
        setLoadingAnimation(false);
        spotifyApi.setToken(authentication.getToken());
        showPlaylists();
    }

    private void setLoadingAnimation(boolean visible) {
        findViewById(R.id.spinnerLayout).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void showPlaylists() {
        spotifyApi.getPlaylists(this, playlistResultListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (authentication.getTokenOnActivityResult(requestCode, resultCode, data) != null) {
                afterAuthentication();
            }
        } catch (SpotifyAuthenticationStoppedException e){
            Toast.makeText(this, "Spotify authentication aborted", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Next time be patient and wait until you'll see your playlists", Toast.LENGTH_LONG).show();
            finish();
        }
        switch (requestCode) {
            case termsRequestCode:
                if (resultCode != RESULT_OK) finish();
                else authenticate(); break;
        }
    }

    private void setAdapter(RecyclerView.Adapter adapter) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView.getAdapter() != adapter) {
            recyclerView.setAdapter(adapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            if (adapter instanceof TrackAdapter){
                recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        showTracks(null);
                    }
                });
            }
        } else adapter.notifyDataSetChanged();
    }

    private void showTracks(Integer playlistIndex) {
        if (playlistIndex == null && activePlaylist == null)
            throw new IllegalArgumentException("Playlist index can't be null while active playlist is null");
        if (playlistIndex != null) {
            activePlaylist = playlistAdapter.getPlaylists().get(playlistIndex);
        }
        spotifyApi.getTracks(this, activePlaylist.getOwner(), activePlaylist.getId(), trackAdapter == null ? 0 : trackAdapter.getItemCount(), ONE_PAGE_TRACKS_COUNT, new ApiResultListener<List<Track>>() {

            @Override
            public void onSuccess(List<Track> result) {
                ArrayList<TrackAdapterItem> trackItemList = new ArrayList<>();
                for (Track track : result) {
                    TrackAdapterItem item = new TrackAdapterItem();
                    item.setTrack(track);
                    trackItemList.add(item);
                }
                if (trackAdapter == null) {
                    trackAdapter = new TrackAdapter(MainActivity.this, new ArrayList<>(), false);
                }
                trackAdapter.setPartContent(result.size() == ONE_PAGE_TRACKS_COUNT);
                trackAdapter.getList().addAll(trackItemList);
                setAdapter(trackAdapter);
            }

            @Override
            public void onFailure(JSONObject errorResponse, Throwable throwable, int code) {
                onSpotifyApiFail(errorResponse, throwable, code);
            }
        });
    }

    private void onSpotifyApiFail(JSONObject errorResponse, Throwable throwable, int code) {
        if (code == 401)
            authenticate();
        else
            Toast.makeText(this, code + ": " + throwable.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(int index) {
        showTracks(index);
    }

    @Override
    public void onClick(View v, int position) {
        switch (v.getId()) {
            case R.id.copyButton:
                copyTrackByIndexToClipboard(position);
                break;
            case R.id.downloadButton:
                processTrackOperation(position, TrackOperation.DOWNLOAD);
                break;
            case R.id.pauseButton:
                processTrackOperation(position, TrackOperation.PAUSE);
                break;
            case R.id.cancelButton:
                processTrackOperation(position, TrackOperation.CANCEL);
                break;
            case R.id.resumeButton:
                processTrackOperation(position, TrackOperation.RESUME);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v, int position) {
        copyTrackByIndexToClipboard(position);
        return true;
    }

    private void copyTrackByIndexToClipboard(int index) {
        if (trackAdapter == null) return;
        String value = trackAdapter.getList().get(index).track.getFullName();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Track name", value);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied: " + value, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Can't copy to clipboard", Toast.LENGTH_LONG).show();
        }
    }

    private void processTrackOperation(int index, TrackOperation trackOperation) {
        TrackAdapterItem item = trackAdapter.getList().get(index);
        switch (trackOperation) {
            case DOWNLOAD:
                lastIndex = index;
                if (!checkWriteFilePermission()) return;
                if (item.asyncTask != null) item.asyncTask.cancel(true);
                item.asyncTask = (TrackDownloaderAsyncTask) new TrackDownloaderAsyncTask(trackAdapter).execute(item);
                break;
            case CANCEL:
                if (item.asyncTask != null) {
                    Log.i("AsyncTask", "cancel(true)");
                    item.asyncTask.cancel(true);
                }
                break;
        }
    }

    private boolean checkWriteFilePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, grantedPermissionRequestCode);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            processTrackOperation(lastIndex, TrackOperation.DOWNLOAD);
        } else {
            Toast.makeText(this, "I will not download anything for you until you grant me to write files into your phone", Toast.LENGTH_LONG).show();
        }
    }

    private enum TrackOperation {DOWNLOAD, PAUSE, CANCEL, RESUME}

    private Long lastBackPressed;
    @Override
    public void onBackPressed() {
        boolean tracksVisible = ((RecyclerView)findViewById(R.id.recyclerView)).getAdapter() instanceof TrackAdapter;
        boolean backPressTimeDifferenceIsLarge = lastBackPressed==null||System.currentTimeMillis()-lastBackPressed>2000;
        lastBackPressed = System.currentTimeMillis();
        if (tracksVisible&&backPressTimeDifferenceIsLarge){
            showPlaylists();
        } else {
            super.onBackPressed();
        }
    }
}
