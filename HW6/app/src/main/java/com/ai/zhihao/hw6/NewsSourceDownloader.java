package com.ai.zhihao.hw6;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhihaoai on 4/22/18.
 */

public class NewsSourceDownloader extends AsyncTask<String, Void, String> {

    private static final String TAG = "NewsSourceDownloader";
    private MainActivity ma;

    private final String queryURL = "https://newsapi.org/v1/sources?language=en&country=us";
    private int responseCode;

    private String category;
    private ArrayList<Source> sources = new ArrayList<>();
    private ArrayList<String> categories;

    public NewsSourceDownloader(MainActivity ma, String category) {
        this.ma = ma;
        if (category.equals("all")){
            this.category = "";
        } else {
            this.category = category;
        }
    }

    @Override
    protected void onPostExecute(String resultJSON) {
        Log.d(TAG, "onPostExecute: ");

        parseJSON(resultJSON);
        Set categories = new HashSet();
        for (Source s : sources){
            categories.add(s.getCategory());
        }
        this.categories = new ArrayList<>(categories);
        this.categories.add("all");
        Collections.sort(this.categories);
        Log.d(TAG, "onPostExecute: " + this.sources.get(0));
        Log.d(TAG, "onPostExecute: " + this.categories);
        ma.setSources(this.sources, this.categories);
    }

    @Override
    protected String doInBackground(String... keys) {
        Uri.Builder buildURL = Uri.parse(queryURL).buildUpon();
        buildURL.appendQueryParameter("category", category);
        buildURL.appendQueryParameter("apiKey", "8e83f68781474617b6d44bf2138c71e6");
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                Log.d(TAG, "doInBackground: Response code = " + conn.getResponseCode() + ", " + conn.getResponseMessage());
                return null;
            }

            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            is.close();
            conn.disconnect();
            return sb.toString();
        } catch (Exception e) {
            Log.d(TAG, "doInBackground: " + e.getMessage());
        }
        return null;
    }

    private void parseJSON(String jsonString) {
        Log.d(TAG, "parseJSON: ");
        try {
            JSONObject resultObject = new JSONObject(jsonString);

            JSONArray sources = resultObject.getJSONArray("sources");
            if (sources != null) {
                for (int i = 0; i < sources.length(); i++) {
                    JSONObject source = (JSONObject) sources.get(i);
                    String id = source.getString("id");
                    String name = source.getString("name");
                    String url = source.getString("url");
                    String category = source.getString("category");
                    this.sources.add(new Source(id, name, url, category));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "parseJSON: ", e);
        }
    }
}
