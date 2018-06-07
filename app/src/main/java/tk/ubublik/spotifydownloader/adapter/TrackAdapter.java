package tk.ubublik.spotifydownloader.adapter;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import tk.ubublik.spotifydownloader.R;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    private OnListItemButtonClickListener clickListener;
    private ArrayList<TrackAdapterItem> list;
    private boolean partContent;

    public TrackAdapter(OnListItemButtonClickListener clickListener, ArrayList<TrackAdapterItem> list, boolean partContent) {
        this.clickListener = clickListener;
        this.list = list;
        this.partContent = partContent;
    }

    public OnListItemButtonClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(OnListItemButtonClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public ArrayList<TrackAdapterItem> getList() {
        return list;
    }

    public void setList(ArrayList<TrackAdapterItem> list) {
        this.list = list;
    }

    public boolean isPartContent() {
        return partContent;
    }

    public void setPartContent(boolean partContent) {
        this.partContent = partContent;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrackViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.track_item, parent, false), clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        TrackAdapterItem item = list.get(position);
        holder.setErrorText(item.errorText);
        holder.setProgressBarMode(item.progressMode);
        holder.setProgressBarValue(item.progress);
        holder.setTrackName(item.track.getFullName());
        holder.setButtonsVisibility(item.copyVisible, item.downloadVisible, item.cancelVisible, item.pauseVisible, item.resumeVisible);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @FunctionalInterface
    public interface OnListItemButtonClickListener {
        void onClick(View v, int position);

        default boolean onLongClick(View v, int position) {
            return false;
        }
    }

    public static class TrackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView trackNameView, errorTextView;
        private ImageButton copyButton, downloadButton, cancelButton, pauseButton, resumeButton;
        private ProgressBar progressBar;
        private WeakReference<OnListItemButtonClickListener> listenerReference;

        public TrackViewHolder(View view, OnListItemButtonClickListener listener) {
            super(view);
            findIds(view);
            setButtonsListeners();
            this.listenerReference = new WeakReference<>(listener);
        }

        private void findIds(View parent) {
            trackNameView = parent.findViewById(R.id.trackNameView);
            errorTextView = parent.findViewById(R.id.errorTextView);
            copyButton = parent.findViewById(R.id.copyButton);
            downloadButton = parent.findViewById(R.id.downloadButton);
            cancelButton = parent.findViewById(R.id.cancelButton);
            pauseButton = parent.findViewById(R.id.pauseButton);
            resumeButton = parent.findViewById(R.id.resumeButton);
            progressBar = parent.findViewById(R.id.downloadProgressBar);
        }

        private void setButtonsListeners() {
            copyButton.setOnClickListener(this);
            downloadButton.setOnClickListener(this);
            cancelButton.setOnClickListener(this);
            pauseButton.setOnClickListener(this);
            resumeButton.setOnClickListener(this);
        }

        public void setProgressBarValue(double value) {
            progressBar.setProgress((int) (progressBar.getMax() * value));
        }

        private int getModeColorId(TrackAdapterItem.ProgressMode mode) {
            switch (mode) {
                case DOWNLOAD:
                    return R.color.progressDownload;
                case PAUSE:
                    return R.color.progressPause;
                case ERROR:
                    return R.color.progressError;
                case DONE:
                    return R.color.progressDone;
                default:
                    throw new IllegalArgumentException("Unsupported mode: " + mode);
            }
        }

        public void setProgressBarMode(TrackAdapterItem.ProgressMode mode) {
            int color = ContextCompat.getColor(progressBar.getContext(), getModeColorId(mode));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                progressBar.setProgressTintList(ColorStateList.valueOf(color));
            } else {
                progressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        }

        public void setButtonsVisibility(boolean copy, boolean download, boolean cancel, boolean pause, boolean resume) {
            copyButton.setVisibility(copy ? View.VISIBLE : View.GONE);
            downloadButton.setVisibility(download ? View.VISIBLE : View.GONE);
            cancelButton.setVisibility(cancel ? View.VISIBLE : View.GONE);
            pauseButton.setVisibility(pause ? View.VISIBLE : View.GONE);
            resumeButton.setVisibility(resume ? View.VISIBLE : View.GONE);
        }

        public void setErrorText(String text) {
            if (text == null || text.isEmpty()) {
                errorTextView.setVisibility(View.GONE);
            } else {
                errorTextView.setVisibility(View.VISIBLE);
                errorTextView.setText(text);
            }
        }

        public void setTrackName(String name) {
            trackNameView.setText(name);
        }

        public void clearError() {
            setErrorText(null);
        }

        @Override
        public void onClick(View v) {
            Object listenerObject = listenerReference.get();
            if (listenerObject != null) ((OnListItemButtonClickListener)listenerObject).onClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            Object listenerObject = listenerReference.get();
            return listenerObject != null && ((OnListItemButtonClickListener) listenerObject).onLongClick(v, getAdapterPosition());
        }
    }
}
