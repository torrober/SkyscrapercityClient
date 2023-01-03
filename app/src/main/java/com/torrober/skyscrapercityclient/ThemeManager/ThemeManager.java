package com.torrober.skyscrapercityclient.ThemeManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;

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

import static android.content.Context.MODE_PRIVATE;

public class ThemeManager {
    public static boolean setThemeMode(Context context) {
        //https://www.skyscrapercity.com/misc/style?style_id=2
        SharedPreferences preferences;
        preferences = context.getSharedPreferences("userData", MODE_PRIVATE);
        String mode = preferences.getString("currentStyle", "Teal");
        String cookies = preferences.getString("cookie", "");
        String token = preferences.getString("_xFToken", "");
        boolean isRegistered = preferences.getBoolean("isRegistered", false);
        System.out.println(preferences.getString("userGroupTitle", "")+" "+ isRegistered);
        if(!isRegistered) {
            return false;
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            if (mode.equals("Teal")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putString("currentStyle", "DarkMode");
                changeThemeOnWebsite(cookies, "DarkMode", token, context);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putString("currentStyle", "Teal");
                changeThemeOnWebsite(cookies, "Teal", token,context);
            }
            editor.commit();
            return true;
        }

    }
    private static void changeThemeOnWebsite(String cookies, String theme, String csrfToken, Context context) {
        String url;
        if(theme.equals("Teal")) {
             url = "https://www.skyscrapercity.com/misc/style?style_id=9&t="+csrfToken;
        } else {
             url = "https://www.skyscrapercity.com/misc/style?style_id=2&t="+csrfToken;
        }
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .build();
        Request request = new Request.Builder()
                .url(url)
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
                    System.out.println("Theme Changed");
                } else {
                    System.out.println(response.body().string());
                }
            }
        });
    }
}
