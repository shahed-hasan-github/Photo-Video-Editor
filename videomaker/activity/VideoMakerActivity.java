package infiapp.com.videomaker.activity;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arthenica.mobileffmpeg.Config;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import infiapp.com.videomaker.MyApplication;
import infiapp.com.videomaker.R;
import infiapp.com.videomaker.util.Ads_Preference;
import infiapp.com.videomaker.util.KSUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import infiapp.com.videomaker.theme.util.FileUtils;
import infiapp.com.videomaker.theme.util.Utils;

public class VideoMakerActivity extends AppCompatActivity {
    TextView perTV;
    LinearLayout banner_container;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_maker);

        banner_container = findViewById(R.id.banner_container);
        banner_container.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
            }
        });


        perTV = findViewById(R.id.perTV);
        new ProcessVideo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public class ProcessVideo extends AsyncTask<Integer, Integer, String> {
        File imgDir;
        String cmd;

        @Override
        protected void onPreExecute() {
//dosomething
        }


        protected String doInBackground(Integer... params) {

            new File(FileUtils.TEMP_DIRECTORY, "video.txt").delete();
            if (!VideoThemeActivity.logFile.exists()) {
                try {
                    VideoThemeActivity.logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            imgDir = FileUtils.getImageDirectory(VideoThemeActivity.application.selectedTheme
                    .toString());
            Calendar c = Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
            String formattedDate = df.format(c.getTime());

            VideoThemeActivity.outputPath = VideoThemeActivity.folderPath;
            File file = new File(VideoThemeActivity.outputPath);
            if (!file.exists())
                file.mkdirs();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Log.e("111", "doInBackground: " + "q1");
                VideoThemeActivity.outputPath = FileUtils.filepath1
                        + "/" + getResources().getString(R.string.app_name)
                        + "/" + "video_" + formattedDate + ".mp4";

            } else {
                Log.e("111", "doInBackground: " + "q2");
                VideoThemeActivity.outputPath = Environment.getExternalStorageDirectory().getPath()
                        + "/" + getResources().getString(R.string.app_name)
                        + "/" + "video_" + formattedDate + ".mp4";
            }

            int finalwidth = MyApplication.VIDEO_WIDTH;
            VideoThemeActivity.duration1 = Float.parseFloat(""
                    + String.valueOf(VideoThemeActivity.total)
                    .replace(" Seconds", "")) * 1000;

            //without frame
            if (Utils.framePostion > -1) {
                File file11 = new File(FileUtils.frameFile.getAbsolutePath());
                if (file11.exists()) {

                    cmd = "-y&-r&22.0/" + VideoThemeActivity.application.getSecond() + "&-i&" + imgDir.getAbsolutePath() + "/img%5d.jpg" + "&-i&"
                            + FileUtils.frameFile.getAbsolutePath() + "&-ss&" + 0
                            + "&-i&" + VideoThemeActivity.application.getMusicData().trackdata + "&-filter_complex&[1]scale="
                            + finalwidth +
                            ":-1[b];[0:v][b]overlay&-vcodec&libx264&-acodec&aac&-r&30&-t&" +
                            VideoThemeActivity.total + "&-strict&experimental&-preset&ultrafast&" +
                            VideoThemeActivity.outputPath + "";

                } else {
                    cmd = "-y&-r&" + 22.0 / VideoThemeActivity.application.getSecond() + "&-i&" + imgDir.getAbsolutePath()
                            + "/img%5d.jpg&-ss&" +
                            0 + "&-i&" + VideoThemeActivity.application.getMusicData().trackdata +
                            "&-map&0:0&-map&1:0&-vcodec&libx264&-acodec&aac&-r&30&-t&" + VideoThemeActivity.total +
                            "&-strict&experimental&-preset&ultrafast&" + VideoThemeActivity.outputPath + "";
                }
                Log.e("withframe", String.valueOf(Utils.framePostion));


            } else {
                //without frame
                Log.v("withoutframe", "withoutframe");
                cmd = "-y&-r&" + 22.0 / VideoThemeActivity.application.getSecond() + "&-i&" + imgDir.getAbsolutePath()
                        + "/img%5d.jpg&-ss&" +

                        0 + "&-i&" + VideoThemeActivity.application.getMusicData().trackdata +
                        "&-map&0:0&-map&1:0&-vcodec&libx264&-acodec&aac&-r&30&-t&" + VideoThemeActivity.total +
                        "&-strict&experimental&-preset&ultrafast&" + VideoThemeActivity.outputPath + "";
            }
            String[] command = cmd.split("&");
            if (command.length != 0) {
                execFFmpegBinary(command);
            } else {
                Toast.makeText(getApplicationContext(), "Command Empty", Toast.LENGTH_LONG).show();
            }
            return VideoThemeActivity.outputPath;

        }


        @Override
        protected void onPostExecute(String result) {
//dosomething


        }

        @Override
        protected void onProgressUpdate(final Integer... values) {
//dosomething
        }

    }

    private void fileCopy() throws IOException {

        Log.e("video_output", "onPostExecute: " + VideoThemeActivity.outputPath);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        String formattedDate = df.format(c.getTime());
        String videoFormate = getResources().getString(R.string.app_name) + "_" + formattedDate + ".mp4";

        File copyVideo = new File(VideoThemeActivity.outputPath);

        String movePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/" + getResources().getString(R.string.app_name) + "/" + videoFormate;

        Log.e("vvvv", "movePath: " + movePath);


        File destination = new File(movePath);
        Log.e("vvvv", "11");

        if (!destination.getParentFile().exists()) {
            destination.getParentFile().mkdirs();
            Log.e("vvvv", "22 " + destination.getAbsolutePath());
        }
        if (!destination.exists()) {
            destination.createNewFile();
            Log.e("vvvv", "23");

        }

        Log.e("vvvv", "33");
        try {
            FileUtils.copyFile(copyVideo, destination);
            Log.e("vvvv", "44 ");

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("vvvv", "55 " + e.getMessage());

        }

    }

    void execFFmpegBinary(final String[] command) {
        Config.enableLogCallback(message -> Log.e(Config.TAG, message.getText()));
        Config.enableStatisticsCallback(newStatistics -> {
            float progress = Float.parseFloat(String.valueOf(newStatistics.getTime())) * 100 / (int) (((float) (KSUtil.videoPathList.size() - 1)) * VideoThemeActivity.seconds);
            perTV.setText("" + (int) progress / 1000 + " %");
        });
        Log.d("TAG", "Started command : ffmpeg " + Arrays.toString(command));


        long executionId = com.arthenica.mobileffmpeg.FFmpeg.executeAsync(
                command, ((executionId1, returnCode) -> {
                    if (returnCode == Config.RETURN_CODE_SUCCESS) {
                        removeFrameImage(VideoThemeActivity.folderPath);
                        removeFrameImage(VideoThemeActivity.folderPath + "/temp");
                        removeFrameImage(VideoThemeActivity.folderPath + "/edittmpzoom");
                        FileUtils.deleteFile(VideoThemeActivity.tempFile);
                        removemusic(VideoThemeActivity.folderPath + "/music/");
                        File f = new File(VideoThemeActivity.outputPath);
                        MediaScannerConnection.scanFile(getApplicationContext(),
                                new String[]{f.getAbsolutePath()},
                                new String[]{"mp4"}, null);

                        try {
                            fileCopy();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        VideoThemeActivity.handler.postDelayed(runnable, 1000);
                    } else if (returnCode == Config.RETURN_CODE_CANCEL) {
                        Log.e("TAG", "Async command execution cancelled by user.");

                    } else {
                        Log.e("TAG", String.format("Async command execution failed with returnCode=%d.", returnCode));

                    }
                })
        );

        Log.e("TAG", "execFFmpegMergeVideo executionId-" + executionId);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            VideoThemeActivity.handler.removeCallbacks(runnable);

            Intent in = new Intent(VideoMakerActivity.this,
                    SaveVideoFileActivity.class);
            in.putExtra("videourl", VideoThemeActivity.outputPath);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(in);
        }
    };


    void removeFrameImage(String path) {
        File appimages = new File(path);
        if (appimages.exists()) {
            File[] files = appimages.listFiles();
            if (files != null) {
                for (File f : files) {
                    if ((f.getName().endsWith(".jpg") || f.getName().endsWith(
                            ".png"))) f.delete();
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
                    if ((f.getName().endsWith(".mp3"))) f.delete();
                }
            }
        }
    }

    boolean back = false;

    @Override
    public void onBackPressed() {
        if (back) {
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

}
