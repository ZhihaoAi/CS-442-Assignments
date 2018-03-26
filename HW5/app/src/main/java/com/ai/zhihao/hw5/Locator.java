package com.ai.zhihao.hw5;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by zhihaoai on 3/24/18.
 */

public class Locator {

    private static final String TAG = "Locator";
    private int LOCATION_PERMISSION_REQUEST_CODE = 666;

    private MainActivity ma;
    private LocationManager locationManager;
    private LocationListener locationListener;
//    private Location myLocation;

    public Locator(MainActivity ma) {
        this.ma = ma;
        if(checkPermission()) {
            setUpLocationManager();
            determineLocation();
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(ma,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ma,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

            Log.d(TAG, "checkPermission: ACCESS_FINE_LOCATION Permission requested, awaiting response.");
            return false;
        } else {
            Log.d(TAG, "checkPermission: Already have ACCESS_FINE_LOCATION Permission for this app.");
            return true;
        }
    }

    public void setUpLocationManager() {

        if (locationManager != null || !checkPermission())
            return;

        locationManager = (LocationManager) ma.getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
//                Toast.makeText(ma, "Update from " + location.getProvider(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onLocationChanged: " + location.getProvider());
                ma.setLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Nothing to do here
            }

            public void onProviderEnabled(String provider) {
                // Nothing to do here
            }

            public void onProviderDisabled(String provider) {
                // Nothing to do here
            }
        };

        // Register the listener with the Location Manager to receive GPS location updates
        // Refreshing rate: 1 minute, 1000 meters
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 60000, 1000, locationListener);
    }

    public Location determineLocation() {

        if (!checkPermission())
            return null;

        if (locationManager == null)
            setUpLocationManager();

//        for (String providerName : locationManager.getAllProviders()) {
//            Location loc = locationManager.getLastKnownLocation(providerName);
//            if (loc != null) {
////                log the locations found via network, gps and passive
////                long timeNow = System.currentTimeMillis();
////                Log.d(TAG, "findCurrentLocation:\n" +
////                        "Provider: " + providerName + "\n" +
////                        "Accuracy: " + loc.getAccuracy() + "m\n" +
////                        "Time: " + (timeNow - loc.getTime()) / 1000 + "sec\n" +
////                        "Latitude: " + loc.getLatitude() + "\n" +
////                        "Longitude: " + loc.getLongitude() + "\n");
////
//                if (myLocation == null || loc.getAccuracy() < myLocation.getAccuracy()) {
//                    myLocation = new Location(loc);
//                }
//            }
//        }
//        Toast.makeText(ma, "Using " + myLocation.getProvider() + " Location provider", Toast.LENGTH_SHORT).show();
//        return myLocation;

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc != null) {
//                ma.setLocation(loc.getLatitude(), loc.getLongitude());
                Toast.makeText(ma, "Using " + LocationManager.NETWORK_PROVIDER + " Location provider", Toast.LENGTH_SHORT).show();
                return loc;
            }
        }

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (loc != null) {
//                ma.setLocation(loc.getLatitude(), loc.getLongitude());
                Toast.makeText(ma, "Using " + LocationManager.PASSIVE_PROVIDER + " Location provider", Toast.LENGTH_SHORT).show();
                return loc;
            }
        }

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc != null) {
//                ma.setLocation(loc.getLatitude(), loc.getLongitude());
                Toast.makeText(ma, "Using " + LocationManager.GPS_PROVIDER + " Location provider", Toast.LENGTH_SHORT).show();
                return loc;
            }
        }

        Toast.makeText(ma, "No location providers were available.", Toast.LENGTH_LONG).show();
        ma.noLocationAvailable();
        return null;
    }

    public void shutdown() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
            locationManager = null;
        }
    }
}
