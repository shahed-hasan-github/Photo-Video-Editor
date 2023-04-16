package infiapp.com.videomaker.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import ch.halcyon.squareprogressbar.SquareProgressBar;
import infiapp.com.videomaker.util.Utils;
import infiapp.com.videomaker.R;
import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;

import static android.os.Environment.getExternalStorageDirectory;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;


public class VideoTrimActivity extends AppCompatActivity implements OnTrimVideoListener {
    RelativeLayout exportVideo;
    ApplyFilter asyntask;
    String duration;
    String videoResolution;
    String path;
    Handler handler;
    Runnable runnable = new MyRunnable();
    long j = 0;
    ProgressDialog mProgressDialog;
    K4LVideoTrimmer mVideoTrimmer;
    String saveVideoUrl;
    Animation shakeAnimation;
    SquareProgressBar squareProgressBar;
    private Activity context;


    @Override
    public void onCreate(Bundle bundle) {
        String stringExtra;
        super.onCreate(bundle);
        setContentView(R.layout.activity_trim_video);

        context = this;
        initView();


        handler = new Handler(Looper.getMainLooper());

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            stringExtra = intent.getStringExtra("EXTRA_VIDEO_PATH");
            duration = intent.getStringExtra("duration");
            videoResolution = intent.getStringExtra("video_resolution");
        } else {
            stringExtra = "";
        }


        shakeAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

        exportVideo.setOnClickListener(view -> exportVideo.startAnimation(shakeAnimation));


        try {
            if (!(Utils.staticVideoModelData == null || Utils.staticVideoModelData.getVideoThumb() == null || isFinishing())) {
                Picasso.get().load(Utils.staticVideoModelData.getVideoThumb()).into(squareProgressBar.getImageView());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        setUpProgressBar(squareProgressBar);

        if (mVideoTrimmer != null && stringExtra != null && !stringExtra.trim().isEmpty() && new File(stringExtra).exists()) {
            if (duration.matches("[0-9]+")) {
                mVideoTrimmer.setMaxDuration(Integer.parseInt(duration));
            }
            mVideoTrimmer.setOnTrimVideoListener(this);
            if (new File(stringExtra).exists()) {
                mVideoTrimmer.setVideoURI(Uri.fromFile(new File(stringExtra)));
            } else {
                Toast.makeText(this, "File is Corrupt", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initView() {


        exportVideo = findViewById(R.id.export_video);
        squareProgressBar = findViewById(R.id.squareProgressBar);
        mVideoTrimmer = findViewById(R.id.timeLine);

    }

    private void setUpProgressBar(SquareProgressBar squareProgressBar) {

        squareProgressBar.setColor("#ff5722");
        squareProgressBar.setProgress(0);
        squareProgressBar.setRoundedCorners(true);
        squareProgressBar.setOpacity(true);
        squareProgressBar.showProgress(true);
        squareProgressBar.setWidth(6);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.trimming_progress));
    }


    public void callBroadCast() {

        try {
            MediaScannerConnection.scanFile(this, new String[]{getExternalStorageDirectory().toString()}, null, (str, uri) -> Log.i("Scan", "Done"));

        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    public void cancelAction() {
        mProgressDialog.cancel();
        mVideoTrimmer.destroy();
        finish();
    }

    public void executeCommand(String[] strArr) {
        MediaPlayer create = MediaPlayer.create(context, Uri.parse(path));
        int duration1 = create.getDuration();
        try {
            j = TimeUnit.MILLISECONDS.toSeconds((long) create.getDuration());
        } catch (Exception e) {
            e.printStackTrace();
        }
        create.release();
        FFmpeg.executeAsync(strArr, (executionId, returnCode) -> {

            com.arthenica.mobileffmpeg.Config.resetStatistics();

            com.arthenica.mobileffmpeg.Config.enableStatisticsCallback(statistics -> Log.i("stats", "enabled"));

            if (returnCode == RETURN_CODE_SUCCESS) {

                new Handler(Looper.getMainLooper()).post(() -> {
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{new File(saveVideoUrl).getAbsolutePath()}, new String[]{"mp4"}, null);
                    handler.postDelayed(runnable, 500);
                });
            } else {

                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(this, "Failed..", Toast.LENGTH_SHORT).show());

            }

        });

        com.arthenica.mobileffmpeg.Config.resetStatistics();

        com.arthenica.mobileffmpeg.Config.enableStatisticsCallback(statistics -> squareProgressBar.setProgress(getPercentages(statistics.getTime(), duration1)));


    }

    private int getPercentages(int curntTime, int duration) {

        float progressF;
        int progress;

        int videoDuration = duration;


        progressF = ((float) curntTime / (float) videoDuration) * 100;

        progress = (int) progressF;


        if (progress >= 100) {
            return 100;
        } else {
            return progress;
        }


    }

    public void getResult(Uri uri) {
        mProgressDialog.cancel();
        runOnUiThread(() -> exportVideo.setVisibility(View.VISIBLE));
        path = uri.getPath();
        asyntask = new ApplyFilter();
        asyntask.execute();
    }


    public class ApplyFilter extends AsyncTask<Void, Integer, Void> {
        public Void doInBackground(Void... voidArr) {
            return b(voidArr);
        }

        @SuppressLint({"SdCardPath", "WrongConstant"})
        public Void b(Void... voidArr) {
            @SuppressLint("SimpleDateFormat") String format = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(Calendar.getInstance().getTime());
            StringBuilder sb = new StringBuilder();
            sb.append(getExternalStorageDirectory().getAbsolutePath());
            sb.append("/");
            StringBuilder sb2 = new StringBuilder();
            sb2.append(getResources().getString(R.string.app_name));
            sb2.append("/.temp_trimmer");
            sb.append(sb2.toString());
            if (!new File(sb.toString()).exists()) {
                new File(sb.toString()).mkdir();
            }
            sb.append("/video_trimer_");
            sb.append(format);
            sb.append(".mp4");
            StringBuilder sb3 = new StringBuilder();
            sb3.append("-i&");
            sb3.append(path);
            sb2 = new StringBuilder();
            sb2.append("&-filter:v&scale=");
            sb2.append(videoResolution);
            sb2.append("&-c:a&copy&");
            sb3.append(sb2.toString());
            sb3.append(sb);
            String[] split = sb3.toString().split("&");
            saveVideoUrl = sb.toString();
            if (split.length != 0) {
                executeCommand(split);
            } else {
                Toast.makeText(getApplicationContext(), "Command Empty", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        public void onPreExecute() {

            //something do
        }
    }

    public class MyRunnable implements Runnable {
        @SuppressLint({"WrongConstant"})
        public void run() {
            handler.removeCallbacks(runnable);
            VideoEditorActivity.resizedVideoPathFinal = saveVideoUrl;
            exportVideo.setVisibility(View.GONE);
            File file = new File(path);
            if (file.exists() && file.delete()) {
                callBroadCast();
            }
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (exportVideo.getVisibility() == View.VISIBLE) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_confirm_back);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            ((ImageView) dialog.findViewById(R.id.cancel)).setOnClickListener(view -> dialog.dismiss());
            ((Button) dialog.findViewById(R.id.yes)).setOnClickListener(view -> {
                dialog.dismiss();
                try {
                    File file = new File(path);
                    if (file.exists() && file.delete()) {
                        callBroadCast();
                    }
                    asyntask.cancel(true);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            });
            ((Button) dialog.findViewById(R.id.no)).setOnClickListener(view -> dialog.dismiss());
            dialog.show();
            return;
        }
        try {
            File file = new File(path);
            if (file.exists() && file.delete()) {
                callBroadCast();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }
}
