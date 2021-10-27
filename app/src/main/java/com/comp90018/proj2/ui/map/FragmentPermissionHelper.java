package com.comp90018.proj2.ui.map;

import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.FragmentActivity;

public class FragmentPermissionHelper {
    public void startPermissionRequest(FragmentActivity fragmentActivity) {
        ActivityResultLauncher<String> requestPermissionLauncher =
                fragmentActivity.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                    } else {
                        // Explain to the user that the feature is unavailable because the
                        // features requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.
                    }
                });
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }
}
