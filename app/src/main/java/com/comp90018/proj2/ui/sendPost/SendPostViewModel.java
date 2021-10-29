package com.comp90018.proj2.ui.sendPost;

import android.location.Location;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comp90018.proj2.R;
import com.comp90018.proj2.ui.login.LoginFormState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SendPostViewModel extends ViewModel {
    private String TAG = "SendPostViewModel";

    private MutableLiveData<String> longitude;
    private MutableLiveData<String> latitude;

    private MutableLiveData<SendPostFormState> sendPostFormState = new MutableLiveData<>();


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

    public void updateLocation(double latitude, double longitude) {
        this.longitude.setValue(String.valueOf(longitude));
        this.latitude.setValue(String.valueOf(latitude));
    }

    public void locationDataChanged(String latitude, String longitude) {
        Log.i(TAG, "Latitude: " + latitude + "; Longitude: " + longitude);
        if (!isLatitudeValid(latitude)) {
            sendPostFormState.setValue(new SendPostFormState(R.string.invalid_latitude, null));
        } else if (!isLongitudeValid(longitude)) {
            sendPostFormState.setValue(new SendPostFormState(null, R.string.invalid_longitude));
        } else {
            sendPostFormState.setValue(new SendPostFormState(true));
        }
    }

    private boolean isLatitudeValid(String latitude) {
        if (latitude == null) {
            return false;
        }
        try {
            Double.parseDouble(latitude);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isLongitudeValid(String longitude) {
        if (longitude == null) {
            return false;
        }
        try {
            Double.parseDouble(longitude);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public MutableLiveData<SendPostFormState> getSendPostFormState() {
        return sendPostFormState;
    }
}
