package com.ai.zhihao.hw3;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView rowTitle;
    public TextView rowTime;
    public TextView rowContent;

    public MyViewHolder(View view) {
        super(view);
        rowTitle = (TextView) view.findViewById(R.id.title);
        rowTime = (TextView) view.findViewById(R.id.time);
        rowContent = (TextView) view.findViewById(R.id.content);
    }

}
