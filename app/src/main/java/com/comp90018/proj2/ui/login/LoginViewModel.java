package com.comp90018.proj2.ui.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comp90018.proj2.data.LoginRepository;

public class LoginViewModel extends ViewModel {

    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

}
