package com.torrober.skyscrapercityclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.torrober.skyscrapercityclient.API.API;
import com.torrober.skyscrapercityclient.Data.User;
import com.torrober.skyscrapercityclient.ThemeManager.ThemeManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        SharedPreferences preferences;
        preferences = getSharedPreferences("userData", MODE_PRIVATE);
        User.getCSRFToken(this.getApplicationContext());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String token = preferences.getString("_xFToken", "");
                String cookies = preferences.getString("cookie", "");
                User.getLogOutUrl(token,cookies, getApplicationContext());
        }});
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        View header = navigationView.getHeaderView(0);
        //Switch switch1 = (Switch) header.findViewById(R.id.switch1);
        ImageView userAvatar = (ImageView) header.findViewById(R.id.imageView);
        TextView userId = (TextView) header.findViewById(R.id.userid);
        if(!preferences.getBoolean("isRegistered", false)) {
            //switch1.setEnabled(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please, log in or sign up.");
            builder.setPositiveButton("Log in", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                    intent.putExtra("url", "https://www.skyscrapercity.com/login/");
                    startActivity(intent);
                }
            });
            builder.setNeutralButton("Sign up", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                    intent.putExtra("url", "https://www.skyscrapercity.com/register/");
                    startActivity(intent);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }  else {
            //https://www.skyscrapercity.com/d1/avatars/m/1139/1139957.jpg
            String userID = preferences.getString("userId", "0");
            String stringImage = "https://www.skyscrapercity.com/d1/avatars/m/"+userID.substring(0,4)+"/"+userID+".jpg";
            System.out.println(stringImage);
            Picasso.get()
                    .load(stringImage)
                    .placeholder(R.drawable.defaut_icon_background)
                    .error(R.drawable.defaut_icon_background)
                    .into(userAvatar)
            ;
            System.out.println(stringImage);
            userId.setText(preferences.getString("userName", "Unregistered"));
            userAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String res = "https://skyscrapercity.com";
                    Intent intent = new Intent(getApplicationContext(), TopicActivity.class);
                    intent.putExtra("url", res);
                    startActivity(intent);
                }
            });
        };
        if(preferences.getString("currentStyle", "Teal").equals("Teal")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}