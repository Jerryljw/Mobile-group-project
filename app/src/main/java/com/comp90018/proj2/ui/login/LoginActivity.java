package com.comp90018.proj2.ui.login;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.comp90018.proj2.databinding.ActivityMainBinding;

public class LoginActivity extends AppCompatActivity {

    private String TAG = "LoginActivity";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.i(TAG, "onCreate: ");
    }
}
