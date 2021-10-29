package com.comp90018.proj2;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static androidx.camera.core.CameraX.getContext;
import static androidx.core.content.PermissionChecker.checkCallingOrSelfPermission;

import android.Manifest;
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


import com.comp90018.proj2.ui.map.MapFragment;
import com.comp90018.proj2.utils.LocationCommunication;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.comp90018.proj2.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationCommunication {
// implements LocationCommunication to share the location with child fragments
    private ActivityMainBinding binding;
    private String TAG = "MainActivity";


    // get user's current location
    private GeoPoint current;
    private LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationUpdate();


        Log.i(TAG, "onCreate: ");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // check if come from the Post Location Button
        if(getIntent().getIntExtra("fromLocationToMap",0)==1){

            NavInflater navInflater = navController.getNavInflater();
            NavGraph navGraph = navInflater.inflate(R.navigation.mobile_navigation);
            navGraph.setStartDestination(R.id.navigation_dashboard);
            navController.setGraph(navGraph, new Bundle());
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
            Location temp = (lm.getLastKnownLocation(provider));
            current = locationCvtGeo(temp);
        }
        
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

        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        current = locationCvtGeo(location);
        // Log.e("current location", String.valueOf(current));

        // For every 2s, update user's location
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 8,mLocationListener);
    }

    /**
     * Calculate distance through post location and current location
     * @param current current user's location
     * @param postPoint post location
     * @return the distance in km
     */
    static public double caldistance(GeoPoint current, GeoPoint postPoint)
    {
        double lon1 = Math.toRadians(current.getLongitude());
        double lon2 = Math.toRadians(postPoint.getLongitude());
        double lat1 = Math.toRadians(current.getLatitude());
        double lat2 = Math.toRadians(postPoint.getLatitude());

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return (c * r);
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