package com.torrober.skyscrapercityclient;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.torrober.skyscrapercityclient.Adapter.MyFragmentAdapter;
import com.torrober.skyscrapercityclient.Data.User;

public class UserActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private MyFragmentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /* get data from previous activity*/
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String username = bundle.getString("username");
        TextView userNameText = findViewById(R.id.userName);
        TextView userInfo = findViewById(R.id.userInfo);
        ImageView userAvatar = findViewById(R.id.userAvatar);
        userNameText.setText(username);
        userInfo.setText(bundle.getString("userInfo"));
        boolean hasPFP = bundle.getBoolean("hasPFP");
        if(hasPFP) {
            Picasso.get()
                    .load(bundle.getString("pfpLink"))
                    .into(userAvatar, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            System.out.println(e);
                        }
                    });
        } else {
            String[] userStyling = intent.getStringArrayExtra("userStyling");
            User.drawUserImage(userStyling,userAvatar);
        }
        tabLayout = findViewById(R.id.tabLayout);
        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new MyFragmentAdapter(fragmentManager , getLifecycle());
        adapter.setUsername(bundle.getString("userID"));
        viewPager2 = findViewById(R.id.userview);
        viewPager2.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }
    // tabs
    //back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}