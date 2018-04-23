package com.ai.zhihao.hw6;

import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class NewsFragment extends Fragment {

    private static final String TAG = "NewsFragment";
    public static final String TITLE = "TITLE";
    public static final String AUTHOR = "AUTHOR";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String URLTOIMAGE = "URLTOIMAGE";
    public static final String TIME = "TIME";
    public static final String COUNT = "COUNT";

    public static final NewsFragment newInstance(String title, String author, String description, String urlToImage, String time, String count)
    {
        NewsFragment f = new NewsFragment();
        Bundle bdl = new Bundle(6);
        bdl.putString(TITLE, title);
        bdl.putString(AUTHOR, author);
        bdl.putString(DESCRIPTION, description);
        bdl.putString(URLTOIMAGE, urlToImage);
        bdl.putString(TIME, time);
        bdl.putString(COUNT, count);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String title = getArguments().getString(TITLE);
        String author = getArguments().getString(AUTHOR);
        String description = getArguments().getString(DESCRIPTION);
        String urlToImage = getArguments().getString(URLTOIMAGE);
        String time = getArguments().getString(TIME);
        String count = getArguments().getString(COUNT);

        View v = inflater.inflate(R.layout.fragment_news, container, false);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(title);

        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = null;
        try {
            date = parser.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate;
        if (date != null) {
            SimpleDateFormat formatter= new SimpleDateFormat("MMM dd, yyyy HH:mm");
            formattedDate = formatter.format(date);
        } else {
            formattedDate = time;
        }
        ((TextView) v.findViewById(R.id.tvTime)).setText(formattedDate);

        ((TextView) v.findViewById(R.id.tvAuthor)).setText(author);
        ((TextView) v.findViewById(R.id.tvDescription)).setText(description);
        ((TextView) v.findViewById(R.id.tvCount)).setText(count);

        final ImageView imageView = v.findViewById(R.id.imageView);
        Picasso picasso = new Picasso.Builder(this.getContext())
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Log.d(TAG, "onImageLoadFailed: ");
                        picasso.load(R.drawable.brokenimage).into(imageView);
                    }
                })
                .build();

        picasso.load(urlToImage)
                .error(R.drawable.brokenimage)
                .into(imageView);
        return v;
    }

}