package com.comp90018.proj2.ui.sendPost;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.comp90018.proj2.ui.login.LoginViewModel;

import org.jetbrains.annotations.NotNull;

public class SendPostViewModelFactory  implements ViewModelProvider.Factory {

    private Context context;

    public SendPostViewModelFactory(Context context) {
        this.context = context;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SendPostViewModel.class)) {
            return (T) new SendPostViewModel();
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
