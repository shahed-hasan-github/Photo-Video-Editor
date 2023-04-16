package infiapp.com.videomaker.model;

public class MusicData {

    public String songDownloadUri = "";
    public boolean isAvailableOffline = true;
    public long trackId;
    public String trackTitle;
    public String trackdata;
    public String trackDisplayName;
    public long trackDuration;
    public boolean isDownloading = false;

    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    public String getTrackdata() {
        return trackdata;
    }

    public void setTrackdata(String trackdata) {
        this.trackdata = trackdata;
    }

    public String getTrackDisplayName() {
        return trackDisplayName;
    }

    public void setTrackDisplayName(String trackDisplayName) {
        this.trackDisplayName = trackDisplayName;
    }

    public long getTrackDuration() {
        return trackDuration;
    }

    public void setTrackDuration(long trackDuration) {
        this.trackDuration = trackDuration;
    }
}
