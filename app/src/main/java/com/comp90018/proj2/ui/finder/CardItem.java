package com.comp90018.proj2.ui.finder;

public class CardItem {

    // TODO: userid contains all
    private int userId;
    private int postId;

    private int img;
    private String titles;
    private int headsIcon;
    private String usernames;
    private String distance;
    private String imgUrl;

    // set a default value which is unsolved
    private int postType = 0;


    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getPostType() {
        return postType;
    }

    public void setPostType(int postType) {
        this.postType = postType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
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

    @Override
    public String toString() {
        return "CardItem{" +
                "userId=" + userId +
                ", postId=" + postId +
                ", img=" + img +
                ", titles='" + titles + '\'' +
                ", headsIcon=" + headsIcon +
                ", usernames='" + usernames + '\'' +
                ", distance='" + distance + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", postType=" + postType +
                '}';
    }
}

