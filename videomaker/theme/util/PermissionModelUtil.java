package infiapp.com.videomaker.theme.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;


public class PermissionModelUtil {
    String[] necessaryPermission = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private Context context;
    SharedPreferences sharedPrefs;

    private PermissionModelUtil() {
    }

    public PermissionModelUtil(Context context) {
        this.context = context;
        this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean needPermissionCheck() {
        boolean z = false;
        if (VERSION.SDK_INT < 23) {
            return false;
        }
        if (this.context.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            z = true;
        }
        return z;
    }



}
