package com.comp90018.proj2.ui.sendPost;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comp90018.proj2.R;
import com.comp90018.proj2.ui.login.LoginFormState;

public class SendPostViewModel extends ViewModel {
    private String TAG = "SendPostViewModel";

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();

    SendPostViewModel() { }

    public void updateLocation(String longitude, String latitude) {

    }


}
