package com.ai.zhihao.hw4;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

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
        holder.tvPrice.setText(Double.toString(stock.getPrice()));
        holder.tvChange.setText(Double.toString(stock.getChange()));
        holder.tvCompanyName.setText(stock.getCompanyName());
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
