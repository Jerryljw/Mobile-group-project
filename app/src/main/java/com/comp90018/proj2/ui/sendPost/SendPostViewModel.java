package com.comp90018.proj2.ui.sendPost;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SendPostViewModel extends ViewModel {
    private String TAG = "SendPostViewModel";

    private MutableLiveData<String> longitude;
    private MutableLiveData<String> latitude;


    SendPostViewModel() {
        longitude = new MutableLiveData<String>();
        latitude = new MutableLiveData<String>();
    }

    public MutableLiveData<String> getLongitude() {
        return longitude;
    }

    public MutableLiveData<String> getLatitude() {
        return latitude;
    }

    public void updateLocation(double longitude, double latitude) {
        this.longitude.setValue(String.valueOf(longitude));
        this.latitude.setValue(String.valueOf(latitude));
    }

}
