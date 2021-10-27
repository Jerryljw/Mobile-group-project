package com.comp90018.proj2.ui.sendPost;

import androidx.annotation.Nullable;

public class SendPostFormState {

    @Nullable
    private Integer longitudeError;
    @Nullable
    private Integer latitudeError;
    private boolean isDataValid = true;

    SendPostFormState(@Nullable Integer longitudeError, @Nullable Integer latitudeError) {
        this.longitudeError = longitudeError;
        this.latitudeError = latitudeError;
        this.isDataValid = false;
    }

    SendPostFormState(boolean isDataValid) {
        this.longitudeError = null;
        this.latitudeError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getLongitudeError() {
        return longitudeError;
    }

    @Nullable
    public Integer getLatitudeError() {
        return latitudeError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}
