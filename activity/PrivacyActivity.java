package infiapp.com.videomaker.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import infiapp.com.videomaker.R;

public class PrivacyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        WebView webviwe;
        webviwe = findViewById(R.id.webview);
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.loadUrl("http://epsilonitservice.com/videomaker/Privacy_Policy.html");

        webviwe.getSettings().setJavaScriptEnabled(true);
        webviwe.setWebViewClient(new WebViewClient());

        webviwe.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                loader.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                loader.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
//                /loader.setVisibility(View.GONE);
            }
        });
        webviwe.loadUrl("https://epsilonitservice.com/videomaker/Privacy_Policy.html");
    }
}
