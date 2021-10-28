package com.comp90018.proj2.ui.signUp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.comp90018.proj2.R;
import com.comp90018.proj2.databinding.ActivitySignUpBinding;
import com.comp90018.proj2.ui.login.LoginFormState;
import com.comp90018.proj2.ui.photo.GlideEngine;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {

    // Initialize
    private String TAG = "SignUpActivity";
    private SignUpViewModel signUpViewModel;
    private ActivitySignUpBinding binding;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    // Fields
    private ImageView bIcon;
    private Button bSignup;
    private EditText tEmail;
    private EditText tPassword;
    private EditText tDisplayName;
    private ProgressBar loadingProgressBar;

    // Icon path
    private String currIconPath;
    private String iconUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        signUpViewModel = new ViewModelProvider(this, new SignUpViewModelFactory(getApplicationContext()))
                .get(SignUpViewModel.class);

        mAuth = FirebaseAuth.getInstance();

        Log.i(TAG, "onCreate: ");

        bIcon = binding.buttonIcon;
        bSignup = binding.signUp;
        tEmail = binding.email;
        tPassword = binding.password;
        tDisplayName = binding.displayName;
        loadingProgressBar = binding.loading;

        // Icon Button
        bIcon.setOnClickListener(v -> {
            Log.i(TAG, "onClick: selectIcon");
            // [START select image]
            selectImage();
            // [END select image]
        });

        // Edit Text Validation
        TextWatcher afterTextChangedListener = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                signUpViewModel.signUpDataChanged(tEmail.getText().toString(),
                        tPassword.getText().toString(), tDisplayName.getText().toString());
            }
        };

        tEmail.addTextChangedListener(afterTextChangedListener);
        tPassword.addTextChangedListener(afterTextChangedListener);
        tDisplayName.addTextChangedListener(afterTextChangedListener);

        // Add error message
        signUpViewModel.getSignUpFormState().observe(this, new Observer<SignUpFormState>() {
            @Override
            public void onChanged(@Nullable SignUpFormState signUpFormState) {
                if (signUpFormState == null) {
                    return;
                }
                bSignup.setEnabled(signUpFormState.isDataValid());
                if (signUpFormState.getUsernameError() != null) {
                    tEmail.setError(getString(signUpFormState.getUsernameError()));
                }
                if (signUpFormState.getPasswordError() != null) {
                    tPassword.setError(getString(signUpFormState.getPasswordError()));
                }
                if (signUpFormState.getDisplayNameError() != null) {
                    tDisplayName.setError(getString(signUpFormState.getDisplayNameError()));
                }
            }
        });


        // Sign Up
        bSignup.setOnClickListener(v -> {
            Log.i(TAG, "onClick: ");
            loadingProgressBar.setVisibility(View.VISIBLE);

            // [START sign up]
            signUp();
            // [END sign up]

            loadingProgressBar.setVisibility(View.INVISIBLE);
        });


    }

    private void saveIcon() {
        String[] filename =  currIconPath.split("\\.");
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(currIconPath, opts);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Upload image
        String uuid = UUID.randomUUID().toString() + "-" + Calendar.getInstance().getTimeInMillis();
        StorageReference uploadRef = storageRef.child("icons/" + uuid + "." + filename[filename.length - 1]);
        UploadTask uploadTask = uploadRef.putBytes(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                // Continue with the task to get the download URL
                return uploadRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "Uri: " +  task.getResult());
//                    iconUri = task.getResult().getPath();
                    updateUserProfile(task.getResult());
                } else {
                    // Handle failures
                    // ...
                }
            }
        });


//        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                if (taskSnapshot.getMetadata() != null) {
//                    Log.i(TAG, taskSnapshot.getMetadata().getPath());
//                    iconUri = taskSnapshot.getMetadata().getPath();
//                } else {
//                    Toast.makeText(getApplicationContext(), R.string.error_upload_image, Toast.LENGTH_LONG).show();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Toast.makeText(getApplicationContext(), R.string.error_upload_image, Toast.LENGTH_LONG).show();
//            }
//        });
    }

    private void signUp() {
        String email = tEmail.getText().toString();
        String password = tPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail: success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                Log.d(TAG, "profileUpdates: start");

                                Log.i(TAG, "Expected Display Name = " + tDisplayName.getText().toString());
                                Log.i(TAG, "Expected Uri = " + iconUri);

                                if (currIconPath != null && !"".equals(currIconPath)) {
                                    saveIcon();

                                } else {
                                    updateUserProfile(Uri.parse("https://firebasestorage.googleapis.com/v0/b/mobiletest-e36f3.appspot.com/" +
                                            "o/icons%2Fvecteezypeople-business-avatarpp0421_generated.jpg?alt=media" +
                                            "&token=fd8edb77-c9a1-4e07-862c-8a88d0f82261"));
                                }

                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUserProfile(Uri photoUrl) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(tDisplayName.getText().toString())
                .setPhotoUri(photoUrl)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");

                            Log.i(TAG, "profileUpdates: succeed");
                            Log.i(TAG, "Display Name = " + user.getDisplayName());
                            Log.i(TAG, "Uri = " + String.valueOf(user.getPhotoUrl()));
                        }
                    }
                });
        finish();
    }
    private void selectImage() {
//        Intent intent = new Intent(SendPostActivity.this, PhotoActivity.class);
        EasyPhotos.createAlbum(SignUpActivity.this, true, false, GlideEngine.getInstance())
                .setFileProviderAuthority("com.comp90018.proj2.ui.photo.fileprovider")
                .start(new SelectCallback() {
                    @Override
                    public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                        currIconPath = photos.get(0).path;
                        Log.i(TAG, "selectCallBack: " + currIconPath);

                        BitmapFactory.Options opts = new BitmapFactory.Options();
                        opts.inJustDecodeBounds = false;
                        opts.inSampleSize = 3;
                        Bitmap bm = BitmapFactory.decodeFile(currIconPath, opts);
                        bIcon.setImageBitmap(bm);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
//        startActivity(intent);
    }

}
