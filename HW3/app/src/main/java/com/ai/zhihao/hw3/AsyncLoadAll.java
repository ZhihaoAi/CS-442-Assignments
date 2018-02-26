package com.ai.zhihao.hw3;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class AsyncLoadAll extends AsyncTask<String, Integer, String>{

    private static final String TAG = "AsyncLoadAll";
    private MainActivity ma;
    private String fileName;
    private String encoding;
    StringBuilder jsonString;

    public AsyncLoadAll(MainActivity ma) {
        this.ma = ma;
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: ");
        try {
            if (strings[0] != null)
                fileName = strings[0];
            if (strings[1] != null)
                encoding = strings[1];

            InputStream is = ma.getApplicationContext().openFileInput(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));

            jsonString = new StringBuilder();
            String tmp;
            while ((tmp = br.readLine()) != null){
                jsonString.append(tmp);
            }
            br.close();
            Log.d(TAG, "doInBackground: " + jsonString);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "doInBackground: no file found");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonString.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: ");
        super.onPostExecute(s);

        JSONArray array = null;
        try {
            array = new JSONArray(s);
        } catch (Exception e) {
            Log.d(TAG, "onPostExecute: cannot parse to JSON array");
        }

        ma.whenAsyncIsDone(array);
    }
}
