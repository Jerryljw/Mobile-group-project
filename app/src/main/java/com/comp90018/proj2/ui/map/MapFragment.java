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
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.OnClick;

public class MapFragment extends Fragment implements View.OnClickListener {

    private String TAG = "Map Page";

    private MapViewModel mapViewModel;
    private FragmentMapBinding binding;

    private Button bCaptureImage;

    private final int PREFERED_IMAGE_WIDTH_SIZE = 1200;

    private final int CAMERA_PERMISSION_CODE = 300;
    private final int RESULT_CAMERA_LOAD_IMG = 1889;

    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        mapViewModel =
                new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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

            if (allPermissionsGranted()) {
                startCamera();
            } else {
                ActivityCompat.requestPermissions(
                        requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }

//                openCameraIntent();
        });


        return root;
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void startCamera() {
        ListenableFuture<ProcessCameraProvider>
                cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(
                new Runnable() {
                    @Override
                    public void run() {
                        openCameraIntent();
                    }
                },
                ActivityCompat.getMainExecutor(requireContext())
        );
    }

    private void openCameraIntent() {
        Log.d(TAG, "openCameraIntent: ");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
//                photoFile.getName();
//                Uri photoURI = FileProvider.getUriForFile(requireContext(), photoFile);
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(cameraIntent, RESULT_CAMERA_LOAD_IMG);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.ENGLISH).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        Log.d(TAG, "createImageFile: " + image.getAbsolutePath());
//        imagesFilesPaths.add(image.getAbsolutePath());
        return image;
    }


    public interface camera {
        void captureImage();
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