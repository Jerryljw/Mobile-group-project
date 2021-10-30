package com.comp90018.proj2.ui.map;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.comp90018.proj2.data.model.CardItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<Location> location;
    private MutableLiveData<List<CardItem>> postList;
    private MutableLiveData<Map<String, CardItem>> postData;

    public MapViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is map fragment");
        location = new MutableLiveData<>();
        postList = new MutableLiveData<>();
        postData = new MutableLiveData<>();
    }

    public Location getLocation() {
        return location.getValue();
    }

    public void setLocation(Location location) {
        this.location.setValue(location);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public List<CardItem> getPostList() {
        return postList.getValue();
    }

    public void setPostList(List<CardItem> postList) {
        this.postList.setValue(postList);
    }

    public void addPost(String postId, CardItem cardItem) {
        if (this.postData.getValue() == null) {
            this.postData.setValue(new HashMap<String, CardItem>());
        }
        Objects.requireNonNull(this.postData.getValue()).put(postId, cardItem);
    }

    public CardItem getPost(String postId) {
        if (this.postData.getValue() == null) {
            this.postData.setValue(new HashMap<String, CardItem>());
        }
        return Objects.requireNonNull(this.postData.getValue()).get(postId);
    }
}