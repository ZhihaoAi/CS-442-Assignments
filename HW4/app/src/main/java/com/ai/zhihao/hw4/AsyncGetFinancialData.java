package com.ai.zhihao.hw4;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zhihaoai on 3/2/18.
 */

public class AsyncGetFinancialData extends AsyncTask<String, Void, Void> {

    private static final String TAG = "AsyncGetPrice";
    private MainActivity ma;
    private String symbol;
    private String companyName;
    private double price;
    private double change;
    private double percent;
    private String financialJSON;

    private final String financialURL = "https://api.iextrading.com/1.0/stock/";

    public AsyncGetFinancialData(MainActivity ma) {
        this.ma = ma;
    }

    @Override
    protected Void doInBackground(String... keys) {
        symbol = keys[0];
        companyName = keys[1];
        String urlToUse = financialURL + symbol + "/quote";
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        financialJSON = sb.toString();
        return null;
    }

    @Override
    protected void onPostExecute(Void s) {
        super.onPostExecute(s);

        parseJSON(financialJSON);

        // stub - check if get a response, assuming yes

        Stock stock = new Stock(symbol, companyName, price, change, percent);

        ma.addNewStock(stock);
    }

    private void parseJSON(String jsonString) {
        Log.d(TAG, "parseJSON: ");
        try {
            JSONObject results = new JSONObject(jsonString);

            String symbol = results.getString("symbol");
            if (!symbol.equals(symbol)){
                // stub - do something if symbol doesn't match
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
