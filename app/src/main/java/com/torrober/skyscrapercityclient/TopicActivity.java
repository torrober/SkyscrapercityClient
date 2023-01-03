package com.torrober.skyscrapercityclient;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class TopicActivity extends AppCompatActivity {

    private WebView webView;
    private String url;
    private final static int FILECHOOSER_RESULTCODE = 1;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private ValueCallback<Uri[]> mUploadMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView = findViewById(R.id.webview);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        SharedPreferences preferences;
        preferences =  getSharedPreferences("userData",MODE_PRIVATE);
        String style = preferences.getString("currentStyle", "Teal");
        ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.webviewLayout);
        url = bundle.getString("url");;
        webView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override public boolean onLongClick(View v)
            {

                WebView webView = (WebView) v;
                final WebView.HitTestResult hr = webView.getHitTestResult();

                int type = hr.getType();
                if (type == hr.IMAGE_TYPE || type == hr.SRC_IMAGE_ANCHOR_TYPE) {
                    String imageUrl = hr.getExtra();
                    downloadImage(imageUrl);
                }


                return false  ;} });
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String newurl) {
                String urlHost = Uri.parse(newurl).getHost();
                switch (urlHost) {
                    case "skyscrapercity.com":
                        return false;
                    case "www.skyscrapercity.com":
                        return false;
                    default:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newurl));
                        startActivity(intent);
                        return true;
                }
            }
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onPageFinished(WebView view, String newurl) {
                //removes header and another elements
                view.evaluateJavascript("document.querySelector('header').style = 'display: none'; document.querySelectorAll('.p-sectionLinks')[0].style = 'display: none;'; document.querySelector('#header-banner').style = 'display: none'; document.querySelector('#footer').style = 'display: none;'; document.querySelector('.p-breadcrumbs--bottom').style = 'display: none;'; document.querySelector('#thread-recommended-reading').style = 'display: none';", null);
                String cookies = CookieManager.getInstance().getCookie(newurl);

                view.evaluateJavascript("window.adConfig.currentStyle", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        if(s.contains("DarkMode")){
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            String ua =webView.getSettings().getUserAgentString();
                            System.out.println(ua);
                            saveSettings(cookies, "DarkMode", ua);
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            String ua =webView.getSettings().getUserAgentString();
                            saveSettings(cookies, "Teal",  ua);
                        }
                    }
                });
                ProgressBar bar = findViewById(R.id.progressBar);
                bar.setVisibility(View.INVISIBLE);
                view.setVisibility(View.VISIBLE);
                url = newurl;
            }
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                view.setVisibility(View.INVISIBLE);
                ProgressBar bar = findViewById(R.id.progressBar);
                bar.setVisibility(View.VISIBLE);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                }
                mUploadMessage = filePathCallback;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*"); // set MIME type to filter
                TopicActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), TopicActivity.FILECHOOSER_RESULTCODE );
                return true;
            }
        });
        webView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                WebView webView = (WebView) v;

                switch(keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        if (webView.canGoBack()) {
                            webView.goBack();
                            return true;
                        }
                        break;
                }
            }

            return false;
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.loadUrl(url);
    }
    private void downloadImage(String imageUrl) {
        if(URLUtil.isValidUrl(imageUrl)){
            Intent intent = new Intent(this, ImageActivity.class);
            intent.putExtra("url", imageUrl);
            intent.putExtra("backurl", url);
            startActivity(intent);
        }
    }
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        // manejo de seleccion de archivo
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FILECHOOSER_RESULTCODE) {

            if (null == mUploadMessage || intent == null || resultCode != RESULT_OK) {
                return;
            }

            Uri[] result = null;
            String dataString = intent.getDataString();

            if (dataString != null) {
                result = new Uri[]{Uri.parse(dataString)};
            }

            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void checkIfLogged(final WebView view, String cookies) {
        view.evaluateJavascript("window.adConfig", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                if (s.contains("null")){
                } else {
                    try {
                        JSONObject userOptions = new JSONObject(s);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void saveSettings(String cookies, String theme, String userAgent) {
        SharedPreferences preferences = getSharedPreferences("userData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("cookie", cookies);
        editor.putString("User-Agent", userAgent);
        editor.putString("currentStyle", theme);
        editor.commit();
        System.out.println("cookies saved "+ cookies);
    }
}
