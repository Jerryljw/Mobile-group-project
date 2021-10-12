package com.comp90018.proj2.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executor;

public class LoginDataSource {

    private String TAG = "LoginDataSource";

    private Context context;

    public LoginDataSource(Context context) {
        this.context = context;
    }

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public Result<FirebaseUser> login(String email, String password) {
        final FirebaseUser[] user = new FirebaseUser[1];

        Log.i(TAG, "login: " + email + ", " + password);

        try {
            // TODO: handle loggedInUser authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(context.getMainExecutor(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail: success");
                                Log.d(TAG, task.getResult().toString());

                                user[0] = mAuth.getCurrentUser();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.d(TAG, "signInWithEmail: fail");
                                user[0] = null;
                            }
                        }
                    });

            return new Result.Success<>(user[0]);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
