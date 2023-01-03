package com.torrober.skyscrapercityclient.Adapter;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.torrober.skyscrapercityclient.DiscussionsFragment;
import com.torrober.skyscrapercityclient.LatestActivityFragment;

public class MyFragmentAdapter extends FragmentStateAdapter {


    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public MyFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if (position == 1){
            DiscussionsFragment disc = new DiscussionsFragment(username);
            Bundle bundle = new Bundle();
            bundle.putString("userName", username);
            disc.setArguments(bundle);
            return disc;
        } else {
            LatestActivityFragment last = new LatestActivityFragment(username);
            Bundle bundle = new Bundle();
            bundle.putString("userName", username);
            last.setArguments(bundle);
            return last;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
