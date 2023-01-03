package com.torrober.skyscrapercityclient.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.torrober.skyscrapercityclient.Data.User;
import com.torrober.skyscrapercityclient.Models.PostModel;
import com.torrober.skyscrapercityclient.R;
import com.torrober.skyscrapercityclient.TopicActivity;
import com.torrober.skyscrapercityclient.UserActivity;
import com.torrober.skyscrapercityclient.Utils.DateManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;

import okhttp3.Call;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    private Context mContext;
    private LayoutInflater inflater;
    private List<PostModel> posts;

    public PostAdapter(Context mContext, List<PostModel> posts) {
        this.mContext = mContext;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.post_card, parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.MyViewHolder holder, int position) {
        SharedPreferences preferences = mContext.getSharedPreferences("userData", MODE_PRIVATE);
        String cookies = preferences.getString("cookie", "");
        String token = preferences.getString("_XFToken", "");
        if(posts.get(position).getUserPFPLink().equals("null")) {
            User.drawUserImage(posts.get(position).getUserStyling(),holder.avatar);
        } else {
                Picasso.get()
                        .load("https://www.skyscrapercity.com"+posts.get(position).getUserPFPLink())
                        .placeholder(R.drawable.defaut_icon_background)
                        .error(R.drawable.defaut_icon_background)
                        .into(holder.avatar);
        };
        holder.threadName.setText(posts.get(position).getTitle());
        holder.topicName.setText(posts.get(position).getForumTitle());
        holder.replier.setText(posts.get(position).getOpPoster()+"");
        holder.datePublished.setText(DateManager.getForumFormat(posts.get(position).getCreatedDate()));
        holder.lastReply.setText(posts.get(position).getLastPoster()+" replied "+DateManager.getForumFormat(posts.get(position).getLastReplyDate()));
        holder.thread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://skyscrapercity.com"+posts.get(position).getLink();
                Intent intent = new Intent(view.getContext(), TopicActivity.class);
                intent.putExtra("url", url);
                holder.itemView.getContext().startActivity(intent);
            }
        });
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] res = new String[1];
                String notificationURL = "https://www.skyscrapercity.com/members/"+posts.get(position).getOpPosterID()+"?_xfResponseType=json&_xfToken="+token;
                CookieManager cookieManager = new CookieManager();
                cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
                OkHttpClient client = new OkHttpClient.Builder()
                        .cookieJar(new JavaNetCookieJar(cookieManager))
                        .build();
                Request request = new Request.Builder()
                        .url(notificationURL)
                        .addHeader("cookie", cookies)
                        .build();
                client.newCall(request).enqueue(new okhttp3.Callback() {
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
                                Document doc = Jsoup.parse(html.getString("content"));
                                //div[qid="user-status-label"]
                                String userInfo = doc.select("div[qid=\"user-status-label\"]").get(0).wholeText();
                                String username = html.getString("title");
                                Intent intent = new Intent(view.getContext(), UserActivity.class);
                                intent.putExtra("username", username);
                                intent.putExtra("userID",posts.get(position).getOpPosterID());
                                intent.putExtra("userInfo", userInfo);
                                if(!posts.get(position).getUserPFPLink().equals("null")) {
                                    intent.putExtra("pfpLink", "https://www.skyscrapercity.com"+posts.get(position).getUserPFPLink().replace("/s/", "/h/"));
                                    intent.putExtra("hasPFP", true);
                                }  else {
                                    intent.putExtra("hasPFP", false);
                                    intent.putExtra("userStyling", posts.get(position).getUserStyling());
                                }
                                holder.itemView.getContext().startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                JSONObject data = new JSONObject(response.body().string());
                                JSONArray errors = data.getJSONArray("errors");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            System.out.println();
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView topicName;
        TextView threadName;
        TextView replier;
        TextView datePublished;
        TextView lastReply;
        TextView replies;
        LinearLayout thread;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            topicName = itemView.findViewById(R.id.topicName);
            threadName = itemView.findViewById(R.id.threadName);
            replier = itemView.findViewById(R.id.replier);
            datePublished = itemView.findViewById(R.id.datePublished);
            lastReply = itemView.findViewById(R.id.lastReply);
            thread = itemView.findViewById(R.id.thread);
        }
    }
}
