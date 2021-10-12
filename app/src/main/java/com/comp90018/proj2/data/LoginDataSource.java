package com.comp90018.proj2.data;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.comp90018.proj2.data.model.LoggedInUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.Executor;

public class LoginDataSource {

    private String TAG = "LoginDataSource";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public Result<FirebaseUser> login(String email, String password) {

        try {
            // TODO: handle loggedInUser authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                return new Result.Success<>(user);

//                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                            }
                        }
                    });

//            LoggedInUser fakeUser =
//                    new LoggedInUser(
//                            java.util.UUID.randomUUID().toString(),
//                            "Jane Doe");
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
