package tk.ubublik.spotifydownloader.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import tk.ubublik.spotifydownloader.R;
import tk.ubublik.spotifydownloader.entity.Playlist;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private OnPlaylistClickListener listener;
    private ArrayList<Playlist> playlists;

    public PlaylistAdapter(OnPlaylistClickListener listener, ArrayList<Playlist> playlists) {
        this.listener = listener;
        this.playlists = playlists;
    }

    public OnPlaylistClickListener getListener() {
        return listener;
    }

    public void setListener(OnPlaylistClickListener listener) {
        this.listener = listener;
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_item, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        holder.setText(playlists.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    @FunctionalInterface
    public interface OnPlaylistClickListener {
        void onClick(int index);
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView playlistNameView;
        private WeakReference<OnPlaylistClickListener> listenerReference;

        PlaylistViewHolder(View itemView, OnPlaylistClickListener listener) {
            super(itemView);
            this.listenerReference = new WeakReference<>(listener);
            itemView.setOnClickListener(this);
            playlistNameView = itemView.findViewById(R.id.playlistNameView);
        }

        public void setText(String text){
            playlistNameView.setText(text);
        }


        @Override
        public void onClick(View v) {
            Object listenerObject = listenerReference.get();
            if (listenerObject != null){
                ((OnPlaylistClickListener) listenerObject).onClick(getAdapterPosition());
            }
        }
    }
}
