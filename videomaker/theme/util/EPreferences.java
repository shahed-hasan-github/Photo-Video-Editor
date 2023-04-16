package infiapp.com.videomaker.theme.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.ArrayList;
import java.util.Arrays;

public class EPreferences {

    public static final String PREF_KEY_VIDEO_QUALITY = "pref_key_video_quality";
    private static final String PREF_NAME = "slideshow_pref";
    String prefKeyUrl = "all_url";
    private SharedPreferences mCsPref;

    private EPreferences(Context context, String s, int n) {
        this.mCsPref = context.getSharedPreferences(s, n);
    }

    public static EPreferences getInstance(Context context) {
        return new EPreferences(context, PREF_NAME, 0);
    }

    public boolean checkUrlAvailable(String s) {
        return getCsvURL().contains(s);
    }

    public void clear(String s) {
        this.mCsPref.edit().remove(s).commit();
    }

    public void clearPref() {
        Editor edit = this.mCsPref.edit();
        edit.clear();
        edit.commit();
        StringBuilder sb = new StringBuilder();
        sb.append("Pref=>");
        sb.append(getCsvURL());
    }

    public void clearURLPref() {
        Editor edit = this.mCsPref.edit();
        edit.putString(this.prefKeyUrl, "");
        edit.commit();
    }

    public ArrayList<String> getAllURL() {
        String csvURL = getCsvURL();
        if (csvURL == null || csvURL.equals("") || csvURL.length() <= 0) {
            return null;
        }
        return new ArrayList(Arrays.asList(csvURL.split(",")));
    }

    public boolean getBoolean(String s, boolean b) {
        return this.mCsPref.getBoolean(s, b);
    }

    public String getCsvURL() {
        return this.mCsPref.getString(this.prefKeyUrl, "");
    }

    public int getInt(String s, int n) {
        return this.mCsPref.getInt(s, n);
    }

    public String getString(String s, String s2) {
        return this.mCsPref.getString(s, s2);
    }

    public int putBoolean(String s, boolean b) {
        Editor edit = this.mCsPref.edit();
        edit.putBoolean(s, b);
        edit.commit();
        return 0;
    }

    public int putInt(String s, int n) {
        Editor edit = this.mCsPref.edit();
        edit.putInt(s, n);
        edit.commit();
        return 0;
    }



    public int putString(String s, String s2) {
        Editor edit = this.mCsPref.edit();
        edit.putString(s, s2);
        edit.commit();
        return 0;
    }

    public void putURLValue(String s) {
        Editor edit = this.mCsPref.edit();
        String csvURL = getCsvURL();
        if (csvURL == null || csvURL.equals("") || csvURL.length() <= 0) {
            edit.putString(this.prefKeyUrl, s);
        } else {
            String prefKeyUrl = this.prefKeyUrl;
            StringBuilder sb = new StringBuilder();
            sb.append(csvURL);
            sb.append(",");
            sb.append(s);
            edit.putString(prefKeyUrl, sb.toString());
        }
        edit.commit();
    }

    public void setString(String s, String s2) {
        Editor edit = this.mCsPref.edit();
        edit.putString(s, s2);
        edit.commit();
    }
}
