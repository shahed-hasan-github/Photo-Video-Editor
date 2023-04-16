package infiapp.com.videomaker.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.view.ViewGroup.LayoutParams;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import infiapp.com.videomaker.MyApplication;
import infiapp.com.videomaker.R;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import infiapp.com.videomaker.adapter.FrameAdapter;
import infiapp.com.videomaker.adapter.ThemeAdapter;
import infiapp.com.videomaker.model.ImageData;
import infiapp.com.videomaker.model.MusicData;
import infiapp.com.videomaker.theme.mask.AllTheme;

import infiapp.com.videomaker.theme.service.ServiceAnim;

import infiapp.com.videomaker.theme.util.FileUtils;
import infiapp.com.videomaker.util.Ads_Preference;
import infiapp.com.videomaker.util.KSUtil;


public class VideoThemeActivity extends AppCompatActivity implements OnClickListener, OnSeekBarChangeListener {
    public static MyApplication application;
    ArrayList<ImageData> arrayList;
    private static final String PREFS_NAME = "preferenceName";
    LinearLayout cvframeview;
    LinearLayout cvthemview;
    Float[] duration = new Float[]{1.0f, 1.5f,
            2.0f, 2.5f, 3.0f, 3.5f,
            4.0f, 4.5f, 5.0f};
    View flLoader;

    int frame;
    FrameAdapter frameAdapter;
    RequestManager glide;
    public static Handler handler = new Handler();
    int seekProgress = 0;
    ImageView idanimation;
    ImageView ibAddMusic;
    ImageView ibAddDuration;
    ImageView idviewFrame;

    LayoutInflater inflater;
    boolean isFromTouch = false;
    ImageView ivFrame;
    View ivPlayPause;
    ImageView ivPlayPause1;
    ImageView ivPreview;
    ImageView backimgPreview;
//    ImageView donePreview;
    CardView donePreview;
    LinearLayout llEdit;
    LockRunnable lockRunnable = new LockRunnable();
    MediaPlayer mPlayer;
    RecyclerView rvFrame;
    RecyclerView rvThemes;
    public static float seconds = 3.0f;
    SeekBar seekBar;
    ThemeAdapter themeAdapter;
    LinearLayout toolbar;
    TextView tvEndTime;
    TextView tvTime;
    public static String outputPath = "";
    public static String folderPath = null;
    public static int mDuration;
    public static File tempFile;
    public static Float duration1;
    public static int total;
    FrameLayout scaleCard;
    public static File logFile = new File(FileUtils.TEMP_DIRECTORY, "video.txt");
    ImageView imgBtnYes;
    ImageView imgBtnNo;
    LinearLayout maindailog;
    LinearLayout laySeconds;
    TextView txtsec1;
    TextView txtsec15;
    TextView txtsec2;
    TextView txtsec25;
    TextView txtsec3;
    TextView txtsec35;
    TextView txtsec4;
    TextView txtsec45;
    TextView txtsec5;
    private InterstitialAd interstitialAd;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_creater);

        loadAd();

        application = MyApplication.getInstance();
        application.videoImages.clear();
        bindView();
        folderPath = FileUtils.filepath1 + "/"
                + getResources().getString(R.string.app_name);

        Intent intent = new Intent(getApplicationContext(), ServiceAnim.class);
        intent.putExtra(ServiceAnim.EXTRA_SELECTED_THEME, application.getCurrentTheme());
        startService(intent);


        init();
        addListner();
        MyApplication.isBreak = false;

        LinearLayout.LayoutParams paramsbtn = new LinearLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 120 / 1080,
                getResources().getDisplayMetrics().heightPixels * 120 / 1920);
        idanimation.setLayoutParams(paramsbtn);
        idviewFrame.setLayoutParams(paramsbtn);
        ibAddMusic.setLayoutParams(paramsbtn);
        ibAddDuration.setLayoutParams(paramsbtn);




    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }



    void bindView() {
        flLoader = findViewById(R.id.flLoader);


        flLoader.setOnClickListener(v -> {

        });
        ivPreview = findViewById(R.id.previewImageView1);
        ivFrame = findViewById(R.id.ivFrame);
        seekBar = findViewById(R.id.sbPlayTime);
        seekBar.setEnabled(false);
        Drawable dr = ResourcesCompat.getDrawable(getResources(), R.drawable.shipbar_round, null);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap,
                getResources().getDisplayMetrics().widthPixels * 40 / 1080,
                getResources().getDisplayMetrics().widthPixels * 40 / 1080, true));
        seekBar.setThumb(d);
        tvEndTime = findViewById(R.id.tvEndTime);
        tvTime = findViewById(R.id.tvTime);
        llEdit = findViewById(R.id.llEdit);
        scaleCard = findViewById(R.id.scaleCard);
        ivPlayPause = findViewById(R.id.ivPlayPause);

        LinearLayout.LayoutParams paramsd = new LinearLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 130 / 1080,
                getResources().getDisplayMetrics().heightPixels * 130 / 1920);
        ivPlayPause.setLayoutParams(paramsd);


        ivPlayPause1 = findViewById(R.id.ivPlayPause1);
        LinearLayout.LayoutParams paramsp = new LinearLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 60 / 1080,
                getResources().getDisplayMetrics().heightPixels * 60 / 1920);
        ivPlayPause1.setLayoutParams(paramsp);

        toolbar = findViewById(R.id.toolbar_preview);
        rvThemes = findViewById(R.id.rvThemes);
        laySeconds = findViewById(R.id.laySeconds);

        rvFrame = findViewById(R.id.rvFrame);

        ibAddDuration = findViewById(R.id.ibAddDuration);
        ibAddMusic = findViewById(R.id.ibAddMusic);

        backimgPreview = findViewById(R.id.backimgPreview);
        donePreview = findViewById(R.id.cardHome);

        LinearLayout.LayoutParams ln = new
                LinearLayout.LayoutParams(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_WIDTH);
        scaleCard.setLayoutParams(ln);

        FrameLayout.LayoutParams fr = new
                FrameLayout.LayoutParams(MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_WIDTH);
        ivPreview.setLayoutParams(fr);
        ivFrame.setLayoutParams(fr);

        backimgPreview.setOnClickListener(v -> onBackPressed());

        donePreview.setOnClickListener(v -> {

            showInterstitial();


        });


        txtsec1 = findViewById(R.id.txtsec1);
        txtsec15 = findViewById(R.id.txtsec15);
        txtsec2 = findViewById(R.id.txtsec2);
        txtsec25 = findViewById(R.id.txtsec25);
        txtsec3 = findViewById(R.id.txtsec3);
        txtsec35 = findViewById(R.id.txtsec35);
        txtsec4 = findViewById(R.id.txtsec4);
        txtsec45 = findViewById(R.id.txtsec45);
        txtsec5 = findViewById(R.id.txtsec5);

        txtsec1.setOnClickListener(v -> sec1());
        txtsec15.setOnClickListener(v -> sec15());
        txtsec2.setOnClickListener(v -> sec2());
        txtsec25.setOnClickListener(v -> sec25());
        txtsec3.setOnClickListener(v -> sec3());
        txtsec35.setOnClickListener(v -> sec35());
        txtsec4.setOnClickListener(v -> sec4());
        txtsec45.setOnClickListener(v -> sec45());
        txtsec5.setOnClickListener(v -> sec5());


        LinearLayout.LayoutParams paramstxt = new LinearLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 204 / 1080,
                getResources().getDisplayMetrics().widthPixels * 204 / 1080);
        paramstxt.setMargins(10, 10, 10, 10);
        txtsec1.setLayoutParams(paramstxt);
        txtsec15.setLayoutParams(paramstxt);
        txtsec2.setLayoutParams(paramstxt);
        txtsec25.setLayoutParams(paramstxt);
        txtsec3.setLayoutParams(paramstxt);
        txtsec35.setLayoutParams(paramstxt);
        txtsec4.setLayoutParams(paramstxt);
        txtsec45.setLayoutParams(paramstxt);
        txtsec5.setLayoutParams(paramstxt);
    }

    void init() {
        seconds = application.getSecond();
        inflater = LayoutInflater.from(this);
        glide = Glide.with(this);
        application = MyApplication.getInstance();
        arrayList = application.getSelectedImages();


        total = (int) (((float) (arrayList.size() - 1)) * seconds);
        seekBar.setMax((arrayList.size() - 1) * 30);


        tvEndTime.setText(String.format("%02d:%02d", total / 60, total % 60));
        setUpThemeAdapter();
//        glide.load(((ImageData) application.getSelectedImages().get(0)).imagePath).into(ivPreview);


        setTheme();


    }


    void addListner() {
        findViewById(R.id.video_clicker).setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        findViewById(R.id.ibAddMusic).setOnClickListener(this);
        findViewById(R.id.ibAddDuration).setOnClickListener(this);
    }


    void reinitMusic() {

        try {
            MusicData pvmwsMusicData = application.getMusicData();

            if (pvmwsMusicData != null) {
                mPlayer = MediaPlayer.create(this, Uri.parse(pvmwsMusicData.trackdata));
                Log.e("musicpath", pvmwsMusicData.trackdata);
                mPlayer.setLooping(true);

                mPlayer.prepare();

            } else {
                return;
            }

        } catch (Exception e1) {
//dosomething
        }

    }

    void playMusic() {
        if (flLoader.getVisibility() != View.VISIBLE && mPlayer != null && !mPlayer.isPlaying()) {
            mPlayer.start();
        }
    }

    void pauseMusic() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
    }


    public static void appendVideoLog(String text) {
        if (!FileUtils.TEMP_DIRECTORY.exists()) {
            FileUtils.TEMP_DIRECTORY.mkdirs();
        }
        try {
            try (BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true))) {
                buf.append(text);
                buf.newLine();
                buf.close();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    void removeFrameImage(String path) {
        File appimages = new File(path);
        if (appimages.exists()) {
            File[] files = appimages.listFiles();
            if (files != null) {
                for (File f : files) {
                    if ((f.getName().endsWith(".jpg") || f.getName().endsWith(
                            ".png"))) {
                        f.delete();
                    }
                }
            }
        }
    }

    void removemusic(String path) {
        File appimages = new File(path);
        if (appimages.exists()) {
            File[] files = appimages.listFiles();
            if (files != null) {
                for (File f : files) {
                    if ((f.getName().endsWith(".mp3"))) {

                        f.delete();
                        //dosomething


                    }
                }
            }
        }
    }

    void setUpThemeAdapter() {
        themeAdapter = new ThemeAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false);
        GridLayoutManager gridLayoutManagerFrame = new GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false);
        rvThemes.setLayoutManager(gridLayoutManager);
        rvThemes.setItemAnimator(new DefaultItemAnimator());
        rvThemes.setAdapter(themeAdapter);
        frameAdapter = new FrameAdapter(this);
        rvFrame.setLayoutManager(gridLayoutManagerFrame);
        rvFrame.setItemAnimator(new DefaultItemAnimator());
        rvFrame.setAdapter(frameAdapter);

        cvthemview = findViewById(R.id.cvthemview);
        cvframeview = findViewById(R.id.cvframeview);
        idanimation = findViewById(R.id.idanimation);
        idviewFrame = findViewById(R.id.idviewFrame);


        idanimation.setOnClickListener(v -> {
            unpress();
            idanimation.setImageResource(R.drawable.theme_unpresed);
            cvthemview.setVisibility(View.VISIBLE);
            cvframeview.setVisibility(View.GONE);
            laySeconds.setVisibility(View.GONE);



        });

        idviewFrame.setOnClickListener(v -> {
            unpress();
            idviewFrame.setImageResource(R.drawable.frame_unpresed);
            cvframeview.setVisibility(View.VISIBLE);
            cvthemview.setVisibility(View.GONE);
            laySeconds.setVisibility(View.GONE);


        });
    }


    @SuppressLint("DefaultLocale")
    synchronized void displayImage() {
        try {
            if (seekProgress >= seekBar.getMax()) {
                seekProgress = 0;
                lockRunnable.stop();
            } else {
                if (seekProgress > 0 && flLoader.getVisibility() == View.VISIBLE) {
                    flLoader.setVisibility(View.GONE);
                    if (!(mPlayer == null || mPlayer.isPlaying())) {
                        mPlayer.start();
                    }
                }


                seekBar.setSecondaryProgress(application.videoImages.size());


                if (seekBar.getProgress() < seekBar.getSecondaryProgress()) {


                    seekProgress %= application.videoImages.size();


                    glide.load(application.videoImages.get(seekProgress))
                            .asBitmap()
                            .signature(new MediaStoreSignature("image/*", System.currentTimeMillis(), 0))
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            ivPreview.setImageBitmap(resource);
                        }
                    });
                    seekProgress++;
                    if (!isFromTouch) {
                        seekBar.setProgress(seekProgress);
                    }
                    int j = (int) ((((float) seekProgress) / 30.0) * seconds);
                    int mm = j / 60;
                    int ss = j % 60;
                    tvTime.setText(String.format("%02d:%02d", mm, ss));


                    total = (int) (((float) (arrayList.size() - 1)) * seconds);


                    tvEndTime.setText(String.format("%02d:%02d", total / 60, total % 60));
                }
            }
        } catch (Exception e) {
            glide = Glide.with(this);
        }
    }

    @SuppressLint({"WrongConstant"})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibAddDuration:
                unpress();
                ibAddDuration.setImageResource(R.drawable.timer_unpresed);
                cvframeview.setVisibility(View.GONE);
                cvthemview.setVisibility(View.GONE);
                if (laySeconds.getVisibility() == View.GONE) {
                    laySeconds.setVisibility(View.VISIBLE);

                } else {
                    laySeconds.setVisibility(View.GONE);
                }

                return;

            case R.id.ibAddMusic:

                flLoader.setVisibility(8);
                loadSongSelection();
                return;

            case R.id.video_clicker:
                if (lockRunnable.isPause()) {
                    Log.e("111", "onClick: "+"1111" );
                    lockRunnable.play();
                    return;
                } else {


                    lockRunnable.pause();
                    return;
                }
            default:
                return;
        }
    }

    void unpress() {
        ibAddDuration.setImageResource(R.drawable.timer_presed);
        idviewFrame.setImageResource(R.drawable.frame_presed);
        idanimation.setImageResource(R.drawable.theme_presed);
    }

    protected void onPause() {
        super.onPause();
        lockRunnable.pause();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        application.isEditEnable = false;
        Log.e("xxxxxxxx", "onActivityResult: "+"xxxxxxxx" );
        reinitMusic();
        if (resultCode == -1) {

            if (requestCode == 101) {
                application.isFromSdCardAudio = true;
                seekProgress = 0;
                reinitMusic();

            } else {
                //do something
            }
        }
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekProgress = progress;

        if (isFromTouch) {
            seekBar.setProgress(Math.min(progress, seekBar.getSecondaryProgress()));
            displayImage();
            seekMediaPlayer();
        }
    }

    void seekMediaPlayer() {
        if (mPlayer != null) {
            try {
                mPlayer.seekTo(((int) (((((float) seekProgress) / 30.0) * seconds) * 1000.0f)) % mPlayer.getDuration());
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        isFromTouch = true;
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        isFromTouch = false;
    }

    public void reset() {
        MyApplication.isBreak = false;
        application.videoImages.clear();
        handler.removeCallbacks(lockRunnable);
        lockRunnable.stop();
        Glide.get(this).clearMemory();
        new ThreadClass().start();
        FileUtils.deleteTempDir();
        glide = Glide.with(this);
        flLoader.setVisibility(View.VISIBLE);
        setTheme();
    }

    public void onBackPressed() {
        if (llEdit.getVisibility() != View.VISIBLE) {
            llEdit.setVisibility(View.VISIBLE);
            application.isEditEnable = false;
        } else {

            open();
        }
    }

    public void open() {

        final Dialog dDelete = new Dialog(VideoThemeActivity.this);
        dDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dDelete.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);

        dDelete.setContentView(R.layout.dialog_delete);

        TextView maintext = dDelete.findViewById(R.id.maintext);
        imgBtnYes = dDelete.findViewById(R.id.imgBtnYes);
        imgBtnNo = dDelete.findViewById(R.id.imgBtnNo);
        maindailog = dDelete.findViewById(R.id.maindailog);


        popuplayoutDialog();

        maintext.setText(getResources().getString(R.string.back_m));
        imgBtnYes.setOnClickListener(arg0 -> {

            application.videoImages.clear();
            application.getSelectedImages().clear();
            removeFrameImage(folderPath);
            removeFrameImage(folderPath + "/temp");
            removeFrameImage(folderPath + "/edittmpzoom");
            FileUtils.deleteFile(tempFile);
            removemusic(folderPath + "/music/");


            File dir = new File(FileUtils.filepath1 + "/MS_SlideShow" + "/imagesfolder");
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    new File(dir, children[i]).delete();
                }
            }


            dDelete.dismiss();
            Intent iBack = new Intent(VideoThemeActivity.this, MainActivity.class);
            iBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(iBack);

        });
        imgBtnNo.setOnClickListener(arg0 -> dDelete.dismiss());
        dDelete.show();

    }


    public void setTheme() {
        if (application.isFromSdCardAudio) {
            new ThreadC1().start();
        } else {
            new ThreadC().start();
        }
    }


    public void setFrame(int data) {
        frame = data;
        if (data == -1) {
            ivFrame.setImageDrawable(null);
        } else {
            ivFrame.setImageResource(data);
        }
        application.setFrame(data);
    }

    public int getFrame() {
        return application.getFrame();
    }

    void loadSongSelection() {

        Intent i2 = new Intent(this, SongGalleryActivity.class);
        startActivityForResult(i2,101);


    }


    class ThreadClass extends Thread {
        ThreadClass() {
        }

        public void run() {
            Glide.get(VideoThemeActivity.this).clearDiskCache();
        }
    }


    class ThreadC extends Thread {

        ThreadC() {
        }

        public void run() {
            AllTheme pvswsThemes = application.selectedTheme;
            try {
                FileUtils.TEMP_DIRECTORY_AUDIO.mkdirs();
                tempFile = new File(FileUtils.TEMP_DIRECTORY_AUDIO, "temp.mp3");
                if (tempFile.exists()) {
                    FileUtils.deleteFile(tempFile);
                }
                try (InputStream in = getResources().openRawResource(pvswsThemes.getThemeMusic())) {
                    FileOutputStream out = new FileOutputStream(tempFile);
                    byte[] buff = new byte[1024];
                    while (true) {
                        int read = in.read(buff);
                        if (read <= 0) {
                            break;
                        }
                        out.write(buff, 0, read);
                    }
                }
                MediaPlayer player = new MediaPlayer();
                player.setDataSource(tempFile.getAbsolutePath());

                player.prepare();
                final MusicData pvmwsMusicData = new MusicData();
                pvmwsMusicData.trackdata = tempFile.getAbsolutePath();
                player.setOnPreparedListener(mp -> {
                    pvmwsMusicData.trackDuration = (long) mp.getDuration();
                    mp.stop();
                });
                pvmwsMusicData.trackTitle = "temp";
                application.setMusicData(pvmwsMusicData);
            } catch (Exception e) {

                //do something
            }
            runOnUiThread(() -> {
                reinitMusic();
                lockRunnable.play();

                lockRunnable.pause();
                new Handler().postDelayed(() -> lockRunnable.play(), 4800);
            });
        }
    }

    class ThreadC1 extends Thread {

        ThreadC1() {
        }

        public void run() {
            AllTheme pvswsThemes = application.selectedTheme;
            try {
                FileUtils.TEMP_DIRECTORY_AUDIO.mkdirs();
                tempFile = new File(FileUtils.TEMP_DIRECTORY_AUDIO, "temp.mp3");
                if (tempFile.exists()) {
                    FileUtils.deleteFile(tempFile);
                }
                InputStream in;
                in = getResources().openRawResource(pvswsThemes.getThemeMusic());
                FileOutputStream out;
                out = new FileOutputStream(tempFile);
                byte[] buff = new byte[1024];
                while (true) {
                    int read = in.read(buff);
                    if (read <= 0) {
                        break;
                    }
                    out.write(buff, 0, read);
                }
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String custom = settings.getString("musiccustom", "abc");
                MediaPlayer player = new MediaPlayer();
                player.setDataSource(tempFile.getAbsolutePath());

                player.prepare();

                final MusicData pvmwsMusicData = new MusicData();
                pvmwsMusicData.trackdata = custom;
                player.setOnPreparedListener(mp -> {
                    pvmwsMusicData.trackDuration = (long) mp.getDuration();
                    mp.stop();
                });
                application.setMusicData(pvmwsMusicData);
            } catch (Exception e) {
                //dosomething
            }
            runOnUiThread(() -> {
                reinitMusic();
                lockRunnable.play();

                lockRunnable.pause();
                new Handler().postDelayed(() -> lockRunnable.play(), 4800);
            });
        }
    }


    protected void onResume() {
        super.onResume();
        loadAd();
    }


    public void sec1() {
        seconds = duration[0].floatValue();
        setDuration();
    }

    public void sec15() {
        seconds = duration[1].floatValue();
        setDuration();
    }

    public void sec2() {
        seconds = duration[2].floatValue();
        setDuration();
    }

    public void sec25() {
        seconds = duration[3].floatValue();
        setDuration();
    }

    public void sec3() {
        seconds = duration[4].floatValue();
        setDuration();
    }

    public void sec35() {
        seconds = duration[5].floatValue();
        setDuration();
    }

    public void sec4() {
        seconds = duration[6].floatValue();
        setDuration();
    }

    public void sec45() {
        seconds = duration[7].floatValue();
        setDuration();
    }

    public void sec5() {
        seconds = duration[8].floatValue();
        setDuration();
    }

    void setDuration() {

        application.setSecond(seconds);

        total = (int) (((float) (arrayList.size() - 1)) * seconds);

        lockRunnable.stop();

    }


    class LockRunnable implements Runnable {
        boolean isPause = false;

        class StartPlayClass implements AnimationListener {
            StartPlayClass() {
            }

            public void onAnimationStart(Animation animation) {
                ivPlayPause.setVisibility(View.VISIBLE);
                ivPlayPause1.setImageResource(R.drawable.pause2);

            }

            public void onAnimationRepeat(Animation animation) {
                //dosomethind
            }

            public void onAnimationEnd(Animation animation) {
                ivPlayPause.setVisibility(View.GONE);
                ivPlayPause1.setImageResource(R.drawable.pause2);
            }
        }

        class PauseClass implements AnimationListener {
            PauseClass() {
            }

            public void onAnimationStart(Animation animation) {
                ivPlayPause.setVisibility(View.VISIBLE);
                ivPlayPause1.setImageResource(R.drawable.small_play);
            }

            public void onAnimationRepeat(Animation animation) {
                //dosomethind
            }

            public void onAnimationEnd(Animation animation) {
                //dosomethind
            }
        }

        LockRunnable() {
        }


        public void run() {
            displayImage();
            if (!isPause) {
                /*ms*/
                handler.postDelayed(lockRunnable, (long) Math.round(27.0f * seconds));
            }
        }

        public boolean isPause() {
            return isPause;
        }

        public void play() {
            isPause = false;
            playMusic();

            handler.postDelayed(lockRunnable, (long) Math.round(27.0f * seconds));
            Animation animation = new AlphaAnimation(1.0f, 0.0f);
            animation.setDuration(500);
            animation.setFillAfter(true);
            animation.setAnimationListener(new StartPlayClass());
            ivPlayPause.startAnimation(animation);
            if (llEdit.getVisibility() != View.VISIBLE) {
                llEdit.setVisibility(View.VISIBLE);
                application.isEditEnable = false;
                if (ServiceAnim.isImageComplate) {
                    Intent intent = new Intent(getApplicationContext(), ServiceAnim.class);
                    intent.putExtra(ServiceAnim.EXTRA_SELECTED_THEME, application.getCurrentTheme());
                    startService(intent);
                }
            }

        }

        public void pause() {
            isPause = true;
            pauseMusic();
            Animation animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setDuration(500);
            animation.setFillAfter(true);
            ivPlayPause.startAnimation(animation);
            animation.setAnimationListener(new PauseClass());
        }

        public void stop() {
            pause();
            seekProgress = 0;
            try {


                if (mPlayer != null) {
                    mPlayer.stop();
                }
                reinitMusic();
            } catch (Exception e) {
//do something
            }
            seekBar.setProgress(seekProgress);

        }
    }

    void popuplayoutDialog() {

        RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 270 / 1080,
                getResources().getDisplayMetrics().heightPixels * 106 / 1920);
        params4.addRule(RelativeLayout.CENTER_IN_PARENT);
        imgBtnYes.setLayoutParams(params4);
        imgBtnNo.setLayoutParams(params4);

        RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 840 / 1080,
                getResources().getDisplayMetrics().heightPixels * 415 / 1920);
        params5.addRule(RelativeLayout.CENTER_IN_PARENT);
        maindailog.setLayoutParams(params5);
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
                        VideoThemeActivity.this.interstitialAd = interstitialAd;

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
                                        VideoThemeActivity.this.interstitialAd = null;
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
        if (mPlayer != null) {
            mDuration = mPlayer.getDuration();

            if (mPlayer.isPlaying())
                mPlayer.pause();

        }

        lockRunnable.stop();

        Intent i2 = new Intent(VideoThemeActivity.this, VideoMakerActivity.class);
        startActivityForResult(i2,0);
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
