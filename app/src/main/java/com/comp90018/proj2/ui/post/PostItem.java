package com.comp90018.proj2.ui.post;

public class PostItem {
    private int postId;
    private int userId;
    private String username;
    private int img;
    private int headIcon;

    public PostItem() {
    }

    public PostItem(int postId, int userId, String username, int img, int headIcon) {
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.img = img;
        this.headIcon = headIcon;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public int getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(int headIcon) {
        this.headIcon = headIcon;
    }
}
