package com.torrober.skyscrapercityclient.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.torrober.skyscrapercityclient.Adapter.PostAdapter;
import com.torrober.skyscrapercityclient.Models.PostModel;
import com.torrober.skyscrapercityclient.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    List<PostModel> posts  = new ArrayList<>();;
    RecyclerView recyclerView;
     PostAdapter postadapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        SharedPreferences preferences;
        preferences = getActivity().getSharedPreferences("userData", MODE_PRIVATE);
        String cookies = preferences.getString("cookie", "");
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
                    try {
                        JSONObject data = new JSONObject(res[0]);
                        JSONObject msg = data.getJSONObject("message");
                        JSONArray threads = msg.getJSONArray("threads");
                        for(int i = 0;  i <  threads.length(); i++) {
                            JSONObject post = threads.getJSONObject(i);
                            String threadName = post.getString("title");
                            int threadId = post.getInt("id");
                            String link = post.getString("link");
                            int replies = post.getInt("reply_count");
                            int createdDate = post.getInt("post_date");
                            String topicName = post.getJSONObject("forum").getString("title");
                            JSONObject OpUser = post.getJSONObject("user");
                            JSONObject lastPost = post.getJSONObject("last_post");
                            int lastReplyDate = lastPost.getInt("post_date");
                            String lastPoster = post.getJSONObject("last_poster").getString("username");
                            String opPoster = OpUser.getString("username");
                            String opPosterID = OpUser.getString("user_id");
                            String userPFPLink = OpUser.getString("avatar_url");
                            String[] userStyling = {
                                    OpUser.getJSONObject("avatar_styling").getString("bgColor"),
                                    OpUser.getJSONObject("avatar_styling").getString("color"),
                                    OpUser.getJSONObject("avatar_styling").getString("innerContent")
                            };
                            posts.add(new PostModel(threadId,threadName,link,topicName,opPoster,opPosterID,lastPoster,createdDate,lastReplyDate,replies,userPFPLink,userStyling));
                        }
                        if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                postadapter = new PostAdapter(getContext(), posts);
                                recyclerView.setAdapter(postadapter);
                            }
                        });}
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    res[0] = "Error";
                }
            }
        });
        return root;
    }

}