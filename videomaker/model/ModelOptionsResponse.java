package infiapp.com.videomaker.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelOptionsResponse {
    @SerializedName("ReportData")
    public List<OptionModel> reportData;

    public List<OptionModel> getReportData() {
        return this.reportData;
    }

    public void setReportData(List<OptionModel> arrayList) {
        this.reportData = arrayList;
    }
}
