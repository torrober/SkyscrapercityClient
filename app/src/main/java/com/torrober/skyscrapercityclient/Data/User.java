package com.torrober.skyscrapercityclient.Data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;
import android.widget.Toast;

import com.torrober.skyscrapercityclient.RegisterActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class User {

    private SharedPreferences preferences;

    public static String getUserNotifications(String token, String cookies) {
        final String[] res = new String[1];
        String notificationURL = "https://www.skyscrapercity.com/account/visitor-menu?_xfRequestUri=%2F&_xfWithData=1&_xfResponseType=json&_xfToken="+token;
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .build();
        Request request = new Request.Builder()
                .url(notificationURL)
                .addHeader("cookie", cookies)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    res[0] = response.body().string();
                    System.out.println(res[0]);
                } else {
                    res[0] = response.body().string();
                    System.out.println(res[0]);
                }
            }
        });
        return res[0];
    }
    public static void getLogOutUrl(String token, String cookies, Context context) {
        final String[] res = new String[1];
        String notificationURL = "https://www.skyscrapercity.com/account/visitor-menu?_xfRequestUri=%2F&_xfWithData=1&_xfResponseType=json&_xfToken="+token;
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .build();
        Request request = new Request.Builder()
                .url(notificationURL)
                .addHeader("cookie", cookies)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    res[0] = response.body().string();
                    try {
                        JSONObject data = new JSONObject(res[0]);
                        JSONObject html = data.getJSONObject("html");
                        String htmlContent = html.getString("content");
                        Document doc = Jsoup.parse(htmlContent);
                        Elements links = doc.getElementsByTag("a");
                        String linkHref = links.get(9).attr("href");
                        res[0] = "https://skyscrapercity.com"+linkHref;
                        Intent intent = new Intent(context.getApplicationContext(), RegisterActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("url", res[0]);
                        context.startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    res[0] = response.body().string();
                }
            }
        });
    }
    public static void getCSRFToken(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences("userData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String cookies = preferences.getString("cookie", "");
        String userAgent = preferences.getString("userAgent", "");
        String notificationURL = "https://www.skyscrapercity.com";
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .build();
        Request request = new Request.Builder()
                .url(notificationURL)
                .addHeader("cookie", cookies)
                .addHeader("User-Agent", userAgent)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res;
                if(response.isSuccessful()){
                    res = response.body().string();
                    Document doc = Jsoup.parse(res);
                    String token = doc.select("input[name='_XFtoken']").attr("value");
                    editor.putString("_XFToken", token);
                    editor.commit();
                    System.out.println("Token saved "+token);
                } else {
                    System.out.println("CSRF Failed");
                }
            }
        });
    }
    public static void drawUserImage(String UserStyling[], ImageView avatar) {
        String bgColor = UserStyling[0];
        String color = UserStyling[1];
        String innerContent =  UserStyling[2];
        Bitmap bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        float size = 30.0f;
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor(bgColor));
        canvas.drawRect(0, 0, 50, 50, paint);
        paint.setColor(Color.parseColor(color));
        paint.setTextSize(size);
        paint.setTextAlign(Paint.Align.CENTER);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;
        canvas.drawText(innerContent,xPos,yPos,paint);
        avatar.setImageBitmap(bitmap);
    }
}
