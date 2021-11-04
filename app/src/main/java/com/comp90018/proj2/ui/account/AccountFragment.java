package com.comp90018.proj2.ui.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.request.RequestOptions;
import com.comp90018.proj2.R;
import com.comp90018.proj2.databinding.FragmentAccountBinding;
import com.comp90018.proj2.databinding.UpdatePasswordBinding;
import com.comp90018.proj2.ui.login.LoginActivity;
import com.comp90018.proj2.ui.photo.GlideEngine;
import com.comp90018.proj2.utils.GlideApp;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Objects;

/**
 * Fragment for account
 */
public class AccountFragment extends Fragment {

    private final String TAG = "UploadImageActivity";

    // UI Components
    private AccountViewModel accountViewModel;
    private FragmentAccountBinding binding;
    private UpdatePasswordBinding binding2;

    private TextView occupationTxtView, nameTxtView, displayNameView;
    private TextView emailTxtView, newPasswordView, confirmPasswordView;
    private ImageView userImageView, emailImageView, phoneImageView, nameImageView;
    private ImageView facebookImageView, twitterImageView;

    // The path for the current icon
    private String currIconPath;

    // Firebase instances
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = storage.getReference();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        // Get the bindings of UI components
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        binding2 = UpdatePasswordBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView textView = binding.textAccount;
        occupationTxtView = binding.occupationTextview;
        displayNameView = binding.displayNameTextview;
        emailTxtView = binding.emailTextview;

        nameImageView = binding.nameImageView;
        userImageView = binding.userImageview;
        emailImageView = binding.emailImageview;
        phoneImageView = binding.phoneImageview;
        Button logoutButton = binding.logout;
        Button updateButton = binding.update;
        Button resetPasswordButton = binding.resetPassword;


        // Get the user's current information, and display to the view
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String Name = currentUser != null ? currentUser.getDisplayName() : "";
        if ("".equals(Name)){
            Name = "Default Name";
        }
        emailTxtView.setText(currentUser != null ? currentUser.getEmail() : "");
        occupationTxtView.setText("Name");
        displayNameView.setText(Name);

        // Add click listener for selecting new avatar
        userImageView.setOnClickListener(v -> {
            Log.i(TAG, "onClick: selectIcon");
            // [START select image]
            selectImage();
            // [END select image]
        });


        //Load image in to image view
        GlideApp.with(this)
                .load(String.valueOf(mAuth.getCurrentUser().getPhotoUrl()))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_card_portrait)
                        .fitCenter())
                .into(userImageView);


        textView.setText("Welcome");

        // Add click listener for loggint out
        logoutButton.setOnClickListener(view -> {
            mAuth.signOut();

            Intent intent = new Intent();
            intent.setClass(requireActivity().getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });


        // Update button action for updating user image and name
        updateButton.setOnClickListener(view -> {
            String newDisplayName = displayNameView.getText().toString();
            if (newDisplayName.length() < 6) {
                Toast.makeText(this.getContext(), "Please use a Name longer than 6 letter", Toast.LENGTH_SHORT).show();
            } else {
                if (currIconPath != null && !"".equals(currIconPath)) {
                    saveIcon(currentUser);

                } else {
                    updateUserProfile(Uri.parse(Objects.requireNonNull(mAuth.getCurrentUser().getPhotoUrl()).toString()));
                }
            }

        });

        // Add click listener for resetting password
        resetPasswordButton.setOnClickListener(view -> {

            FirebaseUser user = mAuth.getCurrentUser();

            // Popup the dialog for inputting new password
            final AlertDialog.Builder alertDialog7 = new AlertDialog.Builder(this.requireContext());
            View view1 = View.inflate(this.getContext(), R.layout.update_password, null);
            final EditText et = view1.findViewById(R.id.et);
            Button cancel_button = view1.findViewById(R.id.cancel_button);
            Button reset_button = view1.findViewById(R.id.reset_button);
            EditText newPasswordView = view1.findViewById(R.id.new_password);
            EditText confirmPasswordView = view1.findViewById(R.id.confirm_password);
            alertDialog7
                    .setTitle("Reset Password")
                    .setIcon(R.drawable.ic_baseline_security_24)
                    .setView(view1)
                    .create();
            final AlertDialog show = alertDialog7.show();

            // Add click listener for cancelling password reset
            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(requireActivity().getApplicationContext(), "Update Cancelled", Toast.LENGTH_SHORT).show();
                    show.dismiss();
                }
            });

            // Add click listener for resetting password
            reset_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Get the input
                    String p = newPasswordView.getText().toString();
                    String cp = confirmPasswordView.getText().toString();

                    // Check if the new password is valid, and popup message accordingly.
                    if (p.equals(cp) && p.length() >= 6) {
                        user.updatePassword(p).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG, "eeeeee: " + e);
                            }
                        })
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(requireActivity().getApplicationContext(), "Password Update succeed", Toast.LENGTH_SHORT).show();
                                            show.dismiss();
                                        } else {
                                            Toast.makeText(requireActivity().getApplicationContext(), "Updated Password Failed!! Please re-login to try.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    } else {
                        Toast.makeText(requireActivity().getApplicationContext(), "Please enter same password longer than 6", Toast.LENGTH_SHORT).show();
                    }

                }
            });


        });

        return root;


    }

    //    Select image function for selecting user image
    private void selectImage() {
//        Intent intent = new Intent(SendPostActivity.this, PhotoActivity.class);
        EasyPhotos.createAlbum(this, true, false, GlideEngine.getInstance())
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
                        userImageView.setImageBitmap(bm);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
//        startActivity(intent);
    }


    //Save image and get the url for the image
    private void saveIcon(FirebaseUser currentUser) {
        String[] filename = currIconPath.split("\\.");
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(currIconPath, opts);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Upload image
        String uuid = currentUser.getUid();
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
                    Log.i(TAG, "Uri: " + task.getResult());
                    updateUserProfile(task.getResult());
                } else {
                    Log.i(TAG, "SaveIcon failed");
                }
            }
        });
    }

    //    Update user profile to firebase
    private void updateUserProfile(Uri photoUrl) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayNameView.getText().toString())
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
                            Log.i(TAG, "Uri = " + user.getPhotoUrl());
                            Toast.makeText(requireActivity().getApplicationContext(), "User Profile Updated Successes!!", Toast.LENGTH_SHORT).show();
                            displayNameView.clearFocus();
                        } else {
                            Toast.makeText(requireActivity().getApplicationContext(), "Updated Failed!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//        finish();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}