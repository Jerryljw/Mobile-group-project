package com.comp90018.proj2.ui.account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comp90018.proj2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public AccountViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is account fragment");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String welcome = "Welcome " + currentUser.getEmail() + "\n";
            mText.setValue(welcome + mText.getValue());
        }
    }

    public LiveData<String> getText() {
        return mText;
    }
}