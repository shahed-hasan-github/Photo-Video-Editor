package infiapp.com.videomaker.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import infiapp.com.videomaker.BuildConfig;
import infiapp.com.videomaker.adapter.CategoryAdapter;
import infiapp.com.videomaker.adapter.VideoViewAdapter;
import infiapp.com.videomaker.retrofit.APIClientData;
import infiapp.com.videomaker.util.Ads_Preference;
import infiapp.com.videomaker.util.MyAppUtils;
import infiapp.com.videomaker.util.UtilsPermission;
import infiapp.com.videomaker.model.ModelCategoryResponse;
import infiapp.com.videomaker.model.ModelVideoResponce;
import infiapp.com.videomaker.model.VideoviewModel;
import infiapp.com.videomaker.MyApplication;
import infiapp.com.videomaker.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LibraryActivity extends AppCompatActivity {


    private Activity context;
    private ArrayList<VideoviewModel> videoviewModel = new ArrayList<>();
    private RecyclerView rvAllCategory = null;
    private RecyclerView rvAllVideos = null;
    private LinearLayout llNoDataAvailable = null;
    private String selectedCategory = "Latest";
    private int page = 1;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private boolean loading = false;
    private LinearLayout llProgressbar;
    private LottieAnimationView lottieAnimationView;
    private VideoViewAdapter nvVideoViewAdapter;
    SwipeRefreshLayout swiperefreshLayout;
    private StaggeredGridLayoutManager layoutManager;
    LottieAnimationView lottiAnimationNodata;
    LinearLayout llBack;
    String link;

    LinearLayout banner_container;
    private AdView adView;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.contain_main);
        context = this;

        banner_container = findViewById(R.id.banner_container);
        banner_container.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
            }
        });

        llProgressbar =  findViewById(R.id.llProgressbar);
        swiperefreshLayout = findViewById(R.id.swiperefreshLayout);
        lottiAnimationNodata =  findViewById(R.id.lottiAnimationNodata);
        lottieAnimationView =  findViewById(R.id.animationView);
        rvAllCategory =  findViewById(R.id.rvAllCategory);
        llNoDataAvailable =  findViewById(R.id.llNoDataAvailable);
        rvAllVideos =  findViewById(R.id.rvAllVideos);
        llBack = findViewById(R.id.llBack);



        lottiAnimationNodata.playAnimation();


        SharedPreferences prefs = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        link = prefs.getString("link", "No name defined");

        new UtilsPermission(context).checkPermissionsGranted();
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
        copyFile("blankimage.jpg");

        llBack.setOnClickListener(view -> finish());

        swiperefreshLayout.setOnRefreshListener(() -> {

            page = 1;
            videoviewModel.clear();
            nvVideoViewAdapter.notifyDataSetChanged();

            getVideos(selectedCategory);

            swiperefreshLayout.setRefreshing(false);

        });


        recyclerViewsetup();

    }


    private void copyFile(String str) {
        try {
            InputStream open = getAssets().open(str);
            StringBuilder sb = new StringBuilder();
            sb.append(getCacheDir());
            sb.append("/");
            sb.append(str);
            try (FileOutputStream fos = new FileOutputStream(sb.toString())) {
                byte[] bArr = new byte[1024];
                while (true) {
                    int read = open.read(bArr);
                    if (read != -1) {
                        fos.write(bArr, 0, read);
                    } else {
                        open.close();
                        fos.flush();
                        fos.close();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void recyclerViewsetup() {

        layoutManager = new StaggeredGridLayoutManager(2, 1);
        rvAllVideos.setLayoutManager(layoutManager);
        rvAllVideos.setHasFixedSize(true);


        rvAllVideos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                int[] firstVisibleItems = null;
                firstVisibleItems = layoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                if (firstVisibleItems != null && firstVisibleItems.length > 0) {
                    pastVisibleItems = firstVisibleItems[0];
                }

                if (!loading) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loading = true;
                        page = page + 1;

                        llProgressbar.setVisibility(View.VISIBLE);

                        new Handler(getMainLooper()).postDelayed(() -> {

                            loadMoreData(selectedCategory);

                            llProgressbar.setVisibility(View.GONE);

                        }, 100);

                    }
                }

            }
        });

        if (MyAppUtils.isConnectingToInternet(context)) {
            lottieAnimationView.setVisibility(View.GONE);
            lottieAnimationView.pauseAnimation();
            getCategory();
            getVideos("Latest");
        } else {
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.playAnimation();
        }


    }

    private void loadMoreData(String str) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("app", BuildConfig.APPLICATION_ID);
        jsonObject.addProperty("cat", str);
        jsonObject.addProperty("page", page);
        try {
            APIClientData.getInterface().getCatVideo(jsonObject).enqueue(new Callback<ModelVideoResponce>() {
                @Override
                public void onFailure(@NotNull Call<ModelVideoResponce> call, @NotNull Throwable th) {
//dosomething
                }

                @Override
                public void onResponse(@NotNull Call<ModelVideoResponce> call, @NotNull Response<ModelVideoResponce> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        ModelVideoResponce modelVideoResponce = response.body();
                        int i = 0;

                        llNoDataAvailable.setVisibility(View.GONE);
                        llProgressbar.setVisibility(View.GONE);
                        rvAllVideos.setVisibility(View.VISIBLE);

                        while (i < modelVideoResponce.getMsg().size()) {
                            if (i % 5 == 2) {
                                videoviewModel.add(null);
                            }
                            videoviewModel.add(modelVideoResponce.getMsg().get(i));
                            i++;
                        }

                        nvVideoViewAdapter.setDataList(videoviewModel);
                        nvVideoViewAdapter.notifyDataSetChanged();
                        loading = false;


                    } else {
                        Toast.makeText(context, "No video found", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void getCategory() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("app", BuildConfig.APPLICATION_ID);
        jsonObject.addProperty("cat", "Latest");

        APIClientData.getInterface().getAllCategory(jsonObject).enqueue(new Callback<ModelCategoryResponse>() {
            public void onFailure(@NotNull Call<ModelCategoryResponse> call, @NotNull Throwable th) {
//dosomething
            }

            public void onResponse(@NotNull Call<ModelCategoryResponse> call, @NotNull Response<ModelCategoryResponse> response) {
                ModelCategoryResponse modelCategoryResponse = response.body();

                if (modelCategoryResponse == null || modelCategoryResponse.getMsg() == null || modelCategoryResponse.getMsg().isEmpty()) {
                    llNoDataAvailable.setVisibility(View.VISIBLE);
                    return;
                }


                llNoDataAvailable.setVisibility(View.GONE);
                CategoryAdapter nvCategoryAdapter = new CategoryAdapter(context, modelCategoryResponse.getMsg());
                rvAllCategory.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false));
                rvAllCategory.setAdapter(nvCategoryAdapter);

                nvCategoryAdapter.setOnItemClickListener((position, catData) -> {

                    page = 1;
                    selectedCategory = catData.get(position).getCategory();
                    Log.e("sssss", "onResponse: " + selectedCategory);
                    getVideos(selectedCategory);
                    nvCategoryAdapter.notifyDataSetChanged();

                });

            }
        });
    }

    public void getVideos(String str) {

        Log.e("sssss", "onResponse: " + str + page);
        llProgressbar.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("app", BuildConfig.APPLICATION_ID);
        jsonObject.addProperty("cat", str);
        jsonObject.addProperty("page", page);
        try {
            APIClientData.getInterface().getCatVideo(jsonObject).enqueue(new Callback<ModelVideoResponce>() {
                public void onFailure(Call<ModelVideoResponce> call, Throwable th) {
                    llProgressbar.setVisibility(View.GONE);
                    llNoDataAvailable.setVisibility(View.VISIBLE);
                    rvAllVideos.setVisibility(View.GONE);

                }

                public void onResponse(@NotNull Call<ModelVideoResponce> call, @NotNull Response<ModelVideoResponce> response) {

                    try {
                        ModelVideoResponce modelVideoResponce = response.body();
                        int i = 0;
                        if (modelVideoResponce.getCode() == null || !modelVideoResponce.getCode().equals("200")) {
                            llNoDataAvailable.setVisibility(View.VISIBLE);
                            rvAllVideos.setVisibility(View.GONE);
                            llProgressbar.setVisibility(View.GONE);
                            return;
                        }
                        llNoDataAvailable.setVisibility(View.GONE);
                        llProgressbar.setVisibility(View.GONE);
                        rvAllVideos.setVisibility(View.VISIBLE);
                        videoviewModel = new ArrayList();
                        while (i < modelVideoResponce.getMsg().size()) {
                            if (i % 5 == 0 && i != 0) {
                                videoviewModel.add(null);
                            }
                            videoviewModel.add(modelVideoResponce.getMsg().get(i));
                            i++;
                        }
                        nvVideoViewAdapter = new VideoViewAdapter(context, videoviewModel);
                        layoutManager = new StaggeredGridLayoutManager(2, 1);
                        rvAllVideos.setLayoutManager(layoutManager);
                        rvAllVideos.setAdapter(nvVideoViewAdapter);

                    } catch (Exception e) {
                        llProgressbar.setVisibility(View.GONE);
                        llNoDataAvailable.setVisibility(View.VISIBLE);
                        rvAllVideos.setVisibility(View.GONE);
                    }

                }
            });
        } catch (Exception e) {

            e.printStackTrace();
        }
    }




    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.click = false;
    }

    private void loadBanner() {

        adView = new AdView(this);
        adView.setAdUnitId(new Ads_Preference(this).get_admob_banner_id());
        banner_container.removeAllViews();
        banner_container.addView(adView);

        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);

    }

    private AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = banner_container.getWidth();

        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }


}

