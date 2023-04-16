package infiapp.com.videomaker.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelCategoryResponse {
    @SerializedName("msg")
    public List<ModelCategoryImgage> msg;
    @SerializedName("code")
    String code;

    public List<ModelCategoryImgage> getMsg() {
        return msg;
    }

    public void setMsg(List<ModelCategoryImgage> msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
