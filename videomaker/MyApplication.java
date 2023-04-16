package infiapp.com.videomaker;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.onesignal.OneSignal;

import infiapp.com.videomaker.util.Util_StringShared;
import infiapp.com.videomaker.util.UtilsShareddata;
import infiapp.com.videomaker.model.ImageData;
import infiapp.com.videomaker.model.MusicData;
import infiapp.com.videomaker.theme.mask.AllTheme;
import infiapp.com.videomaker.theme.util.EPreferences;
import infiapp.com.videomaker.theme.util.FileUtils;
import infiapp.com.videomaker.theme.util.PermissionModelUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class MyApplication extends Application {
    public static int VIDEO_HEIGHT;
    public static int VIDEO_WIDTH;
    private static MyApplication instance;
    public static boolean isBreak = false;
    HashMap<String, ArrayList<ImageData>> allAlbum;
    ArrayList<String> allFolder;
    int frame = 0;
    public boolean isEditEnable = false;
    public boolean isFromSdCardAudio = false;
    public int minPos = Integer.MAX_VALUE;
    private MusicData pvmwsMusicData;
    private float second = 3.0f;
    String selectedFolderId = "";
    public final ArrayList<ImageData> selectedImages = new ArrayList();
    public final ArrayList<ImageData> selectedImagesstart = new ArrayList();
    public AllTheme selectedTheme = AllTheme.Mixer;
    public ArrayList<String> videoImages = new ArrayList();
    public static String[] startframelist;
    public static String[] endframelist;

    public static boolean click = false;
    public static SimpleCache simpleCache = null;
    Long exoPlayerCacheSize = (long) (90 * 1024 * 1024);
    LeastRecentlyUsedCacheEvictor leastRecentlyUsedCacheEvictor = null;
    ExoDatabaseProvider exoDatabaseProvider = null;
    Context context;
    FirebaseRemoteConfig mFirebaseRemoteConfig;

    public static MyApplication getInstance() {
        return instance;
    }

    public static String appName = "";
    public static String pkgName = "";

    @Override
    public void onCreate() {
        super.onCreate();
        UtilsShareddata.init(this);

        appName = getResources().getString(R.string.app_name);

        pkgName = getPackageName();

        Log.e("vvv", "file path 1: " + FileUtils.filepath1 + "/" + getResources().getString(R.string.app_name));

        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                    }
                });

        FirebaseApp.initializeApp(context);

        initializeFirebase();
        new Thread(this::firebaseConfig).start();
        try {
            context = this;
        } catch (Exception e) {
            e.printStackTrace();
        }
        FirebaseApp.initializeApp(this);


        instance = this;

        try {
            startframelist = getAssets().list("startframe");

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            endframelist = getAssets().list("endframe");

        } catch (IOException e) {

            e.printStackTrace();
        }

        init();


        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

    }


    private void init() {
        if (!new PermissionModelUtil(this).needPermissionCheck()) {
            getFolderList();
            if (!FileUtils.appDirectory.exists()) {
                FileUtils.appDirectory.mkdirs();
            }

        }
        try {
            setVideoHeightWidth();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setVideoHeightWidth() {
        String s = getResources().getStringArray(R.array.video_height_width)
                [EPreferences.getInstance(getApplicationContext()).getInt(EPreferences.PREF_KEY_VIDEO_QUALITY, 2)];
        StringBuilder sb = new StringBuilder();
        sb.append("Application VideoQuality value  is:- ");
        sb.append(s);
        Log.d("TAG", sb.toString());

    }


    public Bitmap loadBitmapFromAssets(Context context, String path) {
        InputStream stream = null;
        try {
            stream = context.getAssets().open(path);
            return BitmapFactory.decodeStream(stream);
        } catch (Exception ignored) {
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public HashMap<String, ArrayList<ImageData>> getAllAlbum() {
        return this.allAlbum;
    }


    public String getCurrentTheme() {
        return getSharedPreferences("theme", 0).getString("current_theme", AllTheme.Mixer.toString());
    }

    public void getFolderList() {
        this.allFolder = new ArrayList();
        this.allAlbum = new HashMap();
        Cursor query = getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "_id", "bucket_display_name", "bucket_id", "datetaken", "_data"}, (String) null, (String[]) null, "_data DESC");
        if (query.moveToFirst()) {
            int columnIndex = query.getColumnIndex("bucket_display_name");
            int columnIndex2 = query.getColumnIndex("bucket_id");
            setSelectedFolderId(query.getString(columnIndex2));
            do {
                ImageData pvmwsImageData = new ImageData();
                pvmwsImageData.imagePath = query.getString(query.getColumnIndex("_data"));
                pvmwsImageData.imageThumbnail = query.getString(query.getColumnIndex("_data"));
                if (!pvmwsImageData.imagePath.endsWith(".gif")) {
                    String string = query.getString(columnIndex);
                    String string2 = query.getString(columnIndex2);
                    if (!this.allFolder.contains(string2)) {
                        this.allFolder.add(string2);
                    }
                    ArrayList<ImageData> list = (ArrayList) this.allAlbum.get(string2);
                    if (list == null) {
                        list = new ArrayList();
                    }
                    pvmwsImageData.folderName = string;
                    list.add(pvmwsImageData);
                    this.allAlbum.put(string2, list);
                }
            } while (query.moveToNext());
        }
    }

    public int getFrame() {
        return this.frame;
    }

    public MusicData getMusicData() {
        return this.pvmwsMusicData;
    }


    public float getSecond() {
        return this.second;
    }


    public ArrayList<ImageData> getSelectedImages() {
        return this.selectedImages;
    }

    public ArrayList<ImageData> getSelectedImagesstart() {
        return this.selectedImagesstart;
    }


    public void initArray() {
        this.videoImages = new ArrayList();
    }


    public void setCurrentTheme(String s) {
        Editor edit = getSharedPreferences("theme", 0).edit();
        edit.putString("current_theme", s);
        edit.commit();
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public void setMusicData(MusicData pvmwsMusicData) {
        this.pvmwsMusicData = pvmwsMusicData;
    }


    public void setSecond(float second) {
        this.second = second;
    }

    public void setSelectedFolderId(String selectedFolderId) {
        this.selectedFolderId = selectedFolderId;
    }


    public void firebaseConfig() {
        try {
            mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this::setupFirebaseConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void initializeFirebase() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setConfigSettingsAsync(new FirebaseRemoteConfigSettings.Builder().build());
    }

    public void cacheClear() {

        try {
            File dir = getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }


    public void setupFirebaseConfig(Task task) {
        if (task.isSuccessful()) {
            String string = mFirebaseRemoteConfig.getString(getResources().getString(R.string.Kotlins_api));
            String string2 = mFirebaseRemoteConfig.getString(getResources().getString(R.string.Kotlins_key));
            if (TextUtils.isEmpty(UtilsShareddata.get(Util_StringShared.MYGST_API))) {
                UtilsShareddata.set(Util_StringShared.MYGST_API, string);
            }
            if (TextUtils.isEmpty(UtilsShareddata.get(Util_StringShared.MYGST_KEY))) {
                UtilsShareddata.set(Util_StringShared.MYGST_KEY, string2);
            }
        }


        if (leastRecentlyUsedCacheEvictor == null) {
            leastRecentlyUsedCacheEvictor = new LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize);
        }

        if (exoDatabaseProvider != null) {
            exoDatabaseProvider = new ExoDatabaseProvider(this);
        }

        if (simpleCache == null) {
            simpleCache = new SimpleCache(getCacheDir(), leastRecentlyUsedCacheEvictor, exoDatabaseProvider);
            if (simpleCache.getCacheSpace() >= 400207768) {
                cacheClear();
            }
            Log.i("TAG", "onCreate: " + simpleCache.getCacheSpace());
        }


    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

}
