package infiapp.com.videomaker.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelVideoResponce {
    @SerializedName("msg")
    public List<VideoviewModel> msg;
    @SerializedName("code")
    String code;

    public List<VideoviewModel> getMsg() {
        return msg;
    }

    public void setMsg(List<VideoviewModel> msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
