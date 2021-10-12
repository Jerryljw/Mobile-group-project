package com.comp90018.proj2.data;

import android.util.Log;

import com.comp90018.proj2.data.model.LoggedInUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginRepository {

    private String TAG = "LoginRepository";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<FirebaseUser> login(String email, String password) {
        // handle login
        Result<FirebaseUser> result = dataSource.login(email, password);
        if (result instanceof Result.Success) {
            Log.i(TAG, "login: " + ((Result.Success<?>) result).getData());
//            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }

}
