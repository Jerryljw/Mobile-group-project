package com.comp90018.proj2.ui.signUp;

import androidx.annotation.Nullable;

public class SignUpFormState {
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer displayNameError;
    private boolean isDataValid;

    SignUpFormState(@Nullable Integer usernameError, @Nullable Integer passwordError, @Nullable Integer displayNameError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.displayNameError = displayNameError;
        this.isDataValid = false;
    }

    SignUpFormState(boolean isDataValid) {
        this.usernameError = null;
        this.passwordError = null;
        this.displayNameError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getDisplayNameError() {
        return displayNameError;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}
