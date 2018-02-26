package com.ai.zhihao.hw4;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener, View.OnLongClickListener{

    private static final String TAG = "MainActivity";

    private List<Stock> stockList = new ArrayList<>();

    private SwipeRefreshLayout swiper;
    private RecyclerView recyclerView;

    private StocksAdapter stocksAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_main);

        stocksAdapter = new StocksAdapter(stockList, this);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setAdapter(stocksAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });
    }

    private void doRefresh() {
        Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                final EditText et = new EditText(this);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                et.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                et.setGravity(Gravity.CENTER_HORIZONTAL);

                builder.setTitle("Stock Selection")
                        .setMessage("Please enter a Stock Symbol:")
                        .setView(et)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(MainActivity.this, et.getText().toString(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Stock s = stockList.get(pos);

        Toast.makeText(view.getContext(), "SHORT " + s.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onLongClick(final View view) {
        final int pos = recyclerView.getChildLayoutPosition(view);
        final Stock s = stockList.get(pos);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete Stock")
                .setMessage("Delete Stock Symbol" + s.getSymbol() + "?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(view.getContext(), "Stock Symbol" + s.getSymbol() + "' deleted", Toast.LENGTH_SHORT).show();
                        stockList.remove(pos);
                        stocksAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }
}
