package com.comp90018.proj2.ui.signUp;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comp90018.proj2.R;

public class SignUpViewModel extends ViewModel {

    private String TAG = "SignUpViewModel";

    private MutableLiveData<SignUpFormState> signUpFormState = new MutableLiveData<>();

    SignUpViewModel() { }

    LiveData<SignUpFormState> getSignUpFormState() {
        return signUpFormState;
    }


    public void signUpDataChanged(String email, String password, String displayName) {
        if (!isEmailValid(email)) {
            signUpFormState.setValue(new SignUpFormState(R.string.invalid_email, null, null));
        } else if (!isPasswordValid(password)) {
            signUpFormState.setValue(new SignUpFormState(null, R.string.invalid_password, null));
        } else if (!isDisplayNameValid(displayName)) {
            signUpFormState.setValue(new SignUpFormState(null, null, R.string.invalid_display_name));
        } else {
            signUpFormState.setValue(new SignUpFormState(true));
        }
    }

    private boolean isDisplayNameValid(String displayName) {
        return displayName != null && displayName.trim().length() > 5;
    }

    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        if (email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return !email.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

}
