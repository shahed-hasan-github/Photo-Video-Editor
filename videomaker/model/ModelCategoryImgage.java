package infiapp.com.videomaker.model;

import com.google.gson.annotations.SerializedName;

public class ModelCategoryImgage {
    @SerializedName("id")
    String id;
    @SerializedName("category")
    String category;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
