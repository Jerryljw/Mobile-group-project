package com.comp90018.proj2.ui.sendPost;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.comp90018.proj2.MainActivity;
import com.comp90018.proj2.R;
import com.comp90018.proj2.databinding.ActivitySendPostBinding;
import com.comp90018.proj2.ui.photo.GlideEngine;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.kyleduo.switchbutton.SwitchButton;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Activity for Send Post
 */
public class SendPostActivity extends AppCompatActivity {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    // Initialize
    private final String TAG = "SendPostActivity";

    // Firebase instances
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = storage.getReference();

    // UI components
    ImageButton newImageButton;
    ImageButton bUpdateLocation;
    EditText tPostLat;
    EditText tPostLon;
    EditText tPostTitle;
    EditText tPostMessage;
    SwitchButton bPostType;
    EditText tPostSpecies;
    Button bSendPost;
    AVLoadingIndicatorView loading;
    private SendPostViewModel sendPostViewModel;
    private ActivitySendPostBinding binding;

    // Adview for picture list and album item list
    private RelativeLayout photosAdView, albumItemsAdView;

    // The path for the current image
    private String currentFilePath;

    // Location
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private boolean locationPermissionGranted = false;

    // The maximum file size
    private double MAX_SIZE = 400.00;


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar).setDisplayHomeAsUpEnabled(true);

        binding = ActivitySendPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sendPostViewModel = new ViewModelProvider(this, new SendPostViewModelFactory(getApplicationContext()))
                .get(SendPostViewModel.class);

        // Get user's location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
        getDeviceLocation();

        Log.i(TAG, "onCreate: ");

        // Get bindings of UI components
        newImageButton = binding.buttonNewImage;
        bUpdateLocation = binding.buttonUpdateLocation;
        tPostLat = binding.textPostLat;
        tPostLon = binding.textPostLon;
        tPostTitle = binding.textPostTitle;
        tPostMessage = binding.textPostMessage;
        tPostSpecies = binding.textPostSpecies;
        bPostType = binding.switchPostType;
        bSendPost = binding.sendPost;
        loading = binding.loading;

        // Latitude & Longitude: onChange Validation
        TextWatcher locationTextWatcher = getLocationTextWatcher();
        tPostTitle.addTextChangedListener(locationTextWatcher);
        tPostMessage.addTextChangedListener(locationTextWatcher);
        tPostSpecies.addTextChangedListener(locationTextWatcher);

        // Latitude: add Observer
        sendPostViewModel.getLatitude().observe(this, tPostLat::setText);

        // Longitude: add Observer
        sendPostViewModel.getLongitude().observe(this, tPostLon::setText);

        // Post Species: add Observer
        sendPostViewModel.getSpecies().observe(this, tPostSpecies::setText);

        // Add observer for post form state
        sendPostViewModel.getSendPostFormState().observe(this, sendPostFormState -> {
            if (sendPostFormState == null) {
                return;
            }

            bSendPost.setEnabled(sendPostFormState.isDataValid());
            if (sendPostFormState.getImageError() != null) {
                tPostTitle.setError(getString(sendPostFormState.getImageError()));
            }
            if (sendPostFormState.getTitleError() != null) {
                tPostTitle.setError(getString(sendPostFormState.getTitleError()));
            }
            if (sendPostFormState.getMessageError() != null) {
                tPostMessage.setError(getString(sendPostFormState.getMessageError()));
            }
        });

        // Disable the text fields
        tPostLon.setEnabled(false);
        tPostLat.setEnabled(false);

        // sendPostButton: add ClickListener
        bSendPost.setOnClickListener(v -> {
            Log.i(TAG, "onClick: ");
            loading.show();

            // Check if image selected.
            if (currentFilePath == null || "".equals(currentFilePath)) {
                return;
            }
            // [START send post]
            post();
            // [END send post]
        });

        // Add click listener for selecting new image
        newImageButton.setOnClickListener(v -> {
            Log.i(TAG, "onClick: newImageButton");

            // [START select image]
            selectImage();
            // [END select image]
        });

        // Add click listener for updating location
        bUpdateLocation.setOnClickListener(v -> {
            getDeviceLocation();
        });

        // When the page is created, hide the loading
        loading.hide();
    }

    /**
     * Method to popup EasyPhotos to select avatar image
     */
    private void selectImage() {
        EasyPhotos.createAlbum(SendPostActivity.this, true, false, GlideEngine.getInstance())
                .setFileProviderAuthority("com.comp90018.proj2.ui.photo.fileprovider")
                .start(new SelectCallback() {
                    @Override
                    public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                        currentFilePath = photos.get(0).path;
                        Log.i(TAG, "selectCallBack: " + currentFilePath);

                        // Load the image and display it om the view
                        BitmapFactory.Options opts = new BitmapFactory.Options();
                        opts.inJustDecodeBounds = false;
                        opts.inSampleSize = 3;
                        Bitmap bm = BitmapFactory.decodeFile(currentFilePath, opts);
                        newImageButton.setImageBitmap(bm);

                        // Label the selected image through Google API
                        InputImage image = InputImage.fromBitmap(bm, 0);
                        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

                        labeler.process(image)
                                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                                    @Override
                                    public void onSuccess(@NonNull List<ImageLabel> labels) {
                                        if (labels.size() == 0) {
                                            Toast.makeText(getApplicationContext(), R.string.error_label, Toast.LENGTH_LONG).show();
                                        } else {
                                            // Display the label to the view
                                            sendPostViewModel.setSpecies(labels.get(0).getText());
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), R.string.error_label, Toast.LENGTH_LONG).show();

                                    }
                                });

                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    /**
     * Send Post to the Firestore
     */
    private void post() {
        // [START send post to firebase]
        Log.i(TAG, "bSendPostButton: clicked");

        // Read image
        String[] filename = currentFilePath.split("\\.");
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(currentFilePath, opts);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Check the image size, if the size exceed maximum, then resize it
        double mid = (double) data.length / 1024;
        Log.i(TAG, "Bitmap size: " + data.length);
        Log.i(TAG, "Bitmap size: " + (double) data.length);
        Log.i(TAG, "Bitmap size: " + mid);

        int quality = 100;

        // Loop until reach the expected size
        while (mid > MAX_SIZE) {
            quality -= 10;
            Log.i(TAG, "Exceed max size");

            double i = mid / MAX_SIZE;
            Log.i(TAG, bm.getWidth() + " -> " + bm.getWidth() / Math.sqrt(i));

            // Resize image
            bm = zoomImage(bm, bm.getWidth() / Math.sqrt(i), bm.getHeight() / Math.sqrt(i));
            baos.reset();
            bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            data = baos.toByteArray();
            Log.i(TAG, "Zoomed Bitmap size: " + data.length);
            mid = (double) data.length / 1024;
        }

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
                loading.hide();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), R.string.error_upload_image, Toast.LENGTH_LONG).show();
                loading.hide();
            }
        });
        // [END send post to firebase]
    }

    /**
     * Method to save post to firebase store
     * @param imagePath the image path
     */
    private void sendPost2Firestore(String imagePath) {
        // Create the dto
        Map<String, Object> postDto = new HashMap<>();
        postDto.put("PostImage", imagePath);
        postDto.put("PostFlag", 0);
        postDto.put("PostTitle", tPostTitle.getText().toString());
        postDto.put("PostMessage", tPostMessage.getText().toString());
        postDto.put("PostLocation", new GeoPoint(Double.parseDouble(tPostLat.getText().toString()),
                Double.parseDouble(tPostLon.getText().toString())));
        postDto.put("PostSpecies", tPostSpecies.getText().toString());
        postDto.put("PostTime", new Timestamp(new Date(System.currentTimeMillis())));
        postDto.put("PostType", bPostType.isChecked() ? getResources().getString(R.string.post_type_on)
                : getResources().getString(R.string.post_type_off));
        postDto.put("UserDisplayName", mAuth.getCurrentUser() == null ? "" : mAuth.getCurrentUser().getDisplayName());
        postDto.put("UserPhotoUri", (mAuth.getCurrentUser() == null || mAuth.getCurrentUser().getPhotoUrl() == null) ?
                "" : mAuth.getCurrentUser().getPhotoUrl().toString());
        postDto.put("UserId", mAuth.getUid());

        // Send dto to firebase
        db.collection("Post_Temp")
                .add(postDto)
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


    /**
     * Get the device location
     */
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

    /**
     * Method to get location text watcher
     * @return
     */
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
                sendPostViewModel.formDataChanged(currentFilePath,
                        tPostTitle.getText().toString(), tPostMessage.getText().toString());
            }
        };

        return afterTextChangedListener;
    }

    /**
     * Helper method to zoom image
     * @param bitmap original bitmap
     * @param newWidth new width
     * @param newHeight new height
     * @return zoomed bitmap
     * The method references the code from: https://blog.csdn.net/tf576776047/article/details/8143456
     */
    private Bitmap zoomImage(Bitmap bitmap, double newWidth, double newHeight) {
        // Get original width and height
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();

        // Calculate how many scale to zoom
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // Zoom image
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap zoomedBitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) width,
                (int) height, matrix, true);
        return zoomedBitmap;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent1 = new Intent(SendPostActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("fromLocationToMap", 1);
        intent1.putExtras(bundle);
        double latitude = sendPostViewModel.getLatitude().getValue() == null ?
                -34 : Double.parseDouble(sendPostViewModel.getLatitude().getValue());
        double longitude = sendPostViewModel.getLongitude().getValue() == null ?
                151 : Double.parseDouble(sendPostViewModel.getLongitude().getValue());

        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);
        intent1.putExtras(bundle);
        startActivity(intent1);
        return super.onOptionsItemSelected(item);
    }
}