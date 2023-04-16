package infiapp.com.videomaker.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.File;

import infiapp.com.videomaker.BuildConfig;
import infiapp.com.videomaker.R;
import infiapp.com.videomaker.util.Ads_Preference;

import static infiapp.com.videomaker.util.MyAppUtils.setToast;


public class SaveVideoFileActivity extends AppCompatActivity {
    ImageView whatsapp;
    ImageView facebook;
    ImageView insta;
    ImageView more;
    ImageView playPuase;
    RelativeLayout rlPlaypause;
    ImageView back;
    SessionManager nvSessionManager;
    VideoView videoView;
    private String videoUrl = "";
    Activity context;

    LinearLayout banner_container;
    private AdView adView;
    private InterstitialAd interstitialAd;


    @SuppressLint({"ClickableViewAccessibility"})
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_saved);

        loadAd();
        banner_container = findViewById(R.id.banner_container);
        banner_container.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
            }
        });

        back = findViewById(R.id.back);
        videoView = findViewById(R.id.videoview);
        playPuase = findViewById(R.id.play_pause);
        rlPlaypause = findViewById(R.id.playing_status);
        whatsapp = findViewById(R.id.whatsapp);
        facebook = findViewById(R.id.facebook);
        insta = findViewById(R.id.insta);
        more = findViewById(R.id.more);
        context = this;

        nvSessionManager = new SessionManager(this);

        Intent intent1 = getIntent();
        if (intent1.getExtras() != null) {
            videoUrl = intent1.getStringExtra("videourl");
            Log.e("hhhh", "onCreate: " + videoUrl);
        }


        back.setOnClickListener(view ->
            onBackPressed()

        );

        CardView backHome = findViewById(R.id.home);


        if (!nvSessionManager.getBooleanData(SessionManager.prefAppRated)) {
            showRateDialog();
        }
        backHome.setOnClickListener(view -> {
           showInterstitial();


        });

        initVideo();

        whatsapp.setOnClickListener(view -> {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", new File(videoUrl));

            intent.setAction(Intent.ACTION_SEND);
            intent.setPackage("com.whatsapp");
            intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share) + context.getPackageName());
            intent.putExtra(Intent.EXTRA_STREAM, uriForFile);
            intent.setType("video/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                setToast(context, "Whtasapp not installed.");
            }


        });


        facebook.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", new File(videoUrl));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, uriForFile);
            String stringBuilder2 = getString(R.string.share) + getPackageName();
            intent.putExtra(Intent.EXTRA_TEXT, stringBuilder2);
            intent.setPackage("com.facebook.katana");
            try {

                startActivity(Intent.createChooser(intent, "Share Video..."));


            } catch (ActivityNotFoundException unused) {
                Toast.makeText(SaveVideoFileActivity.this, R.string.install_fb, Toast.LENGTH_LONG).show();
            }
        });


        this.insta.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", new File(videoUrl));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, uriForFile);
            String stringBuilder2 = getString(R.string.share) +
                    getPackageName();
            intent.putExtra(Intent.EXTRA_TEXT, stringBuilder2);
            intent.setPackage("com.instagram.android");
            try {
                startActivity(Intent.createChooser(intent, "Share Video..."));


            } catch (ActivityNotFoundException unused) {
                Toast.makeText(SaveVideoFileActivity.this, "Please Install Instagram", Toast.LENGTH_LONG).show();
            }
        });

        this.more.setOnClickListener(view -> {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("video/*");
            Log.e("vvvvv", "onCreate: "+videoUrl );
            Uri uriForFile = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", new File(videoUrl));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, uriForFile);
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share) + getPackageName());
            startActivity(Intent.createChooser(intent, "Share Your Video!"));


        });
    }


    public void showRateDialog() {
        new RatingDialog.Builder(this).title("Like New Wave 2021").positiveButtonTextColor(R.color.grey_500).negativeButtonTextColor(R.color.grey_500).playstoreUrl("https://play.google.com/store/apps/details?id=" + getPackageName()).onRatingBarFormSumbit(str -> {

        }).build().show();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initVideo() {

        if (getIntent() != null) {

            try {
                this.videoView.setVideoURI(Uri.parse(videoUrl));
                this.rlPlaypause.setVisibility(View.GONE);
                this.videoView.requestFocus();
                this.videoView.start();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        this.videoView.setOnCompletionListener(mediaPlayer -> videoView.start());
        this.videoView.setOnTouchListener((view, motionEvent) -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                rlPlaypause.setVisibility(View.VISIBLE);
                playPuase.setImageResource(R.drawable.ic_play_new);
                return false;
            }
            playPuase.setImageResource(R.drawable.ic_pause_new);
            videoView.start();
            new Handler(Looper.getMainLooper()).postDelayed(() -> rlPlaypause.setVisibility(View.GONE), 2000);
            return false;
        });
    }



    @Override
    public void onPause() {
        super.onPause();
        this.videoView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();

        loadAd();
        this.rlPlaypause.setVisibility(View.GONE);
        this.videoView.start();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SaveVideoFileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

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
                        SaveVideoFileActivity.this.interstitialAd = interstitialAd;

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
                                        SaveVideoFileActivity.this.interstitialAd = null;
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
        Intent intent = new Intent(SaveVideoFileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
}
