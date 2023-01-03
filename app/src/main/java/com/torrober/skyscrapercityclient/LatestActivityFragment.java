package com.torrober.skyscrapercityclient;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.torrober.skyscrapercityclient.Adapter.AlertAdapter;
import com.torrober.skyscrapercityclient.Adapter.LatestActivityAdapter;
import com.torrober.skyscrapercityclient.Models.AlertModel;
import com.torrober.skyscrapercityclient.Models.LatestActivityModel;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LatestActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LatestActivityFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    List<AlertModel> alerts  = new ArrayList<>();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String username;
    private RecyclerView recyclerView;

    public LatestActivityFragment(String username) {
        this.username = username;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LatestActivityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LatestActivityFragment newInstance(String param1, String param2) {
        LatestActivityFragment fragment = new LatestActivityFragment("");
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        List<LatestActivityModel> activity = new ArrayList<>();
        View root = inflater.inflate(R.layout.fragment_latest_activity, container, false);
        TextView contentView = root.findViewById(R.id.noActitivity);
        contentView.setVisibility(View.GONE);
        SharedPreferences preferences;
        preferences = getActivity().getSharedPreferences("userData", MODE_PRIVATE);
        //System.out.println(username);
        String cookies = preferences.getString("cookie", "");
        String token = preferences.getString("_xFToken", "");
        recyclerView = root.findViewById(R.id.laRecycler);
        final String[] res = new String[1];
        String notificationURL = "https://www.skyscrapercity.com/members/"+username+"/latest-activity?&_xfWithData=1&_xfResponseType=json&_xfToken="+token;
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
                        Elements links = doc.getElementsByTag("ul");
                        Elements notifications = links.select("li");
                        if(notifications.size() > 0 ){
                            for(int i = 0; i < notifications.size(); i++){
                                if(notifications.get(i).select(".contentRow-title").size() > 0){
                                    Element alert = notifications.get(i).select(".contentRow-title").get(0);
                                    String link = notifications.get(i).getElementsByTag("a").get(2).attr("href");
                                    //alert.select("time").remove();
                                    String message = alert.wholeText().trim();
                                    activity.add(new LatestActivityModel(message, link));
                                }
                            }
                        }
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(activity.size() > 0) {
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                        LatestActivityAdapter laAdapter = new LatestActivityAdapter(getContext(), activity);
                                        recyclerView.setAdapter(laAdapter);
                                    } else {
                                        String noactivity = doc.select(".block-row").get(0).wholeText().trim();
                                        contentView.setText(noactivity);
                                        contentView.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                    }
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