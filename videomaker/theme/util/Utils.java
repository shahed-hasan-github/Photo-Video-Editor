package infiapp.com.videomaker.theme.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build.VERSION;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import infiapp.com.videomaker.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import infiapp.com.videomaker.model.MusicData;

public class Utils {

    public static final String FFPAUDIO = "ffpaudio";
    private static final int REQUESTPERMISSIONCODE = 222;
   int height = 0;
   String jsonAudioFile = "https://raw.githubusercontent.com/NihalPandya/demoUpload/master/JsonAudioList.txt";
    ArrayList<ProgressModel> mPrgModel = new ArrayList();
    ArrayList<String> allAudioFileLink;
    ArrayList<String> localFileName;
    EPreferences myPref;
    public static int framePostion =-1;
    public static int defaultFontScale = 1;

    public static void applyFontToMenuItem(Context context, MenuItem menuItem) {
        Typeface fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/Comfortaa-Regular.ttf");
        SpannableString title = new SpannableString(menuItem.getTitle());
        title.setSpan(new CustomTypefaceSpan("", fromAsset), 0, title.length(), 18);
        menuItem.setTitle(title);
    }

    public static boolean checkConnectivity(Context context, boolean b) {
        if (isNetworkConnected(context)) {
            return true;
        }
        if (!b) {
            return false;
        }
        Toast.makeText(context, "Data/Wifi Not Available", Toast.LENGTH_SHORT).show();
        return false;
    }

    public static boolean checkPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(context, "android.permission.READ_EXTERNAL_STORAGE") == 0;
    }

    public static float dp2px(Resources resources, float n) {
        return (resources.getDisplayMetrics().density * n) + 0.5f;
    }

    public static int dpToPx(int n) {
        return (int) (((float) n) * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int[] getCordinate(Bitmap bitmap, int n) {
        int n3;
        int n4;
        int n5;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > height) {
            int n2 = ((int) ((100.0f - ((float) (height / (width / 100)))) * ((float) (n / 100)))) / 2;
            n3 = n - n2;
            n4 = n2 + 0;
            n5 = 0;
        } else {
            int n6 = ((int) ((100.0f - ((float) (width / (height / 100)))) * ((float) (n / 100)))) / 2;
            int n8 = n;
            n -= n6;
            n4 = 0;
            n3 = n8;
            n5 = n6 + 0;
        }
        return new int[]{n5, n4, n, n3};
    }

    public static int getPixelForDp(Context context, int n) {
        return (int) TypedValue.applyDimension(1, (float) n, context.getResources().getDisplayMetrics());
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static String getVideoName() {
        String s;
        String s2;
        Calendar instance = Calendar.getInstance();
        int value = instance.get(Calendar.DAY_OF_WEEK);
        if (2 == value) {
            s = "Monday";
        } else if (3 == value) {
            s = "Tuesday";
        } else if (4 == value) {
            s = "Wednesday";
        } else if (5 == value) {
            s = "Thursday";
        } else if (6 == value) {
            s = "Friday";
        } else if (7 == value) {
            s = "Saturday";
        } else if (value != 0) {
            s = "Sunday";
        } else {
            s = null;
        }
        int value2 = instance.get(Calendar.HOUR_OF_DAY);
        if (value2 >= 5 && value2 < 12) {
            s2 = "Morning";
        } else if (value2 >= 12 && value2 < 18) {
            s2 = "Noon";
        } else if (value2 < 18 || value2 >= 24) {
            s2 = "Night";
        } else {
            s2 = "Evening";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append(" ");
        sb.append(s2);
        return sb.toString();
    }

    public static Path getWavePath(float n, float n2, float n3, float n4, float n5) {
        Path path = new Path();
        n2 -= n3;
        path.moveTo(0.0f, 0.0f);
        path.lineTo(0.0f, n2);
        int n6 = 0;
        while (true) {
            float n7 = (float) n6;
            if (n7 >= 10.0f + n) {
                path.lineTo(n, 0.0f);
                path.close();
                return path;
            }
            n6 += 10;
            path.lineTo(n7, (((float) Math.sin((((((double) n6) * 3.141592653589793d) / 180.0d) / ((double) n5)) + ((double) n4))) * n3) + n2);
        }
    }

    public static void hideKeyBoard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 2);
        }
    }

    public static boolean isLmpMR1() {
        return VERSION.SDK_INT == 22;
    }

    public static boolean isLmpMR1OrAbove() {
        return VERSION.SDK_INT >= 22;
    }

    public static boolean isLmpOrAbove() {
        return VERSION.SDK_INT >= 21;
    }

    public static boolean isNetworkConnected(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }


    public static void requestPermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"}, REQUESTPERMISSIONCODE);
    }

    public static void setFont(Activity activity, int n) {
        View viewById = activity.findViewById(n);
        Typeface fromAsset = Typeface.createFromAsset(activity.getAssets(), "fonts/Comfortaa-Regular.ttf");
        if (viewById instanceof TextView) {
            ((TextView) activity.findViewById(n)).setTypeface(fromAsset);
        } else if (viewById instanceof Button) {
            ((Button) activity.findViewById(n)).setTypeface(fromAsset);
        }
    }

    public static void setFont(Activity activity, View view) {
        Typeface fromAsset = Typeface.createFromAsset(activity.getAssets(), "fonts/Comfortaa-Regular.ttf");
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(fromAsset);
        } else if (view instanceof Button) {
            ((Button) view).setTypeface(fromAsset);
        } else if (view instanceof EditText) {
            ((EditText) view).setTypeface(fromAsset);
        } else if (view instanceof CheckBox) {
            ((CheckBox) view).setTypeface(fromAsset);
        }
    }

    public static void setFont(Activity activity, TextView textView) {
        textView.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Comfortaa-Regular.ttf"));
    }

    @SuppressLint({"NewApi"})
    public static void setStatusBarColor(Activity activity) {
        if (VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.clearFlags(67108864);
            window.setStatusBarColor(activity.getResources().getColor(R.color.imagepickerColor));
        }
    }

    public static void slideDown(Context context, View view) {
        Animation loadAnimation = AnimationUtils.loadAnimation(context, R.anim.bottom_down);
        view.setVisibility(View.GONE);
        view.startAnimation(loadAnimation);
    }

    public static void slideUp(Context context, View view) {
        Animation loadAnimation = AnimationUtils.loadAnimation(context, R.anim.bottom_up);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(loadAnimation);
    }

    public static float sp2px(Resources resources, float n) {
        return resources.getDisplayMetrics().scaledDensity * n;
    }

    public void fillJsonData() {
        Log.e("PREE", "fillJsonData() called");
        String makeServiceCall = new HttpHandler().makeServiceCall(jsonAudioFile);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Response from url: ");
        stringBuilder.append(makeServiceCall);
        if (makeServiceCall != null) {
            try {
                JSONArray jSONArray = new JSONObject(makeServiceCall).getJSONArray("audio_list");
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                    String string = jSONObject.getString("url");
                    String string2 = jSONObject.getString("display_name");
                    this.allAudioFileLink.add(string);
                    this.localFileName.add(string2);
                }
            } catch (JSONException e) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Json parsing error: ");
                stringBuilder.append(e.getMessage());
            }
        } else {
            Log.e("JSON", "Couldn't get json from server.");
        }
        Log.d("PREE", "fillJsonData() called End");
    }

    public final String getAudioFolderPath() {
        StringBuilder sb = new StringBuilder();
        sb.append(getOutputPath());
        sb.append(FFPAUDIO);
        sb.append(File.separator);
        String string = sb.toString();
        File file = new File(string);
        if (!file.exists()) {
            file.mkdirs();
        }
        return string;
    }

    public ArrayList<MusicData> getOnlineAudioFiles(Context context) {
        this.myPref = EPreferences.getInstance(context);
        this.allAudioFileLink = new ArrayList();
        this.localFileName = new ArrayList();
        ArrayList<MusicData> list = new ArrayList();
        fillJsonData();
        String audioFolderPath = getAudioFolderPath();
        for (int i = 0; i < this.allAudioFileLink.size(); i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(audioFolderPath);
            stringBuilder.append( this.localFileName.get(i));
            if (new File(stringBuilder.toString()).exists()) {
                for (int i2 = 0; i2 < mPrgModel.size(); i2++) {
                    if (( mPrgModel.get(i2)).uri.equals(this.allAudioFileLink.get(i))) {
                        MusicData pvmwsMusicData = new MusicData();
                        pvmwsMusicData.trackDisplayName =  this.localFileName.get(i);
                        pvmwsMusicData.isAvailableOffline = ( mPrgModel.get(i2)).isAvailableOffline;
                        pvmwsMusicData.isDownloading = ( mPrgModel.get(i2)).isDownloading;
                        pvmwsMusicData.songDownloadUri = ( mPrgModel.get(i2)).uri;
                        list.add(pvmwsMusicData);
                        break;
                    }
                }
                if (mPrgModel.size() == 0) {
                    ArrayList allURL = this.myPref.getAllURL();
                    if (allURL != null && allURL.size() > 0) {
                        for (int i3 = 0; i3 < allURL.size(); i3++) {
                            if (( allURL.get(i3)).equals(this.allAudioFileLink.get(i))) {
                                MusicData pvmwsMusicData2 = new MusicData();
                                pvmwsMusicData2.trackDisplayName =  this.localFileName.get(i);
                                pvmwsMusicData2.isAvailableOffline = false;
                                pvmwsMusicData2.isDownloading = false;
                                pvmwsMusicData2.songDownloadUri =  this.allAudioFileLink.get(i);
                                list.add(pvmwsMusicData2);
                            }
                        }
                    }
                }
            } else {
                MusicData pvmwsMusicData3 = new MusicData();
                pvmwsMusicData3.trackDisplayName =  this.localFileName.get(i);
                pvmwsMusicData3.isAvailableOffline = false;
                pvmwsMusicData3.songDownloadUri =  this.allAudioFileLink.get(i);
                list.add(pvmwsMusicData3);
            }
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("mMusicDatas.size() = ");
        stringBuilder2.append(list.size());
        Log.d("PREE", stringBuilder2.toString());
        Log.d("PREE", "getOnlineAudioFiles() End");
        return list;
    }

    public final String getOutputPath() {
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtils.appDirectory.getAbsolutePath());
        sb.append(File.separator);
        String string = sb.toString();
        File file = new File(string);
        if (!file.exists()) {
            file.mkdirs();
        }
        return string;
    }
}
