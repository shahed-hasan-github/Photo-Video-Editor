package infiapp.com.videomaker.theme.service;

import android.app.IntentService;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.DisplayMetrics;


import infiapp.com.videomaker.MyApplication;
import infiapp.com.videomaker.R;
import infiapp.com.videomaker.util.KSUtil;
import infiapp.com.videomaker.theme.mask.MaskBitmap3D;
import infiapp.com.videomaker.theme.util.FileUtils;
import infiapp.com.videomaker.theme.util.ScalingUtilities;
import infiapp.com.videomaker.theme.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


public class ServiceAnim extends IntentService {
    public static final String EXTRA_SELECTED_THEME = "selected_theme";
    ArrayList<String> arrayList;
    public static boolean isImageComplate = false;
    String selectedTheme;
    MyApplication application;
    boolean check;
    Builder mBuilder;
    NotificationManager mNotifyManager;
    int totalImages;
    DisplayMetrics displayMetrics;

    public ServiceAnim() {
        this(ServiceAnim.class.getName());
    }

    public ServiceAnim(String name) {
        super(name);
        this.check = false;
    }

    public void onCreate() {
        super.onCreate();
        this.application = MyApplication.getInstance();
        displayMetrics = getResources().getDisplayMetrics();

    }

    @Deprecated
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void onHandleIntent(Intent intent) {
        this.mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        this.mBuilder = new Builder(this);
        this.mBuilder.setContentTitle("Preparing Video").setContentText("Making in progress").setSmallIcon(R.mipmap.ic_launcher);
        selectedTheme = intent.getStringExtra(EXTRA_SELECTED_THEME);
        arrayList = KSUtil.videoPathList;
        this.application.initArray();
        isImageComplate = false;
        new ProcessImage1().execute();

    }

    class ProcessImage1 extends AsyncTask<Void, Void, Boolean> {
        protected void onPreExecute() {
            //dosomething
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            createImages();
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            isImageComplate = true;
            stopSelf();
            isSameTheme();
        }
    }

    private void createImages() {
        Bitmap newSecondBmp2 = null;
        this.totalImages = arrayList.size();
        int i = 0;
        int imgSize = 0;


        while (i < arrayList.size() - 1 && isSameTheme() && !MyApplication.isBreak) {
            Bitmap newFirstBmp;
            File imgDir = FileUtils.getImageDirectory(this.application.selectedTheme.toString());
            Bitmap firstBitmap = null;
            Bitmap temp;
            if (i == 0) {
                firstBitmap = ScalingUtilities.checkBitmap(arrayList.get(i), application);
                temp = ScalingUtilities.scaleCenterCrop(firstBitmap, MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT);
                newFirstBmp = ScalingUtilities.convetrSameSize(firstBitmap, temp, MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Utils.defaultFontScale, 0.0f);
                temp.recycle();
                firstBitmap.recycle();
                System.gc();
            } else {
                if (newSecondBmp2 == null || newSecondBmp2.isRecycled()) {
                    firstBitmap = ScalingUtilities.checkBitmap(arrayList.get(i), application);
                    temp = ScalingUtilities.scaleCenterCrop(firstBitmap, MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT);
                    newSecondBmp2 = ScalingUtilities.convetrSameSize(firstBitmap, temp, MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Utils.defaultFontScale, 0.0f);
                    temp.recycle();
                    firstBitmap.recycle();
                    System.gc();
                }
                newFirstBmp = newSecondBmp2;
            }
            Bitmap secondBitmap = ScalingUtilities.checkBitmap(arrayList.get(i + 1), application);
            Bitmap temp2 = ScalingUtilities.scaleCenterCrop(secondBitmap, MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT);
            newSecondBmp2 = ScalingUtilities.convetrSameSize(secondBitmap, temp2, MyApplication.VIDEO_WIDTH, MyApplication.VIDEO_HEIGHT, Utils.defaultFontScale, 0.0f);

            temp2.recycle();
            secondBitmap.recycle();
            System.gc();
            MaskBitmap3D.reintRect();

            MaskBitmap3D.EFFECT effect = this.application.selectedTheme.getTheme().get(i % this.application.selectedTheme.getTheme().size());
            effect.initBitmaps(newFirstBmp, newSecondBmp2);
            Bitmap bitmap3 = null;
            for (int j = 0; ((float) j) < MaskBitmap3D.animatedFrame && isSameTheme() && !MyApplication.isBreak; j++) {


                bitmap3 = effect.getMask(newFirstBmp, newSecondBmp2, j);

                if (isSameTheme()) {
                    File file = imgDir;
                    File file2 = new File(file, String.format("img%05d.jpg", new Object[]{Integer.valueOf(imgSize)}));
                    imgSize++;
                    try {
                        if (file2.exists()) {
                            file2.delete();
                        }
                        OutputStream fileOutputStream = new FileOutputStream(file2);
                        bitmap3.compress(CompressFormat.JPEG, 70, fileOutputStream);
                        fileOutputStream.close();
                        System.gc();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    boolean isBreak = false;
                    while (this.application.isEditEnable) {
                        if (this.application.minPos != Integer.MAX_VALUE) {
                            i = this.application.minPos;
                            isBreak = true;
                        }

                    }
                    if (isBreak) {
                        ArrayList<String> str = new ArrayList();
                        str.addAll(this.application.videoImages);
                        this.application.videoImages.clear();
                        int size = Math.min(str.size(), Math.max(0, i - i) * 30);
                        for (int p = 0; p < size; p++) {
                            this.application.videoImages.add(str.get(p));
                        }
                        this.application.minPos = Integer.MAX_VALUE;
                    }
                    if (!isSameTheme() || MyApplication.isBreak) {
                        break;
                    }
                    this.application.videoImages.add(file2.getAbsolutePath());
                    if (((float) j) == MaskBitmap3D.animatedFrame - Utils.defaultFontScale) {
                        for (int m = 0; m < 8 && isSameTheme() && !MyApplication.isBreak; m++) {
                            this.application.videoImages.add(file2.getAbsolutePath());
                        }
                    }
                    if (((float) imgSize) == MaskBitmap3D.animatedFrame) {
                        break;
                    }
                }
            }
            i++;

            this.check = true;
        }
        isImageComplate = true;
        stopSelf();
        isSameTheme();
    }

    private boolean isSameTheme() {
        return selectedTheme.equals(this.application.getCurrentTheme());
    }


}
