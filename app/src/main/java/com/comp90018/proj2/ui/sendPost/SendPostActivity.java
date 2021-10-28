package com.comp90018.proj2.ui.sendPost;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.comp90018.proj2.R;
import com.comp90018.proj2.databinding.ActivitySendPostBinding;
import com.comp90018.proj2.ui.login.LoginFormState;
import com.comp90018.proj2.ui.photo.GlideEngine;
import com.comp90018.proj2.ui.photo.PhotoActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.utils.String.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SendPostActivity extends AppCompatActivity {

    // Initialize
    private String TAG = "SendPostActivity";
    private SendPostViewModel sendPostViewModel;
    private ActivitySendPostBinding binding;


    /**
     * photos
     */
    private final ArrayList<Photo> selectedPhotoList = new ArrayList<>();

    /**
     * Adview for picture list and album item list
     */
    private RelativeLayout photosAdView, albumItemsAdView;

    /**
     * Adview is loaded over or not
     */
    private final boolean photosAdLoaded = false;
    private final boolean albumItemsAdLoaded = false;

    // Fields
    ImageButton newImageButton;
    EditText textPostLat;
    EditText textPostLon;
    EditText textPostType;
    EditText textPostSpecies;
    Button sendPostButton;
    ProgressBar loadingProgressBar;

    private String currentFilePath;

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private boolean locationPermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    /**
     * Auth
     */
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySendPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sendPostViewModel = new ViewModelProvider(this, new SendPostViewModelFactory(getApplicationContext()))
                .get(SendPostViewModel.class);

        // Get user's location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
        getDeviceLocation();

        Log.i(TAG, "onCreate: ");

        newImageButton = binding.buttonNewImage;
        textPostLat = binding.textPostLat;
        textPostLon = binding.textPostLon;
        textPostType = binding.textPostType;
        textPostSpecies = binding.textPostSpecies;
        sendPostButton = binding.sendPost;
        loadingProgressBar = binding.loading;


        // Latitude & Longitude: onChange Validation
        TextWatcher locationTextWatcher = getLocationTextWatcher();
        textPostLat.addTextChangedListener(locationTextWatcher);
        textPostLon.addTextChangedListener(locationTextWatcher);


        // Latitude: add Observer
        sendPostViewModel.getLatitude().observe(this, textPostLat::setText);

        // Longitude: add Observer
        sendPostViewModel.getLongitude().observe(this, textPostLon::setText);


        sendPostViewModel.getSendPostFormState().observe(this, sendPostFormState -> {
            if (sendPostFormState == null) {
                return;
            }
            sendPostButton.setEnabled(sendPostFormState.isDataValid());
        });

        textPostLon.setEnabled(false);
        textPostLat.setEnabled(false);

        // sendPostButton: add ClickListener
        sendPostButton.setOnClickListener(v -> {
            Log.i(TAG, "onClick: ");
            loadingProgressBar.setVisibility(View.VISIBLE);

            // Check image selected.
            if (currentFilePath == null || "".equals(currentFilePath)) {
                sendPostViewModel.getSendPostFormState().setValue(
                        new SendPostFormState(R.string.invalid_post_image));
                return;
            }
            // [START send post]
            post();
            // [END send post]

            loadingProgressBar.setVisibility(View.INVISIBLE);
        });

        newImageButton.setOnClickListener(v -> {
            Log.i(TAG, "onClick: newImageButton");

            // [START select image]
            selectImage();
            // [END select image]
        });
    }

    private void selectImage() {
//        Intent intent = new Intent(SendPostActivity.this, PhotoActivity.class);
        EasyPhotos.createAlbum(SendPostActivity.this, true, false, GlideEngine.getInstance())
                .setFileProviderAuthority("com.comp90018.proj2.ui.photo.fileprovider")
                .start(new SelectCallback() {
                    @Override
                    public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                        currentFilePath = photos.get(0).path;
                        Log.i(TAG, "selectCallBack: " + currentFilePath);

                        BitmapFactory.Options opts = new BitmapFactory.Options();
                        opts.inJustDecodeBounds = false;
                        opts.inSampleSize = 3;
                        Bitmap bm = BitmapFactory.decodeFile(currentFilePath, opts);
                        newImageButton.setImageBitmap(bm);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
//        startActivity(intent);
    }

    /**
     * Send Post to the Firestore
     */
    private void post() {
        // [START send post to firebase]
        loadingProgressBar.setVisibility(View.VISIBLE);

        Log.i(TAG, "bSendPostButton: clicked");

        // Read image
        String[] filename =  currentFilePath.split("\\.");
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(currentFilePath, opts);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Upload image
        String uuid = UUID.randomUUID().toString() + "-" + Calendar.getInstance().getTimeInMillis();
        StorageReference uploadRef = storageRef.child("images/" + uuid + "." + filename[filename.length - 1]);
        UploadTask uploadTask = uploadRef.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null) {
                    Log.i(TAG, taskSnapshot.getMetadata().getPath());
                    sendPost2Firestore(taskSnapshot.getMetadata().getPath());
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_upload_image, Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), R.string.error_upload_image, Toast.LENGTH_LONG).show();
            }
        });
        loadingProgressBar.setVisibility(View.INVISIBLE);
        // [END send post to firebase]
    }

    private void sendPost2Firestore(String imagePath) {
        // Test
        Map<String, Object> user = new HashMap<>();
        user.put("PostImage", imagePath);
        user.put("PostLocation", new GeoPoint(Double.parseDouble(textPostLat.getText().toString()),
                Double.parseDouble(textPostLon.getText().toString())));
        user.put("PostSpecies", textPostSpecies.getText().toString());
        user.put("PostTime", new Timestamp(new Date(System.currentTimeMillis())));
        user.put("PostType", textPostType.getText().toString());
        user.put("UserId", mAuth.getUid());

        db.collection("Post")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getApplicationContext(), R.string.success_send_post, Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Error adding document", e);
                        Toast.makeText(getApplicationContext(), R.string.error_upload_image, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */

    // [START maps_current_place_location_permission]
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    // [END maps_current_place_location_permission]


    // [START maps_current_place_get_device_location]
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                Log.i(TAG, "Your Location: " + "\n" + "Latitude: " +
                                        lastKnownLocation.getLatitude() + "\n" + "Longitude: " + lastKnownLocation.getLongitude());
                                sendPostViewModel.updateLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
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
    protected void onResume() {
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

    private TextWatcher getLocationTextWatcher() {
        TextWatcher afterTextChangedListener = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                sendPostViewModel.locationDataChanged(textPostLat.getText().toString(),
                        textPostLon.getText().toString());
            }
        };

        return afterTextChangedListener;
    }
}
