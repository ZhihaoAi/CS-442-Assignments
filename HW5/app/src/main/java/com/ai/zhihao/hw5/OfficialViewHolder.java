package com.ai.zhihao.hw5;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by zhihaoai on 3/24/18.
 */

public class OfficialViewHolder extends RecyclerView.ViewHolder {

    public TextView office;
    public TextView name;

    public OfficialViewHolder(View itemView) {
        super(itemView);
        office = itemView.findViewById(R.id.rowOffice);
        name = itemView.findViewById(R.id.rowName);
    }
}
