package com.torrober.skyscrapercityclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Picasso;
import com.torrober.skyscrapercityclient.R;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String stringImage = bundle.getString("url");
        TouchImageView imageView = this.findViewById(R.id.mainImage);
        Picasso.get()
                .load(stringImage)
                .into(imageView)
        ;
    }
    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Intent intentB = new Intent(this, TopicActivity.class);
        intent.putExtra("url", bundle.getString("backurl"));
        startActivity(intentB);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = getIntent();
                Bundle bundle = intent.getExtras();
                Intent intentB = new Intent(this, TopicActivity.class);
                intent.putExtra("url", bundle.getString("backurl"));
                startActivity(intentB);
                break;
        }
        return true;
    }
}