package com.ai.zhihao.hw5;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by zhihaoai on 3/25/18.
 */

public class OfficialActivity extends AppCompatActivity {

    private static final String TAG = "OfficialActivity";
    private static final String defaultString = "No Data Provided";
    private Official official;
    private TextView addressBar;
    private ImageView imageView;
    HashMap<String, String> channels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        imageView = findViewById(R.id.ivPhoto);
        addressBar = findViewById(R.id.tvAddress_official);
        Intent intent = getIntent();
        if (intent.hasExtra("address")) {
            addressBar.setText(intent.getStringExtra("address"));
        }
        if (intent.hasExtra("official")) {
            official = (Official) intent.getSerializableExtra("official");
            ((TextView) findViewById(R.id.tvOffice_official)).setText(official.getOffice());
            ((TextView) findViewById(R.id.tvName_official)).setText(official.getName());
            ((TextView) findViewById(R.id.tvParty)).setText("(" + official.getParty() + ")");
            ((TextView) findViewById(R.id.tvAddress)).setText(official.getAddress());
            ((TextView) findViewById(R.id.tvPhone)).setText(official.getPhone());
            ((TextView) findViewById(R.id.tvEmail)).setText(official.getEmail());
            ((TextView) findViewById(R.id.tvUrl)).setText(official.getUrl());
            channels = official.getChannels();
            if (!channels.keySet().contains("Youtube"))
                findViewById(R.id.ivYoutube).setVisibility(View.INVISIBLE);
            if (!channels.keySet().contains("GooglePlus"))
                findViewById(R.id.ivGooglePlus).setVisibility(View.INVISIBLE);
            if (!channels.keySet().contains("Twitter"))
                findViewById(R.id.ivTwitter).setVisibility(View.INVISIBLE);
            if (!channels.keySet().contains("Facebook"))
                findViewById(R.id.ivFacebook).setVisibility(View.INVISIBLE);

            if (!official.getAddress().equals(defaultString)) {
                Pattern pattern = Pattern.compile(".*", Pattern.DOTALL);
                Linkify.addLinks(((TextView) findViewById(R.id.tvAddress)), pattern, "geo:0,0?q=");
            }
            Linkify.addLinks(((TextView) findViewById(R.id.tvPhone)), Linkify.PHONE_NUMBERS);
            Linkify.addLinks(((TextView) findViewById(R.id.tvEmail)), Linkify.EMAIL_ADDRESSES);
            Linkify.addLinks(((TextView) findViewById(R.id.tvUrl)), Linkify.WEB_URLS);

            if (official.getParty().equals("Republican")) {
                findViewById(R.id.svOfficial).setBackgroundColor(Color.RED);
            } else {
                if (official.getParty().equals("Democratic")) {
                    findViewById(R.id.svOfficial).setBackgroundColor(Color.BLUE);
                } else {
                    findViewById(R.id.svOfficial).setBackgroundColor(Color.BLACK);
                }
            }
            loadImage();
        }
    }

    public void openPhotoActivity(View view) {
        if (!official.getPhotoUrl().equals(defaultString)) {
            Intent photo = new Intent(this, PhotoActivity.class);
            photo.putExtra("address", addressBar.getText().toString());
            photo.putExtra("official", official);
            startActivity(photo);
        }
    }

    private void loadImage() {
        final String imageURL = official.getPhotoUrl();
        Log.d(TAG, "loadImage: " + imageURL);

        if (!imageURL.equals(defaultString)) {
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
        } else {
//            Picasso.with(this).load(imageURL)
//                    .error(R.drawable.brokenimage)
//                    .placeholder(R.drawable.missingimage)
//                    .into(imageView);
            Picasso.with(this).load(R.drawable.missingimage)
                    .into(imageView);
        }
    }

    public void youTubeClicked(View v) {
        String name = channels.get("Youtube");
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    public void googlePlusClicked(View v) {
        String name = channels.get("GooglePlus");
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus", "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://plus.google.com/" + name)));
        }
    }

    public void twitterClicked(View v) {
        Intent intent = null;
        String name = channels.get("Twitter");
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + channels.get("Facebook");
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + channels.get("Facebook");
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }
}
