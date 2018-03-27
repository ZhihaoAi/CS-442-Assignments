package com.ai.zhihao.hw5;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zhihaoai on 3/25/18.
 */

public class CivicInfoDownloader extends AsyncTask<String, Void, String> {

    private static final String TAG = "CivicInfoDownloader";
    private MainActivity ma;

    private final String queryURL = "https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyD-KeTUcz7gGKHxUT6VAg1JmSePvPM__fg";
    private int responseCode;

    private static final String defaultString = "No Data Provided";
    private String location = defaultString;
    private ArrayList<Official> officialsData = new ArrayList<Official>();
    private Object[] data = new Object[2];

    public CivicInfoDownloader(MainActivity ma) {
        this.ma = ma;
    }

    @Override
    protected void onPostExecute(String resultJSON) {
        Log.d(TAG, "onPostExecute: ");

        // service unavailable
        if (resultJSON == null) {
            Log.d(TAG, "onPostExecute: Civic Info service is unavailable");
            Toast.makeText(ma, "The Civic Info service is unavailable", Toast.LENGTH_SHORT).show();
            ma.generateOfficialsList(null);
            return;
        }
        // no data is available for the specified location
        if (resultJSON.equals("")) {
            Log.d(TAG, "onPostExecute: no data is available for the specified location");
            Toast.makeText(ma, "No data is available for the specified location", Toast.LENGTH_SHORT).show();
            ma.generateOfficialsList(null);
            return;
        }

        parseJSON(resultJSON);
        data[0] = location;
        data[1] = officialsData;
        ma.generateOfficialsList(data);
    }

    @Override
    protected String doInBackground(String... keys) {
        Uri.Builder buildURL = Uri.parse(queryURL).buildUpon();
        buildURL.appendQueryParameter("address", keys[0]);
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

            JSONObject normalizedInput = resultObject.getJSONObject("normalizedInput");
            String city = normalizedInput.getString("city");
            String state = normalizedInput.getString("state");
            String zip = normalizedInput.getString("zip");
            location = city + ", " + state + " " + zip;
            Log.d(TAG, "parseJSON: " + location);

            JSONArray offices = resultObject.getJSONArray("offices");
            HashMap<String, String> indexToOfficeName = new HashMap<>();
            for (int i = 0; i < offices.length(); i++) {
                JSONObject office = (JSONObject) offices.get(i);
                String name = office.getString("name");
                JSONArray indices = office.getJSONArray("officialIndices");

                for (int j = 0; j < indices.length(); j++) {
                    indexToOfficeName.put(indices.getString(j), name);
                }
            }

            JSONArray officials = resultObject.getJSONArray("officials");
            for (int i = 0; i < officials.length(); i++) {

                JSONObject official = (JSONObject) officials.get(i);

                String office = indexToOfficeName.get(String.format("%d", i));

                String name = official.getString("name");

                String address;
                if (official.has("address")) {
                    JSONObject addressObject = (JSONObject) official.getJSONArray("address")
                            .get(0);
                    StringBuilder sb = new StringBuilder();
                    if (addressObject.has("line1"))
                        sb.append(addressObject.getString("line1"));
                    if (addressObject.has("line2"))
                        sb.append(", " + addressObject.getString("line2"));
                    if (addressObject.has("line3"))
                        sb.append(", " + addressObject.getString("line3"));
                    sb.append("\n" + addressObject.get("city") + ", " + addressObject.get("state") + " " + addressObject.get("zip"));
                    address = sb.toString();
                } else {
                    address = defaultString;
                }

                String party;
                if (official.has("party"))
                    party = official.getString("party");
                else
                    party = "Unknown";

                String phone;
                if (official.has("phones")) {
                    phone = official.getJSONArray("phones")
                            .get(0).toString();
                } else {
                    phone = defaultString;
                }

                String url;
                if (official.has("urls")) {
                    url = official.getJSONArray("urls")
                            .get(0).toString();
                } else {
                    url = defaultString;
                }

                String email;
                if (official.has("emails")) {
                    email = official.getJSONArray("emails")
                            .get(0).toString();
                } else {
                    email = defaultString;
                }

                String photoUrl;
                if (official.has("photoUrl")) {
                    photoUrl = official.getString("photoUrl");
                } else {
                    photoUrl = defaultString;
                }

                HashMap<String, String> channels = new HashMap<>();
                if (official.has("channels")) {
                    JSONArray channelsArray = official.getJSONArray("channels");
                    for (int j = 0; j < channelsArray.length(); j++) {
                        JSONObject channel = (JSONObject) channelsArray.get(j);
                        channels.put(channel.getString("type"), channel.getString("id"));
                    }
                }

                Log.d(TAG, "parseJSON: \n"
                        + "office: " + office + "\n"
                        + "name: " + name + "\n"
                        + "address: " + address + "\n"
                        + "party: " + party + "\n"
                        + "url: " + url + "\n"
                        + "email: " + email + "\n"
                        + "photoUrl: " + photoUrl
                );

                officialsData.add(new Official(office, name, address, party, phone, url, email, photoUrl, channels));
            }
        } catch (JSONException e) {
            Log.e(TAG, "parseJSON: ", e);
        }
    }
}
