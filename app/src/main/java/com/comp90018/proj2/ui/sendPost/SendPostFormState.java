package com.comp90018.proj2.ui.sendPost;

import androidx.annotation.Nullable;

/**
 * The state of send post form
 */
public class SendPostFormState {

    @Nullable
    private Integer imageError;
    @Nullable
    private Integer titleError;
    @Nullable
    private Integer messageError;
    private boolean isDataValid = true;

    public SendPostFormState(@Nullable Integer imageError, @Nullable Integer titleError, @Nullable Integer messageError) {
        this.imageError = imageError;
        this.titleError = titleError;
        this.messageError = messageError;
    }

    SendPostFormState(boolean isDataValid) {
        this.imageError = null;
        this.titleError = null;
        this.messageError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getImageError() {
        return imageError;
    }

    @Nullable
    public Integer getTitleError() {
        return titleError;
    }

    @Nullable
    public Integer getMessageError() {
        return messageError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}