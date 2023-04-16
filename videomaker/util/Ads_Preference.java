package infiapp.com.videomaker.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Ads_Preference {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String PRIORITY = "PRIORITY";

    String ADMOB_APP_ID = "ADMOB_APP_ID";
    String ADMOB_ADS_CLICK_COUNT = "ads_click_counrter";
    String ADMOB_INTERSTITIAL_ID = "ADMOB_INTERSTITIAL_ID";
    String ADMOB_NATIVE_ADVANCED_ID = "ADMOB_NATIVE_ADVANCED_ID";
    String ADMOB_BANNER_ID = "ADMOB_BANNER_ID";
    String ADMOB_OPEN_AD_ID = "ADMOB_OPEN_AD_ID";
    String ADMOB_REWARDED_AD_ID = "ADMOB_REWARDED_AD_ID";

    String FACEBOOK_APP_ID = "FACEBOOK_APP_ID";
    String FACEBOOK_INTERSTITIAL_ID = "FACEBOOK_INTERSTITIAL_ID";
    String FACEBOOK_NATIVE_ID = "FACEBOOK_NATIVE_ID";
    String FACEBOOK_BANNER_ID = "FACEBOOK_BANNER_ID";
    String FACEBOOK_REWARDED="FACEBOOK_REWARDED_ID";

    String OneSignalAppId="onesignal_app_id";
    String OneSignalRestKey="onesignal_rest_key";

    public Ads_Preference(Context context) {
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences("Rohit Kotar", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public String get_priority() {
        return sharedPreferences.getString(PRIORITY, "");
    }

    public void set_priority(String priority) {
        this.editor.putString(this.PRIORITY, priority).commit();
    }

    public String getADMOB_ADS_CLICK_COUNT(){
        return sharedPreferences.getString(ADMOB_ADS_CLICK_COUNT,"");
    }
    public void setADMOB_ADS_CLICK_COUNT(String admob_ads_click_count){
        this.editor.putString(ADMOB_ADS_CLICK_COUNT,admob_ads_click_count).commit();
    }


    //Admob Id's
    public String get_admob_app_id() {
        return sharedPreferences.getString(ADMOB_APP_ID, "");
    }

    public void set_admob_app_id(String appid) {
        this.editor.putString(this.ADMOB_APP_ID, appid).commit();
    }

    public String get_admob_interstitial_id() {
        return sharedPreferences.getString(ADMOB_INTERSTITIAL_ID, "");
    }

    public void set_admob_interstitial_id(String interstitial) {
        this.editor.putString(this.ADMOB_INTERSTITIAL_ID, interstitial).commit();
    }

    public String get_admob_native_advance_id() {
        return sharedPreferences.getString(ADMOB_NATIVE_ADVANCED_ID, "");
    }

    public void set_admob_native_advance_id(String native_advance) {
        this.editor.putString(this.ADMOB_NATIVE_ADVANCED_ID, native_advance).commit();
    }

    public String get_admob_banner_id() {
        return sharedPreferences.getString(ADMOB_BANNER_ID, "");
    }

    public void set_admob_banner_id(String banner) {
        this.editor.putString(this.ADMOB_BANNER_ID, banner).commit();
    }

    public String get_admob_open_ad_id() {
        return sharedPreferences.getString(ADMOB_OPEN_AD_ID, "");
    }

    public void set_admob_open_ad_id(String open_ad) {
        this.editor.putString(this.ADMOB_OPEN_AD_ID, open_ad).commit();
    }

    public String getADMOB_REWARDED_AD_ID(){
        return this.sharedPreferences.getString(ADMOB_REWARDED_AD_ID,"");
    }

    public void  setADMOB_REWARDED_AD_ID(String rewarded_ad_id){
        this.editor.putString(ADMOB_REWARDED_AD_ID,rewarded_ad_id).commit();
    }


        //Facebook Id's

    public String get_facebook_app_id() {
        return sharedPreferences.getString(FACEBOOK_APP_ID, "");
    }

    public void set_facebook_app_id(String appid) {
        this.editor.putString(this.FACEBOOK_APP_ID, appid).commit();
    }

    public String get_facebook_interstitial_id() {
        return sharedPreferences.getString(FACEBOOK_INTERSTITIAL_ID, "CAROUSEL_IMG_SQUARE_APP_INSTALL");
    }

    public void set_facebook_interstitial_id(String interstitialId) {
        this.editor.putString(this.FACEBOOK_INTERSTITIAL_ID, interstitialId).commit();
    }

    public String get_facebook_native_id() {
        return sharedPreferences.getString(FACEBOOK_NATIVE_ID, "CAROUSEL_IMG_SQUARE_LINK");
    }

    public void set_facebook_native_id(String native_id) {
        this.editor.putString(this.FACEBOOK_NATIVE_ID, native_id).commit();
    }

    public String get_facebook_banner_id() {
        return sharedPreferences.getString(FACEBOOK_BANNER_ID, "IMG_16_9_APP_INSTALL");
    }

    public void set_facebook_banner_id(String banner_id) {
        this.editor.putString(this.FACEBOOK_BANNER_ID, banner_id).commit();
    }

    public void setOneSignalAppId(String oneSignalAppId){
        this.editor.putString(this.OneSignalAppId,OneSignalAppId).commit();
    }

    public String getOneSignalAppId(){
       return this.sharedPreferences.getString(this.OneSignalAppId,"");
    }

    public void setOneSignalRestKey(String OneSignalRestKey){
        this.editor.putString(this.OneSignalRestKey,OneSignalRestKey).commit();
    }

    public String getOneSignalRestKey(){
       return this.sharedPreferences.getString(this.OneSignalRestKey,"");
    }

}
