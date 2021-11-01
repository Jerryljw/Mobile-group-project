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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.request.RequestOptions;
import com.comp90018.proj2.R;
import com.comp90018.proj2.databinding.FragmentAccountBinding;
import com.comp90018.proj2.ui.login.LoginActivity;
import com.comp90018.proj2.ui.photo.GlideEngine;
import com.comp90018.proj2.ui.signUp.SignUpActivity;
import com.comp90018.proj2.utils.GlideApp;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;


public class AccountFragment extends Fragment {

    private AccountViewModel accountViewModel;
    private FragmentAccountBinding binding;
    private TextView occupationTxtView, nameTxtView, displayNameView;
    private TextView emailTxtView, phoneTxtView, videoTxtView, facebookTxtView, twitterTxtView;
    private ImageView userImageView, emailImageView, phoneImageView, videoImageView,nameImageView;
    private ImageView facebookImageView, twitterImageView;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private String TAG = "UploadImageActivity";
    private String currIconPath;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAccount;
        occupationTxtView = binding.occupationTextview;
        displayNameView = binding.displayNameTextview;
        emailTxtView = binding.emailTextview;
        phoneTxtView = binding.phoneTextview;
        videoTxtView = binding.videoTextview;
        facebookTxtView = binding.facebookTextview;
//        twitterTxtView = binding.twitterTextview;

        nameImageView = binding.nameImageView;
        userImageView = binding.userImageview;
        emailImageView = binding.emailImageview;
        phoneImageView = binding.phoneImageview;
        videoImageView = binding.videoImageview;
        facebookImageView = binding.facebookImageview;
//        twitterImageView = binding.twitterImageview;


        FirebaseUser currentUser = mAuth.getCurrentUser();

        String Name = currentUser.getDisplayName();
        if (Name.equals("")){
            Name = "Default Name";
        }
        emailTxtView.setText(currentUser.getEmail());
        occupationTxtView.setText("Name");
        displayNameView.setText(Name);
        phoneTxtView.setText("phone");
        videoTxtView.setText("Country");

        userImageView.setOnClickListener(v -> {
            Log.i(TAG, "onClick: selectIcon");
            // [START select image]
            selectImage();
            // [END select image]
        });



        GlideApp.with(this)
                .load(String.valueOf(mAuth.getCurrentUser().getPhotoUrl()))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_card_portrait)
                        .fitCenter())
                .into(userImageView);

        accountViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        final Button logoutButton = binding.logout;
        logoutButton.setOnClickListener(view -> {
            mAuth.signOut();

            Intent intent = new Intent();
            intent.setClass(requireActivity().getApplicationContext(), LoginActivity.class);
            startActivity(intent);

        });



        final Button updateButton = binding.update;
        updateButton.setOnClickListener(view -> {
            String newDisplayName = displayNameView.getText().toString();
            if (newDisplayName.length()<6){
                Toast.makeText(this.getContext(), "Please use a Name longer than 6 letter", Toast.LENGTH_SHORT).show();
            }else{
                if (currIconPath != null && !"".equals(currIconPath)) {
                    saveIcon();

                } else {
                    updateUserProfile(Uri.parse(mAuth.getCurrentUser().getPhotoUrl().toString()));
                }
            }
//            if (currIconPath != null && !"".equals(currIconPath)) {
//                saveIcon();
//
//            } else {
//                updateUserProfile(Uri.parse("https://firebasestorage.googleapis.com/v0/b/mobiletest-e36f3.appspot.com/" +
//                        "o/icons%2Fvecteezypeople-business-avatarpp0421_generated.jpg?alt=media" +
//                        "&token=fd8edb77-c9a1-4e07-862c-8a88d0f82261"));
//            }




        });

        return root;




    }
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



    private void saveIcon() {
        String[] filename = currIconPath.split("\\.");
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
                    Log.i(TAG, "Uri: " + task.getResult());
//                    iconUri = task.getResult().getPath();
                    updateUserProfile(task.getResult());
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }
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
                            Log.i(TAG, "Uri = " + String.valueOf(user.getPhotoUrl()));
                            Toast.makeText(getActivity().getApplicationContext(), "User Profile Updated Successes!", Toast.LENGTH_SHORT).show();
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
