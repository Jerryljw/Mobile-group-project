package com.comp90018.proj2.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.request.RequestOptions;
import com.comp90018.proj2.MainActivity;
import com.comp90018.proj2.R;
import com.comp90018.proj2.databinding.ActivityLoginBinding;
import com.comp90018.proj2.ui.signUp.SignUpActivity;
import com.comp90018.proj2.utils.GlideApp;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Objects;

/**
 * Activity for user login
 */
public class LoginActivity extends AppCompatActivity {

    // Initialize
    private String TAG = "LoginActivity";
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;


    // Firebase Auth instances
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;

    // UI components
    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;
    TextView signUpText;
    AVLoadingIndicatorView loading;
    ImageView logoImage;

    @Override
    protected void onStart() {
        super.onStart();
        // Add the listener for auth status
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Remove the listener for auth status
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory(getApplicationContext()))
                .get(LoginViewModel.class);

        Log.i(TAG, "onCreate: ");

        // Get the bindings of UI components
        usernameEditText = binding.email;
        passwordEditText = binding.password;
        loginButton = binding.login;
        signUpText = binding.signUp;
        loading = binding.loading;
        logoImage = binding.logo;

        // The listener for text changed event
        TextWatcher afterTextChangedListener = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // After the EditText is changed, than update it to view model
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        // Add the listeners to proper fields
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        // Add observer for form status
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                // Enable/Disable the login button based on form status
                loginButton.setEnabled(loginFormState.isDataValid());
            }
        });

        // [START auth_state_listener]
        // this method execute as soon as there is a change in Auth status , such as user sign in or sign out.
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            // Hide the loading progress bar
            loading.hide();

            if (user != null) {
                Log.i(TAG, "Display Name = " + user.getDisplayName());
                Log.i(TAG, "Uri = " + String.valueOf(user.getPhotoUrl()));

                // Update the UI
                updateUiWithUser(user);
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        };
        // [END auth_state_listener]

        // Add click listener for login button
        loginButton.setOnClickListener(v -> {
            Log.i(TAG, "onClick: ");

            // Show the loading progress bar
            loading.show();

            // [START login]
            login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            // [END login]

        });

        // Add click listener for signup navigation button
        signUpText.setOnClickListener(v -> {
            Log.i(TAG, "signUpText onClick: ");

            // Start the SignUpActivity
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
        // [END sign_up_click_listener]

        // Display the app logo
//        GlideApp.with(getApplication())
//                .load("https://firebasestorage.googleapis.com/v0/b/mobiletest-e36f3.appspot.com" +
//                        "/o/Luora.png?alt=media&token=0a004bd2-9c1e-434f-8749-fecee60c38ba")
//                .apply(new RequestOptions()
//                        .fitCenter())
//                .into(logoImage);

        // When the page is created, hide the loading
        loading.hide();
    }

    /**
     * Method for login
     * @param email user's input email
     * @param password user's input password
     */
    private void login(String email, String password) {
        // Validate input with Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                    } else {
                        showLoginFailed();
                        // If sign in fails, display a message to the user.
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthInvalidUserException e) {
                            Log.w(TAG, "signInWithEmail: Invalid User");
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            Log.w(TAG, "signInWithEmail: Invalid Password");
                        } catch (FirebaseNetworkException e) {
                            Log.e(TAG, "signInWithEmail: FirebaseNetworkException");
                        } catch (Exception e) {
                            Log.e(TAG, "signInWithEmail: " + e.getMessage());
                        }
                        loading.hide();
                    };
                });
    }

    /**
     * Update the user based on user's stautus
     * @param user logged in user
     */
    private void updateUiWithUser(@NonNull FirebaseUser user) {
        // Popup welcome message
        String welcome = getString(R.string.welcome) + user.getEmail();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();

        // Start the MainActivity
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Handle login failure
     */
    private void showLoginFailed() {
        Toast.makeText(getApplicationContext(), R.string.login_failed, Toast.LENGTH_SHORT).show();
    }
}
