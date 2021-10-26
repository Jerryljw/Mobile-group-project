package com.comp90018.proj2.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.comp90018.proj2.MainActivity;
import com.comp90018.proj2.R;
import com.comp90018.proj2.databinding.FragmentMapBinding;
import com.comp90018.proj2.ui.login.LoginActivity;
import com.comp90018.proj2.ui.sendPost.SendPostActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class MapFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener{

    private String TAG = "Map Page";

    private MapViewModel mapViewModel;
    private FragmentMapBinding binding;

    private Button bCaptureImage;

    private GoogleMap map;
    private MapView mapView;

    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        mapViewModel =
                new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // Map
        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        // Text
        final TextView textView = binding.textMap;
        mapViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        // Camera Button
        bCaptureImage = binding.cameraCaptureButton;
        bCaptureImage.setOnClickListener(view -> {
            Log.d(TAG, "bCaptureImage Click:");

            Intent intent = new Intent();
            intent.setClass(getActivity(), SendPostActivity.class);
            startActivity(intent);
        });


        return root;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        googleMap.setOnInfoWindowClickListener(this);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);

        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f));
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        Toast.makeText(getContext().getApplicationContext(), "Info window clicked", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }

}