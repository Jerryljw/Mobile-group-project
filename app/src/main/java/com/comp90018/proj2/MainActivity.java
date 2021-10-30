package com.comp90018.proj2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.comp90018.proj2.databinding.ActivityMainBinding;
import com.comp90018.proj2.utils.GeoPointUtils;
import com.comp90018.proj2.utils.LocationCommunication;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.GeoPoint;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements LocationCommunication {
// implements LocationCommunication to share the location with child fragments
    private ActivityMainBinding binding;
    private String TAG = "MainActivity";


    // get user's current location
    private GeoPoint current;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationUpdate();


        Log.i(TAG, "onCreate: ");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_finder, R.id.navigation_map, R.id.navigation_account)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // check if come from the Post Location Button
        if(getIntent().getIntExtra("fromLocationToMap",0)==1){

            NavInflater navInflater = navController.getNavInflater();
            NavGraph navGraph = navInflater.inflate(R.navigation.mobile_navigation);
            navGraph.setStartDestination(R.id.navigation_map);
            Log.d(TAG, "onCreate: bundle" + getIntent().getExtras());
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
            current = GeoPointUtils.locationCvtGeo(location);
        }

        @SuppressLint("WrongConstant")
        @Override
        public void onProviderEnabled(@NonNull String provider) {
            if ( checkCallingOrSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,"Please Enable your GPS.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 0);
                return;
            }

            // When GPS LocationProvider is on, update current location with real data
            Location temp = (locationManager.getLastKnownLocation(provider));
            current = GeoPointUtils.locationCvtGeo(temp);
        }
        
        public void onProviderDisabled(String provider) {

            // default location
            current = new GeoPoint(-34, 151);
        }
    };

    @SuppressLint("WrongConstant")
    public void locationUpdate() {

        // if location permission is not on, ask users for opening
        if ( checkCallingOrSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(MainActivity.this, "Please enable GPS.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        current = GeoPointUtils.locationCvtGeo(location);
        // Log.e("current location", String.valueOf(current));

        // For every 2s, update user's location
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 8,mLocationListener);
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
}