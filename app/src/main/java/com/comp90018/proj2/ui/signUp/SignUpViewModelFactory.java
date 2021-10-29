package com.comp90018.proj2.ui.signUp;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SignUpViewModelFactory implements ViewModelProvider.Factory {

    private Context context;

    public SignUpViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SignUpViewModel.class)) {
            return (T) new SignUpViewModel();
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}