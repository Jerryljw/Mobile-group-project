package com.comp90018.proj2.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.comp90018.proj2.R;
import com.comp90018.proj2.data.model.CardItem;
import com.comp90018.proj2.databinding.FragmentMapBinding;
import com.comp90018.proj2.ui.post.PostActivity;
import com.comp90018.proj2.ui.sendPost.SendPostActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMarkerClickListener {

    private static final int DEFAULT_ZOOM = 15;
    // Keys for storing activity state.
    // [START maps_current_place_state_keys]
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private String TAG = "MapFrag";
    private MapViewModel mapViewModel;
    private FragmentMapBinding binding;
    private Button bCaptureImage;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean permissionDenied = false;
    // [END maps_current_place_state_keys]
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    private LatLng latLngFromPostActivity;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (getArguments() != null) {
            Bundle arguments = getArguments();
            double latitude = arguments.getDouble("latitude");
            double longitude = arguments.getDouble("longitude");
            latLngFromPostActivity = new LatLng(latitude, longitude);
            Log.d(TAG, "onCreateView: latlng from activity" + latLngFromPostActivity);
            Log.d(TAG, "onCreateView: ");
        }

        // Menu
        setHasOptionsMenu(true);

        // Map
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_frag);
        mapFragment.getMapAsync(this);


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

    /* Setup Menu */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), SendPostActivity.class);

        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    private void getPosts() {
        firebaseFirestore.collection("Post_Temp")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<CardItem> postList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String postId = document.getId();
                                Log.d(TAG, postId + " => " + document.getData());

                                String postSpecies = document.getString("PostSpecies");
                                Timestamp postTime = document.getTimestamp("PostTime");
                                String postType = document.getString("PostType");
                                String postUserid = document.getString("UserId");
                                String postFlag = document.getString("PostFlag");
                                GeoPoint postGeoPoint = document.getGeoPoint("PostLocation");
                                String postImg = document.getString("PostImage");
                                String postTitle = document.getString("PostTitle");
                                String postMessage = document.getString("PostMessage");
                                String userDisplayName = document.getString("UserDisplayname");
                                GeoPoint postLocation = (GeoPoint) document.getData().get("PostLocation");

                                CardItem cardItem = new CardItem();
                                cardItem.setPostSpecies(postSpecies);
                                cardItem.setPostTime(postTime);
                                cardItem.setPostType(postType);
                                cardItem.setUserId(postUserid);
                                cardItem.setPostFlag(Integer.parseInt(postFlag));
                                cardItem.setPoint(postGeoPoint);
                                cardItem.setTitles(postTitle);

                                if (userDisplayName == null || userDisplayName.isEmpty()) {
                                    cardItem.setUsernames("User-" + postUserid);
                                } else {
                                    cardItem.setUsernames(userDisplayName);
                                }
                                cardItem.setPostMessage(postMessage);

                                StorageReference gsReference = firebaseStorage.getReferenceFromUrl(postImg);
                                cardItem.setImg(gsReference);

                                postList.add(cardItem);
                                mapViewModel.addPost(postId, cardItem);

                                if (postLocation != null) {
                                    LatLng latLng = new LatLng(postLocation.getLatitude(), postLocation.getLongitude());
                                    map.addMarker(new MarkerOptions().position(latLng).
                                            title("Post Marker")).setTag(postId);
                                }
                            }

                            mapViewModel.setPostList(postList);

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
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
        map.setOnInfoWindowClickListener(this);
        map.setOnMyLocationButtonClickListener(this);
        map.setInfoWindowAdapter(new CardInfoWindowAdapter());
        enableMyLocation();

        // Get user's location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        getDeviceLocation();

        LatLng latlng;
        if (latLngFromPostActivity != null) {
            Log.d(TAG, "onMapReady: from post");
            latlng = latLngFromPostActivity;
        } else if (lastKnownLocation != null) {
            Log.d(TAG, "onMapReady: from last location");

            latlng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        } else {
            Log.d(TAG, "onMapReady: default");
            latlng = defaultLocation;
        }
        Log.d(TAG, "onMapReady: on first start move camera" +  latlng);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, DEFAULT_ZOOM));

        View mapView = mapFragment.getView();
        moveCompassButton(mapView);
        getPosts();
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        Toast.makeText(getContext(), "Info window clicked", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), PostActivity.class);
        Bundle bundle = new Bundle();
        String postId = (String) marker.getTag();
        Log.e(TAG, postId);
        bundle.putString("postId", postId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Intent intent = new Intent(getActivity(), PostActivity.class);
        Bundle bundle = new Bundle();
        String postId = (String) marker.getTag();
        Log.e(TAG, postId);
        bundle.putString("postId", postId);
        intent.putExtras(bundle);
        startActivity(intent);
        return false;
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    // [START maps_current_place_on_save_instance_state]
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }
    // [END maps_current_place_on_save_instance_state]

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        // [END maps_check_location_permission]
    }

    // [START maps_check_location_permission_result]
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // [START_EXCLUDE]
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
            // [END_EXCLUDE]
        }
    }
    // [END maps_check_location_permission_result]


    // [START maps_current_place_get_device_location]
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                Log.i(TAG, "Your Location: " + "\n" + "Latitude: " +
                                        lastKnownLocation.getLatitude() + "\n" + "Longitude: " + lastKnownLocation.getLongitude());
                                mapViewModel.setLocation(lastKnownLocation);
                                LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                if (latLngFromPostActivity == null) {
                                    Log.d(TAG, "onComplete: here moving map camera");
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                                }
                            } else {
                                Log.d(TAG, "lastKnownLocation: " + lastKnownLocation);
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
    // [END maps_current_place_get_device_location]

    @Override
    public void onResume() {
        super.onResume();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getChildFragmentManager(), "dialog");
    }

    /**
     * Move the compass button to the right side, centered vertically.
     */
    private void moveCompassButton(View mapView) {
        try {
            assert mapView != null; // skip this if the mapView has not been set yet

            Log.d(TAG, "moveCompassButton()");

            // View view = mapView.findViewWithTag("GoogleMapCompass");
            View view = mapView.findViewWithTag("GoogleMapMyLocationButton");

            // move the compass button to the right side, centered
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            layoutParams.setMarginEnd(18);

            view.setLayoutParams(layoutParams);
        } catch (Exception ex) {
            Log.e(TAG, "moveCompassButton() - failed: " + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
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


    class CardInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View mContent;


        CardInfoWindowAdapter() {
            mContent = getLayoutInflater().inflate(R.layout.carditem, null);
        }

        @Nullable
        @Override
        public View getInfoWindow(@NonNull Marker marker) {
            return null;
        }

        @Nullable
        @Override
        public View getInfoContents(@NonNull Marker marker) {
            render(marker, mContent);
            return mContent;
        }

        private void render(Marker marker, View view) {
            ImageView postImg = (ImageView) view.findViewById(R.id.home_item_img);
            ImageView userImg = (ImageView) view.findViewById(R.id.home_item_user_img);
            TextView location = (TextView) view.findViewById(R.id.home_item_location);
            TextView title = (TextView) view.findViewById(R.id.home_item_title);
            TextView username = (TextView) view.findViewById(R.id.home_item_username);

            // set default image value
            postImg.setImageResource(R.drawable.ic_card_image);
            userImg.setImageResource(R.drawable.ic_card_portrait);

            // update view
            String postId = (String) marker.getTag();
            CardItem post = mapViewModel.getPost(postId);

            Glide.with(view.getContext())
                    .load(post.getImg())
                    .centerCrop()
                    .listener(new MarkerCallback(marker))
                    .into(postImg);
            title.setText(post.getTitles());
            username.setText(post.getUsernames());
        }

        class MarkerCallback implements RequestListener<Drawable> {

            private Marker marker;

            public MarkerCallback(Marker marker) {
                this.marker = marker;
            }

            private void onSuccess() {
                if (marker != null && marker.isInfoWindowShown()) {
                    marker.showInfoWindow();
                }
            }

            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                onSuccess();
                return false;
            }
        }
    }


}