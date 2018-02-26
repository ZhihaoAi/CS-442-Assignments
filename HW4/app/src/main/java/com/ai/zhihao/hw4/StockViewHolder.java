package com.ai.zhihao.hw4;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by zhihaoai on 2/25/18.
 */

public class StockViewHolder extends RecyclerView.ViewHolder {

    public TextView tvSymbol;
    public TextView tvPrice;
    public TextView tvChange;
    public TextView tvCompanyName;

    public StockViewHolder(View view) {
        super(view);
        tvSymbol = view.findViewById(R.id.rowSymbol);
        tvPrice = view.findViewById(R.id.rowPrice);
        tvChange = view.findViewById(R.id.rowChange);
        tvCompanyName = view.findViewById(R.id.rowCompanyName);
    }

}
