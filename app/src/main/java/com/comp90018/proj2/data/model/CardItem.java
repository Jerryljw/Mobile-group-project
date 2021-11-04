package com.comp90018.proj2.data.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.StorageReference;

/**
 * Card item for RecycleView in Finder
 */
public class CardItem {

    // TODO: userid contains all
    private String userId;
    private String postId;

    private StorageReference img;
    private String imgUrl;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public StorageReference getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(StorageReference headIcon) {
        this.headIcon = headIcon;
    }

    private StorageReference headIcon;
    private String titles;
    private String usernames;
    private Timestamp postTime;

    private String postSpecies;
    private String postType;
    private String postMessage;

    public String getPostMessage() {
        return postMessage;
    }

    public void setPostMessage(String postMessage) {
        this.postMessage = postMessage;
    }

    public String getPostSpecies() {
        return postSpecies;
    }

    public void setPostSpecies(String postSpecies) {
        this.postSpecies = postSpecies;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    // set a default value which is unsolved
    private int postFlag;

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


    public int getPostFlag() {
        return postFlag;
    }

    public void setPostFlag(int postFlag) {
        this.postFlag = postFlag;

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

    public StorageReference getImg() {
        return img;
    }

    public void setImg(StorageReference img) {
        this.img = img;
    }

    public String getTitles() {
        return titles;
    }

    public void setTitles(String titles) {
        this.titles = titles;
    }

    public String getUsernames() {
        return usernames;
    }

    public void setUsernames(String usernames) {
        this.usernames = usernames;
    }

    @Override
    public String toString() {
        return "CardItem{" +
                "userId='" + userId + '\'' +
                ", postId='" + postId + '\'' +
                ", img=" + img +
                ", titles='" + titles + '\'' +
                ", headsIcon=" + headIcon +
                ", usernames='" + usernames + '\'' +
                ", postTime=" + postTime +
                ", postSpecies='" + postSpecies + '\'' +
                ", postType='" + postType + '\'' +
                ", postMessage='" + postMessage + '\'' +
                ", postFlag=" + postFlag +
                ", point=" + point +
                '}';
    }
}
