package com.ai.zhihao.hw6;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class NewsFragment extends Fragment {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    public static final NewsFragment newInstance(String message)
    {
        NewsFragment f = new NewsFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, message);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String message = getArguments().getString(EXTRA_MESSAGE);
        View v = inflater.inflate(R.layout.fragment_news, container, false);
        TextView messageTextView = (TextView)v.findViewById(R.id.textView);
        messageTextView.setText(message);

        return v;
    }

}