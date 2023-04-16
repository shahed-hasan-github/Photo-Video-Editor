package infiapp.com.videomaker.retrofit;

import com.google.gson.JsonObject;

import infiapp.com.videomaker.model.ModelAdmanger;
import infiapp.com.videomaker.model.ModelCategoryResponse;
import infiapp.com.videomaker.model.ModelOptionsResponse;
import infiapp.com.videomaker.model.ModelVideoResponce;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SwUserService {

    //categoery wise data api call
    @POST("getdatacategorywise1.php")
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    Call<ModelVideoResponce> getCatVideo(@Body JsonObject jsonObject);

    @POST("ooption.php")
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    Call<ModelOptionsResponse> getCatVideoOoptions(@Body JsonObject jsonObject);

    //get categoery api call
    @POST("getallcategory.php")
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    Call<ModelCategoryResponse> getAllCategory(@Body JsonObject jsonObject);

    //download file api
    @POST("download.php")
    Call<JsonObject> updateDownloads(@Body JsonObject jsonObject);

    //get categoery api call
    @POST("adssettings.php")
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    Call<ModelAdmanger> getAdsid(@Body JsonObject jsonObject);
}
