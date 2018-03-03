package com.ai.zhihao.hw4;

import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zhihaoai on 3/2/18.
 */

public class AsyncGetFinancialData extends AsyncTask<String, Void, String> {

    private static final String TAG = "AsyncGetPrice";
    private MainActivity ma;
    private String symbol;
    private String companyName;
    private double price;
    private double change;
    private double percent;
    private String financialJSON;

    private final String financialURL = "https://api.iextrading.com/1.0/stock/";
    private int responseCode;

    public AsyncGetFinancialData(MainActivity ma) {
        this.ma = ma;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: ");

        // Nothing found based on the symbol
        if (responseCode != 200) {
            new AlertDialog.Builder(ma)
                    .setTitle("Symbol Not Found: " + symbol)
                    .setMessage("Cannot find symbol on iextrading.com.")
                    .create()
                    .show();
            return;
        }

        parseJSON(financialJSON);
        // Symbols doesn't match. Server problem.
        if (price == -1) {
            new AlertDialog.Builder(ma)
                    .setTitle("Symbol Not Found: " + symbol)
                    .setMessage("iextrading.com is not returning data for the symbol requested.")
                    .create()
                    .show();
            return;
        }

        Stock stock = new Stock(symbol, companyName, price, change, percent);
        Log.d(TAG, "onPostExecute: stock = " + stock);

        ma.addNewStock(stock);
    }

    @Override
    protected String doInBackground(String... keys) {
        symbol = keys[0];
        companyName = keys[1];
        String urlToUse = financialURL + symbol + "/quote";
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Log.d(TAG, "doInBackground: Response code = " + conn.getResponseCode() + ", " + conn.getResponseMessage());
            responseCode = conn.getResponseCode();
            if (responseCode != 200)
                return symbol;

            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            financialJSON = sb.toString();
            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
        }

        return symbol;
    }

    private void parseJSON(String jsonString) {
        Log.d(TAG, "parseJSON: ");
        try {
            JSONObject results = new JSONObject(jsonString);

            if (!symbol.equals(results.getString("symbol"))){
                price = -1;
                Log.d(TAG, "parseJSON: symbol doesn't match");
                return;
            }

            price = results.getDouble("latestPrice");
            change = results.getDouble("change");
            percent = results.getDouble("changePercent");

        } catch (Exception e) {
            Log.e(TAG, "parseJSON: ", e);
        }

    }
}
