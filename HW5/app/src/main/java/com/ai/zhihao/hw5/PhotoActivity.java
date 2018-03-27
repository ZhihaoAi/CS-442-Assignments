package com.ai.zhihao.hw5;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by zhihaoai on 3/26/18.
 */

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = "PhotoActivity";

    private Official official;
    private TextView addressBar;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        imageView = findViewById(R.id.ivFullPhoto);
        addressBar = findViewById(R.id.tvAddress_photo);
        Intent intent = getIntent();
        if (intent.hasExtra("address")) {
            addressBar.setText(intent.getStringExtra("address"));
        }
        if (intent.hasExtra("official")) {
            official = (Official) intent.getSerializableExtra("official");
            ((TextView) findViewById(R.id.tvOffice_photo)).setText(official.getOffice());
            ((TextView) findViewById(R.id.tvName_photo)).setText(official.getName());

            if (official.getParty().equals("Republican")) {
                findViewById(R.id.clPhoto).setBackgroundColor(Color.RED);
            } else {
                if (official.getParty().equals("Democratic")) {
                    findViewById(R.id.clPhoto).setBackgroundColor(Color.BLUE);
                } else {
                    findViewById(R.id.clPhoto).setBackgroundColor(Color.BLACK);
                }
            }
            loadImage();
        }
    }

    private void loadImage() {
        final String imageURL = official.getPhotoUrl();
        Log.d(TAG, "loadImage: " + imageURL);

        Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) { // Here we try https if the http image attempt failed
                final String changedUrl = imageURL.replace("http:", "https:");
                picasso.load(changedUrl)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(imageView);
            }
        }).build();
        picasso.load(imageURL)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(imageView);
    }
}
