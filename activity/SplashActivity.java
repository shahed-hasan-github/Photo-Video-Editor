package infiapp.com.videomaker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import infiapp.com.videomaker.R;
import infiapp.com.videomaker.util.Ads_Preference;

public class SplashActivity extends AppCompatActivity {

    public static String Google_banner, Google_interstitial, ads_click_counrter;

    Ads_Preference ads_preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ads_preference = new Ads_Preference(this);
        startact();
        ads_preference.setADMOB_ADS_CLICK_COUNT(getString(R.string.adsCount));
        ads_preference.set_admob_banner_id(getString(R.string.admob_banner));
        ads_preference.set_admob_interstitial_id(getString(R.string.admob_inter));

    }


    private void startact() {

        new Handler().postDelayed(() -> {

            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);

            finish();
        }, 2000);
    }
}
