package com.ai.zhihao.hw4;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Locale;

/**
 * Created by zhihaoai on 2/25/18.
 */

public class StocksAdapter extends RecyclerView.Adapter<StockViewHolder> {

    private static final String TAG = "StocksAdapter";
    private List<Stock> stockList;
    private MainActivity mainAct;

    public StocksAdapter(List<Stock> stockList, MainActivity mainAct) {
        this.stockList = stockList;
        this.mainAct = mainAct;
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_list_row, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        holder.tvSymbol.setText(stock.getSymbol());
        holder.tvCompanyName.setText(stock.getCompanyName());
        holder.tvPrice.setText(String.format(Locale.US,"%.2f", stock.getPrice()));
        if (stock.getChange() > 0){
            holder.tvChange.setText(String.format(Locale.US,"\u25b2 %.2f (%.2f%%)",
                    stock.getChange(), stock.getPercent()*100));
            holder.tvSymbol.setTextColor(Color.GREEN);
            holder.tvPrice.setTextColor(Color.GREEN);
            holder.tvChange.setTextColor(Color.GREEN);
            holder.tvCompanyName.setTextColor(Color.GREEN);
        } else {
            holder.tvChange.setText(String.format(Locale.US,"\u25bc %.2f (%.2f%%)",
                    stock.getChange(), stock.getPercent()*100));
            holder.tvSymbol.setTextColor(Color.RED);
            holder.tvPrice.setTextColor(Color.RED);
            holder.tvChange.setTextColor(Color.RED);
            holder.tvCompanyName.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
