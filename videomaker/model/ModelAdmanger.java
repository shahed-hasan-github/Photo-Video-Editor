package infiapp.com.videomaker.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelAdmanger {

    @SerializedName("msg")
    public List<Admanger> msg;
    @SerializedName("code")
    String code;

    public List<Admanger> getMsg() {
        return msg;
    }

    public void setMsg(List<Admanger> msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
