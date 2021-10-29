package com.comp90018.proj2.ui.sendPost;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comp90018.proj2.R;

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

    public void formDataChanged(String image, String title, String message) {
        Log.i(TAG, "Image: " + image + "; Title: " + title + "; Message: " + message);
        if (isEmptyString(image)) {
            sendPostFormState.setValue(new SendPostFormState(R.string.invalid_image, null, null));
        } else if (isEmptyString(title)) {
            sendPostFormState.setValue(new SendPostFormState(null, R.string.invalid_title, null));
        } else if (isEmptyString(message)) {
            sendPostFormState.setValue(new SendPostFormState(null, null, R.string.invalid_message));
        } else {
            sendPostFormState.setValue(new SendPostFormState(true));
        }
    }

    private boolean isEmptyString(String input) {
        return input == null || "".equals(input);
    }

    public MutableLiveData<SendPostFormState> getSendPostFormState() {
        return sendPostFormState;
    }
}