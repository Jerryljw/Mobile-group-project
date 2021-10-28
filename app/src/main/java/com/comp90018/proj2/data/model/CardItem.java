package com.comp90018.proj2.data.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class CardItem {

    // TODO: userid contains all
    private String userId;
    private String postId;

    private int img;
    private String titles;
    private int headsIcon;
    private String usernames;
    private Timestamp postTime;

    // set a default value which is unsolved
    private int postType = 0;

    // TODO: need to read from db as Coordinates to do calculate or??
    private GeoPoint point;

    public Timestamp getPostTime() {
        return postTime;
    }

    public void setPostTime(Timestamp postTime) {
        this.postTime = postTime;
    }


    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public int getPostType() {
        return postType;
    }

    public void setPostType(int postType) {
        this.postType = postType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public GeoPoint getPoint() {
        return point;
    }

    public void setPoint(GeoPoint point) {
        this.point = point;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitles() {
        return titles;
    }

    public void setTitles(String titles) {
        this.titles = titles;
    }

    public int getHeadsIcon() {
        return headsIcon;
    }

    public void setHeadsIcon(int headsIcon) {
        this.headsIcon = headsIcon;
    }

    public String getUsernames() {
        return usernames;
    }

    public void setUsernames(String usernames) {
        this.usernames = usernames;
    }


}
