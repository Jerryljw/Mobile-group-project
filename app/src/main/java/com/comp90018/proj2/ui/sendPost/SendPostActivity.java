package com.comp90018.proj2.ui.sendPost;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.comp90018.proj2.databinding.ActivityLoginBinding;
import com.comp90018.proj2.databinding.ActivitySendPostBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SendPostActivity extends AppCompatActivity {

    // Initialize
    private String TAG = "SendPostActivity";
    private SendPostViewModel sendPostViewModel;
    private ActivitySendPostBinding binding;

    /** Auth */
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

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

        Log.i(TAG, "onCreate: ");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }
}
