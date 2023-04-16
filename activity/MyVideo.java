package infiapp.com.videomaker.activity;

import static android.os.Build.VERSION.SDK_INT;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import infiapp.com.videomaker.adapter.VideoAdapter;
import infiapp.com.videomaker.R;
import infiapp.com.videomaker.theme.util.FileUtils;
import infiapp.com.videomaker.util.Ads_Preference;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyVideo extends AppCompatActivity {

    List<String> videoPath = new ArrayList<>();
    RecyclerView videoListView;
    VideoAdapter videoAdapter;
    int flagVideo = 21;
    ImageView backIV;
    RelativeLayout header;

    LinearLayout banner_container;
    private AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_videos);

        banner_container = findViewById(R.id.banner_container);
        banner_container.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
            }
        });


        Log.e("hhhh", "videoLoader: " + videoPath);
        Log.e("hhhh", "222: " + String.valueOf(R.string.app_name));
        Log.e("hhhh", "112: " + Environment
                .getExternalStorageDirectory());

        init();

        videoLoader();

        backIV.setOnClickListener(v -> onBackPressed());

    }

    private void init() {

        header = findViewById(R.id.header);
        backIV = findViewById(R.id.back);
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    public void videoLoader() {
        getFromStorage();
        videoListView = (RecyclerView) findViewById(R.id.recyclerView);
        Log.e("hhhh", "videoLoader: " + videoPath);
        videoAdapter = new VideoAdapter(videoPath, MyVideo.this, (v, position) -> {

            Intent intent = new Intent(MyVideo.this, SaveVideoFileActivity.class);
            intent.putExtra("videourl", videoPath.get(position));
            startActivityForResult(intent, flagVideo);
        });

        videoListView.setLayoutManager(new GridLayoutManager(this, 2));
        videoListView.setItemAnimator(new DefaultItemAnimator());
        videoListView.setAdapter(videoAdapter);

    }

    public void getFromStorage() {
        String folder;

      /*  folder = FileUtils.filepath1
                + "/" + getResources().getString(R.string.app_name);*/

//        folder = FileUtils.filepath + "/" + getResources().getString(R.string.app_name);

        if (SDK_INT >= Build.VERSION_CODES.R) {
            folder = FileUtils.filepath1
                    + "/" + getResources().getString(R.string.app_name);
        } else folder = FileUtils.filepath + "/" + getResources().getString(R.string.app_name);

        File file = new File(folder);
        videoPath = new ArrayList<>();
        if (file.isDirectory()) {
            File[] listFile = file.listFiles();
            Arrays.sort(listFile, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].getAbsolutePath().contains(".mp4")) {
                    videoPath.add(listFile[i].getAbsolutePath());
                    Log.e("hhhh", "videoLoader: " + videoPath);

                }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == flagVideo) {
            videoAdapter.notifyDataSetChanged();
            videoLoader();
        }
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
