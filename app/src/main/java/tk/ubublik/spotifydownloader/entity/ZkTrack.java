package tk.ubublik.spotifydownloader.entity;

public class ZkTrack {
    private String artist;
    private String name;
    private int id;
    private int duration;
    private String downloadLink;

    public ZkTrack() {
    }

    public ZkTrack(String artist, String name, int id, int duration, String downloadLink) {
        this.artist = artist;
        this.name = name;
        this.id = id;
        this.duration = duration;
        this.downloadLink = downloadLink;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }
}
