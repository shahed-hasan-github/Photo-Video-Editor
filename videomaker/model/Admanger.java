package infiapp.com.videomaker.model;

import com.google.gson.annotations.SerializedName;


public class Admanger {

    @SerializedName("ads_control")
    String adsControl;
    @SerializedName("google_banner_ad_id")
    String googleBannerAdId;
    @SerializedName("google_interstital_ad_id")
    String googleInterstitalAdId;
    @SerializedName("google_native_ad_id")
    String googleNativeAdId;
    @SerializedName("facebook_banner_ad")
    String facebookBannerAd;
    @SerializedName("facebook_interstital_ad")
    String facebookInterstitalAd;
    @SerializedName("facebook_native_ad_id")
    String facebookNativeAdId;

    public String getAdsControl() {
        return adsControl;
    }

    public void setAdsControl(String adsControl) {
        this.adsControl = adsControl;
    }

    public String getGoogleBannerAdId() {
        return googleBannerAdId;
    }

    public void setGoogleBannerAdId(String googleBannerAdId) {
        this.googleBannerAdId = googleBannerAdId;
    }

    public String getGoogleInterstitalAdId() {
        return googleInterstitalAdId;
    }

    public void setGoogleInterstitalAdId(String googleInterstitalAdId) {
        this.googleInterstitalAdId = googleInterstitalAdId;
    }

    public String getGoogleNativeAdId() {
        return googleNativeAdId;
    }

    public void setGoogleNativeAdId(String googleNativeAdId) {
        this.googleNativeAdId = googleNativeAdId;
    }

    public String getFacebookBannerAd() {
        return facebookBannerAd;
    }

    public void setFacebookBannerAd(String facebookBannerAd) {
        this.facebookBannerAd = facebookBannerAd;
    }

    public String getFacebookInterstitalAd() {
        return facebookInterstitalAd;
    }

    public void setFacebookInterstitalAd(String facebookInterstitalAd) {
        this.facebookInterstitalAd = facebookInterstitalAd;
    }

    public String getFacebookNativeAdId() {
        return facebookNativeAdId;
    }

    public void setFacebookNativeAdId(String facebookNativeAdId) {
        this.facebookNativeAdId = facebookNativeAdId;
    }
}
