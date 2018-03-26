package com.ai.zhihao.hw5;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener{

    private static final String TAG = "MainActivity";
    private int LOCATION_PERMISSION_REQUEST_CODE = 666;

    private List<Official> officialsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OfficialsAdapter officialsAdapter;

    private Locator locator;
    private String zipCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        recyclerView = findViewById(R.id.recycler);
        officialsAdapter = new OfficialsAdapter(officialsList, this);
        recyclerView.setAdapter(officialsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        locator = new Locator(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                Intent info = new Intent(this, InfoActivity.class);
                startActivity(info);
                return true;
            case R.id.location:
                final EditText et = new EditText(this);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                et.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                et.setGravity(Gravity.CENTER_HORIZONTAL);

                new AlertDialog.Builder(this)
                        .setTitle("Enter a City, State or Zip Code")
                        .setView(et)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
//                                 Toast.makeText(MainActivity.this, et.getText().toString(), Toast.LENGTH_SHORT).show();
                                new CivicInfoDownloader(MainActivity.this).execute(et.getText().toString());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onLongClick(View view) {
        Toast.makeText(this, "long clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: grant length: " + grantResults.length);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 0) {
                Log.d(TAG, "onRequestPermissionsResult: empty permissions array??");
                return;
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: Fine location permission granted");
                locator.setUpLocationManager();
                setLocation(locator.determineLocation());
            } else {
                Log.d(TAG, "onRequestPermissionsResult: Fine location permission NOT granted");
                Toast.makeText(this, "No location providers were available.", Toast.LENGTH_LONG).show();
                noLocationAvailable();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void setLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Log.d(TAG, "setLocation: Lat: " + latitude + ", Lon: " + longitude);
        String address = getAddress(latitude, longitude);
        if (address != null) {
//            ((TextView) findViewById(R.id.tvAddress)).setText(address);
            new CivicInfoDownloader(MainActivity.this).execute(zipCode);
        }
    }

    @Nullable
    private String getAddress(double latitude, double longitude) {
        Log.d(TAG, "getAddress: Lat: " + latitude + ", Lon: " + longitude);

        List<Address> addresses;
        for (int times = 0; times < 3; times++) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                Log.d(TAG, "getAddress: Getting address now");
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                Address address = addresses.get(0);
                String location = address.getLocality() + ", " + address.getAdminArea() + " " + address.getPostalCode();
                zipCode = address.getPostalCode();
                Log.d(TAG, "getAddress: " + location);
                return location;
            } catch (Exception e) {
                Log.d(TAG, "getAddress: " + e.getMessage());
            }
            Toast.makeText(this, "GeoCoder service is slow - please wait", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "The address cannot be acquired from provided latitude/longitude.", Toast.LENGTH_LONG).show();
        return null;
    }

    public void noLocationAvailable() {
        ((TextView) findViewById(R.id.tvAddress)).setText("No Data For Location");
    }

    public void generateOfficialsList(Object[] data) {
        if (data == null) {
            noLocationAvailable();
            officialsList.clear();
            officialsAdapter.notifyDataSetChanged();
            return;
        }
        String address = (String) data[0];
        ArrayList<Official> officials = (ArrayList<Official>) data[1];

        ((TextView) findViewById(R.id.tvAddress)).setText(address);
        officialsList.clear();
        for (Official official: officials){
            officialsList.add(official);
        }
        officialsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        locator.shutdown();
        super.onDestroy();
    }
}
