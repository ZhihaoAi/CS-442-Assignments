package com.ai.zhihao.hw5;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener{

    private static final String TAG = "MainActivity";
    private int LOCATION_PERMISSION_REQUEST_CODE = 666;

    private List<Official> officialsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OfficialsAdapter notesAdapter;

    private Locator locator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        recyclerView = findViewById(R.id.recycler);
        notesAdapter = new OfficialsAdapter(officialsList, this);
        recyclerView.setAdapter(notesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        test
        for (int i=0;i<10;i++){
            officialsList.add(new Official("US","ABC","C"));
        }
        notesAdapter.notifyDataSetChanged();

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
                                 Toast.makeText(MainActivity.this, et.getText().toString(), Toast.LENGTH_SHORT).show();
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 0) {
                Log.d(TAG, "onRequestPermissionsResult: empty permissions array??");
                return;
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "Fine location permission granted");
                locator.setUpLocationManager();
                locator.determineLocation();
            } else {
                noLocationAvailable();
            }
        }
    }

    public void setLocation(double latitude, double longitude) {
        Log.d(TAG, "setLocation: Lat: " + latitude + ", Lon: " + longitude);
        String address = getAddress(latitude, longitude);
        ((TextView) findViewById(R.id.tvAddress)).setText(address);
    }

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
                Log.d(TAG, "getAddress: " + location);
                return location;

            } catch (Exception e) {
                Log.d(TAG, "getAddress: " + e.getMessage());
            }
            Toast.makeText(this, "GeoCoder service is slow - please wait", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "GeoCoder service timed out - please try again", Toast.LENGTH_LONG).show();
        return null;
    }

    public void noLocationAvailable() {
        Toast.makeText(this, "No location providers were available.\nUsing default location.", Toast.LENGTH_LONG).show();
//        location of IIT
        setLocation(41.8348731d, -87.6291946d);
    }

    @Override
    protected void onDestroy() {
        locator.shutdown();
        super.onDestroy();
    }
}
