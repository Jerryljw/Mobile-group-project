package com.comp90018.proj2.ui.login;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comp90018.proj2.R;

/**
 * View model for Login
 */
public class LoginViewModel extends ViewModel {

    private String TAG = "LoginViewModel";

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();

    LoginViewModel() { }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    /**
     * Method for user input validation
     * @param username input username
     * @param password input password
     */
    public void loginDataChanged(String username, String password) {
        // If the input is invalid, set the error to the form state
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_email, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    /**
     * Method for checking username
     * @param username input username
     * @return valid - true; invalid - false
     */
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    /**
     * Method for checking password
     * @param password input password
     * @return valid - true; invalid - false
     */
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
