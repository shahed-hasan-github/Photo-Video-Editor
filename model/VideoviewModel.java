package infiapp.com.videomaker.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VideoviewModel implements Serializable {
    @SerializedName("id")
    String id;
    @SerializedName("title")
    String title;
    @SerializedName("category")
    String category;
    @SerializedName("video_thumb")
    String videoThumb;
    @SerializedName("video_link")
    String videoLink;
    @SerializedName("video_zip")
    String videoZip;

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideoThumb() {
        return videoThumb;
    }

    public void setVideoThumb(String videoThumb) {
        this.videoThumb = videoThumb;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getVideoZip() {
        return videoZip;
    }

    public void setVideoZip(String videoZip) {
        this.videoZip = videoZip;
    }
}
