package com.ai.zhihao.hw4;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by zhihaoai on 3/1/18.
 */

public class AsyncSearch extends AsyncTask<String, Void, String> {

    private static final String TAG = "AsyncSearch";
    private MainActivity ma;
    private HashMap<String, String> stockData = new HashMap<>();
    private String stockJSON;

    private final String stockURL = "http://d.yimg.com/aq/autoc?region=US&lang=en-US";

    public AsyncSearch(MainActivity ma) {
        this.ma = ma;
    }

    @Override
    protected String doInBackground(String... keys) {
        Uri.Builder buildURL = Uri.parse(stockURL).buildUpon();
        buildURL.appendQueryParameter("query", keys[0]);
        String urlToUse = buildURL.build().toString();
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

        stockJSON = sb.toString();

        return keys[0];
    }

    @Override
    protected void onPostExecute(String user_input) {
        Log.d(TAG, "onPostExecute: ");

        parseJSON(stockJSON);

        if (stockData.isEmpty()) {
            new AlertDialog.Builder(ma)
                    .setTitle("Symbol Not Found: " + user_input)
                    .setMessage("Please check your spelling.")
                    .create()
                    .show();
            return;
        }

        if (stockData.size() == 1) {
            String symbol = stockData.keySet().toArray()[0].toString();
            ma.getSearchResult(symbol, stockData.get(symbol));
            return;
        }

        final String[] options = new String[stockData.size()];
        int i = 0;
        for (String s : stockData.keySet())
            options[i++] = String.format("%s - %s", s, stockData.get(s));

        new AlertDialog.Builder(ma)
                .setTitle("Make a selection")
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String symbol = options[which].substring(0, options[which].indexOf(" - "));
                        ma.getSearchResult(symbol, stockData.get(symbol));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();

    }

    private void parseJSON(String jsonString) {
        Log.d(TAG, "parseJSON: ");
        try {
            JSONArray results = new JSONObject(jsonString)
                    .getJSONObject("ResultSet")
                    .getJSONArray("Result");

            for (int i = 0; i < results.length(); i++) {
                JSONObject stockJSON = (JSONObject) results.get(i);
                if (!stockJSON.getString("type").equals("S") || stockJSON.getString("symbol").contains("."))
                    continue;
                stockData.put(stockJSON.getString("symbol"), stockJSON.getString("name"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "parseJSON: ", e);
        }

//        log the valid stocks
        for (String stock : stockData.keySet()) {
            Log.d(TAG, "parseJSON: " + stock + ": " + stockData.get(stock));
        }

    }
}
