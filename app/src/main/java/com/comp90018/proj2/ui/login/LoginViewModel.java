package com.comp90018.proj2.ui.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comp90018.proj2.data.LoginRepository;
import com.comp90018.proj2.data.Result;
import com.comp90018.proj2.data.model.LoggedInUser;

public class LoginViewModel extends ViewModel {

    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public void login(String email, String password) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(email, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
//            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else {
//            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

}
