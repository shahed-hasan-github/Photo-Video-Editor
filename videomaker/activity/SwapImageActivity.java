package infiapp.com.videomaker.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import infiapp.com.videomaker.MyApplication;
import infiapp.com.videomaker.gridview.AbsDynamicGridView;
import infiapp.com.videomaker.R;
import infiapp.com.videomaker.util.Ads_Preference;
import infiapp.com.videomaker.util.EditImageUtil;
import infiapp.com.videomaker.adapter.SwapperAdapter;
import infiapp.com.videomaker.util.KSUtil;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.infiapp.imagelibrary.editimage.EditImageActivity;

import java.io.File;
import java.util.ArrayList;

import infiapp.com.videomaker.model.ImageData;
import infiapp.com.videomaker.theme.util.FileUtils;

public class SwapImageActivity extends AppCompatActivity implements View.OnClickListener {
    AbsDynamicGridView absDynamicGridView;
    SwapperAdapter zapperAdapter;
    ImageView back;
    ImageView done;
    ArrayList<String> local;
    ArrayList<String> local2;

    File myDir;
    MyApplication application;
    RelativeLayout header;
    LinearLayout banner_container;
    private AdView adView;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swapimage);

        loadAd();

        banner_container = findViewById(R.id.banner_container);
        banner_container.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
            }
        });

        application = MyApplication.getInstance();
        application.isEditEnable = true;

        String root = FileUtils.appDirectory + "/";

        myDir = new File(root + "imagesfolder");

        if (myDir.exists()) {

        } else {
            myDir.mkdirs();
        }

        init();
        initGrid();
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    void init() {
        header = (RelativeLayout) findViewById(R.id.header);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);

        done = (ImageView) findViewById(R.id.done);
        done.setOnClickListener(this);

    }

    void initGrid() {
        absDynamicGridView = (AbsDynamicGridView) findViewById(R.id.dynamicGrid);

        zapperAdapter = new SwapperAdapter(SwapImageActivity.this, KSUtil.videoPathList, 2);
        absDynamicGridView.setAdapter(zapperAdapter);

        absDynamicGridView.setOnDragListener(new AbsDynamicGridView.OnDragListener() {
            @Override
            public void onDragStarted(int position) {
//dosomething
            }

            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {
                if (oldPosition == newPosition)
                    return;
                if (oldPosition > newPosition) {
                    local = new ArrayList<>();
                    local.clear();
                    local.add(KSUtil.videoPathList.get(oldPosition));

                    for (int i = oldPosition; i > newPosition; i--) {
                        KSUtil.videoPathList.set(i, KSUtil.videoPathList.get(i - 1));
                    }

                    KSUtil.videoPathList.set(newPosition, local.get(0));

                } else {

                    local2 = new ArrayList<>();
                    local2.clear();
                    local2.add(KSUtil.videoPathList.get(oldPosition));

                    for (int i = oldPosition; i < newPosition; i++) {
                        KSUtil.videoPathList.set(i, KSUtil.videoPathList.get(i + 1));
                    }

                    KSUtil.videoPathList.set(newPosition, local2.get(0));

                }
                zapperAdapter.notifyDataSetChanged();


                absDynamicGridView.stopEditMode();


            }
        });
        absDynamicGridView.setOnItemLongClickListener((parent, view, position, id) -> {

            absDynamicGridView.startEditMode(position);

            return true;
        });
        absDynamicGridView.setOnItemClickListener((parent, view, position, id) -> {

        });


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;

            case R.id.done:

                showInterstitial();

                break;
        }
    }

    public void gotoMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    class Done extends AsyncTask<Void, Void, Void> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(SwapImageActivity.this);
            pd.setMessage("Loading....");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            application.isEditEnable = false;

            application.selectedImages.clear();
            for (int i = 0; i < KSUtil.videoPathList.size(); i++) {
                ImageData idata = new ImageData();
                idata.setImagePath(KSUtil.videoPathList.get(i));
                application.selectedImages.add(i, idata);
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
            application.isEditEnable = false;

            Intent i2 = new Intent(SwapImageActivity.this, VideoThemeActivity.class);
            startActivityForResult(i2,0);


        }
    }


    public static final int ACTION_REQUEST_EDITIMAGE = 9;

    public void editorIntent(int position, String path) {
        Log.e("calll", "calll");
        KSUtil.imbEditorPos = position;
        KSUtil.imgEditorPath = path;

        File outputFile = EditImageUtil.genEditFile();
        EditImageActivity.start(this, KSUtil.imgEditorPath, outputFile.getAbsolutePath(), ACTION_REQUEST_EDITIMAGE);

        Uri uri = getImageContentUri(SwapImageActivity.this, new File(KSUtil.imgEditorPath));


    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_REQUEST_EDITIMAGE && resultCode == RESULT_OK) {
            handleEditorImage(data);
        }
    }

    private void handleEditorImage(Intent data) {
        String newFilePath = data.getStringExtra(EditImageActivity.EXTRA_OUTPUT);
        boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IMAGE_IS_EDIT, false);

        if (isImageEdit) {
        } else {//Not edited or used the original image
            newFilePath = data.getStringExtra(EditImageActivity.FILE_PATH);
        }

        KSUtil.videoPathList.set(KSUtil.imbEditorPos, newFilePath);
        initGrid();
        zapperAdapter.refreshlist();
    }

    @Override
    public void onBackPressed() {
        if (absDynamicGridView.isEditMode()) {
            absDynamicGridView.stopEditMode();
        } else {
            File dir = new File(FileUtils.filepath1 + "/MS_SlideShow" + "/imagesfolder");
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    new File(dir, children[i]).delete();
                }
            }


            super.onBackPressed();


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
                        SwapImageActivity.this.interstitialAd = interstitialAd;

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
                                        SwapImageActivity.this.interstitialAd = null;
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
        if (KSUtil.videoPathList.size() >= 4) {
            new Done().execute();
        } else {
            Toast.makeText(SwapImageActivity.this, "Please Select at list 4 Images...", Toast.LENGTH_SHORT).show();
            gotoMain();
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
