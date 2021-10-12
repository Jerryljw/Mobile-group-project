package com.comp90018.proj2.ui.login;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.comp90018.proj2.data.LoginDataSource;
import com.comp90018.proj2.data.LoginRepository;

public class LoginViewModelFactory implements ViewModelProvider.Factory {

    private Context context;

    public LoginViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(
                    LoginRepository.getInstance(new LoginDataSource(context)));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}