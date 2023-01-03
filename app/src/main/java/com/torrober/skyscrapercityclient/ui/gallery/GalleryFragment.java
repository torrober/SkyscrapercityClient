package com.torrober.skyscrapercityclient.ui.gallery;

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

import com.torrober.skyscrapercityclient.Adapter.AlertAdapter;
import com.torrober.skyscrapercityclient.Adapter.PostAdapter;
import com.torrober.skyscrapercityclient.Models.AlertModel;
import com.torrober.skyscrapercityclient.Models.PostModel;
import com.torrober.skyscrapercityclient.R;
import com.torrober.skyscrapercityclient.Utils.DateManager;

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
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.Integer.parseInt;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    List<AlertModel> alerts  = new ArrayList<>();
    AlertAdapter alertAdapter;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        SharedPreferences preferences;
        recyclerView = root.findViewById(R.id.alertRecycler);
        preferences = getActivity().getSharedPreferences("userData", MODE_PRIVATE);
        String cookies = preferences.getString("cookie", "");
        String token = preferences.getString("_xFToken", "");
        final String[] res = new String[1];
        String notificationURL = "https://www.skyscrapercity.com/account/alerts?_xfResponseType=json&_xfToken="+token;
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
                        Elements links = doc.getElementsByTag("ol");
                        Elements notifications = links.select("li");
                        for(int i = 0; i < notifications.size(); i++){
                            Element alert = notifications.get(i).select(".contentRow-main--close").get(0);
                            Element pfp =  notifications.get(i).select(".contentRow-figure").get(0);
                            int unixTime = parseInt(alert.select("time").attr("california-data-time"));
                            String username  = pfp.select(".avatar").attr("data-user-id");
                            String url;
                            if( alert.getElementsByTag("a").size() > 1) {
                                url = alert.getElementsByTag("a").get(1).attr("href");
                            } else {
                                url = "";
                            }
                            alert.select("time").remove();
                            String message = alert.wholeText().trim();
                            System.out.println(pfp.select("avatar"));
                            if (pfp.select(".avatar").attr("class").contains("avatar--default--dynamic")) {
                                String styles = pfp.select(".avatar-background").attr("style");
                                styles = styles.replace("background-color:", "").replace("color:", "");
                                String innerContent =  pfp.select(".avatar-background").get(0).wholeText();
                                innerContent = innerContent.trim();
                                String PFPStyles[] = {styles.split(";")[0].trim(), styles.split(";")[1].trim(), innerContent};
                                AlertModel nopfpAlert = new AlertModel(username,message,url,unixTime, false);
                                nopfpAlert.setUserStyling(PFPStyles);
                                alerts.add(nopfpAlert);
                            } else {
                                AlertModel pfpAlert = new AlertModel(username,message,url,unixTime, true);
                                pfpAlert.setPFPLink(pfp.select(".avatar").select("img").attr("src"));
                                alerts.add(pfpAlert);
                            }
                        }
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                    alertAdapter = new AlertAdapter(getContext(), alerts);
                                    recyclerView.setAdapter(alertAdapter);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    res[0] = "Error";
                    System.out.println(response.body().string());
                }
            }
        });
        return root;
    }
}