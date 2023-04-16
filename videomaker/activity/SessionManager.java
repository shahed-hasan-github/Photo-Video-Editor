package infiapp.com.videomaker.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SessionManager {
    public static String prefAppRated = "isAppRated";

    Context mContext;
    SharedPreferences mPrefs;
    Editor editor;

    public SessionManager(Context context) {
        this.mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.mContext = context;
        editor = mPrefs.edit();
    }

    public boolean getBooleanData(String str) {
        return this.mPrefs.getBoolean(str, false);
    }

    public int getIntData(String str) {
        return this.mPrefs.getInt(str, -1);
    }

    public String getStringData(String str) {
        return this.mPrefs.getString(str, "");
    }

    public void logoutUser() {
        this.editor.clear();
        this.editor.commit();
    }

    public void setBooleanData(String str, Boolean bool) {
        this.editor.putBoolean(str, bool.booleanValue());
        this.editor.commit();
    }

    public void setIntData(String str, int i) {
        this.editor.putInt(str, i);
        this.editor.commit();
    }

    public void setStringData(String str, String str2) {
        this.editor.putString(str, str2);
        this.editor.commit();
    }
}
