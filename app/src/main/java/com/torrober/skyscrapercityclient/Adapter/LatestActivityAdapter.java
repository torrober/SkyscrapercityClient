package com.torrober.skyscrapercityclient.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.torrober.skyscrapercityclient.Models.AlertModel;
import com.torrober.skyscrapercityclient.Models.LatestActivityModel;
import com.torrober.skyscrapercityclient.R;
import com.torrober.skyscrapercityclient.TopicActivity;

import java.util.List;

public class LatestActivityAdapter extends  RecyclerView.Adapter<LatestActivityAdapter.MyViewHolder> {
    private Context mContext;
    private LayoutInflater inflater;
    private List<LatestActivityModel> activity;

    public LatestActivityAdapter(Context mContext, List<LatestActivityModel> activity) {
        this.mContext = mContext;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.latest_activity, parent,false);
        return new LatestActivityAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.message.setText(activity.get(position).getMessage());
        holder.notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://skyscrapercity.com"+activity.get(position).getLink();
                Intent intent = new Intent(view.getContext(), TopicActivity.class);
                intent.putExtra("url", url);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return activity.size();
    }
    //LatestActivityItem

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        LinearLayout notification;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            notification = itemView.findViewById(R.id.LatestActivityItem);
            message = itemView.findViewById(R.id.LAText);
        }
    }
}
