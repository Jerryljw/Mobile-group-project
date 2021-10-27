package com.comp90018.proj2.ui.finder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FinderViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FinderViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is finder fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}