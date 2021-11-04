package com.comp90018.proj2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.comp90018.proj2.databinding.ActivityMainBinding;
import com.comp90018.proj2.utils.LocationCommunication;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.GeoPoint;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * The main activity for the application
 */
public class MainActivity extends AppCompatActivity implements LocationCommunication, SensorEventListener {
    private ActivityMainBinding binding;
    private String TAG = "MainActivity";

    // For Shake-shake
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    private boolean bb = false;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    // get user's current location
    private GeoPoint current;
    // implements LocationCommunication to share the location with child fragments
    private LocationManager lm;

    /**
     * Override the onCreate method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the location
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationUpdate();

        // Initialize sensors
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        // Initialize the view
        Log.i(TAG, "onCreate: ");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the main bottom navigation
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_finder, R.id.navigation_map, R.id.navigation_account)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // Check if come from the Post Location Button
        if(getIntent().getIntExtra("fromLocationToMap",0)==1){
            NavInflater navInflater = navController.getNavInflater();
            NavGraph navGraph = navInflater.inflate(R.navigation.mobile_navigation);
            navGraph.setStartDestination(R.id.navigation_map);
            navController.setGraph(navGraph, getIntent().getExtras());
        }

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }


    /**
     * Override interface, child fragments will callback on this method
     * @return user's current location
     */
    @Override
    public GeoPoint getLocation() {
        return current;
    }

    /**
     * Location Listener to check if location is changed
     */
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            current = locationCvtGeo(location);
        }

        /**
         * Get user's location
         */
        @SuppressLint("WrongConstant")
        @Override
        public void onProviderEnabled(@NonNull String provider) {
            if (checkCallingOrSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,"Please Enable your GPS.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 0);
                return;
            }

            // When GPS LocationProvider is on, update current location with real data
            Location temp = (lm.getLastKnownLocation(provider));
            if (temp == null) {
                current = new GeoPoint(-34, 151);
            } else {
                current = locationCvtGeo(temp);
            }
        }

        /**
         * If the location is not permitted
         */
        public void onProviderDisabled(String provider) {
            // default location
            current = new GeoPoint(-34, 151);
        }
    };

    /**
     * In Java, Coordinates data is Location, while Firebase is GeoPoint
     * @param location java data type
     * @return current location in GeoPoint
     */
    public GeoPoint locationCvtGeo(Location location){
        double lat = location.getLatitude();
        // Log.e("convert", String.valueOf(lat));
        double lng = location.getLongitude();
        return new GeoPoint(lat, lng);
    }

    @SuppressLint("WrongConstant")
    public void locationUpdate() {

        // if location permission is not on, ask users for opening
        if ( checkCallingOrSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(MainActivity.this, "Please enable GPS.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            current = new GeoPoint(-34, 151);
        } else {
            current = locationCvtGeo(location);
        }
        // Log.e("current location", String.valueOf(current));

        // For every 2s, update user's location
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 8,mLocationListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");

    }

    /**
     * Handlers for sensor changed event
     * @param sensorEvent the sensor event
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD && !bb) {
                    bb = true;
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Do you want to share your feelings about using the program with us?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
                            bb = false;
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bb = false;
                        }
                    });
                    builder.show();
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}