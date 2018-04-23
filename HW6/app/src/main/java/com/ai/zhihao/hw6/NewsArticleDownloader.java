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

/**
 * Created by zhihaoai on 4/23/18.
 */

public class NewsArticleDownloader extends AsyncTask<String, Void, String> {

    private static final String TAG = "NewsArticleDownloader";
    private NewsService ns;

    private final String queryURL = "https://newsapi.org/v1/articles?";
    private int responseCode;

    private String source;
    private ArrayList<Article> articles = new ArrayList<>();

    public NewsArticleDownloader(NewsService ns, String source) {
        this.ns = ns;
        this.source = source;
    }

    @Override
    protected void onPostExecute(String resultJSON) {
        Log.d(TAG, "onPostExecute: ");

        parseJSON(resultJSON);
        Log.d(TAG, "onPostExecute: " + articles);
        ns.setArticles(this.articles);
    }

    @Override
    protected String doInBackground(String... keys) {
        Uri.Builder buildURL = Uri.parse(queryURL).buildUpon();
        buildURL.appendQueryParameter("source", source);
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

            JSONArray articles = resultObject.getJSONArray("articles");
            if (articles != null) {
                for (int i = 0; i < articles.length(); i++) {
                    JSONObject source = (JSONObject) articles.get(i);
                    String title = source.getString("title");
                    String author = source.getString("author");
                    String description = source.getString("description");
                    String urlToImage = source.getString("urlToImage");
                    String time = source.getString("publishedAt");
                    String url = source.getString("url");
                    this.articles.add(new Article(title, author, description, urlToImage, time, url));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "parseJSON: ", e);
        }
    }
}
