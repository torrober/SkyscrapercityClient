package com.torrober.skyscrapercityclient.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.torrober.skyscrapercityclient.Data.User;
import com.torrober.skyscrapercityclient.Models.AlertModel;
import com.torrober.skyscrapercityclient.R;
import com.torrober.skyscrapercityclient.TopicActivity;
import com.torrober.skyscrapercityclient.UserActivity;
import com.torrober.skyscrapercityclient.Utils.DateManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.Integer.parseInt;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.MyViewHolder> {
    private Context mContext;
    private LayoutInflater inflater;
    private List<AlertModel> alerts;
    private SharedPreferences preferences;

    public AlertAdapter(Context mContext, List<AlertModel> alerts) {
        this.mContext = mContext;
        this.alerts = alerts;
    }

    @NonNull
    @Override
    public AlertAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.alert_item, parent,false);
        return new AlertAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertAdapter.MyViewHolder holder, int position) {
        preferences = mContext.getSharedPreferences("userData", MODE_PRIVATE);
        String cookies = preferences.getString("cookie", "");
        String token = preferences.getString("_XFToken", "");
        if (alerts.get(position).isHasPFP()) {
            Picasso.get()
                    .load(alerts.get(position).getPFPLink().replace("/s/", "/l/"))
                    .into(holder.avatar, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            System.out.println(e);
                        }
                    });
        } else {
            User.drawUserImage(alerts.get(position).getUserStyling(),holder.avatar);
        }
        holder.message.setText(alerts.get(position).getMessage());
        holder.datetime.setText(DateManager.getForumFormat(alerts.get(position).getAlertTime()));
        holder.alertLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = alerts.get(position).getNotificationLink();
                Intent intent = new Intent(view.getContext(), TopicActivity.class);
                intent.putExtra("url", url);
                holder.itemView.getContext().startActivity(intent);
            }
        });
        holder.avatar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!alerts.get(position).getUsername().equals("0")) {
                final String[] res = new String[1];
                String notificationURL = "https://www.skyscrapercity.com/members/"+alerts.get(position).getUsername()+"?_xfResponseType=json&_xfToken="+token;
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
                                intent.putExtra("userID",alerts.get(position).getUsername());
                                intent.putExtra("userInfo", userInfo);
                                intent.putExtra("hasPFP", alerts.get(position).isHasPFP());
                                if(alerts.get(position).isHasPFP()) {
                                    intent.putExtra("pfpLink", alerts.get(position).getPFPLink().replace("/s/", "/h/"));
                                }  else {
                                    intent.putExtra("userStyling", alerts.get(position).getUserStyling());
                                }
                                holder.itemView.getContext().startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            res[0] = "Error";
                        }
                    }
                });
            }
        }
    });
}

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView message;
        private TextView datetime;
        private LinearLayout alertLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatarAlert);
            message = itemView.findViewById(R.id.messageAlert);
            datetime = itemView.findViewById(R.id.dateAlert);
            alertLayout = itemView.findViewById(R.id.alertContainer);
        }
    }
}
