package infiapp.com.videomaker.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import infiapp.com.videomaker.R;
import infiapp.com.videomaker.util.Ads_Preference;
import infiapp.com.videomaker.util.KSUtil;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class VideoPlayerActivity extends AppCompatActivity implements
        SeekBar.OnSeekBarChangeListener {

    ImageView btnback;
    VideoView videoView;
    String path;
    MediaController mediaController;
    int vw;
    int vh;
    FrameLayout frame;
    SeekBar seekVideo;
    TextView tvEndVideo;
    TextView tvStartVideo;
    int duration = 0;
    Handler handler = new Handler();
    ImageView btnPlayVideo;
    boolean isPlay = false;
    ImageView btnDelete;
    ImageView btnShare;
    RelativeLayout top;
    RelativeLayout main;

    String tt = "00:00";

    LinearLayout banner_container;
    private AdView adView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_video);

        banner_container = findViewById(R.id.banner_container);
        banner_container.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
            }
        });

        main = findViewById(R.id.main);
        top = findViewById(R.id.header);
        btnback = findViewById(R.id.btn_back1);
        videoView = findViewById(R.id.video111);
        frame = findViewById(R.id.frame1);
        seekVideo = findViewById(R.id.videoSeek);
        seekVideo.setOnSeekBarChangeListener(this);
        seekVideo.setEnabled(false);
        Drawable dr = ResourcesCompat.getDrawable(getResources(), R.drawable.shipbar_round, null);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap,
                getResources().getDisplayMetrics().widthPixels * 40 / 1080,
                getResources().getDisplayMetrics().widthPixels * 40 / 1080, true));
        seekVideo.setThumb(d);
        tvStartVideo = findViewById(R.id.tvStartVideo);
        tvEndVideo = findViewById(R.id.tvEndVideo);
        btnPlayVideo = findViewById(R.id.btnPlayVideo);

        btnShare = findViewById(R.id.btnShare);
        btnShare.setOnClickListener((View v) -> {
            videoView.pause();
            share();

        });

        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener((View v) -> {
            videoView.pause();
            delete();

        });


        btnback.setOnClickListener((View arg0) ->
                //  something do
                onBackPressed()

        );


        mediaController = new MediaController(VideoPlayerActivity.this);

        path = getIntent().getExtras().getString("video_path");
        videoView.setVideoPath(path);
        videoView.seekTo(100);
        try (MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever()) {
            mediaMetadataRetriever.setDataSource(this.path);

            vw = Integer
                    .valueOf(mediaMetadataRetriever
                            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            vh = Integer
                    .valueOf(mediaMetadataRetriever
                            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));

            mediaMetadataRetriever.release();
        }

        frame.setOnClickListener((View view) -> {

            if (!isPlay) {

                videoView.seekTo(seekVideo.getProgress());
                videoView.start();
                handler.postDelayed(seekrunnable, 200);
                videoView.setVisibility(View.VISIBLE);
                btnPlayVideo.setVisibility(View.VISIBLE);
                btnPlayVideo.setImageResource(0);
                btnPlayVideo.setImageResource(R.drawable.pause2);
            } else {

                videoView.pause();
                handler.removeCallbacks(seekrunnable);
                btnPlayVideo.setVisibility(View.VISIBLE);
                btnPlayVideo.setImageResource(0);
                btnPlayVideo.setImageResource(R.drawable.play);

            }
            isPlay = !isPlay;


        });
        videoView.setOnPreparedListener((MediaPlayer mp) -> {

            duration = videoView.getDuration();
            seekVideo.setMax(duration);

            tvStartVideo.setText(tt);
            try {
                tvEndVideo.setText("" + formatTimeUnit(duration));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        });

        videoView.setOnCompletionListener((
                MediaPlayer mp) ->

        {
            btnPlayVideo.setImageResource(0);
            btnPlayVideo.setImageResource(R.drawable.play);
            btnPlayVideo.setVisibility(View.VISIBLE);
            videoView.seekTo(100);
            seekVideo.setProgress(0);
            tvStartVideo.setText(tt);
            handler.removeCallbacks(seekrunnable);
            isPlay = !isPlay;


        });
        btnPlayVideo.setOnClickListener(onclickplayvideo);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }




    @Override
    protected void onResume() {
        super.onResume();
        videoView.seekTo(100);
    }

    @Override
    public void onProgressChanged(SeekBar seekbar, int progress,
                                  boolean fromTouch) {
        if (fromTouch) {
            videoView.seekTo(progress);
            try {
                tvStartVideo.setText("" + formatTimeUnit(progress));
            } catch (ParseException e) {
                //  something do
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0) {
        //  something do

    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) {
        // something do

    }

    public static String formatTimeUnit(long millis) throws ParseException {
        @SuppressLint("DefaultLocale") String formatted = String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                        .toMinutes(millis)));
        return formatted;
    }

    OnClickListener onclickplayvideo = new OnClickListener() {

        @Override
        public void onClick(View v) {

            Log.e("", "play status " + isPlay);

            if (!isPlay) {
                videoView.seekTo(seekVideo.getProgress());
                videoView.start();
                handler.postDelayed(seekrunnable, 200);
                videoView.setVisibility(View.VISIBLE);
                btnPlayVideo.setVisibility(View.VISIBLE);
                btnPlayVideo.setImageResource(0);
                btnPlayVideo.setImageResource(R.drawable.pause2);

            } else {
                videoView.pause();
                handler.removeCallbacks(seekrunnable);
                btnPlayVideo.setVisibility(View.VISIBLE);
                btnPlayVideo.setImageResource(0);
                btnPlayVideo.setImageResource(R.drawable.play);
            }
            isPlay = !isPlay;

        }
    };

    Runnable seekrunnable = new Runnable() {
        public void run() {
            if (videoView.isPlaying()) {
                int curPos = videoView.getCurrentPosition();
                seekVideo.setProgress(curPos);
                try {
                    tvStartVideo.setText("" + formatTimeUnit(curPos));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (curPos == duration) {
                    seekVideo.setProgress(0);
                    tvStartVideo.setText(tt);
                    handler.removeCallbacks(seekrunnable);
                } else
                    handler.postDelayed(seekrunnable, 200);

            } else {
                seekVideo.setProgress(duration);
                try {
                    tvStartVideo.setText("" + formatTimeUnit(duration));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                handler.removeCallbacks(seekrunnable);
            }
        }
    };


    void share() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.setType("video/*");
        Uri photoURI = FileProvider.getUriForFile(
                getApplicationContext(),
                getApplicationContext()
                        .getPackageName() + ".provider", new File(path));
        share.putExtra(Intent.EXTRA_STREAM,
                photoURI);
        startActivity(Intent.createChooser(share, "Share via"));
    }

    PopupWindow popupWindow;
    LinearLayout alertLay;
    ImageView yes;
    ImageView no;

    public void delete() {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.delete_alert, null);

        alertLay = alertLayout.findViewById(R.id.alertLay);
        yes = alertLayout.findViewById(R.id.yes);
        no = alertLayout.findViewById(R.id.no);

        yes.setOnClickListener((View v) -> {
            popupWindow.dismiss();
            new File(path).delete();
            Toast.makeText(VideoPlayerActivity.this, "File is deleted!!!",
                    Toast.LENGTH_SHORT).show();
            onBackPressed();

        });

        no.setOnClickListener((View v) ->
                popupWindow.dismiss()

        );

        popupWindow = new PopupWindow(alertLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        //display the popup window
        popupWindow.showAtLocation(main, Gravity.CENTER, 0, 0);
        dialogParam();
    }

    void dialogParam() {
        LinearLayout.LayoutParams paramsDialog = new LinearLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 840 / 1080,
                getResources().getDisplayMetrics().heightPixels * 415 / 1920);
        alertLay.setLayoutParams(paramsDialog);

        LinearLayout.LayoutParams paramsCamera = new LinearLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 270 / 1080,
                getResources().getDisplayMetrics().heightPixels * 106 / 1920);
        yes.setLayoutParams(paramsCamera);
        no.setLayoutParams(paramsCamera);
    }

    int flagVideo = 21;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (KSUtil.fromAlbum) {
            Intent intent = new Intent();
            setResult(flagVideo, intent);
        } else {
            gotoMain();
        }
    }

    public void gotoMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
}
