package infiapp.com.videomaker.model;

import com.google.gson.annotations.SerializedName;

public class OptionModel {
    @SerializedName("colorkeyrand")
    public String colorKeyrand;
    @SerializedName("duration")
    public String duration;
    @SerializedName("ff_cmd")
    public String ffCmd;
    @SerializedName("ff_cmd_user")
    public String ffCmdUser;
    @SerializedName("ff_cmd_video")
    public String ffCmdVideo;
    @SerializedName("imglist")
    public String imgList;
    @SerializedName("imgratio")
    public String imgRatio;
    @SerializedName("total_image")
    public String totalImage;
    @SerializedName("video_resolution")
    public String videoResolution;

    public String getColorKeyrand() {
        return colorKeyrand;
    }

    public void setColorKeyrand(String colorKeyrand) {
        this.colorKeyrand = colorKeyrand;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFfCmd() {
        return ffCmd;
    }

    public void setFfCmd(String ffCmd) {
        this.ffCmd = ffCmd;
    }

    public String getFfCmdUser() {
        return ffCmdUser;
    }

    public void setFfCmdUser(String ffCmdUser) {
        this.ffCmdUser = ffCmdUser;
    }

    public String getFfCmdVideo() {
        return ffCmdVideo;
    }

    public void setFfCmdVideo(String ffCmdVideo) {
        this.ffCmdVideo = ffCmdVideo;
    }

    public String getImgList() {
        return imgList;
    }

    public void setImgList(String imgList) {
        this.imgList = imgList;
    }

    public String getImgRatio() {
        return imgRatio;
    }

    public void setImgRatio(String imgRatio) {
        this.imgRatio = imgRatio;
    }

    public String getTotalImage() {
        return totalImage;
    }

    public void setTotalImage(String totalImage) {
        this.totalImage = totalImage;
    }

    public String getVideoResolution() {
        return videoResolution;
    }

    public void setVideoResolution(String videoResolution) {
        this.videoResolution = videoResolution;
    }


}
