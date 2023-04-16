package infiapp.com.videomaker.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;

import infiapp.com.videomaker.BuildConfig;
import infiapp.com.videomaker.retrofit.APIClientData;
import infiapp.com.videomaker.model.ModelAdmanger;
import infiapp.com.videomaker.MyApplication;
import infiapp.com.videomaker.R;
import infiapp.com.videomaker.util.Ads_Preference;
import infiapp.com.videomaker.util.Animatee;
import infiapp.com.videomaker.util.KSUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String[] permissionsList = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    LinearLayout linearStart;
    LinearLayout linearShare;
    LinearLayout linearRateUs;
    LinearLayout lyrical_video;
    LinearLayout llAlbum;
    TextView textPrivacyPolicy;
    String link;
    private RelativeLayout adContainerView;
    private AdView adView;
    private InterstitialAd interstitialAd;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadAd();


        File f1 = new File(Environment.getExternalStorageDirectory(), getResources().getString(R.string.app_name));
        if (!f1.exists()) {
            f1.mkdirs();
        }
        adContainerView = findViewById(R.id.banner_container);
        adContainerView.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
            }
        });

        SharedPreferences prefs = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        link = prefs.getString("link", "No name defined");
        getAdsid();
        init();

    }


    void init() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        MyApplication.VIDEO_HEIGHT = displaymetrics.widthPixels;
        MyApplication.VIDEO_WIDTH = displaymetrics.widthPixels;


        linearStart = findViewById(R.id.linearStart);
        linearStart.setOnClickListener(this);

        lyrical_video = findViewById(R.id.lyrical_video);
        lyrical_video.setOnClickListener(this);

        linearRateUs = findViewById(R.id.linearRateUs);
        linearRateUs.setOnClickListener(this);

        linearShare = findViewById(R.id.linearShare);
        linearShare.setOnClickListener(this);

        textPrivacyPolicy = findViewById(R.id.textPrivacyPolicy);
        textPrivacyPolicy.setOnClickListener(this);

        llAlbum = findViewById(R.id.llAlbum);
        llAlbum.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearStart:

                status = "photovideo";
                showInterstitial();

                break;


            case R.id.linearRateUs:
                rateUs();
                break;

            case R.id.linearShare:
                shareApp();
                break;

            case R.id.lyrical_video:

                status = "lyrical_video";
                showInterstitial();
                break;

            case R.id.textPrivacyPolicy:
                Intent i1 = new Intent(MainActivity.this, PrivacyActivity.class);
                startActivityForResult(i1, 0);

                break;

            case R.id.llAlbum:

                if (!checkPermissions(this, permissionsList)) {
                    ActivityCompat.requestPermissions(this, permissionsList, 21);
                } else {
                    KSUtil.fromAlbum = true;
                    Intent i2 = new Intent(MainActivity.this, MyVideo.class);
                    startActivityForResult(i2, 0);
                    Animatee.animateSlideUp(MainActivity.this);
                }

                break;

            default:
                break;
        }
    }

    private void getAdsid() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("app", BuildConfig.APPLICATION_ID);
        jsonObject.addProperty("cat", "Latest");

        APIClientData.getInterface().getAdsid(jsonObject).enqueue(new Callback<ModelAdmanger>() {
            public void onFailure(@NotNull Call<ModelAdmanger> call, @NotNull Throwable th) {
                Log.e("modelll", "onResponse: " + "nooo");
            }

            public void onResponse(@NotNull Call<ModelAdmanger> call, @NotNull Response<ModelAdmanger> response) {
                ModelAdmanger modelCategoryResponse = response.body();


                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();


                editor.putString("status", "1");  // status
//                editor.putString("google_banner", modelCategoryResponse.getMsg().get(0).getGoogleBannerAdId());  // google banner
                editor.putString("google_banner", "ca-app-pub-3940256099942544/6300978111");  // google banner
                editor.putString("google_interstitial", "ca-app-pub-3940256099942544/1033173712");  // google interstitial

                editor.apply();

            }
        });
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }


    public void moreApp() {
        startActivity(new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/dev?id=8974970846236633270")));
    }

    public void shareApp() {
        String shareBody = "https://play.google.com/store/apps/details?id="
                + getApplicationContext().getPackageName();

        Intent sharingIntent = new Intent(
                android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent
                .putExtra(android.content.Intent.EXTRA_SUBJECT,
                        "(This app is for making beautiful video from photos. Open it in Google Play Store to Download the Application)");

        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void rateUs() {
        Uri uri = Uri.parse("market://details?id="
                + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                | Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + getApplicationContext().getPackageName())));
        }
    }


    public static boolean checkPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == ImagePickerActivity.PICKER_REQUEST_CODE) {
            KSUtil.videoPathList.clear();
            KSUtil.videoPathList = data.getExtras().getStringArrayList(ImagePickerActivity.KEY_DATA_RESULT);
            if (KSUtil.videoPathList != null && !KSUtil.videoPathList.isEmpty()) {
                StringBuilder sb = new StringBuilder("");
                for (int i = 0; i < KSUtil.videoPathList.size(); i++) {
                    sb.append("Image Path" + (i + 1) + ":" + KSUtil.videoPathList.get(i));
                    sb.append("\n");

                }
                Log.e("Image", sb.toString());

                Intent i2 = new Intent(MainActivity.this, SwapImageActivity.class);
                startActivityForResult(i2, 0);

            }
        }
    }

    private void loadBanner() {
        adView = new AdView(this);
        Log.e("klklkk", "loadBanner: " + new Ads_Preference(this).get_admob_banner_id());
        adView.setAdUnitId(new Ads_Preference(this).get_admob_banner_id());
        adContainerView.removeAllViews();
        adContainerView.addView(adView);

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

        float adWidthPixels = adContainerView.getWidth();

        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        Log.e("hhhhhh", "loadAd: " + new Ads_Preference(this).get_admob_interstitial_id());
        InterstitialAd.load(
                this,
                new Ads_Preference(this).get_admob_interstitial_id(),
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        MainActivity.this.interstitialAd = interstitialAd;

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.

                                        gotonext();

                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        MainActivity.this.interstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error

                        interstitialAd = null;

                        String error =
                                String.format(
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());

                    }
                });
    }

    private void gotonext() {
        if (status.equals("photovideo")) {
            if (!checkPermissions(this, permissionsList)) {
                ActivityCompat.requestPermissions(this, permissionsList, 21);
            } else {
                KSUtil.fromAlbum = false;
                Intent mIntent = new Intent(MainActivity.this, ImagePickerActivity.class);
                mIntent.putExtra(ImagePickerActivity.KEY_LIMIT_MAX_IMAGE, 30);
                mIntent.putExtra(ImagePickerActivity.KEY_LIMIT_MIN_IMAGE, 4);

                startActivityForResult(mIntent, ImagePickerActivity.PICKER_REQUEST_CODE);
            }
        } else if (status.equals("lyrical_video")) {

            Intent i = new Intent(MainActivity.this, LibraryActivity.class);
            startActivity(i);
        }
    }

    private void showInterstitial() {
        SharedPreferences prefs = getSharedPreferences("click_counter", MODE_PRIVATE);
        int cc_count = prefs.getInt("count", 0);

        cc_count = cc_count + 1;
        int ads_click = Integer.parseInt(new Ads_Preference(this).getADMOB_ADS_CLICK_COUNT());

        SharedPreferences.Editor editor11 = getSharedPreferences("click_counter", MODE_PRIVATE).edit();
        editor11.putInt("count", cc_count);
        editor11.apply();

        if (ads_click <= cc_count) {

            editor11.putInt("count", 0);
            editor11.apply();


            if (interstitialAd != null) {
                interstitialAd.show(this);
            } else {

                gotonext();

            }


        } else {
            gotonext();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        loadAd();
    }

}
