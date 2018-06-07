package tk.ubublik.spotifydownloader.entity;

public class Track {

    private String id;
    private String name;
    private String previewUrl;
    private String[] artists;
    private long duration;

    public String getFullName(){
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" - ");
        boolean hasPrevious = false;
        for (String artist: artists){
            if (hasPrevious) sb.append(", ");
            sb.append(artist);
            hasPrevious = true;
        }
        return sb.toString();
    }

    public Track() {
    }

    public Track(String id, String name, String previewUrl, String[] artists, long duration) {
        this.id = id;
        this.name = name;
        this.previewUrl = previewUrl;
        this.artists = artists;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String[] getArtists() {
        return artists;
    }

    public void setArtists(String[] artists) {
        this.artists = artists;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
