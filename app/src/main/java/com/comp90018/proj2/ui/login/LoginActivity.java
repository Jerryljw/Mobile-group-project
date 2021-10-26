package com.comp90018.proj2.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.comp90018.proj2.MainActivity;
import com.comp90018.proj2.R;
import com.comp90018.proj2.databinding.ActivityLoginBinding;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    // Initialize
    private String TAG = "LoginActivity";
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;


    /**
     * Auth
     */
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
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

        final EditText usernameEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        TextWatcher afterTextChangedListener = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
//                if (loginFormState.getUsernameError() != null) {
//                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
//                }
//                if (loginFormState.getPasswordError() != null) {
//                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
//                }
            }
        });

        // [START auth_state_listener] ,this method execute as soon as there is a change in Auth status , such as user sign in or sign out.
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                updateUiWithUser(user);
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
//                showLoginFailed(null);
            }

        };
        // [END auth_state_listener]

        loginButton.setOnClickListener(v -> {
            Log.i(TAG, "onClick: ");
            loadingProgressBar.setVisibility(View.VISIBLE);

            // [START login]
            login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            // [END login]

            loadingProgressBar.setVisibility(View.INVISIBLE);
        });
    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                    } else {
//                        showLoginFailed(null);
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
                    }
                });
    }
    private void updateUiWithUser(@NonNull FirebaseUser user) {
        String welcome = getString(R.string.welcome) + user.getEmail();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();

        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
