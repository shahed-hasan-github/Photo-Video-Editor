package infiapp.com.videomaker.retrofit;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import infiapp.com.videomaker.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClientData extends AppCompatActivity {
    public static SwUserService getInterface() {
        Retrofit retrofit;
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS);


        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(Level.BODY);
            httpClient.addInterceptor(httpLoggingInterceptor);
        }
        retrofit = new Builder().baseUrl("http://flickapp9.com/Infiapp/AdminPanelV4/").addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
        return retrofit.create(SwUserService.class);
    }
}
