package com.torrober.skyscrapercityclient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.torrober.skyscrapercityclient.ThemeManager.ThemeManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ResourceBundle;

public class WebViewActivity extends AppCompatActivity {

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
                checkIfLogged(view, cookies);
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
                WebViewActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), WebViewActivity.FILECHOOSER_RESULTCODE );
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
        webView.loadUrl(url);
    }
    private void downloadImage(String imageUrl) {
        if(URLUtil.isValidUrl(imageUrl)){
            // Initialize a new download request
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageUrl));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);

            Toast.makeText(getApplicationContext(),"image saved.",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(),"Invalid image url.",Toast.LENGTH_SHORT).show();
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
                        setData(view, userOptions, cookies);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setData(WebView view, JSONObject userData, String cookies) {
        SharedPreferences preferences = getSharedPreferences("userData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        view.evaluateJavascript("document.querySelectorAll('input[name]')[4].value", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                System.out.println("csrf saved");
                s = s.substring(1, 44);
                try {
                    editor.putString("userId",userData.getString("userId"));
                    editor.putString("currentStyle", userData.getString("currentStyle"));
                    editor.putString("userGroupTitle", userData.getString("userGroupTitle"));

                    editor.putString("_xFToken", s);
                    editor.putString("cookie", cookies);
                    if(userData.getString("userGroupTitle").equals("Unregistered / Unconfirmed")) {
                        editor.putBoolean("isRegistered",false);
                    } else {
                        editor.putBoolean("isRegistered",true);
                    }
                    editor.commit();
                    String theme = preferences.getString("currentStyle", "Teal");
                    if(theme.equals("Teal")) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        if(preferences.getBoolean("isRegistered", false)) {
        view.evaluateJavascript("document.querySelectorAll('.avatar-u"+preferences.getString("userId", "")+"-s')[1].getAttribute('alt')", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                System.out.println("username saved");
                s = s.substring(1, 9);
                System.out.println(s);
                if(preferences.getBoolean("isRegistered", false)) {
                    editor.putString("userName", s);
                    editor.commit();
                }
            }
        });
        }
}
}