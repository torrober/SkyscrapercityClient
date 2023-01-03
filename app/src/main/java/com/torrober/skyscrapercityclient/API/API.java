package com.torrober.skyscrapercityclient.API;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class API {
    //https://www.skyscrapercity.com/v1/nodes/top-forums
    //https://www.skyscrapercity.com/content-feed?pageNumber=1
    public static String getTopForums(String cookies) {
        final String[] res = new String[1];
        String notificationURL = "https://www.skyscrapercity.com/v1/nodes/top-forums";
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
                } else {
                    res[0] = "Error";
                }
            }
        });
        return res[0];
    }
    public static String getContentFeed(String cookies) {
        final String[] res = new String[1];
        String notificationURL = "https://www.skyscrapercity.com/content-feed?pageNumber=1";
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
                } else {
                    res[0] = response.body().string();
                    System.out.println(res[0]);
                }
            }
        });
        return res[0];
    }

}
