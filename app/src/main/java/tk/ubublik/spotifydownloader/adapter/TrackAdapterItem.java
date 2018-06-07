package tk.ubublik.spotifydownloader.adapter;

import tk.ubublik.spotifydownloader.async.TrackDownloaderAsyncTask;
import tk.ubublik.spotifydownloader.entity.Track;
import tk.ubublik.spotifydownloader.entity.ZkTrack;

public class TrackAdapterItem {
    public enum ProgressMode {DOWNLOAD, PAUSE, ERROR, DONE}

    public TrackDownloaderAsyncTask asyncTask;
    public Track track;
    public ZkTrack zkTrack;
    public double progress = 0;
    public ProgressMode progressMode = ProgressMode.PAUSE;
    public String errorText;
    public boolean copyVisible = true, downloadVisible = true, cancelVisible = false, pauseVisible = false, resumeVisible = false;

    public TrackAdapterItem() {
    }

    public TrackAdapterItem(TrackDownloaderAsyncTask asyncTask, Track track, ZkTrack zkTrack, double progress,
                            ProgressMode progressMode, String errorText, boolean copyVisible, boolean downloadVisible,
                            boolean cancelVisible, boolean pauseVisible, boolean resumeVisible) {
        this.asyncTask = asyncTask;
        this.track = track;
        this.zkTrack = zkTrack;
        this.progress = progress;
        this.progressMode = progressMode;
        this.errorText = errorText;
        this.copyVisible = copyVisible;
        this.downloadVisible = downloadVisible;
        this.cancelVisible = cancelVisible;
        this.pauseVisible = pauseVisible;
        this.resumeVisible = resumeVisible;
    }

    public TrackDownloaderAsyncTask getAsyncTask() {
        return asyncTask;
    }

    public void setAsyncTask(TrackDownloaderAsyncTask asyncTask) {
        this.asyncTask = asyncTask;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public ZkTrack getZkTrack() {
        return zkTrack;
    }

    public void setZkTrack(ZkTrack zkTrack) {
        this.zkTrack = zkTrack;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public ProgressMode getProgressMode() {
        return progressMode;
    }

    public void setProgressMode(ProgressMode progressMode) {
        this.progressMode = progressMode;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public boolean isCopyVisible() {
        return copyVisible;
    }

    public void setCopyVisible(boolean copyVisible) {
        this.copyVisible = copyVisible;
    }

    public boolean isDownloadVisible() {
        return downloadVisible;
    }

    public void setDownloadVisible(boolean downloadVisible) {
        this.downloadVisible = downloadVisible;
    }

    public boolean isCancelVisible() {
        return cancelVisible;
    }

    public void setCancelVisible(boolean cancelVisible) {
        this.cancelVisible = cancelVisible;
    }

    public boolean isPauseVisible() {
        return pauseVisible;
    }

    public void setPauseVisible(boolean pauseVisible) {
        this.pauseVisible = pauseVisible;
    }

    public boolean isResumeVisible() {
        return resumeVisible;
    }

    public void setResumeVisible(boolean resumeVisible) {
        this.resumeVisible = resumeVisible;
    }
}
