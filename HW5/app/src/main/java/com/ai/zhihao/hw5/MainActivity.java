package com.ai.zhihao.hw5;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
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
    private OfficialsAdapter notesAdapter;

    private LocationManager locationManager;
    private Location defaultLocation;
    private Location myLocation;

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

        defaultLocation = new Location("default");
        defaultLocation.setLatitude(41.8348731d);
        defaultLocation.setLongitude(-87.6291946d);

        if (checkPermission()) {
            findCurrentLocation();
            setLocation();
        }
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
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 0) {
                Log.d(TAG, "onRequestPermissionsResult: empty permissions array??");
                return;
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "Fine location permission granted");
                findCurrentLocation();
            } else {
                Toast.makeText(this, "Using default location", Toast.LENGTH_SHORT).show();
                myLocation = defaultLocation;
            }
            setLocation();
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

            Log.d(TAG, "checkPermission: ACCESS_FINE_LOCATION Permission requested, awaiting response.");
            return false;
        } else {
            Log.d(TAG, "checkPermission: Already have ACCESS_FINE_LOCATION Permission for this app.");
            return true;
        }
    }

    private void findCurrentLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        for (String providerName : locationManager.getAllProviders()) {
            Location loc = locationManager.getLastKnownLocation(providerName);
            if (loc != null) {
//                test
                long timeNow = System.currentTimeMillis();
                Log.d(TAG, "findCurrentLocation:\n" +
                        "Provider: " + providerName + "\n" +
                        "Accuracy: " + loc.getAccuracy() + "m\n" +
                        "Time: " + (timeNow - loc.getTime()) / 1000 + "sec\n" +
                        "Latitude: " + loc.getLatitude() + "\n" +
                        "Longitude: " + loc.getLongitude() + "\n");
                if (myLocation == null || loc.getAccuracy() < myLocation.getAccuracy()) {
                    myLocation = new Location(loc);
                }
            }
        }
        if (myLocation == null) {
            Log.d(TAG, "findCurrentLocation: using default location");
            Toast.makeText(this, "Using default location", Toast.LENGTH_SHORT).show();
            myLocation = defaultLocation;
        }
    }

    private void setLocation() {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
            Log.d(TAG, "setLocation: " + addresses.get(0).getAddressLine(1).toString());
            ((TextView) findViewById(R.id.tvLocation)).setText(addresses.get(0).getAddressLine(1).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
